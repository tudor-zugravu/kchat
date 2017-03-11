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
