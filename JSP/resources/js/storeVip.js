/**
 * 店铺会员列表相关JS
 * @author zhaihl
 */

/**
 * 加载店铺优惠券
 * @param show
 * @param sel
 * @param hide
 * @param muti
 * @returns
 */
function ff_coupon(show,sel,hide,muti){
	if(muti){
		if(getSelected()==""){
			alert("请指定要发放优惠券的用户");
			return;
		}
	}
	jQuery.post(webPath+"/loadCoupon.htm", {
		"storeId" : storeid
	}, function(data) {
		if(data!=''){
			$("#"+show).hide();
			$("#"+sel).empty();
			$("#"+sel).append("<option value=''>请选择店铺优惠券...</option>");
			jQuery.each(data, function(index, item) {
				jQuery("#"+sel).append(
						"<option value='"+item.id+"'>"
								+ item.name + "</option>");
				$("#"+hide).show();
			});
		} else{
			confirm("您的店铺还没有优惠券，是否现在添加？","亲，出错了",function(a){
				if(a=="ok"){
					window.location.href = webPath+"/seller/coupon_add.htm";
				}
			});
		}
	}, "json");
}


/**
 * 下拉改变，关联会员与优惠券
 * @param obj
 * @param userid
 * @returns
 */
function select(obj,userid){
	jQuery.post(webPath+"/sendCoupon.htm", {
		"userids" : userid,
		"num" : 1,
		"couponid" : obj.value
	}, function(data) {
		$(obj).after("<span style='color:red'>"+data.msg+"</span>").fadeOut(1000);
		window.location.href = webPath+"/seller/storevip.htm"
	}, "json");
}

/**
 * 批量修改
 * @param obj
 * @returns
 */
function batchSend(obj){
	var userids = getSelected();
	if(userids == ""){
		alert("请指定要发放优惠券的用户");
		return;
	}
	select(obj,userids);
}

/**
 * 获得多选ID
 * @returns
 */
function getSelected(){
	var mulitId = "";
	jQuery(":checkbox:checked").each(function() {
		if (jQuery(this).val() != "") {
			if(mulitId == ""){
				mulitId = jQuery(this).val();
			}
			else{
				mulitId = mulitId + "," + jQuery(this).val();
			}
		}
	});
	return mulitId;
}