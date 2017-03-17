//
//  SocketIOManager.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/11/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class SocketIOManager: NSObject {
    static let sharedInstance = SocketIOManager()
    
    var socket: SocketIOClient = SocketIOClient(socketURL: NSURL(string: "http://188.166.157.62:3000")! as URL)//, config: [.log(true)])
    var pendingEmits: [(event: String, param: String)] = []
    
    override init() {
        super.init()
    }
    
    func establishConnection() {
        socket.connect()
        socket.on("connect") {data, ack in
            for currEmit in self.pendingEmits {
                self.socket.emit(currEmit.event, currEmit.param)
            }
            self.pendingEmits.removeAll()
        }
        
        socket.on("update_chat") { ( dataArray, ack) -> Void in
            let responseString = dataArray[0] as! String
            print(responseString)
        }
    }
    
    func closeConnection() {
        socket.disconnect()
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
    
    func setRoomCreatedListener(completionHandler: @escaping (_ response: String) -> Void) {
        socket.off("room_created")
        socket.on("room_created") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            completionHandler(responseString)
        }
    }
    
    func createRoom(receiverId: String, userId: String) {
        socket.emit("create_room", receiverId, userId)
    }
    
    func addUser(roomId: String, userId: String) {
        socket.emit("add_user", roomId, userId)
    }
    
    func sendMessage(message: String) {
        socket.emit("send_chat", message)
    }
    
    func setRoomListener(room: String, completionHandler: @escaping (_ messageId: Int, _ username: String, _ message: String, _ timestamp: String) -> Void) {
        socket.on(room) { ( dataArray, ack) -> Void in
            
            let messageId = dataArray[0] as! Int
            let username = dataArray[1] as! String
            let message = dataArray[2] as! String
            let timestamp = dataArray[3] as! String
            completionHandler(messageId, username, message, timestamp)
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
}
