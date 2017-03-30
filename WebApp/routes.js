var express = require('express');
var router = express.Router();
var Client = require('node-rest-client').Client;
var client = new Client();
var app = require('./app');


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

            res.render('individualChat', { user_id: req.session.user_id, fullname: req.session.fullname, target_id: 0, target_name: ""});
        });
    } else {
        res.render('login');
    }
});

// clear all the sessions when user logout
router.route('/logout').get(function(req, res, next) {
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

// get and send all required information to chat page
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


// get all contacts informations and send to contact page
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

// get all contacts informations and send to group chat page
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

// get all profile informations and send to profile page
router.route('/profile').get(function(req, res){
    res.render('profile', { user_id: req.session.user_id,
                            username: req.session.username,
                            fullname: req.session.fullname,
                            profile_picture: req.session.profile_picture,
                            email: req.session.email,
                            phone: req.session.phone,
                            biography: req.session.biography });
});

// save all the changes in profile page and send new data to profile page
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

// authenticate user when they login and save information in sessions
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

        res.render('individualChat', {user_id: req.session.user_id, fullname: req.session.fullname, target_id : 0,target_name:""});
    });
});

// register user to system and save all the information to sessions if register success
router.route('/register').post(function(req,res){

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

// change password and save new password to session
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


// change profile information in database and save in session
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


// change group profile in server
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

         if(id == data.toString()){

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

// retrive information from contacts page and send the information to chat page
router.route('/chat').post(function(req, res){

    id = req.body.contactId;
    name = req.body.contactName;

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

    });
});

// saves all the contatcs information and send data to realated links in contatcs page
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
