//
//  LogInModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/15/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

protocol LogInModelProtocol: class {
    func permissionReceived(_ permission: NSString)
}


class LogInModel: NSObject, URLSessionDataDelegate {
    
    //properties
    weak var delegate: LogInModelProtocol!
    
    func data_request(_ username: String, password: String) {
        
        let url: URL = URL(string: "http://188.166.157.62:3000/login")!
        let session = URLSession.shared
        
        var request = URLRequest(url:url)
        request.httpMethod = "POST"
        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
        
        let paramString = "username=\(username)&password=\(password)"
        request.httpBody = paramString.data(using: String.Encoding.utf8)
        
        let task = session.dataTask(with: request, completionHandler: {
            (
            data, response, error) in
            
            guard let _:Data = data, let _:URLResponse = response, error == nil else {
                print("error")
                return
            }
            
            let dataString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            
            DispatchQueue.main.async(execute: { () -> Void in
                
                self.delegate.permissionReceived(dataString!)
            })
            
        })
        
        task.resume()
        
    }
    
}
