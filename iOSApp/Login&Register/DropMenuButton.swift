//
//  DropMenuButton.swift
//  Login&Register
//
//  Created by 骧小爷 on 2017/3/17.
//  Copyright © 2017年 骧小爷. All rights reserved.
//

import UIKit

class DropMenuButton: UIButton, UITableViewDelegate, UITableViewDataSource
{
    var items = [String]()
    var table = UITableView()
    var act = [() -> (Void)]()
    
    var superSuperView = UIView()
    
    func showItems()
    {
        
        fixLayout()
        
        if(table.alpha == 0)
        {
            self.layer.zPosition = 1
            UIView.animate(withDuration: 0.3
                , animations: {
                    self.table.alpha = 1;
            })
            
        }
        
    }
    
    
    func initMenu(_ items: [String], actions: [() -> (Void)])
    {
        self.items = items
        self.act = actions
        
        var resp = self as UIResponder
        
        while !(resp.isKind(of: UIViewController.self) || (resp.isKind(of: UITableViewCell.self))) && resp.next != nil
        {
            resp = resp.next!
            
        }
        
        if let vc = resp as? UIViewController{
            superSuperView = vc.view
        }
        else if let vc = resp as? UITableViewCell{
            superSuperView = vc
        }
        
        table = UITableView()
        table.rowHeight = self.frame.height
        table.delegate = self
        table.dataSource = self
        table.isUserInteractionEnabled = true
        table.alpha = 0
        table.separatorColor = self.backgroundColor
        superSuperView.addSubview(table)
        self.addTarget(self, action:#selector(DropMenuButton.showItems), for: .touchUpInside)
        
        
    }
    
    
    func fixLayout()
    {
        
        var tableFrameHeight = CGFloat()
        
        tableFrameHeight = self.frame.height * CGFloat(self.act.count) - 1
        table.layer.cornerRadius = 10
        table.frame = CGRect(x: 200, y: 20 + self.frame.height, width: 150, height:tableFrameHeight)
        table.rowHeight = self.frame.height
        table.layer.borderColor = UIColor.lightGray.cgColor
        table.layer.borderWidth = 1
        table.layer.shadowOpacity = 0.6
        table.clipsToBounds = true

        table.reloadData()
        
    }
    
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
//        self.setNeedsDisplay()
        fixLayout()
        
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int
    {
        
        return items.count
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath)
    {
        
        self.act[indexPath.row]()
        showItems()
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        
        let itemLabel = UILabel(frame: CGRect(x: 0, y: 0, width: 150, height: self.frame.height))
        itemLabel.textAlignment = NSTextAlignment.center
        itemLabel.text = items[(indexPath as NSIndexPath).row]
        itemLabel.font = self.titleLabel?.font
        itemLabel.textColor = self.backgroundColor
        
        let bgColorView = UIView()
        bgColorView.backgroundColor = UIColor.red
        
        let cell = UITableViewCell(frame: CGRect(x: 0, y: 0, width: 200, height: self.frame.height))
        cell.backgroundColor = UIColor.white
        cell.selectedBackgroundView = bgColorView
        cell.separatorInset = UIEdgeInsetsMake(0, self.frame.width, 0, self.frame.width)
        cell.addSubview(itemLabel)
        
        
        return cell
    }
    
}
