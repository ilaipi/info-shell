function ShowInfoText($scope, $http) {
    $scope.clickId = "GatherShopDao";
    $scope.menuShow = "none";

    $scope.headerMap = {
        GatherShopDao: ["SHOP_CITY", "INFO_NET_NAME", "INFO_SOURCE_SHOP", "INFO_TITLE_SHOP", "FULL_CATEGORY_PATH_NAME", "SHOP_ADDRESS", "SHOP_TEL", "SHOP_HOURS",
            "SHOP_STARS", "SHOP_AVERAGE", "SHOP_FLAVOUR", "SHOP_ENV", "SHOP_SERVICE", "SHOP_RECOMMEND_DISH", "infoUrl"],
        GatherCommonInfoDao: ["INFO_NET_NAME", "INFO_SOURCE", "INFO_TITLE", "infoUrl"],
        GatherPolicyDao: ["infoTitle", "pubTime", "infoUrl"],
        GatherTechnologyDao: ["infoTitle", "pubTime", "infoUrl"],
        GatherHuiyiDao: ["infoTitle", "beginTime", "infoUrl"],
        GatherHotelDao: ["HOTEL_CITY", "INFO_NET_NAME", "INFO_SOURCE_HOTEL", "INFO_TITLE_HOTEL", "HOTEL_ADDRESS", "HOTEL_TEL","HOTEL_SERVICE",
            "HOTEL_ROOM", "HOTEL_TOP_RANK", "HOTEL_TOTAL", "HOTEL_INTRO", "HOTEL_TRANSPORT", "HOTEL_OPENED", "infoUrl"]
    };

    $scope.headerNames = {
        pubTime: "发布时间",
        infoSource: "来源网站",
        infoChannel: "采集栏目",
        infoTitle: "标题",
        originalInfoTitle: "原始标题",
        infoReference: "资讯出处",
        infoUrl: "原始url",
        beginTime: "开始时间",
        endTime: "结束时间",
        meetingPlace: "会议地点",
        meetingOrganizer: "主办方",
        meetingUndertaker: "承办方",


        //基础信息部分
        INFO_NET_NAME: "来源网站",
        INFO_SOURCE: "信息分类",
        INFO_TITLE: "分类标题",

        //店铺信息部分
        INFO_SOURCE_SHOP: "店铺分类",
        INFO_TITLE_SHOP: "店铺名称",
        FULL_CATEGORY_PATH_NAME: "全分类路线",
        SHOP_ADDRESS: "店铺地址",
        SHOP_TEL: "店铺电话",
        SHOP_HOURS: "营业时间",
        SHOP_STARS: "点评星级",
        SHOP_AVERAGE: "店铺人均消费",
        SHOP_SERVICE: "店铺服务评分",
        SHOP_FLAVOUR: "店铺口味评分",
        SHOP_ENV: "店铺环境评分",
        SHOP_CITY: "城市",
        SHOP_RECOMMEND_DISH: "推荐菜",

        //酒店信息部分
        INFO_SOURCE_HOTEL: "酒店分类",
        INFO_TITLE_HOTEL: "酒店名称",
        HOTEL_ADDRESS: "酒店地址",
        HOTEL_TEL: "酒店电话",
        HOTEL_SERVICE: "服务设施",
        HOTEL_ROOM: "客房设施",
        HOTEL_CITY: "城市",
        HOTEL_TOP_RANK: "当地排名",
        HOTEL_TOTAL: "综合设施",
        HOTEL_INTRO: "酒店简介",
        HOTEL_TRANSPORT: "酒店交通/景点",
        HOTEL_OPENED: "开业/装修"

    };

    $scope.selectDao = function(daoName) {
        $scope.clickId = daoName;
        $scope.menuShow = "none";
    };
    $scope.showMenu = function() {
        $scope.menuShow = "block";
    };
    $scope.search = function() {
        console.log("queryStr:", $scope.queryStr);
        if ($scope.queryStr || $scope.infoId) {
            $http({
                url: "infoData.do",
                method: "GET",
                params: {action: "searchInfoList", daoName: $scope.clickId, queryStr: $scope.queryStr, infoId : $scope.infoId}
            }).success(function(data) {
                var tempList = [data.length];
                var headers = $scope.headerMap[$scope.clickId];
                for (var i = 0; i < data.length; i++) {
                    var tempData = {};
                    tempData['uuid'] = data[i].uuid;
                    for (var j = 0;j<headers.length;j++) {
                        var header = headers[j];
                        var value = data[i][header];
                        switch (header) {
                            case "beginTime" :
                            case "endTime" :
                            case "pubTime" :
                                value = dateFormat(new Date(value), "yyyy-MM-dd hh:mm:ss");
                                break;
//                            case "infoUrl" :
//                                if (value && value.length > 50) {
//                                    value = value.substring(0, 46) + "...";
//                                }
//                                break;
                        }
                        tempData[header] = value;
                    }
                    tempList[i] = tempData;
                }
                $scope.infoList = tempList;
                console.log("infoList:", $scope.infoList);
            });
        }
    };
}

function dateFormat(date, format) {
    if(format === undefined){
        format = date;
        date = new Date();
    }
    var map = {
        "M": date.getMonth() + 1, //月份 
        "d": date.getDate(), //日 
        "h": date.getHours(), //小时 
        "m": date.getMinutes(), //分 
        "s": date.getSeconds(), //秒 
        "q": Math.floor((date.getMonth() + 3) / 3), //季度 
        "S": date.getMilliseconds() //毫秒 
    };
    format = format.replace(/([yMdhmsqS])+/g, function(all, t){
        var v = map[t];
        if(v !== undefined){
            if(all.length > 1){
                v = '0' + v;
                v = v.substr(v.length-2);
            }
            return v;
        }
        else if(t === 'y'){
            return (date.getFullYear() + '').substr(4 - all.length);
        }
        return all;
    });
    return format;
}