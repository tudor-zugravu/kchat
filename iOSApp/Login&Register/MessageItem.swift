//
//  MessageItem.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/19.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

    //message type
    enum ChatType {
        case mine
        case someone
    }
    
    class MessageItem {
        //portrait
        var logo:String
        //message timestamp
        var date:Date
        //mesage type
        var mtype:ChatType
        //片content information, lable or picture
        var view:UIView
        //edge
        var insets:UIEdgeInsets
        
        //set my text message edge
        class func getTextInsetsMine() -> UIEdgeInsets {
            return UIEdgeInsets(top:5, left:10, bottom:11, right:17)
        }
        
        //set others text message edge
        class func getTextInsetsSomeone() -> UIEdgeInsets {
            return UIEdgeInsets(top:5, left:15, bottom:11, right:10)
        }
        
        //set my picture message edge
        class func getImageInsetsMine() -> UIEdgeInsets {
            return UIEdgeInsets(top:11, left:13, bottom:16, right:22)
        }
        
        //set others picture message edge
        class func getImageInsetsSomeone() -> UIEdgeInsets {
            return UIEdgeInsets(top:11, left:13, bottom:16, right:22)
        }
        
        //construct text message
        convenience init(body:NSString, logo:String, date:Date, mtype:ChatType) {
            let font =  UIFont.boldSystemFont(ofSize: 12)
            
            let width =  225, height = 10000.0
            
            let atts =  [NSFontAttributeName: font]
            
            let size =  body.boundingRect(with: CGSize(width: CGFloat(width), height: CGFloat(height)),
                                          options: .usesLineFragmentOrigin, attributes:atts, context:nil)
            
            let label =  UILabel(frame:CGRect(x: 0, y: 0, width: size.size.width, height: size.size.height))
            
            label.numberOfLines = 0
            label.lineBreakMode = NSLineBreakMode.byWordWrapping
            label.text = (body.length != 0 ? body as String : "")
            label.font = font
            label.backgroundColor = UIColor.clear
            
            let insets:UIEdgeInsets =  (mtype == ChatType.mine ?
                MessageItem.getTextInsetsMine() : MessageItem.getTextInsetsSomeone())
            
            self.init(logo:logo, date:date, mtype:mtype, view:label, insets:insets)
        }
        
        //It should be user information
        init(logo:String, date:Date, mtype:ChatType, view:UIView, insets:UIEdgeInsets) {
            self.view = view
            self.logo = logo
            self.date = date
            self.mtype = mtype
            self.insets = insets
        }

}
