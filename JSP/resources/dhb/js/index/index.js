var initNav = (function(){
    $('.side-category .sub-item').mouseenter(function(){
    //    $(this).find('.left-sub-list').show().find('img').trigger('porty');
    	 $(this).find('.left-sub-list').show();
    }).mouseleave(function(){
        $(this).find('.left-sub-list').hide();
    });
   /* $('.side-category img').lazyload({
        effect:'fadeIn',
        event:'porty'
    });*/
});



// 首页幻灯切换
var indexSlide = function(){
    this.eleBox;
    this.eleList;
    this.elePoints;
    this.playLock=false;
    this.playDuration = 500;
    this.index = 0;
    this.eleLength;
    this.playDwell = 5000; //停留时间
};
indexSlide.prototype.play = function(){
    var self = this;
        this.autoPlayInterval = setInterval(function(){
            self.next();
        },self.playDwell)
    }
indexSlide.prototype.go = function(index){
        var self = this;
        var curr = self.eleList.children().eq(index);

        self.updateBanner(curr);
        curr.fadeIn(self.playDuration);

        self.eleList.children().not(curr).fadeOut(self.playDuration,function(){
            self.playLock = false;
        });

        self.updatePoint(index);
    }

indexSlide.prototype.next = function(){
        if(this.playLock) return;
        this.playLock = true;

        this.index = (this.index+1)%this.eleLength;

        this.go(this.index);
    }

indexSlide.prototype.prev = function(){
        if(this.playLock) return;
        this.playLock = true;

        this.index = (this.index-1+this.eleLength)%this.eleLength;

        this.go(this.index);
    }

indexSlide.prototype.showBtnAndbind = function(){
    var self = this;
        self.eleBox.find('.slide-next').show().click(function(){
            self.next();
        })

        self.eleBox.find('.slide-prev').show().click(function(){
            self.prev();
        })

        self.eleBox.mouseenter(function(){
            clearInterval(self.autoPlayInterval)
        }).mouseleave(function(){
            self.play();
        })

        self.elePoints.on('click','a',function(e){
            self.go($(this).index());
        })
    }

indexSlide.prototype.showPoint = function(offset){

        var html = [];
        for(var i=0; i < this.eleLength; i++){
            html[i] = '<a href="javascript:;" onclick="return false;"></a>';
        }
        this.elePoints.html(html.join('')).show();
        if(this.elePoints.css('position')=='absolute'){
            offset |=0;
            this.elePoints.css('marginLeft',(offset-this.elePoints.outerWidth())/2);
        }
        this.updatePoint(0);
    }

indexSlide.prototype.updatePoint = function(index){
        this.elePoints.find('a').removeClass('curr-point').eq(index).addClass('curr-point');
    }

indexSlide.prototype.updateBanner = function(ele){
        function getMiniUrl(url){
            url = url.replace("qhimg", "qhmsg");
            if(url.indexOf("qhmsg") == -1) return url;
            return  window.isSupportWebp ? url.replace(".jpg", ".webp") : url;
        }
        ele = ele? $(ele) : this.eleList.find('.slider-film').eq(0);
        try{
            ele.filter('.js-lazyload').trigger('porty');
            ele.find('.js-lazyload').trigger('porty');
        }catch(err){
            if(ele && ele.find('img')){
                var img = ele.find('img');
                img.each(function(){
                    var item = $(this);
                    if(item.data('original')){
                        item.attr('src',getMiniUrl(item.data('original'))).removeAttr('data-original');
                    }
                });
            }
            ele && ele.filter('.js-lazyload').length>0 && ele.data('original') && ele.css({backgroundImage:'url('+getMiniUrl(ele.data('original'))+')'}).removeAttr('data-original');
        }

    }

indexSlide.prototype.init = function(ele,dwell,offset,minShow){
        this.eleBox = $(ele);
        this.minShow=minShow;//最小显示数�?
        this.playDwell = dwell || this.playDwell;
        this.eleList = this.eleBox.find('.slideBox');
        this.eleLength = this.eleList.find('.slider-film').length;
        this.elePoints = this.eleBox.find(".slide-point");

        this.updateBanner();

        this.eleList.children().first().show();


        if(this.eleLength>1){
            this.showPoint(offset);
            this.showBtnAndbind();
            this.play();
        }else{
            this.eleBox.find('.slide-next,.slide-prev').hide();
            this.elePoints.hide();
        }
        this.eleList.find('.js-lazyload').lazyload({
            event:'porty'
        });
}

var countdown=function(cfg){
    var eleRemain=$('.countdown'),
        remain=0,
        callback=function(){},
        update=function(ele,time){
            ele.html(time.day+'�?'+time.hour+"�?"+time.minute+'�?'+time.second+'�?'+time.msecond);
        },startTime=+new Date(),
        msPrecision=false;//毫秒精度

    var init=function(cfg){
        var configN = cfg||{};
        eleRemain = $(configN.eleRemain);
        if(eleRemain){
            remain = parseInt(eleRemain.attr('data-countdown'));
        }
        update = configN.update || update;
        callback = configN.callback;
        msPrecision = configN.msPrecision;
        if(remain > 0){
            playCountdown();
        }
    }
    var updateView=function(newRemain){
        var showtime={
            msecond : Math.floor(newRemain%1000/10),
            second : Math.floor(newRemain/1000)%60,
            minute : Math.floor(newRemain/1000/60%60),
            hour : Math.floor(newRemain/1000/60/60%24),
            day : Math.floor(newRemain/1000/60/60/24)
        }
        if(typeof update ==="function"){
            update(eleRemain,showtime);
        }
    };
    var playCountdown=function(){
        var newRemain = remain*1000 + startTime - new Date();
        if(newRemain > 0){
            updateView(newRemain);
            setTimeout(playCountdown,msPrecision?30:300);
        }else{
            if(typeof callback ==="function"){
                callback(eleRemain);
            }
        }
    };
    init(cfg);
};

var initCountdown=function(){
    $('.countdown').each(function(i,obj){
         new countdown({
             eleRemain:$(obj),  //必需
             update:function(ele,time){ //可�?
                ele.parent().show();
                ele.html((time.day>0?('<i>'+time.day+'</i>�?'):'')+'<i>'+time.hour+"</i>�?<i>"+time.minute+'</i>�?<i>'+time.second+'</i>�?');
             },
             callback:function(ele){ //倒计时结束后执行方法
                ele.parent().hide();
             }
         });
     });
}


// 入口
var indexPage = (function(){

    var init = function(){
        // 首页左侧分类
        initNav();
      
        // banner切换
        new indexSlide().init('.banner-slide', 5000,10,1);

        // 普通栏目延时加�?
        $('img.js-lazyload').lazyload({
            threshold : 350
        });

        //延迟执行，优先首屏优�?
        setTimeout(function(){
            $('.part-suggest').each(function(i,obj){
                new indexSlide().init(obj, 5000,0,4);
            });
        },1000);

     //   initCountdown();

        //滚动后执行，懒加�?
        /**
         * 新品速递隐藏，暂时把这块加载屏�?
         */
        /*var lazyInitMod = function(){
            var newProductsDom = $('.part-newproducts');
            if(newProductsDom.length > 0 && $(window).scrollTop() >= newProductsDom.offset().top-$(window).height()-200){
                newProducts().init();
                $(window).off('scroll',lazyInitMod);
            }
        };

        $(window).on('scroll',lazyInitMod);*/

    }

    return{
        init : init
    }
})();

void function(){

    var resizePosition = function(){
            var winHeightHalf = ($(window).height())/2;
            var imgHalfw = -($('.popping img').width()/2);
            var imgHalfh = -($('.popping img').height()/2);

            if(imgHalfw == -0){
                setTimeout(resizePosition,200);
                return;
            }
            $('.popping').css('top',winHeightHalf);
            $('.popping').css('margin-left',imgHalfw + 'px');
            $('.popping').css('margin-top',imgHalfh + 'px');
    } ;
  //console.log($.cookie("mask"));
    // if(!$.jStorage.get("isNeedPopA")){
    //     $('.shade').show();
    //     $('.popping').show();
    //     resizePosition();

    // }
    // if(!$.jStorage.get("isNeedPopB")){
    //     $('.bshade').show();
    //     $('.botad').show();
    // }

    $('.close').click(function(){
      $(this).parent('.popping').hide();
      $('.shade').hide();
      $.jStorage.set("isNeedPopA", "opened", {TTL: 60 * 1000 * 60 * 24});
    })
    $('.closeb').click(function(){
      $(this).parent().parent('.botad').hide();
      $('.bshade').hide();
      $.jStorage.set("isNeedPopB", "opened", {TTL: 60 * 1000 * 60 * 24})
    })
}();
    //搜索框如�? 没有填写，则默认搜索placeholder
function  check_search_val(default_val,form){
    var custom_val=$(form).find('input[name="q"]').val();
    if(!custom_val){
        $(form).find('input[name="q"]').val(default_val);
    }
    return true;
}


function commonLogoutCallback(data) {
    return null;
}