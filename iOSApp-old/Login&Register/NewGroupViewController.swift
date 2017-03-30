//
//  NewGroupViewController.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/3/14.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class NewGroupViewController: UIViewController {
    @IBOutlet weak var groupNameTextField: UITextField!

    @IBOutlet weak var groupDescription: UITextView!
    
    @IBOutlet weak var newMemberTableView: UITableView!
    let groupModel=GroupModel()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    @IBAction func doneButtonPressed(_ sender: Any) {
        if groupNameTextField.text != nil{
            groupModel.data_request(groupNameTextField.text!)
        }else{
            
            // Display alert messaage
            displayAlertMessage(mymessage: "All fields are required!");
        }
    }
    @IBAction func backButtonPressed(_ sender: Any) {
          let _ = navigationController?.popViewController(animated: true)
    }
    // Display alert message function
    func displayAlertMessage(mymessage:String) {
        let myAlert = UIAlertController(title:"Error", message:mymessage, preferredStyle:.alert);
        let okaction=UIAlertAction(title:"ok", style:UIAlertActionStyle.default, handler:nil);
        myAlert.addAction(okaction);
        self.present(myAlert, animated:true, completion:nil);
    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

}
