$(function() {
  // Initialize form validation on the registration form.
  // It has the name attribute "registration"
  $("form[name='registration']").validate({

    errorPlacement: function(error, element) {
                      error.appendTo("div#errors");

                  },
    // Specify validation rules
    rules: {
      // The key name on the left side is the name attribute
      // of an input field. Validation rules are defined
      // on the right side
      username: "required",
      fullName: "required",
      email: {
        required: true,
        // Specify that email should be validated
        // by the built-in "email" rule
        email: true
      },
      pwd: {
        required: true,

      }
    },
    // Specify validation error messages
    messages: {
      username: "</br>Please enter your username",
      fullName: "</br>Please enter your fullname",
      pwd: {
        required: "</br>Please provide a password",
      
      },
      email: "</br>Please enter a valid email address",
    },
    // Make sure the form is submitted to the destination defined
    // in the "action" attribute of the form when valid
    submitHandler: function(form) {
      form.submit();
    }
  });
});
