//
//  LoginViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/9.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController, LogInModelProtocol {

    @IBOutlet weak var userNameTextField: UITextField!
    @IBOutlet weak var userPwdTextField: UITextField!
    
    let logInModel = LogInModel()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        logInModel.delegate = self
    }

    override func viewWillAppear(_ animated: Bool) {
        
        // Adding the gesture recognizer that will dismiss the keyboard on an exterior tap
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIInputViewController.dismissKeyboard))
        view.addGestureRecognizer(tap)
    }
    
    @IBAction func loginPressed(_ sender: Any) {
        
        userNameTextField.resignFirstResponder()
        userPwdTextField.resignFirstResponder()
        
        if userNameTextField.text != nil && userNameTextField.text != "" && userPwdTextField.text != nil && userPwdTextField.text != "" {
            logInModel.data_request(userNameTextField.text!, password: userPwdTextField.text!)
        } else {
            let alertView = UIAlertController(title: "Login Failed",
                                              message: "Wrong username or password." as String, preferredStyle:.alert)
            let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
            alertView.addAction(okAction)
            self.present(alertView, animated: true, completion: nil)
        }
    }
    
    // The function called at the arival of the response from the server
    func userInfoReceived(_ userDetails: [String:Any]) {
        
        // Check if the user has been found
        if (userDetails["status"] as? String) != nil {
            let alertView = UIAlertController(title: "Login Failed",
                                              message: "Invalid username or password" as String, preferredStyle:.alert)
            let okAction = UIAlertAction(title: "Try again", style: .default, handler: nil)
            alertView.addAction(okAction)
            self.present(alertView, animated: true, completion: nil)
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
            performSegue(withIdentifier: "loginTabBarViewController", sender: nil)
        }
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
}
