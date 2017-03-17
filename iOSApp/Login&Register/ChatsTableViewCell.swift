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
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.layer.cornerRadius = 30
        self.layer.shadowColor=UIColor.lightGray.cgColor
        self.layer.shadowOffset = CGSize(width:-3, height:3)
        self.layer.shadowRadius=4
        self.layer.shadowOpacity=0.4
        self.clipsToBounds=false
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
