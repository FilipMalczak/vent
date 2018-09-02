/*
Checking checbkox should enable datetime picker, unchecking should start timeout that updates chosen date every second and disable picker.
Every action should store the state in local storage.
On page load it should be loaded and widget state should match it.
*/

$(function(){
    $.get("/api/v1/db/temporal/timezone", function(d) {
        $("#timezone").text(d);
    });
});

//fixme date picker is unresponsive now; figure out inlining that shit