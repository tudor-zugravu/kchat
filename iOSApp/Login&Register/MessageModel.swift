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
    var senderId: String?
    var message: String?
    var timestamp: String?

    //empty constructor
    override init()
    {
        
    }

    //construct with @name, @email and @telephone parameters
    init(messageId: Int, senderId: String, message: String, timestamp: String) {
        
        self.messageId = messageId
        self.senderId = senderId
        self.message = message
        
        let separators = CharacterSet(charactersIn: " -:")
        print("\(timestamp.components(separatedBy: separators)[0]) \(timestamp.components(separatedBy: separators)[1]) \(timestamp.components(separatedBy: separators)[2]) \(timestamp.components(separatedBy: separators)[3]) \(timestamp.components(separatedBy: separators)[4]) \(timestamp.components(separatedBy: separators)[5])")
        self.timestamp = timestamp.components(separatedBy: separators)[1]
    }

    //prints object's current state
    override var description: String {
        return "Message Id: \(messageId), senderId: \(senderId), Message: \(message), Timestamp: \(timestamp)"
    }

}
