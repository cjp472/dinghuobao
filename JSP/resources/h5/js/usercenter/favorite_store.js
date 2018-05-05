/**
 * 我的商城-商品收藏  h5 js
 */

function removeStore(mulitId){
	var url = domainURL+"/mobile/buyer/favorite_del.htm?mulitId="+mulitId+"&type=1";
window.location.href = url;
}

var currentPage=2;
function initPage(){
	if($(".loading").is(":hidden")){
		$(".loading").show();
	}
	$.ajax({
			type:'post',
			url:domainURL+"/mobile/buyer/favorite_store_ajax.htm",
			data:{"currentPage":currentPage},
			dataType:'html',
			success:function(result){
				if(result!=null&&result!=""){
					currentPage++;
					$("#favorites_list").append(result);
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

