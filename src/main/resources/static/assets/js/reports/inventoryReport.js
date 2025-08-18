$(document).ready(function(){
    $("#DT_InventoryReport").DataTable({
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
            url: '/reports/ajax/getItemList', // PHP/Node/any server script
            type: 'POST',                  // or 'GET'
        },
        columns: [
            { data: 'item_code' },
            { data: 'batch_code' },
            { data: 'item_name' },
            { data: 'category' },
            { data: 'unit_measurement' },
            { data: 'stock' },
            {
                data: 'expiration_date',
                render : function(data, type, row){
                    return data ? data : '-';
                }
            },
        ]

    });
});