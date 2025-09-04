$(document).ready(function(){
    initDT_AuditLogs();

    $("#form_search").on("submit", function(e){
        e.preventDefault();
        initDT_AuditLogs();
    });

    function initDT_AuditLogs(){
        if ($.fn.DataTable.isDataTable('#DT_AuditLogs')) {
             $('#DT_AuditLogs').DataTable().destroy();
             $('#DT_AuditLogs').empty();
        }

        $("#DT_AuditLogs").DataTable({
            "ajax": {
                "url": "audit_logs/ajax/getAuditLogs",
                "type": "POST",
                "data": function (d) {
                    d.date_from = $('#date_from').val();
                    d.date_to   = $('#date_to').val();
                    d.username = $('#username').val();
                }
            },
            "columns": [
                {
                    "data": null,
                    render : function (data, type, row, meta){
                        return meta.row + 1;
                    }
                },
                { "data": "username" },
                { "data": "action" },
                { "data": "timestamp" }
            ]
        });
    }
});