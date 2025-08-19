$(document).ready(function(){

    DT_InventoryReport();

    function DT_InventoryReport(){
        $('#DT_InventoryReport').DataTable().destroy();

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
                url: '/reports/ajax/getItemList',
                type: 'POST',
                data: function (d) {
                    d.date_from = $('#date_from_inventory').val();
                    d.date_to = $('#date_to_inventory').val();
                    d.category_id = $('#category_id_inventory').val();
                }
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
    }

    $("#form_search_inventory").on("submit", function(e){
        e.preventDefault();

        DT_InventoryReport();
    });
});