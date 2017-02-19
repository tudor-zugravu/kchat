//
//  ChatDataSource.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/2/19.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import Foundation

/*
 protocol of data provided
 */
protocol ChatDataSource
{
    
    func rowsForChatTable( _ tableView:TableView) -> Int
    
    func chatTableView(_ tableView:TableView, dataForRow:Int)-> MessageItem
}

