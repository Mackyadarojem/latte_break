$(document).ready(function(){
    initDT_AuditLogs();


    function initDT_AuditLogs(){
        $("#DT_AuditLogs").DataTable({
            "ajax": {
                "url": "audit_logs/ajax/getAuditLogs",
                "type": "POST"
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