var Brewer       = Brewer || {};

Brewer.MaskMoney = (function() {
	
	function MaskMoney() {
		this.decimal = $('.js-decimal');
		this.plain   = $('.js-plain');
	}
	
	MaskMoney.prototype.enable = function() {
		this.decimal.maskNumber({ decimal: ',', thousands: '.' });
		this.plain.maskNumber({ integer: true, thousands: '.' });
	}
	
	return MaskMoney;
	
}());

$(function() {
	var maskMoney       = new Brewer.MaskMoney();
	maskMoney.enable();
});