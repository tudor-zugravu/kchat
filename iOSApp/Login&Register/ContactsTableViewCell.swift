//
//  ContactsTableViewCell.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/21/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ContactsTableViewCell: UITableViewCell {
    
    
    @IBOutlet weak var fullNameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var profilePicture: UIImageView!
    @IBOutlet weak var containerView: UIView!
    
    override func awakeFromNib() {
        super.awakeFromNib()

        //cell layout
        containerView.layer.cornerRadius = 10
        containerView.layer.shadowColor = UIColor.lightGray.cgColor
        containerView.layer.shadowOffset = CGSize(width:-2, height:2)
        containerView.layer.shadowRadius = 3
        containerView.layer.shadowOpacity = 0.6
        containerView.clipsToBounds = false
    }
    
    func configureCell(_ fullName: String, email: String, profilePic: String) {
        fullNameLabel.text = fullName
        emailLabel.text = email
        if (profilePic != "") {
            let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(profilePic)").path
            profilePicture.image = UIImage(contentsOfFile: filename)
        } else {
            profilePicture.image = UIImage(named: "profile-logo.png")
        }
    }
    
}
