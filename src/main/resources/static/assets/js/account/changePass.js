$(document).ready(function(){
    var passwordRegex =8;

   $("#form_changePass").on('submit', function(e){
        e.preventDefault();

        var currentPassword = $("#currentPassword").val();
        var password = $("#password").val();
        var confirmPassword = $("#confirmPassword").val();
        var user_id = $("#user_id").val();
        console.log(password);
        if(password.length <= 8){
            $("#error1").removeClass("d-none");
            $("#error2").addClass("d-none");
            return;
        }else if(password != confirmPassword){
           $("#error1").addClass("d-none");
           $("#error2").removeClass("d-none");
           return;
        }else{
            $("#error2").addClass("d-none");
            $("#error1").addClass("d-none");
            Swal.fire({
              title: "Do you want to save the changes?",
              showCancelButton: true,
              confirmButtonText: "Save",
              icon: "warning"
            }).then((result) => {
                if(result.isConfirmed){
                    $.ajax({
                        url: "/ajax/changePassword",
                        type: "POST",
                        data: {
                            password: password,
                            user_id: user_id,
                            currentPassword : currentPassword
                            },
                        success: function(response) {
                            console.log("Success:", response);
                            if(response.status == "success"){
                                Swal.fire(response.message, "", "success").then(() => {
                                   location.reload();
                                });
                            }else{
                                Swal.fire(response.message, "", "error");
                            }
                        },
                        error: function(xhr, status, error) {
                            Swal.fire("There's something wrong in changing your password", "", "error");
                            console.error("Error:", error); // Function to handle errors
                        }
                    });
                }
            });
        }
   })



});