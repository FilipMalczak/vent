//todo: add another approach/mode: choosing range and clicking play that replays all actions recorded in vent during that timespan

var temporalService = (function(){
    var nowUpdateIntervalHandle = null;
    var openable = true;
    var flatpickrConfig = {
        enableTime: true,
        dateFormat: "Y-m-dTH:i:S.000",
        altFormat: "Y.m.d H:i:S",
        altInput: true
    };

    function currentConfig(obj){
        obj = obj || {};
        return Object.assign(flatpickrConfig, {clickOpens: openable}, obj);
    }

    function initChooser(){
        $("#current-date").flatpickr(flatpickrConfig);
    }

    function startUpdates(){
        if (!nowUpdateIntervalHandle){
            nowUpdateIntervalHandle = setInterval(function(){
                $.get("/api/v1/db/temporal/now", function(date){
                    $("#current-date").flatpickr(currentConfig({
                        defaultDate: date
                    }));
                    console.log(temporalService.currentTimeForVent());
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

    return {
        initChooser: initChooser,
        setNowApproach: function() {
            openable = false;
            startUpdates();
            //todo enable write stack ops
        },
        setFixedApproach: function(){
            openable = true;
            $("#current-date").flatpickr(currentConfig());
            stopUpdates();
//            $("#current-date").change(function() { //todo actual change handler
//                console.log($(this).val());
//            });
            //todo disable write stack ops
        },
        currentTimeForVent: function(){
            return $("#current-date").val()
        }
    };
})();

$(function(){
    $.get("/api/v1/db/temporal/timezone", function(d) {
        $("#timezone").text(d);
    });

    temporalService.initChooser();

    $("#temporal-approach").change(function(){
        var value = $(this).val();
        $("#current-date").unbind('change');
        if (value == "NOW") {
            temporalService.setNowApproach();
        } else if (value == "FIXED") {
            temporalService.setFixedApproach();
        } else
            alert("Unknown temporal approach (the way we handle current time on the page)! The page won't work properly!");
    });

    $("#temporal-approach").trigger("change");
});