//
//  RegisterViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/8.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class RegisterViewController: UIViewController, RegisterModelProtocol {

    @IBOutlet weak var fullNameTextField: UITextField!
    @IBOutlet weak var usernameTextField: UITextField!
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var phoneNoTextField: UITextField!
    @IBOutlet weak var pwdTextField: UITextField!
    @IBOutlet weak var confirmPwdTextField: UITextField!
    
    let registerModel = RegisterModel()
  
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        registerModel.delegate = self
    }

    override func viewWillAppear(_ animated: Bool) {
        
        // Adding the gesture recognizer that will dismiss the keyboard on an exterior tap
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIInputViewController.dismissKeyboard))
        view.addGestureRecognizer(tap)
    }
    
    @IBAction func registerButton(_ sender: Any) {
        fullNameTextField.resignFirstResponder()
        usernameTextField.resignFirstResponder()
        emailTextField.resignFirstResponder()
        phoneNoTextField.resignFirstResponder()
        pwdTextField.resignFirstResponder()
        confirmPwdTextField.resignFirstResponder()
        
        // Check for empty fields
        if fullNameTextField.text != nil && fullNameTextField.text != "" && usernameTextField.text != nil &&
            usernameTextField.text != "" && emailTextField.text != nil && emailTextField.text != "" &&
            phoneNoTextField.text != nil && phoneNoTextField.text != "" && pwdTextField.text != nil &&
            pwdTextField.text != "" && confirmPwdTextField.text != nil && confirmPwdTextField.text != "" {
            
            // Check if the two passwords match
            if pwdTextField.text != confirmPwdTextField.text {
                
                // Display alert message
                displayAlertMessage(mymessage: "Passwords do not match!")
            } else {
                
                // Call the request function from the Model component
                registerModel.data_request(fullNameTextField.text!, username: usernameTextField.text!, email: emailTextField.text!, phoneNo: phoneNoTextField.text!, pwd: pwdTextField.text!)
            }
        } else {
            
            // Display alert messaage
            displayAlertMessage(mymessage: "All fields are required!");
        }
    }
    
    // The function called at the arival of the response from the server
    func responseReceived(_ permission: NSString) {
        if permission == "success" {
            
            // Add the user details to the user defaults.
            let userDefaults = UserDefaults.standard;
            userDefaults.set(emailTextField.text, forKey:"email");
            userDefaults.set(usernameTextField.text, forKey:"username");
            userDefaults.set(phoneNoTextField.text, forKey:"phoneNo");
            userDefaults.set(pwdTextField.text, forKey:"password");
            userDefaults.set(fullNameTextField.text, forKey:"fullName");
            userDefaults.set(true, forKey: "hasLoginKey")
            userDefaults.synchronize();
            
            // Alert for success and view change on dismiss
            let myAlert = UIAlertController(title:"Success", message:"You are registered", preferredStyle:.alert);
            let okaction=UIAlertAction(title:"done", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in self.onSuccess()});
            myAlert.addAction(okaction);
            self.present(myAlert, animated:true, completion:nil);
            
        } else if permission == "duplicate" {
            
            // Alert for duplicate user error
            displayAlertMessage(mymessage: "User already exists")
        } else {
            
            // Alert for register error
            displayAlertMessage(mymessage: "There was a problem with the registration")
        }
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    @IBAction func backButtonPressed(_ sender: AnyObject) {
        let _ = navigationController?.popViewController(animated: true)
    }
    
    func onSuccess() {
        self.performSegue(withIdentifier: "registerTabBarViewController", sender: nil)
    }
    
    // Display alert message function
    func displayAlertMessage(mymessage:String) {
        let myAlert = UIAlertController(title:"Error", message:mymessage, preferredStyle:.alert);
        let okaction=UIAlertAction(title:"ok", style:UIAlertActionStyle.default, handler:nil);
        myAlert.addAction(okaction);
        self.present(myAlert, animated:true, completion:nil);
    }
}
