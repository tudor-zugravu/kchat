//
//  LoginViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/9.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {

    @IBOutlet weak var userNameTextField: UITextField!
    @IBOutlet weak var userPwdTextField: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    @IBAction func loginPressed(_ sender: Any) {
        let userName=userNameTextField.text;
        let userPwd=userPwdTextField.text;
        
        
        
//        if UserDefaults.standard.object(forKey: "userName") != nil && UserDefaults.standard.object(forKey: "userPwd") != nil {
//            let userNameDefault:String=UserDefaults.standard.object(forKey: "userName") as! String;
//            let userPwdDefault=UserDefaults.standard.object(forKey: "userPwd") as! String;
//            
//            if userNameDefault == userName{
//                if userPwdDefault == userPwd{
//                    //login is successful (the following is just use default to store it)
//                    UserDefaults.standard.set(true, forKey: "isUserLogin");
//                    UserDefaults.standard.synchronize()
//                    //self.dismiss(animated: true, completion: nil)
//                    
//                    // Alert for success
//                    let myAlert = UIAlertController(title:"Success!", message:"Logged in", preferredStyle:.alert);
//                    let okaction=UIAlertAction(title:"ok", style:UIAlertActionStyle.default, handler:nil);
//                    myAlert.addAction(okaction);
//                    self.present(myAlert, animated:true, completion:nil);
//                } else {
//                    // Alert for wrong password
//                    let myAlert = UIAlertController(title:"Error!", message:"Wrong password", preferredStyle:.alert);
//                    let okaction=UIAlertAction(title:"ok", style:UIAlertActionStyle.default, handler:nil);
//                    myAlert.addAction(okaction);
//                    self.present(myAlert, animated:true, completion:nil);
//                }
//            } else {
//                // Alert for wrong username
//                let myAlert = UIAlertController(title:"Error!", message:"Wrong username", preferredStyle:.alert);
//                let okaction=UIAlertAction(title:"ok", style:UIAlertActionStyle.default, handler:nil);
//                myAlert.addAction(okaction);
//                self.present(myAlert, animated:true, completion:nil);
//            }
//        } else {
//            // Alert for no default values
//            let myAlert = UIAlertController(title:"Error!", message:"No default values", preferredStyle:.alert);
//            let okaction=UIAlertAction(title:"ok", style:UIAlertActionStyle.default, handler:nil);
//            myAlert.addAction(okaction);
//            self.present(myAlert, animated:true, completion:nil);
//        }
        
        let url: URL = URL(string: "http://188.166.157.62:3000/users")!
        let session = URLSession.shared
        
        var request = URLRequest(url:url)
        request.httpMethod = "GET"
        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
        
//        let paramString = "email=\(email)&password=\(password)"
//        request.httpBody = paramString.data(using: String.Encoding.utf8)
        
        let task = session.dataTask(with: request, completionHandler: {
            (
            data, response, error) in
            
            guard let _:Data = data, let _:URLResponse = response, error == nil else {
                print("error")
                return
            }
            
            let dataString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            
            DispatchQueue.main.async(execute: { () -> Void in
                
                print(dataString!)
            })
            
        })
        
        task.resume()
        
        
    }
}
