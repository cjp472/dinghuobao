$(function(){
	$('#list-address-valve').click(function(){
        $.ajax({
            type:'post',
            url:domainURL+"/mobile/buyer/cart_address_ajax.htm?addr_id="+$("#addr_id").val(), 
            dataType:'html',
            async:false,
            success:function(result){
                $("#list-address-add-list-ul").html(result);
                $("#list-address-wrapper").show();
                $("#mainlay").hide();
                amitleft("list-address-wrapper","",'');
             
            }
        });
    });
	
	
	
      //新增地址
      $("#new-address-valve").click(function(){
    	  $('#addForm')[0].reset(); 
    	  amitleft("new-address-wrapper","","list-address-wrapper");
      });
      
      $("#invContent").click(function(){
    	  amitleft("invoice-wrapper","",'');
      });
      // 发票类型选择
      $('input[name="invoiceType"]').click(function(){
          if ($(this).val() == '0') {
        	  $("#invoice").val("");
              $('#inv-title-li').hide();
              $("#person").parent().addClass("checked");
              $("#company").parent().removeClass("checked");
            
          } else {
              $('#inv-title-li').show();
              $("#company").parent().addClass("checked");
              $("#person").parent().removeClass("checked");
          }
      });
      //组合商品
      $(".baby_gp>a").mouseover(function(){
    		$(this).parent().find(".arrow").show();									  
    	    $(this).parent().find(".baby_group").show();
    	  });
    	  $(".baby_gp").mouseleave(function(){
    	    $(this).parent().find(".arrow").hide();									  
    	    $(this).parent().find(".baby_group").hide();
    	  });
    	  
    	  //选择优惠券
    	  $('#useRPT').click(function(){
              if ($(this).prop('checked')) {
                  $(this).parent().addClass("checked");
                  $("#rptInfo").show();
              } else {
                  $(this).parent().removeClass("checked");
                  $("#rptInfo").hide();
              }
            
            //  $('#totalPrice,#onlineTotal').html(total_price.toFixed(2));
          });
    	  $("input[name='coupon_id']").click(function(){
    		  var coupon_amount=parseFloat(jQuery(this).attr("coupon_amount"));
    		  if(isNaN(coupon_amount)||coupon_amount<0){
    			 coupon_amount=0;
    		  }
    		  $("#order_coupon_show").html(coupon_amount.toFixed(2));
    		  var ship_price = parseFloat(jQuery("#ship_price").val());
    		  if(isNaN(ship_price)){
    			  ship_price = 0; 
    		  }
    		  $("#ship_price_show").text(ship_price.toFixed(2))
    		  var goods_amount = parseFloat(jQuery("#goods_amount").val());
    		  var order_fee = parseFloat(goods_amount-coupon_amount+ship_price);
    		  jQuery("#order_store_amount").html(order_fee.toFixed(2));
    		  $("#totalPrice").html(order_fee.toFixed(2));
    	  })
    	  
    	  //回显费
    	  choose_ship_price();
    	  
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
	
})
//生成订单
function saveOrder(){
	var val = $('#addr_id').val();
	
	if(val == null || val== "" || val == undefined){
		layer.open({
		    content: '请选择收货地址！'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		 });
	}else{
		
		$("#cart_form").submit();
	}
		
}
//选择物流配送方式
function choose_ship_price(){
	 var coupon_amount=parseFloat($("input[name='coupon_id']:checked").attr("coupon_amount"));
	  if(isNaN(coupon_amount)||coupon_amount<0){
		 coupon_amount=0;
	  }
	 var ship_price = parseFloat(jQuery("#ship_price").val());
	  if(isNaN(ship_price)){
		  ship_price = 0; 
	  }
	  $("#order_coupon_show").html(coupon_amount.toFixed(2));
 	  $("#ship_price_show").text(ship_price.toFixed(2))
	  var goods_amount = parseFloat(jQuery("#goods_amount").val());
	  var order_fee = parseFloat(goods_amount-coupon_amount+ship_price);
	  jQuery("#order_store_amount").html(order_fee.toFixed(2));
	  $("#totalPrice").html(order_fee.toFixed(2));
	
}

//选择发票类型
function chooseInvoiceType(){
	
	 var v1=$('input[name="invoiceType"]:checked').val();
	 if(v1==0){
		 $("#invContent").text("个人");
	 }else if(v1==1){
		 $("#invContent").text("单位");
	 }
	 $("#invoice-wrapper").addClass('right').removeClass('left');
}

//地区选择
function cart_chooseAddr(obj){
	  // 地区选择
       $("#addr_id").val($("#addrId_"+obj).val());
       $("#true_name").text($("#trueName_"+obj).text());
       $("#mob_phone").text($("#mobile_"+obj).text());
       $("#address").text($("#area_"+obj).text());
       $("#list-address-wrapper").addClass('right').removeClass('left');
       $("#list-address-wrapper").hide();
       $("#mainlay").show();
       
       
}
function amitleft(wraper,scroll,parentwraper){
	  $("#"+wraper).removeClass('hide').removeClass('right').addClass('left');
	  if(parentwraper!=null&&parentwraper!=""){
		  $("#"+parentwraper).hide();//地址列表隐藏
	  }
	  if(scroll!=null&&scroll!=''){
    	 if (typeof(myScrollArea) == 'undefined') {
             if (typeof(IScroll) == 'undefined') {
                 $.ajax({
                     url: webPathURL+'/resources/h5/js/v4/iscroll.js',
                     dataType: "script",
                     async: false
                   });
             }
             myScrollArea = new IScroll(scroll, { mouseWheel: true, click: true });
         } else {
             myScrollArea.destroy();
             myScrollArea = new IScroll(scroll, { mouseWheel: true, click: true });
         }
	  }
    	 //头部返回
     $("#"+wraper).on('click', '.header-l > a', function(){
    	if(parentwraper!=null&&parentwraper!=""){
   		  $("#"+parentwraper).show();//地址列表隐藏
   	  	}
    	$("#"+wraper).addClass('right').removeClass('left');
    	if(wraper=="list-address-wrapper"){
    		$("#list-address-wrapper").hide();
    		$("#mainlay").show();
    	}
     });
}

//选择地区
var addrinfo='';
function chooseAddr(){
	
	$('#areaSelected').find('#filtrate_ul').find('li').eq(0).addClass('selected').siblings().removeClass('selected');
	$('#areaSelected').find(".nctouch-default-list").html($("#hidepro").html());
	$('#areaSelected').show();
	$('#areaSelected').find('.nctouch-full-mask').addClass('left').removeClass('right');
	addrinfo='';
	 if (typeof(myScrollArea) == 'undefined') {
         if (typeof(IScroll) == 'undefined') {
             $.ajax({
                 url: webPathURL+'/resources/h5/js/v4/iscroll.js',
                 dataType: "script",
                 async: false
               });
         }
         myScrollArea = new IScroll('#areaSelected .nctouch-main-layout-a', { mouseWheel: true, click: true });
     } else {
         myScrollArea.destroy();
         myScrollArea = new IScroll('#areaSelected .nctouch-main-layout-a', { mouseWheel: true, click: true });
     }
}

function selectArea(obj,mark){
	var id=$(obj).attr("attrId");
	
	if(mark=="province"||mark=="city"){
		addrinfo+=$(obj).text();
		$.post(
				domainURL+"/mobile/buyer/account_getAreaChilds.htm",
				{
				"parent_id":id,
				"areaMark":mark
				},
				function(data){
					if(data==null||data.trim()==""){
						$("#area_id").val(id);
						$("#areashow").val(addrinfo);
						 $('#areaSelected').find('.nctouch-full-mask').addClass('right').removeClass('left');
					}else{
						if(mark=="province"){
							 $('#areaSelected').find('#filtrate_ul').find('li').eq(1).addClass('selected').siblings().removeClass('selected');
						}
						if(mark=="city"){
							 $('#areaSelected').find('#filtrate_ul').find('li').eq(2).addClass('selected').siblings().removeClass('selected');
						}
						$('#areaSelected').find(".nctouch-default-list").html(data);
			            if (typeof(myScrollArea) == 'undefined') {
			                if (typeof(IScroll) == 'undefined') {
			                    $.ajax({
			                        url: webPathURL+'/resources/h5/js/v4/iscroll.js',
			                        dataType: "script",
			                        async: false
			                      });
			                }
			                myScrollArea = new IScroll('#areaSelected .nctouch-main-layout-a', { mouseWheel: true, click: true });
			            } else {
			                myScrollArea.destroy();
			                myScrollArea = new IScroll('#areaSelected .nctouch-main-layout-a', { mouseWheel: true, click: true });
			            }
			           
					}
					
				},"text");
	}else{
		$("#area_id").val(id);
		$("#areashow").val(addrinfo);
		$('#areaSelected').find('.nctouch-full-mask').addClass('right').removeClass('left');
	}
}

//保存地址
function checkcarAddress(){
	var trueName = document.getElementById("trueName").value;
	var area_info = document.getElementById("area_info").value;
	var mobile = document.getElementById("mobile").value;
	

	if (trueName.length == 0) {
		//提示
		  layer.open({
		    content: '姓名不能为空!'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		  });
		return false;
	}

	if ($("#areashow").val() == "" || $("#areashow").val() == null) {
			 layer.open({
				    content: '地址不能为空!'
				    ,skin: 'msg'
				    ,time: 2 //2秒后自动关闭
				  });
			return false;
		
	}

	if (area_info.length == 0) {
		 layer.open({
			    content: '详细地址不能为空!'
			    ,skin: 'msg'
			    ,time: 2 //2秒后自动关闭
			  });
		return false;
	}

	if (mobile.length == 0) {
		layer.open({
		    content: '联系手机不能为空!'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		  });
		return false;
	} else if (mobile.length > 0) {
		mobilee(mobile);
	}
}
function _close(){
	$('#areaSelected').find('.nctouch-full-mask').addClass('right').removeClass('left');
}
function mobilee(mobile) {
	var telephone = document.getElementById("telephone").value;
	
	var myreg = /^((1[0-9]{2})+\d{8})$/;
	if (!myreg.test(mobile)) {
		layer.open({
		    content: '请输入有效的手机号码！'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		  });
		return false;
	}

	 
	if(telephone.length>0){
		var telephoneRule = /^[0-9]+$/;
		if (!telephoneRule.test(telephone)) {
			layer.open({
			    content: '只能填入数字！'
			    ,skin: 'msg'
			    ,time: 2 //2秒后自动关闭
			  });
			return false;
		}
	}

	var submitLayer = layer.open({
	    type: 2
	    ,content: '提交中'
	    ,shadeClose: false // 不允许点击遮罩关闭
	  });
	 
	//异步提交表单
	$.ajax({
			url:domainURL+"/mobile/cart_address_save.htm",
			async:false,
			data:$("#addForm").serialize(),
			type:"post",
			dataType:"json",
			success:function(data){
				//重新加载列表
				 $.ajax({
			            type:'post',
			            url:domainURL+"/mobile/buyer/cart_address_ajax.htm?addr_id="+$("#addr_id").val(), 
			            dataType:'html',
			            async:false,
			            success:function(result){
			                $("#list-address-add-list-ul").html(result);
			                $("#list-address-wrapper").show();
			                $("#new-address-wrapper").removeClass('left').addClass('right');
			                
			                layer.close(submitLayer); 
			               
			            }
			        });
				 
				}
			});
			 
}
