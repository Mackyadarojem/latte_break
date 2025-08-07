$(document).ready(function(){

    var DT_AccountArchived = $("#DT_ArchiveAccount").DataTable({
                "processing": true,
                   "ajax": {
                       "url": "ajax/getAllArchivedUser",
                       "type": "POST"
                   },
                   "columns": [
                    { "render": function(data, type, row, meta) {
                      return `<button data-id="${row.user_id}" class='btn restore btn-primary btn-sm'>Restore</button>
                            <button data-id="${row.user_id}" class='btn delete btn-danger btn-sm'>Delete</button>`;
                      }
                    },
                    {
                        "data": "user_id",
                        "render": function(data, type, row, meta) {
                           return "User-" + String(data).padStart(3, '0');
                        }
                    },
                    {
                     "render": function(data, type, row, meta) {
                           return row.first_name + ' ' + row.middle_name + ' ' + row.last_name;
                        }
                     },
                    { "data": "username" },
                    { "data": "email" },
                    { "data": "role_name" },
                    { "data": "archive_at",
                         "render": function(data, type, row, meta) {
                            return (data && data !== '') ? data : '-';
                         }
                    },
                    { "data": "archive_by",
                        "render": function(data, type, row, meta) {
                           return (data && data !== '') ? data : '-';
                        }
                    }
                   ],
                   "columnDefs": [
                       { "className": "text-start", "targets": "_all" } // Apply to all columns
                   ],
                    fnCreatedRow: function(row, data, dataIndex) {
                          $(row).find('.restore').on('click', function() {
                            var id = $(this).data("id");
                            Swal.fire({
                               icon: "warning",
                               title: "Do you want to restore this account?",
                               showCancelButton: true,
                               confirmButtonText: "Save"
                            }).then((result) => {
                                if(result.isConfirmed){
                                    $.ajax({
                                        url: 'ajax/restoreAccount',
                                        type: 'POST',
                                        data: { user_id : id},
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
                                            console.error('AJAX Error:', status, error);
                                        }
                                    });
                                }
                            });
                          });

                           $(row).find('.delete').on('click', function() {
                              var id = $(this).data("id");
                              Swal.fire({
                                 icon: "warning",
                                 title: "Do you want to delete this account?",
                                 showCancelButton: true,
                                 confirmButtonText: "Save"
                              }).then((result) => {
                                  if(result.isConfirmed){
                                      $.ajax({
                                          url: "ajax/deleteAccount",
                                          type: "POST",
                                          data: {id : id},
                                          dataType: "json", // Expected response type
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
                                              Swal.fire(error, "", "error");
                                              console.error("AJAX Error:", error);
                                          }
                                      });
                                  }
                              });
                          });
                    }
            });

    $("#form_searchArchiveAccount").on("submit", function(e){
        e.preventDefault();
        var formData = $(this).serialize();
        $.ajax({
            url: "ajax/getAllArchivedUser",
            type: "POST",
            data: formData,
            dataType: "json",
            success: function(response) {
                DT_AccountArchived.clear();
                DT_AccountArchived.rows.add(response.data).draw();
            },
            error: function(xhr, status, error) {
                console.error("AJAX Error: " + error);
            }
        });
    })

    var DT_ArchiveList = $("#DT_ArchiveList").DataTable({
            "processing": true,
            "ajax": {
               "url": "ajax/getAllArchiveItem",
               "type": "POST"
            },
            columns: [
                {
                  "data": "id",
                   "render": function(data, type, row, meta) {
                       return `
                       <div class="d-flex gap-2">
                       <button class="btn btn-primary btn-sm restore" data-id="${data}"><span>Restore</span></button>
                       <button class="btn btn-danger btn-sm delete" data-id="${data}"><span>Delete</span></button>
                       </div>
                       `
                   }
                },
                {
                    "data": "name",
                    "render": data => data ? data : "-"
                },
                {
                    "data": "category_name",
                    "render": data => data ? data : "-"
                },
                {
                    "data": "brand",
                    "render": data => data ? data : "-"
                },
                {
                    "data": "archive_at",
                    "render": data => data ? data : "-"
                },
                {
                    "data": "archive_by",
                    "render": data => data ? data : "-"
                },
            ],
            "columnDefs": [
                 { "className": "text-start", "targets": "_all" }
             ],
            fnCreatedRow: function(row, data, dataIndex) {
                $(row).find('.restore').on('click', function() {
                    var id = $(this).data("id");
                    Swal.fire({
                       icon: "warning",
                       title: "Do you want to recover this item?",
                       showCancelButton: true,
                       confirmButtonText: "Save"
                    }).then((result) => {
                        if(result.isConfirmed){
                            $.ajax({
                                url: "ajax/restoreItem",
                                type: "POST",
                                data: {id : id},
                                dataType: "json", // Expected response type
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
                                    Swal.fire(error, "", "error");
                                    console.error("AJAX Error:", error);
                                }
                            });
                        }
                    });
                });

                 $(row).find('.delete').on('click', function() {
                    var id = $(this).data("id");
                    Swal.fire({
                       icon: "warning",
                       title: "Do you want to delete this item?",
                       showCancelButton: true,
                       confirmButtonText: "Save"
                    }).then((result) => {
                        if(result.isConfirmed){
                            $.ajax({
                                url: "ajax/deleteItem",
                                type: "POST",
                                data: {id : id},
                                dataType: "json", // Expected response type
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
                                    Swal.fire(error, "", "error");
                                    console.error("AJAX Error:", error);
                                }
                            });
                        }
                    });
                });
            }
        });

    $("#form_searchArchiveItem").on("submit", function(e){
            e.preventDefault();
            console.log("tesst");
            var formData = $(this).serialize();
            console.log(formData);
             $.ajax({
                url: "ajax/getAllArchiveItem",
                type: "POST",
                data: formData,
                dataType: "json",
                success: function(response) {
                    DT_ArchiveList.clear();
                    DT_ArchiveList.rows.add(response.data).draw();
                },
                error: function(xhr, status, error) {
                    console.error("AJAX Error: " + error);
                }
            });
        });

    $("#btnClear").on("click", function(){
                $("#form_search input").val("");
                $("#form_search select").val("0").trigger("change");
           });

    var DT_ArchiveProductList = $("#DT_ArchiveProductList").DataTable({
        "processing": true,
        "ajax": {
           "url": "ajax/getArchivedProduct",
           "type": "POST"
        },
        columns: [
            {
              "data": "id",
               "render": function(data, type, row, meta) {
                   return `
                   <div class="d-flex gap-2">
                   <button class="btn btn-primary btn-sm restore" data-id="${data}"><span>Restore</span></button>
                   <button class="btn btn-danger btn-sm delete" data-id="${data}"><span>Delete</span></button>
                   </div>
                   `
               }
            },
            {
                "data": "name",
                "render": data => data ? data : "-"
            },
            {
                "data": "category_name",
                "render": data => data ? data : "-"
            },
            {
                "data": "price",
                "render": data => data ? data : "-"
            },
            {
                "data": "description",
                "render": data => data ? data : "-"
            },
            {
                "data": "archive_at",
                "render": data => data ? data : "-"
            },
            {
                "data": "archive_by",
                "render": data => data ? data : "-"
            },
        ],
        "columnDefs": [
             { "className": "text-start", "targets": "_all" }
         ],
        fnCreatedRow: function(row, data, dataIndex) {
            $(row).find('.restore').on('click', function() {
                var id = $(this).data("id");
                Swal.fire({
                   icon: "warning",
                   title: "Do you want to recover this product?",
                   showCancelButton: true,
                   confirmButtonText: "Save"
                }).then((result) => {
                    if(result.isConfirmed){
                        $.ajax({
                            url: "ajax/restoreProduct",
                            type: "POST",
                            data: {id : id},
                            dataType: "json", // Expected response type
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
                                Swal.fire(error, "", "error");
                                console.error("AJAX Error:", error);
                            }
                        });
                    }
                });
            });

             $(row).find('.delete').on('click', function() {
                var id = $(this).data("id");
                Swal.fire({
                   icon: "warning",
                   title: "Do you want to delete this product?",
                   showCancelButton: true,
                   confirmButtonText: "Save"
                }).then((result) => {
                    if(result.isConfirmed){
                        $.ajax({
                            url: "ajax/deleteProduct",
                            type: "POST",
                            data: {id : id},
                            dataType: "json", // Expected response type
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
                                Swal.fire(error, "", "error");
                                console.error("AJAX Error:", error);
                            }
                        });
                    }
                });
            });
        }
    });
});