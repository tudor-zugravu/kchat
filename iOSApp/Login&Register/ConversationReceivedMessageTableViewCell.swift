//
//  ConversationReceivedMessageTableViewCell.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/15/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ConversationReceivedMessageTableViewCell: UITableViewCell {

    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var timestampLabel: UILabel!
    @IBOutlet weak var profilePicture: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    func configureCell(_ message: String, _ timestamp: String, _ profilePic: String) {
        messageLabel.text = message
        timestampLabel.text = timestamp
        if (profilePic != "") {
            let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(profilePic)").path
            profilePicture.image = UIImage(contentsOfFile: filename)
        } else {
            profilePicture.image = UIImage(named: "profile-logo.png")
        }
        messageLabel.sizeToFit()
        
    }
}
