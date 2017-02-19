//
//  MessagesViewController.swift
//  MessagesExtension
//
//  Created by 骧小爷 on 2017/2/16.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit


class MessagesViewController: UIViewController, ChatDataSource {
        
        var Chats:Array<MessageItem>!
        var tableView:TableView!
        
        override func viewDidLoad() {
            super.viewDidLoad()
            
            setupChatTable()
        }
        
        /*Create table and data*/
        func setupChatTable() {
            self.tableView = TableView(frame:CGRect(x: 0, y: 20,
                                                    width: self.view.frame.size.width, height: self.view.frame.size.height - 20), style: .plain)
            
            //create a re-used tableView
            self.tableView!.register(TableViewCell.self, forCellReuseIdentifier: "MsgCell")
            // person head picture
            let me = "xiaoming.png"
            let you = "xiaohua.png"
            
            let first =  MessageItem(body:"hi dud, do you like this picture！", logo:me,
                                     date:Date(timeIntervalSinceNow:-600), mtype:ChatType.mine)
            
            let second =  MessageItem(body:"UIImage(named:luguhu.jpeg",logo:me,
                                      date:Date(timeIntervalSinceNow:-290), mtype:ChatType.mine)
            
            let third =  MessageItem(body:"amazing！",logo:you,
                                     date:Date(timeIntervalSinceNow:-60), mtype:ChatType.someone)
            
            let fouth =  MessageItem(body:"Let's go together next time！",logo:me,
                                     date:Date(timeIntervalSinceNow:-20), mtype:ChatType.mine)
            
            let fifth =  MessageItem(body:"Of course！",logo:you,
                                     date:Date(timeIntervalSinceNow:0), mtype:ChatType.someone)
            
            Chats = [first,second, third, fouth, fifth]
            
            self.tableView.chatDataSource = self
            self.tableView.reloadData()
            self.view.addSubview(self.tableView)
        }
        
        /*return the number of line in tableView*/
        func rowsForChatTable(_ tableView:TableView) -> Int {
            return self.Chats.count
        }
        
        /*return content in specfic line*/
        func chatTableView(_ tableView:TableView, dataForRow row:Int) -> MessageItem {
            return Chats[row]
        }
        
        override func didReceiveMemoryWarning() {
            super.didReceiveMemoryWarning()
        }
}



