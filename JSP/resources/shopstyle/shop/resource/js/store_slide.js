// 幻灯片事件 
$(window).load(function() {
	$('.flexslider').flexslider();
});
$(function() {
	/* 删除图片 */
	$('a[nctype="del"]').unbind().click(
			function() {
				$(this).siblings(":first").attr("src", "");
				$(this).parent().parent().siblings(".ncsc-upload-btn").find(
						":file").val("");
				var id=$(this).attr("attrId");
				$("#del_"+id).val("1");
			});
});

/* 图片上传ajax */
function ajaxFileUpload(url, id, file_id) {
	$('div[nctype="' + id + '"]').find('i').remove().end().find('img').remove()
			.end().prepend(
					'<img nctype="' + id + '" scr="' + SHOP_TEMPLATES_URL
							+ '/images/loading.gif">');
	$('img[nctype="' + id + '"]').attr('src',
			SHOP_TEMPLATES_URL + "/images/loading.gif");

	$.ajaxFileUpload({
		url : url,
		secureuri : false,
		fileElementId : id,
		dataType : 'json',
		data : {
			name : 'logan',
			id : id,
			file_id : file_id
		},
		success : function(data, status) {
			if (typeof (data.error) != 'undefined') {
				alert(data.error);
				$('img[nctype="' + id + '"]').attr(
						'src',
						UPLOAD_SITE_URL + '/' + ATTACH_COMMON
								+ '/default_goods_image.gif');
			} else {
				$('input[nctype="' + id + '"]').val(data.file_name).attr(
						'file_id', data.file_id);
				$('img[nctype="' + id + '"]').attr(
						'src',
						UPLOAD_SITE_URL + '/' + ATTACH_STORE + '/slide/'
								+ data.file_name);
				$('#' + id).attr('file_id', data.file_id);
			}
			$
					.getScript(SHOP_RESOURCE_SITE_URL
							+ "/js/jquery.flexslider-min.js");
			$.getScript(SHOP_RESOURCE_SITE_URL + "/js/store_slide.js");
		},
		error : function(data, status, e) {
			alert(e);
			$.getScript(SHOP_RESOURCE_SITE_URL + "/js/store_slide.js");
		}
	})
	return false;

}
