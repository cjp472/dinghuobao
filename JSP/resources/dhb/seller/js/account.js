$(function(){
	$("#subPass").click(function(){
		if($("#passForm").valid()){
			$.ajax({
				url:storePath+"/seller/account_password_save.htm",
				data:$("#passForm").serialize(),
				type:"post",
				dataType:"json",
				success:function(data){
					if(data){
						alert("密码修改成功,请重新登录！");
						window.location.href=storePath+"/jm_logout.htm";
						
					}else{
						alert("原始密码输入错误，修改失败");
					
					}
				}
			})
		}
	})
	
	$("#subMoblie").click(function(){
		if($("#phoneForm").valid()){
			$.ajax({
				url:storePath+"/seller/account_mobile_save.htm",
				data:$("#phoneForm").serialize(),
				type:"post",
				dataType:"json",
				success:function(data){
					if(data){
						alert("登录手机号修改成功,请重新登录！");
						window.location.href=storePath+"/jm_logout.htm";
						
					}else{
						alert("验证码错误，登录手机号修改失败");
					
					}
				}
			})
		}
	})
})