$(document).ready(function(){
   $("#DT_EventList").DataTable({
    processing: true,
    ajax: {
        url: '/event_management/ajax/getAllEvent',
        type: 'POST'
    },
    columns: [
        {
            data: null,
            render : function(data, type, row, meta){
                return meta.row + 1;
            }
        },
        {
            data: 'event_name'
        },
        {
            data: 'purpose'
        },
        {
            data: 'date',
            render : function (data, type, row){
                return formatDate(data)  + ' ' + formatTime(row.time);
            }
        },
        {
            data: 'name'
        },
        {
            data: 'id',
            render : function (data, type , row){
                return `<div class="d-flex gap-2 ">
                    <button class="btn btn-warning btnEdit ">Edit</button>
                    <button class="btn btn-danger btnArchive">Archive</button>
                </div>`
            }
        }
    ],
    createdRow: function (row, data, dataIndex) {
        $(row).find(".btnEdit").off("click").on("click", function(){
            $("#method").val("edit");
            $("#event_id").val(data.id);
            $("#event_name").val(data.event_name);
            $("#time").val(data.time);
            $("#date").val(data.date);
            $("#purpose").val(data.purpose);
            $("#participants_id").val(data.participants_ids);
            $("#participants").val(data.participants_name);
            $("#add_event_modal_label").text("Edit Event");
            $("#add_event_modal").modal("show");
        });

        $(row).find(".btnArchive").off("click").on("click", function(){
            var id = data.id;

            Swal.fire({
                title: 'Do you want to archive this event?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: "Save"
            }).then((result) => {
                if (result.isConfirmed) {
                    $.get("/event_management/ajax/archiveEvent", {id : id}, function(res){
                        if (res.status == "success") {
                            Swal.fire(
                                'Event Management',
                                res.message,
                                'success'
                            ).then(() => {
                                location.reload();
                            });
                        } else {
                            Swal.fire(
                                'Event Management',
                                res.message,
                                'error'
                            ).then(() => {
                                location.reload();
                            });
                        }
                    });
                }
            });

        });
    }
   });

  let selectedParticipants = [];

   var dtParticipants = $("#DT_Participants").DataTable({
        ajax: '/event_management/ajax/getAllUsers',
        columns: [
            {
                data: 'id',
                render : function (data, type, row){
                    return `<div class="form-check">
                              <input class="form-check-input selectParticipants" type="checkbox" value="" data-id="${data}">
                            </div>`;
                }
            },
            { data: 'name' },
            { data: 'role' }
        ],
        createdRow: function (row, data, dataIndex) {
            $(row).find('.selectParticipants').on("change", function(){
                var checkbox = $(this);

                if (checkbox.is(':checked')) {
                    // Avoid duplicates
                    if (!selectedParticipants.some(p => p.id === data.id)) {
                        selectedParticipants.push({
                            id: data.id,
                            name: data.name
                        });
                    }
                } else {
                    // Remove from array if unchecked
                    selectedParticipants = selectedParticipants.filter(p => p.id !== data.id);
                }
            });
        }
   });

    $("#btn_saveParticipants").on("click", function(){
         let ids = selectedParticipants.map(p => p.id).join(',');
         let names = selectedParticipants.map(p => p.name).join(', ');

        $("#participants").val(names);
        $("#participants_id").val(ids);

        $("#participants_modal").modal("hide");
    });

    $("#addEvent").on("click", function(){
        $("#add_event_modal_label").text("Add Event");
        $("#add_event_modal").modal("show");
    });

    // Pre-check logic when modal opens
    $("#participants_modal").on("shown.bs.modal", function(){
        let ids = $("#participants_id").val().split(',').map(id => id.trim()).filter(id => id !== "");

        // Loop through DataTable rows
        dtParticipants.rows().every(function(){
            let rowData = this.data();
            let checkbox = $(this.node()).find('.selectParticipants');

            if (ids.includes(String(rowData.id))) {
                checkbox.prop('checked', true);

                // Add to array if not already there
                if (!selectedParticipants.some(p => p.id === rowData.id)) {
                    selectedParticipants.push({
                        id: rowData.id,
                        name: rowData.name
                    });
                }
            } else {
                checkbox.prop('checked', false);
            }
        });
    });


    $("#btnBrowseParticipants").on("click", function(){
        $("#participants_modal").modal("show");
    });

    $("#form_addEvent").on("submit", function(e){
        e.preventDefault();
        $("#add_event_modal").modal("hide");
        var method = $("#method").val();
        var formData = $(this).serialize();
        Swal.fire({
            title: 'Do you want to save this event?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: "Save"
        }).then((result) => {
          if (result.isConfirmed) {
            if(method == 'add'){
                $.get("/event_management/ajax/addEvent", formData, function(res){
                    if (res.status == "success") {
                        Swal.fire(
                            'Event Management',
                            res.message,
                            'success'
                        ).then(() => {
                            location.reload();
                        });
                    } else {
                        Swal.fire(
                            'Event Management',
                            res.message,
                            'error'
                        ).then(() => {
                            location.reload();
                        });
                    }
                });
            }else{
                $.get("/event_management/ajax/editEvent", formData, function(res){
                    if (res.status == "success") {
                        Swal.fire(
                            'Event Management',
                            res.message,
                            'success'
                        ).then(() => {
                            location.reload();
                        });
                    } else {
                        Swal.fire(
                            'Event Management',
                            res.message,
                            'error'
                        ).then(() => {
                            location.reload();
                        });
                    }
                });
            }
          }
        });


    });

    function formatDate(date) {
        const dateObj = new Date(date);
        return dateObj.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    function formatTime(time) {
        let [hours, minutes] = time.split(':');
        hours = parseInt(hours, 10);
        const ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12 || 12; // convert to 12-hour format
        return `${hours}:${minutes} ${ampm}`;
    }
});