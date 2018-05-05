var myScroll = new IScroll('#wrapper', {
		scrollbars: false,
		mouseWheel: true,
		interactiveScrollbars: true,
		shrinkScrollbars: 'scale',
		click: true,
		fadeScrollbars: true
});
var child_load=0;
function show_children_class(id,arg){
	var status = jQuery(arg).attr('status');
	jQuery("#fenlei ul li").removeClass("this");
	jQuery(arg).parent().addClass("this");
	if(status=='0'&&child_load==0){
		child_load==1;
		 jQuery("ul[id^=pulli_]").hide();
		 jQuery("#pulli_"+id).show();
		   child_load==0;
		    jQuery(arg).attr('status','1');
			myScroll.refresh();
	}else if(status=='1'){
		jQuery("ul[id^=pulli_]").hide();
 		 jQuery("#pulli_"+id).show();
		 myScroll.refresh();
	}
}
var myScroll2 = new IScroll('#wrapper2', {
		scrollbars: false,
		mouseWheel: true,
		interactiveScrollbars: true,
		shrinkScrollbars: 'scale',
		click: true,
		fadeScrollbars: true
});