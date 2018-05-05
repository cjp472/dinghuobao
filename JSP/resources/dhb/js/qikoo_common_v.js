
/*!art-template - Template Engine | http://aui.github.com/artTemplate/*/
;var tempTemplate;

String.prototype.protocol = function(){

    var str = this;

    str = window.isSupportWebp ? (str.replace(/(\.jpg|\.png)/g, ".webp")) : str;

    return str.replace(/http:\/\/p\d\.qh[imgs]{3}/,"https://p.ssl.qhmsg");
};
!function(){function a(a){return a.replace(t,"").replace(u,",").replace(v,"").replace(w,"").replace(x,"").split(y)}function b(a){return"'"+a.replace(/('|\\)/g,"\\$1").replace(/\r/g,"\\r").replace(/\n/g,"\\n")+"'"}function c(c,d){function e(a){return m+=a.split(/\n/).length-1,k&&(a=a.replace(/\s+/g," ").replace(/<!--[\w\W]*?-->/g,"")),a&&(a=s[1]+b(a)+s[2]+"\n"),a}function f(b){var c=m;if(j?b=j(b,d):g&&(b=b.replace(/\n/g,function(){return m++,"$line="+m+";"})),0===b.indexOf("=")){var e=l&&!/^=[=#]/.test(b);if(b=b.replace(/^=[=#]?|[\s;]*$/g,""),e){var f=b.replace(/\s*\([^\)]+\)/,"");n[f]||/^(include|print)$/.test(f)||(b="$escape("+b+")")}else b="$string("+b+")";b=s[1]+b+s[2]}return g&&(b="$line="+c+";"+b),r(a(b),function(a){if(a&&!p[a]){var b;b="print"===a?u:"include"===a?v:n[a]?"$utils."+a:o[a]?"$helpers."+a:"$data."+a,w+=a+"="+b+",",p[a]=!0}}),b+"\n"}var g=d.debug,h=d.openTag,i=d.closeTag,j=d.parser,k=d.compress,l=d.escape,m=1,p={$data:1,$filename:1,$utils:1,$helpers:1,$out:1,$line:1},q="".trim,s=q?["$out='';","$out+=",";","$out"]:["$out=[];","$out.push(",");","$out.join('')"],t=q?"$out+=text;return $out;":"$out.push(text);",u="function(){var text=''.concat.apply('',arguments);"+t+"}",v="function(filename,data){data=data||$data;var text=$utils.$include(filename,data,$filename);"+t+"}",w="'use strict';var $utils=this,$helpers=$utils.$helpers,"+(g?"$line=0,":""),x=s[0],y="return new String("+s[3]+");";r(c.split(h),function(a){a=a.split(i);var b=a[0],c=a[1];1===a.length?x+=e(b):(x+=f(b),c&&(x+=e(c)))});var z=w+x+y;g&&(z="try{"+z+"}catch(e){throw {filename:$filename,name:'Render Error',message:e.message,line:$line,source:"+b(c)+".split(/\\n/)[$line-1].replace(/^\\s+/,'')};}");try{var A=new Function("$data","$filename",z);return A.prototype=n,A}catch(B){throw B.temp="function anonymous($data,$filename) {"+z+"}",B}}var d=function(a,b){return"string"==typeof b?q(b,{filename:a}):g(a,b)};d.version="3.0.0",d.config=function(a,b){e[a]=b};var e=d.defaults={openTag:"<%",closeTag:"%>",escape:!0,cache:!0,compress:!1,parser:null},f=d.cache={};d.render=function(a,b){return q(a,b)};var g=d.renderFile=function(a,b){var c=d.get(a)||p({filename:a,name:"Render Error",message:"Template not found"});return b?c(b):c};d.get=function(a){var b;if(f[a])b=f[a];else if("object"==typeof document){var c=document.getElementById(a);if(c){var d=(c.value||c.innerHTML).replace(/^\s*|\s*$/g,"");b=q(d,{filename:a})}}return b};var h=function(a,b){return"string"!=typeof a&&(b=typeof a,"number"===b?a+="":a="function"===b?h(a.call(a)):""),a},i={"<":"&#60;",">":"&#62;",'"':"&#34;","'":"&#39;","&":"&#38;"},j=function(a){return i[a]},k=function(a){return h(a).replace(/&(?![\w#]+;)|[<>"']/g,j)},l=Array.isArray||function(a){return"[object Array]"==={}.toString.call(a)},m=function(a,b){var c,d;if(l(a))for(c=0,d=a.length;d>c;c++)b.call(a,a[c],c,a);else for(c in a)b.call(a,a[c],c)},n=d.utils={$helpers:{},$include:g,$string:h,$escape:k,$each:m};d.helper=function(a,b){o[a]=b};var o=d.helpers=n.$helpers;d.onerror=function(a){var b="Template Error\n\n";for(var c in a)b+="<"+c+">\n"+a[c]+"\n\n";"object"==typeof console&&console.error(b)};var p=function(a){return d.onerror(a),function(){return"{Template Error}"}},q=d.compile=function(a,b){function d(c){try{return new i(c,h)+""}catch(d){return b.debug?p(d)():(b.debug=!0,q(a,b)(c))}}b=b||{};for(var g in e)void 0===b[g]&&(b[g]=e[g]);var h=b.filename;try{var i=c(a,b)}catch(j){return j.filename=h||"anonymous",j.name="Syntax Error",p(j)}return d.prototype=i.prototype,d.toString=function(){return i.toString()},h&&b.cache&&(f[h]=d),d},r=n.$each,s="break,case,catch,continue,debugger,default,delete,do,else,false,finally,for,function,if,in,instanceof,new,null,return,switch,this,throw,true,try,typeof,var,void,while,with,abstract,boolean,byte,char,class,const,double,enum,export,extends,final,float,goto,implements,import,int,interface,long,native,package,private,protected,public,short,static,super,synchronized,throws,transient,volatile,arguments,let,yield,undefined",t=/\/\*[\w\W]*?\*\/|\/\/[^\n]*\n|\/\/[^\n]*$|"(?:[^"\\]|\\[\w\W])*"|'(?:[^'\\]|\\[\w\W])*'|\s*\.\s*[$\w\.]+/g,u=/[^\w$]+/g,v=new RegExp(["\\b"+s.replace(/,/g,"\\b|\\b")+"\\b"].join("|"),"g"),w=/^\d[^,]*|,\d[^,]*/g,x=/^,+|,+$/g,y=/^$|,+/;e.openTag="{{",e.closeTag="}}";var z=function(a,b){var c=b.split(":"),d=c.shift(),e=c.join(":")||"";return e&&(e=", "+e),"$helpers."+d+"("+a+e+")"};e.parser=function(a){a=a.replace(/^\s/,"");var b=a.split(" "),c=b.shift(),e=b.join(" ");switch(c){case"if":a="if("+e+"){";break;case"else":b="if"===b.shift()?" if("+b.join(" ")+")":"",a="}else"+b+"{";break;case"/if":a="}";break;case"each":var f=b[0]||"$data",g=b[1]||"as",h=b[2]||"$value",i=b[3]||"$index",j=h+","+i;"as"!==g&&(f="[]"),a="$each("+f+",function("+j+"){";break;case"/each":a="});";break;case"echo":a="print("+e+");";break;case"print":case"include":a=c+"("+b.join(",")+");";break;default:if(/^\s*\|\s*[\w\$]/.test(e)){var k=!0;0===a.indexOf("#")&&(a=a.substr(1),k=!1);for(var l=0,m=a.split("|"),n=m.length,o=m[l++];n>l;l++)o=z(o,m[l]);a=(k?"=":"=#")+o}else a=d.helpers[c]?"=#"+c+"("+b.join(",")+");":"="+a}return a},tempTemplate=d,"function"==typeof define?define(function(){return d}):"undefined"!=typeof exports?module.exports=d:tempTemplate=d}();

var qikoo = window.qikoo || {};
qikoo.widget = qikoo.widget || {};

// 喜欢
qikoo.like = (function(){
    // var eleBtn;

    var doLike = function(ele,id){
        $.post('/submit/addXh',{id:id},function(data){
            if(data.errno==0){
                riseLike(ele,id);
                likedView(ele)
            }else if(data.errno==20001 || data.errno==20002){
                likedView(ele)
            }
        },'json');
    }

    var riseLike = function(ele,id){
        var ele = ele.filter('[data-id='+id+']');
        var em = ele.find('em');

        var n = parseInt(em.eq(0).text())
        if(n>=0) em.empty().text(n+1);
    }
    
    var likedView = function(ele){
        ele.addClass('is-liked').attr("title","已喜欢");
    }

    var init = function(ele){
        var eleBtn = $(ele);
        //itemId = eleBtn.attr('data-id');

        eleBtn.click(function(){
            var ele = $(this);
            var id = ele.attr('data-id')
            if(!ele.hasClass('is-liked')){
                doLike(ele,id);
            }
        })
    }

    return {
        init : init
    }
})();

// 分享
qikoo.share = (function(){
    var eleBtn;

    var params = function(obj){
        var arr = [];
        $.each(obj,function(key,val){
            arr.push(key+"=" + encodeURIComponent(val))
        });

        return arr.join("&");
    }



    var init = function(ele,cfg,callback){
        var eleBtn = $(ele);

        eleBtn.click(function(){
            share(cfg);
            callback && callback();
        })
    }

    return {
        init : init
    }
})();

// 弹窗
qikoo.dialog = (function(){
    var eleBg,eleBox;
    var config = {};

    var init = function(){
        var htmlarr = [];
        htmlarr.push('<div class="mod-dialog-bg"></div><div class="mod-dialog">');
        config.hasNav && htmlarr.push('<div class="dialog-nav"><span class="dialog-title"></span><a href="#" onclick="return false" class="dialog-close"></a></div>');
        htmlarr.push('<div class="dialog-main"></div></div>');
        var html = htmlarr.join('');

        var eles = $(html).hide().appendTo('body');

        eleBg = eles.filter('.mod-dialog-bg');
        eleBox = eles.filter('.mod-dialog');

        eleBox.find('.dialog-close').click(function(){
            hide();
        });
    }

    var render = function(){
        //eleBox.width(config.width||'auto');
        eleBox.css('width',config.width||'');

        eleBox.find('.dialog-title').html(config.title);
        eleBox.find('.dialog-main').html(config.html);

        eleBox.show();
        eleBg.show();
        setTimeout(setPos,50);
    }

    var setPos = function(){
        // var left = ($(window).width()-eleBox.width())/2;

        var viewportH = window.innerHeight || document.documentElement.clientHeight;
        /*      
        var top=$(window).height();
        BUG：这种写法在shop/gotoorder页面，top值获取不正确，获取值应为视口高度，而在该页面下为整个文档的高度，
             在别的页面暂时未发现问题
             2016-3-9
        */
        var top = (viewportH-eleBox.height())/2;

        top = top>0?top+$(window).scrollTop() : $(window).scrollTop();

        eleBox.css({
            // left: left,
            top : top
        })
    }

    var show = function(cfg){



        if(typeof cfg != 'object'){
            cfg = {
                html : cfg || ''
            }
        }

        config = $.extend({
            title : '提示',
            html : '',
            // width : 530,
            closeFn : null,
            hasNav : true
        },cfg)

        if(!eleBox){
            init();
        }

        render();

        return eleBox;
    }

    var hide = function(){
        eleBg && eleBg.hide();
        eleBox && eleBox.hide();

        config.closeFn && config.closeFn.call(this);
    }

    return {
        show : show,
        hide : hide
    }
})();

qikoo.dialog.confirm = function(text,okFn,cancelFn){
    var html = [
        '<div class="dialog-content">',
            '<p>'+text+'</p>',
        '</div>',
        '<div class="dialog-console clearfix_new">',
            '<a class="console-btn-confirm" href="#" onclick="return false;">确定</a>',
            '<a class="console-btn-cancel" href="#" onclick="return false;">取消</a>',
        '</div>'].join('');

    var ele = qikoo.dialog.show({
        html : html
    });

    ele.find('.console-btn-confirm').click(function(){
        var rt = okFn && okFn.call(ele);
        if(rt !== false) qikoo.dialog.hide();
    })

    ele.find('.console-btn-cancel').click(function(){
        cancelFn && cancelFn.call(ele);
        qikoo.dialog.hide();
    })

    return ele;
}
 
qikoo.dialog.alert = function(text,callback){


    var html = [
        '<div class="dialog-content">',
            '<p>'+text+'</p>',
        '</div>',
        '<div class="dialog-console clearfix_new">',
            '<a class="console-btn-confirm" href="#" onclick="return false;">确定</a>',
        '</div>'].join('');

    var ele = qikoo.dialog.show({
        html : html
    });

    ele.find('.console-btn-confirm').click(function(){
        var rt = callback && callback.call(ele);
        if(rt !== false) qikoo.dialog.hide();
    })

    return ele;
}

qikoo.dialog.payNotice = function(fnSucc,fnErr,fnClose){
    fnSucc = fnSucc || function(){
        window.location.reload();
    }
    fnErr = fnErr || function(){
        window.location.reload();
    }

    var ele = qikoo.dialog.show({
        html : ['<div class="dialog-content" style="padding: 0 80px; text-align:left">',
                    '<div class="content-title">请您在新打开的页面<br>上完成付款。</div>',
                    '<p style="padding-left: 50px">付款完成前请不要关闭此窗口。<br>完成付款后请根据您的情况点击下面的按钮：</p>',
                '</div>',
                '<div class="dialog-console clearfix_new" style="margin:25px auto;width:252px;">',
                    '<a class="console-btn-confirm" href="#" onclick="return false;">已完成付款</a>',
                    '<a class="console-btn-cancel" href="#" onclick="return false;">付款遇到问题</a>',
                '</div>'].join(''),
        width : 530,
        closeFn : fnClose
    })

    ele.find('.console-btn-confirm').click(function(){
        fnSucc();
        qikoo.dialog.hide()
    });
    ele.find('.console-btn-cancel').click(function(){
        fnErr();
        qikoo.dialog.hide()

    })
}


// 跟随确认浮层
qikoo.pop = function(cfg){
    var eleBox;
    var html =[
        '<div class="mod-pop">',
            '<div class="pop-nav">',
                '<span class="pop-title"></span>',
                '<a class="pop-close" href="#" onclick="return false"></a>',
            '</div>',
            '<div class="pop-main"></div>',
        '</div>'
    ].join('');

    eleBox = $(html).hide().appendTo('body');
    eleBox.find('.pop-close').click(function(){
        eleBox.remove();
    })

    cfg = $.extend({
        title : '温馨提示：',
        html : ''
    },cfg)

    eleBox.find('.pop-title').html(cfg.title);
    eleBox.find('.pop-main').html(cfg.html);
    return eleBox;
}
qikoo.popConfirm = (function(){
    var elePop;

    var init = function(txt){
        var html = [
            '<p>'+txt+'</p>',
            '<div class="pop-console">',
                '<a class="pop-btn-green" href="#" onclick="return false">确定</a>',
                '<a class="pop-btn-gray" href="#" onclick="return false">取消</a>',
            '</div>'
        ].join('');

        return qikoo.pop({
            title : '温馨提示：',
            html : html
        });
    }

    var setPos = function(ele){
        var pos = $(ele).offset();
        pos.left = pos.left + $(ele).width() - elePop.width();
        pos.top = pos.top + $(ele).height();

        if(pos.left<0){
            pos.left=0
        }
        if(pos.top<0){
            pos.top=0
        }

        elePop.css(pos);
    }

    var bindEvent = function(cb){
        elePop.find('.pop-btn-gray').click(function(){
            hide();
        })

        elePop.find('.pop-btn-green').click(function(){
            cb && cb.call(this);
            hide();
        })
    }

    var hide = function(){
        elePop && elePop.remove();
        elePop = null;
    }

    return function(ele,txt,callback){
        //同时只能显示一个弹窗
        hide();

        elePop = init(txt);

        setPos(ele);

        bindEvent(callback);

        elePop.show();
    }
})();




// 浏览器类型判断
qikoo.browser = (function() {
    var na = window.navigator,
        ua = na.userAgent.toLowerCase(),
        browserTester = /(msie|webkit|gecko|presto|opera|safari|firefox|chrome|maxthon|android|ipad|iphone|webos|hpwos|trident)[ \/os]*([\d_.]+)/ig,
        Browser = {
            platform: na.platform
        };

    ua.replace(browserTester, function(a, b, c) {
        if (!Browser[b]) {
            Browser[b] = c;
        }
    });

    if (Browser.opera) { //Opera9.8后版本号位置变化
        ua.replace(/opera.*version\/([\d.]+)/, function(a, b) {
            Browser.opera = b;
        });
    }

    //IE 11 的 UserAgent：Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv 11.0) like Gecko
    if (!Browser.msie && Browser.trident) {
        ua.replace(/trident\/[0-9].*rv[ :]([0-9.]+)/ig, function(a, c) {
                Browser.msie = c;
            });
    }

    if (Browser.msie) {
        Browser.ie = Browser.msie;
        var v = parseInt(Browser.msie, 10);
        Browser['ie' + v] = true;
    }

    return Browser;
}());




// 图片预加载
qikoo.preLoadImg = function(imagePath){
    if($.isArray(imagePath)){
        $.each(imagePath,function(i,path){
            qikoo.preLoadImg(path);
        })
    }else if(typeof imagePath == "string"){
        $("<img>").hide().appendTo('body').attr("src",imagePath);
    }
}


// 获取url中的参数
qikoo.getUrlParam = function(param){
    var params = location.search.substr(1).split(/\&|\?/);
    var val = ""
    $.each(params,function(i,str){
        var arr = str.split('=');
        if (arr[0]==param){
            val = arr[1]
        }
    })
    return val;
}


qikoo.placeholder = (function(){
    if( !('placeholder' in document.createElement('input')) ){ 
        $('input[placeholder],textarea[placeholder]').each(function(){    
            var that = $(this),    
            text= that.attr('placeholder');    
            if(that.val()===""){    
                that.val(text).addClass('placeholder');    
            }    
            that.focus(function(){    
                if(that.val()===text){    
                    that.val("").removeClass('placeholder');    
                }    
            })    
            .blur(function(){    
                if(that.val()===""){    
                    that.val(text).addClass('placeholder');    
                }    
            })    
            .closest('form').submit(function(){    
                if(that.val() === text){    
                    that.val('');    
                }    
            });    
        });    
    }   
});


qikoo.placeholder();

