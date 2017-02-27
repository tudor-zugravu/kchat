var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
//Example POST method invocation
var Client = require('node-rest-client').Client;


//var index = require('./routes/index');
var users = require('./routes/users');

var profile = require('./routes/profile');
var login= require('./routes/login')
var register = require('./routes/register')
var app = express();
var mysql = require('mysql');
var client = new Client();

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

//profile
app.get('/profile', function(req, res){

res.render('profile');
});

//login , as of now redirect to profle page first
app.post('/authenticate', function(req,res){

  var args = {
      data: {
        username : req.body.username,
        password : req.body.password,
      },
      headers: { "Content-Type": "application/json" }
  };

  client.post("http://188.166.157.62:3000/login", args, function (data, response) {
      // parsed response body as js object
      var ans = data;
     if (ans.status === 'failed'){
      console.log('unmatched username and password');
    }
    else {
      console.log(ans);

      app.locals.username=ans.username+"23";
      app.locals.email=ans.email;
      app.locals.phone=ans.phone_number;
      app.locals.fullname=ans.name;
      res.render('profile')
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






app.use('/users', users);
app.use('/login',login);
app.use('/register',register);


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
