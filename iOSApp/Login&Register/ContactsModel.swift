//
//  ContactsModel.swift
//  Login&Register
//
//  Created by Tudor Zugravu on 2/21/17.
//  Copyright © 2017 骧小爷. All rights reserved.
//

import Foundation

protocol ContactsModelProtocol: class {
    func itemsDownloaded(_ items: [[ConferenceModel]])
    func itemAdded(_ status: NSString)
    func itemRemoved(_ status: NSString)
}


class AgendaModel: NSObject, URLSessionDataDelegate {
    
    //properties
    weak var delegate: AgendaModelProtocol!
    var data : NSMutableData = NSMutableData()
    
    func addConference(_ email: String, conferenceId: String) {
        
        let url: URL = URL(string: "http://zugravu.xyz/services/adaugare_programare.php")!
        let session = URLSession.shared
        
        let request = NSMutableURLRequest(url: url)
        request.httpMethod = "POST"
        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
        
        let paramString = "email=\(email)&conferenceId=\(conferenceId)"
        request.httpBody = paramString.data(using: String.Encoding.utf8)
        
        let task = session.dataTask(with: request, completionHandler: {
            (
            data, response, error) in
            
            guard let _:Data = data, let _:URLResponse = response, error == nil else {
                print("error")
                return
            }
            
            let dataString = NSString(data: data!, encoding: String.Encoding.utf8)
            
            DispatchQueue.main.async(execute: { () -> Void in
                
                self.delegate.itemAdded(dataString!)
            })
            
        })
        
        task.resume()
        
    }
    
    func removeConference(_ email: String, conferenceId: String) {
        
        let url: URL = URL(string: "http://zugravu.xyz/services/stergere_programare.php")!
        let session = URLSession.shared
        
        let request = NSMutableURLRequest(url: url)
        request.httpMethod = "POST"
        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
        
        let paramString = "email=\(email)&conferenceId=\(conferenceId)"
        request.httpBody = paramString.data(using: String.Encoding.utf8)
        
        let task = session.dataTask(with: request, completionHandler: {
            (
            data, response, error) in
            
            guard let _:Data = data, let _:URLResponse = response, error == nil else {
                print("error")
                return
            }
            
            let dataString = NSString(data: data!, encoding: String.Encoding.utf8)
            
            DispatchQueue.main.async(execute: { () -> Void in
                
                self.delegate.itemRemoved(dataString!)
            })
            
        })
        
        task.resume()
        
    }
    
    func downloadItems(_ email: String, general: Bool) {
        
        self.data = NSMutableData()
        
        let url: URL
        let paramString: String
        
        if email == "" {
            url = URL(string: "http://zugravu.xyz/services/conferinte.php")!
            paramString = ""
        } else {
            if general == true {
                url = URL(string: "http://zugravu.xyz/services/conferinte_utilizator_general.php")!
            } else {
                url = URL(string: "http://zugravu.xyz/services/conferinte_utilizator.php")!
            }
            paramString = "email=\(email)"
        }
        
        let session = URLSession.shared
        
        let request = NSMutableURLRequest(url: url)
        request.httpMethod = "POST"
        request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData
        request.httpBody = paramString.data(using: String.Encoding.utf8)
        
        let task = session.dataTask(with: request, completionHandler: {
            (
            data, response, error) in
            
            guard let _:Data = data, let _:URLResponse = response, error == nil else {
                print("error")
                return
            }
            
            self.data.append(data!)
            
            self.parseJSON()
            
        })
        
        task.resume()
        
    }
    
    func parseJSON() {
        
        var jsonResult: NSMutableArray = NSMutableArray()
        
        do{
            jsonResult = try JSONSerialization.jsonObject(with: self.data as Data, options:JSONSerialization.ReadingOptions.allowFragments) as! NSMutableArray
            
        } catch let error as NSError {
            print(error)
            
        }
        
        var jsonElement: NSDictionary = NSDictionary()
        var conferences: [[ConferenceModel]] = []
        var dayConferences: [ConferenceModel] = []
        
        if let empty = jsonResult[0] as? String {
            if empty == "empty" {
                print("da")
            } else {
                print("nu!!!!!")
            }
        } else {
            if jsonResult.count != 0 {
                
                for i in 0 ..< jsonResult.count
                {
                    
                    jsonElement = jsonResult[i] as! NSDictionary
                    let conference = ConferenceModel()
                    
                    //the following insures none of the JsonElement values are nil through optional binding
                    if let id = jsonElement["id"] as? String,
                        let name = jsonElement["nume"] as? String,
                        let date = jsonElement["data"] as? String,
                        let hour = jsonElement["ora"] as? String,
                        let notificationDateTime = jsonElement["data_notificare"] as? String,
                        let speakers = jsonElement["oratori"] as? String,
                        let added = jsonElement["adaugat"] as? String,
                        let location = jsonElement["locatie"] as? String,
                        let about = jsonElement["descriere"] as? String
                    {
                        conference.id = id
                        conference.name = name
                        conference.date = date
                        conference.hour = hour
                        conference.notificationDateTime = notificationDateTime
                        conference.speakers = speakers
                        conference.added = added
                        conference.location = location
                        conference.about = about
                    }
                    
                    if (dayConferences.count != 0 && dayConferences[0].date != conference.date) {
                        conferences.append(dayConferences)
                        dayConferences = []
                        dayConferences.append(conference)
                    } else {
                        dayConferences.append(conference)
                    }
                    
                }
                
                conferences.append(dayConferences)
            }
        }
        
        DispatchQueue.main.async(execute: { () -> Void in
            
            self.delegate.itemsDownloaded(conferences)
        })
    }
}
