//
//  GroupMessageModel.swift
//  Login&Register
//
//  Created by 骧小爷 on 28/03/2017.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

class GroupMessageModel: NSObject, NSCoding {
    
    //properties
    var messageId: Int?
    var senderId: String?
    var message: String?
    var timestamp: String?
    var profilePicture: String?
    var name: String?
    
    //empty constructor
    override init()
    {
        
    }
    
    //construct with @name, @email and @telephone parameters
    init(messageId: Int, senderId: String, message: String, timestamp: String, profilePicture: String, name: String) {
        
        self.messageId = messageId
        self.senderId = senderId
        self.message = message
        self.profilePicture = profilePicture
        self.name = name
        
        let separators = CharacterSet(charactersIn: " -:")
        let timeParts = timestamp.components(separatedBy: separators)
        self.timestamp = "\(timeParts[0])-\(timeParts[1]) \(timeParts[3]):\(timeParts[4])"
    }
    
    required init(coder decoder: NSCoder) {
        self.messageId = decoder.decodeObject(forKey: "messageId") as? Int
        self.senderId = decoder.decodeObject(forKey: "senderId") as? String ?? ""
        self.message = decoder.decodeObject(forKey: "message") as? String ?? ""
        self.timestamp = decoder.decodeObject(forKey: "timestamp") as? String ?? ""
        self.profilePicture = decoder.decodeObject(forKey: "profilePicture") as? String ?? ""
        self.name = decoder.decodeObject(forKey: "name") as? String ?? ""
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(messageId, forKey: "messageId")
        coder.encode(senderId, forKey: "senderId")
        coder.encode(message, forKey: "message")
        coder.encode(timestamp, forKey: "timestamp")
        coder.encode(profilePicture, forKey: "profilePicture")
        coder.encode(name, forKey: "name")
    }
    
    //prints object's current state
    override var description: String {
        return "Message Id: \(messageId), senderId: \(senderId), Message: \(message), Timestamp: \(timestamp), pp: \(profilePicture), name: \(name)"
    }
    
}
