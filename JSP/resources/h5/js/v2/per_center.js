//获取验证码
jQuery("#getCaptcha").click(function(){
	if(jQuery("#getCaptcha").html() != "获取验证码"){
		return false;
	}
	var mobileNum = jQuery("#mobileRegNum").val();	
	if(!isMobileNO(mobileNum)){
		return false;
	}
	jQuery.getJSON("/mobile/member/getCaptcha.html?mobileNum="+mobileNum,function(data,status){
		var json = data;
		if(json.success){
			timeDown(jQuery("#getCaptcha"),60);
		}
		alert(json.data);
	});
});
//验证码验证
jQuery("#captcha").blur(function(){
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
})
//新密码验证
jQuery("#passwordReg").blur(function(){
	var filter = /^[A-Za-z0-9]{6,20}$/;
	var password = jQuery("#passwordReg").val();
	if(!filter.test(password)){
		hintShow("newPwdMsg","密码长度在6-20之间");
		return false;
	}else{
		hintHide("newPwdMsg");
	}
})
//新确认密码验证
jQuery("#repassword").blur(function(){
	var filter = /^[A-Za-z0-9]{6,20}$/;
	var password = jQuery("#passwordReg").val();
	var repassword = jQuery("#repassword").val();
	if(!filter.test(repassword)){
		hintShow("newPwdMsg2","密码长度在6-20之间");
		return false;
	}else{
		hintHide("newPwdMsg2");
	}
	if(password != repassword){
		hintShow("newPwdMsg2","两次密码输入不一致");
		return false;
	}else{
		hintHide("newPwdMsg2");
	}
})
//表单提交
jQuery("#memberRegist").click(function(){
	if(!checkRegForm()){
		return false;
	}
jQuery("#registForm").submit();
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
function checkRegForm(){
	var mobileNum = jQuery("#mobileRegNum").val();
	if(mobileNum == ''){
		hintShow("mobileNumMsg","用户名不能为空");
		return false;
	}
	if(!isMobileNO(mobileNum)){
		hintShow("mobileNumMsg","用户名不正确");
		return false;
	}
	var captcha = jQuery("#captcha").val();
	if(captcha == null || captcha == ""){
		hintShow("captchaMsg","请输入验证码");
		return false;
	}
	var filter = /^[A-Za-z0-9]{6,20}$/;
	var password = jQuery("#passwordReg").val();
	var repassword = jQuery("#repassword").val();
	if(!filter.test(password)){
		hintShow("newPwdMsg","密码长度在6-20之间");
		return false;
	}
	if(password != repassword){
		hintShow("newPwdMsg2","两次密码输入不一致");
		return false;
	}
	return true;
}
	