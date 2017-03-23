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
    
    var searchActive : Bool = false
    var chats: [ChatModel] = []
    var filteredChats: [ChatModel] = []
    
    override func viewDidLoad() {
        tableView.delegate = self
        tableView.dataSource = self
        searchBar.delegate = self
        
        self.tableView.contentInset = UIEdgeInsetsMake(8, 0, 0, 0)
        
        SocketIOManager.sharedInstance.setGetChatsListener(completionHandler: { (userList) -> Void in
            DispatchQueue.main.async(execute: { () -> Void in
                self.chatsDownloaded(userList!)
            })
        })
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in
            SocketIOManager.sharedInstance.getChats(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
        })
        SocketIOManager.sharedInstance.setIWasDeletedListener(completionHandler: { (enemy) -> Void in
            SocketIOManager.sharedInstance.getChats(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
        })
        SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })

        SocketIOManager.sharedInstance.getChats(userId: String(describing: UserDefaults.standard.value(forKey: "userId")!))
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        
        filteredChats = chats.filter({ (chat) -> Bool in
            return (chat.receiverName!.lowercased().hasPrefix(searchText.lowercased()));
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
            return filteredChats.count
        } else {
            return chats.count
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "chatsCell") as? ChatsTableViewCell {
            
            var item: ChatModel
            
            if(searchActive){
                item = filteredChats[indexPath.row]
            } else {
                item = chats[indexPath.row]
            }
            
            cell.configureCell(item.receiverName!, lastMessage: item.lastMessage!, timestamp: item.timestamp!, profilePic: item.profilePicture!)
            return cell
        } else {
            return ChatsTableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        let conversationViewController = self.storyboard?.instantiateViewController(withIdentifier: "conversationViewController") as? ConversationViewController
        conversationViewController?.passedValue = (chats[indexPath.row].receiverName!, chats[indexPath.row].receiverId!, chats[indexPath.row].profilePicture!)
        conversationViewController?.cameFrom = true
        self.navigationController?.pushViewController(conversationViewController!, animated: true)
    }
    
    // The function called at the arival of the response from the server
    func chatsDownloaded(_ chatDetails: [[String:Any]]) {
        
        var chatsAux: [ChatModel] = []
        var item:ChatModel;
        
        // parse the received JSON and save the contacts
        for i in 0 ..< chatDetails.count {
            
            if let receiverId = chatDetails[i]["receiverId"] as? Int,
                let receiverName = chatDetails[i]["receiverName"] as? String,
                let senderId = chatDetails[i]["senderId"] as? Int,
                let senderName = chatDetails[i]["senderName"] as? String,
                let lastMessage = chatDetails[i]["message"] as? String,
                let timestamp = chatDetails[i]["timestmp"] as? String
            {
                var ppicture = ""
                item = ChatModel()
                if senderId == UserDefaults.standard.value(forKey: "userId")! as! Int {
                    item.receiverId = receiverId
                    item.receiverName = receiverName
                    ppicture = "receiverProfilePicture";
                } else {
                    item.receiverId = senderId
                    item.receiverName = senderName
                    ppicture = "senderProfilePicture";
                }
                item.senderName = senderName
                item.lastMessage = lastMessage
                let separators = CharacterSet(charactersIn: "T.")
                item.timestamp = timestamp.components(separatedBy: separators)[1]
                
                if let profilePicture = chatDetails[i][ppicture] as? String {
                    
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
