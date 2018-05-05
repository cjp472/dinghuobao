
var flag=false;
function importData(){
	
	if($("#upfile").val()==null||$("#upfile").val()==""){
		layer.msg("请选择要导入的文件数据");
		return;
	}
	var v1=$("#upfile").val();

	if(v1.lastIndexOf(".")==0){
		layer.msg("导入的文件格式不正确");
		return;
	}
	
	var pefix=v1.substring(v1.lastIndexOf(".")+1)
	if(pefix=="xls"||pefix=="xlsx"){
		if(confirm("导入的商品模板与商品类型必须对应，您确定要导入？")){
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
			$('#importData_theForm').ajaxSubmit({    
		        url:webPath+'/seller/importGoods.htm',  
		        dataType: 'json',  
		        error : function(request) {
					flag=false;
					layer.alert("服务器异常");
					layer.close(msg);
				},
				success : function(data) {
					layer.close(msg);
					flag=false;
					switch(data)
					{
					case 0:
						layer.msg("商品导入成功");
						jQuery('#cboxClose').click();
						window.location.reload();
					    break;
					case 1:
					  layer.msg("请先登录");	
					  break;
					case 2:
						layer.msg("文件格式不正确");	
						break;
					case 3:
						layer.msg("商品模板与类型不对应");	
						  break;
					 
					}
				}
		    }); 
			
		}
	}else{
		layer.msg("导入的文件格式只能为“xls”和“xlsx” ");
		return;
	}
}
