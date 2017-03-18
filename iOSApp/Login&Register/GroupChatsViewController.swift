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
    
    var chats: [ChatModel] = []
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        searchBar.delegate = self
        
        SocketIOManager.sharedInstance.setGetGroupChatsListener(completionHandler: { (userList) -> Void in
            DispatchQueue.main.async(execute: { () -> Void in
                self.chatsDownloaded(userList!)
            })
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
                let item: ChatModel = chats[indexPath.row]
                cell.configureCell(item.receiverName!, lastMessage: item.lastMessage!, timestamp: item.timestamp!, profilePic: item.profilePicture!)
            }
            return cell
        } else {
            return ChatsTableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        print("tap on \(chats[indexPath.row].receiverName!), \(chats[indexPath.row].receiverId!)");
        
//        let conversationViewController = self.storyboard?.instantiateViewController(withIdentifier: "conversationViewController") as? ConversationViewController
//        conversationViewController?.passedValue = (chats[indexPath.row].receiverName!, chats[indexPath.row].receiverId!)
//        self.navigationController?.pushViewController(conversationViewController!, animated: true)
    }
    
    // The function called at the arival of the response from the server
    func chatsDownloaded(_ chatDetails: [[String:Any]]) {
        
        var chatsAux: [ChatModel] = []
        var item:ChatModel;
        
        // parse the received JSON and save the contacts
        for i in 0 ..< chatDetails.count {
            
            if let groupId = chatDetails[i]["group_id"] as? Int,
                let groupName = chatDetails[i]["name"] as? String,
                let lastMessage = chatDetails[i]["message"] as? String,
                let timestamp = chatDetails[i]["timestmp"] as? String
            {
                
                item = ChatModel()
                
                item.receiverId = groupId
                item.receiverName = groupName
                item.lastMessage = lastMessage
                let separators = CharacterSet(charactersIn: "T.")
                item.timestamp = timestamp.components(separatedBy: separators)[1]
                
                if let profilePicture = chatDetails[i]["group_picture"] as? String {
                    
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
                
                chatsAux.append(item)
            }
        }
        chats = chatsAux
        
        self.tableView.reloadData()
    }
    
}
