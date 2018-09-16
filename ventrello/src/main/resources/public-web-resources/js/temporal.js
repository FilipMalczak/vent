//todo: add another approach/mode: choosing range and clicking play that replays all actions recorded in vent during that timespan

var nowInstantRepresentation = (function(){
    return {
        asString: function() { return "NOW ("+temporalService.currentTimeForVent()+")"; },
        currentDate: function() { return temporalService.currentTimeForVent(); },
        modifyUrl: function(url){
            return url;
        },
        allowWrite: function(){ return true; }
    };
})();


function fixedInstantRepresentation(date){
    return {
        asString: function() { return "FIXED ("+date+")"; },
        currentDate: function() { return date; },
        modifyUrl: function(url){
            var resultUrl = url;
            if (resultUrl.includes("?"))
                resultUrl = resultUrl+"&";
            else
                resultUrl = resultUrl+"?";
            resultUrl = resultUrl+"queryAt="+date;
            return resultUrl;
        },
         allowWrite: function(){ return false; }
    }
}

var temporalService = (function(){
    var nowUpdateIntervalHandle = null;
    var openable = true;
    var flatpickrConfig = {
        enableTime: true,
        dateFormat: "Y-m-dTH:i:S.000",
        altFormat: "Y.m.d H:i:S",
        altInput: true,
        onChange: function() { changeDateCallback(currentTimeForVent()); }
    };

    var changeDateCallback = function(){}

    var chooser = null;

    function currentConfig(obj){
        obj = obj || {};
        return Object.assign(flatpickrConfig, {clickOpens: openable}, obj);
    }

    function initChooser(){
        chooser = $("#current-date").flatpickr(flatpickrConfig);
    }

    function startUpdates(){
        if (!nowUpdateIntervalHandle){
            nowUpdateIntervalHandle = setInterval(function(){
                $.get("/api/v1/db/temporal/now", function(date){
                    chooser.setDate(date);
                });
            }, 1000);
        }
    }

    function stopUpdates(){
        if (nowUpdateIntervalHandle) {
            clearInterval(nowUpdateIntervalHandle);
            nowUpdateIntervalHandle = null;
        }
    }

    var currentDateRepr = nowInstantRepresentation;

    function currentTimeForVent(){
        return $("#current-date").val()
    }

    var onRefreshListeners = [];

    function triggerRefresh(){
        onRefreshListeners.forEach(function(e) {
            e();
        });
    }

    return {
        initChooser: initChooser,
        setNowApproach: function() {
            openable = false;
            startUpdates();
            changeDateCallback = function() {};
            currentDateRepr = nowInstantRepresentation;

            triggerRefresh();
            $(".only-when-time-is-now").prop("disabled", false);
        },
        setFixedApproach: function(){
            openable = true;
            chooser.set("openable", false);
            stopUpdates();
            changeDateCallback = function(dateStr) {
                currentDateRepr = fixedInstantRepresentation(dateStr);
                triggerRefresh();
            };
            $(".only-when-time-is-now").prop("disabled", true);
        },
        currentTimeForVent: currentTimeForVent,
        currentDateRepr: function() { return currentDateRepr;},
        //todo refreshing should be moved to appropriate dedicated service
        onRefresh: function(listener){
            onRefreshListeners.push(listener);
        },
        triggerRefresh: triggerRefresh
    };
})();

$(function(){
    $.get("/api/v1/db/temporal/timezone", function(d) {
        $("#timezone").text(d);
    });

    temporalService.initChooser();

    $("#current-date").val()

    $("#temporal-approach").change(function(){
        var value = $(this).val();
        if (value == "NOW") {
            temporalService.setNowApproach();
        } else if (value == "FIXED") {
            temporalService.setFixedApproach();
        } else
            alert("Unknown temporal approach (the way we handle current time on the page)! The page won't work properly!");
    });

    $("#temporal-approach").trigger("change");
});

//todo: save current date and approach to local storage, load it from there on startup, to make it stay between pages
//fixme: in "now" approach the datetime picker shouldnt be openable