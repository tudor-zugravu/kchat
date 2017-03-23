//
//  NewGroupViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/3/17.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class NewGroupViewController: UIViewController {

    @IBOutlet weak var groupNameTextField: UITextField!
    @IBOutlet weak var groupDescriptionTextField: UITextField!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var selectedImage: UIImageView!
    
    let groupModel=GroupModel()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        SocketIOManager.sharedInstance.setDisconnectedListener(completionHandler: { (userList) -> Void in
            print("disconnected");
            Utils.instance.logOut()
            _ = self.navigationController?.popToRootViewController(animated: true)
        })
        SocketIOManager.sharedInstance.setGlobalPrivateListener(completionHandler: { () -> Void in })
    }

    @IBAction func cameraButtonPressed(_ sender: Any) {
    }
    
    @IBOutlet weak var photosButtonPressed: UIButton!
    
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

}
