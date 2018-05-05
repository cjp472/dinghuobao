function gotoAajxPage(n){
	var murl=$("#ListForm").attr("action");
	 $("#currentPage").val(n);
		 $.ajax({
	 		url:murl,
	 		data:$("#ListForm").serialize(),
	 		type:"post",
	 		dataType:"html",
	 		success:function(data){
	 			$("#cboxLoadedContent").html(data);
	 		}
	 	});
	 }
	
	function closeWin(){
		 jQuery('#cboxClose').click();
	 }
	 function chooseClient(){
		var v1= $(".client_chk input[name='id']:checked").val();
		if(v1==null||v1==""){
			alert("请选择客户！")
			return;
		}
		var client_show=$("#clent_name_"+v1).text();
		if(client_show==null||client_show==""){
			client_show=$("#mobile_"+v1).text();
		}
		$("#client_show").val(client_show);
		$("#client").val(v1);
		jQuery('#cboxClose').click();
		
		var d=$("#goods_add");
		var url=d.attr("dialog_uri");
		if(url.indexOf("?")>-1){
		  url=url+'&buyer_id='+v1;
		}else{
		  url=url+'?buyer_id='+v1;
		}
		d.attr("dialog_uri",url);
		var b={title:d.attr("dialog_title"),width:Number(d.attr("dialog_width"))+100,height:Number(d.attr("dialog_height"))+200,overlayClose:false,opacity:0.75,fixed:true,href:d.attr("dialog_uri")};
		d.colorbox(b);
	 }
	 
	 function chooseItemGoods(){
		   var vv1= $("input[name='item_id']:checked");
			if(vv1==null||vv1==""||vv1.length==0){
				alert("请选择货品！")
				return;
			}
			var count=0;
			var str1="";
			$("input[name='item_id']:checked").each(function(){
				var v1=$(this).val();
				if($("#itemtr_"+v1).html()!=null&&$("#itemtr_"+v1).html()!=""){
					//alert("您已选择了该商品了！")
					return;
				}
				count++;
				var str='<tr class="tr_pdtb" id="itemtr_'+v1+'">'
             		+'<td class="size14 td_left"><input type="hidden" name="itemId" value="'+v1+'">'+$("#img_"+v1).html()+' <span>'+$("#goods_name_"+v1).text()+'</span></td>'
                 	+'<td>'+$("#spec_info_"+v1).text()+'</td>'
                 	+'<td>'+$("#goods_units_"+v1).text()+'</td>'
                	+' <td>'+$("#goods_inventory_"+v1).val()+'</td>'
                    +'<td class="number_a"><input type="text" value="1" name="number" data-inventory="'+$("#goods_inventory_"+v1).val()+'" id="number_'+v1+'" onblur="blurNumber(this)" maxlength="9"/></td>'
                 	+'<td class="number_b"><input type="text" attrId="'+v1+'" value="'+$("#price_"+v1).val()+'" id="purchase_price_'+v1+'" name="purchase_price" attrId="'+v1+'" onblur="blurPrice(this)"  maxlength="8"/><input type="hidden" id="my_hide_price_'+v1+'" value="'+$("#price_"+v1).val()+'"></td>'
                 	+'<td id="subsum_'+v1+'">0</td>'
              		+'<td class="delect_sc"><a href="javascript:void(0)" onclick="removeItem('+v1+')" >删除</a></td></tr>';
				
				str1+=str;
				
			})
			if(vv1.length==count){
				$("#mycontent").append(str1);
				jQuery('#cboxClose').click();
				sumNumber();
			}else{
				alert("不能重复选择货品");
			}
			
	 }
	 
	 
	 function removeItem(id){
		 jQuery('#cboxClose').click();
		 $("#itemtr_"+id).remove();
		 sumNumber();
	 }
	 
	 function blurNumber(obj){
		 	var reg = new RegExp("^[0-9]*$");
			if(!reg.test(jQuery(obj).val())){
			    jQuery(obj).val("1");
			} 
			if(jQuery(obj).val()<1){
				 jQuery(obj).val("1");
			}
			var inventory= $(obj).attr("data-inventory");
			if(inventory==null||inventory==""){
				inventory="0";
			}
			if((+jQuery(obj).val())>(+inventory)){
				alert("该货品库存不足");
				 jQuery(obj).val(inventory);
			}
			sumNumber();
	 }
	 function blurPrice(obj){
			var id=$(obj).attr("attrId");
		   var v1=jQuery(obj).val();
			if(!isNaN(v1)){
				
				if(!/^\d+(?:\.\d{1,2})?$/.test(jQuery(obj).val())){
					jQuery(obj).val(Math.abs((+v1).toFixed(2)))
				}else{
					jQuery(obj).val(Math.abs(+v1))
				}
			}else{
				jQuery(obj).val(Math.abs($("#my_hide_price_"+id).val()));
			}
		 
			
			sumNumber();
	}
	 //计算种类
	 function sumNumber(){
			 
			 var v2=0;
			 $("input[id^='number_']").each(function(){
				var num= $(this).val();
				if(num==null||num==""||isNaN(num)){
					num=0
				}
				v2+=(+num);
			 })
			// $("#goods_sum").val(v2);
			 $("#goods_sum_show").html(v2);
			 
			 var v3=0;
			 $("input[id^='purchase_price_']").each(function(){
				var price= $(this).val();
				var id=$(this).attr("attrId");
				if(price==null||price==""||isNaN(price)){
					price=Math.abs($("#my_hide_price_"+id).val());
				}
				
				var id=$(this).attr("attrId");
				var num= $("#number_"+id).val();
				if(num==null||num==""||isNaN(num)){
					num=0
				}
				var sumprice=(+num)*parseFloat(price);
				$("#subsum_"+id).text(sumprice);
				v3+=sumprice;
			
			 })
			// $("#money_total").val(v3.toFixed(2));
			 $("#money_total_show").html(v3.toFixed(2));
			 $("#goods_amount").val(v3.toFixed(2));
			 v3+=(+$("#ship_price").val())
			 $("#total_price_show").html("￥"+v3.toFixed(2))
			 $("#total_price").val(v3.toFixed(2));
	 }
	 
	 $(function(){
		 $("#ship_price_btn").click(function(){
			$("#ship_price_win").show();
			$("#ship_price_btn img").toggle();
				
		})
		$("#total_price_btn").click(function(){
			$("#total_price_win").show();
			$("#total_price_btn img").toggle();
				
		})
		
		//发票弹框
		$("#invoice_info img").click(function(){
			$("#companyName").removeClass("error");
			$("#invoice_info_win").show();
			$("#invoice_info img").toggle();
				
		})
		
		$("#client_address_btn").click(function(){
			if($("#client").val()==null||$("#client").val()==""){
				alert("请先选择客户");
				return;
			}
			$.ajax({
		 		url:storePath+"/seller/valet_order_addr_choose.htm",
		 		data:{"client_id":$("#client").val()},
		 		type:"post",
		 		dataType:"html",
		 		success:function(data){
		 			$("#addr_content").html(data);
		 			$(".send_bomb").show();
					$(".endnews_img img").toggle();
		 		}
		 	});
			
		})
		
		//初始化附件组件
		 onfileUpload(0);
		 $('#fileupload_0').bind('fileuploadsubmit', function (e, data) {

		        data.formData = { "fileId": fileId };  //如果需要额外添加参数可以在这里添加

		  });
		 
		
	 })
	 //关闭运费弹框
	 function close_ship_win(){
		 $("#ship_price_win").hide();
		 $("#ship_price_btn img").toggle();
	 }
	 function close_total_win(){
		 $("#total_price_win").hide();
		 $("#total_price_btn img").toggle();
	 }
	 
	 function close_invoice_win(){
		 $("#invoice_info_win").hide();
		 $("#invoice_info img").toggle();
	 }
	 
	 //运费失去焦点
	 function ship_price_blur(obj) {
		   var v1=jQuery(obj).val();
			if(!isNaN(v1)){
				
				if(!/^\d+(?:\.\d{1,2})?$/.test(jQuery(obj).val())){
					jQuery(obj).val(Math.abs((+v1).toFixed(2)))
				}else{
					jQuery(obj).val(Math.abs(+v1))
				}
			}else{
				jQuery(obj).val(Math.abs($("#ship_price").val()));
			}
	}
	 
	 function total_price_blur(obj) {
		   var v1=jQuery(obj).val();
			if(!isNaN(v1)){
				if(!/^\d+(?:\.\d{1,2})?$/.test(jQuery(obj).val())){
					if(v1==null||v1==""){
						v1=$("#total_price").val();
					}
					jQuery(obj).val(Math.abs((+v1).toFixed(2)))
				}else{
					jQuery(obj).val(Math.abs(+v1))
				}
			}else{
				jQuery(obj).val(Math.abs($("#total_price").val()));
			}
	}
	 function sure_ship_win(){
		 var v1=$("#ship_price_input").val();
		 if(v1==null||v1==""){
			 v1=$("#ship_price").val();
		 }
		 $("#ship_price").val(v1)
		 $("#ship_price_show").html("￥"+Math.abs((+v1)).toFixed(2));
		 sumNumber();
		 close_ship_win();
	 }
	 
	 function sure_total_win(){
		 var v1=$("#total_price_input").val();
		 if(v1==null||v1==""){
			 v1=$("#total_price").val();
		 }
		 $("#total_price").val(v1)
		 $("#total_price_show").html("￥"+Math.abs((+v1)).toFixed(2));
		 close_total_win();
	 }
	 
	 function close_addr_win(){
		$(".send_bomb").hide();
		$(".endnews_img img").toggle();
		$("#addr_content").html("");
	 }
	 
	 //发票抬头
	 function changeInvoice(obj){
		 var v=$(obj).val();
		 if(v=='0'){
			 $("#invoice_info").hide();
		 }else if(v=='1'){
			 $("#invoice_info").show();
		 }
	 }
	 //填写发票抬头
	 function sure_invoice_win(){
		 if($("#companyName").val()==null||$("#companyName").val()==""){
			 $("#companyName").addClass("error");
			 return;
		 }else{
			 $("#companyName").removeClass("error");
		 }
		 $("#companyName_show").html('发票抬头：'+$("#companyName").val());
		 close_invoice_win();
	 }
	 var fileId="";
	 //附件上传
	 function onfileUpload(index){
			$('#fileupload_'+index).fileupload({
				  dataType: 'json',
		        replaceFileInput: true,
		        sequentialUploads: true,
		        progressInterval: 50,
		        bitrateInterval: 500,

		       add: function(e, data) {
		    	   
		            if (data.files && data.files.length == 1) {
		                if(data.files[0]!=null){
		    				var fileSize=data.files[0].size;
		    			    var maxSize = 10*1024*1024;
		    			    if(fileSize!=null&&fileSize>0&&fileSize > maxSize){
		    			    	alert('上传文件最大为10M');
		    			        return false;
		    			    }
		    			    if(fileSize!=null&&fileSize <1 ){
		    			    	alert('上传文件不能为空文件');
		    			        return false;
		    			    }
		    			}
		               
		                data.submit();
		            }
		        },
		        done: function(e, data) {
		        	fileId=data.result.id;
		        	$("#file_title").html(data.result.file_name);
		        	$("#file_id").val(data.result.id);
		        },
		        progress: function(e, data) {
		        }
			});
		}