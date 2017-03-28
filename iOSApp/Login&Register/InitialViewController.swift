//
//  InitialViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/6.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class InitialViewController: UIViewController, LogInModelProtocol {

    let logInModel = LogInModel()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        logInModel.delegate = self
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        
        // If a user is already logged in, proceed to the messages view
        if UserDefaults.standard.bool(forKey: "hasLoginKey") == true {
            logInModel.data_request(UserDefaults.standard.value(forKey: "username")! as! String, password: UserDefaults.standard.value(forKey: "password")! as! String)
        } else {
            self.performSegue(withIdentifier: "loginView", sender: self)
        }
    }
    
    // The function called at the arival of the response from the server
    func userInfoReceived(_ userDetails: [String:Any]) {
        // Check if the user has been found
        if (userDetails["status"] as? String) != nil || (userDetails["status"] as? String) == "failed" {
            self.performSegue(withIdentifier: "loginView", sender: self)
        } else {
            // Ensure that none of the JSON values are nil through optional binding
            if let email = userDetails["email"] as? String,
                let username = userDetails["username"] as? String,
                let userId = userDetails["user_id"] as? Int,
                let phoneNo = userDetails["phone_number"] as? String,
                let fullName = userDetails["name"] as? String,
                let password = userDetails["password"] as? String
            {
                // Add the user details to the user defaults.
                let userDefaults = UserDefaults.standard;
                userDefaults.set(email, forKey:"email");
                userDefaults.set(userId, forKey:"userId");
                userDefaults.set(username, forKey:"username");
                userDefaults.set(phoneNo, forKey:"phoneNo");
                userDefaults.set(password, forKey:"password");
                userDefaults.set(fullName, forKey:"fullName");
                userDefaults.set(true, forKey: "hasLoginKey")
                
                if let about = userDetails["biography"] as? String {
                    userDefaults.set(about, forKey: "about")
                } else {
                    userDefaults.set("", forKey: "about")
                }
                
                // Download the profile picture, if it exists
                if let profilePicture = userDetails["profile_picture"] as? String {
                    userDefaults.set(profilePicture, forKey:"profilePicture");
                    if let url = URL(string: "http://188.166.157.62/profile_pictures/\(userDefaults.value(forKey: "profilePicture")!)") {
                        if let data = try? Data(contentsOf: url) {
                            var profileImg: UIImage
                            profileImg = UIImage(data: data)!
                            if let data = UIImagePNGRepresentation(profileImg) {
                                let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(userDefaults.value(forKey: "profilePicture"))")
                                try? data.write(to: filename)
                                userDefaults.set(true, forKey:"hasProfilePicture");
                            }
                        }
                    }
                }
                
                userDefaults.synchronize();
            }
            SocketIOManager.sharedInstance.establishConnection()
            self.performSegue(withIdentifier: "initialTabBarViewController", sender: nil)
        }
    }
}

