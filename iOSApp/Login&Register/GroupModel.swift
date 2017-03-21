//
//  GroupModel.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/3/17.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import Foundation

protocol GroupModelModelProtocol: class {
    func responseReceived(_ response: NSString)
}

class GroupModel: NSObject, URLSessionDataDelegate {
    
    weak var delegate: GroupModelModelProtocol!
    
    func data_request(_ groupname: String) {
        
        // Setting up the server session with the URL and the request
        let url: URL = URL(string: "http://188.166.157.62:4000/changePass")!
        let session = URLSession.shared
        var request = URLRequest(url:url)
        request.httpMethod = "POST"
        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
        
        // Request parameters
        let paramString = "groupname=\(groupname)"
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
