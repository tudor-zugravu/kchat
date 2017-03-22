//
//  SocketIOManager.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/11/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit
import AVFoundation
import UserNotifications

class SocketIOManager: NSObject {
    static let sharedInstance = SocketIOManager()
    
    var socket: SocketIOClient = SocketIOClient(socketURL: NSURL(string: "http://188.166.157.62:4000")! as URL)
    
    var pendingEmits: [(event: String, param: String)] = []
    private var currentRoom: String = ""
    var player: AVAudioPlayer?
    
    override init() {
        super.init()
    }
    
    func establishConnection() {
        socket = SocketIOClient(socketURL: NSURL(string: "http://188.166.157.62:4000")! as URL)
        socket.connect()
        socket.on("connect") {data, ack in
            self.socket.emit("authenticate", UserDefaults.standard.value(forKey: "userId") as! Int, UserDefaults.standard.value(forKey: "fullName") as! String)
        }
        
        socket.on("authenticated") {data, ack in
            for currEmit in self.pendingEmits {
                self.socket.emit(currEmit.event, currEmit.param)
            }
            self.pendingEmits.removeAll()
            
            self.socket.on("update_chat") { ( dataArray, ack) -> Void in
                let responseString = dataArray[0] as! String
                print(responseString)
            }
        }
    }
    
    func closeConnection() {
        socket.disconnect()
    }
    
    func setCurrentRoom (room: String) {
        self.currentRoom = room
    }
    
    func setSearchUserReceivedListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        
        socket.on("search_user_received") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler([[:]])
            } else {
                if let data = responseString.data(using: .utf8) {
                    do {
                        let parsedData = try JSONSerialization.jsonObject(with: data, options: []) as! [[String:Any]]
                        completionHandler(parsedData)
                    } catch {
                        print(error.localizedDescription)
                    }
                }
            }
        }
    }
    
    func predictSearch(username: String, userId: String) {
        
        socket.emit("search_user_filter", username, userId)
    }
    
    func setSentRequestListener(completionHandler: @escaping (_ status: Bool) -> Void) {
        
        socket.on("sent_request") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler(false)
            } else {
                completionHandler(true)
            }
        }
    }
    
    func addContact(userId: String, receiverId: String) {
        
        socket.emit("send_contact_request", userId, receiverId)
    }
    
    func setSentRequestsListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        
        socket.on("sent_requests") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler([[:]])
            } else {
                if let data = responseString.data(using: .utf8) {
                    do {
                        let parsedData = try JSONSerialization.jsonObject(with: data, options: []) as! [[String:Any]]
                        completionHandler(parsedData)
                    } catch {
                        print(error.localizedDescription)
                    }
                }
            }
        }
    }
    
    func getSentRequests(userId: String) {
        
        socket.emit("sent_contact_requests", userId)
    }
    
    func setRequestDeletedListener(completionHandler: @escaping (_ status: String) -> Void) {
        
        socket.on("request_deleted") { ( dataArray, ack) -> Void in

            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func deleteRequest(userId: String, receiverId: String, receiver: String) {
        
        socket.emit("delete_contact_request", userId, receiverId, receiver)
    }
    
    func setReceivedRequestsListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {

        socket.on("received_requests") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler([[:]])
            } else {
                if let data = responseString.data(using: .utf8) {
                    do {
                        let parsedData = try JSONSerialization.jsonObject(with: data, options: []) as! [[String:Any]]
                        completionHandler(parsedData)
                    } catch {
                        print(error.localizedDescription)
                    }
                }
            }
        }
    }

    func getReceivedRequests(userId: String) {
        
        socket.emit("received_contact_requests", userId)
    }
    
    func setRequestAcceptedListener(completionHandler: @escaping (_ status: String) -> Void) {
        socket.on("request_accepted") { ( dataArray, ack) -> Void in
        
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }

    func acceptContact(userId: String, receiverId: String, receiver: String) {
        socket.emit("accept_contact_request", receiverId, userId, receiver)
    }
    
    func setGetChatsListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        socket.on("sent_chats") { ( dataArray, ack) -> Void in
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler([[:]])
            } else {
                if let data = responseString.data(using: .utf8) {
                    do {
                        let parsedData = try JSONSerialization.jsonObject(with: data, options: []) as! [[String:Any]]
                        completionHandler(parsedData)
                    } catch {
                        print(error.localizedDescription)
                    }
                }
            }
        }
    }

    func getChats(userId: String) {
        if socket.status.rawValue == 3 {
            socket.emit("get_chats", userId)
        } else {
            pendingEmits.append(("get_chats", userId))
        }
    }
    
    func setPrivateRoomCreatedListener(completionHandler: @escaping (_ response: String) -> Void) {
        socket.off("private_room_created")
        socket.on("private_room_created") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func createPrivateRoom(receiverId: String, userId: String) {
        socket.emit("create_private_room", receiverId, userId)
    }
    
    func sendMessage(message: String) {
        socket.emit("send_chat", message)
    }
    
    func sendGroupMessage(message: String) {
        socket.emit("send_group_chat", message)
    }
    
    func setRoomListener(room: String, completionHandler: @escaping (_ messageId: Int, _ username: String, _ message: String, _ timestamp: String) -> Void) {
        
        currentRoom = room
        socket.on(room) { ( dataArray, ack) -> Void in
            
            let messageId = dataArray[0] as! Int
            let username = dataArray[1] as! String
            let message = dataArray[2] as! String
            let timestamp = dataArray[3] as! String
            completionHandler(messageId, username, message, timestamp)
        }
    }
    
    func setGlobalPrivateListener(completionHandler: @escaping () -> Void) {
        
        self.socket.off("global_private_messages")
        self.socket.on("global_private_messages") { ( dataArray, ack) -> Void in
            
            let room = dataArray[0] as! String
            if (room != self.currentRoom) {
              
                AudioServicesPlaySystemSound (1334)
                
                let username = dataArray[2] as! String
                let message = dataArray[3] as! String

                let content = UNMutableNotificationContent()
                content.title = username
                content.body = message
                content.categoryIdentifier = "newMessage.category"
                let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 0.1, repeats: false)
                let request = UNNotificationRequest(identifier: "newMessage", content: content, trigger: trigger)
                
                UNUserNotificationCenter.current().add(request){
                    (error) in
                    if error != nil{
                        print ("Add notification error: \(error?.localizedDescription)")
                    }
                }
                completionHandler()
            }
        }
    }

    
    func setIWasDeletedListener(completionHandler: @escaping (_ enemy: String) -> Void) {
        
        self.socket.off("you_were_deleted")
        self.socket.on("you_were_deleted") { ( dataArray, ack) -> Void in
            
            let enemy = dataArray[0] as! String
            completionHandler(enemy)
        }
    }
    
    func setIReceivedContactRequestListener(completionHandler: @escaping () -> Void) {
        
        self.socket.off("you_received_contact_request")
        self.socket.on("you_received_contact_request") { ( dataArray, ack) -> Void in
            
            let friend = dataArray[0] as! String
            AudioServicesPlaySystemSound (1334)
            
            let content = UNMutableNotificationContent()
            content.title = "Contact Request Received"
            content.body = "\(friend) wants to add you as a contact!"
            content.categoryIdentifier = "newContact.category"
            let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 0.1, repeats: false)
            let request = UNNotificationRequest(identifier: "newContact", content: content, trigger: trigger)
            
            UNUserNotificationCenter.current().add(request){
                (error) in
                if error != nil{
                    print ("Add notification error: \(error?.localizedDescription)")
                }
            }
            completionHandler()
        }
    }
    
    func setNoMoreContactRequestListener(completionHandler: @escaping () -> Void) {
        
        self.socket.off("no_more_contact_request")
        self.socket.on("no_more_contact_request") { ( dataArray, ack) -> Void in
        
            AudioServicesPlaySystemSound (1334)
            completionHandler()
        }
    }
    
    func setMyRequestAcceptedListener(completionHandler: @escaping () -> Void) {
        
        self.socket.off("accepted_my_contact_request")
        self.socket.on("accepted_my_contact_request") { ( dataArray, ack) -> Void in
            print("yeeeeeep")
            AudioServicesPlaySystemSound (1334)
            completionHandler()
        }
    }
    
    func setDisconnectedListener(completionHandler: @escaping () -> Void) {
        self.socket.on("disconnected") { ( dataArray, ack) -> Void in
            completionHandler()
        }
    }
    
    func getRecentMessage(userId: String, receiverId: String, limit: String) {
        socket.emit("get_recent_messages", userId, receiverId, limit)
    }
    
    func setGetRecentMessagesListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        socket.on("send_recent_messages") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler([[:]])
            } else {
                if let data = responseString.data(using: .utf8) {
                    do {
                        let parsedData = try JSONSerialization.jsonObject(with: data, options: []) as! [[String:Any]]
                        completionHandler(parsedData)
                    } catch {
                        print(error.localizedDescription)
                    }
                }
            }
        }
    }
    
    func createGroup(name: String, description: String, ownerId: Int, group_picture: String, members: [Int]) {
        socket.emit("create_group", name, description, ownerId, group_picture, members)
    }
    
    func setGroupCreatedListener(completionHandler: @escaping (_ userList: String?) -> Void) {
        socket.on("group_created") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func setGetGroupChatsListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        socket.on("sent_group_chats") { ( dataArray, ack) -> Void in
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler([[:]])
            } else {
                if let data = responseString.data(using: .utf8) {
                    do {
                        let parsedData = try JSONSerialization.jsonObject(with: data, options: []) as! [[String:Any]]
                        completionHandler(parsedData)
                    } catch {
                        print(error.localizedDescription)
                    }
                }
            }
        }
    }
    
    func getGroupChats(userId: String) {
        if socket.status.rawValue == 3 {
            socket.emit("get_group_chats", userId)
        } else {
            if !pendingEmits.contains(where: { (event, params) -> Bool in
                if (event == "get_chats") {
                    return true
                }
                return false
            }) {
                pendingEmits.append(("get_chats", userId))
            }
        }
    }
    
    func getRecentGroupMessages(groupId: Int, limit: String) {
        socket.emit("get_recent_group_messages", groupId, limit)
    }
    
    func setGetRecentGroupMessagesListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        socket.on("send_recent_group_messages") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler([[:]])
            } else {
                if let data = responseString.data(using: .utf8) {
                    do {
                        let parsedData = try JSONSerialization.jsonObject(with: data, options: []) as! [[String:Any]]
                        completionHandler(parsedData)
                    } catch {
                        print(error.localizedDescription)
                    }
                }
            }
        }
    }
    
    func deleteContact(userId: Int, otherUserId: Int) {
        socket.emit("delete_contact", userId, otherUserId)
    }
    
    func setContactDeletedListener(completionHandler: @escaping (_ response: String) -> Void) {
        socket.on("contact_deleted") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func setGroupRoomCreatedListener(completionHandler: @escaping (_ response: String) -> Void) {
        socket.off("group_room_created")
        socket.on("group_room_created") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func createGroupRoom(groupId: String) {
        socket.emit("create_group_room", groupId)
    }
}
