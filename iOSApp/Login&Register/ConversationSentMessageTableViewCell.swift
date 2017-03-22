//
//  ConversationSentMessageTableViewCell.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 3/15/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import UIKit

class ConversationSentMessageTableViewCell: UITableViewCell {

    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var timestampLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    func configureCell(_ message: String, _ timestamp: String) {
        messageLabel.text = message
        timestampLabel.text = timestamp
//        messageLabel.sizeToFit()
        
    }
}
