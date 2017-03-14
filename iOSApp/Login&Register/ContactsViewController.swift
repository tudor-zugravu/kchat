//
//  ContactsViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/21/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ContactsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate, ContactsModelProtocol {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    var contacts: [ContactModel] = []
    let contactsModel = ContactsModel()
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        contactsModel.delegate = self
        searchBar.delegate = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        contactsModel.downloadContacts()
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        self.contacts.filter { (contact) -> Bool in
            print((contact.name!.lowercased().range(of: searchText.lowercased()) != nil))
            return (contact.name!.lowercased().range(of: searchText.lowercased()) != nil);
        }
        self.tableView.reloadData()
    }
    
    func tableView(_ tableView:UITableView, numberOfRowsInSection section:Int) -> Int
    {
        return contacts.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "contactsCell") as? ContactsTableViewCell {
            if contacts.count == 0 {
                cell.configureCell("", email: "", profilePic: "")
            } else {
                let item: ContactModel = contacts[indexPath.row]
                cell.configureCell(item.name!, email: item.email!, profilePic: item.profilePicture!)
            }
            return cell
        } else {
            return ContactsTableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    // The function called at the arival of the response from the server
    func contactsDownloaded(_ contactDetails: [[String:Any]]) {
        
        var contactsAux: [ContactModel] = []
        var item:ContactModel;

        // parse the received JSON and save the contacts
        for i in 0 ..< contactDetails.count {
            
            if let username = contactDetails[i]["username"] as? String,
                let name = contactDetails[i]["name"] as? String,
                let email = contactDetails[i]["email"] as? String,
                let phoneNo = contactDetails[i]["phone_number"] as? String,
                let userId = contactDetails[i]["user_id"] as? Int,
                let contactId = contactDetails[i]["contact_id"] as? Int,
                let timestamp = contactDetails[i]["timestmp"] as? String
            {
                item = ContactModel()
                item.username = username
                item.name = name
                item.email = email
                item.phoneNo = phoneNo
                item.userId = userId
                item.contactId = contactId
                item.timestamp = timestamp
                
                if let profilePicture = contactDetails[i]["profile_picture"] as? String {
                    
                    // Download the profile picture, if exists
                    if let url = URL(string: "http://188.166.157.62/profile_pictures/\(profilePicture)") {
                        if let data = try? Data(contentsOf: url) {
                            var profileImg: UIImage
                            profileImg = UIImage(data: data)!
                            if let data = UIImagePNGRepresentation(profileImg) {
                                let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(profilePicture)")
                                try? data.write(to: filename)
                                item.profilePicture = profilePicture
                            } else {
                                item.profilePicture = ""
                            }
                        } else {
                            item.profilePicture = ""
                        }
                    }
                } else {
                    item.profilePicture = ""
                }
                
                contactsAux.append(item)
            }
        }
        contacts = contactsAux
//        UserDefaults.standard.set(contactsAux, forKey:"contacts");
        
        
        self.tableView.reloadData()
    }
    
    @IBAction func sentRequestsPressed(_ sender: Any) {
        let contactRequestsViewController = self.storyboard?.instantiateViewController(withIdentifier: "contactRequestsController") as? ContactRequestsViewController
        contactRequestsViewController?.passedValue = true
        self.navigationController?.pushViewController(contactRequestsViewController!, animated: true)
    }
    
    @IBAction func receivedRequestsPressed(_ sender: Any) {
        let contactRequestsViewController = self.storyboard?.instantiateViewController(withIdentifier: "contactRequestsController") as? ContactRequestsViewController
        contactRequestsViewController?.passedValue = false
        self.navigationController?.pushViewController(contactRequestsViewController!, animated: true)
    }
}
