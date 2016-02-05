/**
 * combobox级联
 * 在需要级联的combobox上配置两个关键属性 with、link
 * with代表与其关联的combobx的id
 * link为本combobox获取数据用的url地址
 * 请求到后台传递的参数为 val、text val表示combbox的值，text表示combobox的显示值
 */
$.extend($.fn.combobox.methods,{
	cascade : function(){
		var lstCombo = $(".easyui-combobox[with][link]");
	    $.each(lstCombo,function(id,combo){
            var withName = $(this).attr("with");
	    	var url = $(this).attr("link");
	    	var into = $(this).attr("into");//定义关联的下拉框所处的位置,jquery表达式格式
	    	
	    	var area = $("body");
	    	if(into){
	    		area = $(into);
	    	}
	    	//查找关联的下拉框
	    	var withCombo = area.find(".easyui-combobox[id='"+withName+"']");
	    	if(withCombo[0]){
				//设置onChange事件
	    		withCombo.combobox({onChange:function(nVal,oVal){
	    			var val = $(withCombo).combobox("getValue");
	    			var text = $(withCombo).combobox("getText");
	    			$(combo).combobox({onBeforeLoad:function(param){
	    				//绑定参数
	    				param.val = val;
	    				param.text = text;
	    			},onLoadSuccess:function(){
	    				//选中第一个
	    				var data = $(this).combobox("getData");
	    				if(data.length>0){
	    					$(this).combobox("select",data[0].value);
	    				}
	    			}});
	    			$(combo).combobox("reload",url);
	    		}});
	    	}
	    });
	}
});
$(function(){
	$.fn.combobox.methods.cascade();
});