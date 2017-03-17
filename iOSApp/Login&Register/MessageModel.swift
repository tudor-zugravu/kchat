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
    var sent: Bool?
    var message: String?

    //empty constructor
    override init()
    {
        
    }

    //construct with @name, @email and @telephone parameters
    init(sent: Bool, message: String) {
        
        self.sent = sent
        self.message = message
    }

    //prints object's current state
    override var description: String {
        return "Sent: \(sent), Message: \(message)"
    }

}
