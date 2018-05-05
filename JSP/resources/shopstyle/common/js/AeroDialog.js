var imgPath = "";

function getRnd() {
	return String((new Date()).getTime()).replace(/\D/gi, '');
}
function GetRandom(min, max) {// 生成随机数
	if (min > max) {
		return (-1);
	}
	if (min == max) {
		return (min);
	}
	return (min + parseInt(Math.random() * (max - min + 1)));
}
// 可自动设置参数
window.AeroDialog = {
	// 自适应最小宽度
	minWidth : 250,
	// 自适应最大宽度,auto
	maxWidth : 333,
	// 模态层
	Cover : {
		// 透明度
		opacity : 0.7,
		// 背景颜色
		background : '#DCE2F1'
	},
	// 动画效果
	Flash : false,
	// 动画效果
	WinFlash : {
		Speed : 300,// 效果延迟时间,单位是毫秒
		FlashMode : 'easeOutCubic'// 特效方式
	},
	// 按钮文本
	Btntxt : {
		// action 值 ok
		OK : '确 定',
		// action 值 no
		NO : ' 否 ',
		// action 值 yes
		YES : ' 是 ',
		// action 值 cancel
		CANCEL : '取 消',
		// action 值 CLOSE
		CLOSE : '关闭'
	}
};
(function($) {
	function MainAeroWindow(wTitle, wBtns, wContent) {
		return [
				'<iframe  style="position:absolute;z-index:-1;width:100%;height:100%;top:0;left:0;scrolling:no;_width:expression(this.parentNode.offsetWidth);_height:expression(this.parentNode.offsetHeight);opacity:0;filter:alpha(opacity=0)" frameborder="0" src="about:blank"></iframe><div class="AeroWindow">',
				'<table border="0" cellspacing="0" cellpadding="0">', ' <tr>',
				'   <td class="tb-tl"></td>', '   <td class="tb-tm"></td>',
				'  <td class="tb-tr"></td>', '</tr>', '<tr>',
				'  <td class="tb-lm"></td>', '  <td class="tb-mm">',
				'  <div class="titles"><nobr><span><b>', wTitle,
				'</b></span></nobr></div>', '  <div class="winBtns">', wBtns,
				'   </div>', '   <div class="tb-mm-container">', wContent,
				'   </div>', '  </td>', '  <td class="tb-rm"></td>', '</tr>',
				'<tr>', '  <td class="tb-bl"></td>',
				'  <td class="tb-bm"></td>', '  <td class="tb-br"></td>',
				'</tr>', '</table>', '</div>' ].join('');
	}

	// 以下为对话框代码
	function divCover() {// 遮罩层
		return '<div id="winDialogCover" style="width:100%; height:100%; margin:0px; padding:0px; position:fixed;top:0px; left:0px;z-index:10000;background-color:'
				+ ad.Cover.background
				+ '; opacity:'
				+ ad.Cover.opacity
				+ ';filter:alpha(opacity=70);display:block; "></div>';
	}

	function callBack(b) {// button点击，回调函数返回值
		var d, e = ad.btn.CLOSE.concat(b.buttons);
		$.each(e, function(e, f) {
			$("#" + b.id + "_" + f.result).click(
					function(e) {
						var g = $(this);
						return g.attr("disabled", "disabled"), d = b
								.callback(f.result),
								(typeof d == "undefined" || d)
										&& ad.close(b.id), g
										.removeAttr("disabled"), e
										.preventDefault(), !1
					})
		})
	}

	function AeroDialogs(c, t, fn, ty) {
		var $id = "Dialog" + getRnd() + "-" + ty;// 生成ID
		f = {
			id : $id,
			icon : ty,
			WinTitle : typeof t == 'undefined' ? ty : t,
			type : ty,
			content : c,
			top : 0,
			left : 0,
			callback : typeof fn == 'undefined' ? $.noop : fn
		};
		if (ty == "alert" || "success" || "error")
			f.buttons = ad.btn.OK;
		switch (ty) {
		case "confirm":
			f.buttons = ad.btn.OKCANCEL;
			break;
		case "warning":
			f.buttons = ad.btn.YESNOCANCEL
		}
		var wbtn = '<div class="winbtn-leftadge"></div><a id="'
				+ f.id
				+ "_"
				+ ad.btn.CLOSE[0].result
				+ '" href="javascript:void(0)" class="win-closebtn active" title="'
				+ ad.btn.CLOSE[0].title
				+ '"></a><div class="winbtn-rightedge"></div>';

		$(window.top.document).find('body').append(divCover());
		$('body').append(
				'<div id="' + $id + '">'
						+ MainAeroWindow(f.WinTitle, wbtn, DialogContent(f))
						+ '</div>');

		$('.showContent')
				.css(
						{
							width : $('.showContent').width() > ad.minWidth ? ad.maxWidth
									: ad.minWidth
						});
		var aid = $('#' + $id);
		var awin = aid.find('.AeroWindow');
		var dtop = ($(window).height()) / 2 - awin.height() / 2 - 20, dleft = ($(
				window).width() / 2)
				- awin.width() / 2;
		aid.css({
			width : awin.width(),
			height : awin.height(),
			position : 'absolute',
			'z-index' : 10001,
			top : '0px',
			left : dleft + 'px'
		});
		aid.animate({
			top : dtop
		}, {
			duration : 300
		});
		aid
				.draggable({
					cursor : "move",
					cancel : '.tb-mm-dialog',
					containment : $('html'),
					handle : $('#dragHelper'),
					helper : function(event) {
						return $('<div id="dragHelper" style="width:'
								+ awin.width()
								+ 'px; height:'
								+ awin.height()
								+ 'px; margin:0px; padding:0px;border: 2px dotted #3CF;border-radius:10px 10px 10px 10px;position:absolute;top:0px;background-color:#fff;opacity:0.3;filter:alpha(opacity=30);left:0px;z-index:10002;"></div>')
					},
					start : function() {
						awin.addClass('active');
					},
					stop : function() {
						aid.animate({
							top : $('#dragHelper').css('top'),
							left : $('#dragHelper').css('left')
						}, 300);
					}
				});
		aid.click(function() {
			awin.addClass('active');
		});
		callBack(f);
	}

	function AddDialogBtn(bo) {// 添加button按钮
		var cx = [];
		return $.each(bo.buttons, function($, d) {
			cx.push('<a class="dialogBtns d-' + d.result + '" id="', bo.id,
					"_", d.result, '" href="javascript:void(0)"><span> ',
					d.value, " </span></a>")
		}), cx.join("")
	}

	function DialogContent(aWin) {
		return [ '<div class="tb-mm-dialog">',
				'<table border="0" cellspacing="0" cellpadding="0">', '  <tr>',
				'	<td align="center"><div class="d-icons d-', aWin.type,
				'"></div></td>',/*
								 * <img width="64" height="64"
								 * src="images/icons/',aWin.type,'.png"
								 * border="0">
								 */
				'	<td><div class="showContent" style="margin:5px;"><p>',
				aWin.content, '</p></div></td>', '	</tr>', '	<tr>',
				'	  <td colspan="2">', AddDialogBtn(aWin), '	  </td>',
				'	</tr>', '</table>', '</div>' ].join('');
	}

	var ad = AeroDialog;

	// 用于回调事件处理
	ad.btn = {
		OK : [ {
			value : ad.Btntxt.OK,
			result : "ok"
		} ],
		NO : [ {
			value : ad.Btntxt.NO,
			result : "no"
		} ],
		YES : [ {
			value : ad.Btntxt.YES,
			result : "yes"
		} ],
		CANCEL : [ {
			value : ad.Btntxt.CANCEL,
			result : "cancel"
		} ],
		CLOSE : [ {
			title : ad.Btntxt.CLOSE,
			result : "close"
		} ]
	}, ad.btn.OKCANCEL = ad.btn.CANCEL.concat(ad.btn.OK),
			ad.btn.YESNO = ad.btn.NO.concat(ad.btn.YES),
			ad.btn.YESNOCANCEL = ad.btn.CANCEL.concat(ad.btn.NO).concat(
					ad.btn.YES),

			ad.close = function(winID) {
				// alert(divID);
				$('#' + winID).remove();// 移除对话框层
				$(window.top.document).find('#winDialogCover').remove();// 点击对话框任何按钮移除遮罩层
			}

	ad.alert = function(c, t, fn) {
		AeroDialogs(c, t, fn, 'alert');
	}, ad.html = function(c, t, fn) {
		AeroDialogs(c, t, fn, "html");
	}
	ad.confirm = function(c, t, fn) {
		AeroDialogs(c, t, fn, "confirm");
	}, ad.success = function(c, t, fn) {
		AeroDialogs(c, t, fn, "success");
	}, ad.warning = function(c, t, fn) {
		AeroDialogs(c, t, fn, "warning");
	}, ad.error = function(c, t, fn) {
		AeroDialogs(c, t, fn, "error");
	}, ad.question = function(c, t, fn) {
		AeroDialogs(c, t, fn, "question");
	}
})(jQuery);


// 提示框
function showAlert() {
	var id = arguments[0];// 窗口id
	var title = arguments[1]; // 窗口标题
	var content = arguments[2];// 提示内容
	var type = 0;// 0为提示框
	var icon = arguments[4];// 显示图标，包括warning,succeed,question,smile,sad,error
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

	var s = "<div id='"
			+ id
			+ "'><div class='message_white_content' style='width:360px; height:180px;'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"
			+ id
			+ "\").remove();'></a><div style='border:3px solid #CCCCCC;'><div class='message_white_iframe_del' style='width:352px; height:160px;'><h3 class='message_white_title'><span>"
			+ title
			+ "</span></h3><div class='message_white_box_del'><span class='message_white_img_"
			+ icon
			+ "'></span><span class='message_white_font' style='font-size:14px;'>"
			+ content
			+ "</span></div>   <div class='message_white_box1'><input id='sure' type='button' value='确定'/></div>    </div></div></div><div class='black_overlay'></div>";

	// 消息框
	jQuery("body").append(s);
	
/*	var top = jQuery(window).scrollTop()
			+ (jQuery(window).height() - jQuery(document).outerHeight()) / 2;
	jQuery(".message_white_content").css("margin-top",
			jQuery(window).scrollTop() + "px");
	var h = jQuery(document).height();*/
	//jQuery('.black_overlay').css("height", h);

	// 点击确定
	jQuery("#sure").click(function() {
		jQuery("#" + id).remove();
	});
}


//重写alert函数
window.alert = function(content){
	//AeroDialog.alert(t,'提示');
	showAlert("thealert","系统提示",content,0,"warning");
}

//window.confirm = function(c,t,f){
//	if(!t)
//		t='确认';
//	AeroDialog.confirm(c,t,f);
//}