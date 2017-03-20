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
    
    @IBOutlet weak var dropButton: DropMenuButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.dropInit()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        Utils.instance.setTabBarValues(tabBarController: self.tabBarController as! TabBarController)
        usernameLabel.text = UserDefaults.standard.value(forKey: "username") as! String?
        fullNameLabel.text = UserDefaults.standard.value(forKey: "fullName") as! String?
        emailLabel.text = UserDefaults.standard.value(forKey: "email") as! String?
        phoneNoLabel.text = UserDefaults.standard.value(forKey: "phoneNo") as! String?
        
        if UserDefaults.standard.bool(forKey: "hasProfilePicture") {
            let image = UIImage(contentsOfFile: (Utils.instance.getDocumentsDirectory().appendingPathComponent("\(UserDefaults.standard.value(forKey: "profilePicture"))")).path)
            profilePictureImageView.image = image
        }
    }

    @IBAction func menuPressed(_ sender: Any) {
        self.dropInit()
    }
    //touch the space and hide drop down menu
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.dropButton.table.isHidden = true
        
    }

    
    @IBAction func changePassword(_ sender: Any) {
        
    }
    //Dropdown menu Initinal
    func dropInit() {
        dropButton.initMenu(["Edit Profile", "Change Password", "Change Profile image", "Logout"],actions: [({ () -> (Void) in
            let nextView:UIViewController = (self.storyboard?.instantiateViewController(withIdentifier: "editProfileViewController"))!
            self.navigationController?.pushViewController(nextView , animated: true)
        }), ({ () -> (Void) in
            //push to change password page
            let nextView:UIViewController = (self.storyboard?.instantiateViewController(withIdentifier: "changePWD"))!
            self.navigationController?.pushViewController(nextView , animated: true)
        }), ({ () -> (Void) in
            print("Change Profile image")
        }),({ () -> (Void) in
            self.logOut(Any.self)
        })])
        

    }
   func logOut(_ sender: Any) {
        
    Utils.instance.logOut()
    navigationController?.popToRootViewController(animated: true)
//    self.performSegue(withIdentifier: "profileLogInViewController", sender: self)
    }
}
