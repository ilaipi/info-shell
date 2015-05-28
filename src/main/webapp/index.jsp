<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="shortcut icon" href="./static/favicon.ico">
    <link href="./static/css/bootstrap.min.css" rel="stylesheet">
    <link href="./static/css/font-awesome.min.css" rel="stylesheet">
    <script type="text/javascript" src="./static/js/jquery.js"></script>
    <style>
        td>a:link {
            color: #000;
        }
        td>a:visited{
            color: #000;
        }
        td>a:hover{
            color: #449d44;
        }
        td>a:active{
            color: #000;
        }
    </style>
    <script type="text/javascript">
        $(function(){
            gatherFlowList();//页面加载完成后直接加载列表
            $("#select_gather_flow_list").click(function(){
                gatherFlowList();
            });
            $("#keyword_input").bind('keyup',function(event){
                if(event.keyCode === 13){
                    gatherFlowList();
                }
            });
            $("#add_gather_flow").click(function(){
                window.open("flow.html", "_blank");
            });
        });
        function deleteFlow(_uuid){
            console.log("uuid:", _uuid);
            console.log("this", this);
            $.get(
                "flow.do",
                {
                    action:"delete",
                    UUID:_uuid
                },
                function(data){
                    if(data.success){
                        //删除节点
                        $("#" + _uuid).remove();
                        //刷新编号
                        var listShow = $("#gather_flow_list");
                        var listBody = $(listShow.find("tbody")[0]);
                        var rows = $(listBody).find("tr");
                        console.log("rows length:", rows);
                        if(rows.length === 0){
                            var listHead = $(listShow.find("thead")[0]);
                            var gatherFlow = "<tr><td colspan='" + listHead.find("th").length + "'>没有相关采集！</td></tr>";
                            listBody.html("");
                            listBody.append(gatherFlow);
                        }
                        for(var i = 0;i<rows.length;i++){
                            $($(rows[i]).find("td")[0]).html(i+1);
                        }
                    }
                }
            );
        }
        function gatherFlowList(){
            var keyword = $("#keyword_input").val();
            $.getJSON(
                "flow.do",//URL
                {action : "getFlowList", keyword : keyword},//data
                function(data){//载入成功时回调函数
                    var listShow = $("#gather_flow_list");
                    var listBody = $(listShow.find("tbody")[0]);
                    if(data.count === 0){
                        var listHead = $(listShow.find("thead")[0]);
                        var gatherFlow = "<tr><td colspan='" + listHead.find("th").length + "'>没有相关采集！</td></tr>";
                        listBody.html("");
                        listBody.append(gatherFlow);
                        return;
                    }
                    listBody.html('');
                    var gatherFlowList = data.data;
                    for(var i = 0;i<gatherFlowList.length;i++){
                        var item = gatherFlowList[i];
                        var listPageUrl = item.listPageUrl;
                        if(listPageUrl && listPageUrl.length > 50){
                            listPageUrl = listPageUrl.substring(0, 46) + "...";
                        }
                        var gatherFlow = "<tr id='" + item.uuid + "'><td>" + (i + 1) + "</td>";
                        gatherFlow += "<td>" + item.netName + "</td>";
                        gatherFlow += "<td><a href='flow.html?_uuid=" + item.uuid + "' target='_blank'>" + item.gatherPrograma + "</a></td>";
                        gatherFlow += "<td title='" + item.listPageUrl + "'>" + listPageUrl + "</td>";
                        gatherFlow += "<td><a href='javascript:void(0);' onclick='javascript:deleteFlow(\"" + item.uuid + "\");return false;'>删除</a></td>";
                        //gatherFlow += "<td style='display:none;'>" + item.uuid + "</td>";
                        gatherFlow += "</tr>";
                        listBody.append(gatherFlow);
                    }
                }
            );
        }
    </script>
</head>
<body>
    <div class="container">
        <div class="row">
            <div class="col-lg-8"><h1>INFO-SHELL</h1></div>
            <div class="col-lg-3">
                <div class="input-group" style="margin: 20px 0 10px 0;">
                    <input type="text" class="form-control" id="keyword_input" name="keyword">
                    <span class="input-group-addon" id="select_gather_flow_list"><i class="icon-search"></i></span>
                </div>
            </div>
            <div class="col-lg-1" style="margin: 20px 0 10px 0;">
                <div><button type="button" class="btn btn-success" id="add_gather_flow">新增</button></div>
            </div>
        </div>
        <!-- 
        <form class="form-inline" action="/flow.do">
            <div class="col-lg-3">
                <input type="text" class="form-control" placeholder="网站或栏目" name="keyword">
            </div>
            <input type="hidden" name="action" value="getFlowList">
            <button type="submit" class="btn btn-default">搜索</button>
        </form>
        -->
        <table class="table table-hover" id="gather_flow_list">
            <thead>
                <th>编号</th>
                <th>网站</th>
                <th>栏目</th>
                <th>URL</th>
                <th>操作</th>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</body>
</html>