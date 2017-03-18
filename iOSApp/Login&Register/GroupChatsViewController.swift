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
    
    var chats: [GroupChatModel] = []
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        searchBar.delegate = self
        
        self.tableView.contentInset = UIEdgeInsetsMake(8, 0, 0, 0)
        
        SocketIOManager.sharedInstance.setGetGroupChatsListener(completionHandler: { (userList) -> Void in
            DispatchQueue.main.async(execute: { () -> Void in
                self.chatsDownloaded(userList!)
            })
        })
        
        SocketIOManager.sharedInstance.setGroupCreatedListener(completionHandler: { (response) -> Void in
            if response == "fail" {
                print("group create error")
            } else {
                print(response);
            }
        })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        SocketIOManager.sharedInstance.getGroupChats(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
    }
    
    func tableView(_ tableView:UITableView, numberOfRowsInSection section:Int) -> Int
    {
        return chats.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "chatsCell") as? ChatsTableViewCell {
            if chats.count == 0 {
                cell.configureCell("", lastMessage: "", timestamp: "", profilePic: "")
            } else {
                let item: GroupChatModel = chats[indexPath.row]
                cell.configureCell(item.groupName!, lastMessage: item.lastMessage!, timestamp: item.timestamp!, profilePic: item.groupPicture!)
            }
            return cell
        } else {
            return ChatsTableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        print("tap on \(chats[indexPath.row].groupName!), \(chats[indexPath.row].groupId!), \(chats[indexPath.row].groupDescription!)");
        
        let groupConversationViewController = self.storyboard?.instantiateViewController(withIdentifier: "groupConversationViewController") as? GroupConversationViewController
        groupConversationViewController?.passedValue = (chats[indexPath.row].groupName!, chats[indexPath.row].groupId!, chats[indexPath.row].groupDescription!)
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
                let groupDescription = chatDetails[i]["description"] as? String
            {
                
                item = GroupChatModel()
                
                item.groupId = groupId
                item.groupName = groupName
                item.groupDescription = groupDescription
                
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
                        if let url = URL(string: "http://188.166.157.62/profile_pictures/\(groupPicture)") {
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
        
        self.tableView.reloadData()
    }
    
    @IBAction func createGroupButtonPressed(_ sender: Any) {
        let members: [Int] = [2, 33, 36]
        SocketIOManager.sharedInstance.createGroup(name: "test", description: "this group is meant to be used for testing", ownerId: 34, group_picture: "group_picture1.jpg", members: members)
    }
}
