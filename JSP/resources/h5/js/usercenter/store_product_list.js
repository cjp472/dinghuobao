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
    
   
    
    $(window).scroll(function(){
        if(($(window).scrollTop() + $(window).height() > $(document).height()-1)){
            initPage();
        }
    });
    
    initCartCount();
})


function initPage(){
	if($(".loading").is(":hidden")){
		$(".loading").show();
	}
	$.ajax({
			type:'post',
	        url:domainURL+"/mobile/store_search_list_ajax.htm?currentPage="+currentPage, 
	        data:$("#orderByForm").serialize(),
	        dataType:'html',
			success:function(result){
				if(result!=null&&result.trim()!=""){
					currentPage++;
					$("#list_ul").append(result);
					//实时更新购物车数量
					initCartCount();
				}else{
					$(".loading").html("- - - - - - - - - - 我是有底线的 - - - - - - - - - -");
				}
				
			}
		})
}
//初始换购物车数量及信息显示
var cartShow=new Array();
function initCartCount(){
	$.ajax({
		type:'post',
        url:domainURL+"/mobile/init_cartCount.htm", 
        dataType:'json',
		success:function(data){
			if(data.count!="0"){
	       			$("#good_class_count").html(data.count);
	       			$("#goods_total_price").html("<span>￥</span>"+data.total_price.toFixed(2));
	       			$("#goods_content").html("共 "+data.count+" 种商品 "+data.goods_total_count+" 件")
	       	/*	if(data.cartList!=null){
	       			cartShow=data.cartList;
	       			if(cartShow.length>0){
	       				for(var i=0;i<cartShow.length;i++){
	       					if(cartShow[i].indexOf(";")>0){
	       						var v1=cartShow[i].split(";");
		       					jQuery("#single_member_"+v1[0]).text(v1[1]);
	       					}
	       					
	       				}
	       			 
	       			}
	       		}*/
	       	}
			
		}
	})
}

function initMulitCartShow(id){
	$.ajax({
		type:'post',
        url:domainURL+"/mobile/init_mulitCountShow.htm", 
        data:{"goodsId":id},
        dataType:'json',
		success:function(data){
			
	       		if(data.cartlist!=null){
	       			var multcartShow=data.cartlist;
	       			if(multcartShow.length>0){
	       				for(var i=0;i<multcartShow.length;i++){
	       					if(multcartShow[i].indexOf(";")>0){
	       						var v1=multcartShow[i].split(";");
		       					jQuery("#itemnum_"+v1[0]).val(v1[1]);
	       					}
	       					
	       				}
	       			}
	       		}
		}
	})
}



//单规格加数量
function addNum(id){
	var count=jQuery("#single_member_"+id).text();
//	var store_id=jQuery(this).attr("store_id");
	var count=jQuery("#single_member_"+id).text().replace(/\D/g,'');
	if(count==""||count<0){
		count=0;
	}
	  count++;
	  var goods_inventory=$("#single_member_"+id).attr("data-inventory");
	  if(count>(+goods_inventory)){
		  layer.open({
			    content: '购买量不能超过库存量！'
			    ,skin: 'msg'
			    ,time: 2 //2秒后自动关闭
			});
	  }else{
		  jQuery("#single_member_"+id).text(count);
		  update_inventory(id,count);
	  }
	  
	
}
//单规格减数量
function jianNum(id){
	var count=jQuery("#single_member_"+id).text();
	var count=jQuery("#single_member_"+id).text().replace(/\D/g,'');
	if(count==""){
		count=1;
	}
	if(count>1){
		  count--;
		  jQuery("#single_member_"+id).text(count);
		  update_inventory(id,count);
	}else{
		jQuery("#single_member_"+id).text(1);
	}
}
function tologin(){
	window.location.href=domainURL+"/mobile/user/login.htm";
}
//单规格加入购物车更新库存
function update_inventory(cart_id,count){
	
	//更新数量
	jQuery.post(domainURL+"/mobile/add_goods_cart.htm",{"id":cart_id,"count":count,"gsp":""},function(data){
		if(data.count!="0"){
			$("#good_class_count").html(data.count);
			$("#goods_total_price").html("<span>￥</span>"+data.total_price.toFixed(2));
			$("#goods_content").html("共 "+data.count+" 种商品 "+data.goods_total_count+" 件")
			
			if(data.price_show!=null&&data.price_show>0){
				$("#price_"+cart_id).html((data.price_show).toFixed(2));
			} 
		}else{
			layer.open({
			    content: '至少购买一件商品!'
			    ,skin: 'msg'
			    ,time: 2 //2秒后自动关闭
			});
		}
	},"json"); 
}
//单规格数量选择
function chooseSingleNum(id){
	$(".single_size").show();
	$(".order_specification").addClass("order_specificationa")
	var data_units= $("#single_member_"+id).attr("data-units");
	var data_inventory=$("#single_member_"+id).attr("data-inventory");
	$("#goods_inventory_show").html("1"+data_units+"起订 库存"+data_inventory);
	$("#goods_units_show").html("单位："+data_units);
	$("#singlebtn").attr("data-id",id);
}
//单规格加入购物车
function addSingleCart(){
	var id=$("#singlebtn").attr("data-id");
	var data_inventory=$("#single_member_"+id).attr("data-inventory");
	var count=$("#count_show").val().replace(/\D/g,'');
	if(count==""){
		layer.open({
		    content: '至少购买一件商品!'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		});
		return;
	}
	if((+count)<1){
		layer.open({
		    content: '至少购买一件商品!'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		});
		return;
	}
	
	if((+count)>(+data_inventory)){
		  layer.open({
			    content: '购买量不能超过库存量！'
			    ,skin: 'msg'
			    ,time: 2 //2秒后自动关闭
			});
	  }else{
		  jQuery("#single_member_"+id).text(count);
		  update_inventory(id,count);
		  hideSingleNum();
	  }
}

function blurSingleCart(){
	var id=$("#singlebtn").attr("data-id");
	var data_inventory=$("#single_member_"+id).attr("data-inventory");
	var count=$("#count_show").val().replace(/\D/g,'');
	if(count!=null&&count!=""){
		if((+count)>(+data_inventory)){
			count=data_inventory
		}
		$("#count_show").val(Math.abs(count));
	}else{
		$("#count_show").val("");
	}
	
}

function hideSingleNum(){
	$(".single_size").hide();
}

//多规格隐藏货品
function hideMulitNum(){
	$(".bomb").hide();
}
//多规格选择货品
var mulitflag=true;
function chooseMulitNum(id){
	 $("#specification").html('<div style="height: 10rem; text-align: center;"><img style="margin-top: 4rem;height: 2rem" src="'+webPathURL+'/resources/dhb/buyer/images/loading.gif"/></div>');
	 $(".bomb").show();
	 $(".bomb_box").addClass("bomb_boxa");
	$.ajax({ 
            type : "POST", 
            url  : domainURL+"/mobile/store_goods_cart_ajax.htm",  
            cache : false, 
            dataType:"html",
            data : {"id":id}, 
            success : function(data){
            	$("#specification").html(data);
            	
            //	initMulitCartShow(id);
            	
            	$("#store_goods_savebtn").click(function(){
            		if(mulitflag){
            			
            			mulitflag=false;
            			
            			$.ajax({ 
             	            type : "POST", 
             	            url  : domainURL+"/mobile/add_goods_cartAll.htm",  
             	            cache : false, 
             	            dataType:"json",
             	            data : $("#addcartAll").serialize(), 
             	            success : function(data){
             	            	mulitflag=true;
             	            	
             	            	if(data.count!="0"){
    	         	       			$("#good_class_count").html(data.count);
    	         	       			$("#goods_total_price").html("<span>￥</span>"+data.total_price.toFixed(2));
    	         	       			$("#goods_content").html("共 "+data.count+" 种商品 "+data.goods_total_count+" 件")
    	         	       			layer.open({
    	         	       			    content: '已成功加入购物车!'
    	         	       			    ,skin: 'msg'
    	         	       			    ,time: 2 //2秒后自动关闭
    	         	       			});
    	         	       		    $(".bomb").hide();
    	         	       		}else{
    	         	       			layer.open({
    	         	       			    content: '至少购买一件商品!'
    	         	       			    ,skin: 'msg'
    	         	       			    ,time: 2 //2秒后自动关闭
    	         	       			});
    	         	       		}
             	            }, 
             	            error : function(data){
             	            	mulitflag=true;
             	            }
             	        });
            		}
            		 
            	})
            }, 
            error : function(data){
            	
            }
        });
	
}


//切换规格
function chooseSpec(id){
	$(".divCon").hide();
	$("#divCon_"+id).show();
	$("#tabspec_"+id).siblings().removeClass("active_size");
	$("#tabspec_"+id).addClass("active_size");
}
//多规格
function blurItemNum(id){
	var count=jQuery("#itemnum_"+id).val();
	var count=jQuery("#itemnum_"+id).val().replace(/\D/g,'');
	var inventory=jQuery("#itemnum_"+id).attr("data-inventory");
	if(count==""||count<0){
		count=0;
	}
	if(+inventory<=0){
		layer.open({
		    content: '该商品库存不足'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		});
		jQuery("#itemnum_"+id).val("");
		return;
	}
	
	if((+count)>(+inventory)){
		 layer.open({
			    content: '订购商品数应小于库存量'
			    ,skin: 'msg'
			    ,time: 2 //2秒后自动关闭
			});
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
//多规格
function addItemNum(id){
	var count=jQuery("#itemnum_"+id).val();
	var count=jQuery("#itemnum_"+id).val().replace(/\D/g,'');
	if(count==""||count<0){
		count=0;
	}
	var inventory=jQuery("#itemnum_"+id).attr("data-inventory");
	if(+inventory<=0){
		layer.open({
		    content: '该商品库存不足'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		});
		jQuery("#itemnum_"+id).val("");
		return;
	}
	if((+count)>=(+inventory)){
		layer.open({
		    content: '订购商品数应小于库存量'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		});
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
	
	if(+inventory<=0){
		layer.open({
		    content: '该商品库存量不足'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		});
		jQuery("#itemnum_"+id).val("");
		return;
	}
	if((+count)>(+inventory)){
		layer.open({
		    content: '订购商品数应小于库存量'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		});
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
        url  : domainURL+"/queryMulitPrice.htm",  
        cache : false, 
        dataType:"json",
        data : {"id":id,"count":count}, 
        success : function(data){
        	if(data.price!=null&&data.price>0){
        		$("#price_"+id).html("<span>￥</span>"+data.price);
        	}else{
        		$("#price_"+id).html("<span>￥</span>"+$("#price_"+id).attr("data-price"));
        	}
        }, 
        error : function(data){
        	
        }
    });
}