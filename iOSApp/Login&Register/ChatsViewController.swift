//
//  ChatsViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/14/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ChatsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    var chats: [ChatModel] = []
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        searchBar.delegate = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        SocketIOManager.sharedInstance.getChats(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!), completionHandler: { (userList) -> Void in
            DispatchQueue.main.async(execute: { () -> Void in
                self.chatsDownloaded(userList!)
            })
        })
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
    }
    
    // The function called at the arival of the response from the server
    func chatsDownloaded(_ chatDetails: [[String:Any]]) {
        
        var chatsAux: [ChatModel] = []
        var item:ChatModel;
        print(chatDetails)
        // parse the received JSON and save the contacts
        for i in 0 ..< chatDetails.count {
            
            if let receiverId = chatDetails[i]["receiverId"] as? Int,
                let receiverName = chatDetails[i]["receiverName"] as? String,
                let senderName = chatDetails[i]["senderName"] as? String,
                let lastMessage = chatDetails[i]["message"] as? String,
                let timestamp = chatDetails[i]["timestmp"] as? String
            {
                item = ChatModel()
                item.receiverId = receiverId
                item.receiverName = receiverName
                item.senderName = senderName
                item.lastMessage = lastMessage
                item.timestamp = timestamp
                
                if let profilePicture = chatDetails[i]["profilePicture"] as? String {
                    
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
                
                chatsAux.append(item)
            }
        }
        chats = chatsAux        
        
        self.tableView.reloadData()
    }
    
}
