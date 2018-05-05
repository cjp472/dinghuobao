jQuery(function(){
	//获取验证码
	jQuery(document).on("click","#getCaptcha",function(){
		if(jQuery("#getCaptcha").html() != "获取验证码"){
			return false;
		}
		var mobileNum = jQuery("#mobileRegNum").val();	
		if(!isMobileNO(mobileNum)){
			return false;
		}
		jQuery.getJSON(url,function(data,status){
			var json = data;
			if(json.success){
				timeDown(jQuery("#getCaptcha"),60);
			}
			alert(json.data);
		});
	});
	//验证码验证
	jQuery(document).on("blur","#captcha",function(){
		var captcha = jQuery("#captcha").val();
		if(captcha == null || captcha == ""){
			hintShow("captchaMsg","请输入验证码");
			return false;
		}else{
			hintHide("captchaMsg");
		}
		//获得验证码
		jQuery.getJSON(url,function(data,status){
			var json = data;
		});
	});
	//倒计时
	function timeDown(obj,seconds) {//obj为按钮的对象，seconds是秒数
		var wait = seconds;
		if (wait == 0) {
			obj.removeAttr("disabled");           
			obj.html("获取验证码");//改变按钮中value的值
			wait = seconds;
		} else {
			obj.attr("disabled", true);//倒计时过程中禁止点击按钮
			obj.html(wait + "秒后重新获取");//改变按钮中value的值
			wait--;
			setTimeout(function() {
				timeDown(obj,wait);//循环调用
			},
			1000)
		}
	}
	//手机号校验
	function isMobileNO(mobileNum){
		var pattern = new RegExp("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		if(!pattern.test(mobileNum)){
			hintShow("mobileNumMsg","用户名不正确");
			return false;
		}else{
			hintHide("mobileNumMsg");
		}
		jQuery("#mobileNumMsg").empty();
		return true;
	}
	//表单提交
	jQuery("#memberRegist").click(function(){
		if(!checkRegForm_Regist()){
			return false;
		}
	jQuery("#registForm").submit();
	});
	function checkRegForm_Regist(){
		jQuery("#getCaptcha").trigger('click');
		jQuery("#captcha").trigger('blur');
		jQuery("#new_pwd1").trigger('blur');
		jQuery("#new_pwd2").trigger('blur');
		return true;
	}
	//原始密码验证
	jQuery(document).on("blur","#old_pwd",function(){
		var old_pwd = jQuery("#old_pwd").val();
		if(old_pwd == ''){
			hintShow("old_pwd_msg","请输入原始密码!");
			return false;
		}else{
			hintHide("old_pwd_msg");
		}
		//ajax 请求后台验证
		if(old_pwd != '123'){
			hintShow("old_pwd_msg","原始密码错误!");
			return false;
		}else{
			hintHide("old_pwd_msg");
		}
	});
	//表单提交
	jQuery("#pwdSubmit").click(function(){
		if(!checkRegForm_update()){
			return false;
		}
	jQuery("#registForm").submit();
	});
	function checkRegForm_update(){
		jQuery("#old_pwd").trigger('blur');
		jQuery("#new_pwd1").trigger('blur');
		jQuery("#new_pwd2").trigger('blur');
		return true;
	}
	//邮箱验证
	jQuery(document).on("blur","#email",function(){
		var email = jQuery("#email").val();
		var emailReg = /^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$/;
;	
		if(email != ""){
			if(!emailReg.test(email)){
				hintShow("emailMsg","请输入正确的邮箱！");
				return false;
			}else{
				hintHide("emailMsg");
			}
		}
	});
	//表单提交
	jQuery("#mineSub").click(function(){
		if(!checkmineSub()){
			return false;
		}
	jQuery("#registForm").submit();
	});
	function checkmineSub(){
		jQuery("#email").trigger('blur');
		return true;
	}
	//新密码验证
	jQuery(document).on("blur","#new_pwd1",function(){
		var new_pwd1 = jQuery("#new_pwd1").val();
		if(new_pwd1 == ''){
			hintShow("new_pwd1_msg","请输入新密码!");
			return false;
		}else{
			hintHide("new_pwd1_msg");
		}
		var filter = /^[A-Za-z0-9]{6,20}$/;
		if(!filter.test(new_pwd1)){
			hintShow("new_pwd1_msg","密码长度在6-20之间");
			return false;
		}else{
			hintHide("new_pwd1_msg");
		}
	});
	//新确认密码验证
	jQuery(document).on("blur","#new_pwd2",function(){
		var new_pwd1 = jQuery("#new_pwd1").val();
		var new_pwd2 = jQuery("#new_pwd2").val();
		if(new_pwd2 == ''){
			hintShow("new_pwd2_msg","请输入确认密码!");
			return false;
		}else{
			hintHide("new_pwd2_msg");
		}
		var filter = /^[A-Za-z0-9]{6,20}$/;
		if(!filter.test(new_pwd2)){
			hintShow("new_pwd2_msg","密码长度在6-20之间");
			return false;
		}else{
			hintHide("new_pwd2_msg");
		}
		if(new_pwd1 != new_pwd2){
			
			hintShow("new_pwd2_msg","两次密码输入不一致");
			return false;
		}else{
			hintHide("new_pwd2_msg");
		}
	});
	//提示显示
	function hintShow(showId,showCon){
		jQuery("#"+showId).parent().show();
		jQuery("#"+showId).text(showCon);
		
	}
	//提示隐藏
	function hintHide(hideId){
		jQuery("#"+hideId).parent().hide();
		jQuery("#"+hideId).text("");
	}
	
	
	/*jQuery("#old_pwd").keydown(function(){
		if(jQuery(this).val() != ''){
			jQuery("#old_pwd_x").css('display','block'); 
		}
	});
	jQuery("#old_pwd_x").click(function(){
		jQuery("#old_pwd").val("");
		jQuery(this).css('display','none');
	});
	jQuery("#new_pwd1").keydown(function(){
		if(jQuery(this).val() != ''){
			jQuery("#new_pwd1_x").css('display','block'); 
		}
	});
	jQuery("#new_pwd1_x").click(function(){
		jQuery("#new_pwd1").val("");
		jQuery(this).css('display','none');
	});
	jQuery("#new_pwd2").keydown(function(){
		if(jQuery(this).val() != ''){
			jQuery("#new_pwd2_x").css('display','block'); 
		}
	});
	jQuery("#new_pwd2_x").click(function(){
		jQuery("#new_pwd2").val("");
		jQuery(this).css('display','none');
	});*/
})