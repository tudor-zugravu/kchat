//
//  ConversationViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/15/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ConversationViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var messageInputTextField: UITextField!
    @IBOutlet weak var sendButton: UIButton!
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var navigationView: UIView!
    
    var didOverscroll: Bool = false
    var convLimit: Int = 20
    var contactId: Int = 0;
    var messages: [MessageModel] = []
    var passedValue: (contactName: String, contactId: Int)?
    var cameFrom: Bool?
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 60
        
        SocketIOManager.sharedInstance.setPrivateRoomCreatedListener(completionHandler: { (response) -> Void in
            if response == "fail" {
                print("room create error")
            } else {
                SocketIOManager.sharedInstance.setRoomListener(room: response, completionHandler: { (messageId, username, message, timestamp) -> Void in
                    
                    let item = MessageModel(messageId: messageId, senderId: username, message: message, timestamp: timestamp)
                    
                    self.messages.append(item)
                    self.tableView.reloadData()
                    self.tableViewScrollToBottom(topOrBottom: true, animated: true, delay: 100)
                })
                SocketIOManager.sharedInstance.setGetRecentMessagesListener(completionHandler: { (messagesList) -> Void in
                    DispatchQueue.main.async(execute: { () -> Void in
                        self.messagesDownloaded(messagesList!)
                    })
                })
                if let value = self.passedValue {
                    SocketIOManager.sharedInstance.getRecentMessage(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!), receiverId: String(value.contactId), limit: String(self.convLimit))
                }
            }
        })
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIWasDeletedListener(completionHandler: { (enemy) -> Void in
            if (enemy == String(describing: (self.passedValue?.contactId)!)) {
                let _ = self.navigationController?.popViewController(animated: true)
                if self.cameFrom! == true {
                    self.navigationController?.topViewController?.childViewControllers[0].viewWillAppear(true)
                } else {
                    self.navigationController?.topViewController?.childViewControllers[2].viewWillAppear(true)
                }
            }
        })
        
        if let value = passedValue {
            titleLabel.text = value.contactName
            self.contactId = value.contactId
            
            SocketIOManager.sharedInstance.createPrivateRoom(receiverId: String(value.contactId), userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
        }
        
        // Adding the gesture recognizer that will dismiss the keyboard on an exterior tap
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIInputViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        navigationView.addGestureRecognizer(tap)
        tableView.addGestureRecognizer(tap)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        SocketIOManager.sharedInstance.setCurrentRoom(room: "")
    }
    
    func tableView(_ tableView:UITableView, numberOfRowsInSection section:Int) -> Int
    {
        return messages.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        if messages.count == 0 {
            if let cell = tableView.dequeueReusableCell(withIdentifier: "sentMessageCell") as? ConversationSentMessageTableViewCell {
                cell.configureCell("")
                return cell
            } else {
                return ConversationSentMessageTableViewCell()
            }
        } else {
            if messages[indexPath.row].senderId == String(describing: UserDefaults.standard.value(forKey: "userId")!) {
                if let cell = tableView.dequeueReusableCell(withIdentifier: "sentMessageCell") as? ConversationSentMessageTableViewCell {
                    
                    let item: MessageModel = messages[indexPath.row]
                    cell.configureCell(item.message!)
                    return cell
                } else {
                    return ConversationSentMessageTableViewCell()
                }
            } else {
                if let cell = tableView.dequeueReusableCell(withIdentifier: "receivedMessageCell") as? ConversationReceivedMessageTableViewCell {
                    
                    let item: MessageModel = messages[indexPath.row]
                    cell.configureCell(item.message!)
                    return cell
                } else {
                    return ConversationReceivedMessageTableViewCell()
                }
            }
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    // The function called at the arival of the response from the server
    func messagesDownloaded(_ messagesDetails: [[String:Any]]) {
        
        var messagesAux: [MessageModel] = []
        
        // parse the received JSON and save the messages
        for i in 0 ..< messagesDetails.count {
            
            if let messageId = messagesDetails[i]["message_id"] as? Int,
                let senderId = messagesDetails[i]["sender_id"] as? Int,
                let message = messagesDetails[i]["message"] as? String,
                let timestamp = messagesDetails[i]["timestmp"] as? String
            {
                let item = MessageModel(messageId: messageId, senderId: String(describing: senderId), message: message, timestamp: timestamp)
                messagesAux.insert(item, at: 0)
            }
        }
        messages = messagesAux
        
        self.tableView.reloadData()
        if self.convLimit == 20 {
            self.tableViewScrollToBottom(topOrBottom: true, animated: true, delay: 100)
        } else {
            self.tableViewScrollToBottom(topOrBottom: false, animated: true, delay: 100)
        }
    }
    
    @IBAction func sendButtonPressed(_ sender: Any) {
        
        if (messageInputTextField.text != nil && messageInputTextField.text != "") {
            
            SocketIOManager.sharedInstance.sendMessage(message: messageInputTextField.text!)
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
                if messages.count >= self.convLimit {
                    self.convLimit += 20
                    
                    SocketIOManager.sharedInstance.getRecentMessage(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!), receiverId: String(self.contactId), limit: String(self.convLimit))
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
        if cameFrom! == true {
            navigationController?.topViewController?.childViewControllers[0].viewWillAppear(true)
        } else {
            navigationController?.topViewController?.childViewControllers[2].viewWillAppear(true)
        }
    }
    
}
