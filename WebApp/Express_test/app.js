var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
//Example POST method invocation
var Client = require('node-rest-client').Client;

var http = require('http').Server(app);
var socket_io = require('socket.io');
var socket_io_cli = require('socket.io-client')('http://188.166.157.62:3000');
//var index = require('./routes/index');
var users = require('./routes/users');

var profile = require('./routes/profile');
var login= require('./routes/login');
var register = require('./routes/register');
var changePass= require('./routes/changePass');
var loginError=require('./routes/loginError');
var contacts=require('./routes/contacts');
var individualChat= require('./routes/individualChat');
var groupChat=require('./routes/groupChat');
var chat=require('./routes/Chat');
var changeUsername=require('./routes/changeUsername');

var app = express();

var client = new Client();

var io = socket_io();
app.io = io;
//var socket = new io.Socket();
//socket.connect('http://188.166.157.62:3000');
//var socket = io.connect('http://188.166.157.62:3000');


/*var con = mysql.createConnection({
  host     : 'localhost',
  user     : 'yuke',
  password : '123456789',
  database: "kchat"
});*/
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static( 'public'));

/*app.local({
    user: {
      fullName: "Yuke",
      username:"yuke",
      password:"yuke",
      email:"yuke@outlook.com",
      phone:"7897985430543"

    }
});*/
app.locals.username = "";
app.locals.email = "";
app.locals.phone = "";
app.locals.fullName=""
app.locals.user_id="";
app.locals.contactList="";
app.locals.biography="";
app.locals.profile_picture="";
//profile
app.get('/profile', function(req, res){

res.render('profile');
});

//login , as of now redirect to profle page first
app.post('/authenticate', function(req,res){
  console.log ("test login 1");
  var args = {
      data: {
        username : req.body.username, // username is unique
        password : req.body.password,
      },
      headers: { "Content-Type": "application/json" }
  };

  client.post("http://188.166.157.62:3000/login", args, function (data, response) {
    console.log ("test login 2");
      // parsed response body as js object

      var ans = data;
     if (ans.status === 'failed'){
      res.render('loginError');
    }
    else {
      console.log("Login ans "+ans);}

      app.locals.username=ans.username;
      app.locals.email=ans.email;
      app.locals.phone=ans.phone_number;
      app.locals.fullname=ans.name;
      app.locals.user_id=ans.user_id;
      app.locals.biography=ans.biography;
      app.locals.profile_picture=ans.profile_picture;

  });
  var args= {
      data: {
        userId :36
      },
      headers: { "Content-Type": "application/json" }
  };

    console.log ("test contacts2")

    client.post("http://188.166.157.62:3000/contacts", args, function (data, response) {
      console.log ("reached contacts in login 1")
        // parsed response body as js object
        app.locals.contactList=data;
        console.log( data);
        var contacts_list = data;
         console.log("contacts has :  "+contacts_list);
           res.render('individualChat', {target_id : 0,target_name:"",contacts_list});
      });

});




app.post('/register', function(req,res){
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
      // parsed response body as js object
     var ans = data;
      console.log(ans.toString());
      console.log("response: "+data);
      console.log(ans.insertId);
      var split = ans.split(" ");
      var insertId = split[split.length-1];
      if (ans.includes("success")){
          app.locals.username=req.body.username ;
          app.locals.email=req.body.email;
          app.locals.phone=req.body.phoneNo;
          app.locals.fullname=req.body.fullName;
          app.locals.user_id=insertId;
          app.locals.biography=req.body.biography;
          res.render('individualChat', {target_id : 0,target_name:""});
        }

    });

  });




  app.post('/changePass', function(req,res){
console.log("req.body.app.local.username");
    var args = {
        data: {
          newPassword : req.body.newPassword,
          password : req.body.password,
          username : app.locals.username,
        },
        headers: { "Content-Type": "application/json" }
    };
    console.log(req.body.newPassword);
    console.log(req.body.password);
    console.log(app.locals.username);

    client.post("http://188.166.157.62:3000/changePass", args, function (data, response) {
        // parsed response body as js object
        var ans = data;
         console.log(ans.toString());
         if (ans.toString()== 'success'){
           console.log ("u have changed password");
           res.render('profile');
         }
      });

    });

    app.post('/changeUsername', function(req,res){
       console.log("changeUsername reached 1");
      var args = {
          data: {
            newUsername: req.body.new_Username,
            id:app.locals.user_id,
          },
          headers: { "Content-Type": "application/json" }
      };
      console.log(req.body.new_Username);
      console.log(app.locals.user_id);
      console.log("changeUsername reached 2");

      client.post("http://188.166.157.62:3000/changeUsername", args, function(req,res){
        console.log("changeUsername reached 3");
      });

      });

// contacts page
    app.get('/contacts', function(req,res){
      console.log ("test contacts1")
      var args = {
            data: {
              userId :app.locals.user_id
            },
            headers: { "Content-Type": "application/json" }
        };
        console.log ("test contacts2")

        client.post("http://188.166.157.62:3000/contacts", args, function (data, response) {
          console.log ("test contacts3")
            // parsed response body as js object
            app.locals.contactList=data;
            var contacts = data;
             console.log("contacts has :  "+contacts);
             var send = { contacts , profilePicture:"DummyDummy" , chats : [ {message : "Dummy"} ], pointer : 0};
             res.render('contacts',send);
          });

      });




app.get('/contacts/:id', function(req,res){

    target = req.params.id;
    temp = app.locals.contactList;

    pointer = 0;

    for(var i = 0; i < temp.length; i++){
      /*console.log(temp[i].user_id);
      console.log(target);
      console.log(temp[i].user_id == target);*/
      if(temp[i].user_id == target){
        pointer = i;
      }

    }
    /*console.log("pointer:"+pointer);
    console.log(app.locals.contactList[pointer]);*/
    var send = { contacts : app.locals.contactList , profilePicture:"DummyDummy" , pointer : pointer};
    res.render('contacts',send);
});

app.get('/chat', function(req, res){
  var args= {
      data: {
        userId :app.locals.user_id
      },
      headers: { "Content-Type": "application/json" }
  };

    console.log ("test contacts2")

    client.post("http://188.166.157.62:3000/contacts", args, function (data, response) {
      console.log ("reached contacts in login 1")
        // parsed response body as js object
        app.locals.contactList=data;
        console.log("response from server "+ data);
        var contacts_list = data;
         console.log("contacts has :  "+contacts_list);
           res.render('individualChat', {target_id : 0,target_name:"",contacts_list});
      });
});

app.post('/chat', function(req, res){
  console.log("conversation");
  id=req.body.contactId;
  name=req.body.contactName;
  var send = { target_id : id,target_name:name};
  console.log("Now we r gonna chat with"+id);
  console.log("Now we r gonna chat with"+name);
  res.render('individualChat',send);
});

app.get('/groupChat', function(req, res){
  console.log ("test contacts1");
  var args = {
        data: {
          userId :app.locals.user_id
        },
        headers: { "Content-Type": "application/json" }
    };
  client.post("http://188.166.157.62:3000/contacts", args, function (data, response) {
    console.log ("test contacts3");
      // parsed response body as js object
      app.locals.contactList=data;
      var contacts = data;
       console.log("contacts has : "+contacts);
       var send = { contacts , pointer : 0};
       res.render('groupChat',send);
    });


});

io.on('connection', function(socket){
  //'chat message equals to the app function in express'
  socket.on('chat message', function(msg){
    io.emit('chat message', msg);
  });
});


app.get('/test', function(req, res){
  /*var id = 2;
  socket_io_cli.emit('get_chats',id);
  socket_io_cli.on('sent_chats', function(msg){
  var target = JSON.parse(msg);
  console.log(target);
  });

  var id = 37;
  var currentUser = 2;
  socket_io_cli.emit('get_recent_messages',currentUser,id,10);
  socket_io_cli.on('send_recent_messages', function(msg){
  console.log(msg);
    });
  $("#chatContent").empty();
  var target = JSON.parse(msg);
  console.log(target.length);
  console.log(target[0]);*/

  console.log("changeUsername reached 1");
 var args = {
     data: {
       newUsername: "zoe11",
       id: 48,
     },
     headers: { "Content-Type": "application/json" }
 };
 console.log(req.body.new_Username);
 console.log(app.locals.user_id);
 console.log("changeUsername reached 2");

 client.post("http://188.166.157.62:3000/changeUsername", args, function(req,res){
   console.log("changeUsername reached 3");
 });


});


app.use('/users', users);
app.use('/login',login);
app.use('/register',register);
app.use('/changePass',changePass);
app.use('/loginError',loginError);
app.use('/contacts',contacts);
app.use('/individualChat',individualChat);
app.use('/groupChat',groupChat);
app.use('/chat',chat);
app.use('/changeUsername',changeUsername);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
