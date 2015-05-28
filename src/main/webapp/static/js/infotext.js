$(function() {
    var params = window.location.search;
    params = getParams(params);
    if(params._uuid && params.daoName){
        $.get("infoData.do",{
            action : "showInfoText",
            uuid : params._uuid,
            daoName : params.daoName
        }, function(data){
            $("body").html(data);
        });
    }
});

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