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
    @IBOutlet weak var aboutLabel: UILabel!
    @IBOutlet weak var deleteContactButton: UIButton!
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var dropButton: DropMenuButton!
    @IBOutlet weak var otherBottomView: UIView!
    
    var passedValue: ContactModel?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.dropInit()
        
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setContactDeletedListener(completionHandler: { (response) -> Void in
            if(response == "success") {
                let _ = self.navigationController?.popViewController(animated: true)
            } else {
                let alertView = UIAlertController(title: "Error",
                                                  message: "There was a problem deleting \((self.passedValue?.name)!) from your contacts" as String, preferredStyle:.alert)
                let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
                alertView.addAction(okAction)
                self.present(alertView, animated: true, completion: nil)
            }
        })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        if let value = passedValue {
            usernameLabel.text = value.username
            fullNameLabel.text = value.name
            emailLabel.text = value.email
            phoneNoLabel.text = value.phoneNo
            aboutLabel.text = value.about
            
            let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(value.profilePicture!)")
            if FileManager.default.fileExists(atPath: filename.path) {
                profilePictureImageView.image = UIImage(contentsOfFile: filename.path)
            } else {
                // Download the profile picture, if exists
                if let url = URL(string: "http://188.166.157.62/profile_pictures/\(value.profilePicture!)") {
                    if let data = try? Data(contentsOf: url) {
                        var profileImg: UIImage
                        profileImg = UIImage(data: data)!
                        if let data = UIImagePNGRepresentation(profileImg) {
                            try? data.write(to: filename)
                            profilePictureImageView.image = UIImage(contentsOfFile: value.profilePicture!)
                        }
                    }
                }
            }
            dropButton.isHidden = true
            otherBottomView.isHidden = false
            backButton.isHidden = false

        } else {
            usernameLabel.text = UserDefaults.standard.value(forKey: "username") as! String?
            fullNameLabel.text = UserDefaults.standard.value(forKey: "fullName") as! String?
            emailLabel.text = UserDefaults.standard.value(forKey: "email") as! String?
            phoneNoLabel.text = UserDefaults.standard.value(forKey: "phoneNo") as! String?
            aboutLabel.text = UserDefaults.standard.value(forKey: "about") as! String?
            
            if UserDefaults.standard.bool(forKey: "hasProfilePicture") {
                let image = UIImage(contentsOfFile: (Utils.instance.getDocumentsDirectory().appendingPathComponent("\(UserDefaults.standard.value(forKey: "profilePicture"))")).path)
                profilePictureImageView.image = image
            }
            dropButton.isHidden = false
            otherBottomView.isHidden = true
            backButton.isHidden = true
            Utils.instance.setTabBarValues(tabBarController: self.tabBarController as! TabBarController)
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
        dropButton.initMenu(["Delete Account", "Change Password", "Logout"],actions: [({ () -> (Void) in
            let nextView:UIViewController = (self.storyboard?.instantiateViewController(withIdentifier: "editProfileViewController"))!
            self.navigationController?.pushViewController(nextView , animated: true)
        }), ({ () -> (Void) in
            print("delete account")
        }), ({ () -> (Void) in
            //push to change password page
            let nextView:UIViewController = (self.storyboard?.instantiateViewController(withIdentifier: "changePWD"))!
            self.navigationController?.pushViewController(nextView , animated: true)
        }),({ () -> (Void) in
            self.logOut(Any.self)
        })])
        

    }
   func logOut(_ sender: Any) {
        
    Utils.instance.logOut()
    _ = self.navigationController?.popToRootViewController(animated: true)
    }
    
    func deleteContact(receiver: Int) {
        SocketIOManager.sharedInstance.deleteContact(userId: UserDefaults.standard.value(forKey: "userId") as! Int, otherUserId: receiver)
    }
    
    @IBAction func deleteContactPressed(_ sender: Any) {
        let myAlert = UIAlertController(title:"Delete contact", message:"Are you sure you want to delete \((self.passedValue?.name)!) from your contacts?", preferredStyle:.alert);
        let yesAction=UIAlertAction(title:"Yes", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in self.deleteContact(receiver: (self.passedValue?.userId)!)});
        myAlert.addAction(yesAction);
        let noAction=UIAlertAction(title:"No", style:UIAlertActionStyle.default, handler:nil);
        myAlert.addAction(noAction);
        self.present(myAlert, animated:true, completion:nil);
    }
    
    @IBAction func backButtonPressed(_ sender: Any) {
        let _ = navigationController?.popViewController(animated: true)
    }
    
}
