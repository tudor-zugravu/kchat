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
    
    var didOverscroll: Bool = false
    var convLimit: Int = 20
    var groupId: Int = 0;
    var messages: [MessageModel] = []
    var passedValue: (groupName: String, groupId: Int, groupDescription: String)?
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 60
        
        SocketIOManager.sharedInstance.setGroupRoomCreatedListener(completionHandler: { (response) -> Void in
            if response == "fail" {
                print("room create error")
            } else {
                SocketIOManager.sharedInstance.setRoomListener(room: response, completionHandler: { (messageId, username, message, timestamp) -> Void in
                    
                    let item = MessageModel(messageId: messageId, senderId: username, message: message, timestamp: timestamp)
                    self.messages.append(item)
                    self.tableView.reloadData()
                    self.tableViewScrollToBottom(topOrBottom: true, animated: true, delay: 100)
                })
                SocketIOManager.sharedInstance.setGetRecentGroupMessagesListener(completionHandler: { (messagesList) -> Void in
                    DispatchQueue.main.async(execute: { () -> Void in
                        self.messagesDownloaded(messagesList!)
                    })
                })
                if self.passedValue != nil {
                    SocketIOManager.sharedInstance.getRecentGroupMessages(groupId: self.groupId, limit: String(self.convLimit))
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
        
        if let value = passedValue {
            
            titleLabel.text = value.groupName
            descriptionLabel.text = value.groupDescription
            self.groupId = value.groupId
            
            SocketIOManager.sharedInstance.createGroupRoom(groupId: String(value.groupId))
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
    
    // The function called at the arival of the response from the server
    func messagesDownloaded(_ messagesDetails: [[String:Any]]) {
        
        var messagesAux: [MessageModel] = []
        
        // parse the received JSON and save the messages
        for i in 0 ..< messagesDetails.count {
            
            if let messageId = messagesDetails[i]["message_id"] as? Int,
                let senderId = messagesDetails[i]["user_id"] as? Int,
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
            
            SocketIOManager.sharedInstance.sendGroupMessage(message: messageInputTextField.text!)
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
    }
    
}