jQuery(document).ready(function(){

	var now = new Date(); //当前日期 
	var nowDayOfWeek = now.getDay() - 1; //今天本周的第几天 
	var nowDay = now.getDate(); //当前日 
	var nowMonth = now.getMonth(); //当前月 
	var nowYear = now.getFullYear(); //当前年 
	
    $("#benzhou").click(function(){
    	//获得本周的开端日期
    	var weekStartDate = new Date(nowYear, nowMonth, nowDay - nowDayOfWeek);
    	$("#beginTime").val(formatDate(weekStartDate));
    	
    	//获得本周的停止日期
    	var weekEndDate = new Date(nowYear, nowMonth, nowDay + (6 - nowDayOfWeek)); 
    	$("#endTime").val(formatDate(weekEndDate));
   	});
	
    $("#benyue").click(function(){
    	//获得本月的开端日期
    	var monthStartDate = new Date(nowYear, nowMonth, 1);
    	$("#beginTime").val(formatDate(monthStartDate));
    	
    	//获得本月的停止日期
    	var monthEndDate = new Date(nowYear, nowMonth, getMonthDays(nowYear, nowMonth));
    	$("#endTime").val(formatDate(monthEndDate));
   	});
    
    $("#bennian").click(function(){
    	//获得本年的开端日期
    	$("#beginTime").val(nowYear+"-01-01");
    	
    	//获得本年的停止日期
    	$("#endTime").val(nowYear+"-12-31");
   	});
  
});

//格局化日期：yyyy-MM-dd 
function formatDate(date) { 
	var myyear = date.getFullYear(); 
	var mymonth = date.getMonth()+1; 
	var myweekday = date.getDate(); 
	if(mymonth < 10){ mymonth = "0" + mymonth; } 
	if(myweekday < 10){ myweekday = "0" + myweekday; } 
	return (myyear+"-"+mymonth + "-" + myweekday);
}
//获得某月的天数 
function getMonthDays(nowYear, myMonth){ 
	var monthStartDate = new Date(nowYear, myMonth, 1); 
	var monthEndDate = new Date(nowYear, myMonth + 1, 1); 
	var days = (monthEndDate - monthStartDate)/(1000 * 60 * 60 * 24); 
	return days; 
}
