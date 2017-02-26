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
    @IBOutlet weak var removeButton: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
        func configureCell(_ fullName: String) {
            fullNameLabel.text = fullName
        }
    
}
