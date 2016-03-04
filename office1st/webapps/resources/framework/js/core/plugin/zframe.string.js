String.prototype.startsWith = function(s){
	
};
String.prototype.endsWith = function(s){
	
};
String.prototype.trim = function(){
	
};
String.prototype.replaceAll = function(source,target){
	var val = this;
	while(val.indexOf(source)>-1){
		val = val.replace(source,target);
	}
	return val;
};