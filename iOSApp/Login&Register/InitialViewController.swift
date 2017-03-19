//
//  InitialViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/6.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class InitialViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    override func viewDidAppear(_ animated: Bool) {
        
        // If a user is already logged in, proceed to the messages view
        if UserDefaults.standard.bool(forKey: "hasLoginKey") == true {
            SocketIOManager.sharedInstance.establishConnection()
            self.performSegue(withIdentifier: "initialTabBarViewController", sender: nil)
        } else {
            self.performSegue(withIdentifier: "loginView", sender: self)
        }
    }
}

