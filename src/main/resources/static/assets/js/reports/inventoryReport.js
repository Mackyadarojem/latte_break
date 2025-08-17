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
                ]
    });
});