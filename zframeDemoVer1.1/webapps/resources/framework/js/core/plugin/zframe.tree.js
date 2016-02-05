$.extend($.fn.tree.methods,{
	unSelect:function(jq,target){
		return jq.each(function(){
			$(target).removeClass("tree-node-selected");
		});
	}
});