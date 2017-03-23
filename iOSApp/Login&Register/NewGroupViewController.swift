//
//  NewGroupViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/3/17.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class NewGroupViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

    @IBOutlet weak var groupNameTextField: UITextField!
    @IBOutlet weak var groupDescriptionTextField: UITextField!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var selectedImage: UIImageView!
    
    let groupModel=GroupModel()
    let picker = UIImagePickerController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        picker.delegate = self

        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        selectedImage.layer.shadowColor = UIColor.lightGray.cgColor
        selectedImage.layer.shadowRadius = 5
        selectedImage.layer.shadowOpacity = 0.6
        selectedImage.clipsToBounds = false
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
//        if groupNameTextField.text != nil{
//            groupModel.data_request(groupNameTextField.text!)
//        }else{
//            
//            // Display alert messaage
//            displayAlertMessage(mymessage: "All fields are required!");
//        }

    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : AnyObject]) {
        let chosenImage = info[UIImagePickerControllerOriginalImage] as! UIImage //2
        selectedImage.contentMode = .scaleAspectFit //3
        selectedImage.image = chosenImage //4
        dismiss(animated:true, completion: nil) //5
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        dismiss(animated: true, completion: nil)
    }

}
