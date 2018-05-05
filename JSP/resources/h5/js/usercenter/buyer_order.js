/**
 * 个人中心订单相关
 */
function remove_order(id,currentPage){
	layer.open({
	    content: '您确定要删除该订单吗？'
	    ,btn: ['确定', '取消']
	    ,yes: function(index){
	      layer.close(index);
	      window.location.href=domainURL+'/mobile/buyer/order_delete.htm?id='+id+'&currentPage='+currentPage;
	    }
	  });
}
function toorderList(orderStatus){
	
	if(orderStatus!="order_all"){
		window.location.href=domainURL+'/mobile/buyer/order.htm?order_status='+orderStatus;
	}else{
		window.location.href=domainURL+'/mobile/buyer/order.htm';
	}

}

var currentPage=2;
function initPage(){
	if($(".loading").is(":hidden")){
		$(".loading").show();
	}
	$.ajax({
			type:'post',
			url:domainURL+"/mobile/buyer/order_ajax.htm",
			data:{"currentPage":currentPage,"order_status":orderStatus},
			dataType:'html',
			success:function(result){
				if(result!=null&&result!=""){
					currentPage++;
					$("#order-list").append(result);
				}else{
					$(".loading").html("- - - - - - - - - - 我是有底线的 - - - - - - - - - -");
				}
				
			}
		})
}

$(function(){
	  // 右上侧小导航控件
    $('#header').on('click', '#header-nav', function(){
        if ($('.nctouch-nav-layout').hasClass('show')) {
            $('.nctouch-nav-layout').removeClass('show');
        } else {
            $('.nctouch-nav-layout').addClass('show');
        }
    });
    $('#header').on('click', '.nctouch-nav-layout',function(){
        $('.nctouch-nav-layout').removeClass('show');
    });
    $(document).scroll(function(){
        $('.nctouch-nav-layout').removeClass('show');
    });
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

