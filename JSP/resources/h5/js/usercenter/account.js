

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
	}
}
function _close(){
	$('#areaSelected').find('.nctouch-full-mask').addClass('right').removeClass('left');
}
var flag = false;//防止重复提交
function check(){
	 if (flag) {
			alert("数据提交中，请耐心等待");
			return false;
		}
	flag=true;
	document.getElementById("ac_form").submit();
}
$(function(){
	 $('#birthday').datepicker({
		    dateFormat:"yy-mm-dd",
			yearRange:"1940:2030",
			changeMonth: true,
			changeYear: true
		});
})