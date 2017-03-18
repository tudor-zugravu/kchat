//
//  ChatsTableViewCell.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/14/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ChatsTableViewCell: UITableViewCell {
        
    
    @IBOutlet weak var receiverNameLabel: UILabel!
    @IBOutlet weak var lastMessageLabel: UILabel!
    @IBOutlet weak var profilePicture: UIImageView!
    @IBOutlet weak var timestampLabel: UILabel!
    @IBOutlet weak var containerView: UIView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        containerView.layer.cornerRadius = 10
        containerView.layer.shadowColor = UIColor.lightGray.cgColor
        containerView.layer.shadowOffset = CGSize(width:-2, height:2)
        containerView.layer.shadowRadius = 3
        containerView.layer.shadowOpacity = 0.6
        containerView.clipsToBounds = false
    }
    
    func configureCell(_ receiverName: String, lastMessage: String, timestamp: String, profilePic: String) {
        receiverNameLabel.text = receiverName
        lastMessageLabel.text = lastMessage
        timestampLabel.text = timestamp
        if (profilePic != "") {
            let filename = Utils.instance.getDocumentsDirectory().appendingPathComponent("\(profilePic)").path
            profilePicture.image = UIImage(contentsOfFile: filename)
        }
    }
}
