//
//  MessageModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/15/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

class MessageModel: NSObject {
    
    //properties
    var messageId: Int?
    var sent: Bool?
    var message: String?
    var timestamp: String?

    //empty constructor
    override init()
    {
        
    }

    //construct with @name, @email and @telephone parameters
    init(messageId: Int, senderId: String, message: String, timestamp: String) {
        
        self.messageId = messageId
        
        if senderId == String(describing: UserDefaults.standard.value(forKey: "userId")!) {
            self.sent = true
        } else {
            self.sent = false
        }

        self.message = message
        
        let separators = CharacterSet(charactersIn: " ")
        self.timestamp = timestamp.components(separatedBy: separators)[1]
    }

    //prints object's current state
    override var description: String {
        return "Message Id: \(messageId), Sent: \(sent), Message: \(message), Timestamp: \(timestamp)"
    }

}
