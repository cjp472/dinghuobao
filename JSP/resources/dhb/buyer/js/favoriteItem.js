
$(function(){
    //防止重复提交
    var flag = true;
    // 设置菜单焦点
    $(".menu-item").removeClass("item-active");
    $(".favorite_menu").addClass("item-active");

   // favoriteItem.findList(1,favoriteItem.pageSize,true);
    // 图片HTTPS
    String.prototype.protocol = function(){
        var str = this;
        str = window.isSupportWebp ? (str.replace(/(\.jpg|\.png)/g, ".webp")) : str;
        return str.replace(/http:\/\/p\d\.qh[imgs]{3}/,"https://p.ssl.qhmsg");
    };
    String.prototype.authorProtocol = function(){
        var str = this.replace("quc.qhimg", "p8.qhimg");
        str = window.isSupportWebp ? (str.replace(/(\.jpg|\.png)/g, ".webp")) : str;
        return str.replace(/http:\/\/p\d\.qh[imgs]{3}/,"https://p.ssl.qhmsg");
    };

// 图片剪裁
    String.prototype.drImage = function(w, h) {
        var url = this;
        var httpReg = /^http:\/\/p\d*\.qh(img|msg)\.com\//;
        var httpsReg = /^https:\/\/p\d*\.ssl\.qh(img|msg)\.com\//;
        h = h || w;

        if( httpReg.test(url) ) {
            return url.replace(httpReg, function(all) {
                return all + 'dr/' + [w, h, '/'].join('_')
            });
        } else if( httpsReg.test(url) ) {
            return url.replace(httpsReg, function(all) {
                return all + 'dr/' + [w, h, '/'].join('_');
            });
        } else {
            return url;
        }
    };
    var itembox = null,
        itemIndex = null;
    $(".favorite_list").on("mouseenter",".favorite_item_img",function(){
        itembox=$(this).parents(".favorite_item");
        itemIndex = $(this).parent().index();
        itembox.find(".favorite_item_btn_box").css("visibility","visible");
        itembox.siblings().find(".favorite_item_btn_box").css("visibility","hidden");
    });
    $(".favorite_list").on("mouseenter",".favorite_item_name",function(){
        itembox=$(this).parents(".favorite_item");
        itemIndex = $(this).parent().index();
        itembox.find(".favorite_item_btn_box").css("visibility","visible");
        itembox.siblings().find(".favorite_item_btn_box").css("visibility","hidden");
    });
    $(".favorite_list").on("mouseleave","li.favorite_item",function(){
        $(this).find(".favorite_item_btn_box").css("visibility","hidden");
    });

    /*点击取消按钮*/
    $(".favorite_list").on("click",".favorite_cancelbtn",function(event){
        $(this).parent().next().show();
    });
    /*点击取消-取消按钮*/
    $(".favorite_list").on("click",".qxsc_btn",function(event){
        $(this).parent().parent().hide();
    });

 

});

