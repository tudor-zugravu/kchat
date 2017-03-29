//Example POST method invocation
var Client = require('node-rest-client').Client;
var client = new Client();

// set content-type header and data as json in args parameter
var args = {
    data: {
      username : "tudor",
      password : "tudor"
    },
    headers: { "Content-Type": "application/json" }
};

client.post("http://188.166.157.62:3000/login", args, function (data, response) {
    // parsed response body as js object
    var response = data;
    console.log(response);
});
