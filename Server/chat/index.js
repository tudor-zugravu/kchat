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


// Validating user credentials with the database
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
	 		res.json(rows[0]);
	 	} else {
			res.json({ status: 'failed' });
		}
	});
});

// Inserting a new user in the database
app.post('/register', function(req, res) {

	var queryString = "INSERT INTO Users (`name`, `email`, `username`, `password`, `phone_number`, `session`) VALUES (" + 
						con.escape(req.body.fullName) + ", " + con.escape(req.body.email) + ", "+ con.escape(req.body.username) + 
						", " + con.escape(req.body.pwd) + ", " + con.escape(req.body.phoneNo) + ", '1')";
	
    con.query(queryString, function(err, rows, fields) {
	    if (err) {
	    	if (err.code == 'ER_DUP_ENTRY') {
	    		res.end('duplicate');
	    	}
	    } else if (rows != null && rows.insertId > 0){
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