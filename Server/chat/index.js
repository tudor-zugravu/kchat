var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var bodyParser = require('body-parser')

app.use( bodyParser.json({limit:'50mb'}) );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
  extended: true,
	limit:'50mb'
})); 

// Include the REST requests handlers
var routes = require('./routes');
app.use('/', routes);

// Include the Socket.IO functions
require('./message_stream')(io);

// Initial code
app.get('/', function(req, res){
 res.sendFile(__dirname + '/index.html');
});

http.listen(3000, function(){
 console.log('listening on *:3000');
});
