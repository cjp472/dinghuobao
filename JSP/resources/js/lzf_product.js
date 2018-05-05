// JavaScript Document
var lzf={
    init:function(){
	  this.seAll();
	  this.xl("#t_wzdh","#t_wzdh_mian");
	  this.xl("#t_help","#t_help_mian");
	  
	  
	  this.newtj();
	  //this.ilc("#cat_xs");
	  this.iTop();
	  this.allNav();
      this.rwm();
	},
	
	
   seAll:function(){/*****搜索框全部分类***/
      var oseach_all_fl=$("#seach_all_fl");
	  var oseach_li=$("#seach_all_fl ul:first");
	  oseach_all_fl.mouseover(function(){
	    oseach_li.show();
	  });
	  
	  oseach_all_fl.mouseout(function(){
	    oseach_li.hide();
	  });
    },
	
	xl:function(obj1,obj2){//头部下拉菜单
       $(obj1).mouseover(function(){
		   $(this).addClass("hover");
	       $(obj2).show();
	   });
	   
	   $(obj1).mouseout(function(){
		   $(this).removeClass("hover");
	       $(obj2).hide();
	   });
	},
	
	
	newtj:function(){//新品推荐
	   $("#showcase .ws_l_mian:eq(0)").show();
       $("#showcase .newtj_t").mouseover(function(){
		  $("#showcase .newtj_t").removeClass("now");	
	      $(this).addClass("now");							  
	      $("#showcase .ws_l_mian").hide();
		  $("#showcase .ws_l_mian:eq("+$(this).index()+")").show();
	   });
	},
	
	
	ilc:function(op){//楼层
       var aBut=$(op+" .i_t");
	   var aM=$(op+" .smc");
	   aM.eq(0).show();
	   aBut.mouseover(function(){
			aBut.removeClass("now");
	        $(this).addClass("now");
			aM.hide();
			aM.eq($(this).index()).show();
	   });
	},
	
	
	
	iTop:function(){
		
      $("#toTop").css("top",($(window).height()-$("#toTop").height())/2);
	  $("#toTop .close").click(function(){
	      $("#toTop").hide();
	  });
	  
	  $("#toTop .fhdb").hide();  

	  $(window).scroll(function(){
	     if($(window).scrollTop()>$(window).height()){
		    $("#toTop .fhdb").show();
		 }else{
			$("#toTop .fhdb").hide();  
		 }
		 $("#toTop").css("top",($(window).height()-$("#toTop").height())/2);
	  });
	  
	  
	},
	
	allNav:function(){
	    $("#a_sw_nav").mouseover(function(){
		    $("#all_nav").show();
		});
		$("#a_sw_nav").mouseout(function(){
		    $("#all_nav").hide();
		});
	},
	
	
	rwm:function(){
	   $("#rwm .close").click(function(){
	      $("#rwm").hide();
	   });
	}

	

};

$(function(){
   lzf.init();
})

function getRootPath(){
    var curPath=window.document.location.href;
    var pathName=window.document.location.pathname;
    var pos=curPath.indexOf(pathName);
    var localhostPaht=curPath.substring(0,pos);
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    return(localhostPaht);
}
/**
 * @2014.7.16
 * 在所有页面统一写入以下脚本，在弹窗时使用
 */
document.write("<link href='"+webPath+"/resources/shopstyle/common/css/AeroDialog.css' type='text/css' rel='stylesheet' />");
document.write("<script src='"+webPath+"/resources/js/jquery-ui-1.8.21.js'></script>");
document.write("<script src='"+webPath+"/resources/shopstyle/common/js/AeroDialog.js'></script>");