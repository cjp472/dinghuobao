(function (){
    var utils = utils || {};
    utils.path = {};

    utils.path.queryUrl = function(url, key) {
        url = url.replace(/^[^?=]*\?/ig, '').split('#')[0];//去除网址与hash信息
        var json = {};
        //考虑到key中可能有特殊符号如“[].”等，而[]却有是否被编码的可能，所以，牺牲效率以求严谨，就算传了key参数，也是全部解析url�?
        url.replace(/(^|&)([^&=]+)=([^&]*)/g, function (a, b, key , value){
            //对url这样不可信的内容进行decode，可能会抛异常，try一下；另外为了得到最合适的结果，这里要分别try
            try {
                key = key.replace(/\+/g, " ");
                key = decodeURIComponent(key);
            } catch(e) {}

            try {
                value = value.replace(/\+/g, " ");
                value = decodeURIComponent(value);
            } catch(e) {}

            if (!(key in json)) {
                json[key] = /\[\]$/.test(key) ? [value] : value; //如果参数名以[]结尾，则当作数组
            }
            else if (json[key] instanceof Array) {
                json[key].push(value);
            }
            else {
                json[key] = [json[key], value];
            }
        });
        return key ? json[key] : json;
    }
    
    utils.path.makeRestfulPath = function (data){
        var paths = [];
        
        for (var i in data){
            if (data[i] !== undefined){
               paths.push([
                       "", encodeURIComponent(i), encodeURIComponent(data[i])
                   ].join('/') 
               );
            }
        }
         
        return paths.join('');
    };

    utils.path.encodeQueryJson = function (json){
        var s = [];
        for( var p in json ){
            if(json[p]==null) continue;
            if(json[p] instanceof Array)
            {
                for (var i=0;i<json[p].length;i++) s.push( encodeURIComponent(p) + '=' + encodeURIComponent(json[p][i]));
            }
            else
                s.push( encodeURIComponent(p) + '=' + encodeURIComponent(json[p]));
        }
        return s.join('&');
    };

    utils.path.getFromHash = function (key){
        var hash = location.hash; 
        if (!hash){
            return key === undefined ? {} : "";
        }
        var json = {};
        hash = hash.split("#")[1]; 
        hash.replace(/(^|&)([^&=]+)=([^&]*)/g, function (a, b, key , value){
            //对url这样不可信的内容进行decode，可能会抛异常，try一下；另外为了得到最合适的结果，这里要分别try
            try {
                key = decodeURIComponent(key);
            } catch(e) {}

            try {
                value = decodeURIComponent(value);
            } catch(e) {}

            if (!(key in json)) {
                json[key] = /\[\]$/.test(key) ? [value] : value; //如果参数名以[]结尾，则当作数组
            }
            else if (json[key] instanceof Array) {
                json[key].push(value);
            }
            else {
                json[key] = [json[key], value];
            }
        });
        return key ? json[key] : json;
    };

    window.utils = utils;
})();