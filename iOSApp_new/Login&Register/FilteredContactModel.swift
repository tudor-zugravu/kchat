//
//  FilteredContactModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/11/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

class FilteredContactModel: NSObject {
    
    //properties
    var username: String?
    var name: String?
    var userId: Int?
    
    //empty constructor
    override init()
    {
        
    }
    
    //construct with @name, @email and @telephone parameters
    init(username: String, name: String, userId: Int) {
        self.username = username
        self.name = name
        self.userId = userId
    }
    
    
    //prints object's current state
    override var description: String {
        return "Username: \(username), Name: \(name), UserId: \(userId)"
    }
    
    
}
