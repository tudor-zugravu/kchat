var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  var milk = {title : 'Suwichak', welcomeText : 'Welcome to the Jungle, we have fun and game !'};
  res.render('index', milk);
});

module.exports = router;
