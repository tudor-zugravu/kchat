//
//  ContactRequestsViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/13/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ContactRequestsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    var contacts: [ContactModel] = []
    var passedValue: Bool?
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        searchBar.delegate = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        if let value = passedValue {
            print(value)
        } else {
            print("nope...")
        }
        
        // Adding the gesture recognizer that will dismiss the keyboard on an exterior tap
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIInputViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {        
//        SocketIOManager.sharedInstance.predictSearch(username: searchText, userId: String(describing: UserDefaults.standard.value(forKey: "userId")!), completionHandler: { (userList) -> Void in
//            DispatchQueue.main.async(execute: { () -> Void in
//                self.contactsDownloaded(userList!)
//            })
//        })
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
                cell.configureCell(item.username!, email: item.name!, profilePic: item.profilePicture!)
            }
            return cell
        } else {
            return ContactsTableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: true)
        
        print("selected")
        
        //        // Alert for success and view change on dismiss
        //        let myAlert = UIAlertController(title:"Send Request Confirmation", message:"Add \(contacts[indexPath.row].name!) as a contact?", preferredStyle:.alert);
        //        let yesAction=UIAlertAction(title:"Yes", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in self.addContact(receiver: self.contacts[indexPath.row].userId!)});
        //        myAlert.addAction(yesAction);
        //        let noAction=UIAlertAction(title:"No", style:UIAlertActionStyle.default, handler:nil);
        //        myAlert.addAction(noAction);
        //        self.present(myAlert, animated:true, completion:nil);
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
        self.tableView.reloadData()
    }
    
//    func addContact(receiver: Int) {
//        
//        SocketIOManager.sharedInstance.addContact(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!), receiverId: String(receiver), completionHandler: { (response) -> Void in
//            if(response == true) {
//                self.contacts = []
//                self.tableView.reloadData()
//                self.searchBar.text = ""
//            } else {
//                print("error")
//            }
//        })
//    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    @IBAction func backButtonPressed(_ sender: Any) {
        let _ = navigationController?.popViewController(animated: true)
    }
  
}
