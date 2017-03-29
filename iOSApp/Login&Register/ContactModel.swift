//
//  ContactModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/27/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

class ContactModel: NSObject, NSCoding {
    
    //properties
    var username: String?
    var name: String?
    var email: String?
    var phoneNo: String?
    var userId: Int?
    var profilePicture: String?
    var about: String?
    var contactId: Int?
    var timestamp: String?
    
    //empty constructor
    override init()
    {
        
    }
    
    //construct with @name, @email and @telephone parameters
    init(username: String, name: String, email: String, phoneNo: String, userId: Int, profilePicture: String, about: String, contactId: Int, timestamp: String) {

        self.username = username
        self.name = name
        self.email = email
        self.phoneNo = phoneNo
        self.userId = userId
        self.profilePicture = profilePicture
        self.about = about
        self.contactId = contactId
        self.timestamp = timestamp
    }
    
    required init(coder decoder: NSCoder) {
        self.username = decoder.decodeObject(forKey: "username") as? String ?? ""
        self.name = decoder.decodeObject(forKey: "name") as? String ?? ""
        self.email = decoder.decodeObject(forKey: "email") as? String ?? ""
        self.phoneNo = decoder.decodeObject(forKey: "phoneNo") as? String ?? ""
        self.userId = decoder.decodeObject(forKey: "userId") as? Int
        self.profilePicture = decoder.decodeObject(forKey: "profilePicture") as? String ?? ""
        self.about = decoder.decodeObject(forKey: "about") as? String ?? ""
        self.contactId = decoder.decodeObject(forKey: "contactId") as? Int
        self.timestamp = decoder.decodeObject(forKey: "contactId") as? String ?? ""
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(username, forKey: "username")
        coder.encode(name, forKey: "name")
        coder.encode(email, forKey: "email")
        coder.encode(phoneNo, forKey: "phoneNo")
        coder.encode(userId, forKey: "userId")
        coder.encode(profilePicture, forKey: "profilePicture")
        coder.encode(about, forKey: "about")
        coder.encode(contactId, forKey: "contactId")
        coder.encode(timestamp, forKey: "timestamp")
    }
    
    
    //prints object's current state
    override var description: String {
        return "Username: \(username), Name: \(name), Email: \(email), PhoneNo: \(phoneNo), UserId: \(userId), ProfilePicture: \(profilePicture), About: \(about), ContactId: \(contactId), Timestamp: \(timestamp)"
    }
    
    
}
