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
    
    func permissionReceived(_ permission: NSString) {
        if permission == "success" {
            performSegue(withIdentifier: "showTabBarViewController", sender: nil)
        } else {
            let alertView = UIAlertController(title: "Login Failed",
                                              message: "Wrong username or password." as String, preferredStyle:.alert)
            let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
            alertView.addAction(okAction)
            self.present(alertView, animated: true, completion: nil)
        }
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
}
