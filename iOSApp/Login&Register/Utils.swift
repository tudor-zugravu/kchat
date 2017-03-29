//
//  Utils.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/19/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation
import SystemConfiguration

private let _instance = Utils()

class Utils: NSObject {
    
//    var newPrivateMessages: Int
//    var newGroupMessages: Int
//    var newContactRequests: Int
    
    fileprivate override init() {
//        newPrivateMessages = 0
//        newGroupMessages = 0
//        newContactRequests = 0
    }
    
    class var instance: Utils {
        return _instance
    }
    
    // Function that returns the path of the images
    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .allDomainsMask)
        let documentsDirectory = paths[0]
        return documentsDirectory
    }
    
//    func setTabBarValues(tabBarController: TabBarController) {
//        if (self.newPrivateMessages > 0) {
//            tabBarController.tabBar.items?.first?.badgeValue = String(self.newPrivateMessages)
//        } else {
//            tabBarController.tabBar.items?.first?.badgeValue = nil
//        }
//        if (self.newGroupMessages > 0) {
//            tabBarController.tabBar.items?[1].badgeValue = String(self.newGroupMessages)
//        } else {
//            tabBarController.tabBar.items?[1].badgeValue = nil
//        }
//        if (self.newContactRequests > 0) {
//            tabBarController.tabBar.items?[2].badgeValue = String(self.newContactRequests)
//        } else {
//            tabBarController.tabBar.items?[2].badgeValue = nil
//        }
//        tabBarController.reloadInputViews()
//    }
    
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
        userDefaults.removeObject(forKey: "contacts")
        userDefaults.removeObject(forKey: "privateChats")
        userDefaults.removeObject(forKey: "privateMessages")
        userDefaults.removeObject(forKey: "grupChats")
        userDefaults.removeObject(forKey: "groupMessages")
        userDefaults.set(false, forKey: "hasLoginKey")
        userDefaults.set(false, forKey: "hasProfilePicture")
        UserDefaults.standard.synchronize()
        
        SocketIOManager.sharedInstance.closeConnection()
    }
    
    func deleteCachedPicture(image: String) {
        do {
            let fileManager = FileManager.default
            let paths = FileManager.default.urls(for: .cachesDirectory, in: .allDomainsMask)
            let documentsDirectory = paths[0]

            let fileName = documentsDirectory.appendingPathComponent("\(image)").path
            
            if fileManager.fileExists(atPath: fileName) {
                try fileManager.removeItem(atPath: fileName)
            } else {
                print("File does not exist")
            }
        } catch let error as NSError {
            print("An error took place: \(error)")
        }

    }
    
    // Function that returns the internet connection status
    func isInternetAvailable() -> Bool
    {
        var zeroAddress = sockaddr_in()
        zeroAddress.sin_len = UInt8(MemoryLayout.size(ofValue: zeroAddress))
        zeroAddress.sin_family = sa_family_t(AF_INET)
        
        let defaultRouteReachability = withUnsafePointer(to: &zeroAddress) {
            $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {zeroSockAddress in
                SCNetworkReachabilityCreateWithAddress(nil, zeroSockAddress)
            }
        }
        
        var flags = SCNetworkReachabilityFlags()
        if !SCNetworkReachabilityGetFlags(defaultRouteReachability!, &flags) {
            return false
        }
        let isReachable = (flags.rawValue & UInt32(kSCNetworkFlagsReachable)) != 0
        let needsConnection = (flags.rawValue & UInt32(kSCNetworkFlagsConnectionRequired)) != 0
        return (isReachable && !needsConnection)
    }
    
}
