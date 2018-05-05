$(function() {
	if($('#nav li').length>0){
	// 导航滑动效果
		$('#nav .wrap-line').css({
			'left':$('#nav li').eq(0).position().left,'width':$('#nav li').eq(0).outerWidth()
			});
		$('#nav li').hover(function(){
			$('#nav .wrap-line').stop().animate({
				left:$(this).position().left,width:$(this).outerWidth()
				});	
		},function(){
			$('#nav .wrap-line').stop().animate({
				left:$('#nav li').eq(0).position().left,width:$('#nav li').eq(0).outerWidth()
				});	
		});
	}
	// 首页Tab标签卡滑门切换
	if ($(".tabs-nav > li > h3")) {
		$(".tabs-nav > li > h3").bind('mouseover', (function(e) {

			if (e.target == this) {
				var tabs = $(this).parent().parent().children("li");
				var panels = $(this).parent().parent().parent().children(".tabs-panel");
				var index = $.inArray(this, $(this).parent().parent().find("h3"));
				if (panels.eq(index)[0]) {
					tabs.removeClass("tabs-selected").eq(index).addClass("tabs-selected");
					var color = $(this).parents(".floor:first").attr("color");
					$(this).parents(".tabs-nav").find("h3").css({
						"border-color": "",
						"color": ""
					});
					$(this).css({
						"border-color": color + " " + color + " #fff",
						"color": color
					});
					panels.addClass("tabs-hide").eq(index).removeClass("tabs-hide");
				}
			}
		}));
	}

  

});

 