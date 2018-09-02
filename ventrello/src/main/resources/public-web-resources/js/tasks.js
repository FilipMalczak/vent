var tasksController = (function(){
    var tasks = {};

    function findTaskRow(id){
        return $("div.vent-task-row").filter(function(){ return $(this).data("vent-id") == id; });
    }

    function clearTasks(){
        tasks = {};
        clearTasksView();
    }

    function clearTasksView(){
        $("#tasks-container").empty();
    }

    function renderTask(task){
        return $("<div>").addClass("row").addClass("vent-task-row").data("vent-id", task.ventId.id).append(
            $("<div>").addClass("col-sm").append(
                $("<div>").addClass("card").append(
                    $("<div>").addClass("card-header").append(
                        $("<span>").
                            addClass("badge").addClass(task.view.resolved ? "badge-secondary" : "badge-primary").
                            text("#"+task.view.number),
                        $("<span>").
                            addClass("badge").addClass("badge-pill").
                            addClass(task.view.resolved ? "badge-secondary" : "badge-primary").
                            text(task.view.resolved ? "Resolved" : "Unresolved"),
                        taskButtons(task)
                    ),
                    $("<div>").addClass("card-body").append(
                        $("<h5>").attr("id", "task-name-"+task.ventId.id).
                            attr("href", "#").
                            attr("aria-expanded", "false").
                            attr("aria-controls", "#task-name-"+task.ventId.id).
                            attr("data-toggle", "collapse").
                            attr("data-target", "#task-description-"+task.ventId.id).
                            text(task.view.name),
                        $("<p>").attr("id", "task-description-"+task.ventId.id).
                            addClass("collapse").
                            text(task.view.description)
                    )
                )
            )
        );
//        return $("<div>").addClass("row").addClass("vent-task-row").data("vent-id", task.ventId.id).append(
//            $("<div>").addClass("col-sm").
//                append(
//                    $("<button>").addClass("btn").addClass(task.view.resolved ? "btn-secondary" : "btn-primary").
//                        append(
//                            $("<span>").
//                                addClass("badge").addClass("badge-light").
//                                text("#"+task.view.number),
//                            $("<span>").text(task.view.name)
//                        )
//                ),
//            $("<div>").addClass("col-sm").
//                append($("<span>").addClass("badge").addClass("badge-sm").addClass("badge-light").text("This will show boards that the task is on.")),
//            $("<div>").addClass("col-sm").
//                append(
//                    taskButtons(task)
//                )
//        );
    }

    function taskButtons(task){
        return $("<div>").
            addClass("btn-group").
            addClass("pull-right").
            prop("role", "group").
            prop("aria-label", "Task actions").
            append(
                editTaskButton(task),
                deleteTaskButton(task)
            );
    }

    function editTaskButton(task){
        return $("<button>").
            addClass("btn").
            addClass("btn-sm").
            addClass("btn-success").
            addClass("vent-task-edit").
            prop("type", "button").
            attr("data-toggle", "modal").
            attr("data-target", "#taskModal").
            attr("data-ok-text", "Save task").
            attr("data-task-id", task.ventId.id).
            attr("data-ok-action", "edit").
            append(
                $("<i>").addClass("fas").addClass("fa-pen-square")
            );
    }

    function deleteTaskButton(task){
        return $("<button>").
            prop("type", "button").
            addClass("btn").
            addClass("btn-sm").
            addClass("btn-danger").
            append(
                $("<i>").addClass("fas").addClass("fa-trash-alt")
            ).click(function(){
                var confirmation = confirm("Are you sure you want to delete task #"+task.view.number+' "'+task.view.name+'"?');
                if (confirmation)
                    deleteTask(task.ventId.id, function(){
                        findTaskRow(task.ventId.id).remove();
                        delete tasks[task.ventId.id];
                    });
            });
    }

    function renderTasks(){
        var toRender = Object.values(tasks);
        toRender.sort(function(x, y){return x.view.number - y.view.number;});
        toRender.forEach(function(t){
            if (shouldRender(t))
                $("#tasks-container").append(renderTask(t));
        });
    }

    function fetchAllTasks(callback){
        $.get("/api/v1/db/collection/tasks/object", callback);
    }

    function findTasks(path, val, callback){
        $.post({
            url: "/api/v1/db/collection/tasks/query",
            data: JSON.stringify({
                operation: "FIND",
                rootNode: {
                    nodeType: "EQUALS",
                    children: [],
                    payload: [path, val]
                }
            }),
            success: callback,
            dataType: "json",
            contentType: 'application/json'
        });
    }

    function fetchFilteredTasks(resolved, callback){
        findTasks("resolved", resolved, callback);
    }

    function fetchTasks(callback){
        var filter = $("#main-task-filter").val();
        var actions = {
            "ALL": fetchAllTasks,
            "RESOLVED": function(cb) { fetchFilteredTasks(true, cb); },
            "UNRESOLVED": function(cb) { fetchFilteredTasks(false, cb); }
        }
        actions[filter](callback);
    }

    function fillForm(task){
        $("#taskName").val(task.name);
        $("#taskDescription").val(task.description);
        $("#taskResolved").prop('checked', task.resolved);
    }

    function gatherTaskForm(){
        return {
            name: $("#taskName").val(),
            description: $("#taskDescription").val(),
            resolved: $("#taskResolved").prop('checked')
        };
    }

    function fetchAndRenderTasks(){
        fetchTasks(function(d){
            clearTasks();
            d.forEach(function(o){
                tasks[o.ventId.id] = o;
            });
            renderTasks();
        });
    }

    function shouldRender(task){
        return $("#main-task-filter").val() == "ALL" ||
            ($("#main-task-filter").val() == "UNRESOLVED" && !(task.view.resolved)) ||
            ($("#main-task-filter").val() == "RESOLVED" && (task.view.resolved));
    }

    function createTask(callback){
        $.post("/counters/tasks/increment", function(i){
            var body = gatherTaskForm();
            body.number = i;
            $.post({
                url: "/api/v1/db/collection/tasks/object",
                data: JSON.stringify({initialState: body}),
                dataType: "json",
                contentType: 'application/json',
                success: function(id){
                    var task = {
                        ventId: {id: id.id},
                        view: body
                    };
                    console.log("Task: "+JSON.stringify(task))
                    tasks[id] = task;
                    if (shouldRender(task))
                        $("#tasks-container").append(renderTask(task));
                    callback();
                }
            });
        });
    }

    function clearModal() {
        fillForm({name: "", description: "", resolved: false});
    }

    function ventPutValue(id, path, val, callback){
        $.ajax({
            type: "PUT",
            url: "/api/v1/db/collection/tasks/object/"+id+"/state?path="+path,
            data: JSON.stringify({ newState: val }),
            dataType: "json",
            contentType: 'application/json',
            success: callback
        });
    }



    function updateTask(id, callback){
        var newState = gatherTaskForm();
        var oldState = tasks[id].view;
        var wasDescUncollapsed = $("#task-description-"+id).hasClass("show");

        function compareFieldFoo(field, cb){
            return function() {
                if (newState[field] != oldState[field]){
                    ventPutValue(id, field, newState[field], cb);
                } else
                    cb();
            };
        }

        compareFieldFoo("name",
            compareFieldFoo("description",
                compareFieldFoo("resolved", function(){
                    tasks[id].view = Object.assign(oldState, newState);
                    findTaskRow(id).replaceWith(renderTask(tasks[id]));
                    if (wasDescUncollapsed)
                        $("#task-description-"+id).addClass("show")
                    callback();
                })
            )
        )();
    }

    function fetchTaskWithNumber(number, callbackForId){
        if (number) {
            findTasks("number", number, function(d){
                if (d.length) {
                    if (d.length > 1){
                        alert("Found "+d.length+" matching tasks! DB state is inconsistent!");
                    } else {
                        $(d).each(function(){
                            if (!(this.ventId.id in tasks)){
                                tasks[this.ventId.id] = this;
                                clearTasksView();
                                renderTasks();
                            }
                            callbackForId(this.ventId.id);
                        });
                    }
                } else
                    alert("Couldn't find task #"+number+"!")
            });
        } else {
            alert("Enter task number!");
        }
    }

    function deleteTask(id, callback){
        $.ajax({
            type: "DELETE",
            url: "/api/v1/db/collection/tasks/object/"+id,
            dataType: "json",
            contentType: 'application/json',
            success: callback
        });
    }

    return {
        fetchAndRenderTasks: fetchAndRenderTasks,
        loadTaskToModal: function(id){
            $("#vent-edited-task-number").text("#"+tasks[id].view.number);
            fillForm(tasks[id].view);
        },
        clearModal: clearModal,
        actions: {
            create: function(){
                createTask(clearModal);
            },
            edit: function(id){
                updateTask(id, clearModal);
            }
        },
        fetchTaskWithNumber: fetchTaskWithNumber
    };
})();