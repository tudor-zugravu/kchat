//
//  NewGroupViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/3/17.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class NewGroupViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UITableViewDataSource, UITableViewDelegate, ContactsModelProtocol, ImageUploadModelProtocol {

    @IBOutlet weak var groupNameTextField: UITextField!
    @IBOutlet weak var groupDescriptionTextField: UITextField!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var selectedImage: UIImageView!
    
    var contacts: [SelectedContactModel] = []
    let contactsModel = ContactsModel()
    let imageUploadModel = ImageUploadModel()
    var numberOfSelectedContacts: Int = 0
    var imageHasChanged: Bool = false
    var selectedImg: UIImage? = nil
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    
    var bottomDistance: CGFloat = 0;
    let picker = UIImagePickerController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        picker.delegate = self
        tableView.delegate = self
        tableView.dataSource = self
        contactsModel.delegate = self
        imageUploadModel.delegate = self
        
        self.tableView.contentInset = UIEdgeInsetsMake(8, 0, 0, 0)
        
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setGroupCreatedListener(completionHandler: { (response) -> Void in
            if (response == "fail") {
                let alertView = UIAlertController(title: "A problem has occured",
                                                  message: "Please try again!" as String, preferredStyle:.alert)
                let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
                alertView.addAction(okAction)
                self.present(alertView, animated: true, completion: nil)
            } else {
                print("si raspunsul esteeee \(response!)")
                if (self.imageHasChanged) {
                    self.uploadImage(id: response!)
                } else {
                    print("ok")
                    self.goBack()
                }
            }
        })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        selectedImage.layer.shadowColor = UIColor.lightGray.cgColor
        selectedImage.layer.shadowRadius = 5
        selectedImage.layer.shadowOpacity = 0.6
        selectedImage.clipsToBounds = false
        
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIReceivedContactRequestListener(completionHandler: { () -> Void in })
        SocketIOManager.sharedInstance.setIWasDeletedListener(completionHandler: { (enemy) -> Void in
            self.contactsModel.downloadContacts()
        })
        SocketIOManager.sharedInstance.setMyRequestAcceptedListener(completionHandler: { () -> Void in
            self.contactsModel.downloadContacts()
        })
        contactsModel.downloadContacts()
        
        // Adding the gesture recognizer that will dismiss the keyboard on an exterior tap
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIInputViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillHide, object: nil)
    }

    func tableView(_ tableView:UITableView, numberOfRowsInSection section:Int) -> Int
    {
        return contacts.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "addGroupContactsCell") as? AddGroupContactsTableViewCell {
            
            var item: SelectedContactModel
            item = contacts[indexPath.row]
            
            cell.configureCell(item.name!, email: item.email!, profilePic: item.profilePicture!, selected: item.selected!)
            
            return cell
        } else {
            return AddGroupContactsTableViewCell()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let cell = tableView.cellForRow(at: indexPath) as! AddGroupContactsTableViewCell
        if (cell.contactIsSelected) {
            cell.selectCell()
            contacts[indexPath.row].selected = false
            numberOfSelectedContacts -= 1
        } else if (numberOfSelectedContacts == 5) {
            let alertView = UIAlertController(title: "Failed",
                                              message: "There can be only 6 members in a group" as String, preferredStyle:.alert)
            let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
            alertView.addAction(okAction)
            self.present(alertView, animated: true, completion: nil)
        } else {
            cell.selectCell()
            contacts[indexPath.row].selected = true
            numberOfSelectedContacts += 1
        }
    }
    
    // The function called at the arival of the response from the server
    func contactsDownloaded(_ contactDetails: [[String:Any]]) {
        
        var contactsAux: [SelectedContactModel] = []
        var item:SelectedContactModel;
        
        // parse the received JSON and save the contacts
        for i in 0 ..< contactDetails.count {
            
            if let name = contactDetails[i]["name"] as? String,
                let email = contactDetails[i]["email"] as? String,
                let userId = contactDetails[i]["user_id"] as? Int,
                let contactId = contactDetails[i]["contact_id"] as? Int
            {
                item = SelectedContactModel()
                item.name = name
                item.email = email
                item.userId = userId
                item.contactId = contactId
                
                if let profilePicture = contactDetails[i]["profile_picture"] as? String {
                    
                    let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(profilePicture)")
                    if FileManager.default.fileExists(atPath: filename.path) {
                        item.profilePicture = profilePicture
                    } else {
                        // Download the profile picture, if exists
                        if let url = URL(string: "http://188.166.157.62/profile_pictures/\(profilePicture)") {
                            if let data = try? Data(contentsOf: url) {
                                var profileImg: UIImage
                                profileImg = UIImage(data: data)!
                                if let data = UIImagePNGRepresentation(profileImg) {
                                    try? data.write(to: filename)
                                    item.profilePicture = profilePicture
                                } else {
                                    item.profilePicture = ""
                                }
                            } else {
                                item.profilePicture = ""
                            }
                        }
                    }
                } else {
                    item.profilePicture = ""
                }
                item.selected = false
                
                contactsAux.append(item)
            }
        }
        contacts = contactsAux
        numberOfSelectedContacts = 0
        self.tableView.reloadData()
    }

    @IBAction func cameraButtonPressed(_ sender: Any) {
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
    // (Source: http://stackoverflow.com/questions/39620095/uiimagepickercontroller-not-working-properly-with-swift-3)
    // Used for accessing the camera functions of the device
    
    @IBAction func photosButtonPressed(_ sender: Any) {
        picker.allowsEditing = false
        picker.sourceType = .photoLibrary
        picker.mediaTypes = UIImagePickerController.availableMediaTypes(for: .photoLibrary)!
        present(picker, animated: true, completion: nil)
    }
    // (Source: https://makeapppie.com/2016/06/28/how-to-use-uiimagepickercontroller-for-a-camera-and-photo-library-in-swift-3-0/)
    // Used for accessing a photo from the library
    
    @IBAction func backButtonPressed(_ sender: Any) {
        self.goBack()
    }
    
    @IBAction func doneButtonPressed(_ sender: Any) {
        if groupNameTextField.text != nil && groupNameTextField.text != "" && groupDescriptionTextField.text != nil && groupDescriptionTextField.text != "" {
            
            if numberOfSelectedContacts == 0 {
                let myAlert = UIAlertController(title:"Create empty group?", message:"You are the only contact in the group", preferredStyle:.alert);
                let yesAction=UIAlertAction(title:"Yes", style:UIAlertActionStyle.default, handler: {(alert: UIAlertAction!) in self.createTheGroup() })
                myAlert.addAction(yesAction);
                let noAction=UIAlertAction(title:"No", style:UIAlertActionStyle.default, handler:nil);
                myAlert.addAction(noAction);
                self.present(myAlert, animated:true, completion:nil);
            } else {
                createTheGroup()
            }
        } else {
            let alertView = UIAlertController(title: "Failed",
                                              message: "Please fill in the group name and description" as String, preferredStyle:.alert)
            let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
            alertView.addAction(okAction)
            self.present(alertView, animated: true, completion: nil)
        }
    }
        
        func createTheGroup() {
            var selectedContacts: [Int] = []
            contacts.forEach { contact in
                if contact.selected! {
                    selectedContacts.append(contact.userId!)
                }
            }
            
            if (imageHasChanged) {
                SocketIOManager.sharedInstance.createGroup(name: groupNameTextField.text!, description: groupDescriptionTextField.text!, ownerId: UserDefaults.standard.value(forKey: "userId") as! Int, group_picture: "true", members: selectedContacts)
                
            } else {
                SocketIOManager.sharedInstance.createGroup(name: groupNameTextField.text!, description: groupDescriptionTextField.text!, ownerId: UserDefaults.standard.value(forKey: "userId") as! Int, group_picture: "", members: selectedContacts)
            }
        }
    
    func uploadImage(id: String) {
        // upload image
        if let capturePhotoImage = selectedImg {
            let smallerPhoto = cropToBounds(image: capturePhotoImage)
            let jpegCompressionQuality: CGFloat = 1 // Set this to whatever suits your purpose
            if let base64String = UIImageJPEGRepresentation(smallerPhoto, jpegCompressionQuality)?.base64EncodedString() {
                imageUploadModel.uploadImage(id: id, base64String: base64String, type:"group")
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
            print("ok - image uploaded")
            self.goBack()
        } else {
            self.showError()
        }
    }
    
    func goBack() {
        let _ = navigationController?.popViewController(animated: true)
        self.navigationController?.topViewController?.childViewControllers[1].viewWillAppear(true)
    }
    
    func showError() {
        let alertView = UIAlertController(title: "Failed",
                                          message: "There was a problem uploading the image" as String, preferredStyle:.alert)
        let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
        alertView.addAction(okAction)
        self.present(alertView, animated: true, completion: nil)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        let chosenImage = info[UIImagePickerControllerOriginalImage] as! UIImage //2
        selectedImage.contentMode = .scaleAspectFit //3
        selectedImage.image = chosenImage //4
        selectedImg = chosenImage
        imageHasChanged = true
        dismiss(animated:true, completion: nil) //5
    }
    // (Source: https://makeapppie.com/2014/12/04/swift-swift-using-the-uiimagepickercontroller-for-a-camera-and-photo-library/)
    // Used for accessing the selected or taken photograph
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        dismiss(animated: true, completion: nil)
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
            let options = UIViewAnimationOptions(rawValue: UInt((curveValue as AnyObject).integerValue << 16))
            let changeInHeight = 20 * (show ? 1 : 0)
            
            self.bottomConstraint.constant = bottomDistance + CGFloat(changeInHeight)
            UIView.animate(withDuration: duration!, delay: 0, options: options, animations: {
                
                self.view.layoutIfNeeded()
            }, completion: nil)
        }
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
}
