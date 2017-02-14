//
//  RegisterPageViewControllorViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/8.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class RegisterPageViewControllorViewController: UIViewController {

    @IBOutlet weak var userNameTextField: UITextField!
    @IBOutlet weak var userEmailTextField: UITextField!
    @IBOutlet weak var userPhoneNoTestField: UITextField!
    @IBOutlet weak var userPwdTextField: UITextField!
    @IBOutlet weak var confirmPwdTextField: UITextField!
  
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func registerButton(_ sender: Any) {
        let userName:String=userNameTextField.text!;
        let email:String=userEmailTextField.text!;
        let phoneNo:String=userPhoneNoTestField.text!;
        let userPwd:String=userPwdTextField.text!;
        let confirmPwd=confirmPwdTextField.text;
        
        //check for empty field
        if (userName.isEmpty||email.isEmpty||phoneNo.isEmpty||userPwd.isEmpty||(confirmPwd?.isEmpty)!){
            //display alter messaage
            displayAlertMessage(mymessage: "All fields are required!");
            return;
        }
        //two passward is same
        if userPwd != confirmPwd{
            //display alter message
            displayAlertMessage(mymessage: "Wrong password confirmation!")
            return;
        }
        //store data in default area(finally it should be store in database)
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
        self.present(myAlert, animated: true, completion: nil);
    }
  
    //display alter message function
    func displayAlertMessage(mymessage:String) {
        let myAlert = UIAlertController(title:"Oopse", message:mymessage, preferredStyle:.alert);
        let okaction=UIAlertAction(title:"ok", style:UIAlertActionStyle.default, handler:nil);
        myAlert.addAction(okaction);
        self.present(myAlert, animated:true, completion:nil);
        
        // TESTING
    }
    
    
    }
