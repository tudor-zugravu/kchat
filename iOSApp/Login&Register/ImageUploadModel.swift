//
//  ImageUploadModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 26/03/2017.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

protocol ImageUploadModelProtocol: class {
    func imageUploaded(_ response: NSString)
}

class ImageUploadModel: NSObject, URLSessionDataDelegate {
    
    //properties
    weak var delegate: ImageUploadModelProtocol!
    
    // Server request function for validating log in credentials
    func uploadImage(id: String, base64String: String, type: String) {
        
        // Setting up the server session with the URL and the request
        let url: URL = URL(string: "http://188.166.157.62:3000/iOSImageUpload")!
        let session = URLSession.shared
        var request = URLRequest(url:url)
        request.httpMethod = "POST"
        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
        
        // Request parameters
        let paramString = "photoType=\(type)&image=\(base64String.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)!)&sender=\(id)"
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
            DispatchQueue.main.async(execute: { () -> Void in
                self.delegate.imageUploaded(dataString!)
            })
        })
        task.resume()
    }
}
