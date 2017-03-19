//
//  Utils.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/19/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

private let _instance = Utils()

class Utils: NSObject {
    
    var newPrivateMessages: Int
    var newGroupMessages: Int
    var newContactRequests: Int
    
    fileprivate override init() {
        newPrivateMessages = 0
        newGroupMessages = 0
        newContactRequests = 0
    }
    
    class var instance: Utils {
        return _instance
    }
    
    // Function that returns the path of the images
    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let documentsDirectory = paths[0]
        return documentsDirectory
    }
    
}
