//
//  ProfileViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/15.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class ProfileViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, ImageUploadModelProtocol {

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
    let imageUploadModel = ImageUploadModel()
    let picker = UIImagePickerController()
    var selectedImg: UIImage? = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.dropInit()
        self.dropButton.table.isHidden = true
        imageUploadModel.delegate = self
        picker.delegate = self
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        self.setListeners()
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
                    if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
                        SocketIOManager.sharedInstance.changeFullName(userId: UserDefaults.standard.value(forKey: "userId") as! Int, newName: (textField?.text)!)
                    } else {
                        self.noInternetAllert()
                    }
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
                    if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
                        SocketIOManager.sharedInstance.changeUsername(userId: UserDefaults.standard.value(forKey: "userId") as! Int, newUsername: (textField?.text)!)
                    } else {
                        self.noInternetAllert()
                    }
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
                        if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
                            SocketIOManager.sharedInstance.changeEmail(userId: UserDefaults.standard.value(forKey: "userId") as! Int, newEmail: (textField?.text)!)
                        } else {
                            self.noInternetAllert()
                        }
                       
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
                        if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
                            SocketIOManager.sharedInstance.changePhoneNo(userId: UserDefaults.standard.value(forKey: "userId") as! Int, newPhoneNo: (textField?.text)!)
                        } else {
                            self.noInternetAllert()
                        }
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
                        if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
                            SocketIOManager.sharedInstance.changeAbout(userId: UserDefaults.standard.value(forKey: "userId") as! Int, newAbout: (textField?.text)!)
                        } else {
                            self.noInternetAllert()
                        }
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
    
    func profilePictureLongPress(sender : UILongPressGestureRecognizer){
        if sender.state == .began {
            // Popup for validation with accepting a contact request on accept
            let myAlert = UIAlertController(title:"Change profile picture", message:"Select or take a new profile picture", preferredStyle:.alert);
            let cameraAction=UIAlertAction(title:"Take picture", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in
                self.cameraButtonPressed()
            });
            myAlert.addAction(cameraAction);
            let libraryAction=UIAlertAction(title:"Choose picture", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in
                self.photosButtonPressed()
            });
            myAlert.addAction(libraryAction);
            let noAction=UIAlertAction(title:"Cancel", style:UIAlertActionStyle.default, handler:nil);
            myAlert.addAction(noAction);
            self.present(myAlert, animated:true, completion:nil);
        }
    }
    
    func cameraButtonPressed() {
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            picker.allowsEditing = false
            picker.sourceType = UIImagePickerControllerSourceType.camera
            picker.cameraCaptureMode = .photo
            picker.modalPresentationStyle = .fullScreen
            present(picker,animated: true,completion: nil)
        } else {
            let alertView = UIAlertController(title: "Failed",
                                              message: "The camera is not available" as String, preferredStyle:.alert)
            let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
            alertView.addAction(okAction)
            self.present(alertView, animated: true, completion: nil)
        }
    }
    
    func photosButtonPressed() {
        picker.allowsEditing = false
        picker.sourceType = .photoLibrary
        picker.mediaTypes = UIImagePickerController.availableMediaTypes(for: .photoLibrary)!
        present(picker, animated: true, completion: nil)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        let chosenImage = info[UIImagePickerControllerOriginalImage] as! UIImage //2
        self.selectedImg = chosenImage
        if Utils.instance.isInternetAvailable() {
            self.uploadImage(id: String(describing: UserDefaults.standard.value(forKey: "userId")!))
        } else {
            dismiss(animated:true, completion: nil) //5
            self.noInternetAllert()
        }
    }
    
    func uploadImage(id: String) {
        // upload image
        if let capturePhotoImage = selectedImg {
            let smallerPhoto = cropToBounds(image: capturePhotoImage)
            selectedImg = smallerPhoto
            let jpegCompressionQuality: CGFloat = 1 // Set this to whatever suits your purpose
            if let base64String = UIImageJPEGRepresentation(smallerPhoto, jpegCompressionQuality)?.base64EncodedString() {
                imageUploadModel.uploadImage(id: id, base64String: base64String, type:"profile")
            } else {
                self.showError()
            }
        } else {
            self.showError()
        }
    }
    
    func resizeImage(image: UIImage, newWidth: CGFloat) -> UIImage? {
        
        let scale = newWidth / image.size.width
        let newHeight = image.size.height * scale
        UIGraphicsBeginImageContext(CGSize(width: newWidth, height: newHeight))
        image.draw(in: CGRect(x: 0, y: 0, width: newWidth, height: newHeight))
        
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return newImage
    }
    
    func cropToBounds(image: UIImage) -> UIImage {
        
        var size: Int
        if ((image.cgImage?.width)! < (image.cgImage?.height)!) {
            size = (image.cgImage?.width)!
        } else {
            size = (image.cgImage?.height)!
        }
        let contextImage: UIImage = UIImage(cgImage: image.cgImage!)
        
        let contextSize: CGSize = contextImage.size
        
        var posX: CGFloat = 0.0
        var posY: CGFloat = 0.0
        var cgwidth: CGFloat = CGFloat(size)
        var cgheight: CGFloat = CGFloat(size)
        
        // See what size is longer and create the center off of that
        if contextSize.width > contextSize.height {
            posX = ((contextSize.width - contextSize.height) / 2)
            posY = 0
            cgwidth = contextSize.height
            cgheight = contextSize.height
        } else {
            posX = 0
            posY = ((contextSize.height - contextSize.width) / 2)
            cgwidth = contextSize.width
            cgheight = contextSize.width
        }
        
        let rect: CGRect = CGRect(x:posX, y:posY, width:cgwidth, height:cgheight)
        
        // Create bitmap image from context using the rect
        let imageRef: CGImage = contextImage.cgImage!.cropping(to: rect)!
        
        // Create a new image based on the imageRef and rotate back to the original orientation
        let image: UIImage = UIImage(cgImage: imageRef, scale: image.scale, orientation: image.imageOrientation)
        
        return resizeImage(image: image, newWidth: 200)!
    }
    
    func imageUploaded(_ response: NSString) {
        if response.contains("success") {
            let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(UserDefaults.standard.value(forKey: "profilePicture"))")
            if let data = UIImagePNGRepresentation(selectedImg!) {
                do {
                    let fileManager = FileManager.default
                    if fileManager.fileExists(atPath: filename.path) {
                        try fileManager.removeItem(atPath: filename.path)
                        Utils.instance.deleteCachedPicture(image: "\(UserDefaults.standard.value(forKey: "profilePicture"))")
                    } else {
                        print("File does not exist")
                    }
                } catch let error as NSError {
                    print("An error took place: \(error)")
                }
                try? data.write(to: filename)
                profilePictureImageView.image = selectedImg
            }
        } else {
            self.showError()
        }
    }
    
    func showError() {
        let alertView = UIAlertController(title: "Failed",
                                          message: "There was a problem uploading the image" as String, preferredStyle:.alert)
        let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
        alertView.addAction(okAction)
        self.present(alertView, animated: true, completion: nil)
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
            // Popup for validation with accepting a contact request on accept
            let myAlert = UIAlertController(title:"Delete account", message:"Are you sure you want to delete your account?", preferredStyle:.alert);
            let yesAction=UIAlertAction(title:"Yes", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in self.deleteAccount(userId: UserDefaults.standard.value(forKey: "userId") as! Int)});
            myAlert.addAction(yesAction);
            let noAction=UIAlertAction(title:"No", style:UIAlertActionStyle.default, handler:nil);
            myAlert.addAction(noAction);
            self.present(myAlert, animated:true, completion:nil);
            self.dropButton.table.isHidden = true
        }), ({ () -> (Void) in
            self.dropButton.table.isHidden = true
            //push to change password page
            let nextView:UIViewController = (self.storyboard?.instantiateViewController(withIdentifier: "changePWD"))!
            self.navigationController?.pushViewController(nextView , animated: true)
        }), ({ () -> (Void) in
            self.dropButton.table.isHidden = true
            self.logOut(Any.self)
        })])
    }
    
    func logOut(_ sender: Any) {
        
        Utils.instance.logOut()
        _ = self.navigationController?.popToRootViewController(animated: true)
    }
    
    func deleteContact(receiver: Int) {
        if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
            SocketIOManager.sharedInstance.deleteContact(userId: UserDefaults.standard.value(forKey: "userId") as! Int, otherUserId: receiver)
        } else {
            noInternetAllert()
        }
    }
    
    func deleteAccount(userId: Int) {
        if SocketIOManager.sharedInstance.isConnected() && Utils.instance.isInternetAvailable() {
            SocketIOManager.sharedInstance.deleteAccount(userId: userId)
            self.logOut(Any.self)
        } else {
            noInternetAllert()
        }
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
    
    func setListeners() {
        if !(passedValue != nil) {
            let fullNameLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.fullNameLongPress))
            self.fullNameLabel.removeGestureRecognizer(fullNameLongPressGesture)
            self.fullNameLabel.addGestureRecognizer(fullNameLongPressGesture)
            let usernameLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.usernameLongPress))
            self.usernameLabel.removeGestureRecognizer(usernameLongPressGesture)
            self.usernameLabel.addGestureRecognizer(usernameLongPressGesture)
            let emailLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.emailLongPress))
            self.emailLabel.removeGestureRecognizer(emailLongPressGesture)
            self.emailLabel.addGestureRecognizer(emailLongPressGesture)
            let phoneNoLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.phoneNoLongPress))
            self.phoneNoLabel.removeGestureRecognizer(phoneNoLongPressGesture)
            self.phoneNoLabel.addGestureRecognizer(phoneNoLongPressGesture)
            let aboutLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.aboutLongPress))
            self.aboutLabel.removeGestureRecognizer(aboutLongPressGesture)
            self.aboutLabel.addGestureRecognizer(aboutLongPressGesture)
            let profilePictureLongPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(ProfileViewController.profilePictureLongPress))
            self.profilePictureImageView.removeGestureRecognizer(profilePictureLongPressGesture)
            self.profilePictureImageView.addGestureRecognizer(profilePictureLongPressGesture)
            
            SocketIOManager.sharedInstance.setFullNameChangedListener(completionHandler: { (response) -> Void in
                self.fullNameLabel.text = response
                UserDefaults.standard.set(response, forKey:"fullName");
                UserDefaults.standard.synchronize();
            })
            SocketIOManager.sharedInstance.setUsernameChangedListener(completionHandler: { (response) -> Void in
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
}
