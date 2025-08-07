$(document).ready(function(){
    var DT_ItemList = $("#DT_ItemList").DataTable({
        "processing": true,
        "responsive" : true,
        "ajax": {
           "url": "ajax/getAllItem",
           "type": "POST"
        },
       columns: [
           {
               "data": "id",
                "render": function(data, type, row, meta) {
                    return `
                    <div class="d-flex gap-2">
                    <button class="btn btn-warning btn-sm btnViewStock " data-id="${data}"><span>View Stock</span></button>
                    <button class="btn btn-danger btn-sm archive" data-id="${data}"><span>Archive</span></button>
                    </div>
                    `
                }
           },
           { "data": "name", "render": data => data ? data : "-" },
           { "data": "category_name", "render": data => data ? data : "-" },
           { "data": "brand", "render": data => data ? data : "-" },
            { "data": "stock", "render": data => data ? data : "-" },
           { "data": "unit", "render": data => data ? data : "-" },
           { "data": "description", "render": data => data ? data : "-" },
           { "data": "critical_quantity", "render": data => data ? data + ' <i class="fa-solid fa-triangle-exclamation" style="color: #f00f0f;"></i>' : "-" },
           { "data": "expire", "render": data => data ? "Yes" : "No" },
           { "data": "created_by", "render": data => data ? data : "-" },
           { "data": "created_at", "render": data => data ? data : "-" },
           { "data": "updated_by", "render": data => data ? data : "-" },
           { "data": "updated_at", "render": data => data ? data : "-" }
       ],
        fnCreatedRow: function(row, data, dataIndex) {
            $(row).find('.edit').on('click', function() {
            $("#method").val("Edit");
            var method = $("#method").val();
            $("#itemModalLabel").text(method + " Item");
            $("#id").val(data.id);
            $("#name").val(data.name);
            $("#brand").val(data.brand);
            $("#category_id").val(data.category_id);
            console.log(data.category_id);
            $("#description").val(data.description);
            $("#critical_quantity").val(data.critical_quantity);
            $("#unit").val(data.unit);
            if(data.expire){
                $("#expire").prop("checked", true).trigger("change");
            }else{
                $("#expire").prop("checked", false).trigger("change");
            }
            $("#itemModal").modal("show");
          });

            $(row).find('.archive').on('click', function() {
                var id = $(this).data("id");
                Swal.fire({
                   icon: "warning",
                   title: "Do you want to archive this item?",
                   showCancelButton: true,
                   confirmButtonText: "Save"
                }).then((result) => {
                    if(result.isConfirmed){
                        $.ajax({
                            url: "ajax/archiveItem",
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
            })

            $(row).find('.btnViewStock').on('click', function(){
                var item_id = $(this).data("id");
                var item_name = data.name;
                $("#viewStockLabel").text(item_name);
                var DT_ViewStock = $("#DT_ViewStock").DataTable().destroy();
                var DT_ViewStock = $("#DT_ViewStock").DataTable({
                    "processing": true,
                    "responsive" : true,
                    "ajax": {
                       "url": "ajax/viewStockById?id="+item_id,
                       "type": "POST"
                    },
                    columns:[
                        {
                            "data" : "batch_id"
                        },
                        {
                            "data" : "name"
                        },
                        {
                            "data" : "category_name"
                        },
                        {
                            "data" : "quantity"
                        },
                        {
                            "data" : "expiration_date",
                            render : function(data, type, row){
                                return  data ? data : '-';
                            }
                        },
                        {
                            "data" : "description",
                            render : function(data, type, row){
                                return  data ? data : '-';
                            }
                        },
                        {
                            "data" : "encoded_by"
                        },
                        {
                            "data" : "encoded_date"
                        },
                        {
                            "data" : "updated_by",
                            render : function(data, type, row){
                                return  data ? data : '-';
                            }
                        },
                        {
                            "data" : "updated_at",
                            render : function(data, type, row){
                                return  data ? data : '-';
                            }
                        },
                    ]
                });
                $("#viewStock").modal("show");
            });
        }
    });

    $("#form_item").on("submit", function(e){
        var expire = false;
        e.preventDefault();
        if($('#expire').is(':checked')){
            expire = true;
        }else{
            expire = false;
        }
        var formData = $(this).serialize()+'&expire='+expire;
         $("#itemModal").modal("hide");
        Swal.fire({
           icon: "warning",
           title: "Do you want to save these data?",
           showCancelButton: true,
           confirmButtonText: "Save",
        }).then((result) => {
            if(result.isConfirmed){
                $.ajax({
                    url: "ajax/addItem",
                    type: "POST",
                    data: formData,
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
            }else{
                 $("#itemModal").modal("show");
            }
        });
    });

    $("#addItem").on("click", function(){
        $("#method").val("Add");
        var method = $("#method").val();
        $("#itemModalLabel").text(method + " Item");
        $("#id").val("0");
        $("#form_item input").val("");
        $("#form_item select").val("");
        $("#form_item textarea").val("");
        $("#expire").prop("checked", false).trigger("change");
        $("#itemModal").modal("show");
    });

   $("#form_search").on("submit", function(e){
        e.preventDefault();
        var formData = $(this).serialize();
         $.ajax({
            url: "ajax/getAllItem",
            type: "POST",
            data: formData,
            dataType: "json",
            success: function(response) {
                DT_ItemList.clear();
                DT_ItemList.rows.add(response.data).draw();
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

   $("#btnViewAllStock").on("click", function(){
       var DT_ViewAllStock = $("#DT_ViewAllStock").DataTable().destroy();
       var DT_ViewAllStock = $("#DT_ViewAllStock").DataTable({
           "processing": true,
           "responsive" : true,
           "ajax": {
              "url": "ajax/getBatchItemList",
              "type": "POST"
           },
           columns:[
               {
                   "data" : "batch_id"
               },
               {
                   "data" : "name"
               },
               {
                   "data" : "category_name"
               },
               {
                   "data" : "quantity"
               },
               {
                   "data" : "expiration_date",
                   render : function(data, type, row){
                       return  data ? data : '-';
                   }
               },
               {
                   "data" : "description",
                    render : function(data, type, row){
                      return  data ? data : '-';
                    }
               },
               {
                   "data" : "encoded_by"
               },
               {
                   "data" : "encoded_date"
               },
               {
                  "data" : "updated_by"
               },
               {
                 "data" : "updated_at"
               },
           ]
       });
       $("#viewAllStock").modal("show");
   });



    /// Stock In

     $("#stockIn").on("click", function(e){
           $("#stockInModal").modal("show");
    });

    let selectedRows = [];
    var DT_ItemListForStockIn = $("#DT_ItemListForStockIn").DataTable({
        "processing": true,
        "responsive" : true,
        "ajax": {
           "url": "ajax/getAllItem",
           "type": "POST"
        },
        columns: [
            { data: "name", "render": data => data ? data : "-" },
            { data: "brand", "render": data => data ? data : "-" },
            { data: "category_name", "render": data => data ? data : "-" },
            { data: "description", "render": data => data ? data : "-" },
            {
               "render": function(data, type, row, meta) {
                    return `<input class="quantity form-control" type="number" />`
               }
            },
            { data: "unit" },
            {
                data: "expire",
                "render": function(data, type, row, meta) {
                    if(data){
                        return `<input class="expireDate form-control" type="date" />`
                    }else{
                        return '-'
                    }
                }
            },
        ],
    });

    function toggleButton1() {
            $('#btnStockIn').prop('disabled', DT_ItemListForStockIn.rows().count() === 0);
    }

    toggleButton1();

    DT_ItemListForStockIn.on('draw', function () {
        toggleButton1();
    });

    $("#btnStockIn").on("click", function () {
        let batchData = [];
        let valid = true;

        $("#stockInModal").modal("hide");

        $("#DT_ItemListForStockIn tbody tr").each(function () {
            const row = $(this);
            const quantity = parseFloat(row.find(".quantity").val()) || 0;
            const expireDate = row.find(".expireDate").val();

            // Check if quantity is entered but no expiration date
            if (quantity > 0 && expireDate === "") {
                Swal.fire("Please fill the expired date field!", "", "error").then(() => {
                    $("#stockInModal").modal("show");
                });
                valid = false;
                return false; // breaks the each loop
            }

            if (quantity > 0) {
                const rowData = DT_ItemListForStockIn.row(row).data();
                if (rowData) {
                    batchData.push({
                        name: rowData.name || "-",
                        brand: rowData.brand || "-",
                        id: rowData.id,
                        description: rowData.description || "-",
                        unit: rowData.unit || "-",
                        quantity: quantity,
                        expiration_date: expireDate || null
                    });
                }
            }
        });

        if (!valid) return;

        if (batchData.length > 0) {
            Swal.fire({
                icon: "warning",
                title: "Do you want to save these data?",
                showCancelButton: true,
                confirmButtonText: "Save"
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url: "ajax/stockIn",
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(batchData),
                        success: function (response) {
                            if (response.status === "success") {
                                Swal.fire(response.message, "", "success").then(() => {
                                    location.reload();
                                });
                            } else {
                                Swal.fire(response.message, "", "error");
                            }
                        },
                        error: function (error) {
                            Swal.fire("Error in stock-in process:", "", "error");
                            console.log("Error in stock-in process:", error);
                        }
                    });
                } else {
                    $("#stockInModal").modal("show");
                }
            });
        } else {
            Swal.fire("Please insert quantity to stock in!", "", "error").then(() => {
                $("#stockInModal").modal("show");
            });
        }
    });

    $("#DT_StockInHistory").DataTable({
        "processing": true,
        "ajax": {
           "url": "ajax/getStockInHistory",
           "type": "POST"
        },
        columns: [
            {data : "batch_id"},
            {data : "name"},
            {data : "category_name"},
            {data : "brand"},
            {
             data : "description",
             render: function(data, type, row, meta) {
                return data ? data : "-";
             }
            },
            {data : "quantity"},
            {data : "expiration_date"},
            {data : "encoded_by"},
            {data : "encoded_date"},
        ]
    });

//    Stock Out
    $("#stockOut").on("click", function(e){
        initDT_StockOutHistory();
        $("#stockOutModal").modal("show");
    });

    var DT_ViewBatchList = $("#DT_ViewBatchList").DataTable({
        "processing": true,
        "ajax": {
           "url": "ajax/getBatchItemList",
           "type": "POST"
        },
        columns: [
            { data: "batch_id", "render": data => data ? data : "-" },
            { data: "name", "render": data => data ? data : "-" },
            { data: "brand", "render": data => data ? data : "-" },
            { data: "description", "render": data => data ? data : "-" },
            { data: "quantity" },
            {
               "render": function(data, type, row, meta) {
                    return `<input class="quantity form-control" type="number" />`
               }
            },
            { data: "unit" },
            {
               data: "expiration_date",
               "render": function(data, type, row, meta) {
                   return data ? data : "-";
                }
            },
            {
              "render": function(data, type, row, meta) {
                return `<a style="cursor:pointer;" class="remove"><i class="fa-solid fa-lg fa-circle-minus" style="color: #f00f0f;"></i></a>`
              }
            },
        ],
        fnCreatedRow: function(row, data, dataIndex) {
        }
    });

    $("#btnStockOut").on("click", function () {
        let batchData = [];
        let hasInvalidEntry = false;

        $("#stockOutModal").modal("hide");

        $("#DT_ViewBatchList tbody tr").each(function () {
            let $row = $(this);
            let rowData = DT_ViewBatchList.row($row).data();

            if (!rowData) return; // Skip if no data

            let quantity = $row.find(".quantity").val();
            let availStock = parseInt(rowData.quantity);

            if(quantity != 0 || quantity != ""){
                if (parseInt(quantity) > availStock) {
                    Swal.fire("Quantity to stock out should not be greater than available stock", "", "error").then(() => {
                        $("#stockOutModal").modal("show");
                    });
                    hasInvalidEntry = true;
                    return false; // break loop
                }

                batchData.push({
                    batch_id: rowData.batch_id,
                    id: rowData.id,
                    brand: rowData.brand,
                    description: rowData.description,
                    expiration_date: rowData.expiration_date,
                    quantity: parseInt(quantity),
                });
            }
        });

        if (hasInvalidEntry) return;

        if (batchData.length > 0) {
            Swal.fire({
                icon: "warning",
                title: "Do you want to save these data?",
                showCancelButton: true,
                confirmButtonText: "Save"
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url: "ajax/stockOut",
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(batchData),
                        success: function (response) {
                            if (response.status === "success") {
                                Swal.fire(response.message, "", "success").then(() => {
                                    location.reload();
                                });
                            } else {
                                Swal.fire(response.message, "", "error");
                            }
                        },
                        error: function (error) {
                            Swal.fire("Error in stock-out process", "", "error");
                            console.error("Error in stock-out process:", error);
                        }
                    });
                } else {
                    $("#stockOutModal").modal("show");
                }
            });
        } else {
            Swal.fire("Please insert quantity to stock out!", "", "error").then(() => {
                $("#stockOutModal").modal("show");
            });
        }
    });


    function toggleButton() {
            $('#btnStockOut').prop('disabled', DT_ViewBatchList.rows().count() === 0);
    }

     toggleButton();

    DT_ViewBatchList.on('draw', function () {
             toggleButton();
    });

   function initDT_StockOutHistory(){
        $("#DT_StockOutHistory").DataTable().destroy();
        $("#DT_StockOutHistory").DataTable({
            "processing": true,
            "ajax": {
               "url": "ajax/getStockOutHistory",
               "type": "POST"
            },
            columns: [
                {data : "batch_id"},
                {data : "name"},
                {data : "category_name"},
                {data : "brand"},
                {
                 data : "description",
                 render: function(data, type, row, meta) {
                    return data ? data : "-";
                 }
                },
                {data : "quantity"},
                {
                    data : "expiration_date",
                     render: function(data, type, row, meta) {
                        return data ? data : "-";
                     }
                },
                {data : "encoded_by"},
                {data : "encoded_date"},
            ]
        });
   }

});