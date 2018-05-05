﻿/**
 * 处理全选或全部反选
 */
function selectAll(obj) {
	var status = jQuery(obj).attr("checked");
	var id = jQuery(obj).attr("id");
	if (status == "checked" || status) {
		jQuery(":checkbox[id!=" + id + "]").attr("checked", "checked");
	} else {

		jQuery(":checkbox[id!=" + id + "]").attr("checked", false);
	}
}

function getRootPath() {
	// 获取当前网址，如： http://localhost:8088/test/test.jsp
	var curPath = window.document.location.href;
	// 获取主机地址之后的目录，如： test/test.jsp
	var pathName = window.document.location.pathname;
	var pos = curPath.indexOf(pathName);
	// 获取主机地址，如： http://localhost:8088
	var localhostPaht = curPath.substring(0, pos);
	// 获取带"/"的项目名，如：/test
	var projectName = pathName
			.substring(0, pathName.substr(1).indexOf('/') + 1);
	return (localhostPaht + projectName);
}

/*
 * 系统通用方法，根据参数来决定处理的url和参数
 */
function cmd() {
	//alert(12121212);
	var url = arguments[0];
	var mulitId = "";
	jQuery(":checkbox:checked").each(function() {
		if (jQuery(this).val() != "") {
			if(mulitId == ""){
				mulitId = jQuery(this).val();
			} else{
				mulitId = mulitId + "," + jQuery(this).val();
			}
		}
	});
	if (mulitId != "") {
		jQuery("#ListForm #mulitId").val(mulitId);
		if (parent.window) {
			if(parent.confirm("确定要执行该操作？")){
				jQuery("#ListForm").attr("action", url);
				jQuery("#ListForm").submit();
			}
			
		} else {
			if (confirm("确定要执行该操作？")) {
				jQuery("#ListForm").attr("action", url);
				jQuery("#ListForm").submit();
			}
		}
	} else {
		if (parent.window) {
			alert("至少选择一条数据记录");
		} else {
			alert("至少选择一条数据记录");
		}
	}
}

function a_del(url) {
	if (parent.window) {
		if(parent.confirm("确定要执行该操作？")){
			window.location.href = url;
		}
	} else {
		if (confirm("确定要执行该操作？")) {
			window.location.href = url;
		}
	}
}
/**/
/* 火狐下取本地全路径 */
function getFullPath(obj) {
	if (obj) {
		// ie
		if (window.navigator.userAgent.indexOf("MSIE") >= 1) {
			obj.select();
			if (window.navigator.userAgent.indexOf("MSIE") == 25) {
				obj.blur();
			}
			return document.selection.createRange().text;
		} else if (window.navigator.userAgent.indexOf("Firefox") >= 1) { // firefox
			if (obj.files) {
				return window.URL.createObjectURL(obj.files.item(0));
			}
			return obj.value;
		}
		return obj.value;
	}
}
// 自动生成查询条件
function query() {
	jQuery("#queryCondition").empty();
	jQuery.each(jQuery("#queryForm :input"), function() {
		if (this.type != "button" && this.value != "") {
			jQuery("#queryCondition").append(
					"<input name='q_" + this.name + "'type='hidden' id='q_"
							+ this.name + "' value='" + this.value + "' />");
		}
	});
	jQuery("#ListForm").submit();
}
// 表单方式分页
function gotoPage(n) {
	jQuery("#currentPage").val(n);
	jQuery("#ListForm").submit();
}
/** 增加系统提示 */
function tipStyle() {
	if (jQuery.isFunction(jQuery().poshytip)) {
		jQuery("input[title!='']").poshytip({
			className : 'tip-skyblue',
			timeOnScreen : 2000,
			alignTo : 'cursor',
			alignX : 'right',
			alignY : 'bottom',
			offsetX : 3,
			offsetY : 3
		});
		jQuery("img[title!='']").poshytip({
			className : 'tip-skyblue',
			timeOnScreen : 2000,
			alignTo : 'cursor',
			alignX : 'right',
			alignY : 'bottom',
			offsetX : 3,
			offsetY : 3
		});
		jQuery("a[title!='']").poshytip({
			className : 'tip-skyblue',
			timeOnScreen : 2000,
			alignTo : 'cursor',
			alignX : 'right',
			alignY : 'bottom',
			offsetX : 3,
			offsetY : 3
		});
		jQuery("textarea[title!='']").poshytip({
			className : 'tip-skyblue',
			timeOnScreen : 2000,
			alignTo : 'cursor',
			alignX : 'right',
			alignY : 'bottom',
			offsetX : 3,
			offsetY : 3
		});
	}
}
// 模拟alert 
var alert_timer_id;
function showDialog() {
	var id = arguments[0];// 窗口id
	var title = arguments[1]; // 窗口标题
	var content = arguments[2];// 提示内容
	var type = arguments[3];// 0为提示框，1为确认框,2为发布框
	var icon = arguments[4];// 显示图标，包括warning,succeed,question,smile,sad,error
	var second = arguments[5];// 倒计时时间数
	var confirm_action = arguments[6];// callback方法
	if (id == undefined || id == "") {
		id = 1;
	}
	if (title == undefined || title == "") {
		title = "系统提示";
	}
	if (type == undefined || type == "") {
		type == 0;
	}
	if (icon == undefined || icon == "") {
		icon = "succeed";
	}
	if (second == undefined || second == "") {
		second = 60;
	}
	var s = "<div id='"
			+ id
			+ "'><div class='message_white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"
			+ id
			+ "\").remove();'></a><div style='border:3px solid #CCCCCC;'><div class='message_white_iframe'><h3 class='message_white_title'><span>"
			+ title
			+ "</span></h3><div class='message_white_box'><span class='message_white_img_"
			+ icon
			+ "'></span><span class='message_white_font'>"
			+ content
			+ "</span></div><h3 class='message_white_title_bottom'><span id='time_down'>"
			+ second
			+ "</span>秒后窗口关闭</h3></div></div></div><div class='black_overlay'></div>";

	var c = "<div id='"
			+ id
			+ "'><div class='message_white_content' style='width:360px; height:180px;'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"
			+ id
			+ "\").remove();'></a><div style='border:3px solid #CCCCCC;'><div class='message_white_iframe_del' style='width:352px; height:160px;'><h3 class='message_white_title'><span>"
			+ title
			+ "</span></h3><div class='message_white_box_del'><span class='message_white_img_"
			+ icon
			+ "'></span><span class='message_white_font' style='font-size:14px;'>"
			+ content
			+ "</span></div>   <div class='message_white_box1'><input id='sure' type='button' value='确定'/><input id='cancel' type='button' value='取消'/></div>    </div></div></div><div class='black_overlay'></div>";

	var m = "<div id='"
			+ id
			+ "'><div class='release_page'> <div class='release_page_title'><span class='release_page_title_left'>我要发布</span><span class='release_page_title_right'><a href='javascript:void(0);' onclick='javascript:jQuery(\"#"
			+ id
			+ "\").remove();'>×</a></span></div><div class='release_page_ul'><ul>																						<li class='baby'><a href='javascript:void(0);' id='share_select_3' share_mark='3'><div class='margin'><span>宝贝</span><br /> 晒出喜欢的宝贝</div></a></li>																														<li class='pictures'><a href='javascript:void(0);' id='share_select_4' share_mark='4'><div class='margin'><span>店铺</span><br /> 分享精品店铺</div></a></li>																															<li class='article'><a href='javascript:void(0);' id='share_select_5' share_mark='5'><div class='margin'><span>新鲜事</span><br /> 写心得写攻略</div></a></li></ul></div></div><div class='black_overlay'></div>";

	var a = "<div id='"
			+ id
			+ "'><div class='article_page'><div class='article_page_title'><span class='article_page_title_left'>"
			+ title
			+ "</span><span class='article_page_title_right'><a href='javascript:void(0);' onclick='javascript:jQuery(\"#"
			+ id
			+ "\").remove();'>×</a></span></div><div class='article_page_main'><div class='h2'><h2>正文</h2></div><textarea id='select_share_dynamic' name='select_share_dynamic' placeholder='生活每天都有新鲜事.每天都来聊一聊'></textarea><div class='article_page_bottom'><div class='article_page_input_left' id='input_show_error'></div><input type='button' value='分 享' id='select_button_dynamic' share_mark='dynamic' /></div></div></div><div class='black_overlay'></div></div>"

	if (type == 0) {// 消息框
		jQuery("body").append(s);
	}
	if (type == 1) {// 确认框
		jQuery("body").append(c);
	}
	if (type == 2) {// 发布框
		jQuery("body").append(m);
	}
	if (type == 5) {// 发布框_个人主页-发布-新鲜事窗口
		jQuery("body").append(a);
	}
	var top = jQuery(window).scrollTop()
			+ (jQuery(window).height() - jQuery(document).outerHeight()) / 2;
/*	jQuery(".message_white_content").css("margin-top",
			jQuery(window).scrollTop() + "px");*/
	var h = jQuery(document).height();
	jQuery('.black_overlay').css("height", h);

	// 设置关闭时间
	alert_timer_id = window.setInterval("closewin('" + id + "')", 1000);
	// 点击确定
	jQuery("#sure").click(function() {
		jQuery("#" + id).remove();
		runcallback(confirm_action);
	});

	function runcallback(callback) {
		callback();
	}
	// 点击取消
	jQuery("#cancel").click(function() {
		jQuery("#" + id).remove();
	});
	// 点击选择发布类型，将参数添加到页面隐藏域中
	jQuery("a[id^=share_select_]").click(function() {
		jQuery("#share_select_mark").val(jQuery(this).attr("share_mark"));
		jQuery("#" + id).remove();
		runcallback(confirm_action);
	});
	// 点击所有发布确认按钮
	jQuery("input[id^=select_button_]").click(function() {
		var share_mark = jQuery(this).attr("share_mark");
		var content = jQuery("#select_share_" + share_mark).val();
		if (content.length > 0) {
			if (content.length > 140) {
				jQuery("#input_show_error").html("输入字数不能多于140个字！");
			} else {
				jQuery("#share_select_content").val(content);
				jQuery("#" + id).remove();
				runcallback(confirm_action);
			}
		} else {
			jQuery("#input_show_error").html("请输入内容！");
		}
	});

}

function closewin(id) {
	var count = parseInt(jQuery("#" + id + " span[id=time_down]").text());
	count--;
	if (count == 0) {
		jQuery("#" + id).remove();
		window.clearInterval(alert_timer_id);
	} else {
		jQuery("#" + id + " span[id=time_down]").text(count);
	}
}
/**
 * 系统加载
 */
jQuery(document).ready(function() {
	// 改变系统提示的样式
	// jQuery("span .w").mousemove(function(){
	// var id=jQuery(this.parentNode).attr("id");
	// if(id="nothis"){
	// jQuery(this.parentNode).attr("id","this")
	// }
	// }).mouseout(function(){
	// var id=jQuery(this.parentNode).attr("id");
	// if(id="this"){
	// jQuery(this.parentNode).attr("id","nothis")
	// }
	// });
	//
	tipStyle();

});
document
		.write("<link href='"
				+ webPath
				+ "/resources/style/colorbox/colorbox.css' type='text/css' rel='stylesheet' />");
document.write("<script src='" +webPath
		+ "/resources/style/colorbox/jquery.colorbox-min.js'></script>");