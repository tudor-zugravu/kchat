//
//  ChangePasswordViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/19/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ChangePasswordViewController: UIViewController, ChangePasswordModelProtocol {
    
    @IBOutlet weak var profilePictureImageView: UIImageView!
    @IBOutlet weak var usernameLabel: UILabel!
    @IBOutlet weak var initialPasswordTextField: UITextField!
    @IBOutlet weak var newPasswordTextField: UITextField!
    @IBOutlet weak var confirmPasswordTextField: UITextField!
    
    let changePasswordModel = ChangePasswordModel()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        changePasswordModel.delegate = self
        
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        usernameLabel.text = UserDefaults.standard.value(forKey: "username") as! String?
        
        if UserDefaults.standard.bool(forKey: "hasProfilePicture") {
            let image = UIImage(contentsOfFile: (Utils.instance.getDocumentsDirectory().appendingPathComponent("\(UserDefaults.standard.value(forKey: "profilePicture"))")).path)
            profilePictureImageView.image = image
        }
    }
    
    @IBAction func changePasswordPressed(_ sender: Any) {
        initialPasswordTextField.resignFirstResponder()
        newPasswordTextField.resignFirstResponder()
        confirmPasswordTextField.resignFirstResponder()
        
        // Check for empty fields
        if initialPasswordTextField.text != nil && initialPasswordTextField.text != "" && newPasswordTextField.text != nil && newPasswordTextField.text != "" && confirmPasswordTextField.text != nil && confirmPasswordTextField.text != "" {
            
            // Check if the initial password matches
            if initialPasswordTextField.text == UserDefaults.standard.value(forKey: "password") as? String {
                
                // Check if the two passwords match
                if newPasswordTextField.text == confirmPasswordTextField.text {
                
                    // Initiate request for password change
                    changePasswordModel.data_request(UserDefaults.standard.value(forKey: "username") as! String, password: initialPasswordTextField.text!, newPassword: newPasswordTextField.text!)
                } else {
                    
                    // Display error message for different passwords
                    displayAlertMessage(mymessage: "New password and confirmation do not match")
                }
            } else {
                
                // Display error message for wrong password
                displayAlertMessage(mymessage: "Wrong password")
            }
        } else {
            
            // Display error message for wrong password
            displayAlertMessage(mymessage: "All fields are required!")
        }
    }
    
    // The function called at the arival of the response from the server
    func responseReceived(_ permission: NSString) {
        
        print(permission)
        
        if permission == "success" {
            
            // Add the new password to the user defaults.
            UserDefaults.standard.set(newPasswordTextField.text, forKey:"password");
            UserDefaults.standard.synchronize();
            
            // Alert for success and view change on dismiss
            let myAlert = UIAlertController(title:"Success", message:"Your password has been changed", preferredStyle:.alert);
            let okaction=UIAlertAction(title:"done", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in self.onSuccess()});
            myAlert.addAction(okaction);
            self.present(myAlert, animated:true, completion:nil);

        } else if permission == "invalid" {
            
            // Alert for invalid password (in case of remote changes)
            displayAlertMessage(mymessage: "Wrong password")
        } else {
            
            // Alert for change password error
            displayAlertMessage(mymessage: "Please try again")
        }
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    @IBAction func backButtonPressed(_ sender: Any) {
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
