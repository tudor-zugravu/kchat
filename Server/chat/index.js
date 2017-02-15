var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

// DataBase 
var mysql = require("mysql");
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

// Test database connection
var db = con;
var data = "";
db.query('SELECT * FROM Users',function(err,rows){
	//if(err) throw err;
	
	// console.log('Data received from Db:\n');
	console.log(rows);
	var data = rows;
	console.log("Outside--"+data.id);
	//res.render('userIndex', { title: 'User Information', dataGet: data });
});

// Initial code
app.get('/', function(req, res){
 res.sendFile(__dirname + '/index.html');
});

io.on('connection', function(socket){
 console.log('TudorZg connected!!!');
 socket.on('disconnect', function(){
   console.log('user disconnected');
 });
 socket.on('chat message', function(msg){
   console.log('message: ' + msg);
   io.emit('chat message', msg);
 });
});

http.listen(5000, function(){
 console.log('listening on *:5000');
});