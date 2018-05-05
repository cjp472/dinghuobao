//购买数量增加或减少
var DOEVENT = document.hasOwnProperty("ontouchstart") ? "tap" : "click";

(function ($) {
	$.extend($,{
		showMask:function(){			 
			if(!$("#load_mask")[0]){
				var h = $(document.body).height()+"px";
			 	$("<div id='load_mask' class='tc_zz' style='height:"+h+"' />").appendTo("body");
			}else{				 
				$("#load_mask").show();
			}
			return this;
		},
		hideMask:function(){
			$("#load_mask").hide();			 
			return this;
		},
		 
        tip : function(content,s) {
            if(content){
				$("#alert_tip")[0]||$("body").append("<div id='alert_tip'><div></div><div class='alert_content'>"+content+"</div></div>");
				var t=$(document).scrollTop(),h=$(window).height();
				$("#alert_tip").css("top",h*0.3+t+"px");
				$("#alert_tip").is(":hidden")&&(
					$("#alert_tip > div.alert_content").html(content),
					$("#alert_tip").show(),
					typeof(s)=="number"?s!=-1&&window.setTimeout(function(){$("#alert_tip").hide();},s*1000):window.setTimeout(function(){$("#alert_tip").hide();},3000))
			}
			return this;
        },
		after : function(s,callback) {
			s = s || 10;
			callback = $.type(callback)==="function" ? callback : function(){null};
			window.setTimeout(callback,s);
		},
		Dialog:function(config){
			var options = {
				title: "系统提示",  // 弹窗的标题
				content: null,    // 内容:文本或html
				button: {},     // 按钮对象 {按钮文字:点击事件,...}
				ismask: !0,   // 是否遮罩
				isclose: !0,   // 关闭按钮
				callback: null   // 弹出后回调
			},html, $D, D =  $.extend(options, config);
			$("#Dialog")[0]&&$("#mask").remove();$("#Dialog").remove();
			D.ismask&&(html = "<div id='mask'></div>");
			html+="<div id='Dialog'><div><b>"+D.title+"</b><span><a href='javascript:'></a></span></div><div class='text'><div></div>";
			$(html).appendTo("body");$D=$("#Dialog");
			D.ismask&&$("#mask").css("height",$("body").height());
			var t=$(document).scrollTop(),h=$(window).height();
			D.content&&$D.find(".text").html(D.content);
			$D.css("top",h*0.2+t+"px");
			typeof(D.callback)=="function"&&D.callback($D);
			D.isclose&&$("#Dialog span a").text("关闭").on(events_click,function(){$D.close();});
			!$.isEmptyObject(D.button)&&$.each(D.button, function(t, f){$("<a href='javascript:;'>"+t+"</a>").appendTo("#Dialog").on(events_click,function(){f($D);});});
			$D.close = function(){$("#mask").remove();$D.remove();};
			return this;
		}		 
	});
 
})($);

// js stringbuffer类
function StringBuffer()
{
    this.__strings__ =[];
}
StringBuffer.prototype.append = function(str){
    this.__strings__.push(str);
};
StringBuffer.prototype.toString = function(){
    return this.__strings__.join("");
};

// 输出并显示功能导航菜单
/*
 * function showNav() { if ($("#divNav")[0]) { $("#divNav").remove(); }
 * 
 * var buffer = new StringBuffer(); buffer.append("<div class=\"gncd\"
 * id=\"divNav\">"); buffer.append("<header class=\"header\"><span class=\"fh
 * left\"><a href=\"#\">&nbsp;</a></span><span class=\"title\">功能导航</span></header>");
 * buffer.append("<div class=\"gncd_bottom\">"); buffer.append(" <a href=\"#\"
 * class=\"proCateg\">"); buffer.append(" <span>"); buffer.append(" <img
 * src=\"images/v2/public/ioc1.png\"></span>"); buffer.append(" <p>商品分类</p>");
 * buffer.append(" </a>"); buffer.append(" <a href=\"#\">"); buffer.append("
 * <span>"); buffer.append(" <img src=\"images/v2/public/ioc2.png\"></span>");
 * buffer.append(" <p>热门搜索</p>"); buffer.append(" </a>"); buffer.append(" <a
 * href=\"#\">"); buffer.append(" <span>"); buffer.append(" <img
 * src=\"images/v2/public/ioc3.png\"></span>"); buffer.append(" <p>我的收藏</p>");
 * buffer.append(" </a>"); buffer.append(" <a href=\"#\">"); buffer.append("
 * <span>"); buffer.append(" <img src=\"images/v2/public/ioc4.png\"></span>");
 * buffer.append(" <p>订单查询</p>"); buffer.append(" </a>"); buffer.append(" <a
 * href=\"#\">"); buffer.append(" <span>"); buffer.append(" <img
 * src=\"images/v2/public/ioc5.png\"></span>"); buffer.append(" <p>联系客服</p>");
 * buffer.append(" </a>"); buffer.append(" <a href=\"#\">"); buffer.append("
 * <span>"); buffer.append(" <img src=\"images/v2/public/ioc6.png\"></span>");
 * buffer.append(" <p>我的海尔</p>"); buffer.append(" </a>"); buffer.append(" <a
 * href=\"#\">"); buffer.append(" <span>"); buffer.append(" <img
 * src=\"images/v2/public/ioc7.png\"></span>"); buffer.append(" <p>我的浏览</p>");
 * buffer.append(" </a>"); buffer.append(" <a href=\"#\">"); buffer.append("
 * <span>"); buffer.append(" <img src=\"images/v2/public/ioc8.png\"></span>");
 * buffer.append(" <p>我的评价</p>"); buffer.append(" </a>"); buffer.append(" <a
 * href=\"#\" class=\"more\">"); buffer.append(" <p>更多>></p>");
 * buffer.append(" </a>"); buffer.append("</div>"); buffer.append("</div>");
 * 
 * //追加到body $(buffer.toString()).appendTo($(document.body)); //动态设定高度
 * $("#divNav").height(document.body.scrollHeight ||
 * document.documentElement.scrollHeight).show();
 * 
 * $("#divNav .fh a").on(DOEVENT, function (e) { e.preventDefault();
 * $("#divNav").hide(); });
 * 
 * //商品分类 var categBuffer = new StringBuffer(); categBuffer.append("<a
 * href=\"#\">"); categBuffer.append(" <span>"); categBuffer.append(" <img
 * src=\"images/v2/public/s_ioc1.png\"></span>"); categBuffer.append(" <p>冰箱</p>");
 * categBuffer.append(" </a>"); categBuffer.append(" <a href=\"#\">");
 * categBuffer.append(" <span>"); categBuffer.append(" <img
 * src=\"images/v2/public/s_ioc2.png\"></span>"); categBuffer.append(" <p>空调</p>");
 * categBuffer.append(" </a>"); categBuffer.append(" <a href=\"#\">");
 * categBuffer.append(" <span>"); categBuffer.append(" <img
 * src=\"images/v2/public/s_ioc3.png\"></span>"); categBuffer.append(" <p>洗衣机</p>");
 * categBuffer.append(" </a>"); categBuffer.append(" <a href=\"#\">");
 * categBuffer.append(" <span>"); categBuffer.append(" <img
 * src=\"images/v2/public/s_ioc4.png\"></span>"); categBuffer.append(" <p>彩电</p>");
 * categBuffer.append(" </a>"); categBuffer.append(" <a href=\"#\">");
 * categBuffer.append(" <span>"); categBuffer.append(" <img
 * src=\"images/v2/public/s_ioc5.png\"></span>"); categBuffer.append(" <p>厨房卫浴</p>");
 * categBuffer.append(" </a>"); categBuffer.append(" <a href=\"#\">");
 * categBuffer.append(" <span>"); categBuffer.append(" <img
 * src=\"images/v2/public/s_ioc6.png\"></span>"); categBuffer.append(" <p>手机数码</p>");
 * categBuffer.append(" </a>"); categBuffer.append(" <a href=\"#\">");
 * categBuffer.append(" <span>"); categBuffer.append(" <img
 * src=\"images/v2/public/s_ioc7.png\"></span>"); categBuffer.append(" <p>生活家电</p>");
 * categBuffer.append(" </a>"); categBuffer.append(" <a href=\"#\">");
 * categBuffer.append(" <span>"); categBuffer.append(" <img
 * src=\"images/v2/public/s_ioc8.png\"></span>"); categBuffer.append(" <p>家庭医疗</p>");
 * categBuffer.append(" </a>");
 * 
 * $("#divNav .proCateg").on(DOEVENT, function () { $("#divNav
 * .title").text("商品分类"); $(this).parent().html(categBuffer.toString()); }); }
 */
// 隐藏功能导航菜单
/*
 * function hideNav() { $("#divNav").hide(); }
 */
// 展示导航菜单
/*
 * $(function(){ $(".right_but").on(DOEVENT, function () { showNav(); }); });
 */

// remove the left space
function ltrim(s) {
	if(s == null || s == "")
		return s;
    return s.replace(/^\s*/, "");
}
// remove the right space
function rtrim(s) {
	if(s == null || s == "")
		return s;
    return s.replace(/\s*$/, "");
}
// remove the left and the right space
function trim(s) {
	if(s == null || s == "")
		return s;
    // return rtrim(ltrim(s));// can remove half-width characters only
	var temp = s.replace(/(^\s*)|(\s*$)/g, "");// firstly , remove half-width
												// characters space
	return temp.replace(/(^　*)|(　*$)/g, "");// second,remove full-width
											// characters space.
}

function filterSpecialStr(str) {

    var filterStr1 = str ;
    // filterStr = str.replaceAll(filterStr, "", "","");
    // filterStr += str.replaceAll(filterStr, "'", "");
    for (var i = 0; i < filterStr1.length; i ++) {
        // filterStr1 = filterStr1.replace( " ", "");
        filterStr1 = filterStr1.replace("%", "");
        filterStr1 = filterStr1.replace("#", "");
        filterStr1 = filterStr1.replace("&", "");
        filterStr1 = filterStr1.replace("*", "");
        filterStr1 = filterStr1.replace("(", "");
        filterStr1 = filterStr1.replace(")", "");
        filterStr1 = filterStr1.replace("@", "");
        filterStr1 = filterStr1.replace("`", "");
        filterStr1 = filterStr1.replace("/", "");
        filterStr1 = filterStr1.replace("\"", "");
        filterStr1 = filterStr1.replace(",", "");
        // filterStr1 = filterStr1.replace( ".", "");
        filterStr1 = filterStr1.replace("=", "");
        // filterStr1 = filterStr1.replace( "<", "");
        // filterStr1 = filterStr1.replace( ">", "");
        filterStr1 = filterStr1.replace("$", "");
        filterStr1 = filterStr1.replace("!", "");
        filterStr1 = filterStr1.replace("?", "");
        filterStr1 = filterStr1.replace("^", "");
        // filterStr1 = filterStr1.replace( "~", "");
        filterStr1 = filterStr1.replace(":", "");
        filterStr1 = filterStr1.replace(";", "");
        filterStr1 = filterStr1.replace("+", "");
    }

    if (filterStr1 == str) {
        return true;
    } else {
        return false;
    }
}



/**
 * js合法性检查工具文件 包含的功能如下： isNull(str) 检查输入字符串是否为空或者全部都是空格 rtrim(stringObj)
 * 去字符串右端空格 ltrim(stringObj) 去字符串左端空格 trim(stringObj) 去字符串两端空格 isNumber(str)
 * 检查输入字符串是否符合正整数格式 getMaxDay(year, month) 取得指定年月的最大天数 isDate(date, fmt)
 * 是否是按指定格式格式化日期字符串 checkTwoDate(startDate, endDate, fmt)
 * 检查输入的起止日期是否正确，规则为两个日期的格式正确，且结束如期>=起始日期 isLastMatch(str1, str2) 判断字符1是否以字符串2结束
 * isEmail(s) 检查输入对象的值是否符合E-Mail格式 cTrim(sInputString, iType) 字符串去空格的函数
 */


/**
 * 检查输入字符串是否为空或者全部都是空格
 * 
 * @param str
 *            需要进行检查的字符串
 * @return 如果全是空返回true,否则返回false
 */
function isNull(str) {
    if (str == "") return true;
    var regu = "^[ ]+$";
    var re = new RegExp(regu);
    return re.test(str);
}


/**
 * 检查输入字符串是否符合正整数格式
 * 
 * @param str
 *            需要进行检查的字符串
 * @return 如果通过验证返回true,否则返回false
 */
function isNumber(str) {
    var regu = "^[0-9]+$";
    var re = new RegExp(regu);
    if (str.search(re) != -1) {
        return true;
    } else {
        return false;
    }
}

/**
 * 取得指定年月的最大天数
 * 
 * @param year
 *            年
 * @param month
 *            月
 * @return 指定年月的最大天数 1=31 2=29/30 3=31 4=30 5=31 6=30 7=31 8=31 9=30 10=31 11=30
 *         12=31
 */
function getMaxDay(year, month) {
    if (month == 4 || month == 6 || month == 9 || month == 11)
        return "30";
    if (month == 2) {
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)return "29";
        else return "28";
    }
    return "31";
}

/**
 * 是否是按指定格式格式化日期字符串
 * 
 * @param date
 *            日期
 * @param fmt
 *            格式化的格式
 * @return 如果是返回true,否则返回false
 */
function isDate(date, fmt) {
    if (fmt == null) fmt = "yyyyMMdd";
    var yIndex = fmt.indexOf("yyyy");
    if (yIndex == -1) return false;
    var year = date.substring(yIndex, yIndex + 4);
    var mIndex = fmt.indexOf("MM");
    if (mIndex == -1) return false;
    var month = date.substring(mIndex, mIndex + 2);
    var dIndex = fmt.indexOf("dd");
    if (dIndex == -1) return false;
    var day = date.substring(dIndex, dIndex + 2);
    if (!isNumber(year) || year > "2100" || year < "1900") return false;
    if (!isNumber(month) || month > "12" || month < "01") return false;
    if (day > getMaxDay(year, month) || day < "01") return false;
    return true;
}

/**
 * 检查输入的起止日期是否正确，规则为两个日期的格式正确，且结束如期>=起始日期
 * 
 * @param startDate
 *            起始日期，字符串
 * @param endDate
 *            结束如期，字符串
 * @param fmt
 *            日期格式化格式
 * @return 如果通过验证返回true,否则返回false
 */
function checkTwoDate(startDate, endDate, fmt) {
    if (!isDate(startDate, fmt)) {
        alert("起始日期不正确!");
        return false;
    } else if (!isDate(endDate, fmt)) {
        alert("终止日期不正确!");
        return false;
    } else if (startDate > endDate) {
        alert("起始日期不能大于终止日期!");
        return false;
    }
    return true;
}

/**
 * 判断字符1是否以字符串2结束
 * 
 * @param str1
 * @param str2
 * @return 如果通过验证返回true,否则返回false
 */
function isLastMatch(str1, str2) {
    var index = str1.lastIndexOf(str2);
    if (str1.length == index + str2.length) return true;
    return false;
}

/**
 * 检查输入对象的值是否符合E-Mail格式
 * 
 * @param s
 *            输入的字符串
 * @return 如果通过验证返回true,否则返回false
 */
function isEmail(s) {
    if (s == null || s == "" || s.length < 5 || s.length > 150)
        return false;
    var regu = "^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[_.0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT|INFO|info|tom|TOM|biz|BIZ)$";
    var re = new RegExp(regu);
    if (s.search(re) != -1)
        return true;
    else
        return false;
}

/**
 * 字符串去空格的函数
 * 
 * @param sInputString
 *            原始字符串
 * @param iType
 *            0=去掉字符串左边和右边的空格 1=去掉字符串左边的空格 2=去掉字符串左边的空格
 * @return 去掉空格的字符串
 */
function cTrim(sInputString, iType) {
    var sTmpStr = ' ';
    var i = -1;
    if (iType == 0 || iType == 1) {
        while (sTmpStr == ' ') {
            ++i;
            sTmpStr = sInputString.substr(i, 1);
        }
        sInputString = sInputString.substring(i);
    }
    if (iType == 0 || iType == 2) {
        sTmpStr = ' ';
        i = sInputString.length;
        while (sTmpStr == ' ') {
            --i;
            sTmpStr = sInputString.substr(i, 1);
        }
        sInputString = sInputString.substring(0, i + 1);
    }
    return sInputString;
}

/**
 * 用途：检查输入字符串是否符合金额格式 格式定义为带小数的正数，小数点后最多三位 输入： s：字符串 返回： 如果通过验证返回true,否则返回false
 */
function isMoney(s) {
    // var regu = "^[0-9]+[\.][0-9]{0,3}$";
    var regu = /^[0-9]+.?[0-9]{0,2}$/;
    var re = new RegExp(regu);
    if (re.test(s)) {
        return true;
    } else {
        return false;
    }
}
/**
 * 用途：检查输入字符串是否符合正整数格式 输入： s：字符串 返回： 如果通过验证返回true,否则返回false
 */
function isNumber(s) {
    var regu = "^[0-9]+$";
    var re = new RegExp(regu);
    if (s.search(re) != -1) {
        return true;
    } else {
        return false;
    }
}
/**
 * 用途：检查输入字符串是否是域名 输入： s：字符串 返回： 如果通过验证返回true,否则返回false
 */
function isdomainchar(strdomainchar) {
    var regu_domainchar = "^[_0-9a-zA-Z-]+$";
    var re_domainchar = new RegExp(regu_domainchar);
    if (strdomainchar.search(re_domainchar) != -1) {
        return true;
    } else {
        return false;
    }
}
function isdomainlastgroup(strdomainlastgroup) {
    var mygroup = new Array(13);
    mygroup[0] = "com";
    mygroup[1] = "cn";
    mygroup[2] = "net";
    mygroup[3] = "gov";
    mygroup[4] = "mobi";
    mygroup[5] = "biz";
    mygroup[6] = "cc";
    mygroup[7] = "mil";
    mygroup[8] = "org";
    mygroup[9] = "edu";
    mygroup[10] = "int";
    mygroup[11] = "info";
    mygroup[12] = "tom";
    for (imygroup = 0; imygroup < mygroup.length; imygroup++)if (strdomainlastgroup == mygroup[imygroup])break;
    if (imygroup == mygroup.length)return false;
    else return true;
}
function isDomain(strDomain) {
    var strDomainlowcase = strDomain.toLowerCase();
    var doaminArr = strDomainlowcase.split(".");
    if (doaminArr.length < 2 || doaminArr.length > 4){
		// alert("doaminArr.length="+doaminArr.length+" doaminArr.length < 2 ||
		// doaminArr.length > 4");
		return false;
		} // 域名至少有两组 www.baidu.com 或baidu.com 最多4 组
    for (idomain = 0; idomain < doaminArr.length; idomain++) {
        if (doaminArr[idomain] == null || doaminArr[idomain] == "" || !isdomainchar(doaminArr[idomain])){
			// alert("isDomain()=no");
			return false;
			}
    }
    // 判断最后一组
    if (!isdomainlastgroup(doaminArr[doaminArr.length - 1])){
    	// alert("判断最后一组是否是域名的后缀！");
    	return false;
    	}
    return true;
}
function isDomainString(strDomainString) {
    var domainstring_js=window.location.hostname;
    var indexpointfirst=domainstring_js.indexOf(".");
    var domainsecond_js=".ctoshop.com";
    if(indexpointfirst!=-1) domainsecond_js=domainstring_js.substring(indexpointfirst);

    var strDomainStringlowcase = strDomainString.toLowerCase();
    var isok = "yes";
    var DomainStringArr = strDomainStringlowcase.split(",");
    if (strDomainStringlowcase.length < 4 || strDomainStringlowcase.indexOf(domainsecond_js) != -1)
    {
        return false;
    }
    var strDomainStringlowcase_piont=","+strDomainStringlowcase;// 方便判断域名是否有重复的

    for (iDomainString = 0; iDomainString < DomainStringArr.length; iDomainString++) {
     if (DomainStringArr[iDomainString] == null || DomainStringArr[iDomainString] == "" || !isDomain(DomainStringArr[iDomainString]) || strDomainStringlowcase_piont.indexOf(","+DomainStringArr[iDomainString]) != strDomainStringlowcase_piont.lastIndexOf(","+DomainStringArr[iDomainString]))
        // 判断是否有重复的域名
        {
			      isok = "no";
            break;
        }
    }
    if (isok == "yes"){
    	// alert("isok =================================== yes");
    	return true;
    	}
    else {
    	// alert("isok ==================================== no");
    	return false;
    	}
}

/**
 * 检测输入的域名是否合法
 * 
 * @param str_url
 *            域名字符串
 */
function isValidURL(str_url) {
    var strRegex = "^((https|http|ftp|rtsp|mms)?://)?(([0-9a-z_!~*'().&=+$%-]+:)?[0-9a-z_!~*'().&=+$%-]+@)?"
            + "(([0-9]{1,3}\.){3}[0-9]{1,3}|([0-9a-z_!~*'()-]+\.)*([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\."
            + "[a-z]{2,6})(:[0-9]{1,4})?((/?)|(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
    var re = new RegExp(strRegex);
    return (re.test(str_url) ? (true) : (false));
}


	// 全选
    function checkAll(name) {
        var names = document.getElementsByName(name);
        var len = names.length;
        if (len > 0) {
            var i = 0;
            for (i = 0; i < len;i++)
            names[i].checked = true;
        }
    }

    // 全不选
    function checkAllNo(name) {
        var names = document.getElementsByName(name);
        var len = names.length;
        if (len > 0) {
            var i = 0;
            for (i = 0; i < len; i++)
            	names[i].checked = false;
        }
    }

    // 反选
    function reserveCheck(name) {
        var names = document.getElementsByName(name);
        var len = names.length;
        if (len > 0) {
            var i = 0;
            for (i = 0; i < len; i++) {
                if (names[i].checked) names[i].checked = false;
                else names[i].checked = true;
            }
        }
    }
	// 判断是调用全选还是调用反选(按钮触发)
	function selec(name,zid){
		var sele = document.getElementById(zid).value;
		if(sele == "全选"){
			document.getElementById(zid).value='反选';
			sele="反选";
// $("#quanxuan").val("反选");
			checkAll(name);
		}else{		
			document.getElementById(zid).value="全选";	
			sele="全选";
// $("#quanxuan").val("全选");
			reserveCheck(name);
		} 
	}
	// 判断是调用全选还是调用反选(复选框触发)
	function juedin(name,zid){
		var names = document.getElementById(zid);
		if(names.checked){
			checkAll(name);
			// document.getElementById(zid+"Span").innerHTML="反选";
		}else{
			reserveCheck(name);
			names.checked=false;
			// document.getElementById(zid+"Span").innerHTML="全选";
		} 
	}
	
	// 得到选择的box的vlaue
	function getSelectedBox(boxName) {
		var values = "";
		var _boxs = document.getElementsByName(boxName);
		for (var i = 0; i < _boxs.length; i++) {
			if (_boxs[i].checked == true) {
				values = values + _boxs[i].value + ",";
			}
		}
		values = values.substring(0, values.length - 1);
		return values;
	}
	
	// 返回选中的复选框的个数
	function getSelectedBoxCount(boxName) {
		var count = 0;
		var _boxs = document.getElementsByName(boxName);
		for (var i = 0; i < _boxs.length; i++) {
			if (_boxs[i].checked == true) {
				count ++;
			}
		}
		return count;
	}
	
	// checkbox只能选一个 eg:<input type="checkbox" name="box"
	// onclick='getOnlyOneBox(this.name,this)'/>
	function getOnlyOneBox(boxName,obj) {
		var values = "";
		var _boxs = document.getElementsByName(boxName);
		var b = obj.checked;
		for (var i = 0; i < _boxs.length; i++) {
			if (_boxs[i].checked == true) {
				_boxs[i].checked = false;
			}
		}
		if(!b){
			obj.checked = false;
		}else{
			obj.checked = true;
		}
	}
	
	// 重置 str是所有要重置的input的id id之间用,分隔 eg:chongzhi('id1,id2,id3')
	function chongzhi(str){
    	var len = str.split(",").length;
    	for(var i=0;i<len;i++){
    		var id = str.split(",")[i];
    		document.getElementById(id).value="";
    	}
    }
	
	function colorChange(t) {		// 改变当前行的颜色
		if(event.srcElement.tagName=="TD"){
			for (var i=0; i<t.rows.length; i++) {                         					// 遍历行
	      		for (var j=0; j<t.rows[i].cells.length; j++)  {            					// 遍历列
	      			objId = t.rows[i].id;
	         		t.rows[i].id = t.rows[i] == event.srcElement.parentNode ? 'clickTD':''; // 改变背景色
	         	}
	        }
	    }else{
	    	for (var i=0; i<t.rows.length; i++) {                         					// 遍历行
	      		for (var j=0; j<t.rows[i].cells.length; j++)  {            					// 遍历列
	      			objId = t.rows[i].id;
	         		t.rows[i].id = t.rows[i] == event.srcElement.parentNode.parentNode ? 'clickTD':'';  // 改变背景色
	         	}
	        }
	    }
	}
	
/**
 * ids用,号分隔 验证失败后渲染控件的样式
 */
function renderError(ids){
	var id = ids.split(",");
	for(var i=0;i<id.length;i++){
		$("#"+id[i]).attr("class","validate_style");
	}
}

/**
 * elementIds用,号分隔 type计算方式 加、减、乘、除 return restult
 */
function calculate(elementIds,type){
	var id = new Array();
	id = elementIds.split(",");
	var restult = 0;
	if("add" == type){			/* + */
		for(var i=0;i<id.length;i++){
			var val = document.getElementById(id[i]).value;
			restult = restult - (-val);
		}
	}else if("cut" == type){	/* - */
		for(var i=0;i<id.length;i++){
			var val = document.getElementById(id[i]).value;
			if(i == 0){
				restult = val;
			}else{
				restult = restult - val;
			}
		}
	}else if("ride" == type){	/* * */
		for(var i=0;i<id.length;i++){
			var val = document.getElementById(id[i]).value;
			if(i == 0){
				restult = val;
			}else{
				restult = restult * val;
			}
		}
	}else if("del" == type){	/* / */
		for(var i=0;i<id.length;i++){
			var val = document.getElementById(id[i]).value;
			if(i == 0){
				restult = val;
			}else{
				restult = restult / val;
			}
		}
	}
	return restult;
}

/**
 * 功能：禁用页面上的按钮,例如:var buttons=new
 * Array("button1","button2","button3");btnIsEnable(buttons); 参数 ：
 * btnIds：所有按钮控件ID的数组
 */
function btnIsEnable(btnIds,type){
	for(var i=0;i<btnIds.length;i++){
		if("dis" == type){
			// $("#" + btnIds[i]).attr("disabled","true");
			document.getElementById(btnIds[i]).disabled = true;
		}else{
			// $("#" + btnIds[i]).attr("disabled","false");
			document.getElementById(btnIds[i]).disabled = false;
		}
	}
}
/**
 * 使用onblur事件使控件只能输入number类型 默认值为1
 */
function checkNumber(ele, mrNumber){
	var val = isNumber(ele.value) == true ? ele.value:mrNumber;
	ele.value = val;
}

/**
 * 去掉input后台的提示信息
 * 
 * @param ele
 */
function checkNotNull(ele){
	var val = ele.value;
	if(val != null && val != "")
		document.getElementById(ele.id+"span").innerHTML="";
		// $(ele).parent().find("span").html("");
}

// 表单方式分页
function gotoPage(n){
	document.getElementById("currentPage").value=n;
	document.getElementById("ListForm").submit();
}