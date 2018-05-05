var currentPage=2;
$(function(){
	$("#header").on('click', '.header-inp', function(){
        $("#keyForm").submit();
    });
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
    
    //排序
    $("#nav_ul li a").click(function(){
    	var orderby=$(this).attr("id");
    	$("#"+orderby).parent().siblings().find("a").removeClass("current");
    	$("#"+orderby).addClass("current");
    	if(orderby=="default"){
    		orderby="";
    	}
    	$("#orderBy").val(orderby);
    	var orderType=$("#orderType").val();
    	if(orderType==null||orderType==""||orderby==""){
    		orderType="desc";
    	}else{
    		if(orderType=="desc"){
    			orderType="asc";
    		}else{
    			orderType="desc";
    		}
    	}
    	$("#orderType").val(orderType);
    	$.ajax({
            type:'post',
            url:domainURL+"/mobile/search_list_ajax.htm?currentPage=1", 
            data:$("#orderByForm").serialize(),
            dataType:'html',
            async:false,
            success:function(result){
            	currentPage=2;
               $("#list_ul").html(result);
               $(".loading").html('<div class="spinner"><i></i></div>数据读取中...');
            }
        });
    })
    
    $(window).scroll(function(){
        if(($(window).scrollTop() + $(window).height() > $(document).height()-1)){
            initPage();
        }
    });
})


function initPage(){
	if($(".loading").is(":hidden")){
		$(".loading").show();
	}
	$.ajax({
			type:'post',
	        url:domainURL+"/mobile/search_list_ajax.htm?currentPage="+currentPage, 
	        data:$("#orderByForm").serialize(),
	        dataType:'html',
			success:function(result){
				if(result!=null&&result!=""){
					currentPage++;
					$("#list_ul").append(result);
				}else{
					$(".loading").html("- - - - - - - - - - 我是有底线的 - - - - - - - - - -");
				}
				
			}
		})
}
