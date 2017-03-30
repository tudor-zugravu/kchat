//
//  StoredMessagesModel.swift
//  Login&Register
//
//  Created by 骧小爷 on 29/03/2017.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

class StoredMessagesModel: NSObject, NSCoding {
    
    //properties
    var sender: String?
    var receiver: Int?
    var message: String?
    var messageType: String?
    
    //empty constructor
    override init()
    {
        
    }
    
    //construct with @name, @email and @telephone parameters
    init(sender: String, receiver: Int, message: String, messageType: String) {
        
        self.sender = sender
        self.receiver = receiver
        self.message = message
        self.messageType = messageType
    }
    
    required init(coder decoder: NSCoder) {
        self.sender = decoder.decodeObject(forKey: "sender") as? String ?? ""
        self.receiver = decoder.decodeObject(forKey: "receiver") as? Int
        self.message = decoder.decodeObject(forKey: "message") as? String ?? ""
        self.messageType = decoder.decodeObject(forKey: "messageType") as? String ?? ""
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(sender, forKey: "sender")
        coder.encode(receiver, forKey: "receiver")
        coder.encode(message, forKey: "message")
        coder.encode(messageType, forKey: "messageType")
    }
    
    //prints object's current state
    override var description: String {
        return "Sender: \(sender), Receiver: \(receiver), Message: \(message), MessageType: \(messageType)"
    }
    
}
