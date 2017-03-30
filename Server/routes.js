var express = require('express');
var postHandlers = require('./post_handlers.js');

var router = express.Router();

// Validating user credentials with the database
router.route('/login').post(postHandlers.login);

// Inserting a new user in the database
router.route('/register').post(postHandlers.register);

// Change password
router.route('/changePass').post(postHandlers.changePass);

// Change Username
router.route('/changeUsername').post(postHandlers.changeUsername);

// Change email
router.route('/changeEmail').post(postHandlers.changeEmail);

// Change phone number
router.route('/changePhoneNumber').post(postHandlers.changePhoneNumber);

// Change Biography
router.route('/changebiography').post(postHandlers.changeBiography);

// Handler for contact list request
router.route('/contacts').post(postHandlers.contacts);

// Handler for imageUpload
router.route('/imageUpload').post(postHandlers.imageUpload);

// Handler for imageUpload
router.route('/groupImageUpload').post(postHandlers.groupImageUpload);

// Handler for imageUpload
router.route('/iOSImageUpload').post(postHandlers.iOSImageUpload);

// Handler for bufferUpload
router.route('/bufferUpload').post(postHandlers.bufferUpload);

module.exports = router;