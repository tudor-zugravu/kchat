//
//  ProfileViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/15.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class ProfileViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    @IBAction func LogOut(_ sender: Any) {
        UserDefaults.standard.set(false, forKey: "isUserLogin");
        UserDefaults.standard.synchronize()
        self.performSegue(withIdentifier:"loginView",sender:self)
    }
    
}
