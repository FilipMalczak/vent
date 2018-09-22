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
    var currentDateConfig = {
        enableTime: true,
        enableSeconds: true,
        dateFormat: "Y-m-dTH:i:S.000",
        altFormat: "Y.m.d H:i:S",
        altInput: true,
        onChange: function() { changeDateCallback(currentTimeForVent()); }
    };

    var dateRangeConfig = Object.assign(currentDateConfig, {mode: "range"})

    var changeDateCallback = function(){}

    var chooser = null;
    var rangeChooser = null;

    function currentConfig(obj){
        obj = obj || {};
        return Object.assign(currentDateConfig, {clickOpens: openable}, obj);
    }

    function initChoosers(){
        chooser = $("#current-date").flatpickr(currentDateConfig);
        rangeChooser = $("#date-range").flatpickr(dateRangeConfig);
    }

    function startNowUpdates(){
        if (!nowUpdateIntervalHandle){
            nowUpdateIntervalHandle = setInterval(function(){
                $.get("/api/v1/db/temporal/now", function(date){
                    chooser.setDate(date);
                });
            }, 1000);
        }
    }

    function stopNowUpdates(){
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
        initChoosers: initChoosers,
        setNowApproach: function() {
            openable = false;
            startNowUpdates();
            changeDateCallback = function() {};
            currentDateRepr = nowInstantRepresentation;

            triggerRefresh();
            $("#date-range-row").hide();
            $(".only-when-time-is-now").prop("disabled", false);
        },
        setFixedApproach: function(){
            openable = true;
            chooser.set("openable", false);
            stopNowUpdates();
            changeDateCallback = function(dateStr) {
                currentDateRepr = fixedInstantRepresentation(dateStr);
                triggerRefresh();
            };
            $("#date-range-row").hide();
            $(".only-when-time-is-now").prop("disabled", true);
        },
        setRangeApproach: function(){
            openable  = false;
            chooser.set("openable", false);
            stopNowUpdates();
            changeDateCallback = function() {};
            currentDateRepr = fixedInstantRepresentation(currentDateRepr.currentDate());
            rangeChooser.setDate([currentDateRepr.currentDate(), currentDateRepr.currentDate()]);
            $("#date-range-row").show();
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

    temporalService.initChoosers();

    $("#current-date").val()

    $("#temporal-approach").change(function(){
        var value = $(this).val();
        if (value == "NOW") {
            temporalService.setNowApproach();
        } else if (value == "FIXED") {
            temporalService.setFixedApproach();
        } else if (value == "RANGE") {
            temporalService.setRangeApproach();
         } else
            alert("Unknown temporal approach (the way we handle current time on the page)! The page won't work properly!");
    });

    $("#temporal-approach").trigger("change");

    $("#date-range-row").hide();
    $("#pause-icon").toggle();
});

//todo: save current date and approach to local storage, load it from there on startup, to make it stay between pages
//fixme: in "now" approach the datetime picker shouldnt be openable