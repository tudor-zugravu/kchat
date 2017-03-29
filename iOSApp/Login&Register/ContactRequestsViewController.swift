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
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    
    var bottomDistance: CGFloat = 0;
    var searchActive : Bool = false
    var contacts: [ContactModel] = []
    var filteredContacts: [ContactModel] = []
    
    var passedValue: Bool?
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        searchBar.delegate = self
        
        self.tableView.contentInset = UIEdgeInsetsMake(8, 0, 0, 0)
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        self.setListeners()
    
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
        searchActive = true;
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

            cell.configureCell(item.name!, email: item.username!, profilePic: item.profilePicture!)
            return cell
        } else {
            return ContactsTableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: true)
        
        if let value = passedValue {
            if value == true {
                // Popup for validation with accepting a contact request on accept
                let myAlert = UIAlertController(title:"Delete Request Confirmation", message:"Delete the request sent to \(contacts[indexPath.row].name!)?", preferredStyle:.alert);
                let yesAction=UIAlertAction(title:"Yes", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in self.deleteRequest(receiver: indexPath.row)});
                myAlert.addAction(yesAction);
                let noAction=UIAlertAction(title:"No", style:UIAlertActionStyle.default, handler:nil);
                myAlert.addAction(noAction);
                self.present(myAlert, animated:true, completion:nil);
            } else {
                // Popup for validation with accepting a contact request on accept
                let myAlert = UIAlertController(title:"Accept Request Confirmation", message:"Accept \(contacts[indexPath.row].name!) as a contact?", preferredStyle:.alert);
                let yesAction=UIAlertAction(title:"Yes", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in self.acceptContact(receiver: indexPath.row)});
                myAlert.addAction(yesAction);
                let noAction=UIAlertAction(title:"No", style:UIAlertActionStyle.default, handler:nil);
                myAlert.addAction(noAction);
                self.present(myAlert, animated:true, completion:nil);
            }
        }
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
        
        self.tableView.reloadData()
    }
    
    func deleteRequest(receiver: Int) {
        if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
            SocketIOManager.sharedInstance.deleteRequest(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!), receiverId: String(self.contacts[receiver].userId!), receiver: String(receiver))
        } else {
            noInternetAllert()
            self.searchBar.isUserInteractionEnabled = false
        }
    }
    
    func acceptContact(receiver: Int) {
        if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
            SocketIOManager.sharedInstance.acceptContact(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!), receiverId: String(self.contacts[receiver].userId!), receiver: String(receiver))
        } else {
            noInternetAllert()
            self.searchBar.isUserInteractionEnabled = false
        }
    }
    
    @IBAction func backButtonPressed(_ sender: Any) {
        let _ = navigationController?.popViewController(animated: true)
        navigationController?.topViewController?.childViewControllers[2].viewWillAppear(true)
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
            let changeInHeight = (keyboardFrame.height) * (show ? 1 : 0)
            
            self.bottomConstraint.constant = bottomDistance + changeInHeight
            UIView.animate(withDuration: duration!, delay: 0, options: options, animations: {
                
                self.view.layoutIfNeeded()
            }, completion: nil)
        }
    }
    
    func setListeners() {
        SocketIOManager.sharedInstance.setRequestAcceptedListener(completionHandler: { (response) -> Void in
            if(response != "fail") {
                self.contacts.remove(at: Int(response)!)
                self.tableView.reloadData()
            } else {
                print("error")
            }
        })
        SocketIOManager.sharedInstance.setReceivedRequestsListener(completionHandler: { (userList) -> Void in
            DispatchQueue.main.async(execute: { () -> Void in
                self.contactsDownloaded(userList!)
            })
        })
        SocketIOManager.sharedInstance.setRequestDeletedListener(completionHandler: { (response) -> Void in
            if(response != "fail") {
                self.contacts.remove(at: Int(response)!)
                self.tableView.reloadData()
            } else {
                print("error")
            }
        })
        SocketIOManager.sharedInstance.setSentRequestsListener(completionHandler: { (userList) -> Void in
            DispatchQueue.main.async(execute: { () -> Void in
                self.contactsDownloaded(userList!)
            })
        })
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
        if let value = passedValue {
            if value == true {
                titleLabel.text = "Sent Requests"
                if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
                    SocketIOManager.sharedInstance.getSentRequests(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
                }
                SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })
                SocketIOManager.sharedInstance.setMyRequestAcceptedListener(completionHandler: { () -> Void in
                    SocketIOManager.sharedInstance.getSentRequests(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
                })
            } else {
                titleLabel.text = "Received Requests"
                if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
                    SocketIOManager.sharedInstance.getReceivedRequests(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
                }
                SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in
                    SocketIOManager.sharedInstance.getReceivedRequests(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
                })
                SocketIOManager.sharedInstance.setNoMoreContactRequestListener(completionHandler: { () -> Void in
                    SocketIOManager.sharedInstance.getReceivedRequests(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
                })
            }
        }
    }
    
    func noInternetAllert() {
        let alertView = UIAlertController(title: "No internet connection",
                                          message: "Please reconnect to the internet" as String, preferredStyle:.alert)
        let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
        alertView.addAction(okAction)
        self.present(alertView, animated: true, completion: nil)
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
}
