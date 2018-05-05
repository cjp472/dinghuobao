$(function(){
	$(".part-list-item").mousemove(function(){
		
		$(this).find(".part_hover").show();
		$(this).find("a").css("border-color","#fc0d1b")

	}).mouseleave(function(){
		
		$(this).find(".part_hover").hide();
		$(this).find("a").css("border-color","#e8e8e8")
	});
	
	
	
	$(".shop_carun").click(function(){
		window.location.href=storePath+"/user/login.htm";
	});	
	$(".shopping_carun").click(function(){
		window.location.href=storePath+"/user/login.htm";
	});
	
	
	$(".shopping_car").click(function(){
		 var id=$(this).attr("data-id");
		 $(".specification").html("");
		 $.ajax({ 
	            type : "POST", 
	            url  : storePath+"/store_goods_cart_ajax.htm",  
	            cache : false, 
	            dataType:"html",
	            data : {"id":id}, 
	            success : function(data){
	            	$(".specification").html(data);
	            	$(".specification").show();
	            	$(".specifica_con").addClass("order_specificationa");
	            	
	            	$(".modal-close").click(function(){
	            		$(".specification").hide();
	            	});	
	            	$("#store_goods_cancelbtn").click(function(){
	            		$(".specification").hide();
	            	})
	            	$("#store_goods_savebtn").click(function(){
	            		 $.ajax({ 
	         	            type : "POST", 
	         	            url  : storePath+"/add_goods_cartAll.htm",  
	         	            cache : false, 
	         	            dataType:"json",
	         	            data : $("#addcartAll").serialize(), 
	         	            success : function(data){
	         	            	if(data.count!="0"){
	         	            		//更新右侧购物车信息
	         	            		jQuery("#cart_goods_count_top_tool").html("("+data.count+")");
	         	            		qikoo.dialog.alert("已成功加入购物车");
	         	            		$(".specification").hide();
		         	  			}else{
		         	  				qikoo.dialog.alert("至少购买一件商品!");
		         	  			}
	         	            }, 
	         	            error : function(data){
	         	            	
	         	            }
	         	        });
	            	})
	            }, 
	            error : function(data){
	            	
	            }
	        });
		
	});
})

function addNum(id,type){
	var count=jQuery("#num_"+type+"_"+id).val();
//	var store_id=jQuery(this).attr("store_id");
	var count=jQuery("#num_"+type+"_"+id).val().replace(/\D/g,'');
	if(count==""||count<0){
		count=0;
	}
	  count++;
	  jQuery("#num_"+type+"_"+id).val(count);
	  querySinglePrice(id,type,count)
}

function jianNum(id,type){
	var count=jQuery("#num_"+type+"_"+id).val();
//	var store_id=jQuery(this).attr("store_id");
	var count=jQuery("#num_"+type+"_"+id).val().replace(/\D/g,'');
	if(count==""){
		count=1;
	}
	if(count>1){
		  count--;
		  jQuery("#num_"+type+"_"+id).val(count);
		  querySinglePrice(id,type,count);
	}else{
		jQuery("#num_"+type+"_"+id).val(1);
		querySinglePrice(id,type,1);
	}
}
function blurNum(id,type){
	var count=jQuery("#num_"+type+"_"+id).val().replace(/\D/g,'');
	jQuery("#num_"+type+"_"+id).val(count);
	if(count!=""){
		querySinglePrice(id,type,count);
	}
}

//切换规格
function chooseSpec(id){
	$(".divCon").hide();
	$("#divCon_"+id).show();
	$("#tabspec_"+id).siblings().removeClass("selected");
	$("#tabspec_"+id).addClass("selected");
}
//多规格
function blurItemNum(id){
	var count=jQuery("#itemnum_"+id).val();
	var count=jQuery("#itemnum_"+id).val().replace(/\D/g,'');
	var inventory=jQuery("#itemnum_"+id).attr("data-inventory");
	if(count==""||count<0){
		count=0;
	}
	if((+count)>(+inventory)){
		qikoo.dialog.alert("订购商品数应小于库存量");
		jQuery("#itemnum_"+id).val("");
		return;
	}
	if(count>0){
		jQuery("#itemnum_"+id).val(count);
		querymulitPrice(id,count);
	}else{
		jQuery("#itemnum_"+id).val("");
	}
	
	
}

function addItemNum(id){
	var count=jQuery("#itemnum_"+id).val();
	var count=jQuery("#itemnum_"+id).val().replace(/\D/g,'');
	if(count==""||count<0){
		count=0;
	}
	var inventory=jQuery("#itemnum_"+id).attr("data-inventory");
	if((+count)>=(+inventory)){
		qikoo.dialog.alert("订购商品数应小于库存量");
		return;
	}
	  count++;
	  jQuery("#itemnum_"+id).val(count);
	  querymulitPrice(id,count);
}

function jianItemNum(id){
	var count=jQuery("#itemnum_"+id).val();
	var count=jQuery("#itemnum_"+id).val().replace(/\D/g,'');
	if(count==""){
		count=1;
	}
	var inventory=jQuery("#itemnum_"+id).attr("data-inventory");
	if((+count)>(+inventory)){
		qikoo.dialog.alert("订购商品数应小于库存量");
		jQuery("#itemnum_"+id).val("");
		return;
	}
	if(count>1){
		  count--;
		  jQuery("#itemnum_"+id).val(count);
		  querymulitPrice(id,count);
	}else{
		jQuery("#itemnum_"+id).val(1);
		querymulitPrice(id,1);
	}
}
//查询显示的价格
function querymulitPrice(id,count){
	$.ajax({ 
        type : "POST", 
        url  : storePath+"/queryMulitPrice.htm",  
        cache : false, 
        dataType:"json",
        data : {"id":id,"count":count}, 
        success : function(data){
        	if(data.price!=null&&data.price>0){
        		$("#price_"+id).html(data.price+" /"+$("#price_"+id).attr("data-units"));
        	}else{
        		$("#price_"+id).html($("#price_"+id).attr("data-price")+" /"+$("#price_"+id).attr("data-units"));
        	}
        }, 
        error : function(data){
        	
        }
    });
}
	function querySinglePrice(id,type,count){
		$.ajax({ 
	        type : "POST", 
	        url  : storePath+"/querySinglePrice.htm",  
	        cache : false, 
	        dataType:"json",
	        data : {"id":id,"count":count}, 
	        success : function(data){
	        	if(data.price!=null&&data.price>0){
	        		$("#price_"+type+"_"+id).html("￥"+data.price);
	        	}else{
	        		$("#price_"+id).html("￥"+$("#price_"+id).attr("data-price"));
	        	}
	        }, 
	        error : function(data){
	        	
	        }
	    });
}