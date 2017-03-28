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
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    
    var bottomDistance: CGFloat = 0;
    var searchActive : Bool = false
    var contacts: [ContactModel] = []
    var filteredContacts: [ContactModel] = []
    let contactsModel = ContactsModel()
    
    class MyTapGestureRecognizer: UITapGestureRecognizer {
        var selectedName: String?
        var selectedId: Int?
        var selectedPicture: String?
    }
    
    class MyLongPressGestureRecognizer: UILongPressGestureRecognizer {
        var selectedContact: ContactModel?
    }
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        contactsModel.delegate = self
        searchBar.delegate = self
 
        self.tableView.contentInset = UIEdgeInsetsMake(8, 0, 0, 0)
    }

    override func viewWillAppear(_ animated: Bool) {
        if (Utils.instance.isInternetAvailable()) {
            self.setListeners()
            contactsModel.downloadContacts()
        } else {
            noInternetAllert()
            if (UserDefaults.standard.value(forKey: "contacts") != nil) {
                // retrieving a value for a key
                if let data = UserDefaults.standard.data(forKey: "contacts"),
                    let contactsAux = NSKeyedUnarchiver.unarchiveObject(with: data) as? [ContactModel] {
                    contacts = contactsAux
                    self.tableView.reloadData()
                }
            }
        }
        
        // Adding the gesture recognizer that will dismiss the keyboard on an exterior tap
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIInputViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillHide, object: nil)
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        
        filteredContacts = contacts.filter({ (contact) -> Bool in
            return (contact.name!.lowercased().hasPrefix(searchText.lowercased()));
        })
        
        self.tableView.reloadData()
    }
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        if searchBar.text != nil && searchBar.text != "" {
            searchActive = true
        } else {
            searchActive = false
        }
    }
    
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        searchActive = false;
    }
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        searchActive = false;
    }
    
    func tableView(_ tableView:UITableView, numberOfRowsInSection section:Int) -> Int
    {
        if(searchActive){
            return filteredContacts.count
        } else {
            return contacts.count
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "contactsCell") as? ContactsTableViewCell {

            var item: ContactModel
            
            if(searchActive){
                item = filteredContacts[indexPath.row]
            } else {
                item = contacts[indexPath.row]
            }
            
            cell.configureCell(item.name!, email: item.email!, profilePic: item.profilePicture!)
            
            // Press gestures for cells
            let tapGesture = MyTapGestureRecognizer(target: self, action: #selector(ContactsViewController.shortPress))
            tapGesture.selectedId = item.userId!
            tapGesture.selectedName = item.name!
            tapGesture.selectedPicture = item.profilePicture!
            tapGesture.numberOfTapsRequired = 1
            cell.addGestureRecognizer(tapGesture)
            
            let longPress = MyLongPressGestureRecognizer(target: self, action: #selector(ContactsViewController.longPress))
            longPress.selectedContact = item
            cell.addGestureRecognizer(longPress)
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
                let contactId = contactDetails[i]["contact_id"] as? Int
            {
                item = ContactModel()
                item.username = username
                item.name = name
                item.email = email
                item.phoneNo = phoneNo
                item.userId = userId
                item.contactId = contactId
                item.phoneNo = phoneNo
                
                if let about = contactDetails[i]["biography"] as? String {
                    item.about = about
                } else {
                    item.about = ""
                }
                
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
                
                contactsAux.append(item)
            }
        }
        contacts = contactsAux
        
        let storedContacts = NSKeyedArchiver.archivedData(withRootObject: contacts)
        UserDefaults.standard.set(storedContacts, forKey:"contacts");
        
        self.tableView.reloadData()
    }
    
    func shortPress(gestureRecognizer: MyTapGestureRecognizer){
        if (Utils.instance.isInternetAvailable()) {
            let conversationViewController = self.storyboard?.instantiateViewController(withIdentifier: "conversationViewController") as? ConversationViewController
            conversationViewController?.passedValue = (gestureRecognizer.selectedName!, gestureRecognizer.selectedId!, gestureRecognizer.selectedPicture!)
            conversationViewController?.cameFrom = false
            self.navigationController?.pushViewController(conversationViewController!, animated: true)
        } else {
            noInternetAllert()
        }
    }
    
    func longPress(sender : MyLongPressGestureRecognizer){
        if sender.state == .began {
            let profileViewController = self.storyboard?.instantiateViewController(withIdentifier: "profileViewController") as? ProfileViewController
            profileViewController?.passedValue = sender.selectedContact
            self.navigationController?.pushViewController(profileViewController!, animated: true)
        }
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
    
    func keyboardWillShow(notification:NSNotification) {
        adjustingHeight(show: true, notification: notification)
    }
    
    func keyboardWillHide(notification:NSNotification) {
        adjustingHeight(show: false, notification: notification)
    }
    
    func adjustingHeight(show:Bool, notification:NSNotification) {
        
        if let userInfo = notification.userInfo, let durationValue = userInfo[UIKeyboardAnimationDurationUserInfoKey], let curveValue = userInfo[UIKeyboardAnimationCurveUserInfoKey] {
            
            let duration = (durationValue as AnyObject).doubleValue
            let keyboardFrame:CGRect = (userInfo[UIKeyboardFrameBeginUserInfoKey] as! NSValue).cgRectValue
            let options = UIViewAnimationOptions(rawValue: UInt((curveValue as AnyObject).integerValue << 16))
            let changeInHeight = (keyboardFrame.height  - 50) * (show ? 1 : 0)
            
            self.bottomConstraint.constant = bottomDistance + changeInHeight
            UIView.animate(withDuration: duration!, delay: 0, options: options, animations: {
                
                self.view.layoutIfNeeded()
            }, completion: nil)
        }
        self.tableView.reloadData()
    }
    
    func setListeners() {
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIWasDeletedListener(completionHandler: { (enemy) -> Void in
            self.contactsModel.downloadContacts()
        })
        SocketIOManager.sharedInstance.setMyRequestAcceptedListener(completionHandler: { () -> Void in
            self.contactsModel.downloadContacts()
        })
    }
    
    func noInternetAllert() {
        let alertView = UIAlertController(title: "No internet connection",
                                          message: "Please reconnect to the internet" as String, preferredStyle:.alert)
        let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
        alertView.addAction(okAction)
        self.present(alertView, animated: true, completion: nil)
        self.searchBar.isUserInteractionEnabled = false
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
}
