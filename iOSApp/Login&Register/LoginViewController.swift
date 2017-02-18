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
        
//        UserDefaults.standard.set(false, forKey: "hasLoginKey")

        // If a user is already logged in, proceed to the messages view
        if UserDefaults.standard.bool(forKey: "hasLoginKey") {
            performSegue(withIdentifier: "showTabBarViewController", sender: nil)
        }
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
        
        print ("success \(userDetails)")
        
        // Check if the user has been found
        if (userDetails["status"] as? String) != nil {
            let alertView = UIAlertController(title: "Login Failed",
                                              message: "Invalid username or password" as String, preferredStyle:.alert)
            let okAction = UIAlertAction(title: "Try again", style: .default, handler: nil)
            alertView.addAction(okAction)
            self.present(alertView, animated: true, completion: nil)
        } else {
            // Add the user details to the user defaults.
            let userDefaults = UserDefaults.standard;
            userDefaults.set(userDetails["email"] as? String, forKey:"email");
            userDefaults.set(userDetails["username"] as? String, forKey:"username");
            userDefaults.set(userDetails["phone_number"] as? String, forKey:"phoneNo");
            userDefaults.set(userDetails["password"] as? String, forKey:"password");
            userDefaults.set(userDetails["name"] as? String, forKey:"fullName");
            userDefaults.set(true, forKey: "hasLoginKey")
            userDefaults.synchronize();
            performSegue(withIdentifier: "showTabBarViewController", sender: nil)
        }
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
}
