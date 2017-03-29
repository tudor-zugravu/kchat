var express = require('express');
var router = express.Router();

var Client = require('node-rest-client').Client;
var client = new Client();

var app = require('./app');
// var socket = require('socket.io-client')('http://188.166.157.62:3000');

const fileUpload = require('express-fileupload');
app.use(fileUpload());

// Validating user credentials with the database
router.route('/login').get(function(req, res, next) {
    if (req.session.username != undefined && req.session.password != undefined) {
        var args = {
            data: {
                username : req.session.username, // username is unique
                password : req.session.password,
            },
            headers: { "Content-Type": "application/json" }
        };
        client.post("http://188.166.157.62:3000/login", args, function (data, response) {
            var ans = data;
            if (ans.status === 'failed'){
                res.render('loginError');
            }
            req.session.username = ans.username;
            req.session.password = ans.password;
            req.session.email = ans.email;
            req.session.phone = ans.phone_number;
            req.session.fullname = ans.name;
            req.session.user_id = ans.user_id;
            req.session.biography = ans.biography;
            req.session.profile_picture = ans.profile_picture;

            console.log("session started for: " + req.session.fullname);
            res.render('individualChat', { user_id: req.session.user_id, fullname: req.session.fullname, target_id: 0, target_name: ""});
        });
    } else {
        res.render('login');
    }
});

router.route('/logout').get(function(req, res, next) {
    console.log("logging out " + req.session.fullname);
    req.session.username = "";
    req.session.password = "";
    req.session.email = "";
    req.session.phone = "";
    req.session.fullname = "";
    req.session.user_id = "";
    req.session.biography = "";
    req.session.profile_picture = "";
    res.render('login');
});

// Change password
router.route('/changePass').get(function(req, res, next) {
    res.render('changePass');
});

router.route('/chat').get(function(req, res){
    var args= {
        data: {
            userId :req.session.user_id
        },
            headers: { "Content-Type": "application/json" }
    };
    client.post("http://188.166.157.62:3000/contacts", args, function (data, response) {
        req.session.contactList = data;
        var contacts_list = data;
        res.render('individualChat', { user_id: req.session.user_id, fullname: req.session.fullname, target_id : 0,target_name:"",contacts_list});
    });
});

router.route('/contacts').get(function(req,res){
    var args = {
        data: {
          userId :req.session.user_id
        },
        headers: { "Content-Type": "application/json" }
    };
    client.post("http://188.166.157.62:3000/contacts", args, function (data, response) {
        req.session.contactList = data;
        var contacts = data;
        var send = { user_id: req.session.user_id, fullname: req.session.fullname, contacts , profilePicture:"DummyDummy" , chats : [ {message : "Dummy"} ], pointer : 0};
        res.render('contacts',send);
    });
});

router.route('/groupChat').get(function(req, res){
    var args = {
            data: {
            userId :req.session.user_id
        },
        headers: { "Content-Type": "application/json" }
    };
    client.post("http://188.166.157.62:3000/contacts", args, function (data, response) {
        req.session.contactList = data;
        var contacts = data;
        var send = { user_id: req.session.user_id, fullname: req.session.fullname, contacts , pointer : 0};
        res.render('groupChat',send);
    });
});

router.route('/loginError').get(function(req, res, next) {
    res.render('/loginError');
});

router.route('/register').get(function(req, res, next) {
    res.render('register');
});

router.route('/users').get(function(req, res, next) {
    res.send('respond with a resource');
});

router.route('/individualChat').get(function(req, res, next) {
    res.render('/individualChat', { user_id: req.session.user_id, fullname: req.session.fullname });
});

router.route('/profile').get(function(req, res){
    res.render('profile', { user_id: req.session.user_id,
                            username: req.session.username,
                            fullname: req.session.fullname,
                            profile_picture: req.session.profile_picture,
                            email: req.session.email,
                            phone: req.session.phone,
                            biography: req.session.biography });
});

router.route('/profile').post(function(req, res){
    req.session.username = req.body.user;
    req.session.fullname = req.body.name;
    req.session.email = req.body.email;
    req.session.phone = req.body.phone;
    req.session.biography = req.body.about;

    res.render('profile', { user_id: req.session.user_id,
                            username: req.session.username,
                            fullname: req.session.fullname,
                            profile_picture: req.session.profile_picture,
                            email: req.session.email,
                            phone: req.session.phone,
                            biography: req.session.biography });
});



router.route('/changeProfile').get(function(req, res){
    res.render('changeProfile');
});

router.route('/authenticate').post(function(req,res){
    var args = {
        data: {
            username : req.body.username, // username is unique
            password : req.body.password,
        },
        headers: { "Content-Type": "application/json" }
    };
    client.post("http://188.166.157.62:3000/login", args, function (data, response) {
        var ans = data;
        if (ans.status === 'failed'){
            res.render('loginError');
        }
        req.session.username = ans.username;
        req.session.password = ans.password;
        req.session.email = ans.email;
        req.session.phone = ans.phone_number;
        req.session.fullname = ans.name;
        req.session.user_id = ans.user_id;
        req.session.biography = ans.biography;
        req.session.profile_picture = ans.profile_picture;
        console.log("the current user's session id is: " + req.session.user_id);
        res.render('individualChat', {user_id: req.session.user_id, fullname: req.session.fullname, target_id : 0,target_name:""});
    });
});

router.route('/register').post(function(req,res){
    console.log("register test");
    var args = {
        data: {
            username :req.body.username ,
            email : req.body.email,
            fullName : req.body.fullName,
            pwd : req.body.pwd,
            phoneNo :req.body.phoneNo,
            biography:req.body.biography,
        },
        headers: { "Content-Type": "application/json" }
    };
    client.post("http://188.166.157.62:3000/register", args, function (data, response) {
        var ans = data;
        console.log(ans.toString());
       var split = ans.toString().split(" ");
        var insertId = split[split.length-1];
        if (ans.includes("success")){
            req.session.username=req.body.username ;
            req.session.email=req.body.email;
            req.session.phone=req.body.phoneNo;
            req.session.fullname=req.body.fullName;
            req.session.user_id=insertId;
            req.session.biography=req.body.biography;
            res.render('individualChat', {user_id: req.session.user_id, fullname: req.session.fullname, target_id : 0,target_name:""});
        }
      if(ans.toString()=="duplicate"){
          res.render('registerDuplicate.ejs')
        }
        if(ans.toString()=="fail"){
          res.render('registerFail.ejs')
        }

    });
});

router.route('/changePass').post(function(req,res){
    var args = {
        data: {
            newPassword : req.body.newPassword,
            password : req.body.password,
            username : req.session.username,
        },
        headers: { "Content-Type": "application/json" }
    };
    client.post("http://188.166.157.62:3000/changePass", args, function (data, response) {
        var ans = data;
        if (ans.toString()== 'success'){
            req.session.password = req.body.newPassword;
            res.render('profile', { user_id: req.session.user_id,
                                    username: req.session.username,
                                    fullname: req.session.fullname,
                                    profile_picture: req.session.profile_picture,
                                    email: req.session.email,
                                    phone: req.session.phone,
                                    biography: req.session.biography });
        }else{
        res.render('changePassFail.ejs');
        }
    });
});



router.route('/changeProfile').post(function(req,res,next){
  var id = req.session.user_id;

     var profilePic = req.files.new_ProfilePic.data;
     var json = {
       sender : id.toString(),
       usersImage : profilePic
     };
     var args = {
         data: {
             request : 1,
             json : JSON.stringify(json)
         },
         headers: { "Content-Type": "application/json" }
     };
     client.post("http://188.166.157.62:3000/imageUpload", args, function (data, response) {
         if(id == data.toString()){
           console.log("Successful image Write");
           res.render('profile',{user_id: req.session.user_id,
                                   username: req.session.username,
                                   fullname: req.session.fullname,
                                   profile_picture: req.session.profile_picture,
                                   email: req.session.email,
                                   phone: req.session.phone,
                                   biography: req.session.biography});
         }
     });
});


router.route('/changeGroupProfile').post(function(req,res,next){
    var id = req.body.groupId;
     var profilePic = req.files.groupPic.data;
     var json = {
       sender : id.toString(),
       usersImage : profilePic
     };
     var args = {
         data: {
             request : 1,
             json : JSON.stringify(json)
         },
         headers: { "Content-Type": "application/json" }
     };
     client.post("http://188.166.157.62:3000/groupImageUpload", args, function (data, response) {
         console.log("OUT");
         if(id == data.toString()){
            console.log("IN");
            var args = {
                data: {
                userId :req.session.user_id
            },
                headers: { "Content-Type": "application/json" }
            };
            client.post("http://188.166.157.62:3000/contacts", args, function (data, response) {
                req.session.contactList = data;
                var contacts = data;
                var send = { user_id: req.session.user_id, fullname: req.session.fullname, contacts , pointer : 0};
                res.render('groupChat',send);
            });
        }
     });
});


router.route('/chat').post(function(req, res){
 console.log("start chat 1");
    id = req.body.contactId;
    name = req.body.contactName;
    console.log(id);
    console.log(name);
   var send = { user_id: req.session.user_id, fullname: req.session.fullname, target_id: id, target_name: name};
    res.render('individualChat',send);
});

router.route('/changeUsername').post(function(req,res){
    var args = {
        data: {
            newUsername: req.body.new_Username,
            id:req.session.user_id,
        },
        headers: { "Content-Type": "application/json" }
    };
    client.post("http://188.166.157.62:3000/changeUsername", args, function(data, response){
        var ans = data;
        console.log("changeUsername post request: " + ans.toString());
    });
});

router.route('/contacts/:id').get(function(req,res){
    target = req.params.id;
    temp = req.session.contactList;
    pointer = 0;
    for(var i = 0; i < temp.length; i++){
        if(temp[i].user_id == target){
            pointer = i;
        }
    }
    var send = { user_id: req.session.user_id, fullname: req.session.fullname, contacts : req.session.contactList , profilePicture:"DummyDummy" , pointer : pointer};
    res.render('contacts',send);
});

module.exports = router;
