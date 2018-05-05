var flag=false;
function importGoods(type){
	
	var isSuccess=true;
	var ids="";
	var sale_prices="";//销售价
	$(".chk_import:checked").each(function(){
		 ids=jQuery(this).val()+","+ids;
		 var goodsPriceText= $(this).parent().parent().find("input[name='goods_price']");
		 var goodsPriceVal = $(goodsPriceText).val();
		 
		 if(jQuery.trim(goodsPriceVal)==''){
			 alert("销售价必填");
			 $(goodsPriceText).focus();
			 isSuccess = false;
			 return false; 
		 }
		 
		 if(/^[0-9]+([.][0-9]+){0,1}$/.test(goodsPriceVal)){
		 }else{
			 alert("请输入数字");
			 $(goodsPriceText).focus();
			 isSuccess = false;
			 return false;
		 }
		 sale_prices=$(goodsPriceText).val()+","+sale_prices;
		 
	   });
	
	if(!isSuccess){
		return false;
	}
	
	if(ids==null||ids==""){
		alert("至少选择一项");
		return;
	}
	
	$("#ids").val(ids);
	$("#type").val(type);
	$("#sale_prices").val(sale_prices);
	if(confirm("导入后不可取消，您确定要继续？")){
		if(flag){
			return;
		}
		//加载层-风格4
		var msg=layer.msg('正在导入中,请耐心等待..', {
		  icon: 16
		  ,shade: 0.01
		  ,time:0
		});
		flag=true;
		$('#import_goods_Form').ajaxSubmit({    
	        url:storePath+'/buyer/importGoods.htm',  
	        dataType: 'json',  
	        error : function(request) {
				flag=false;
				layer.alert("服务器异常");
				layer.close(msg);
			},
			success : function(data) {
				layer.close(msg);
				flag=false;
				if(data=="0"){
					var index=layer.alert("导入成功", {
							closeBtn: 0
							}, function(){
								$("#cboxClose").click();
								layer.close(index);
							});
				}else if(data=="1"){
					layer.alert("请先登录");
				}
				
			}
	    }); 
		
	}
	
}
function checkAll_chk(obj){
	$(".chk_import").prop("checked",$(obj).prop("checked"));
}