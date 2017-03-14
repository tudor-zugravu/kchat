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
    
    var socket: SocketIOClient = SocketIOClient(socketURL: NSURL(string: "http://188.166.157.62:3000")! as URL)
    
    override init() {
        super.init()
    }
    
    func establishConnection() {
        socket.connect()
    }
    
    
    func closeConnection() {
        socket.disconnect()
    }
    
    func predictSearch(username: String, userId: String, completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        
        socket.emit("search_user_filter", username, userId)
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
    
    func addContact(userId: String, receiverId: String, completionHandler: @escaping (_ status: Bool) -> Void) {
        
        socket.emit("send_contact_request", userId, receiverId)
        socket.on("sent_request") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler(false)
            } else {
                completionHandler(true)
            }
        }
    }
    
    func getSentRequests(userId: String, completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        
        socket.emit("sent_contact_requests", userId)
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
    
    func deleteRequest(userId: String, receiverId: String, completionHandler: @escaping (_ status: Bool) -> Void) {
        
        socket.emit("delete_contact_request", userId, receiverId)
        socket.on("request_deleted") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler(false)
            } else {
                completionHandler(true)
            }
        }
    }
    
    func getReceivedRequests(userId: String, completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        
        socket.emit("received_contact_requests", userId)
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

    func acceptContact(userId: String, receiverId: String, completionHandler: @escaping (_ status: Bool) -> Void) {
        
        socket.emit("accept_contact_request", receiverId, userId)
        socket.on("request_accepted") { ( dataArray, ack) -> Void in
            
            let responseString = dataArray[0] as! String
            if responseString == "fail" {
                completionHandler(false)
            } else {
                completionHandler(true)
            }
        }
    }
    
    func getChats(userId: String, completionHandler: @escaping (_ userList: [[String: Any]]?) -> Void) {
        
        socket.emit("get_chats", userId)
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
}
