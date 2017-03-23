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
        self.dropButton.table.isHidden = true
        
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setContactDeletedListener(completionHandler: { (response) -> Void in
            if(response == "success") {
                let _ = self.navigationController?.popViewController(animated: true)
                self.navigationController?.topViewController?.childViewControllers[2].viewWillAppear(true)
            } else {
                let alertView = UIAlertController(title: "Error",
                                                  message: "There was a problem deleting \((self.passedValue?.name)!) from your contacts" as String, preferredStyle:.alert)
                let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
                alertView.addAction(okAction)
                self.present(alertView, animated: true, completion: nil)
            }
        })
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if !(passedValue != nil) {
            let fullNameLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.fullNameLongPress))
            self.fullNameLabel.addGestureRecognizer(fullNameLongPressGesture)
            let usernameLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.usernameLongPress))
            self.usernameLabel.addGestureRecognizer(usernameLongPressGesture)
            let emailLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.emailLongPress))
            self.emailLabel.addGestureRecognizer(emailLongPressGesture)
            let phoneNoLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.phoneNoLongPress))
            self.phoneNoLabel.addGestureRecognizer(phoneNoLongPressGesture)
            let aboutLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.aboutLongPress))
            self.aboutLabel.addGestureRecognizer(aboutLongPressGesture)
            
            SocketIOManager.sharedInstance.setFullNameChangedListener(completionHandler: { (response) -> Void in
                self.fullNameLabel.text = response
                UserDefaults.standard.set(response, forKey:"fullName");
                UserDefaults.standard.synchronize();
            })
            SocketIOManager.sharedInstance.setUsernameChangedListener(completionHandler: { (response) -> Void in
                print(response)
                if (response == "duplicate") {
                    let alertView = UIAlertController(title: "Failed",
                                                      message: "Username already exists" as String, preferredStyle:.alert)
                    let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
                    alertView.addAction(okAction)
                    self.present(alertView, animated: true, completion: nil)
                } else {
                    self.usernameLabel.text = response
                    UserDefaults.standard.set(response, forKey:"username");
                    UserDefaults.standard.synchronize();
                }
            })
            SocketIOManager.sharedInstance.setEmailChangedListener(completionHandler: { (response) -> Void in
                self.emailLabel.text = response
                UserDefaults.standard.set(response, forKey:"email");
                UserDefaults.standard.synchronize();
            })
            SocketIOManager.sharedInstance.setPhoneNoChangedListener(completionHandler: { (response) -> Void in
                self.phoneNoLabel.text = response
                UserDefaults.standard.set(response, forKey:"phoneNo");
                UserDefaults.standard.synchronize();
            })
            SocketIOManager.sharedInstance.setAboutChangedListener(completionHandler: { (response) -> Void in
                self.aboutLabel.text = response
                UserDefaults.standard.set(response, forKey:"about");
                UserDefaults.standard.synchronize();
            })
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })
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
        }
    }
    
    func fullNameLongPress(sender : UILongPressGestureRecognizer){
        if sender.state == .began {
            let alert = UIAlertController(title: "Edit name", message: "Enter the new name", preferredStyle: .alert)
            alert.addTextField { (textField) in
                textField.text = ""
            }
            alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
                let textField = alert?.textFields![0]
                if textField?.text != "" && textField?.text != nil {
                    SocketIOManager.sharedInstance.changeFullName(userId: UserDefaults.standard.value(forKey: "userId") as! Int, newName: (textField?.text)!)
                }
            }))
            alert.addAction(UIAlertAction(title:"Cancel", style:UIAlertActionStyle.default, handler:nil))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    func usernameLongPress(sender : UILongPressGestureRecognizer){
        if sender.state == .began {
            let alert = UIAlertController(title: "Edit username", message: "Enter the new username", preferredStyle: .alert)
            alert.addTextField { (textField) in
                textField.text = ""
            }
            alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
                let textField = alert?.textFields![0]
                if textField?.text != "" && textField?.text != nil {
                    SocketIOManager.sharedInstance.changeUsername(userId: UserDefaults.standard.value(forKey: "userId") as! Int, newUsername: (textField?.text)!)
                }
            }))
            alert.addAction(UIAlertAction(title:"Cancel", style:UIAlertActionStyle.default, handler:nil))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    func emailLongPress(sender : UILongPressGestureRecognizer){
        if sender.state == .began {
            let alert = UIAlertController(title: "Edit email", message: "Enter the new email", preferredStyle: .alert)
            alert.addTextField { (textField) in
                textField.text = ""
            }
            alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
                let textField = alert?.textFields![0]
                if textField?.text != "" && textField?.text != nil {
                    // Email format check
                    let mailPattern = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$"
                    let email = MyRegex(mailPattern)
                    if email.match(input: (textField?.text!)!) {
                        SocketIOManager.sharedInstance.changeEmail(userId: UserDefaults.standard.value(forKey: "userId") as! Int, newEmail: (textField?.text)!)
                    }else{
                        let alertView = UIAlertController(title: "Failed",
                                                          message: "Email format not valid" as String, preferredStyle:.alert)
                        let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
                        alertView.addAction(okAction)
                        self.present(alertView, animated: true, completion: nil)
                    }
                }
            }))
            alert.addAction(UIAlertAction(title:"Cancel", style:UIAlertActionStyle.default, handler:nil))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    func phoneNoLongPress(sender : UILongPressGestureRecognizer){
        if sender.state == .began {
            let alert = UIAlertController(title: "Edit phone number", message: "Enter the new phone number", preferredStyle: .alert)
            alert.addTextField { (textField) in
                textField.text = ""
            }
            alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
                let textField = alert?.textFields![0]
                if textField?.text != "" && textField?.text != nil {
                    // Telephone format check
                    let phoneParrern = "^7[0-9]{9}$"
                    let matcher = MyRegex(phoneParrern)
                    if matcher.match(input: (textField?.text!)!){
                        SocketIOManager.sharedInstance.changePhoneNo(userId: UserDefaults.standard.value(forKey: "userId") as! Int, newPhoneNo: (textField?.text)!)
                    } else {
                        let alertView = UIAlertController(title: "Failed",
                                                          message: "Phone number format not valid" as String, preferredStyle:.alert)
                        let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
                        alertView.addAction(okAction)
                        self.present(alertView, animated: true, completion: nil)
                    }
                }
            }))
            alert.addAction(UIAlertAction(title:"Cancel", style:UIAlertActionStyle.default, handler:nil))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    func aboutLongPress(sender : UILongPressGestureRecognizer){
        if sender.state == .began {
            let alert = UIAlertController(title: "Edit description", message: "Enter new description", preferredStyle: .alert)
            alert.addTextField { (textField) in
                textField.text = ""
            }
            alert.addAction(UIAlertAction(title: "Save", style: .default, handler: { [weak alert] (_) in
                let textField = alert?.textFields![0]
                if textField?.text != "" && textField?.text != nil {
                    if (textField?.text)!.characters.count <= 50 {
                        SocketIOManager.sharedInstance.changeAbout(userId: UserDefaults.standard.value(forKey: "userId") as! Int, newAbout: (textField?.text)!)
                    } else {
                        let alertView = UIAlertController(title: "Failed",
                                                         message: "Description must be under 50 characters" as String, preferredStyle:.alert)
                        let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
                        alertView.addAction(okAction)
                        self.present(alertView, animated: true, completion: nil)
                    }
                }
            }))
            alert.addAction(UIAlertAction(title:"Cancel", style:UIAlertActionStyle.default, handler:nil))
            self.present(alert, animated: true, completion: nil)
        }
    }

    @IBAction func menuPressed(_ sender: Any) {
        if self.dropButton.table.isHidden {
            self.dropInit()
            let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(dismissMenu))
            tap.cancelsTouchesInView = false
            self.view.addGestureRecognizer(tap)
        }
    }
    
    //touch the space and hide drop down menu
    func dismissMenu(gestureRecognizer: UITapGestureRecognizer) {
        if !self.dropButton.table.frame.contains(gestureRecognizer.location(in: self.view)) {
            self.dropButton.table.isHidden = true
        }
    }
    
    //Dropdown menu Initinal
    func dropInit() {
        dropButton.initMenu(["Delete Account", "Change Password", "Logout"],actions: [({ () -> (Void) in
            print("delete account")
        }), ({ () -> (Void) in
            //push to change password page
            let nextView:UIViewController = (self.storyboard?.instantiateViewController(withIdentifier: "changePWD"))!
            self.navigationController?.pushViewController(nextView , animated: true)
        }), ({ () -> (Void) in
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
        if (passedValue != nil) {
            navigationController?.topViewController?.childViewControllers[2].viewWillAppear(true)
        }
    }
    
    //regular expression function
    struct  MyRegex {
        let regex: NSRegularExpression?
        
        init(_ pattern: String) {
            regex = try? NSRegularExpression(pattern: pattern,
                                             options: .caseInsensitive)
        }
        
        func match(input: String) -> Bool {
            if let matches = regex?.matches(in: input,
                                            options: [],
                                            range: NSMakeRange(0, (input as NSString).length)) {
                return matches.count > 0
            } else {
                return false
            }
        }
    }
}
