        // DataBase 
        var mysql = require("mysql");
        var fs = require('fs');
        var con = mysql.createConnection({
          host: "localhost",
          user: "root",
          password: "kchatadmin",
          database: "kchat"
        });
        con.connect(function(err){
          if(err){
            console.log('Error connecting to Db');
            return;
          }
          console.log('Connection established');
        });

        module.exports = {
            login : function(req, res) {

                var queryString = 'SELECT * FROM Users WHERE username = ' + 
                               con.escape(req.body.username) + ' AND password = ' + 
                               con.escape(req.body.password) ;

                con.query(queryString, function(err, rows, fields) {
                    if (err) { 
                        // throw err; 
                        res.end({ status: 'fail' });
                    }
                    if (rows.length == 1) {
                        res.json(rows[0]);
                    

                    } else {
                        res.json({ status: 'failed' });
                    }
                });
            },
            register : function(req, res) {

                var queryString
                if (req.body.about == "") {
                    queryString = "INSERT INTO Users (`user_id`, `name`, `email`, `username`, `password`, `phone_number`, `blocked`, `session`, `profile_picture`, `biography`) VALUES (NULL, " + 
                                    con.escape(req.body.fullName) + ", " + con.escape(req.body.email) + ", "+ con.escape(req.body.username) + 
                                    ", " + con.escape(req.body.pwd) + ", " + con.escape(req.body.phoneNo) +", 0, 0, NULL, NULL)";
                } else {
                    queryString = "INSERT INTO Users (`user_id`, `name`, `email`, `username`, `password`, `phone_number`, `blocked`, `session`, `profile_picture`, `biography`) VALUES (NULL, " + 
                                    con.escape(req.body.fullName) + ", " + con.escape(req.body.email) + ", "+ con.escape(req.body.username) + 
                                    ", " + con.escape(req.body.pwd) + ", " + con.escape(req.body.phoneNo) +", 0, 0, NULL, " + con.escape(req.body.biography) + ")";   
                }
                
                con.query(queryString, function(err, rows, fields) {
                    if (err) {
                        if (err.code == 'ER_DUP_ENTRY') {
                            res.end('duplicate');
                        }
                    } else if (rows != null && rows.insertId > 0) {
                console.log('reached here 1');
                        res.end('success ' + rows.insertId);
                    } else {
                        console.log('fail');

                        res.end('fail');
                    }
                });
            },
            changePass : function(req, res) {

                var queryString = 'UPDATE Users SET password = ' + con.escape(req.body.newPassword) +
                                    ' WHERE Users.username = ' + con.escape(req.body.username) +
                                    ' AND Users.password = ' + con.escape(req.body.password) + ';';

                con.query(queryString, function(err, rows, fields) {
                    if (err) {
                        res.end('fail');
                    } else if (rows != null && rows.affectedRows > 0) {
                        res.end('success');
                    } else if (rows != null && rows.affectedRows == 0) {
                        res.end('invalid');
                    } else {
                        res.end('fail');
                    }
                });
            },

            imageUpload : function(req, res) {

              var requestType = req.body.request;
              var sentJsonImage = req.body.json; // full json to deserialise
              obj = JSON.parse(sentJsonImage);
              res.end(obj.sender);  //usersImage

              var buf = new Buffer(obj.usersImage, 'base64');
              fs.writeFile('/var/www/html/profile_pictures/profile_picture'+obj.sender+'.jpg', buf,function(error) {
                   if (error) {
                     console.error("write error:  " + error.message);
                   } else {
                     console.log("Successful image Write");
                   }
              });

              queryString = "UPDATE Users SET profile_picture = 'profile_picture" + obj.sender + ".jpg' WHERE user_id = " + obj.sender + ";";                
              con.query(queryString, function(err, rows, fields) {
                if (err) {
                  console.log(err);
                } else {
                  console.log("A MERS BAAAAA");
                }
              });
            },

            groupImageUpload : function(req, res) {

                var requestType = req.body.request;
                var sentJsonImage = req.body.json; // full json to deserialise
                obj = JSON.parse(sentJsonImage);
                res.end(obj.sender);  //usersImage

                var buf = new Buffer(obj.usersImage, 'base64');
                fs.writeFile('/var/www/html/group_pictures/group_picture'+obj.sender+'.jpg', buf,function(error) {
                     if (error) {
                       console.error("write error:  " + error.message);
                     } else {
                       console.log("Successful image Write");
                     }
                });
            },

             bufferUpload : function(req, res) {

                var type = req.body.type; // this is an identifier like 
                var contents = req.body.contents;

                console.log(type);
                console.log(contents);

                var obj = JSON.parse(contents);

                 res.forEach(function(element) {
                var sender = res.end(obj.sender);  //message sender
                var receiver = res.end(obj.receiver);  //message receiver
                var message = res.end(obj.message);  //message 
                var timestamp = res.end(obj.timestamp);  //message timestamp
                var messageType = res.end(obj.type);  //message type  private / group

                if(messageType=="private"){
                //SQL Query goes here
                console.log("Successful stored private message");

                }else if(messageType=="group"){
                //SQL Query goes here
                console.log("Successful stored group message");

                }else{
                console.log("Error storing message");

                }
              
                });
            },

            iOSImageUpload : function(req, res) {
                var sentJsonImage = req.body.image;
                var sender = req.body.sender; // full json to deserialise
                var photoType = req.body.photoType;
                console.log(photoType);
                sentJsonImage = sentJsonImage.replace(/ /g, '+');
                var buf = new Buffer(sentJsonImage, 'base64');
                console.log(photoType + '_pictures/' + photoType + '_picture'+sender+'.jpg');
                fs.writeFile('/var/www/html/' + photoType + '_pictures/' + photoType + '_picture'+sender+'.jpg', buf,function(error) {
                  if (error) {
                    res.end('fail');
                    // console.error("write error:  " + error.message);
                  } else {
                    res.end('success');
                    console.log("Successful image Write");
                  }
                });

                if (photoType == "profile") {
                  queryString = "UPDATE Users SET profile_picture = 'profile_picture" + sender + ".jpg' WHERE user_id = " + sender + ";";                
                  con.query(queryString, function(err, rows, fields) {
                    if (err) {
                      console.log(err);
                    } else {
                      console.log("A MERS BAAAAA");
                    }
                  });
                }
            },

          changeUsername : function(req, res) {

            var queryString = 'UPDATE Users SET username = ' + con.escape(req.body.newUsername) +
                                    ' WHERE Users.user_id = ' + con.escape(req.body.id) +';';
            },
          changeEmail : function(req, res) {
            var queryString = 'UPDATE Users SET email = ' + con.escape(req.body.newEmail) +
                                    ' WHERE Users.user_id = ' + con.escape(req.body.id) +';';    
            },
          changePhoneNumber : function(req, res) {
            var queryString = 'UPDATE Users SET phone_number = ' + con.escape(req.body.newPhoneNumber) +
                      ' WHERE Users.user_id = ' + con.escape(req.body.id) +';';
            },
          changeBiography : function(req, res) {
            var queryString = 'UPDATE Users SET biography = ' + con.escape(req.body.newBiography) +
                      ' WHERE Users.user_id = ' + con.escape(req.body.id) +';';
            },
            contacts : function(req, res) {
                var queryString = 'SELECT contact_id, timestmp, Users.* ' + 
                                    'FROM Contacts JOIN Users ON Users.user_id = Contacts.added_contact_id ' + 
                                    'WHERE owner_id = ' + con.escape(req.body.userId) + ' AND invite = 0 ' + ' UNION ' +
                                    'SELECT contact_id, timestmp, Users.* ' + 
                                    'FROM Contacts JOIN Users ON Users.user_id = Contacts.owner_id ' +
                                    'WHERE added_contact_id = ' + con.escape(req.body.userId) + ' AND invite = 0 ORDER BY name;';
                
                con.query(queryString, function(err, rows, fields) {
                    if (err) { 
                        throw err;
                        res.end('fail');
                    } else {
                        res.json(rows);
                    }
                        // res.json({ status: 'failed' });
                });
            }
        }