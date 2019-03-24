$(document).ready(function() {
    $('#slide-menu').click(function() {
        $('body').toggleClass('moved');
        $('header').toggleClass('moved');
        $('#navMenu').toggleClass('moved');
    });
    $('#navClose').click(function() {
        $('body').removeClass('moved');
        $('header').removeClass('moved');
        $('#navMenu').removeClass('moved');
    });
    $('#slide-menu, #navMenu').clickoutside(function() {
        $('body').removeClass('moved');
        $('header').removeClass('moved');
        $('#navMenu').removeClass('moved');       
    });
});
/* for nav to not move plugin */

(function (jQuery) {
    jQuery.fn.clickoutside = function (callback) {
        var outside = 1, self = $(this);
        self.cb = callback;
        this.click(function () {
            outside = 0;
        });
        $(document).click(function () {
            outside && self.cb();
            outside = 1;
        });
        return $(this);
    }
})(jQuery);
