window.confirm = function(c, t, f) {
	if (!t)
		t = '确认';
	parent.AeroDialog.confirm(c, t, f);
}

/**
 * 删除确认窗口
 */
function doDelete() {
	var url = arguments[0];
	var title = arguments[1];
	var type = arguments[2];
	if(!type)
		type='_self';
	confirm(title,'',function(a) {
		if (a == 'ok')
			window.open(url, type);
	});
}