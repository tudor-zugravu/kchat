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
    @IBAction func loginButtom(_ sender: Any) {
        
        let userName=userNameTextField.text;
        let userPwd=userPwdTextField.text;
        
        let userNameDefult:String=UserDefaults.standard.object(forKey: "userName") as! String;
        let userPwdDefult=UserDefaults.standard.object(forKey: "userPwd") as! String;
        if userNameDefult == userName{
            if userPwdDefult==userPwd{
                //login is successful (the following is just use default to store it)
                UserDefaults.standard.set(true, forKey: "isUserLogin");
                UserDefaults.standard.synchronize()
                self.dismiss(animated: true, completion: nil)
            }
        }
        
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
