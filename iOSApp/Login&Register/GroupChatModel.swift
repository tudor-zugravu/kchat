//
//  GroupChatModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 18/03/2017.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class GroupChatModel: NSObject, NSCoding {
    
    //properties
    var groupId: Int?
    var groupName: String?
    var groupDescription: String?
    var lastMessage: String?
    var timestamp: String?
    var groupPicture: String?
    var owner: Int?
    
    //empty constructor
    override init()
    {
        
    }
    
    //construct with @name, @email and @telephone parameters
    init(groupId: Int, groupName: String, groupDescription: String, lastMessage: String, timestamp: String, groupPicture: String, owner: Int) {
        
        self.groupId = groupId
        self.groupName = groupName
        self.groupDescription = groupDescription
        self.lastMessage = lastMessage
        self.timestamp = timestamp
        self.groupPicture = groupPicture
        self.owner = owner
    }
    
    required init(coder decoder: NSCoder) {
        self.groupId = decoder.decodeObject(forKey: "groupId") as? Int
        self.groupName = decoder.decodeObject(forKey: "groupName") as? String ?? ""
        self.groupDescription = decoder.decodeObject(forKey: "groupDescription") as? String ?? ""
        self.lastMessage = decoder.decodeObject(forKey: "lastMessage") as? String ?? ""
        self.timestamp = decoder.decodeObject(forKey: "timestamp") as? String ?? ""
        self.groupPicture = decoder.decodeObject(forKey: "groupPicture") as? String ?? ""
        self.owner = decoder.decodeObject(forKey: "owner") as? Int
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(groupId, forKey: "groupId")
        coder.encode(groupName, forKey: "groupName")
        coder.encode(groupDescription, forKey: "groupDescription")
        coder.encode(lastMessage, forKey: "lastMessage")
        coder.encode(timestamp, forKey: "timestamp")
        coder.encode(groupPicture, forKey: "groupPicture")
        coder.encode(owner, forKey: "owner")
    }
    
    //prints object's current state
    override var description: String {
        return "Group Id: \(groupId), Group Name: \(groupName), Description: \(groupDescription), Last Message: \(lastMessage), Timestamp: \(timestamp), Group Picture: \(groupPicture), Owner: \(owner)"
    }
    
    
}
