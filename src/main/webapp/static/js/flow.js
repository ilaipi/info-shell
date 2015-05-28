//定义所有的类型
var X_TYPE_SHELL = "x-type-shell",
    X_TYPE_GLOBAL_FIELDS = "x-type-global-field",
    X_TYPE_FIELD = "x-type-list-field",
    X_TYPE_PAGE = 'x-type-page',
    X_TYPE_LOCATE = 'x-type-locate',
    X_TYPE_PAGE_LOCATE = 'x-type-page-locate',
    X_TYPE_FIELD_VALUE = 'x-type-fieldvalue',
    X_TYPE_FIELD_HANDLER = 'x-type-fieldhandler',
    X_TYPE_RESULT_HANDLER = 'x-type-resulthandler';
    
var FIRST_LEVEL_LIST = "first-level-list";
    
var draggingObj, draggingName, draggingType, javaType, draggingPropertiesBox;

var attrsBox;//第三栏属性盒子的$对象

var FLOW_UUID,OPENER_OPERATOR;//保存成功后，把UUID保存到这个变量

var GLOBAL_ID = 0;

//**********************************************************************
//* id的生成方式改为 ul[title] + GLOBAL_ID（如果是编辑，此id会从页面中读出）
//**********************************************************************

/**
 * 所有拖动生成的元素，全部都可以通过拖动进行删除
 * @type type
 */
var deleteAbleDraggable = {
    helper : "clone",
    cursor : "pointer",
    start : function(){
        getXTypeAndName(this);
    }
};

/**
 * 列表页面 > 全局字段 接受drop
 * @type type
 */
var globalFieldsDropable = {
    accept: "li.moveable:contains('" + X_TYPE_GLOBAL_FIELDS + "')",
    drop: function(event, ui) {
        var globalFieldList = getSubList(this, "global-page-sub-list");
        var globalField = commonDropStart();
        $(globalField).droppable({
            accept:"li.moveable:contains('" + X_TYPE_LOCATE + "'), li.moveable:contains('" + X_TYPE_FIELD_HANDLER + "'), li.moveable:contains('" + X_TYPE_FIELD_VALUE + "')",
            drop:function(){
                var fieldSubList = getSubList($(globalField), "global-page-field-sub-list");
                var fieldSub = commonDropStart();
                fieldSubList.append(fieldSub);
                appendAndShowPropertiesBox(fieldSub);
            }
        });
        globalFieldList.append(globalField);
        appendAndShowPropertiesBox(globalField);
    }
};

/**
 * 列表页面 > 列表字段 接受drop
 * @type type
 */
var listFieldsDropable = {
    accept: "li.moveable:contains('" + X_TYPE_FIELD + "')",
    drop: function(event, ui) {
        var listFieldList = getSubList(this, "list-page-sub-list");
        var listField = commonDropStart();
        $(listField).droppable({
            accept:"li.moveable:contains('" + X_TYPE_LOCATE + "'), li.moveable:contains('" + X_TYPE_FIELD_HANDLER + "'), li.moveable:contains('" + X_TYPE_FIELD_VALUE + "')",
            drop:function(){
                var fieldSubList = getSubList($(listField), "list-page-field-sub-list");
                var fieldSub = commonDropStart();
                fieldSubList.append(fieldSub);
                appendAndShowPropertiesBox(fieldSub);
            }
        });
        listFieldList.append(listField);
        appendAndShowPropertiesBox(listField);
    }
};

/**
 * 流程区域 可以 接受 shell、page的drop
 * @type type
 */
var flowSubDropable = {
    accept: "li.moveable:contains('" + X_TYPE_SHELL + "'),li.moveable:contains('" + X_TYPE_PAGE + "')",
    drop: function(event, ui) {
        shellDrop(event, ui);
    }
};

/**
 * 壳 可以 接受 结果处理器的drop
 * @type type
 */
var shellDropable = {
    accept:"li.moveable:contains('" + X_TYPE_RESULT_HANDLER + "')",
    over:function(){
        //console.log(X_TYPE_RESULT_HANDLER + " welcome!");
    },
    drop:function(){
        var shellList = getSubList(this, "flow-shell-sub-list");
        var resultHandler = commonDropStart();
        shellList.append(resultHandler);
        appendAndShowPropertiesBox(resultHandler);
    }
};

/**
 * 页面 > 字段 可以接受 定位、字段处理器、字段值的drop
 * @type type
 */
var fieldDropable = {
    accept:"li.moveable:contains('" + X_TYPE_LOCATE + "'), li.moveable:contains('" + X_TYPE_FIELD_HANDLER + "'), li.moveable:contains('" + X_TYPE_FIELD_VALUE + "')",
    drop:function(){
        var fieldSubList = getSubList($(this), "page-field-sub-list");
        var fieldSub = commonDropStart();
        fieldSubList.append(fieldSub);
        appendAndShowPropertiesBox(fieldSub);
    }
};

/**
 * 页面 可以 接受 页面定位、字段的drop
 * @type type
 */
var pageDropable = {
    accept:"li.moveable:contains('" + X_TYPE_PAGE_LOCATE + "'), li.moveable:contains('" + X_TYPE_FIELD + "')",
    over:function(){
        //console.log(X_TYPE_RESULT_HANDLER + " welcome!");
    },
    drop:function(){
        var subList = getSubList(this, "page-sub-list");
        if(X_TYPE_PAGE_LOCATE === draggingType){
            //判断页面下有没有locate类型的
            var locate = subList.find("li.deleteable:contains('" + X_TYPE_PAGE_LOCATE + "')");
            if(locate.length > 0){
                alert("只能有一个定位！");
                return;
            }
        }
        var sub = commonDropStart();
        if(X_TYPE_PAGE_LOCATE === draggingType){
            subList.prepend(sub);
        } else if(X_TYPE_FIELD === draggingType){
            $(sub).droppable(fieldDropable);
            subList.append(sub);
        }
        appendAndShowPropertiesBox(sub);
    }
};

function getParams(orignalParams){
    orignalParams = orignalParams.substr(orignalParams.indexOf('?') + 1);
    console.log("orignalParams:", orignalParams);
    var params = orignalParams.split("&");
    var result = {};
    for(var i = 0;i<params.length;i++){
        console.log('param ' + i + ":", params[i]);
        var param = params[i].split("=");
        var key = param[0];
        var value = "";
        if(param.length > 1){
            value = param[1];
        }
        result[key] = value;
    }
    return result;
}


$(function() {
    var params = window.location.search;
    params = getParams(params);
    if(params._uuid){
        FLOW_UUID = params._uuid;
        OPENER_OPERATOR = params.action;
        //请求flowDesc。动态显示流程配置和属性配置
        $.get(
            "flow.do",
            {
                action : "getFlowDesc",
                UUID : FLOW_UUID
            },
            function(html){
                
                var flowConfig = $($(html)[0]);
                var toolsConfig = $(flowConfig.find("#tools")[0]);
                var propertiesConfig = $(flowConfig.find("#properties")[0]);
                $("#flow").html(toolsConfig.html());
                $("#attrs_box").html(propertiesConfig.html());
                //TODO 请求流程的一些特殊信息。包括：请求头、请求体
                $.getJSON(
                    "flow.do",
                    {action : "getFlowInfo", UUID : FLOW_UUID},
                    function(flowInfo){
                        var requestHeaders = flowInfo.requestHeaders;
                        var requestBodys = flowInfo.requestBodys;
                        $($("#attrs_box").find("textarea[name='requestHeaders']")[0]).val(requestHeaders);
                        $($("#attrs_box").find("textarea[name='requestBodys']")[0]).val(requestBodys);
                    }
                );
                GLOBAL_ID = $(flowConfig.find("#global_id_span")[0]).text();
                //注册事件
                //1. li.deleteable 注册可拖动
                $("li.deleteable").draggable(deleteAbleDraggable);
                
                //2. 列表页面 点击显示属性
                $("#listPage span:first-child").click(function(){
                    showPropertiesBox("list-page-properties-box"); 
                });
                
                //3. #global-fields 接受drop
                $("#global-fields").droppable(globalFieldsDropable);
                
                //4. #list-fields 接受drop
                $("#list-fields").droppable(listFieldsDropable);
                
                //5. 壳 设置可接受 结果处理器的drop
                var shellObj = $("#" + FIRST_LEVEL_LIST + ">li>span:contains('" + X_TYPE_SHELL + "')");
                if(shellObj !== null && shellObj.length === 1){
                    shellObj = $(shellObj[0]).parent();//得到li的数组
                    shellObj = $(shellObj[0]);
                    shellObj.droppable(shellDropable);
                }
                
                //6. flow 设置可接受 页面、壳的drop
                $("div#flow").droppable(flowSubDropable);
                
                //7. 页面设置可以接受 页面定位、字段的drop
                $($("div#flow span:contains('x-type-page')").parent()).droppable(pageDropable);
                
                //8. 字段 设置可接受 字段定位、字段值、字段处理器的drop
                $($("div#flow span:contains('x-type-list-field')").parent()).droppable(fieldDropable);
                
                
                //9. span.tool_name 点击 可显示对应的属性
                $("span.tool_name").click(function(){
                    toolNameClickFunc(this);
                });
                //10. input[name='display'] focusout 修改对应的工具的tool_name
                $("input[name='displayName']").focusout(function(){
                    var displayName = $(this).val();
                    if(displayName !== ''){
                        var boxId = $($($(this).parent()[0]).parent()[0]).attr("id");
                        //找到id，反推到li
                        $($($("span:contains(" + boxId + ")").parent()[0]).find("span.tool_name")[0]).html(displayName);
                    }
                });
            }, "text"
        );
        $.getJSON(
            "flow.do",
            {
                action : "flowTaskList",
                gatherFlowUUID : FLOW_UUID
            },
            function(data){
                if(data && data.length > 0){
                    for(var i = 0;i<data.length;i++){
                        var flowTaskTr = "<tr id='flow_task_"+data[i].id+"'><td title='taskName'>" + data[i].taskName + "</td><td>" + data[i].cron + "</td><td><a href='javascript:void(0);' onclick='deleteTask(this)'>删除</a></td></tr>";
                        $($("#flowTaskList").find("tbody")[0]).append(flowTaskTr);
                        $("#flowTaskList").show();
                    }
                }
            }
        );
    }
    attrsBox = $("#attrs_box");
    //注册.moveable的拖动事件
    $("div#tools_box li.moveable").draggable({
        helper: "clone",
        cursor : "pointer",
        start: function(event, ui) {
            getXTypeAndName(this);
        }
    });
    $("div#tools_box").droppable({
        accept: "li.deleteable",
        over:function(){
            //alert("确定要删除吗？");
        },
        drop:function(){
            var toDeleteId = getId($(draggingObj)[0]);
            $(draggingObj)[0].remove();
            //删除对应的属性
            $("#" + toDeleteId).remove();
        }
    });
    //给列表页面增加点击事件，显示属性设置
    $("#listPage span:first-child").click(function(){
        showPropertiesBox("list-page-properties-box"); 
    });
    
    //流程区域接收shell、page类型的拖动drop
    $("div#flow").droppable(flowSubDropable);
    
    //全局字段接收“全局字段”工具的drop
    $("#global-fields").droppable(globalFieldsDropable);
    //列表字段接收“字段”工具的drop
    $("#list-fields").droppable(listFieldsDropable);
});

function cloneFlow(){
    if(!FLOW_UUID){
        alert("必须先保存流程！");
    }
    $.get(
        "flow.do",
        {
            action : "clone",
            UUID : FLOW_UUID
        },
        function(data){
            if(data.FLOW_UUID){
                window.open("flow.html?_uuid=" + data.FLOW_UUID, "_blank");
            }
        }
    );
    
}

function testFlow(){
    if(!FLOW_UUID){
        alert("必须先保存流程！");
    }
    console.log("调试" + FLOW_UUID + "开始！");
    $.get(
        "flow.do",
        {
            action : "test",
            UUID : FLOW_UUID
        },
        function (){
            console.log("调试" + FLOW_UUID + "结束！");
        }
    );
}

function saveFlow(){
    var flow = $("#" + FIRST_LEVEL_LIST)[0];
    
    var properties = $("#attrs_box")[0];
    //设置每个input的value属性，便于后端取值
    var inputs = $(properties).find("input[name]");
    for(var i = 0;i<inputs.length;i++){
        var input = $(inputs[i]);
        input.attr("value", input.val());
    }
    
    var flowConfig = "<div>";
    
    //join tools
    flowConfig += "<div id='tools'>";
    flowConfig += flow.outerHTML;
    flowConfig += "</div>";
    
    //join tool properties
    flowConfig += "<div id='properties'>";
    flowConfig += $(properties).html();
    flowConfig += "</div>";
    
    flowConfig += "</div>";
    
    var postCfg = {
        action : "save",
        flowConfig : flowConfig
    };
    
    var requestInfo = $(properties).find("textarea[name]");
    console.log("request info:", requestInfo);
    for(var i = 0;i<requestInfo.length;i++){
        postCfg[$(requestInfo[i]).attr("name")] = $(requestInfo[i]).val();
    }
    if(FLOW_UUID){
        postCfg["UUID"] = FLOW_UUID;
    }
    
    $.post(
        "flow.do",//url
        postCfg,//data
        function(data){
            //alert("save ok!");
            alert(data.msg);
            if(data.FLOW_UUID){
                FLOW_UUID = data.FLOW_UUID;
            }
        },
        "json"//返回数据格式
    );
}

function appendAndShowPropertiesBox(obj){
    if(!draggingPropertiesBox){
        return;
    }
    var id = generateId($(obj));
    //把id写入obj
    var toolId = "<span class='tool_id'>" + id + "</span>";
    $(obj).append(toolId);
    var clonePropertiesBox = $(draggingPropertiesBox.outerHTML)[0];
    $(clonePropertiesBox).attr("id", id);
    var displayNameObj = $(clonePropertiesBox).find("input[name='displayName']");
    if(displayNameObj !== null && displayNameObj.length === 1){
        $(displayNameObj[0]).focusout(function(){
            var displayName = $(this).val();
            if(displayName){
                $($(obj).find("span.tool_name")[0]).html(displayName);
            }
        });
    }
    attrsBox.append(clonePropertiesBox);
    showPropertiesBox(id);
}

function showPropertiesBox(id){
    hideOtherPropertiesBox();
    $("#" + id).removeClass();
    $("#" + id).addClass("properties_box_show");
}

function hideOtherPropertiesBox(){
    var showBoxes = attrsBox.find("div.properties_box_show");
    $(showBoxes[0]).removeClass();
    $(showBoxes[0]).addClass("properties_box_hide");
}

function generateId(obj){
    GLOBAL_ID = parseInt(GLOBAL_ID) + 1;
    console.log("GLOBAL_ID:" + GLOBAL_ID);
    //每次刷新一下页面上全局id的值
    $("#global_id_span").html(GLOBAL_ID);
    return $($(obj).parent()[0]).attr("title") + "_" + GLOBAL_ID;
}

function getId(obj){
    return $($(obj).find("span.tool_id")[0]).text();
}

/**
 * 壳的拖动在drop时的方法实现。
 * 如果是壳，添加到列表的开始，并且设置可以拖动删除、可以接收resulthandler类型的拖动drop。
 * 如果是页面，添加到列表的结尾，并且设置可以拖动删除、可以接收locate、field类型的拖动drop。
 * @param {type} event
 * @param {type} ui
 * @returns {unresolved}
 */
function shellDrop(event, ui){
    var firstLevelList = $("#" + FIRST_LEVEL_LIST);
    //检查是否已经有了壳
    if(X_TYPE_SHELL === draggingType){
        var shell = firstLevelList.find("li.deleteable:contains('" + X_TYPE_SHELL + "')");
        if(shell.length > 0){
            alert("只能有一个壳！");
            return;
        }
    }
    var tempObj = commonDropStart();
    if(X_TYPE_SHELL === draggingType){
        $(tempObj).droppable(shellDropable);
        firstLevelList.prepend(tempObj);//把壳加到flow的显示区域第一层列表的开始
    } else if(X_TYPE_PAGE === draggingType){
        $(tempObj).droppable(pageDropable);
        firstLevelList.append(tempObj);//把页面加到flow的显示区域第一层列表的结束
    }
    //控制drop的工具的属性显示
    appendAndShowPropertiesBox(tempObj);
}

/**
 * 尝试找到obj的第一层的ul。如果找到就返回。如果找不到，就创建一个。
 * @param {type} obj
 * @param {type} title
 * @returns {undefined}  obj的第一层ul
 */
function getSubList(obj, title){
    //判断是否已经有子列表
    var subList = $(obj).find("ul[title='" + title + "']");
    var resultHandlers;
    if(subList.length > 0){
        //已经有ul，直接得到ul，往ul中添加即可
        resultHandlers = $(subList[0]);
    } else{
        //创建ul
        var resultHandlerStr = "<ul style='list-style:none;' title='" + title + "'></ul>";
        resultHandlers = $(resultHandlerStr);
        resultHandlers.appendTo($(obj));
    }
    return resultHandlers;
}

/**
 * 每个drop方法实现时都可以调用这个方法得到一个可操作对象，用于添加到目标
 * @returns {unresolved}
 */
function commonDropStart(){
    var tempObj = $($(draggingObj)[0].outerHTML)[0];
    $(tempObj).attr("class", "deleteable");
    $(tempObj).draggable(deleteAbleDraggable);
    //给span.tool_name添加点击事件
    var toolNameSpan = $(tempObj).find("span.tool_name");
    toolNameSpan = $(toolNameSpan[0]);
    toolNameSpan.click(function(){
        toolNameClickFunc(this);
    });
    //删除属性盒子
    $($(tempObj).find("div")[0]).remove(".properties_box_hide");
    return tempObj;
}

/**
 * 点击 span.tool_name 时的事件
 * @param {type} obj
 * @returns {undefined}
 */
function toolNameClickFunc(obj){
    //显示对应的属性盒子
    var id = getId($(obj).parent()[0]);
    showPropertiesBox(id); 
}

/**
 * 因为accept属性不能调用方法，所以这个方法暂时没有用
 * @param {type} acceptTypes
 * @returns {unresolved}
 */
function gernateAccept(acceptTypes){
    if(acceptTypes === null || acceptTypes.length < 1){
        return;
    }
    var acceptDrop = "li.moveable:contains('" + acceptTypes[0] + "')";
    for(var i = 1;i<acceptTypes.length;i++){
        acceptDrop += (", li.moveable:contains('" + acceptTypes[i] + "')");
    }
}

/**
 * 每次拖动开始时，调用此方法，把拖动的对象信息设置到全局变量中
 * @param {type} obj
 * @returns {undefined}
 */
function getXTypeAndName(obj){
    draggingObj = $(obj)[0];
    draggingName = $($(draggingObj).find(".tool_name")[0]).text();
    draggingType = $($(draggingObj).find(".x-type")[0]).text();
    javaType = $($(draggingObj).find(".java-type")[0]).text();
    draggingPropertiesBox = $(draggingObj).find("div.properties_box_hide")[0];
    console.log("draggingName:", draggingName);
    console.log("draggingType:", draggingType);
    console.log("draggingType:", javaType);
    console.log("draggingPropertiesBox:", draggingPropertiesBox);
}