/**
 * 个人中心地址管理相关
 */
function deleteAddr(id){
	layer.open({
	    content: '您确定要删除该地址吗？'
	    ,btn: ['确定', '取消']
	    ,yes: function(index){
	      layer.close(index);
	      window.location.href=domainURL+'/mobile/buyer/address_del.htm?mulitId='+id;
	    }
	  });
}

var addrinfo='';
function chooseAddr(){
	 $('#areaSelected').find('#filtrate_ul').find('li').eq(0).addClass('selected').siblings().removeClass('selected');
	$('#areaSelected').find(".nctouch-default-list").html($("#hidepro").html());
	$('#areaSelected').show();
	$('#areaSelected').find('.nctouch-full-mask').addClass('left').removeClass('right');
	addrinfo='';
	 if (typeof(myScrollArea) == 'undefined') {
		 
         if (typeof(IScroll) == 'undefined') {	
             $.ajax({
                 url: webPathURL+'/resources/h5/js/v4/iscroll.js',
                 dataType: "script",
                 async: false
               });
         }
         myScrollArea = new IScroll('#areaSelected .nctouch-main-layout-a', { mouseWheel: true, click: true });
     } else {
         myScrollArea.destroy();
         myScrollArea = new IScroll('#areaSelected .nctouch-main-layout-a', { mouseWheel: true, click: true });
     }
}

function selectArea(obj,mark){
	var id=$(obj).attr("attrId");
	if(mark=="province"||mark=="city"){
		addrinfo+=$(obj).text();
		$.post(
				domainURL+"/mobile/buyer/account_getAreaChilds.htm",
				{
				"parent_id":id,
				"areaMark":mark
				},
				function(data){
					if(data==null||data.trim()==""){
						$("#area_id").val(id);
						$("#areashow").val(addrinfo);
						 $('#areaSelected').find('.nctouch-full-mask').addClass('right').removeClass('left');
						 btnCheck2();
					}else{
						if(mark=="province"){
							 $('#areaSelected').find('#filtrate_ul').find('li').eq(1).addClass('selected').siblings().removeClass('selected');
						}
						if(mark=="city"){
							 $('#areaSelected').find('#filtrate_ul').find('li').eq(2).addClass('selected').siblings().removeClass('selected');
						}
						$('#areaSelected').find(".nctouch-default-list").html(data);
			            if (typeof(myScrollArea) == 'undefined') {
			                if (typeof(IScroll) == 'undefined') {
			                    $.ajax({
			                        url: webPathURL+'/resources/h5/js/v4/iscroll.js',
			                        dataType: "script",
			                        async: false
			                      });
			                }
			                myScrollArea = new IScroll('#areaSelected .nctouch-main-layout-a', { mouseWheel: true, click: true });
			            } else {
			                myScrollArea.destroy();
			                myScrollArea = new IScroll('#areaSelected .nctouch-main-layout-a', { mouseWheel: true, click: true });
			            }
			           
					}
					
				},"text");
	}else{
		$("#area_id").val(id);
		$("#areashow").val(addrinfo);
		$('#areaSelected').find('.nctouch-full-mask').addClass('right').removeClass('left');
		btnCheck2();
	}
}
function _close(){
	$('#areaSelected').find('.nctouch-full-mask').addClass('right').removeClass('left');
}

function writeClear2(o) {
    if (o.val().length > 0) {
        o.parent().addClass('write');
    } else {
        o.parent().removeClass('write');
    }
    btnCheck2();
}
function btnCheck2() {
    var btn = true;
    if($("#trueName").val().length==0){
    	btn = false;
    }
    if($("#areashow").val().length==0){
    	btn = false;
    }
    if($("#area_info").val().length==0){
    	btn = false;
    }
    if($("#mobile").val().length==0){
    	btn = false;
    }
    
    
    if (btn) {
        $('#savebtn').parent().addClass('ok');
    } else {
    	 $('#savebtn').parent().removeClass('ok');
    }
}

function input_del2(obj){
	 $(obj).parent().removeClass('write').find('input').val('');
    btnCheck2();
}

function check(v) {
	if (!$(v).parent().hasClass('ok')) {
        return false;
    }
	var trueName = document.getElementById("trueName").value;
	var area_info = document.getElementById("area_info").value;
	var mobile = document.getElementById("mobile").value;
	

	if (trueName.length == 0) {
		//提示
		  layer.open({
		    content: '姓名不能为空!'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		  });
		return false;
	}

	if ($("#areashow").val() == "" || $("#areashow").val() == null) {
			 layer.open({
				    content: '地址不能为空!'
				    ,skin: 'msg'
				    ,time: 2 //2秒后自动关闭
				  });
			return false;
		
	}

	if (area_info.length == 0) {
		 layer.open({
			    content: '详细地址不能为空!'
			    ,skin: 'msg'
			    ,time: 2 //2秒后自动关闭
			  });
		return false;
	}

	if (mobile.length == 0) {
		layer.open({
		    content: '联系手机不能为空!'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		  });
		return false;
	} else if (mobile.length > 0) {
		mobilee(mobile);
	}
}
var flag = false; //防止重复提交
function mobilee(mobile) {
	var telephone = document.getElementById("telephone").value;
	if (flag) {
		alert("数据提交中，请耐心等待!");
		return false;
	}
	var myreg = /^((1[0-9]{2})+\d{8})$/;
	if (!myreg.test(mobile)) {
		layer.open({
		    content: '请输入有效的手机号码！'
		    ,skin: 'msg'
		    ,time: 2 //2秒后自动关闭
		  });
		return false;
	}
	
	
	if(telephone.length>0){
		var telephoneRule = /^[0-9]+$/;
		if (!telephoneRule.test(telephone)) {
			layer.open({
			    content: '只能填入数字！'
			    ,skin: 'msg'
			    ,time: 2 //2秒后自动关闭
			  });
			flag = false;
			return false;
		}
	}

	document.getElementById("addForm").submit();

}
