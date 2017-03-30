//
//  ChangePasswordModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/19/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

protocol ChangePasswordModelProtocol: class {
    func responseReceived(_ response: NSString)
}


class ChangePasswordModel: NSObject, URLSessionDataDelegate {
    
    //properties
    weak var delegate: ChangePasswordModelProtocol!
    
    // Server request function for validating log in credentials
    func data_request(_ username: String, password: String, newPassword: String) {
        
        // Setting up the server session with the URL and the request
        let url: URL = URL(string: "http://188.166.157.62:3000/changePass")!
        let session = URLSession.shared
        var request = URLRequest(url:url)
        request.httpMethod = "POST"
        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
        
        // Request parameters
        let paramString = "username=\(username)&password=\(password)&newPassword=\(newPassword)"
        request.httpBody = paramString.data(using: String.Encoding.utf8)
        
        let task = session.dataTask(with: request, completionHandler: {
            (data, response, error) in
            
            // Check for request errors
            guard let _:Data = data, let _:URLResponse = response, error == nil else {
                print("error")
                return
            }
            
            // Calling the success handler asynchroniously
            let dataString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            DispatchQueue.main.async(execute: { () -> Void in
                self.delegate.responseReceived(dataString!)
            })
        })
        task.resume()
    }
}
