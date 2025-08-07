$(document).ready(function(){
    let yourUsername;
    let yourId;
    let email;
    $("#loginForm").on("submit", function(e){
        e.preventDefault();

        var username = $("#username").val();
        var password = $("#password").val();

       $.ajax({
           url: 'checkUser',  // The URL to send the request to
           type: 'POST', // or 'GET' depending on your needs
           data: { username: username, password: password }, // Data to send to the server
           dataType: 'json', // Expected response format (e.g., 'json', 'html', 'text')
           success: function(response) {
               if(response.status == "success"){
                    window.location.href = "/home";
               }else{
                    $("#error").removeClass("d-none");
               }
           },
           error: function(xhr, status, error) {
               console.error(xhr.responseText); // Handle errors
           }
       });
    });

    $("#form_checkEmail").on("submit", function(e){
        e.preventDefault();
        email = $("#email").val();
        $("#checkEmail").modal("hide");
        $.ajax({
            url: 'ajax/checkEmail',
            type: 'POST',
            data: { email : email},
            dataType: 'json',
            success: function(response) {
            console.log(response);
                 if(response.status == "success"){
                    yourUsername = response.username;
                    yourId = response.id;

                    $.get('sendOTP',{email : email, user_id : yourId}, function(response) {
                        console.log(response);
                        $("#otp").modal("show");
                    });

                }else{
                   Swal.fire(response.message, "", "error").then(() => {
                        location.reload();
                   });
                }
            },
            error: function(xhr, status, error) {
                console.error('AJAX Error:', status, error);
                 location.reload();
            }
        });
    });

    $(".resendOtp").off("click").on("click", function () {
        var $btn = $(this);

        // Disable button
        $btn.prop("disabled", true).text("Resend OTP (60s)");

        // Send OTP via GET request
        $.get('sendOTP', { email: email, user_id: yourId }, function (response) {
            if (response.status === "success") {
                $(".otpResend").removeClass("d-none");
            }
        });

        // Countdown timer
        let timeLeft = 60;
        const interval = setInterval(() => {
            timeLeft--;
            $btn.text(`Resend OTP (${timeLeft}s)`);

            if (timeLeft <= 0) {
                clearInterval(interval);
                $btn.prop("disabled", false).text("Resend OTP");
            }
        }, 1000);
    });

    $("#form_otp").on("submit", function(e){
        e.preventDefault();
        let otpCode = '';
        $('.otp-input').each(function () {
            otpCode += $(this).val();
        });

         $.get('verifyOTP',{otp : parseInt(otpCode), user_id : yourId}, function(response) {
            console.log(response);
            if(response.status == "success"){
                $("#otp").modal("hide");
                $("#changePass").modal("show");
            }else{
               $(".otpError").removeClass("d-none");
            }
         });
    })

    $("#form_changePass").on("submit", function(e){
        e.preventDefault();
        var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>])[A-Za-z\d!@#$%^&*(),.?":{}|<>]{8,}$/;
        var newPassword = $("#newPassword").val();
        var confirmPassword = $("#confirmPassword").val();
        $("#changePass").modal("hide");
        if(!passwordRegex.test(newPassword)){
            Swal.fire({
              title: "Error",
              text: "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character.",
              icon: "error"
            }).then(()=>{
                $("#changePass").modal("show");
            });
        }else if(newPassword != confirmPassword){
            Swal.fire({
              title: "Error",
              text: "New Password and Confirm Password must be the same.",
              icon: "error"
            }).then(()=>{
                $("#changePass").modal("show");
            });
        }else{
            Swal.fire({
                icon:"warning",
                title: "Do you want to save your new password?",
                showCancelButton: true,
                confirmButtonText: "Save"
            }).then(()=>{
                $.ajax({
                    url: 'ajax/updateNewPassword',
                    type: 'POST',
                    data: { username : yourUsername, newPassword : newPassword},
                    dataType: 'json',
                    success: function(response) {
                        if(response.status == "success"){
                            Swal.fire(response.message, "", "success").then(() => {
                                location.reload();
                            });
                        }else{
                            Swal.fire(response.message, "", "error").then(() => {
                                location.reload();
                            });
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error('AJAX Error:', status, error);
                    }
                });
            });
        }
    });

    $(".toggle-password").click(function() {

      $(this).toggleClass("fa-eye fa-eye-slash");
      var input = $($(this).attr("toggle"));
      if (input.attr("type") == "password") {
        input.attr("type", "text");
      } else {
        input.attr("type", "password");
      }
    });

});