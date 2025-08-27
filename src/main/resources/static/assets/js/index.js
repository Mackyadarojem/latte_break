$(document).ready(function () {
    var scheduledDates = [];
    var role_id = $("#role_id").val();

    initEventSchedules();

    function initEventSchedules(){
        $.get("/home/ajax/getAllEvent", function(res){
            var data = res.data;

            data.forEach(data => {
              scheduledDates.push({
                "id" : data.id,
                "date" : data.date
              })
            });

            console.log(scheduledDates);
            initCalendar();
        });
    }

    function initCalendar(){
        $("#calendar").datepicker({
            beforeShowDay: function(date) {
                let y = date.getFullYear();
                let m = (date.getMonth() + 1).toString().padStart(2, '0');
                let d = date.getDate().toString().padStart(2, '0');
                let formattedDate = `${y}-${m}-${d}`;

                let hasSchedule = scheduledDates.some(e => e.date === formattedDate);

                if (hasSchedule) {
                    return [true, "has-schedule", "Scheduled!"];
                }
                 return [true, "", ""];
            },
            onSelect: function(date) {
                let selectedDate = $(this).datepicker("getDate"); // get Date object
                let y = selectedDate.getFullYear();
                let m = (selectedDate.getMonth() + 1).toString().padStart(2, '0');
                let d = selectedDate.getDate().toString().padStart(2, '0');
                // format as MM-DD-YYYY
                let formattedDate = `${y}-${m}-${d}`;
                let events = scheduledDates.filter(e => e.date === formattedDate);

                if (events.length > 0) {
                    var date = events[0].date;
                    initDT_Event(date);
                    $("#event_modal").modal("show");
                }
            }
        });
    }

    function initDT_Event(date){
        if ($.fn.DataTable.isDataTable("#DT_EventList")) {
            $("#DT_EventList").DataTable().clear().destroy();
        }
        $("#DT_EventList").DataTable({
            ajax: {
                url: "/home/ajax/getAllEvent",
                type: "GET",
                data: function(d) {
                    d.date_from = date
                }
            },
            columns : [
                {
                    data : null,
                    render : function (data, type ,row, meta){
                        return meta.row + 1;
                    }
                },
                {
                    data : "event_name"
                },
                {
                    data : "date",
                    render : function (data, type ,row, meta){
                        return data  + ' ' + row.time;
                    }
                },
                {
                    data : "participants_name",
                    render : function (data, type ,row, meta){
                        return data;
                    }
                },
            ]
        });
    }
    var features;
    if(role_id == 1){
         features = [
           { name: 'Dashboard Overview', link: '/home' },
           { name: 'POS', link: '/pos' },
           { name: 'Reports', link: '/reports' },
           { name: 'Item List', link: 'settings.html' },
           { name: 'Pool Monitor', link: '/poolMonitor' },
           { name: 'Schedule List', link: '/scheduleList' },
           { name: 'Sales Report', link: '/salesReport' },
           { name: 'Inventory Report', link: '/inventoryReport' },
           { name: 'Transaction List', link: '/transactionList' },
           { name: 'Item List', link: '/itemList' },
           { name: 'Archive Item List', link: '/archiveItemList' },
           { name: 'Product List', link: '/productList' },
           { name: 'Account List', link: '/accountList' },
           { name: 'Change Password', link: '/changePass' },
           { name: 'Update Security Questions', link: '/updateSecurityQuestion' },
        ];
    }else{
         features =  [
           { name: 'Dashboard Overview', link: '/home' },
           { name: 'POS', link: '/pos' },
           { name: 'Pool Monitor', link: '/poolMonitor' },
           { name: 'Schedule List', link: '/scheduleList' },
           { name: 'Transaction List', link: '/transactionList' },
           { name: 'Change Password', link: '/changePass' },
        ];
    }


    $('#searchInput').on('input', function () {
        const searchText = $(this).val().toLowerCase();
        const filtered = features.filter(f =>
          f.name.toLowerCase().includes(searchText)
        );

        $('#results').html('');
        if (searchText && filtered.length > 0) {
          filtered.forEach(f => {
            $('#results').append(`<div class="feature"><a href="${f.link}">${f.name}</a></div>`);
          });
        } else if (searchText) {
          $('#results').html('<div>No features found.</div>');
        }
      });
});