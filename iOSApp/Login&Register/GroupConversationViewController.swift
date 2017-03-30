//
//  GroupConversationViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 18/03/2017.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class GroupConversationViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var messageInputTextField: UITextField!
    @IBOutlet weak var sendButton: UIButton!
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var navigationView: UIView!
    @IBOutlet weak var dropButton: DropMenuButton!
    @IBOutlet weak var leaveButton: UIButton!
    
    var didOverscroll: Bool = false
    var convLimit: Int = 20
    var groupId: Int = 0;
    var messages: [GroupMessageModel] = []
    var passedValue: (groupName: String, groupId: Int, groupDescription: String, owner: Int, groupPicture: String)?
    var groupIndex: Int?
    var members = [String: (name: String, profilePicture: String)]()
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        self.dropInit()
        self.dropButton.table.isHidden = true
        
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 60
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        self.setListeners()
        if let value = passedValue {
            
            titleLabel.text = value.groupName
            descriptionLabel.text = value.groupDescription
            self.groupId = value.groupId
            
            if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
                SocketIOManager.sharedInstance.createGroupRoom(groupId: String(value.groupId))
            } else {
//                noInternetAllert()
                if (UserDefaults.standard.value(forKey: "group\(groupIndex!)") != nil) {
                    // retrieving a value for a key
                    if let data = UserDefaults.standard.data(forKey: "group\(groupIndex!)"),
                        let groupMessages = NSKeyedUnarchiver.unarchiveObject(with: data) as? [GroupMessageModel] {
                        messages = groupMessages
                        self.tableView.reloadData()
                    }
                }
            }
            if value.owner == UserDefaults.standard.value(forKey: "userId") as! Int {
                dropButton.isHidden = false
                leaveButton.isHidden = true
            } else {
                dropButton.isHidden = true
                leaveButton.isHidden = false
            }
        }
        
        // Adding the gesture recognizer that will dismiss the keyboard on an exterior tap
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIInputViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        navigationView.addGestureRecognizer(tap)
        tableView.addGestureRecognizer(tap)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillHide, object: nil)
    }
    
    func tableView(_ tableView:UITableView, numberOfRowsInSection section:Int) -> Int
    {
        return messages.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        if messages.count == 0 {
            if let cell = tableView.dequeueReusableCell(withIdentifier: "sentMessageCell") as? ConversationSentMessageTableViewCell {
                cell.configureCell("", "")
                return cell
            } else {
                return ConversationSentMessageTableViewCell()
            }
        } else {
            if messages[indexPath.row].senderId == String(describing: UserDefaults.standard.value(forKey: "userId")!) {
                if let cell = tableView.dequeueReusableCell(withIdentifier: "sentMessageCell") as? ConversationSentMessageTableViewCell {
                    
                    let item: GroupMessageModel = messages[indexPath.row]
                    cell.configureCell(item.message!, item.timestamp!)
                    return cell
                } else {
                    return ConversationSentMessageTableViewCell()
                }
            } else {
                if let cell = tableView.dequeueReusableCell(withIdentifier: "receivedMessageCell") as? ConversationReceivedMessageTableViewCell {
                    
                    let item: GroupMessageModel = messages[indexPath.row]
                    cell.configureCell(item.message!, item.timestamp!, item.profilePicture!)
                    return cell
                } else {
                    return ConversationReceivedMessageTableViewCell()
                }
            }
        }
    }
    
    // The function called at the arival of the response from the server
    func membersDownloaded(_ membersDetails: [[String:Any]]) {
        
        // parse the received JSON and save the contacts
        for i in 0 ..< membersDetails.count {
            
            if let userId = membersDetails[i]["user_id"] as? Int,
                let userFullName = membersDetails[i]["name"] as? String
            {
                var item: (name: String, profilePicture: String)
                item.name = userFullName
                var picture = ""
                if let profilePicture = membersDetails[i]["profile_picture"] as? String {
                    let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(profilePicture)")
                    if FileManager.default.fileExists(atPath: filename.path) {
                        picture = profilePicture
                    } else {
                        // Download the profile picture, if exists
                        if let url = URL(string: "http://188.166.157.62/profile_pictures/\(profilePicture)") {
                            if let data = try? Data(contentsOf: url) {
                                var profileImg: UIImage
                                profileImg = UIImage(data: data)!
                                if let data = UIImagePNGRepresentation(profileImg) {
                                    try? data.write(to: filename)
                                    picture = profilePicture
                                } else {
                                    picture = ""
                                }
                            } else {
                                picture = ""
                            }
                        }
                    }
                } else {
                    picture = ""
                }
                item.profilePicture = picture
                members[String(userId)] = item
            }
        }
        SocketIOManager.sharedInstance.getRecentGroupMessages(groupId: self.groupId, limit: String(self.convLimit))
    }
    
    // The function called at the arival of the response from the server
    func messagesDownloaded(_ messagesDetails: [[String:Any]]) {
        
        var messagesAux: [GroupMessageModel] = []

        // parse the received JSON and save the messages
        for i in 0 ..< messagesDetails.count {
            
            if let messageId = messagesDetails[i]["message_id"] as? Int,
                let senderId = messagesDetails[i]["user_id"] as? Int,
                let message = messagesDetails[i]["message"] as? String,
                let timestamp = messagesDetails[i]["timestmp"] as? String
            {
                var pp:String
                var name: String
                if let x = (self.members["\(senderId)"])?.profilePicture {
                    pp = x
                } else {
                    pp = ""
                }
                if let y = (self.members["\(senderId)"])?.name {
                    name = y
                } else {
                    name = ""
                }
                
                let item = GroupMessageModel(messageId: messageId, senderId: String(describing: senderId), message: message, timestamp: timestamp, profilePicture: pp, name: name)

                messagesAux.insert(item, at: 0)
            }
        }
        messages = messagesAux
        
        let storedGroupMessages = NSKeyedArchiver.archivedData(withRootObject: messages)
        UserDefaults.standard.set(storedGroupMessages, forKey:"group\(groupIndex!)");
        
        self.tableView.reloadData()
        if self.convLimit == 20 {
            self.tableViewScrollToBottom(topOrBottom: true, animated: true, delay: 100)
        } else {
            self.tableViewScrollToBottom(topOrBottom: false, animated: true, delay: 100)
        }
    }
    
    @IBAction func sendButtonPressed(_ sender: Any) {
        if (messageInputTextField.text != nil && messageInputTextField.text != "") {
            if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
                SocketIOManager.sharedInstance.sendGroupMessage(message: messageInputTextField.text!)
            } else {
                let item: GroupMessageModel = GroupMessageModel(messageId: 0, senderId: String(describing: UserDefaults.standard.value(forKey: "userId")!), message: messageInputTextField.text!, timestamp: "!!!", profilePicture: "", name: "")
                
                var storedMessages: [StoredMessagesModel]
                if (UserDefaults.standard.value(forKey: "storedMessages") != nil) {
                    if let data = UserDefaults.standard.data(forKey: "storedMessages"),
                        let storedMessagesAux = NSKeyedUnarchiver.unarchiveObject(with: data) as? [StoredMessagesModel] {
                        storedMessages = storedMessagesAux
                    } else {
                        storedMessages = []
                    }
                } else {
                    storedMessages = []
                }
                
                storedMessages.append(StoredMessagesModel(sender: String(describing: UserDefaults.standard.value(forKey: "userId")!), receiver: passedValue!.groupId, message: messageInputTextField.text!, messageType: "group"))
                let storedMessagesAux = NSKeyedArchiver.archivedData(withRootObject: storedMessages)
                UserDefaults.standard.set(storedMessagesAux, forKey:"storedMessages");
                messages.append(item)
                let storedGroupMessages = NSKeyedArchiver.archivedData(withRootObject: messages)
                UserDefaults.standard.set(storedGroupMessages, forKey:"group\(groupIndex!)");
                self.tableView.reloadData()
                self.tableViewScrollToBottom(topOrBottom: true, animated: true, delay: 100)
            }
            messageInputTextField.text = ""
        }
    }
  
    func tableViewScrollToBottom(topOrBottom: Bool, animated: Bool, delay: Int) {
        
        DispatchQueue.main.asyncAfter(deadline: .now() + .milliseconds(delay)) {
            
            if topOrBottom {
                let numberOfRows = self.tableView.numberOfRows(inSection: 0)
                
                if numberOfRows > 0 {
                    let indexPath = IndexPath(row: numberOfRows-1, section: 0)
                    self.tableView.scrollToRow(at: indexPath, at: .bottom, animated: animated)
                }
            } else {
                let indexPath = IndexPath(row: 0, section: 0)
                self.tableView.scrollToRow(at: indexPath, at: .top, animated: animated)
            }
        }
    }
    
    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        if (scrollView.contentOffset.y <= -50) {
            self.didOverscroll = true
        } else if (scrollView.contentOffset.y <= 0) {
            self.didOverscroll = false
        } else {
            self.didOverscroll = false
        }
    }
    
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        if (scrollView.contentOffset.y == 0) {
            if self.didOverscroll {
                if self.messages.count >= self.convLimit {
                    self.convLimit += 20
                    
                    SocketIOManager.sharedInstance.getRecentGroupMessages(groupId: self.groupId, limit: String(self.convLimit))
                }
                self.didOverscroll = false
            }
        } else {
            self.didOverscroll = false
        }
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
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
            let changeInHeight = (keyboardFrame.height) * (show ? 1 : -1)
            
            self.bottomConstraint.constant += changeInHeight
            UIView.animate(withDuration: duration!, delay: 0, options: options, animations: {
                
                self.view.layoutIfNeeded()
            }, completion: nil)
        }
        
        self.tableViewScrollToBottom(topOrBottom: true, animated: false, delay: 0)
    }
    
    @IBAction func backButtonPressed(_ sender: Any) {
        let _ = navigationController?.popViewController(animated: true)
        navigationController?.topViewController?.childViewControllers[1].viewWillAppear(true)
    }

    @IBAction func leaveButtonPressed(_ sender: Any) {
        // Popup for validation with accepting a contact request on accept
        let myAlert = UIAlertController(title:"Leave group", message:"Are you sure you want to leave this group?", preferredStyle:.alert);
        let yesAction=UIAlertAction(title:"Yes", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in
            SocketIOManager.sharedInstance.leaveGroup(userId: UserDefaults.standard.value(forKey: "userId") as! Int, groupId: (self.passedValue?.groupId)!)
        });
        myAlert.addAction(yesAction);
        let noAction=UIAlertAction(title:"No", style:UIAlertActionStyle.default, handler:nil);
        myAlert.addAction(noAction);
        self.present(myAlert, animated:true, completion:nil);
        self.dropButton.table.isHidden = true
    }
    
    @IBAction func menuPressed(_ sender: Any) {
        if self.dropButton.table.isHidden {
            self.dropInit()
            let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(dismissMenu))
            tap.cancelsTouchesInView = false
            self.view.addGestureRecognizer(tap)
            self.tableView.addGestureRecognizer(tap)
        }
    }
    
    //touch the space and hide drop down menu
    func dismissMenu(gestureRecognizer: UITapGestureRecognizer) {
        if !self.dropButton.table.frame.contains(gestureRecognizer.location(in: self.view)) {
            self.dropButton.table.isHidden = true
        }
    }
    
    func setListeners() {
        SocketIOManager.sharedInstance.setGroupRoomCreatedListener(completionHandler: { (response) -> Void in
            if response == "fail" {
                print("room create error")
            } else {
                SocketIOManager.sharedInstance.setRoomListener(room: response, completionHandler: { (messageId, username, message, timestamp) -> Void in
                    
                    let item = GroupMessageModel(messageId: messageId, senderId: username, message: message, timestamp: timestamp, profilePicture: self.members[username]!.profilePicture, name: self.members[username]!.name)
                    self.messages.append(item)
                    self.tableView.reloadData()
                    self.tableViewScrollToBottom(topOrBottom: true, animated: true, delay: 100)
                })
                SocketIOManager.sharedInstance.setGotGroupMembersListener(completionHandler: { (membersList) -> Void in
                    DispatchQueue.main.async(execute: { () -> Void in
                        self.membersDownloaded(membersList!)
                    })
                })
                SocketIOManager.sharedInstance.setGetRecentGroupMessagesListener(completionHandler: { (messagesList) -> Void in
                    DispatchQueue.main.async(execute: { () -> Void in
                        self.messagesDownloaded(messagesList!)
                    })
                })
                SocketIOManager.sharedInstance.setGroupDeletedListener(completionHandler: { (response) -> Void in
                    print(response)
                    if response == "success" {
                        let _ = self.navigationController?.popViewController(animated: true)
                        self.navigationController?.topViewController?.childViewControllers[1].viewWillAppear(true)
                    }
                })
                SocketIOManager.sharedInstance.setGroupLeftListener(completionHandler: { (response) -> Void in
                    print(response)
                    if response == "success" {
                        let _ = self.navigationController?.popViewController(animated: true)
                        self.navigationController?.topViewController?.childViewControllers[1].viewWillAppear(true)
                    }
                })
                if self.passedValue != nil {
                    SocketIOManager.sharedInstance.getGroupMembers(groupId: String(self.groupId))
                }
            }
        })
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setMyRequestAcceptedListener(completionHandler: { () -> Void in })
        
        if let value = passedValue {
            SocketIOManager.sharedInstance.setIWasDeletedFromGroupListener(completionHandler: { (enemy) -> Void in
                if (enemy == String(describing: (self.passedValue?.groupId)!)) {
                    let _ = self.navigationController?.popViewController(animated: true)
                    self.navigationController?.topViewController?.childViewControllers[0].viewWillAppear(true)
                }
            })
        }
    }
    
    func noInternetAllert() {
        let alertView = UIAlertController(title: "No internet connection",
                                          message: "Please reconnect to the internet" as String, preferredStyle:.alert)
        let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
        alertView.addAction(okAction)
        self.present(alertView, animated: true, completion: nil)
    }
    
    //Dropdown menu Initinal
    func dropInit() {
        dropButton.initMenu(["Add contacts to group", "Delete group"],actions: [({ () -> (Void) in
            let addMembersToGroupViewController = self.storyboard?.instantiateViewController(withIdentifier: "addMembersToGroupViewController") as? AddMembersToGroupViewController
            addMembersToGroupViewController?.passedValue = self.passedValue
            addMembersToGroupViewController?.passedMembers = self.members
            self.navigationController?.pushViewController(addMembersToGroupViewController!, animated: true)
            self.dropButton.table.isHidden = true
        }), ({ () -> (Void) in
            // Popup for validation with accepting a contact request on accept
            let myAlert = UIAlertController(title:"Delete group", message:"Are you sure you want to delete this group?", preferredStyle:.alert);
            let yesAction=UIAlertAction(title:"Yes", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in
                SocketIOManager.sharedInstance.deleteGroup(userId: UserDefaults.standard.value(forKey: "userId") as! Int, groupId: self.groupId)
            });
            myAlert.addAction(yesAction);
            let noAction=UIAlertAction(title:"No", style:UIAlertActionStyle.default, handler:nil);
            myAlert.addAction(noAction);
            self.present(myAlert, animated:true, completion:nil);
            self.dropButton.table.isHidden = true
        })])
    }
    
}
