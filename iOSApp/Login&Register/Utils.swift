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
    
    fileprivate override init() {

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
        userDefaults.removeObject(forKey: "storedMessages")
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
        if (isReachable && !needsConnection) == true {
            self.flushBuffer()
        }
        return (isReachable && !needsConnection)
    }
    // (Source: http://stackoverflow.com/questions/39558868/check-internet-connection-ios-10 retrieved on 28.03.2017)
    // Used to check internet connection on iOS
    
    func flushBuffer() {
        if (UserDefaults.standard.value(forKey: "storedMessages") != nil) {
            if let data = UserDefaults.standard.data(forKey: "storedMessages"),
                let storedMessagesAux = NSKeyedUnarchiver.unarchiveObject(with: data) as? [StoredMessagesModel] {
                print(storedMessagesAux.map({ ["sender": $0.sender!, "receiver": $0.receiver!, "message": $0.message!, "messageType": $0.messageType!] }))
                let JSON = try? JSONSerialization.data(withJSONObject: storedMessagesAux.map({ ["sender": $0.sender!, "receiver": $0.receiver!, "message": $0.message!, "messageType": $0.messageType!] }), options: [])
                
                do {
                    let parsedData = try JSONSerialization.jsonObject(with: JSON!, options: [])
                    print(parsedData)
                
                } catch let error as NSError {
                    print(error)
                }
                if let JSONString = String(data: JSON!, encoding: String.Encoding.utf8) {
                    // Setting up the server session with the URL and the request
                    let url: URL = URL(string: "http://188.166.157.62:3000/bufferUpload")!
                    let session = URLSession.shared
                    var request = URLRequest(url:url)
                    request.httpMethod = "POST"
                    request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
                    
                    // Request parameters
                    let paramString = "messages=\(JSONString)"
                    request.httpBody = paramString.data(using: String.Encoding.utf8)
                    
                    let task = session.dataTask(with: request, completionHandler: {
                        (data, response, error) in
                        
                        // Check for request errors
                        guard let _:Data = data, let _:URLResponse = response, error == nil else {
                            print("error")
                            print(error)
                            return
                        }
                        
                        // Calling the success handler asynchroniously
                        let dataString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
                        print(dataString)
                    })
                    task.resume()
                }
                
                
                UserDefaults.standard.removeObject(forKey: "storedMessages")
            }
        }
    }
    
}
