$(document).ready(function(){

    initDT_TransactionList();

    function initDT_TransactionList(){
        if ( $.fn.DataTable.isDataTable('#DT_TransactionList') ) {
            $('#DT_TransactionList').DataTable().destroy();
        }
        $("#DT_TransactionList").DataTable({
            "processing": true,
            "ajax": {
                "url": "transactionList/ajax/getTransactionList",
                "type": "POST",
                "data": function(d) {
                    d.transaction = $('#transaction').val();
                    d.date_from = $('#date_from').val();
                    d.date_to = $('#date_to').val();
                }
            },
            "columns": [
                { "data": "transaction" },
                { "data": "products" },
                { "data": "created_by" },
                { "data": "created_at" },
            ]
        });
    }


    $("#form_search").on("submit", function(e){
        e.preventDefault();

        initDT_TransactionList();
    });
});