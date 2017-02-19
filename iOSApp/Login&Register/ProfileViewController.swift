//
//  ProfileViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/15.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class ProfileViewController: UIViewController {

    @IBOutlet weak var profilePictureImageView: UIImageView!
    @IBOutlet weak var usernameLabel: UILabel!
    @IBOutlet weak var fullNameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var phoneNoLabel: UILabel!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        usernameLabel.text = UserDefaults.standard.value(forKey: "username") as! String?
        fullNameLabel.text = UserDefaults.standard.value(forKey: "fullName") as! String?
        emailLabel.text = UserDefaults.standard.value(forKey: "email") as! String?
        phoneNoLabel.text = UserDefaults.standard.value(forKey: "phoneNo") as! String?
    }

    @IBAction func changePassword(_ sender: Any) {
        
    }

    @IBAction func logOut(_ sender: Any) {
        print("yep..")
        let userDefaults = UserDefaults.standard;
        userDefaults.removeObject(forKey: "email")
        userDefaults.removeObject(forKey: "username")
        userDefaults.removeObject(forKey: "phoneNo")
        userDefaults.removeObject(forKey: "password")
        userDefaults.removeObject(forKey: "fullName")
        userDefaults.set(false, forKey: "hasLoginKey")
        UserDefaults.standard.synchronize()
        self.performSegue(withIdentifier: "profileLogInViewController", sender: self)
    }    
}
