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
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.layer.borderColor = UIColor.black.cgColor
        self.layer.cornerRadius = 10
        self.layer.shadowColor=UIColor(white:000000, alpha:0.3).cgColor
        self.layer.shadowOffset = CGSize(width:-6, height:6)
        self.layer.shadowRadius=4
        self.layer.shadowOpacity=0.4
        
        self.clipsToBounds = false;
    }
    
    func configureCell(_ fullName: String, email: String, profilePic: String) {
        fullNameLabel.text = fullName
        emailLabel.text = email
        if (profilePic != "") {
            let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(profilePic)").path
            profilePicture.image = UIImage(contentsOfFile: filename)
        }
    }
    
}
