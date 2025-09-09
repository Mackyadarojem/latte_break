$(document).ready(function(){
    var method;

     $('.price').maskMoney({
        prefix: '₱ ',    // optional prefix
        allowNegative: false,
        thousands: ',',
        decimal: '.',
        affixesStay: true
      });

    var DT_ProductList = $("#DT_ProductList").DataTable({
        "processing": true,
        "ajax": {
           "url": "ajax/getAllProduct",
           "type": "POST",
           "dataType": "json",
           "dataSrc": function(response) {
               if (response.status === "failed") {
                   window.location.href = "login";
               }else{
                    return response.data;
               }
           }
        },
        "columns":[
            {
                "data": "id",
                render : function(data){
                    return `<button data-id="${data}" class="archiveProduct btn btn-sm btn-danger"><span>Archive</span></button>`
                }
            },
            { "data": "name" },
            { "data": "category_name" },
            {
                "data": "price",
                render : function(data, type, row){
                    var category_id = row.category_id;
                    if(category_id == 1 || category_id == 3 || category_id == 4 || category_id == 5){
                        return `${row.priceMedium}(M) - ${row.priceLarge}(L)`;
                    }else if (category_id == 2){
                        return `${row.priceLarge}(L)`;
                    }else{
                        return `${row.price}`;
                    }

                }
            },
            { "data": "description" },
            {
                "data": "available",
                render: function(data, type, row) {
                    console.log(data);
                    var isChecked = data ? 'checked' : '';
                    return `<div class="form-check form-switch">
                                <input class="form-check-input availSwitch" type="checkbox" role="switch" id="switchCheckChecked" ${isChecked}>
                            </div>`;
                }
            }
        ],
         "columnDefs": [
           { "className": "text-start", "targets": "_all" } // Apply to all columns
        ],
        fnCreatedRow: function(row, data, dataIndex) {
            $(row).find('.editProduct').on('click', function() {
                method = "Edit";
                console.log(row);
                var id = $(this).data("id");
                $("#id").val(id);
                $("#product_name").val(data.name);
                $("#category_id").val(data.category_id);
                $("#description").val(data.description);
                $("#price").val(data.price);
                if (data.available) {
                    $("#available").prop("checked", true); // Correct way to check the checkbox
                } else {
                    $("#available").prop("checked", false); // Uncheck if false
                }
                $("#method").val(method);
                $("#productModalLabel").text(method + "     Product");
                $("#productModal").modal("show");
            });

            $(row).find('.archiveProduct').on('click', function() {
                var id = $(this).data("id");
                $("#id").val(id);

                    Swal.fire({
                       icon: "warning",
                       title: "Do you want to archive this product?",
                       showCancelButton: true,
                       confirmButtonText: "Save"
                    }).then((result) => {
                         if(result.isConfirmed){
                            $.ajax({
                                url: 'ajax/archiveProduct',
                                type: 'POST',
                                data: {id : id},
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

            $(row).find('.availSwitch').on('change', function(){
                var id = data.id;
                var available = $(this).is(':checked');
                $.ajax({
                  url: '/ajax/changeAvailability',
                  type: 'POST',
                  data: {
                    id: id,
                    available: available
                  },
                  dataType: 'json',
                  success: function(response) {
                    console.log('Success:', response);
                  },
                  error: function(xhr, status, error) {
                    console.error('Error:', error);
                  }
                });
            });
        }
     });

    $("#form_addProduct").on("submit", function(e){
        e.preventDefault();

        var available = false

        if($('#available').is(':checked')){
            available = true;
        }else{
            available = false;
        }
        var formData = $(this).serialize() +'&available='+available;

        $("#productModal").modal("hide");
      Swal.fire({
           icon: "warning",
           title: "Do you want to save these data?",
           showCancelButton: true,
           confirmButtonText: "Save"
        }).then((result) => {
            if(result.isConfirmed){
                 $.ajax({
                    url: 'ajax/addProduct',
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
                        Swal.fire(error, "", "error");
                        console.error('Error:', error);
                    }
                });
            }else{
                $("#productModal").modal("show");
            }
        });
    });

    $(".addProduct").on("click", function(){
        method = "Add";
        $("#id").val(0);
        $("#method").val(method);

        $("#form_addProduct select").val("1").trigger("change");
        $("#form_addProduct textarea").val("");
        $("#available").prop("checked", false);

        $("#productModalLabel").text(method + "     Product");
        $("#productModal").modal("show");
    });

    $("#form_search").on("submit", function(e){
        e.preventDefault();
        var formData = $(this).serialize();
        $.ajax({
            url: "ajax/getAllProduct",
            type: "POST",
            data: formData,
            dataType: "json",
            success: function(response) {
                DT_ProductList.clear();
                DT_ProductList.rows.add(response.data).draw();
            },
            error: function(xhr, status, error) {
                console.error("AJAX Error: " + error);
            }
        });
    })

    $("#btnClear").on("click", function(){
        $("#form_search input").val("");
        $("#form_search select").val("0");
    })

    $("#category_id").on("change", function(){
        var value = $(this).val();
        if(value == 1 || value == 3 || value == 4 || value == 5){
            $(".mediumPriceContainer").removeClass("d-none");
            $(".largePriceContainer").removeClass("d-none");
            $(".singlePriceContainer").addClass("d-none");

            $('#mediumPrice').attr('required', true);
            $('#largePrice').attr('required', true);
            $('#singlePrice').attr('required', false);
        }else if( value == 2 ){
            $(".mediumPriceContainer").addClass("d-none");
            $(".largePriceContainer").removeClass("d-none");
            $(".singlePriceContainer").addClass("d-none");

            $('#mediumPrice').attr('required', false);
            $('#largePrice').attr('required', true);
            $('#singlePrice').attr('required', false);
        }
        else{
            $(".mediumPriceContainer").addClass("d-none");
            $(".largePriceContainer").addClass("d-none");
            $(".singlePriceContainer").removeClass("d-none");

            $('#mediumPrice').attr('required', false);
            $('#largePrice').attr('required', false);
            $('#singlePrice').attr('required', true);
        }
    })

    $("#viewCategory").on("click", function(){
        $("#category_product_modal").modal("show");
        initDT_CategoryList();
    });

    function initDT_CategoryList(){
        $("#DT_CategoryList").DataTable().destroy();
        var DT_CategoryList = $("#DT_CategoryList").DataTable({
            "processing": true,
            "ajax": {
               "url": "productList/ajax/getCategory",
               "type": "POST"
            },
            columns: [
                {
                    data : null,
                    render : function(data, type, row, meta){
                        return meta.row + 1;
                    }
                },
                {data : "category_name"},
                {
                    data : "drink",
                    render : function(data,type,row){
                        if(data){
                            var checked = "checked";
                        }else{
                            var checked = "";
                        }
                        return `<div class="form-check form-switch">
                                  <input ${checked} class="form-check-input" type="checkbox" id="flexSwitchCheckDefault">
                                </div>`
                    }
                },
                {
                    data : "size",
                    render : function(data,type,row){
                       return `<div class="form-check">
                                 <input class="form-check-input" type="radio" name="${row.category_id} + 'size'" id='${row.category_id} + "noSize"'>
                                 <label class="form-check-label" for='${row.category_id} + "noSize"'>
                                   No Size
                                 </label>
                                </div>
                                <div class="form-check">
                                     <input class="form-check-input" type="radio" name="${row.category_id} + 'size'" id='${row.category_id} + "mediumOnly"' >
                                     <label class="form-check-label" for='${row.category_id} + "mediumOnly"'>
                                       Medium Only
                                     </label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="${row.category_id} + 'size'" id='${row.category_id} + "largeOnly"' >
                                    <label class="form-check-label" for='${row.category_id} + "largeOnly"'>
                                      Large Only
                                    </label>
                                </div>
                                <div class="form-check">
                                   <input class="form-check-input" type="radio" name="${row.category_id} + 'size'" id='${row.category_id} + "mediumAndLarge"' >
                                   <label class="form-check-label" for='${row.category_id} + "mediumAndLarge"'>
                                     Medium and Large
                                   </label>
                                </div>`;
                    }
                },
                {
                    data : "category_id",
                     render : function(data, type, row, meta){
                        return `<div class="d-flex gap-2">
                        <button data-id="${data}" class="btn-danger deleteCategory btn">Delete</button>
                        </div>`;
                     }
                },
            ],
        });
    }
});