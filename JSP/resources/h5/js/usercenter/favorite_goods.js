/**
 * 我的商城-商品收藏  h5 js
 */

function removeGoods(mulitId){
	
layer.open({
    content: '您确定要取消收藏该商品吗？'
    ,btn: ['确定', '取消']
    ,yes: function(index){
      layer.close(index);
   
      var url = domainURL+"/mobile/buyer/favorite_del.htm?mulitId="+mulitId+"&type=0";
      window.location.href = url;
    }
  });
}

var currentPage=2;
function initPage(){
	if($(".loading").is(":hidden")){
		$(".loading").show();
	}
	$.ajax({
			type:'post',
			url:domainURL+"/mobile/buyer/favorite_goods_ajax.htm",
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

