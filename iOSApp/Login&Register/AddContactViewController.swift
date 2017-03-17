//
//  AddContactViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/11/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class AddContactViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    var contacts: [FilteredContactModel] = []
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        searchBar.delegate = self
        
        SocketIOManager.sharedInstance.setSentRequestListener(completionHandler: { (response) -> Void in
            if(response == true) {
                self.contacts = []
                self.tableView.reloadData()
                self.searchBar.text = ""
            } else {
                print("error")
            }
        })
        
        SocketIOManager.sharedInstance.setSearchUserReceivedListener(completionHandler: { (userList) -> Void in
            DispatchQueue.main.async(execute: { () -> Void in
                self.contactsDownloaded(userList!)
            })
        })

    }
    
    override func viewWillAppear(_ animated: Bool) {
        // Adding the gesture recognizer that will dismiss the keyboard on an exterior tap
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIInputViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        SocketIOManager.sharedInstance.predictSearch(username: searchText, userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
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
                let item: FilteredContactModel = contacts[indexPath.row]
                cell.configureCell(item.username!, email: item.name!, profilePic: "")
            }
            return cell
        } else {
            return ContactsTableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: true)
        
        // Popup for validation with sending a contact request on accept
        let myAlert = UIAlertController(title:"Send Request Confirmation", message:"Add \(contacts[indexPath.row].name!) as a contact?", preferredStyle:.alert);
        let yesAction=UIAlertAction(title:"Yes", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in self.addContact(receiver: self.contacts[indexPath.row].userId!)});
        myAlert.addAction(yesAction);
        let noAction=UIAlertAction(title:"No", style:UIAlertActionStyle.default, handler:nil);
        myAlert.addAction(noAction);
        self.present(myAlert, animated:true, completion:nil);
    }
    
    // The function called at the arival of the response from the server
    func contactsDownloaded(_ contactDetails: [[String:Any]]) {
        
        var contactsAux: [FilteredContactModel] = []
        var item:FilteredContactModel;
        
        // parse the received JSON and save the contacts
        for i in 0 ..< contactDetails.count {
            
            if let username = contactDetails[i]["username"] as? String,
                let name = contactDetails[i]["name"] as? String,
                let userId = contactDetails[i]["user_id"] as? Int
            {
                item = FilteredContactModel()
                item.username = username
                item.name = name
                item.userId = userId
                
                contactsAux.append(item)
            }
        }
        contacts = contactsAux
        
        self.tableView.reloadData()
    }
    
    func addContact(receiver: Int) {
        
        SocketIOManager.sharedInstance.addContact(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!), receiverId: String(receiver))
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    @IBAction func backButtonPressed(_ sender: AnyObject) {
        let _ = navigationController?.popViewController(animated: true)
    }
}

