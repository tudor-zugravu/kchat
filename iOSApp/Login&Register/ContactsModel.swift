//
//  ContactsModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/21/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

protocol ContactsModelProtocol: class {
    func contactsDownloaded(_ contactDetails: [[String:Any]])
}


class ContactsModel: NSObject, URLSessionDataDelegate {
    
    //properties
    weak var delegate: ContactsModelProtocol!
    var data : NSMutableData = NSMutableData()
    
    // Server request function for validating log in credentials
    func downloadContacts() {
        
        self.data = NSMutableData()
        
        // Setting up the server session with the URL and the request
        let url: URL = URL(string: "http://188.166.157.62:4000/contacts")!
        let session = URLSession.shared
        var request = URLRequest(url:url)
        request.httpMethod = "POST"
        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
        
        if let id = UserDefaults.standard.value(forKey: "userId") {
            
            // Request parameters
            let paramString = "userId=\(id)"
            request.httpBody = paramString.data(using: String.Encoding.utf8)
            
            let task = session.dataTask(with: request, completionHandler: {
                (data, response, error) in
                
                // Check for request errors
                guard let _:Data = data, let _:URLResponse = response, error == nil else {
                    print("error")
                    return
                }
                
                do {
                    // Sending the received JSON
                    let parsedData = try JSONSerialization.jsonObject(with: data!, options: []) as! [[String:Any]]
                    DispatchQueue.main.async(execute: { () -> Void in
                        
                        // Calling the success handler asynchroniously
                        self.delegate.contactsDownloaded(parsedData)
                    })
                    
                } catch let error as NSError {
                    print(error)
                }
            })
            task.resume()
        }
    }
}
