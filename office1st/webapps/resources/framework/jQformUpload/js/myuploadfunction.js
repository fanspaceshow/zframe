$(function () {
    $('#fileupload').fileupload({
        dataType: 'json',
 
        done: function (e, data) {
        	var result = jQuery.parseJSON(data);
			console.log(result);
	   		if(result.success == true){
	        	top.Notiy.success("导入成功!");
	        	win_ImportExcel.setReturnValue(true);
	        	win_ImportExcel.close();
	        }else if(result.NoChanges){
	        	top.Notiy.info("您未做任何修改!");
	        	win_ImportExcel.close();			        	
	        }else{
	        	top.Dialog.alert("错误",result.error,"error");
	        }

	   		win_ImportExcel.find("#ok").linkbutton("reset");
        },
 
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .bar').css(
                'width',
                progress + '%'
            );
        },
 
        dropZone: $('#dropzone')
    });
});