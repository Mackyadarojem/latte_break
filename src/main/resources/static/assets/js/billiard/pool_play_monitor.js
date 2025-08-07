function updateDateTime() {
    let now = new Date();
    let options = {
        weekday: 'long', year: 'numeric', month: 'long', day: 'numeric',
        hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: true
    };
    $("#datetime").text(now.toLocaleString('en-US', options));
}

$(document).ready(function() {
    updateDateTime(); // Initial call
    setInterval(updateDateTime, 1000); // Update every second

    $("#DT_PoolMonitor").DataTable();
});