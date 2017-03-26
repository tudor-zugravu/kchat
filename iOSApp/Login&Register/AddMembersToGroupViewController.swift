//
//  AddMembersToGroupViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 26/03/2017.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class AddMembersToGroupViewController: UIViewController, UINavigationControllerDelegate, UITableViewDataSource, UITableViewDelegate, ContactsModelProtocol {
        
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var groupPicture: UIImageView!
    @IBOutlet weak var groupDescription: UILabel!
    @IBOutlet weak var groupName: UILabel!
    
    var contacts: [SelectedContactModel] = []
    let contactsModel = ContactsModel()
    var numberOfSelectedContacts: Int = 0
    var groupId: Int = 0
    var passedValue: (groupName: String, groupId: Int, groupDescription: String, owner: Int, groupPicture: String)?
    var passedMembers: [String: (name: String, profilePicture: String)]?

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        contactsModel.delegate = self
        
        self.tableView.contentInset = UIEdgeInsetsMake(8, 0, 0, 0)
        
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setAddedToGroupListener(completionHandler: { (userList) -> Void in
            let _ = self.navigationController?.popViewController(animated: true)
        })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setMyRequestAcceptedListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIWasDeletedListener(completionHandler: { (enemy) -> Void in
            self.contactsModel.downloadContacts()
        })
        SocketIOManager.sharedInstance.setMyRequestAcceptedListener(completionHandler: { () -> Void in
            self.contactsModel.downloadContacts()
        })
        contactsModel.downloadContacts()
        
        if let value = passedValue {
            
            groupName.text = value.groupName
            groupDescription.text = value.groupDescription
            self.groupId = value.groupId
            
            if value.groupPicture != "" {
                let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(value.groupPicture)")
                if FileManager.default.fileExists(atPath: filename.path) {
                    groupPicture.image = UIImage(contentsOfFile: filename.path)
                }
            }
        }
    }
    
    func tableView(_ tableView:UITableView, numberOfRowsInSection section:Int) -> Int
    {
        return contacts.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "addGroupContactsCell") as? AddGroupContactsTableViewCell {
            
            var item: SelectedContactModel
            item = contacts[indexPath.row]
            
            cell.configureCell(item.name!, email: item.email!, profilePic: item.profilePicture!, selected: item.selected!)
            
            return cell
        } else {
            return AddGroupContactsTableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let cell = tableView.cellForRow(at: indexPath) as! AddGroupContactsTableViewCell
        if (cell.contactIsSelected) {
            cell.selectCell()
            contacts[indexPath.row].selected = false
            numberOfSelectedContacts -= 1
        } else if (numberOfSelectedContacts == 5) {
            let alertView = UIAlertController(title: "Failed",
                                              message: "There can be only 6 members in a group" as String, preferredStyle:.alert)
            let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
            alertView.addAction(okAction)
            self.present(alertView, animated: true, completion: nil)
        } else {
            cell.selectCell()
            contacts[indexPath.row].selected = true
            numberOfSelectedContacts += 1
        }
    }
    
    // The function called at the arival of the response from the server
    func contactsDownloaded(_ contactDetails: [[String:Any]]) {
        
        var contactsAux: [SelectedContactModel] = []
        var item:SelectedContactModel;
        
        // parse the received JSON and save the contacts
        for i in 0 ..< contactDetails.count {
            
            if let name = contactDetails[i]["name"] as? String,
                let email = contactDetails[i]["email"] as? String,
                let userId = contactDetails[i]["user_id"] as? Int,
                let contactId = contactDetails[i]["contact_id"] as? Int
            {
                if passedMembers?[String(userId)] == nil {
                    
                    item = SelectedContactModel()
                    item.name = name
                    item.email = email
                    item.userId = userId
                    item.contactId = contactId
                    
                    if let profilePicture = contactDetails[i]["profile_picture"] as? String {
                        let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(profilePicture)")
                        if FileManager.default.fileExists(atPath: filename.path) {
                            item.profilePicture = profilePicture
                        } else {
                            // Download the profile picture, if exists
                            if let url = URL(string: "http://188.166.157.62/profile_pictures/\(profilePicture)") {
                                if let data = try? Data(contentsOf: url) {
                                    var profileImg: UIImage
                                    profileImg = UIImage(data: data)!
                                    if let data = UIImagePNGRepresentation(profileImg) {
                                        try? data.write(to: filename)
                                        item.profilePicture = profilePicture
                                    } else {
                                        item.profilePicture = ""
                                    }
                                } else {
                                    item.profilePicture = ""
                                }
                            }
                        }
                    } else {
                        item.profilePicture = ""
                    }
                    item.selected = false
                    
                    contactsAux.append(item)
                }
            }
        }
        contacts = contactsAux

        numberOfSelectedContacts = (passedMembers?.count)!
        self.tableView.reloadData()
    }
    
    @IBAction func backButtonPressed(_ sender: Any) {
        let _ = navigationController?.popViewController(animated: true)
    }
        
    @IBAction func saveButtonPressed(_ sender: Any) {
        var selectedContacts: [Int] = []
        contacts.forEach { contact in
            if contact.selected! {
                selectedContacts.append(contact.userId!)
            }
        }
        if selectedContacts.count > 0 {
            print((passedValue?.groupId)!)
            print(selectedContacts)
            SocketIOManager.sharedInstance.addToGroup(groupId: (passedValue?.groupId)!, members: selectedContacts)
        } else {
            let _ = navigationController?.popViewController(animated: true)
        }
    }

}
