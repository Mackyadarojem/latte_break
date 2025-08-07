$(document).ready(function(){
     $("#form_updateQuestion").on("submit", function(e){
        e.preventDefault();
        var formData = $(this).serialize();
        Swal.fire({
            icon : "warning",
            title : "Do you want to save changes?",
            showCancelButton: true,
            confirmButtonText: "Save"
        }).then((result) => {
            if(result.isConfirmed){
                 $.ajax({
                    url: 'ajax/saveSecurityQuestion',
                    type: 'POST',
                    data: formData,
                    dataType: 'json',
                    success: function(response) {
                       if(response.status == "success"){
                          Swal.fire(response.message, "", "success").then(() => {
                             location.reload();
                          });
                       }else{
                          Swal.fire(response.message, "", "error");
                       }
                    },
                    error: function(xhr, status, error) {
                        console.error('Error:', error);
                    }
                });
            }
        });
     });

});