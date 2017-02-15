var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var bodyParser = require('body-parser')
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
  extended: false
})); 

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


// Authenticate user
app.post('/login', function(req, res) {

	var queryString = 'SELECT * FROM Users WHERE username = ' + 
                   con.escape(req.body.username) + ' AND password = ' + 
                   con.escape(req.body.password) ;

    con.query(queryString, function(err, rows, fields) {
	    if (err) { 
	    	throw err;
	    	res.end('fail');
	    }

	 	if (rows.length == 1) {
			res.end('success');
	 	} else {
			res.end('fail');
		}
	});
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

http.listen(3000, function(){
 console.log('listening on *:3000');
});