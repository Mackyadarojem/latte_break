$(document).ready(function () {

    let product = [];

    $(".addToCart").on("click", function () {
        var id = $(this).data("id");
        var productName = $("#productName" + id).val();
        var priceLarge = $("#priceLarge" + id).val();
        var priceMedium = $("#priceMedium" + id).val();
        var price = $("#price" + id).val();
        var quantity = $("#quantity" + id).val();
        if ($("#tab-pane0").hasClass("show active")) {
            var size = $(`input[name="1size${id}"]:checked`).val();
            var type = $(`input[name="1type${id}"]:checked`).val();
        }else{
            var size = $(`input[name="size${id}"]:checked`).val();
            var type = $(`input[name="type${id}"]:checked`).val();
        }
        var intPriceLarge = parseFloat(priceLarge.replace(/[^\d.]/g, ''));
        var intPriceMedium = parseFloat(priceMedium.replace(/[^\d.]/g, ''));
        var intPrice = parseFloat(price.replace(/[^\d.]/g, ''));
        var drink = $("#drink" + id).val();

        console.log("price >>>" + price);

        // Create a base product object
        const selectedProduct = {
            productId: id,
            quantity : quantity,
            productName: productName,
            priceLarge: intPriceLarge,
            priceMedium: intPriceMedium,
            price : intPrice,
            size: size,
            type: type,
            drink : drink
        };

        // Attach the base object temporarily to the modal as a data attribute
        $("#customizeOrder").data("baseProduct", selectedProduct);
            console.log(drink);
            // Show the modal
            if(drink == true || drink == "true"){
                $("#customizeOrder").modal("show");
            }else{
                $("#addToCart").trigger("click");
            }
    });

    $('#customizeOrder').on('hidden.bs.modal', function () {
        $(this).find('input[type="checkbox"]:checked').prop('checked', false);
    });

    $("#addToCart").on("click", function () {
        var totalAddOnsPrice = 0;

        var addOnsPrice = $('input[name="addOns"]:checked').map(function () {
            return $(this).data("price");
        }).get();

        // Calculate total
        $('input[name="addOns"]:checked').each(function () {
            totalAddOnsPrice += $(this).data("price");
        });

        var addOnsName = $('input[name="addOns"]:checked').map(function () {
            return $(this).data("name");
        }).get();

        var ice = $('input[name="ice"]:checked').val();
        var sugarLevel = $('input[name="sugar"]:checked').val();

         var addOns = [{
            addOnsName : addOnsName,
            addOnsPrice : addOnsPrice
         }];

        // Retrieve the base product object stored on the modal
        const baseProduct = $("#customizeOrder").data("baseProduct");

        // Generate a unique ID for each cart item for deletion
        let uniqueId = "cartItem_" + Date.now();

        let basePrice = 0;
        // Compute base price based on selected size
        if(baseProduct.drink == "true"){
          basePrice = baseProduct.size === "large" ? baseProduct.priceLarge : baseProduct.priceMedium;
        }else{
          basePrice = baseProduct.price;
        }

        let quantity = baseProduct.quantity;
        let totalBasePrice = basePrice * quantity;
        let totalPrice = totalBasePrice + totalAddOnsPrice;

         // Merge with customization
        const fullProduct = {
            ...baseProduct,
            addOnsName: addOnsName,
            addOnsPrice: addOnsPrice,
            ice: ice,
            sugarLevel: sugarLevel,
            totalAddOnsPrice: totalAddOnsPrice,
            uniqueId: uniqueId,
            basePrice : basePrice,
            totalPrice : totalPrice,
            addOns : addOns
        };

            // Push the complete product to the  cart
        product.push(fullProduct);

        let addOnsHtml = '';
        let priceQuantity = "";

        if (baseProduct.drink == "true") {
            fullProduct.addOnsName.forEach((name, index) => {
                addOnsHtml += `<div>${name} - ₱${fullProduct.addOnsPrice[index]}</div>`;
            });
            priceQuantity = `
            <div style="margin-top:4px;">${quantity} x ${fullProduct.size} - ₱${totalBasePrice}</div>
            <div style="margin-top:4px;">Sugar - ${fullProduct.sugarLevel}</div>
            <div style="margin-top:4px;">Ice - ${fullProduct.ice}</div>
            `;
        }else{
            priceQuantity = `<div style="margin-top:4px;">${quantity}x - ₱${totalBasePrice}</div>`;
        }


        let cartItemHtml = `
            <div class="cart-item" id="${uniqueId}" style="border:1px solid #ccc; border-radius:10px; padding:16px; margin-bottom:10px; position:relative;">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <strong>${fullProduct.productName}</strong>
                    <a data-id="${uniqueId}"  class="text-danger cursor-pointer removeItem"><i class="fa-solid fa-trash"></i></a>
                </div>
                    ${priceQuantity}
                    ${addOnsHtml}
                <hr />
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <label>
                        <input type="checkbox" class="discountToggle" data-id="${uniqueId}"> Discount this item
                    </label>
                    <div class="itemTotal" data-base="${totalPrice}"><strong>₱${totalPrice}</strong></div>
                </div>
            </div>
        `;

        $("#cartContainer").append(cartItemHtml);
        $("#customizeOrder").modal("hide");
        updateSubTotal();
        updateTotal();
    });

    $("#cartContainer").on("change", ".discountToggle", function () {
        let parent = $(this).closest(".cart-item");
        let totalDiv = parent.find(".itemTotal");
        let basePrice = parseFloat(totalDiv.data("base"));

        if ($(this).is(":checked")) {
            let discounted = basePrice * 0.8;
            let discountAmount = basePrice - discounted;
            totalDiv.data('discount', discountAmount);
            totalDiv.html(`<strong>₱${discounted.toFixed(2)}</strong>`);

        } else {
            totalDiv.data('discount', 0);
            totalDiv.html(`<strong>₱${basePrice.toFixed(2)}</strong>`);
        }
        updateDiscount();
        updateTotal();
    });

    $("#cartContainer").on("click", ".removeItem", function () {
        var id = $(this).data("id");
        $("#selectedId").val(id);
        $("#admin_verification_modal").modal("show");
    });

    $("#btn_voidTransaction").on("click", function(){
        $("#selectedId").val("all");
        $("#admin_verification_modal").modal("show");
    });

    function voidItem() {
       var id = $("#selectedId").val();
       if (id === "all") {
            let requestData = {
                productList: product,
                transaction : "Void Transaction"
            };
            $.ajax({
               url: '/pos/voidTransaction',
               type: 'POST',
               data: JSON.stringify(requestData), // product should be an array of objects
               contentType: 'application/json',
               success: function(response) {
                    if(response.status == "success"){
                        $(".cart-item").remove();
                        product = [];
                    }else{
                        Swal.fire({
                            icon: "error",
                            title: response.message
                        });
                    }
                },
                error: function(xhr) {
                    console.error("Error:", xhr.responseText);
                }
            });
       } else {
            var selectedProduct = product.find(function (item) {
                return item.uniqueId === id;
            });
            let requestData = {
                productList: product,
                transaction : "Void Item"
            };
            $.ajax({
               url: '/pos/voidTransaction',
               type: 'POST',
               data: JSON.stringify(requestData), // product should be an array of objects
               contentType: 'application/json',
               success: function(response) {
                    if(response.status == "success"){
                       $("#" + id).remove();
                            product = product.filter(function (item) {
                            return item.uniqueId !== id;
                       });
                    }else{
                        Swal.fire({
                            icon: "error",
                            title: response.message
                        });
                    }
                },
                error: function(xhr) {
                    console.error("Error:", xhr.responseText);
                }
            });
       }
       updateSubTotal();
       updateDiscount();
       updateTotal();
   }

    $("#form_verifyPassword").on("submit", function(e){
        e.preventDefault();
        var password = $("#admin_password").val();
        console.log(password);
        $.ajax({
          url: "/pos/verifyAdminPassword",
          type: "POST",
          data: {
            password: password
          },
          success: function (response) {
            if(response.status == "success"){
                $(".adminError").addClass("d-none");
                voidItem();
                $("#admin_verification_modal").modal("hide");
            }else{
                $(".adminError").removeClass("d-none");
            }
          },
          error: function (xhr, status, error) {
            console.error("Error: " + error);
          }
        });
    });

    $("#admin_verification_modal").on('hidden.bs.modal', function (){
        $("#admin_password").val("");
        $("#selectedId").val("");
    });

    function updateSubTotal(){
       let total = 0;

       $(".itemTotal").each(function() {
           let value = parseFloat($(this).data("base")) || 0;
           total += value;
       });

       $("#subTotal").text(total);
    }

    function updateDiscount(){
       var total = 0;

       $(".itemTotal").each(function() {
           var value = parseFloat($(this).data("discount")) || 0;
           total += value;
       });
        console.log(total);
       $("#totalDiscount").text(total);
    }

    function updateTotal(){
       var totalDiscount = $("#totalDiscount").text();
       var subTotal = $("#subTotal").text();

       var total = subTotal - totalDiscount;

       $("#total").text(total);
    }

    $("#placeHolder").on("click", function(e){
        if (product.length === 0) {
            Swal.fire("Warning", "Please select a product first.", "warning");
            return;
        }
        var total = $("#total").text();
        $("#amountToPay").text(total);
        $("#paymentMethod").modal("show");
    });


    $("#cashTendered").on("keyup", function(){
        var value = $(this).val();
        var amountToPay =  $("#amountToPay").text();
        var changed = value - amountToPay  ;
        $("#changed").text(changed);
        $("#totalAmountTendered").text(value);
    });

    $("#paymentAmount").on("keyup", function(){
        var value = $(this).val();
        var amountToPay =  $("#amountToPay").text();
        var changed = value - amountToPay  ;
        $("#changed2").text(changed);
        $("#totalAmountTendered2").text(value);
    });

    $("#processPayment").off("click").on("click", function(){
        var activeTab = $('#pills-tab .nav-link.active').data("id");
        var amountToPay = $("#amountToPay").text();

        const checkedValue = $('input[name="serviceType"]:checked').val();
        var senderName;
        var paymentAmount;
        var referenceNo;
        var changed;
        var totalAmountTendered;
        var cashTendered;
        $(".serviceType").text(checkedValue);

        if(activeTab == "cash"){
             cashTendered = $("#cashTendered").val();
             if(cashTendered == "" || parseFloat(cashTendered) < parseFloat(amountToPay)){
                 $(".error").removeClass("d-none");
                 return;
            }
            $(".error").addClass("d-none");
            changed = $("#changed").text();
            totalAmountTendered = $("#totalAmountTendered").text();
            $("#receipt_cashTendered").text(totalAmountTendered);
            $("#receipt_change").text(changed);
        }else{
            paymentAmount = $("#paymentAmount").val();
            senderName = $("#senderName").val();
            referenceNo = $("#referenceNo").val();
            if(paymentAmount == "" || parseFloat(paymentAmount) < parseFloat(amountToPay) || senderName == "" || referenceNo == ""){
                 $(".error").removeClass("d-none");
                 return;
            }
            $(".error").addClass("d-none");
            changed = $("#changed2").text();
            totalAmountTendered = $("#totalAmountTendered2").text();
            $("#receipt_cashTendered").text(totalAmountTendered);
            $("#receipt_change").text(changed);
        }

        $("#paymentMethod").modal("hide");

        Swal.fire({
           icon: "warning",
           title: "Do you want to complete this transaction?",
           showCancelButton: true,
           confirmButtonText: "Save"
        }).then((result) => {
            if(result.isConfirmed){
                let requestData = {
                    total_amount: amountToPay,
                    cash_tendered : $("#receipt_cashTendered").text(),
                    change : $("#receipt_change").text(),
                    mode_of_payment : activeTab,
                    senderName : senderName,
                    referenceNo : referenceNo,
                    serviceType : $(".serviceType").text(),
                    subTotal : $("#subTotal").text(),
                    discount : $("#totalDiscount").text(),
                    productList: product
                };
               $.ajax({
                   url: '/pos/saveInvoice',
                   type: 'POST',
                   data: JSON.stringify(requestData), // product should be an array of objects
                   contentType: 'application/json',
                   success: function(response) {
                        if(response.status == "success"){
                            Swal.fire({
                             icon: "success",
                             title: response.message,
                            }).then((result) => {
                                var employeeUsername = $("#employeeUsername").val();
                                product.forEach(product => {
                                    let addOnsHtml = '';
                                    let addOnsContainer = ''; // This will be included conditionally
                                    if(product.productName != "billiard"){
                                        if (Array.isArray(product.addOnsName) && product.addOnsName.length > 0) {
                                            addOnsHtml = '<span class="d-flex flex-column text-muted">';
                                            for (let i = 0; i < product.addOnsName.length; i++) {
                                                addOnsHtml += `<small>${product.addOnsName[i]} - ${product.addOnsPrice[i]}</small>`;
                                            }
                                            addOnsHtml += '</span>';

                                            // Wrap entire section only if add-ons exist
                                            addOnsContainer = `
                                                <div class="d-flex gap-2">
                                                    <span class="fw-bold"><small>Add Ons :</small></span>
                                                    ${addOnsHtml}
                                                </div>`;
                                        }
                                        const productHtml = `

                                           <div class="border-bottom d-flex align-items-center w-100">
                                             <div class="d-flex flex-column w-100">
                                               <span class="fw-bold">${product.productName}</span>
                                               <span class="fw-bold">${product.quantity} x ₱${product.basePrice}</span>
                                               ${addOnsContainer}
                                             </div>
                                             <div class="text-end d-flex flex-column w-100">
                                               <span class="text-muted">₱${product.basePrice}</span>
                                               ${
                                                 product.totalAddOnsPrice !== 0
                                                   ? `<span class="text-muted">₱${product.totalAddOnsPrice}</span>`
                                                   : ''
                                               }
                                               <span class="fw-bold">₱${product.totalPrice}</span>
                                             </div>
                                           </div>`;

                                        $('.receipt_orderContainer').append(productHtml);
                                    }else{
                                        const billiardHtml = `
                                            <div class="d-flex align-items-center w-100">
                                                <div class="d-flex flex-column w-100">
                                                    <span class="fw-bold">${product.productName.toUpperCase()}</span>
                                                    <span class="fw-bold">${product.timeValue} x ₱120</span>
                                                </div>
                                                <div class="text-end d-flex flex-column w-100">
                                                     <span class="fw-bold">₱${product.totalPrice}</span>
                                                </div>
                                            </div>
                                        `;
                                        $('.receipt_billiardContainer').append(billiardHtml);
                                    }

                                });
                                $("#receipt_amountToPay").text(amountToPay);
                                $("#receipt_amountToPay").text(amountToPay);
                                $("#receipt_discount").text($("#totalDiscount").text());
                                $("#receipt_subtotal").text($("#subTotal").text());
                                $("#employeeUsernameText").text(employeeUsername);
                                var today = new Date();
                                // Format: YYYY-MM-DD
                                var formattedDate = today.getFullYear() + '-' +
                                String(today.getMonth() + 1).padStart(2, '0') + '-' +
                                String(today.getDate()).padStart(2, '0');
                                $("#dateToday_receipt").text(formattedDate);
                                $("#invoice_receipt").text(response.invoiceNumber);
                                $("#receipt_modal").modal("show");
                            });
                        }else{
                            Swal.fire({
                                 icon: "error",
                                 title: response.message
                            });
                        }
                   },
                   error: function(xhr) {
                       console.error("Error:", xhr.responseText);
                   }
               });
            }
        });
    });

    $('#receipt_modal').on('hidden.bs.modal', function () {
        $('.receipt_orderContainer').empty();
        $('.receipt_billiardContainer').empty();
    });

    $("#paymentMethod").off('hidden.bs.modal').on('hidden.bs.modal', function () {
        $('#paymentMethod input').val("");
        $("#changed2").text("0.00");
        $("#totalAmountTendered2").text("0.00");
        $("#changed").text("0.00");
        $("#totalAmountTendered").text("0.00");
   });

    $("#saveAndPrint").on("click", function(){
       var content = $('#receipt_modal .modal-body').html();

          var printWindow = window.open('', '', 'height=600,width=800');
          printWindow.document.write('<html><head><title>Print Receipt</title>');

          // Include CSS files
          printWindow.document.write('<link rel="stylesheet" href="assets/bootstrap-5.3.3-dist/css/bootstrap.min.css">');
          printWindow.document.write('<link rel="stylesheet" href="assets/fontawesome-free-6.7.2-web/css/all.min.css">');
          printWindow.document.write('<link rel="stylesheet" href="/vendor/css/rtl/core.css">');
          printWindow.document.write('<link rel="stylesheet" href="/vendor/css/rtl/theme-default.css">');
          printWindow.document.write('<link rel="stylesheet" href="/vendor/fonts/boxicons.css">');
          printWindow.document.write('<link rel="stylesheet" href="/vendor/fonts/fontawesome.css">');
          printWindow.document.write('<link rel="stylesheet" href="assets/dataTable/datatables.min.css">');

          // Optional custom styles
          printWindow.document.write('<style>body { font-family: Arial; margin: 20px; }</style>');

          printWindow.document.write('</head><body>');
          printWindow.document.write(content);
          printWindow.document.write('</body></html>');

          printWindow.document.close();
          printWindow.focus();

          printWindow.onload = function () {
            printWindow.print();
            printWindow.close();
          };
    });

        /*    BILLIARDS FUNCTION*/

    $("#billiardBtn").on("click", function(){
        $("#billiards_modal").modal("show");
    });

    $("#billiardsMonitorBtn").on("click", function(){
        $("#billiards_monitor_modal").modal("show");
    });

    var DT_PoolPlayMonitor = $("#DT_PoolPlayMonitor").DataTable({
        "ajax": {
            "url": "ajax/getBilliardSched",
            "type": "POST"
        },
        "columns" :[
            {
                data: null,
                "render" : function(data, type, row, meta){
                     return meta.row + 1;
                }
            },
            {
                data: "customerName",
            },
            {
                data: "duration",
                "render": function(data, type, row, meta) {
                   return (data && row.open_hour == false) ? data : '-';;
                }
            },
            {
                data: "open_hour",
                "render": function(data, type, row, meta) {
                   if(data){
                        return `<span class="text-warning">Open Hour</span>`;
                   }else{
                        return `<span class="text-primary">Not Open Hour</span>`;
                   }
                }
            },
            {
                data: "status",
                "render": function(data, type, row, meta) {
                    if(data == "Cancelled" || data == "Stopped"){
                        return `<span class="text-danger">${data}</span>`;
                    }else if (data == "Paid"){
                        return `<span class="text-warning">${data}</span>`;
                    }else if (data == "Running"){
                        return `<span class="text-primary">${data}</span>`;
                    }else if (data == "Not Started"){
                        return `<span class="text-success">${data}</span>`;
                    }else{
                        return data ? data : "";
                    }
                }
            },
            {
                data: "start_time",
                "render": function(data, type, row, meta) {
                   return (data && data !== '') ? data : '-';
                }
            },
            {
                data: "end_time",
                "render": function(data, type, row, meta) {
                   return (data && data !== '') ? data : '-';
                }
            },
            {
                data: "id",
                "render": function(data, type, row, meta) {
                   if(row.status == "Not Started"){
                        return `
                        <div class="d-flex gap-2">
                            <button class="btn_start_time btn-primary btn-sm btn" data-id="${data}">START</button>
                            <button class="btn_cancel_sched btn-danger btn-sm btn" data-id="${data}">CANCEL</button>
                        </div>
                        `;
                   }else if (row.status == "Running" && row.open_hour == false){
                        return `<button class="btn_proceed_payment btn-warning btn-sm btn" data-id="${data}">Procced to Payment</button>`;
                   }else if (row.status == "Running" && row.open_hour == true){
                        return `<button class="btn_stop_time btn-danger btn-sm btn" data-id="${data}">Stop</button>`;
                   }else if (row.status == "Stopped"){
                        return `<button class="btn_proceed_payment btn-warning btn-sm btn" data-id="${data}">Procced to Payment</button>`;
                   }else if (row.status == "Cancelled"){
                        return ``;
                   }else if (row.status == "Paid"){
                        return ``;
                   }
                }
            }
        ],
        fnCreatedRow: function(row, data, dataIndex) {
            $(row).find('.btn_start_time').off('click').on('click', function(){
                var id = $(this).data("id");
                $.get('/billiard/ajax/startTime', {
                    open_hour: data.open_hour,
                    duration: data.duration.trim().replace(/\s+/g, ''),
                    id : id
                }, function(response) {
                   if(response.status == "success"){
                        DT_PoolPlayMonitor.ajax.reload();
                   }else{
                        alert("error");
                   }
                });
             });

            $(row).find(".btn_proceed_payment").off('click').on("click", function(){
                 var duration;
                 var hourStatus;
                 if(data.open_hour){
                    hourStatus = "Open Hour";
                    var startTime = new Date(data.start_time);
                    var endTime = new Date(data.end_time);

                    var dm  = endTime - startTime;
                    const totalSeconds = Math.floor(dm / 1000);
                    const hours = String(Math.floor(totalSeconds / 3600)).padStart(2, '0');
                    const minutes = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0');
                    const seconds = String(totalSeconds % 60).padStart(2, '0');
                    duration = `${hours}:${minutes}:${seconds}`;
                 }else{
                    hourStatus = "Not Open Hour";
                    duration = data.duration;
                 }

                 let uniqueId = "cartItem_" + Date.now();
                 var timeValue = getTimeValue(duration);
                 var totalPrice = 120 * timeValue;
                 let cartItemHtml = `
                     <div class="cart-item" id="${uniqueId}" style="border:1px solid #ccc; border-radius:10px; padding:16px; margin-bottom:10px; position:relative;">
                         <div style="display:flex; justify-content:space-between; align-items:center;">
                             <strong>BILLIARDS</strong>
                             <a data-id="${uniqueId}"  class="text-danger cursor-pointer removeItem"><i class="fa-solid fa-trash"></i></a>
                         </div>
                         <div class="d-flex">
                             <span>${hourStatus}</span>
                         </div>
                         <div class="d-flex">
                             <span>${duration}</span>
                         </div>
                         <div class="d-flex">
                             <span>${timeValue} x  ₱120</span>
                         </div>
                         <hr>
                         <div style="display:flex; justify-content:space-between; align-items:center;">
                               <span></span>
                               <div class="itemTotal" data-base="${totalPrice}"><strong>₱${totalPrice}</strong></div>
                         </div>
                     </div>
                 `;

                 product.push({
                     productName : "billiard",
                     duration : duration,
                     totalPrice : totalPrice,
                     timeValue : timeValue,
                     hourStatus : hourStatus,
                     uniqueId : uniqueId,
                     billiard_id : data.id
                 });

                 $("#cartContainer").append(cartItemHtml);
                 updateSubTotal();
                 updateTotal();
                 $("#billiards_monitor_modal").modal("hide");
             });

            $(row).find('.btn_stop_time').off('click').on('click', function(){
                var id = $(this).data("id");
                $.get('/billiard/ajax/endTime', {
                    id : id
                }, function(response) {
                   if(response.status == "success"){
                        DT_PoolPlayMonitor.ajax.reload();
                   }else{
                        alert("error");
                   }
                });
            });

            $(row).find('.btn_cancel_sched').off('click').on('click', function(){
                 var id = $(this).data("id");
                    $.get('/billiard/ajax/cancelSched', {
                        id : id
                    }, function(response) {
                       if(response.status == "success"){
                            DT_PoolPlayMonitor.ajax.reload();
                       }else{
                            alert("error");
                       }
                    });
            });
        }
    });

   $("#openHour").on("change", function(){
        if ($(this).is(":checked")) {
            $(".durationContainer").addClass("d-none");
            $("#hour").prop("required", false);
            $("#minutes").prop("required", false);
            $("#hour").val("0");
            $("#minutes").val("0");
        } else {
            $(".durationContainer").removeClass("d-none");
            $("#hour").prop("required", true);
            $("#minutes").prop("required", true);
        }
   });

    $("#form_billiard").on("submit", function(e){
          e.preventDefault();

          // Use FormData to allow appending custom values
          var formData = new FormData(this);

          // Get the checkbox state
          var open_hour = $("#openHour").is(":checked"); // corrected method
          formData.append("open_hour", open_hour ? true : false); // send 1 or 0

          // Debug: Log all form data
          for (var pair of formData.entries()) {
              console.log(pair[0]+ ': ' + pair[1]);
          }

           $.ajax({
               url: '/ajax/addBilliardSched',
               method: 'POST',
               data: formData,
               contentType: false,
               processData: false,
               success: function(response) {
                  $("#billiards_modal").modal("hide");

                  if(response.status == "success"){
                     Swal.fire(response.message, "", "success").then(() => {
                        location.reload();
                     });
                  }else{
                     Swal.fire(response.message, "", "error");
                  }
               }
           });
    });

    function getTimeValue(timeStr) {
        const [hours, minutes, seconds] = timeStr.split(':').map(Number);
        const totalMinutes = hours * 60 + minutes;

        if (totalMinutes < 30) {
          return 0.5;
        } else if (minutes >= 1 && minutes <= 10) {
          return hours + 0.5;
        } else if (minutes === 0) {
          return hours;
        } else {
          return hours + 1; // round up for partial hours over 10 minutes
        }
    }

   $("#billiards_monitor_modal").on("shown.bs.modal", function() {
       DT_PoolPlayMonitor.ajax.reload(null, false);
   });


});