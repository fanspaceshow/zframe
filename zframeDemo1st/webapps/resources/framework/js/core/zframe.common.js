var StringUtil = {};
var DateUtil = {};
(function(){
    String.prototype.startWith=function(str){     
	var reg=new RegExp("^"+str);     
	return reg.test(this);        
};
String.prototype.endWith=function(str){     
	var reg=new RegExp(str+"$");     
	return reg.test(this);        
};
String.prototype.toJSON = function(){
	try {
		return jQuery.parseJSON(this);
	} catch (e) {
		return null;
	}
};
//去除前后空格
String.prototype.trim = function(){
	return jQuery.trim(this);
};
//去除所有空格
String.prototype.noSpace = function(){ 
	return this.replace(/[\s]{2,}/g, ""); 
};
/**
 *日期对比 
 * 时间格式为 yyyy-MM-dd
 * @param {Object} startDate 开始时间
 * @param {Object} endDate 结束时间
 * @return =：相等 >：大于 <：小于
 */
DateUtil.compareDate = function(startDate,endDate){
    var arys1= new Array();     
    var arys2= new Array();     
    if(startDate != null && endDate != null) {     
        var sDate= null;
        if(typeof(startDate) == "object"){
            sDate = startDate;
        }else{
             arys1=startDate.split('-');
            sDate = new Date(arys1[0],parseInt(arys1[1]-1),arys1[2]);     
        }
        var eDate = null;
        if(typeof(endDate) == "object"){
            eDate = endDate;
        }else{
             arys2=endDate.split('-');     
             eDate = new Date(arys2[0],parseInt(arys2[1]-1),arys2[2]);    
        }
        
        if(sDate > eDate) {     
            return ">";        
        }else if(sDate < eDate) {  
            return "<";     
        }else{
            return "=";
        }
    } 
};
/**
 *时间对比
 * 时间格式为 yyyy-MM-dd HH:mm:ss 
 * @param {Object} startDate 开始时间
 * @param {Object} endDate 结束时间
 * @return =：相等 >：大于 <：小于
 */
DateUtil.compareDateTime = function(startDate,endDate){
    if (startDate != null && endDate != null) {
        var allStartDate = null;
        var allEndDate = null;
        if(typeof(startDate) == "object"){
            allStartDate = startDate;
        }else{
            var startDateTemp = startDate.split(" ");
            var arrStartDate = startDateTemp[0].split("-");
            var arrStartTime = startDateTemp[1].split(":");
            allStartDate = new Date(arrStartDate[0], arrStartDate[1], arrStartDate[2], arrStartTime[0], arrStartTime[1], arrStartTime[2]);
        }
        if(typeof(endDate) == "object"){
            allEndDate = endDate;
        }else{
            var endDateTemp = endDate.split(" ");  
            var arrEndDate = endDateTemp[0].split("-");  
            var arrEndTime = endDateTemp[1].split(":");  
            allEndDate = new Date(arrEndDate[0], arrEndDate[1], arrEndDate[2], arrEndTime[0], arrEndTime[1], arrEndTime[2]);  
        }
        if(allStartDate.getTime() > allEndDate.getTime()) {  
            return ">";  
        }else if(allStartDate.getTime() < allEndDate.getTime()){  
            return "<";  
        }else{
            return "=";
        }
    }else{
        return false;  
    }  
};
DateUtil.parse = function(str){
    return new Date(Date.parse(str));
};
DateUtil.addDate = function(d1,days){
  var d = null;
  if(typeof(d1) == "object")
    d = d1;
  d = DateUtil.parse(d1);
  d = d.valueOf();
  d = d + days * 24 * 60 * 60 * 1000;
  return new Date(d);
};
DateUtil.format = function(d,pattern){
    JS.importPluginJS("text/SimpleDateFormat.js");
    return new SimpleDateFormat(pattern).format(d);
};
DateUtil.formatNow = function(d,pattern){
    JS.importPluginJS("text/SimpleDateFormat.js");
    return new SimpleDateFormat(pattern).formatNow();
};
})(jQuery);