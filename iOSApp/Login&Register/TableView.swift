//
//  TableView.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/19.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class TableView: UITableView, UITableViewDelegate, UITableViewDataSource{
    //store all message
    var bubbleSection:Array<MessageItem>!
    //exchange data with MessageViewController 
    var chatDataSource:ChatDataSource!
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame:frame,  style:style)
        
        self.bubbleSection = Array<MessageItem>()
        self.backgroundColor = UIColor.clear
        self.separatorStyle = UITableViewCellSeparatorStyle.none
        self.delegate = self
        self.dataSource = self
    }
    
    override func reloadData() {
        self.showsVerticalScrollIndicator = false
        self.showsHorizontalScrollIndicator = false
        
        var count =  0
        if ((self.chatDataSource != nil))
        {
            count = self.chatDataSource.rowsForChatTable(self)
            
            if(count > 0)
            {
                
                for i in 0 ..< count
                {
                    let object =  self.chatDataSource.chatTableView(self, dataForRow:i)
                    bubbleSection.append(object)
                }
                
                //order in date
                bubbleSection.sort(by: {
                    $0.date.timeIntervalSince1970 < $1.date.timeIntervalSince1970
                })
            }
        }
        super.reloadData()
    }
    
    //return the signal of branch. In this example, that is 1.
    func numberOfSections(in tableView:UITableView)->Int {
        return 1
    }
    
    //return the number of specific branch
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (section >= self.bubbleSection.count)
        {
            return 0
        }
        
        return self.bubbleSection.count
    }
    
    //make sure the height of cell
    func tableView(_ tableView:UITableView,heightForRowAt indexPath:IndexPath)
        -> CGFloat {
            let data =  self.bubbleSection[indexPath.row]
            return max(data.insets.top + data.view.frame.size.height + data.insets.bottom, 52)
    }
    
    //return TableViewCell
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath)
        -> UITableViewCell {
            let cellId = "MsgCell"
            let data =  self.bubbleSection[indexPath.row]
            let cell =  TableViewCell(data:data, reuseIdentifier:cellId)
            return cell
    }
}


    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */


