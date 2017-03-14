//
//  ChatModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/14/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ChatModel: NSObject {
    
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
    
    
    //prints object's current state
    override var description: String {
        return "Receiver Id: \(receiverId), Receiver Name: \(receiverName), Sender Name: \(senderName), Last Message: \(lastMessage), Timestamp: \(timestamp), Profile Picture: \(profilePicture)"
    }
    
    
}
