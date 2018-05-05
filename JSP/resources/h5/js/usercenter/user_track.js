/**
 * 我的商城-浏览记录  h5 js
 */



var currentPage=2;
function initPage(){
	if($(".loading").is(":hidden")){
		$(".loading").show();
	}
	$.ajax({
			type:'post',
			url:domainURL+"/mobile/buyer/track_goods_ajax.htm",
			data:{"currentPage":currentPage},
			dataType:'html',
			success:function(result){
				if(result!=null&&result!=""){
					currentPage++;
					$("#track_goods_list").append(result);
				}else{
					$(".loading").html("- - - - - - - - - - 我是有底线的 - - - - - - - - - -");
				}
				
			}
		})
}

$(function(){
	 
    $(window).scroll(function(){
        if(($(window).scrollTop() + $(window).height() > $(document).height()-1)){
            initPage();
        }
    });
})

