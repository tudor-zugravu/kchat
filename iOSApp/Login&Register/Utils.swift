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
    
    func setTabBarValues(tabBarController: TabBarController) {
        if (self.newPrivateMessages > 0) {
            tabBarController.tabBar.items?.first?.badgeValue = String(self.newPrivateMessages)
        } else {
            tabBarController.tabBar.items?.first?.badgeValue = nil
        }
        if (self.newGroupMessages > 0) {
            tabBarController.tabBar.items?[1].badgeValue = String(self.newGroupMessages)
        } else {
            tabBarController.tabBar.items?[1].badgeValue = nil
        }
        if (self.newContactRequests > 0) {
            tabBarController.tabBar.items?[2].badgeValue = String(self.newContactRequests)
        } else {
            tabBarController.tabBar.items?[2].badgeValue = nil
        }
        tabBarController.reloadInputViews()
        print("\(tabBarController.tabBar.items?.first?.badgeValue) \(tabBarController.tabBar.items?[1].badgeValue) \(tabBarController.tabBar.items?[2].badgeValue)")
    }
    
    func logOut() {
        // Delete profile picture
        do {
            let fileManager = FileManager.default
            let fileName = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(UserDefaults.standard.value(forKey: "profilePicture"))").path
            
            if fileManager.fileExists(atPath: fileName) {
                try fileManager.removeItem(atPath: fileName)
            } else {
                print("File does not exist")
            }
        }
        catch let error as NSError {
            print("An error took place: \(error)")
        }
        
        // Delete stored user data
        let userDefaults = UserDefaults.standard;
        userDefaults.removeObject(forKey: "email")
        userDefaults.removeObject(forKey:"userId")
        userDefaults.removeObject(forKey: "username")
        userDefaults.removeObject(forKey: "phoneNo")
        userDefaults.removeObject(forKey: "password")
        userDefaults.removeObject(forKey: "fullName")
        userDefaults.removeObject(forKey: "profilePicture")
        userDefaults.removeObject(forKey: "about")
        userDefaults.set(false, forKey: "hasLoginKey")
        userDefaults.set(false, forKey: "hasProfilePicture")
        UserDefaults.standard.synchronize()
        
        SocketIOManager.sharedInstance.closeConnection()
    }
    
}
