// Socket.io server listens to our app


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
  console.log('Connection established for message stream db \n\n\n');
});

function addUserToRoom (io, roomId, socket) {
  if (rooms.indexOf(roomId) != -1) {
    socket.room = roomId;
    socket.join(roomId);
    io.sockets.in(socket.room).emit('update_chat', 'SERVER: ' + socket.username + ' has connected to this room'); 
  } else {
    socket.emit('update_chat', 'SERVER: Please enter valid code.');
  }
}

var sockets = [];
var rooms = [];
var clients = [];

module.exports = function(io){

  io.on('connection', function(socket) {

    socket.emit('connect', 'connected');
    console.log("connected");

    socket.on('authenticate', function (userId, userFullName) {
      if (sockets["" + userId] != null) {
        console.log("disconnecting " + userFullName);
        sockets["" + userId].emit('disconnected', 'disconnect');
        delete clients[sockets["" + userId].id];
        delete sockets[sockets["" + userId].username];
      }
      console.log("authenticated " + userFullName);
      socket.userFullName = userFullName;
      socket.username = "" + userId;
      clients[socket.id] = "" + userId;
      sockets["" + userId] = socket;
      socket.emit('authenticated', socket.username);
    });

    socket.on('create_private_room', function (receiverId, userId) {
      if(receiverId != null && receiverId != "" && userId != null && userId != "") {
        var queryString = "SELECT contact_id FROM Contacts WHERE owner_id = " + userId + " AND added_contact_id = " + receiverId + 
        " UNION SELECT contact_id FROM Contacts WHERE added_contact_id = " + userId + " AND owner_id = " + receiverId + ";";
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log("LOCATION: create_private_room");
            console.log(err);
            socket.emit('update_chat', 'SERVER: Room not created, you are not friends');
            socket.emit('private_room_created', 'fail');
          } else {
            var new_room = "private" + rows[0].contact_id;
            if (rooms.indexOf(new_room) == -1) {
              rooms.push(new_room);
            }
            socket.emit('update_chat', 'SERVER Your room is ready, invite someone using this ID:' + new_room);

            addUserToRoom(io, new_room, sockets["" + userId]);
            socket.emit('private_room_created', new_room);
          }
        });
      }
    });

    socket.on('create_group_room', function (groupId) {
      if(groupId != null && groupId != "") {
        var new_room = "group" + groupId;
        if (rooms.indexOf(new_room) == -1) {
          rooms.push(new_room);
        }
        socket.emit('update_chat', 'SERVER Your room is ready, invite someone using this ID:' + new_room);
        
        addUserToRoom(io, new_room, sockets["" + socket.username]);

        socket.emit('group_room_created', new_room);
      }
    });

    socket.on('send_chat', function (msg) {

      sqlMsg = msg.replace("'","\\'");
      contactId = socket.room.replace("private","");

      var queryString = 'SELECT CASE WHEN owner_id = ' + socket.username + ' THEN added_contact_id ' +
                        'WHEN added_contact_id = ' + socket.username + ' THEN owner_id ELSE NULL END as receiver FROM Contacts WHERE contact_id = ' + contactId + ';';
      con.query(queryString, function(err, rows, fields) {
        if (err) {
          console.log(err);
        } else {
          var receiver = rows[0].receiver;

          var queryString = 'INSERT INTO Private_messages (message_id, sender_id, receiver_id, message, timestmp) ' + 
                            'VALUES(NULL, ' + socket.username + ',' +  receiver + ",'" + sqlMsg + "', CURRENT_TIMESTAMP);";

          con.query(queryString, function(err, rows, fields) {
            if (err) {
              console.log(err);
            } else {
              var insertId = rows.insertId;

              queryString = "SELECT DATE_FORMAT(timestmp, '%d-%m-%Y %H:%i:%s') as timestmp FROM `Private_messages` WHERE message_id = " + insertId + ';';

              con.query(queryString, function(err, rows, fields) {
                if (err) {
                  console.log(err);
                } else {
                  if(sockets["" + receiver] != null) {
                    addUserToRoom(io, socket.room, sockets["" + receiver]);
                  } else {
                    console.log("socket " + receiver + " does not exist");
                  }

                  io.sockets.in(socket.room).emit('global_private_messages', "" + socket.room, insertId, "" + socket.userFullName, "" + msg, "" + rows[0].timestmp); 
                  io.sockets.in(socket.room).emit(socket.room, insertId, "" + socket.username, "" + msg, "" + rows[0].timestmp); 
                }
              });
            }
          });

        }
      });
    });

    socket.on('send_group_chat', function (msg) {

      sqlMsg = msg.replace("'","\\'");
      groupId = socket.room.replace("group","");
      console.log("SEND GROUP CHAT - msg: " + sqlMsg + " group: " + groupId);
      var queryString = 'INSERT INTO Group_messages (message_id, user_id, group_id, message, timestmp) ' + 
                        'VALUES (NULL, ' + socket.username + ", " + groupId + ", '" + sqlMsg + "', CURRENT_TIMESTAMP);";
      con.query(queryString, function(err, rows, fields) {
        if (err) {
          console.log(err);
        } else {
          var insertId = rows.insertId;
          console.log("SEND GROUP CHAT - insertid: " + insertId);
          queryString = "SELECT DATE_FORMAT(timestmp, '%d-%m-%Y %H:%i:%s') as timestmp FROM `Group_messages` WHERE message_id = " + insertId + ';';
          con.query(queryString, function(err, rows, fields) {
            if (err) {
              console.log(err); 
            } else {
              var timestamp = rows[0].timestmp;
              console.log("SEND GROUP CHAT - timestmp: " + timestamp);
              var queryString = 'SELECT Corelation.user_id FROM Corelation JOIN Users ON Users.user_id = Corelation.user_id' + ' WHERE group_id = ' + groupId + ';';
              con.query(queryString, function(err, rows, fields) {
                if (err) {
                  console.log(err);
                } else {
                  console.log("SEND GROUP CHAT - corelations");
                  rows.forEach(function(element) {
                    if(sockets["" + element.user_id] != null) {
                      console.log(element.user_id + " was added to group " + groupId);
                      addUserToRoom(io, socket.room, sockets["" + element.user_id]);
                    } else {
                      console.log("socket " + element.user_id + " does not exist");
                    }
                  });
                }
                console.log("SEND GROUP CHAT - message sent");
                console.log(timestamp);
                io.sockets.in(socket.room).emit('global_private_messages', "" + socket.room, insertId, "" + socket.userFullName, "" + msg, "" + timestamp); 
                io.sockets.in(socket.room).emit(socket.room, insertId, "" + socket.username, "" + msg, "" + timestamp); 
              }); 
            }
          });
        }
      });
    });

    socket.on('disconnect', function () {
      if(sockets[socket.username] != null) {
        if(socket.id == sockets[socket.username].id) {
          delete clients[socket.id];
          delete sockets[socket.username];
          if (socket.username !== undefined) {
            console.log(socket.username + " has closed the connection");
            socket.broadcast.emit('updatechat', 'SERVER: ' + socket.username + ' has disconnected');
            socket.leave(socket.room);
          }
        }
      } else {
        console.log("someone is trying to crash the server " + socket.id);
      }
      socket.disconnect(true);
    });

   socket.on('search_user_filter', function(username, userId){ // used for searching users in the database

     if(username != null && username != "") {
       var username = con.escape(username + "%");
       var queryString = "SELECT user_id, username, name FROM Users " +
       "WHERE user_id <> " + userId + " " + 
       "AND user_id NOT IN (SELECT added_contact_id FROM Contacts WHERE owner_id = "  + userId + ")" + 
       "AND user_id NOT IN (SELECT owner_id FROM Contacts WHERE added_contact_id = "  + userId + ")" + 
       "AND name LIKE " + username + " ORDER BY name;";
       con.query(queryString, function(err, rows, fields) {
         if (err) {
           console.log(err);
           socket.emit("search_user_received", 'fail');
         } else {
           socket.emit("search_user_received", JSON.stringify(rows));
         }
       });
     } else {
       socket.emit("search_user_received", 'fail');
     }
   });

    socket.on('send_contact_request', function(sendersId, sendersName, receiversId){ // used for searching users in the database
      if(sendersId != null && sendersId != "" && receiversId != null && receiversId != "") {
        var queryString = "INSERT INTO Contacts (contact_id, owner_id, added_contact_id, invite, timestmp) " +
                          "VALUES (NULL, " + sendersId + ", " + receiversId + ", '1', CURRENT_TIMESTAMP);";
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log(err);
            socket.emit("sent_request", 'fail');
          } else {
            socket.emit("sent_request", 'success');
            if (sockets["" + receiversId] != null) {
              sockets["" + receiversId].emit('you_received_contact_request', '' + sendersName);
            }
          }
        });
      }
    });

   socket.on('sent_contact_requests', function(userId){ // used for searching users in the database

     if(userId != null && userId != "") {
       var queryString = 'SELECT contact_id, timestmp, Users.* ' + 
       'FROM Contacts JOIN Users ON Users.user_id = Contacts.added_contact_id ' + 
       'WHERE owner_id = ' + userId + ' AND invite = 1;';
       con.query(queryString, function(err, rows, fields) {
         if (err) {
           console.log(err);
           socket.emit("sent_requests", 'fail');
         } else {
           socket.emit("sent_requests", JSON.stringify(rows));
         }
       });
     }
   });

   socket.on('delete_contact_request', function(sendersId, receiversId, receiver) { // used for searching users in the database
     if(sendersId != null && sendersId != "" && receiversId != null && receiversId != "" && receiver != null && receiver != "") {
       var queryString = "DELETE FROM Contacts WHERE Contacts.owner_id = " + sendersId +
       " AND Contacts.added_contact_id = " + receiversId + ";";
       con.query(queryString, function(err, rows, fields) {
         if (err) {
           console.log(err);
           socket.emit('request_deleted', 'fail');
         } else {
          socket.emit("request_deleted", receiver);
          if (sockets["" + receiversId] != null) {
            sockets["" + receiversId].emit('no_more_contact_request', '' + sendersId);
          }
        }
      });
     }
   });

   socket.on('received_contact_requests', function(userId){ // used for searching users in the database

     if(userId != null && userId != "") {
       var queryString = 'SELECT contact_id, timestmp, Users.* ' + 
       'FROM Contacts JOIN Users ON Users.user_id = Contacts.owner_id ' + 
       'WHERE added_contact_id = ' + userId + ' AND invite = 1;';
       con.query(queryString, function(err, rows, fields) {
         if (err) {
           console.log(err);
           socket.emit("received_requests", 'fail');
         } else {
           socket.emit("received_requests", JSON.stringify(rows));
         }
       });
     }
   });

   socket.on('accept_contact_request', function(sendersId, receiversId, receiver) { // used for searching users in the database
     if(sendersId != null && sendersId != "" && receiversId != null && receiversId != "" && receiver != null && receiver != "") {
       var queryString = "UPDATE Contacts SET invite = '0' WHERE Contacts.owner_id = " + sendersId +
       " AND Contacts.added_contact_id = " + receiversId + ";";
       con.query(queryString, function(err, rows, fields) {
         if (err) {
           console.log(err);
           socket.emit('request_accepted', 'fail');
         } else {
          socket.emit("request_accepted", receiver);
          if (sockets["" + sendersId] != null) {
            sockets["" + sendersId].emit('accepted_my_contact_request', '' + receiversId);
          }
        }
      });
     }
   });

   socket.on('get_chats', function(userId){ // used for searching users in the database
     if(userId != null && userId != "") {
       var queryString = 'SELECT users1.user_id as receiverId, users1.name as receiverName, users1.profile_picture as receiverProfilePicture, ' + 
       'users2.user_id as senderId, users2.name as senderName, users2.profile_picture as senderProfilePicture, message, timestmp ' + 
       'FROM Private_messages JOIN Users users1 ON users1.user_id = Private_messages.receiver_id ' + 
       'JOIN Users users2 ON users2.user_id = Private_messages.sender_id ' + 
       'WHERE message_id IN (SELECT MAX(message_id) ' + 
        'FROM (SELECT receiver_id as receiverId, message_id, timestmp ' + 
          'FROM Private_messages ' + 
          'WHERE sender_id = ' + userId + 
          ' UNION SELECT sender_id as receiverId, message_id, timestmp ' +
          'FROM Private_messages ' +
          'WHERE receiver_id = ' + userId + ') chats ' + 
   'GROUP BY receiverID)' + 
   'ORDER BY timestmp DESC;';
   con.query(queryString, function(err, rows, fields) {
     if (err) {
       console.log(err);
       socket.emit("sent_chats", 'fail');
     } else {
       socket.emit("sent_chats", JSON.stringify(rows));
     }
   });
 }
});

    socket.on('get_recent_messages', function(senderId, receiverId, limit){ // used for searching users in the database
     if(senderId != null && senderId != "" && receiverId != null && receiverId != "" && limit != null && limit != "") {
       var queryString = "SELECT message_id, sender_id, receiver_id, message, DATE_FORMAT(timestmp, '%d-%m-%Y %H:%i:%s') as timestmp FROM Private_messages" +
       ' WHERE (sender_id = ' + senderId + ' AND receiver_id = ' + receiverId + 
        ') OR (sender_id = ' + receiverId + ' AND receiver_id = ' + senderId + 
        ') ORDER BY timestmp DESC LIMIT ' + limit + ';';
    con.query(queryString, function(err, rows, fields) {
     if (err) {
       console.log(err);
       socket.emit("send_recent_messages", 'fail');
     } else {
       socket.emit("send_recent_messages", JSON.stringify(rows));
     }
   });
  }
});

    socket.on('get_group_chats', function(userId){ // used for searching users in the database
      if(userId != null && userId != "") {
        var queryString = 'SELECT Group_messages.group_id, name, group_picture, description, message, timestmp, owner ' + 
                          'FROM Group_messages JOIN Groups ON Groups.group_id = Group_messages.group_id ' +
                          'WHERE message_id IN (SELECT MAX(message_id) FROM Group_messages ' +
                                                'WHERE group_id IN (SELECT group_id FROM Corelation WHERE user_id = ' + userId + ') ' + 
                                                'GROUP BY group_id) ' + 
                          "UNION (SELECT Corelation.group_id, name, group_picture, description, NULL, NULL, owner " + 
                          "FROM Corelation JOIN Groups ON Groups.group_id = Corelation.group_id " + 
                          "WHERE user_id = " + userId + " AND Corelation.group_id NOT IN (SELECT group_id FROM Group_messages)) ORDER BY timestmp DESC;";

        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log(err);
            socket.emit("sent_group_chats", 'fail');
          } else {
            socket.emit("sent_group_chats", JSON.stringify(rows));
          }
        });
      }
    });

    socket.on('create_group', function(name, description, ownerId, group_picture, userIds) { // used for searching users in the database
      var queryString;
      if(name != null && name != "" && description != null && description != "" && ownerId != null && ownerId != "" && userIds != null) {
        queryString = "INSERT INTO Groups (group_id, name, description, owner, group_picture) " +
                      "VALUES (NULL, '" + name + "', '" + description + "', " + ownerId + ", NULL);";
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log("GROUP CREATED - INSERT");
            console.log(err);
            socket.emit("group_created", 'fail');
          } else {
            var insertId = rows.insertId;
            var success = true;
            queryString = "INSERT INTO Corelation (corelation_id, user_id, group_id, banned) " +
                          "VALUES (NULL, '" + ownerId + "', '" + insertId + "', 0);";        
            con.query(queryString, function(err, rows, fields) {
              if (err) {
                console.log("GROUP CREATED - COR OWNER");
                console.log(err);
                success = false;
              } 
            });

            if(group_picture != null && group_picture != "") {
              queryString = "UPDATE Groups SET group_picture = 'group_picture" + insertId + ".jpg' WHERE group_id = " + insertId + ";";        
              con.query(queryString, function(err, rows, fields) {
                if (err) {
                  console.log(err);
                  console.log("GROUP CREATED - PICTURE");
                }
              });
            }
            console.log(insertId);
            console.log(userIds);
            userIds.forEach(function(element) {
              queryString = "INSERT INTO Corelation (corelation_id, user_id, group_id, banned) " +
                            "VALUES (NULL, '" + element + "', '" + insertId + "', 0);";        
              con.query(queryString, function(err, rows, fields) {
                if (err) {
                  console.log("GROUP CREATED - COR - " + element);
                  console.log(err);
                  success = false;
                } else {
                  if (sockets["" + element] != null) {
                    sockets["" + element].emit('you_have_been_added_to_group', '' + insertId);
                  }
                }
              });
            });
            if (success) {
              console.log("GROUP CREATED - SUCCESS");
              socket.emit("group_created", "" + insertId);
            } else {
              console.log("GROUP CREATED - FAIL");
              socket.emit("group_created", 'fail');
            }
          }
        });
      }
    });

    socket.on('get_group_members', function(groupId) { // used for searching users in the database
      if(groupId != null && groupId != "") {
        var queryString = 'SELECT Corelation.user_id, name, username, profile_picture FROM Corelation JOIN Users ON Users.user_id = Corelation.user_id' + ' WHERE group_id = ' + groupId + ';';
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log(err);
            socket.emit("send_group_members", 'fail');
          } else {
            socket.emit("send_group_members", JSON.stringify(rows));
          }
        });
      }
    });

    socket.on('get_recent_group_messages', function(groupId, limit) { // used for searching users in the database
      if(groupId != null && groupId != "" && limit != null && limit != "") {
        var queryString = "SELECT message_id, user_id, message, DATE_FORMAT(timestmp, '%d-%m-%Y %H:%i:%s') as timestmp FROM Group_messages" +
                          ' WHERE group_id = ' + groupId + 
                          ' ORDER BY timestmp DESC LIMIT ' + limit + ';';
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log(err);
            socket.emit("send_recent_group_messages", 'fail');
          } else {
            socket.emit("send_recent_group_messages", JSON.stringify(rows));
          }
        });
      }
    });

    socket.on('leave_group', function(userId, groupId) { // used for searching users in the database
      if(userId != null && userId != "" && groupId != null && groupId != "") {

        var queryString = "UPDATE Corelation SET user_id = 1 WHERE user_id = " + userId + " AND group_id = " + groupId + ";";
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            if (err.code == 'ER_DUP_ENTRY') {
              // socket.emit("group_left", 'success');
            }
          }
        });
        var queryString = 'DELETE FROM Corelation WHERE user_id = ' + userId +' AND group_id = ' + groupId + ';';
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log("LEAVE GROUP - delete corelations");
          } 
        });
        queryString = "UPDATE Group_messages SET user_id = 1 WHERE user_id = " + userId +';';
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log("LEAVE GROUP - update group messages");
            console.log(err);
            socket.emit('group_left', 'fail');
          } else {
            socket.emit("group_left", 'success');
          }
        });
      }
    });

    socket.on('delete_contact', function(userId, otherUserId) { // used for searching users in the database
      if(userId != null && otherUserId != null) {
        var queryString = "DELETE FROM Private_messages WHERE (sender_id = " + userId + " AND receiver_id = " + otherUserId + ")" + 
                                                " OR (sender_id = " + otherUserId + " AND receiver_id = " + userId + ");";
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log("DELETE CONTACT - delete messages");
            console.log(err);
            socket.emit('contact_deleted', 'fail');
          } else {
            var queryString = "DELETE FROM Contacts WHERE (owner_id = " + userId + " AND added_contact_id = " + otherUserId + ")" + 
                                                    " OR (owner_id = " + otherUserId + " AND added_contact_id = " + userId + ");";
            con.query(queryString, function(err, rows, fields) {
              if (err) {
                console.log("DELETE CONTACT - delete contact");
                console.log(err);
                socket.emit('contact_deleted', 'fail');
              } else {
                socket.emit("contact_deleted", 'success');
                if (sockets["" + otherUserId] != null) {
                  sockets["" + otherUserId].emit('you_were_deleted', '' + userId);
                }
              }
            });
          }
        });
      }
    });

    socket.on('delete_account', function(userId) { // used for searching users in the database
      if(userId != null) {
        var queryString = "DELETE FROM Private_messages WHERE sender_id = " + userId + " OR receiver_id = " + userId + ";";
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log("DELETE ACCOUNT - delete messages");
            console.log(err);
            socket.emit('account_deleted', 'fail');
          } else {
            var queryString = 'SELECT CASE WHEN owner_id = ' + userId + ' THEN added_contact_id ' +
                              'WHEN added_contact_id = ' + userId + ' THEN owner_id ELSE NULL END as receiver FROM Contacts ' + 
                              'WHERE owner_id = ' + userId + ' OR added_contact_id = ' + userId + ';';
            con.query(queryString, function(err, rows, fields) {
              if (err) {
                console.log(err);
              } else {
                rows.forEach(function(element) {
                  if (sockets["" + element.receiver] != null) {
                    sockets["" + element.receiver].emit('you_were_deleted', '' + userId);
                  }
                });
              }
            });
            var queryString = 'DELETE FROM Contacts WHERE owner_id = ' + userId + ' OR added_contact_id = ' + userId + ';';
            con.query(queryString, function(err, rows, fields) {
              if (err) {
                console.log("DELETE ACCOUNT - delete contacts");
                console.log(err);
                socket.emit('account_deleted', 'fail');
              } else {
                var queryString = 'UPDATE Corelation SET user_id = 1 WHERE user_id = ' + userId +';';
                con.query(queryString, function(err, rows, fields) {
                  if (err) {
                    if (err.code == 'ER_DUP_ENTRY') {
                      // console.log("DELETE ACCOUNT - update corelations - duplicate");
                    }
                  }
                });

                var queryString = 'DELETE FROM Corelation WHERE user_id = ' + userId +';';
                con.query(queryString, function(err, rows, fields) {
                  if (err) {
                    console.log("DELETE ACCOUNT - delete corelations");
                  } 
                });

                queryString = "UPDATE Group_messages SET user_id = 1 WHERE user_id = " + userId +';';
                con.query(queryString, function(err, rows, fields) {
                  if (err) {
                    console.log("DELETE ACCOUNT - update group messages");
                    console.log(err);
                    socket.emit('account_deleted', 'fail');
                  } else {

                    var queryString = 'UPDATE Groups SET owner = 1 WHERE owner = ' + userId +';';
                    con.query(queryString, function(err, rows, fields) {
                      if (err) {
                        console.log("DELETE ACCOUNT - update groups owner");
                      }
                    });

                    var queryString = 'DELETE FROM Users WHERE user_id = ' + userId + ';';
                    con.query(queryString, function(err, rows, fields) {
                      if (err) {
                        console.log("DELETE ACCOUNT - delete user");
                        console.log(err);
                        socket.emit('account_deleted', 'fail');
                      } else {
                        socket.emit("account_deleted", 'success');
                      }
                    }); 
                  }
                });
              }
            });
          }
        });
      }
    });

    socket.on('add_to_group', function(groupId, userIds) { // used for searching users in the database
      var queryString;
      if(groupId != null && groupId != "" && userIds != null) {
        var success = true;
        userIds.forEach(function(element) {
          queryString = "INSERT INTO Corelation (corelation_id, user_id, group_id, banned) " +
                        "VALUES (NULL, '" + element + "', '" + groupId + "', 0);";        
          con.query(queryString, function(err, rows, fields) {
            if (err) {
              console.log(err);
              success = false;
            } else {
              if (sockets["" + element] != null) {
                console.log("" + element);
                sockets["" + element].emit('you_have_been_added_to_group', '' + groupId);
              }
            }
          });
        });

        if (success) {
          socket.emit("added_to_group", "success");
        } else {
          socket.emit("added_to_group", 'fail');
        }
      }
    });

    socket.on('delete_group', function(userId, groupId) { // used for searching users in the database
      if(userId != null && userId != "" && groupId != null && groupId != "") {
        var queryString = "SELECT owner FROM Groups WHERE group_id = " + groupId + ";";
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log("DELETE_GROUP - Get owner");
            console.log(err);
          } else {
            console.log(userId);
            console.log(groupId);
            if (rows[0].owner != userId) {
              socket.emit("group_deleted", "fail");
            } else {
              var queryString = 'SELECT user_id FROM Corelation WHERE group_id = ' + groupId + ';';
              con.query(queryString, function(err, rows, fields) {
                if (err) {
                  console.log("DELETE_GROUP - Emmiting group left");
                  console.log(err);
                } else {
                  rows.forEach(function(element) {
                    if (sockets["" + element.user_id] != null && element.user_id != userId) {
                      sockets["" + element.user_id].emit('you_were_deleted_from_group', '' + groupId);
                    }
                  });
                }
              });
              var queryString = "DELETE FROM Corelation WHERE group_id = " + groupId + ";";
              con.query(queryString, function(err, rows, fields) {
                if (err) {
                  console.log("DELETE_GROUP - Delete corelations");
                  console.log(err);
                  socket.emit("group_deleted", "fail");
                } else {
                  var queryString = "DELETE FROM Group_messages WHERE group_id = " + groupId + ";";
                  con.query(queryString, function(err, rows, fields) {
                    if (err) {
                      console.log("DELETE_GROUP - Delete group messages");
                      console.log(err);
                      socket.emit("group_deleted", "fail");
                    } else {
                      var queryString = "DELETE FROM Groups WHERE group_id = " + groupId + ";";
                      con.query(queryString, function(err, rows, fields) {
                        if (err) {
                          console.log("DELETE_GROUP - Delete group");
                          console.log(err);
                          socket.emit("group_deleted", "fail");
                        } else {
                          socket.emit("group_deleted", "success");
                        }
                      });
                    }
                  });
                }
              });
            }
          }
        });
      }
    });

    socket.on('change_fullname', function(userId, newName) { // used for searching users in the database
      if(userId != null && userId != "" && newName != null && newName != "") {
        var queryString = "UPDATE Users SET name = '" + newName + "' WHERE user_id = " + userId +';';
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log(err);
            socket.emit('fullname_changed', 'fail');
          } else {
            socket.emit("fullname_changed", '' + newName);
          }
        });
      }
    });

    socket.on('change_username', function(userId, newUsername) { // used for searching users in the database
      if(userId != null && userId != "" && newUsername != null && newUsername != "") {
        var queryString = "UPDATE Users SET username = '" + newUsername + "' WHERE user_id = " + userId +';';
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            if (err.code == 'ER_DUP_ENTRY') {
              socket.emit('username_changed', 'duplicate');
            } else {
              socket.emit('username_changed', 'fail');
            }
          } else {
            socket.emit("username_changed", '' + newUsername);
          }
        });
      }
    });

    socket.on('change_email', function(userId, newEmail) { // used for searching users in the database
      if(userId != null && userId != "" && newEmail != null && newEmail != "") {
        var queryString = "UPDATE Users SET email = '" + newEmail + "' WHERE user_id = " + userId +';';
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log(err);
            socket.emit('email_changed', 'fail');
          } else {
            socket.emit("email_changed", '' + newEmail);
          }
        });
      }
    });

    socket.on('change_phone_number', function(userId, newPhoneNo) { // used for searching users in the database
      if(userId != null && userId != "" && newPhoneNo != null && newPhoneNo != "") {
        var queryString = "UPDATE Users SET phone_number = '" + newPhoneNo + "' WHERE user_id = " + userId +';';
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log(err);
            socket.emit('phone_number_changed', 'fail');
          } else {
            socket.emit("phone_number_changed", '' + newPhoneNo);
          }
        });
      }
    });

    socket.on('change_about', function(userId, newAbout) { // used for searching users in the database
      if(userId != null && userId != "" && newAbout != null && newAbout != "") {
        var queryString = "UPDATE Users SET biography = '" + newAbout + "' WHERE user_id = " + userId +';';
        con.query(queryString, function(err, rows, fields) {
          if (err) {
            console.log(err);
            socket.emit('about_changed', 'fail');
          } else {
            socket.emit("about_changed", '' + newAbout);
          }
        });
      }
    });

  });

};
