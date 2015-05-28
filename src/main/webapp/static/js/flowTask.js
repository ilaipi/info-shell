$(function(){
   $("#add_gather_flow_task").click(function(){
       if(!FLOW_UUID){
           alert("必须先保存流程！");
       }
       var inputs = $($(this).parent()[0]).find("input[name]");
       var data = {action : "configTask"};
       data['taskName'] = $($($(this).parent()[0]).find("input[name='taskName']")[0]).val();
       //判断是否已经有此名字的任务
       var taskNames = $($("#flowTaskList").find("tbody>tr>td[title='taskName']"));
       for(var i = 0;i<taskNames.length;i++){
           if($(taskNames[i]).text() === data['taskName']){
               alert("任务名称已存在！");
               return;
           }
       }
       data['cron'] = $($($(this).parent()[0]).find("input[name='cron']")[0]).val();
       for(var i = 0;i<inputs.length;i++){
           var taskInfo = $(inputs[i]);
           data[taskInfo.attr("name")] = taskInfo.val();
       }
       data["_UUID"] = FLOW_UUID;
       $.getJSON(
           "flow.do",
           data,
           function(dataSuccess){
               if(!dataSuccess.success){
                   alert(dataSuccess.msg);
                   return;
               }
               var flowTaskTr = "<tr id='flow_task_"+dataSuccess.value+"'><td title='taskName'>" + data["taskName"] + "</td><td>" + data["cron"] + "</td><td><a href='javascript:void(0);' onclick='deleteTask(this)'>删除</a></td></tr>";
               $($("#flowTaskList").find("tbody")[0]).append(flowTaskTr);
               $("#flowTaskList").show();
           }
       );
   });
});

function deleteTask(obj){
    var toDeleteTaskId = $($($(obj).parent()[0]).parent()[0]).attr("id");
    toDeleteTaskId = toDeleteTaskId.substr(toDeleteTaskId.lastIndexOf("_") + 1);
    $.getJSON("flow.do",{action : 'deleteFlowTask', taskId : toDeleteTaskId},function(data){
        if(data.success){
            $($($(obj).parent()[0]).parent()[0]).remove();
            if($($("#flowTaskList").find("tbody>tr")).length === 0){
                $("#flowTaskList").hide();
            }
        }
    });
}