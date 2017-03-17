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
//var index = require('./routes/index');
var users = require('./routes/users');

var profile = require('./routes/profile');
var login= require('./routes/login');
var register = require('./routes/register');
var changePass= require('./routes/changePass');
var loginError=require('./routes/loginError');
var contacts=require('./routes/contacts');
var app = express();

var client = new Client();

var io = socket_io();
app.io = io;


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

//profile
app.get('/profile', function(req, res){

res.render('profile');
});

//login , as of now redirect to profle page first
app.post('/authenticate', function(req,res){

  var args = {
      data: {
        username : req.body.username, // username is unique
        password : req.body.password,
      },
      headers: { "Content-Type": "application/json" }
  };

  client.post("http://188.166.157.62:3000/login", args, function (data, response) {

      // parsed response body as js object

      var ans = data;
     if (ans.status === 'failed'){
      res.render('loginError');
    }
    else {
      console.log(ans);

      app.locals.username=ans.username;
      app.locals.email=ans.email;
      app.locals.phone=ans.phone_number;
      app.locals.fullname=ans.name;
      app.locals.user_id=ans.user_id;

      var args = {
          data: {
            userId :app.locals.user_id
          },
          headers: { "Content-Type": "application/json" }
      };


      client.post("http://188.166.157.62:3000/contacts", args, function (data, response) {
          // parsed response body as js object
          app.locals.contactList=data;
          var contacts = data;
           console.log(contacts);
           var send = { contacts , profilePicture:"DummyDummy" , chats : [ {message : "Dummy"} ], pointer : 0};
           res.render('contacts',send);
        });
    };

  });

});




app.post('/register', function(req,res){
  var args = {
      data: {
        username :req.body.username ,
        email : req.body.email,
        fullName : req.body.fullName,
        pwd : req.body.pwd,
        phoneNo :req.body.phoneNo,
      },
      headers: { "Content-Type": "application/json" }
  };


   client.post("http://188.166.157.62:3000/register", args, function (data, response) {
      // parsed response body as js object
     var ans = data;
      console.log(ans.toString());

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

app.get('/sockettest', function(req, res){
  res.render('socket');
});

io.on('connection', function(socket){
  //'chat message equals to the app function in express'
  socket.on('chat message', function(msg){
    io.emit('chat message', msg);
  });
});

app.use('/users', users);
app.use('/login',login);
app.use('/register',register);
app.use('/changePass',changePass);
app.use('/loginError',loginError);
app.use('/contacts',contacts);


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
