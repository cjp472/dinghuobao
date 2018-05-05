$(function(){
	

	// 楼层轮播图
	if ($('.floor-focus')) {

		$.each($('.floor-focus'), function(i, val) {
			var sWidth = $(val).width();
			var len = $(val).find("ul li").length; // 获取焦点图个数
			var index = 0;
			var picTimer;
			// 以下代码添加数字按钮和按钮后的半透明条，还有上一页、下一页两个按钮
			var btn = "<div class='focus-btn'>";

			for (var i = 0; i < len; i++) {
				btn += "<span></span>";
			}
			btn += "</div>";
			$(val).append(btn);
			$(val).find(".btnBg").css("opacity", 0.5);

			// 为小按钮添加鼠标滑入事件，以显示相应的内容
			$(val).find(".focus-btn span").css("opacity", 0.3).mouseover(function() {
				index = $(val).find(".focus-btn span").index(this);
				showPics(index);
			}).eq(0).trigger("mouseover");

			// 本例为左右滚动，即所有li元素都是在同一排向左浮动，所以这里需要计算出外围ul元素的宽度
			$(val).find("ul").css("width", sWidth * (len));

			// 鼠标滑上焦点图时停止自动播放，滑出时开始自动播放
			$(val).hover(function() {
				clearInterval(picTimer);
			}, function() {
				picTimer = setInterval(function() {
					showPics(index);
					index++;
					if (index == len) {
						index = 0;
					}
				}, 3000); // 此4000代表自动播放的间隔，单位：毫秒
			}).trigger("mouseleave");

			// 显示图片函数，根据接收的index值显示相应的内容
			function showPics(index) { // 普通切换
				var nowLeft = -index * sWidth; // 根据index值计算ul元素的left值
				nowLeft="0";
				$(val).find("ul").stop(true, false).animate({
					"left": nowLeft
				}, 300);
				$(val).find(".focus-btn span").stop(true, false).animate({
					"opacity": "0.3"
				}, 300).eq(index).stop(true, false).animate({
					"opacity": "0.7"
				}, 300); // 为当前的按钮切换到选中的效果
			}
		});
	}
	
if($(".floor-list")){
		
		var elevatorfloor = $(".elevator-floor");
		$.each($('.floor-list'),function(i,v){
			var fnum = $.trim($(v).find('.floor-name').text());
			var short_name = $.trim($(v).find('.floor-title input').val());
			if(short_name == '') short_name = fnum;
			var $el = $("<a class='smooth' href='javascript:;'><b class='fs'>"+fnum+"</b><em class='fs-name' title='"+short_name+"'>"+short_name+"</em></a>")
			var $i = $("<i class='fs-line'></i>");
			if(i< $('.floor-list').length-1){
				$el.append($i);
			}
			elevatorfloor.append($el);
		});		
	
		var conTop = 0;
		if($(".floor-list").length>0){
			conTop = $(".floor-list").offset().top;
		}
		$(window).scroll(function() {
			var scrt = $(window).scrollTop();
			if (scrt > conTop) {
				
				$(".elevator").show("fast", function() {
					$(".elevator-floor").css({
						
						"-webkit-transform": "scale(1)",
						"-moz-transform": "scale(1)",
						"transform": "scale(1)",
						"opacity": "1"
					})
				}).css({
					"visibility": "visible"
				})
			} else {
				$(".elevator-floor").css({
					"-webkit-transform": "scale(1.2)",
					"-moz-transform": "scale(1.2)",
					"transform": "scale(1.2)",
					"opacity": "0"
				});
				$(".elevator").css({
					"visibility": "hidden"
				})
			}
			setTab()
		});

		var arr = [],
			fsOffset = 0;
		for (var i = 1; i < $(".floor").length; i++) {
			arr.push(parseInt($(".floor").eq(i).offset().top) + 30)
		}
		$(".elevator-floor a.smooth").click(  function() {
			var _th = $(this);
			_th.blur();
			var index = $(".elevator-floor a.smooth").index(this);
			if (index > 0) {
				fsOffset = 50
			}
			var hh = arr[index];
			$("html,body").stop().animate({
				scrollTop: hh - fsOffset + "px"
			}, 400)
		});
		$(".elevator-floor a.fsbacktotop").click(function() {
			$("html,body").stop().animate({
				scrollTop: 0
			}, 400)
		});

	function setTab() {
		var Objs = $(".floor:gt(0)");
		var textSt = $(window).scrollTop();
		for (var i = Objs.length-1; i >= 0; i--) {
			if (textSt >= $(Objs[i]).offset().top - $(Objs[i-1]).height()/2) {
					$(".elevator-floor a").eq(i).addClass("active").siblings().removeClass("active");
					return
				}
			}
		}
	}


if ($(".floor-tabs-nav > li")) {
	// 首页楼层Tab标签卡滑门切换
	$(".floor-tabs-nav > li").bind('mouseover', (function(e) {
		var color = $(this).parents(".floor").attr("color");
		$(this).addClass('floor-tabs-selected').siblings().removeClass('floor-tabs-selected');
		$(this).find('h3').css({
			'border-color': color + ' ' + color + ' #fff',
			'color': color
		}).parents('li').siblings('li').find('h3').css({
			'border-color': '',
			'color': ''
		});
		$(this).parents('.floor-con').find('.floor-tabs-panel').eq($(this).index()).removeClass('floor-tabs-hide').siblings().addClass('floor-tabs-hide');
	}));
}
});

 
