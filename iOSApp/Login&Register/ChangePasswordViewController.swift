//
//  ChangePasswordViewController.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/19/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ChangePasswordViewController: UIViewController, UITextFieldDelegate,ChangePasswordModelProtocol {
    
    @IBOutlet weak var profilePictureImageView: UIImageView!
    @IBOutlet weak var usernameLabel: UILabel!
    @IBOutlet weak var initialPasswordTextField: UITextField!
    @IBOutlet weak var newPasswordTextField: UITextField!
    @IBOutlet weak var confirmPasswordTextField: UITextField!
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var bottomViewConstraint: NSLayoutConstraint!
    
    let changePasswordModel = ChangePasswordModel()
    var bottomViewDistance: CGFloat = 0
    var bottomDistance: CGFloat = 30
    var offset: CGFloat = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        changePasswordModel.delegate = self
        initialPasswordTextField.delegate = self
        newPasswordTextField.delegate = self
        confirmPasswordTextField.delegate = self

        setListeners()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        usernameLabel.text = UserDefaults.standard.value(forKey: "username") as! String?
        
        if UserDefaults.standard.bool(forKey: "hasProfilePicture") {
            let image = UIImage(contentsOfFile: (Utils.instance.getDocumentsDirectory().appendingPathComponent("\(UserDefaults.standard.value(forKey: "profilePicture"))")).path)
            profilePictureImageView.image = image
        }
        
        // Adding the gesture recognizer that will dismiss the keyboard on an exterior tap
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIInputViewController.dismissKeyboard))
        view.addGestureRecognizer(tap)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillHide, object: nil)
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
                
                    if Utils.instance.isInternetAvailable() {
                        // Initiate request for password change
                        changePasswordModel.data_request(UserDefaults.standard.value(forKey: "username") as! String, password: initialPasswordTextField.text!, newPassword: newPasswordTextField.text!)
                    } else {
                        self.noInternetAllert()
                    }
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
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        if (textField == initialPasswordTextField) {
            self.offset = 155
        } else if (textField == newPasswordTextField) {
            self.offset = 155
        } else {
            self.offset = 155
        }
    }
    
    func keyboardWillShow(notification:NSNotification) {
        adjustingHeight(show: true, notification: notification)
    }
    
    func keyboardWillHide(notification:NSNotification) {
        adjustingHeight(show: false, notification: notification)
    }
    
    func adjustingHeight(show:Bool, notification:NSNotification) {
        
        if let userInfo = notification.userInfo, let durationValue = userInfo[UIKeyboardAnimationDurationUserInfoKey], let curveValue = userInfo[UIKeyboardAnimationCurveUserInfoKey] {
            
            let duration = (durationValue as AnyObject).doubleValue
            let keyboardFrame:CGRect = (userInfo[UIKeyboardFrameBeginUserInfoKey] as! NSValue).cgRectValue
            let options = UIViewAnimationOptions(rawValue: UInt((curveValue as AnyObject).integerValue << 16))
            let changeInHeight = (keyboardFrame.height) * (show ? 1 : 0)
            
            self.bottomViewConstraint.constant = bottomViewDistance + changeInHeight
            self.bottomConstraint.constant = bottomDistance + offset * (show ? 1 : 0)
            UIView.animate(withDuration: duration!, delay: 0, options: options, animations: {
                
                self.view.layoutIfNeeded()
            }, completion: nil)
        }
    }
    
    func setListeners() {
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })
    }
    
    func noInternetAllert() {
        let alertView = UIAlertController(title: "No internet connection",
                                          message: "Please reconnect to the internet" as String, preferredStyle:.alert)
        let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
        alertView.addAction(okAction)
        self.present(alertView, animated: true, completion: nil)
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
}
