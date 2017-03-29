//
//  ChatModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/14/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ChatModel: NSObject, NSCoding {
    
    //properties
    var receiverId: Int?
    var receiverName: String?
    var senderName: String?
    var lastMessage: String?
    var timestamp: String?
    var profilePicture: String?
    
    //empty constructor
    override init()
    {
        
    }
    
    //construct with @name, @email and @telephone parameters
    init(receiverId: Int, receiverName: String, senderName: String, lastMessage: String, timestamp: String, profilePicture: String) {
        
        self.receiverId = receiverId
        self.receiverName = receiverName
        self.senderName = senderName
        self.lastMessage = lastMessage
        self.timestamp = timestamp
        self.profilePicture = profilePicture
    }
    
    required init(coder decoder: NSCoder) {
        self.receiverId = decoder.decodeObject(forKey: "receiverId") as? Int
        self.receiverName = decoder.decodeObject(forKey: "receiverName") as? String ?? ""
        self.senderName = decoder.decodeObject(forKey: "senderName") as? String ?? ""
        self.lastMessage = decoder.decodeObject(forKey: "lastMessage") as? String ?? ""
        self.timestamp = decoder.decodeObject(forKey: "timestamp") as? String ?? ""
        self.profilePicture = decoder.decodeObject(forKey: "profilePicture") as? String ?? ""
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(receiverId, forKey: "receiverId")
        coder.encode(receiverName, forKey: "receiverName")
        coder.encode(senderName, forKey: "senderName")
        coder.encode(lastMessage, forKey: "lastMessage")
        coder.encode(timestamp, forKey: "timestamp")
        coder.encode(profilePicture, forKey: "profilePicture")
    }
    
    //prints object's current state
    override var description: String {
        return "Receiver Id: \(receiverId), Receiver Name: \(receiverName), Sender Name: \(senderName), Last Message: \(lastMessage), Timestamp: \(timestamp), Profile Picture: \(profilePicture)"
    }
    
    
}
