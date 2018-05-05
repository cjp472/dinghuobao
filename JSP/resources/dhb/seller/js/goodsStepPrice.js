var count=0;
function addStepPrice(){
	$("#nocontent").hide();
	count++;
	var v1="add_"+count;
	var str='<tr class="opertr" id="itemtr_'+v1+'">';
	str+=' <td class="number"><input type="hidden" name="id" id="id_'+v1+'"><input type="text" placeholder="" name="begin_num" id="begin_num_'+v1+'" onblur="blurNumber(this)" maxlength="9"/></td>';
	str+=' <td class="number"><input type="text" placeholder="" name="end_num" id="end_num_'+v1+'" onblur="blurNumber(this)" maxlength="9"/></td>';
	str+=' <td class="number"><input type="text" placeholder="" name="price" id="price_'+v1+'"  onblur="blurPrice(this)"  maxlength="8"/></td>';
	str+=' <td> <a href="javascript:void(0)" onclick="addItem(\''+v1+'\')" style="color:#1188ff">保存</a> | <a href="javascript:void(0)" onclick="removeItem(\''+v1+'\')" style="color:#1188ff">删除</a></td></tr>';
	$("#tab_list").append(str);
}
function blurNumber(obj){
 	var reg = new RegExp("^[0-9]*$");
	if(!reg.test(jQuery(obj).val())){
	    jQuery(obj).val("1");
	} 
	if(jQuery(obj).val()<1){
		 jQuery(obj).val("1");
	}
}

function blurPrice(obj){
 
   var v1=jQuery(obj).val();
	if(!isNaN(v1)){
		
		if(!/^\d+(?:\.\d{1,2})?$/.test(jQuery(obj).val())){
			jQuery(obj).val(Math.abs((+v1).toFixed(2)))
		}else{
			jQuery(obj).val(Math.abs(+v1))
		}
	}else{
		jQuery(obj).val("1");
	}
 
}
function addItem(v){
	var begin_num=$("#begin_num_"+v).val();
	if(begin_num==null||begin_num==""){
		alert("请输入起始数量");
		return;
	}
	var end_num=$("#end_num_"+v).val();
	if(end_num==null||end_num==""){
		alert("请输入结束数量");
		return;
	}
	if((+begin_num)>=(+end_num)){
		alert("起始数量应大于结束数量");
		return;
	}
	var price=$("#price_"+v).val();
	if(price==null||price==""){
		alert("请输入货品价格");
		return;
	}
	var goodsItemId=$("#goodsItemId").val();
	var id=$("#id_"+v).val();
	 $.ajax({ 
         type : "POST", 
         url  : webPath+"/seller/goods_step_price_save.htm",  
         cache : false, 
         dataType:"json",
         data : {"begin_num":begin_num,"end_num":end_num,"price":price,"id":id,"goods_item_id":goodsItemId}, 
         success : function(data){
        	 $("#id_"+v).val(data.stepId);
         	 alert("保存成功")
         }, 
         error : function(data){
         	
         }
     });
	
}

function removeItem(v){
	
	var goodsItemId=$("#goodsItemId").val();
	var id=$("#id_"+v).val();
	 $.ajax({ 
         type : "POST", 
         url  : webPath+"/seller/goods_step_price_remove.htm",  
         cache : false, 
         dataType:"json",
         data : {"id":id,"goods_item_id":goodsItemId}, 
         success : function(data){
        	 $("#itemtr_"+v).remove();
         	 alert("删除成功")
         }, 
         error : function(data){
         	
         }
     });
	
}

function closeWin(){
	 jQuery('#cboxClose').click();
	 window.location.reload()
}
