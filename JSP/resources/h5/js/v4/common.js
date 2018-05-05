/**
 * 公共js方法引用
 */
function writeClear(o) {
    if (o.val().length > 0) {
        o.parent().addClass('write');
    } else {
        o.parent().removeClass('write');
    }
    btnCheck(o.parents('form'));
}
function btnCheck(form) {
    var btn = true;
    form.find('input').each(function(){
        if ($(this).hasClass('no-follow')) {
            return;
        }
        if ($(this).val().length == 0) {
            btn = false;
        }
    });
    if (btn) {
        form.find('.btn').parent().addClass('ok');
    } else {
        form.find('.btn').parent().removeClass('ok');
    }
}

function input_del(obj){
	 $(obj).parent().removeClass('write').find('input').val('');
     btnCheck($(obj).parents('form'));
}

(function($) {
    $.extend($, {
        /**
         * 滚动header固定到顶部
         */
        scrollTransparent: function(options) {
            var defaults = {
                    valve : '#header',          // 动作触发
                    scrollHeight : 50
            }
            var options = $.extend({}, defaults, options);
            function _init() {
                $(window).scroll(function(){
                    if ($(window).scrollTop() <= options.scrollHeight) {
                        $(options.valve).addClass('transparent').removeClass('posf');
                    } else {
                        $(options.valve).addClass('posf').removeClass('transparent');
                    }
                });
                
            }
            return this.each(function() {
                _init();
            })();
        }
    });
})(Zepto);