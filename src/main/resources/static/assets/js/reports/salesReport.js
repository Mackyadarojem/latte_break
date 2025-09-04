$(document).ready(function(){
    $("#DT_SalesReport").DataTable({
        dom: 'Bfrtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: '📊 Export Excel',
                title: 'My Data Export',
                className: 'btn btn-primary' // ✅ added class
            },
            {
                extend: 'pdfHtml5',
                text: '📑 Export PDF',
                title: 'My Data Export',
                orientation: 'landscape', // optional
                pageSize: 'A4',            // optional
                className: 'btn btn-primary' // ✅ added class
            },
            {
                extend: 'print',
                text: '🖨️ Print Table',
                className: 'btn btn-primary' // ✅ added class
            }
        ],
        ajax: {
            url: '/reports/ajax/getSalesList',
            type: 'POST',
        },
        columns: [
            { data: 'productName' },
            { data: 'category_name' },
            {
                data: 'add_ons',
                render : function (data, type ,row){
                    return data ? data : '-';
                }
            },
            {
                data: 'quantity'
            },
            {
                data: 'size',
                render : function (data, type ,row){
                    return data ? data : '-';
                }
            },
            {
                data: 'discount',
                render : function (data, type ,row){
                    return data ? data : '-';
                }
            },
            {
                data: 'totalPrice',
                render : function(data, type, row){
                    return data - row.discount;
                }
            },
            {
                data: 'totalPrice',
                render : function(data, type, row){
                    return data;
                }
            },
        ]
    });
});