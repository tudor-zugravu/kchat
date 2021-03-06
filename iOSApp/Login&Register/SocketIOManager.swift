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
    
    var socket: SocketIOClient = SocketIOClient(socketURL: NSURL(string: "http://188.166.157.62:3000")! as URL)
    
    var pendingEmits: [(event: String, param: String)] = []
    private var currentRoom: String = ""
    var player: AVAudioPlayer?
    
    override init() {
        super.init()
    }
    
    func isConnected() -> Bool {
        return socket.status.rawValue != 0
    }
    
    func establishConnection() {
        socket = SocketIOClient(socketURL: NSURL(string: "http://188.166.157.62:3000")! as URL)
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
        
        socket.off("search_user_received")
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
        
        socket.off("sent_request")
        socket.on("sent_request") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler(false)
            } else {
                completionHandler(true)
            }
        }
    }
    
    func addContact(userId: String, userName: String, receiverId: String) {
        
        socket.emit("send_contact_request", userId, userName, receiverId)
    }
    
    func setSentRequestsListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        
        socket.off("sent_requests")
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
        
        socket.off("request_deleted")
        socket.on("request_deleted") { ( dataArray, ack) -> Void in

            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func deleteRequest(userId: String, receiverId: String, receiver: String) {
        
        socket.emit("delete_contact_request", userId, receiverId, receiver)
    }
    
    func setReceivedRequestsListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        socket.off("received_requests")
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
        
        socket.off("request_accepted")
        socket.on("request_accepted") { ( dataArray, ack) -> Void in
        
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }

    func acceptContact(userId: String, receiverId: String, receiver: String) {
        socket.emit("accept_contact_request", receiverId, userId, receiver)
    }
    
    func setGetChatsListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        
        socket.off("sent_chats")
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
        socket.off(room)
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
    
    func setIWasDeletedFromGroupListener(completionHandler: @escaping (_ enemy: String) -> Void) {
        
        self.socket.off("you_were_deleted_from_group")
        self.socket.on("you_were_deleted_from_group") { ( dataArray, ack) -> Void in
            
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
            AudioServicesPlaySystemSound (1334)
            completionHandler()
        }
    }
    
    func setIHaveBeenAddedToGroupListener(completionHandler: @escaping () -> Void) {
        
        self.socket.off("you_have_been_added_to_group")
        self.socket.on("you_have_been_added_to_group") { ( dataArray, ack) -> Void in
            AudioServicesPlaySystemSound (1334)
            completionHandler()
        }
    }
    
    func setDisconnectedListener(completionHandler: @escaping () -> Void) {
        self.socket.off("disconnected")
        self.socket.on("disconnected") { ( dataArray, ack) -> Void in
            completionHandler()
        }
    }
    
    func getRecentMessage(userId: String, receiverId: String, limit: String) {
        socket.emit("get_recent_messages", userId, receiverId, limit)
    }
    
    func setGetRecentMessagesListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        socket.off("send_recent_messages")
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
        socket.off("group_created")
        socket.on("group_created") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func deleteGroup(userId: Int, groupId: Int) {
        socket.emit("delete_group", userId, groupId)
    }
    
    func setGroupDeletedListener(completionHandler: @escaping (_ userList: String?) -> Void) {
        socket.off("group_deleted")
        socket.on("group_deleted") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func leaveGroup(userId: Int, groupId: Int) {
        socket.emit("leave_group", userId, groupId)
    }
    
    func setGroupLeftListener(completionHandler: @escaping (_ userList: String?) -> Void) {
        socket.off("group_left")
        socket.on("group_left") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func addToGroup(groupId: Int, members: [Int]) {
        socket.emit("add_to_group", groupId, members)
    }
    
    func setAddedToGroupListener(completionHandler: @escaping (_ userList: String?) -> Void) {
        socket.off("added_to_group")
        socket.on("added_to_group") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func setGetGroupChatsListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        socket.off("sent_group_chats")
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
    
    func setGotGroupMembersListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        socket.off("send_group_members")
        socket.on("send_group_members") { ( dataArray, ack) -> Void in
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
    
    func getGroupMembers(groupId: String) {
        if socket.status.rawValue == 3 {
            socket.emit("get_group_members", groupId)
        } else {
            if !pendingEmits.contains(where: { (event, params) -> Bool in
                if (event == "get_group_members") {
                    return true
                }
                return false
            }) {
                pendingEmits.append(("get_chats", groupId))
            }
        }
    }
    
    func getRecentGroupMessages(groupId: Int, limit: String) {
        socket.emit("get_recent_group_messages", groupId, limit)
    }
    
    func setGetRecentGroupMessagesListener(completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        socket.off("send_recent_group_messages")
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
        socket.off("contact_deleted")
        socket.on("contact_deleted") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func deleteAccount(userId: Int) {
        socket.emit("delete_account", userId)
    }
    
    func setAccountDeletedListener(completionHandler: @escaping (_ response: String) -> Void) {
        socket.off("account_deleted")
        socket.on("account_deleted") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            print(responseString)
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
    
    func changeFullName(userId: Int, newName: String) {
        socket.emit("change_fullname", userId, newName)
    }
    
    func setFullNameChangedListener(completionHandler: @escaping (_ response: String) -> Void) {
        socket.off("fullname_changed")
        socket.on("fullname_changed") { ( dataArray, ack) -> Void in
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func changeUsername(userId: Int, newUsername: String) {
        socket.emit("change_username", userId, newUsername)
    }
    
    func setUsernameChangedListener(completionHandler: @escaping (_ response: String) -> Void) {
        socket.off("username_changed")
        socket.on("username_changed") { ( dataArray, ack) -> Void in
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func changeEmail(userId: Int, newEmail: String) {
        socket.emit("change_email", userId, newEmail)
    }
    
    func setEmailChangedListener(completionHandler: @escaping (_ response: String) -> Void) {
        socket.off("email_changed")
        socket.on("email_changed") { ( dataArray, ack) -> Void in
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func changePhoneNo(userId: Int, newPhoneNo: String) {
        socket.emit("change_phone_number", userId, newPhoneNo)
    }
    
    func setPhoneNoChangedListener(completionHandler: @escaping (_ response: String) -> Void) {
        socket.off("phone_number_changed")
        socket.on("phone_number_changed") { ( dataArray, ack) -> Void in
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func changeAbout(userId: Int, newAbout: String) {
        socket.emit("change_about", userId, newAbout)
    }
    
    func setAboutChangedListener(completionHandler: @escaping (_ response: String) -> Void) {
        socket.off("about_changed")
        socket.on("about_changed") { ( dataArray, ack) -> Void in
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
}
