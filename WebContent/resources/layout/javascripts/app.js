$(function() {
	$('.js-toggle').bind('click', function(event) {
		$('.js-sidebar, .js-content').toggleClass('is-toggled');
		event.preventDefault();
	});	
	
	$('.js-menu > ul > li > a').bind('click', function(event) {
	  var subItems = $(this).parent().find('ul');

	  if (subItems.length) {
	    event.preventDefault();
	    $(this).parent().toggleClass('is-expanded');
	  }
	});

	$('.aw-menu__item .is-active').parents('.aw-menu__item').addClass('is-expanded is-active');
});
