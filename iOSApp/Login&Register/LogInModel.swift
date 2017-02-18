//
//  LogInModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/15/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

protocol LogInModelProtocol: class {
    func userInfoReceived(_ userDetails: [String:Any])
}


class LogInModel: NSObject, URLSessionDataDelegate {
    
    //properties
    weak var delegate: LogInModelProtocol!
    var data : NSMutableData = NSMutableData()
    
    // Server request function for validating log in credentials
    func data_request(_ username: String, password: String) {
        
        self.data = NSMutableData()
        
        // Setting up the server session with the URL and the request
        let url: URL = URL(string: "http://188.166.157.62:3000/login")!
        let session = URLSession.shared
        var request = URLRequest(url:url)
        request.httpMethod = "POST"
        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
        
        // Request parameters
        let paramString = "username=\(username)&password=\(password)"
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
                let parsedData = try JSONSerialization.jsonObject(with: data!, options: []) as! [String:Any]
                DispatchQueue.main.async(execute: { () -> Void in
                    
                    // Calling the success handler asynchroniously
                    self.delegate.userInfoReceived(parsedData)
                })
    
            } catch let error as NSError {
                print(error)
            }
        })
        task.resume()
    }
}
