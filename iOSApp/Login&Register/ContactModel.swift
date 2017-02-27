//
//  ContactModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/27/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

class ContactModel: NSObject {
    
    //properties
    var username: String?
    var name: String?
    var request: Int?
    var email: String?
    var phoneNo: String?
    var userId: Int?
    var profilePicture: String?
    var contactId: Int?
    var timestamp: String?
    
    //empty constructor
    override init()
    {
        
    }
    
    //construct with @name, @email and @telephone parameters
    init(username: String, name: String, request: Int, email: String, phoneNo: String, userId: Int, profilePicture: String, contactId: Int, timestamp: String) {
        
        self.username = username
        self.name = name
        self.request = request
        self.email = email
        self.phoneNo = phoneNo
        self.userId = userId
        self.profilePicture = profilePicture
        self.contactId = contactId
        self.timestamp = timestamp
        
    }
    
    
    //prints object's current state
    override var description: String {
        return "Username: \(username), Name: \(name), Email: \(email), PhoneNo: \(phoneNo), UserId: \(userId), ProfilePicture: \(profilePicture), ContactId: \(contactId), Timestamp: \(timestamp)"
        
    }
    
    
}
