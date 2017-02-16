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
    
    func responseReceived(_ permission: NSString) {
        if permission == "success" {
            
            // Alert for success and view change on dismiss
            let myAlert = UIAlertController(title:"Success", message:"You are registered", preferredStyle:.alert);
            let okaction=UIAlertAction(title:"done", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in self.onSuccess()});
            myAlert.addAction(okaction);
            self.present(myAlert, animated:true, completion:nil);
            
            
            // Still need to add the data to the user defaults and check on log in.
            
            /*//store data in default area(finally it should be store in database)
            let userid = UserDefaults.standard.string(forKey: "userid")
            if userid != nil{
                return;
            } else {
                let userDefault = UserDefaults.standard;
                userDefault.set(email, forKey:"email");
                userDefault.set(userName, forKey:"userName");
                userDefault.set(phoneNo, forKey:"phoneNo");
                userDefault.set(userPwd, forKey:"userPwd");
                userDefault.set(confirmPwd, forKey:"confirmPwd");
                userDefault.synchronize();
            }
            //Display alter page and register
            let myAlert = UIAlertController(title:"Success", message:"Register is success", preferredStyle:.alert);
            let okaction = UIAlertAction(title:"ok", style:UIAlertActionStyle.default){
                action in self.dismiss(animated: true, completion: nil)
            }
            myAlert.addAction(okaction);
            self.present(myAlert, animated: true, completion: nil);*/
            
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
        let _ = navigationController?.popViewController(animated: true)
    }
    
    // Display alert message function
    func displayAlertMessage(mymessage:String) {
        let myAlert = UIAlertController(title:"Error", message:mymessage, preferredStyle:.alert);
        let okaction=UIAlertAction(title:"ok", style:UIAlertActionStyle.default, handler:nil);
        myAlert.addAction(okaction);
        self.present(myAlert, animated:true, completion:nil);
    }
}
