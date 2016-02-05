var _isInitButtonPrrmissioned = false;
function _InitButtonPrrmission(){
	/*if(_isInitButtonPrrmissioned)
		return;*/
	try {
		var allButtons = $("#ResourceButtons").val();
		var userButtons = $("#UserResourceButtons").val();
		if(!allButtons)
			return;
		//全部禁用
		var array1 = allButtons.split(",");
		$.each(array1,function(i,n){
			try {
				if(_ButtonPrrmission_EqBtn(n,userButtons)){
				    if(top.Config.Prmissioned.remove == true){
				        $("#"+n).remove();
                        //同时禁用右键菜单上with属性名称相同的菜单
                        var contextMenuItemss = $("div[with='"+n+"']");
                        for(var i=0;i<contextMenuItemss.length;i++){
                            var menu = $(contextMenuItemss[i]);
                            var contextMenu = $("#"+menu.parent().attr("id"));
                            contextMenu.menu("removeItem",menu);
                        }
				    }else{
				        $("#"+n).linkbutton("disable");
                        $("#"+n).unbind("click");
                        $("#"+n).unbind("dblclick");
                        $("#"+n).unbind("mouseover");
                        $("#"+n).unbind("mousemove");
                        $("#"+n).unbind("mouseout");
                        $("#"+n).unbind("onfocus");
                        $("#"+n).css("cursor","not-allowed");
                        //同时禁用右键菜单上with属性名称相同的菜单
                        var contextMenuItemss = $("div[with='"+n+"']");
                        for(var i=0;i<contextMenuItemss.length;i++){
                            var menu = $(contextMenuItemss[i]);
                            var contextMenu = $("#"+menu.parent().attr("id"));
                            contextMenu.menu("disableItem",menu);
                        }
				    }
					
				}
			} catch (e) {
				_isInitButtonPrrmissioned = false;
			}
		});
		_isInitButtonPrrmissioned = true;
	} catch (e) {
		_isInitButtonPrrmissioned = false;
	}
}
function _ButtonPrrmission_EqBtn(btn,btns){
	if(!btns)
		return true;
	var array = btns.split(",");
	for(var i=0;i<array.length;i++){
		if(btn==array[i]){
			return false;
		}
	}
	return true;
}
