var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

//var index = require('./routes/index');
var users = require('./routes/users');

var profile = require('./routes/profile');
var login= require('./routes/login')
var app = express();
var mysql = require('mysql');
var con = mysql.createConnection({
  host     : 'localhost',
  port : '8889',
  user     : 'yuke',
  password : '123456789',
  database: "kchat"
});
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

//profile
app.get('/profile', function(req, res){
  con.query('SELECT * FROM users', function(err, rows){
    var yuke = {};
    yuke.name = rows[0].name;
    yuke.email = rows[0].email;
    yuke.phone = rows[0].phone_number;
    console.log(yuke);
    res.render('profile', {yuke});
  });
});

//login , as of now redirect to profle page first
app.post('/authenticate', function(req,res){
  var query =  'SELECT * FROM users where username="'+req.body.username+'"';
  con.query(query, function(err, rows){
    var yuke = {};
    yuke.name=rows[0].name;
    yuke.email = rows[0].email;
    yuke.phone = rows[0].phone_number;
    console.log(rows[0].username);
    console.log(req.body.username);
    console.log(rows[0].password);
    console.log(req.body.password);
   if ((req.body.username === rows[0].username) && (req.body.password===rows[0].password)){
     res.render('profile',{yuke});
   }
  else {console.log("wrong password or username")}
    });

});



//app.use('/', index);
app.use('/users', users);
//app.use('/yuke', yuke);
app.use('/login',login)

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
