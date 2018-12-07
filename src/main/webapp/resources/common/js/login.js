/**
 * Created by billJiang on 2017/1/12. 登录异常信息显示
 */

function LoginValidator(config) {
	this.code = config.code;
	this.message = config.message;
	this.username = config.username;
	this.password = config.password;
	this.captcha = config.captcha;
	this.initValidator();
}

// 0 未授权 1 账号问题 2 密码错误 3 账号密码错误
LoginValidator.prototype.initValidator = function() {
	if (!this.code)
		return;
	if (this.code == 0) {
		this.addPasswordErrorMsg();
	} else if (this.code == 1) {
		this.addUserNameErrorStyle();
		this.addUserNameErrorMsg();
	} else if (this.code == 2) {
		this.addPasswordErrorStyle();
		this.addPasswordErrorMsg();
	} else if (this.code == 3) {
		this.addUserNameErrorStyle();
		this.addPasswordErrorStyle();
		this.addPasswordErrorMsg();
	} else if (this.code == 4) {
		this.addCaptchaErrorStyle();
		this.addCaptchaErrorMsg();
	}
	return;
}

LoginValidator.prototype.addUserNameErrorStyle = function() {
	this.addErrorStyle(this.username);
}

LoginValidator.prototype.addPasswordErrorStyle = function() {
	this.addErrorStyle(this.password);
}

LoginValidator.prototype.addCaptchaErrorStyle = function() {
	this.addErrorStyle(this.captcha);
}

LoginValidator.prototype.addUserNameErrorMsg = function() {
	this.addErrorMsg(this.username);
}

LoginValidator.prototype.addPasswordErrorMsg = function() {
	this.addErrorMsg(this.password);
}

LoginValidator.prototype.addCaptchaErrorMsg = function() {
	this.addErrorMsg(this.captcha);
}

LoginValidator.prototype.addErrorMsg=function(field){
    $("input[name='"+field+"']").parent().append('<small  data-bv-validator="notEmpty" data-bv-validator-for="'+field+'" class="help-block">' + this.message + '</small>');
}

LoginValidator.prototype.addErrorStyle=function(field){
    $("input[name='" + field + "']").parent().addClass("has-error");
}
