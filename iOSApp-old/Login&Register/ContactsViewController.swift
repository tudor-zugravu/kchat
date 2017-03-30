//
//  ContactsViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/21/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//


import UIKit

class ContactsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate,UISearchBarDelegate, ContactsModelProtocol {
    
    @IBOutlet weak var tableView: UITableView!
    
    @IBOutlet weak var searchBar: UISearchBar!
    
    
    var contacts: [ContactModel] = []
    let contactsModel = ContactsModel()
    var userNameArr=[String]()
    var result:[ContactModel] = []
    
    override func viewDidLoad() {
        
        
        self.searchBar.delegate = self
        tableView.delegate = self
        tableView.dataSource = self
        contactsModel.delegate = self
        contactsModel.downloadContacts()
       
        
        
        
        
    }
    

    
    @IBAction func addNewFriend(_ sender: Any) {
        
    }
    
    @IBAction func sendFriendList(_ sender: Any) {
    }
    
    @IBAction func recivedFriendList(_ sender: Any) {
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
                userNameArr.append(item.username!)
            }
        }
        contacts = contactsAux
        //        UserDefaults.standard.set(contactsAux, forKey:"contacts");
        
        self.result=contacts
        self.tableView.reloadData()
    }
    
    func searchBar(_ searchBar: UISearchBar, searchText: String) {
        
        
       
        print("[ViewController searchBar] searchText: \(searchText)")
        if searchText == "" {
            self.result = self.contacts
        } else {
            
            // filter data
          
            
            for arr in userNameArr {
                
                if (arr as AnyObject).lowercased.contains(searchText.lowercased()) {
                 for contact in contacts{
                    if(contact.username == arr){
                        print("search result: \(arr)")
                    self.result.append(contact)
                    }
                    }
                }
            }
        }
        self.tableView.reloadData()
        //searchBar.resignFirstResponder()
        
    }
    
    func searchBarSearchBarClicked() {
        searchBar.resignFirstResponder()
        searchBar.endEditing(true)
        searchBar(searchBar, searchText: searchBar.text!)
    }

    
    func tableView(_ tableView:UITableView, numberOfRowsInSection section:Int) -> Int
    {
        return result.count
    }
    
    
    // long and short press
    func shortPress(){
        //turn to message page
        print("short tap")
    }
    
    func longPress(sender : UIGestureRecognizer){
        print("Long tap")
        if sender.state == .ended {
            print("UIGestureRecognizerStateEnded")
            //Do Whatever You want on End of Gesture
            //Turn to profile page
            
        }
        else if sender.state == .began {
            print("UIGestureRecognizerStateBegan")
            //Do Whatever You want on Began of Gesture
        }
    }

    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        
        if let cell = tableView.dequeueReusableCell(withIdentifier: "contactsCell") as? ContactsTableViewCell {
            //cell.layer.masksToBounds = true
             cell.layer.cornerRadius = 30
             cell.layer.shadowColor=UIColor.lightGray.cgColor
             cell.layer.shadowOffset = CGSize(width:-6, height:6)
             cell.layer.shadowRadius=4
            cell.layer.shadowOpacity=0.4
            cell.clipsToBounds=false
        
            if contacts.count == 0 {
                cell.configureCell("", email: "", profilePic: "")
            } else {
                let item: ContactModel = result[indexPath.row]
                cell.configureCell(item.name!, email: item.email!, profilePic: item.profilePicture!)
            }
            //press cell
            let tapGesture = UITapGestureRecognizer(target: self, action: #selector(ContactsViewController.shortPress))
            let longPress = UILongPressGestureRecognizer(target: self, action: #selector(ContactsViewController.longPress))
            tapGesture.numberOfTapsRequired = 1
            cell.addGestureRecognizer(tapGesture)
            cell.addGestureRecognizer(longPress)

            return cell
        } else {
            return ContactsTableViewCell()
        }
    }
    
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    

    
//    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
//        
//        searchBar.resignFirstResponder()
//    }
    
    
    
  
    

    
    
    
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
