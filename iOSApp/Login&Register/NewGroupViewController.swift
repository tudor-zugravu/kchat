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
    
    @IBAction func photosButtonPressed(_ sender: Any) {
        picker.allowsEditing = false
        picker.sourceType = .photoLibrary
        picker.mediaTypes = UIImagePickerController.availableMediaTypes(for: .photoLibrary)!
        present(picker, animated: true, completion: nil)
    }
    
    @IBAction func backButtonPressed(_ sender: Any) {
        self.goBack()
    }
    
    @IBAction func doneButtonPressed(_ sender: Any) {
        if groupNameTextField.text != nil && groupNameTextField.text != "" && groupDescriptionTextField.text != nil && groupDescriptionTextField.text != "" {
            
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
        } else {
            let alertView = UIAlertController(title: "Failed",
                                              message: "Please fill in the group name and description" as String, preferredStyle:.alert)
            let okAction = UIAlertAction(title: "Done", style: .default, handler: nil)
            alertView.addAction(okAction)
            self.present(alertView, animated: true, completion: nil)
        }
    }
    
    func uploadImage(id: String) {
        // upload image
        if let capturePhotoImage = selectedImg {
            if let smallerPhoto = resizeImage(image: capturePhotoImage, newWidth: 200) {
                let jpegCompressionQuality: CGFloat = 1 // Set this to whatever suits your purpose
                if let base64String = UIImageJPEGRepresentation(smallerPhoto, jpegCompressionQuality)?.base64EncodedString() {
                    imageUploadModel.uploadImage(id: id, base64String: base64String)
                } else {
                    self.showError()
                }
            } else {
                self.showError()
            }
        } else {
            self.showError()
        }
    }
    
    func imageUploaded(_ response: NSString) {
        if response.contains("success") {
            print("ok - image uploaded")
            self.goBack()
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
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        dismiss(animated: true, completion: nil)
    }

}
