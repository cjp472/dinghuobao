/**
 * 个人中心订单相关
 */
function remove_order(id,currentPage){
	layer.open({
	    content: '取消订单将会返回对应的积分，是否继续?'
	    ,btn: ['确定', '取消']
	    ,yes: function(index){
	      layer.close(index);
	      window.location.href=domainURL+'/mobile/buyer/integral_order_cancel.htm?id='+id+'&currentPage='+currentPage;
	    }
	  });
}
function toorderList(orderStatus){
	
	if(orderStatus!="order_all"){
		window.location.href=domainURL+'/mobile/buyer/integral_order_list.htm?order_status='+orderStatus;
	}else{
		window.location.href=domainURL+'/mobile/buyer/integral_order_list.htm';
	}

}

var currentPage=2;
function initPage(){
	if($(".loading").is(":hidden")){
		$(".loading").show();
	}
	$.ajax({
			type:'post',
			url:domainURL+"/mobile/buyer/integral_order_list_ajax.htm",
			data:{"currentPage":currentPage,"order_status":orderStatus},
			dataType:'html',
			success:function(result){
				if(result!=null&&result!=""){
					currentPage++;
					$("#order-list").append(result);
				}else{
					$(".loading").html("- - - - - - - - - - 我是有底线的 - - - - - - - - - -")
				}
				
			}
		})
}

$(function(){
	
    if(orderStatus!=null&&orderStatus!=""){
    	$("#"+orderStatus).addClass("selected")
    }else{
    	$("#order_all").addClass("selected")
    }
    
    $(window).scroll(function(){
        if(($(window).scrollTop() + $(window).height() > $(document).height()-1)){
            initPage();
        }
    });
})

