jQuery(document).ready(function(){
  
 // alert(jQuery("#nav .shop_left_btn_layer").css("top",0));
 
 
  
  jQuery(".shop_left_btn_con li").mouseover(function(){
	var child_count = jQuery(this).attr("child_count");
	if(child_count>0){
	 var id=jQuery(this).attr("id");
     jQuery("#child_"+id).show();			
	}
  }).mouseleave(function(){
	 var child_count = jQuery(this).attr("child_count");
	if(child_count>0){ 
     var id=jQuery(this).attr("id");
     jQuery("#child_"+id).hide();
	}
  });
  jQuery(".specialde li").mouseover(function(){
     jQuery(this).find("i").show();
  }).mouseleave(function(){
     jQuery(this).find("i").hide();
  });
  jQuery(".productone ul").mouseover(function(){
    jQuery(".productone ul").removeClass("this");
	jQuery(this).addClass("this");
  }).mouseleave(function(){
    jQuery(".productone ul").removeClass("this");
  });
  jQuery(".floorul li").mouseover(function(){
	var store_gc=jQuery(this).attr("store_gc");
    jQuery(".floorul li[store_gc="+store_gc+"]").css("cursor","pointer").removeClass("this");
	jQuery(this).addClass("this");
    var id=jQuery(this).attr("id");
	jQuery(".ftab[store_gc="+store_gc+"]").hide();
	jQuery(".ftab[store_gc="+store_gc+"][id="+id+"]").show();
  });
  jQuery(".productone img").lazyload({effect:"fadeIn",width:147,height:147});
  jQuery(".rankimg img").lazyload({effect:"fadeIn",width:73,height:73});
 //
 jQuery(".index_sales_left>h3>ul>li").mouseover(function(){
    jQuery(".index_sales_left>h3>ul>li").removeClass("this");
	jQuery(this).addClass("this");
	jQuery(".index_sales_box>[class^=index_sales_]").hide();
	var div_index=jQuery(this).attr("div_index");
	jQuery(".index_sales_"+div_index).show();
 });
 //
  jQuery(".conti").jCarouselLite({
	 btnNext: "#advert_left",
	  btnPrev: "#advert_right", 
	 auto: 5000,
	 speed: 2000,
	 visible:1
  });
  //
  
  
  jQuery("#toTop .close").hide();
  
  
});

function toTop(){
	$("html, body, .main").animate({scrollTop: 0}, 300); 
};