//
//  AddGroupContactsTableViewCell.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 23/03/2017.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class AddGroupContactsTableViewCell: UITableViewCell {
    
    @IBOutlet weak var fullNameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var profilePicture: UIImageView!
    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var isSelectedImage: UIImageView!
    
    var contactIsSelected: Bool = false
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        //cell layout
        containerView.layer.cornerRadius = 10
        containerView.layer.shadowColor = UIColor.lightGray.cgColor
        containerView.layer.shadowOffset = CGSize(width:-2, height:2)
        containerView.layer.shadowRadius = 3
        containerView.layer.shadowOpacity = 0.6
        containerView.clipsToBounds = false
        isSelectedImage.isHidden = true
    }
    
    func configureCell(_ fullName: String, email: String, profilePic: String, selected: Bool) {
        fullNameLabel.text = fullName
        emailLabel.text = email
        if (profilePic != "") {
            let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(profilePic)").path
            profilePicture.image = UIImage(contentsOfFile: filename)
        } else {
            profilePicture.image = UIImage(named: "profile-logo.png")
        }
        if selected {
            isSelectedImage.isHidden = false
        } else {
            isSelectedImage.isHidden = true
        }
        contactIsSelected = selected
    }
    
    func selectCell () {
        if contactIsSelected {
            isSelectedImage.isHidden = true
            contactIsSelected = false
        } else {
            isSelectedImage.isHidden = false
            contactIsSelected = true
        }
    }
}
