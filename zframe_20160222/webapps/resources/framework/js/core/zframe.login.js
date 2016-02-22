$(function() {
	// 居中
	$('.login_main').center();
	document.getElementById("username").focus();
	$("#username").keydown(function(event) {
		if (event.keyCode == 13) {
			login();
		}
	});
	$("#password").keydown(function(event) {
		if (event.keyCode == 13) {
			login();
		}
	});

});
// 登录
function login() {
	var errorMsg = "";
	var loginName = document.getElementById("username");
	var password = document.getElementById("password");
	if (!loginName.value) {
		errorMsg += "用户名不能为空!";
	}
	if (!password.value) {
		errorMsg += "密码不能为空!";
	}
	if (errorMsg != "") {
		Dialog.alert("提示信息",errorMsg,"info",function(){
			if (!loginName.value) {
				loginName.focus();
			}else{
				password.focus();
			}
		});
	} else {
	}
}