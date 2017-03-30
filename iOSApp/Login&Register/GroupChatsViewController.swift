//
//  GroupChatsViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 18/03/2017.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class GroupChatsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    
    var bottomDistance: CGFloat = 0;
    
    var searchActive : Bool = false
    var chats: [GroupChatModel] = []
    var filteredChats: [GroupChatModel] = []
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        searchBar.delegate = self
        
        self.tableView.contentInset = UIEdgeInsetsMake(8, 0, 0, 0)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        self.setListeners()
        if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
            SocketIOManager.sharedInstance.getGroupChats(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
        } else {
            noInternetAllert()
            if (UserDefaults.standard.value(forKey: "privateChats") != nil) {
                // retrieving a value for a key
                if let data = UserDefaults.standard.data(forKey: "groupChats"),
                    let chatsAux = NSKeyedUnarchiver.unarchiveObject(with: data) as? [GroupChatModel] {
                    chats = chatsAux
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
        
        filteredChats = chats.filter({ (chat) -> Bool in
            return (chat.groupName!.lowercased().hasPrefix(searchText.lowercased()));
        })
        searchActive = true
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
            return filteredChats.count
        } else {
            return chats.count
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "chatsCell") as? ChatsTableViewCell {

            var item: GroupChatModel
            
            if(searchActive){
                item = filteredChats[indexPath.row]
            } else {
                item = chats[indexPath.row]
            }

            cell.configureCell(item.groupName!, lastMessage: item.lastMessage!, timestamp: item.timestamp!, profilePic: item.groupPicture!)
            return cell
        } else {
            return ChatsTableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        let groupConversationViewController = self.storyboard?.instantiateViewController(withIdentifier: "groupConversationViewController") as? GroupConversationViewController
        groupConversationViewController?.passedValue = (chats[indexPath.row].groupName!, chats[indexPath.row].groupId!, chats[indexPath.row].groupDescription!, chats[indexPath.row].owner!, chats[indexPath.row].groupPicture!)
        groupConversationViewController?.groupIndex = indexPath.row
        self.navigationController?.pushViewController(groupConversationViewController!, animated: true)
    }
    
    // The function called at the arival of the response from the server
    func chatsDownloaded(_ chatDetails: [[String:Any]]) {
        
        var chatsAux: [GroupChatModel] = []
        var item: GroupChatModel;
        
        // parse the received JSON and save the contacts
        for i in 0 ..< chatDetails.count {
            
            if let groupId = chatDetails[i]["group_id"] as? Int,
                let groupName = chatDetails[i]["name"] as? String,
                let groupDescription = chatDetails[i]["description"] as? String,
                let owner = chatDetails[i]["owner"] as? Int
            {
                
                item = GroupChatModel()
                
                item.groupId = groupId
                item.groupName = groupName
                item.groupDescription = groupDescription
                item.owner = owner
                
                if let lastMessage = chatDetails[i]["message"] as? String,
                    let timestamp = chatDetails[i]["timestmp"] as? String {
                    item.lastMessage = lastMessage
                    let separators = CharacterSet(charactersIn: "T.")
                    item.timestamp = timestamp.components(separatedBy: separators)[1]
                } else {
                    item.lastMessage = groupDescription
                    item.timestamp = ""
                }
                
                if let groupPicture = chatDetails[i]["group_picture"] as? String {
                    
                    let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(groupPicture)")
                    if FileManager.default.fileExists(atPath: filename.path) {
                        item.groupPicture = groupPicture
                    } else {
                        // Download the profile picture, if exists
                        if let url = URL(string: "http://188.166.157.62/group_pictures/\(groupPicture)") {
                            if let data = try? Data(contentsOf: url) {
                                var profileImg: UIImage
                                profileImg = UIImage(data: data)!
                                if let data = UIImagePNGRepresentation(profileImg) {
                                    try? data.write(to: filename)
                                    item.groupPicture = groupPicture
                                } else {
                                    item.groupPicture = ""
                                }
                            } else {
                                item.groupPicture = ""
                            }
                        }
                    }
                } else {
                    item.groupPicture = ""
                }
                
                chatsAux.append(item)
            }
        }
        chats = chatsAux
        
        let storedGroupChats = NSKeyedArchiver.archivedData(withRootObject: chats)
        UserDefaults.standard.set(storedGroupChats, forKey:"groupChats");

        self.tableView.reloadData()
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
    }
    
    func setListeners() {
        SocketIOManager.sharedInstance.setGetGroupChatsListener(completionHandler: { (userList) -> Void in
            DispatchQueue.main.async(execute: { () -> Void in
                self.chatsDownloaded(userList!)
            })
        })
        SocketIOManager.sharedInstance.setGroupCreatedListener(completionHandler: { (response) -> Void in
            if response == "fail" {
                print("group create error")
            }
        })
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in
            SocketIOManager.sharedInstance.getGroupChats(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
        })
        SocketIOManager.sharedInstance.setIWasDeletedFromGroupListener(completionHandler: { (enemy) -> Void in
            SocketIOManager.sharedInstance.getGroupChats(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
        })
        SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIHaveBeenAddedToGroupListener(completionHandler: { () -> Void in
            SocketIOManager.sharedInstance.getGroupChats(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
        })
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
