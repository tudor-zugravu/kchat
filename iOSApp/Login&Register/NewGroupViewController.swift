//
//  NewGroupViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/3/17.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class NewGroupViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UITableViewDataSource, UITableViewDelegate, ContactsModelProtocol {

    @IBOutlet weak var groupNameTextField: UITextField!
    @IBOutlet weak var groupDescriptionTextField: UITextField!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var selectedImage: UIImageView!
    
    var contacts: [SelectedContactModel] = []
    let contactsModel = ContactsModel()
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
        
        self.tableView.contentInset = UIEdgeInsetsMake(8, 0, 0, 0)
        
        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
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
        let _ = navigationController?.popViewController(animated: true)
    }
    
    @IBAction func doneButtonPressed(_ sender: Any) {
//    if groupNameTextField.text != nil {
//            groupModel.data_request(groupNameTextField.text!)
//    }
        
        
        
        
        print("haha")
        if (imageHasChanged) {
            // upload image
            print("uploading")
            
            if let capturePhotoImage = selectedImg {
                if let smallerPhoto = resizeImage(image: capturePhotoImage, newWidth: 200) {
                    selectedImage.image = smallerPhoto
                    let jpegCompressionQuality: CGFloat = 1 // Set this to whatever suits your purpose
                    if let base64String = UIImageJPEGRepresentation(smallerPhoto, jpegCompressionQuality)?.base64EncodedString() {
//                        let encodedImageData = smallerPhoto.base64EncodedString(options: NSData.Base64EncodingOptions(rawValue: 0))
                        
                        // Setting up the server session with the URL and the request
                        let url: URL = URL(string: "http://188.166.157.62:4000/iOSGroupImageUpload")!
                        let session = URLSession.shared
                        var request = URLRequest(url:url)
                        request.httpMethod = "POST"
                        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
                        
                        // Request parameters
                        let paramString = "image=\(base64String)&sender=\(UserDefaults.standard.value(forKey: "userId")!)"
                        request.httpBody = paramString.data(using: String.Encoding.utf8)
                        
                        let task = URLSession.shared.dataTask(with: request) { data, response, error in
                            guard let data = data, error == nil else {
                                print("error=\(error)")
                                return
                            }
                            if let httpStatus = response as? HTTPURLResponse, httpStatus.statusCode != 200 {           // check for http errors
                                print("statusCode should be 200, but is \(httpStatus.statusCode)")
                                print("response = \(response)")
                            }
                            let responseString = String(data: data, encoding: .utf8)
                            print("responseString = \(responseString)")
                        }
                        task.resume()
                    }
                }
            }
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
