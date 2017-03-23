//
//  SelectedContactModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 23/03/2017.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

class SelectedContactModel: NSObject {
    
    //properties
    var name: String?
    var email: String?
    var userId: Int?
    var profilePicture: String?
    var contactId: Int?
    var selected: Bool?
    
    //empty constructor
    override init()
    {
        
    }
    
    //construct with @name, @email and @telephone parameters
    init(name: String, email: String, userId: Int, profilePicture: String, contactId: Int, selected: Bool) {
        
        self.name = name
        self.email = email
        self.userId = userId
        self.profilePicture = profilePicture
        self.contactId = contactId
        self.selected = selected
    }
    
    
    //prints object's current state
    override var description: String {
        return "Name: \(name), Email: \(email), UserId: \(userId), ProfilePicture: \(profilePicture), ContactId: \(contactId), Selected: \(selected)"
    }
    
    
}
