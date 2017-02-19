//
//  MainTableViewCell.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/19.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class MainTableViewCell: UITableViewCell {

    @IBOutlet weak var headPicture: UIImageView!
    @IBOutlet weak var nameLable: UILabel!
    @IBOutlet weak var subMessageLable: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
