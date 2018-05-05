/*!
 * TouchSlide v1.1
 * javascript����������Ч������ƶ��˻�����Ч����������ͼ������Tab�л���������ͼ�л���
 * �꾡��Ϣ�뿴������http://www.SuperSlide2.com/TouchSlide/
 *
 * Copyright 2013 ����ϯ
 *
 * ������ԭ��������ͷ����Ȩ
 * �ڱ�����Ȩ��ǰ���¿�Ӧ���ڸ��˻���ҵ��;

 * 1.1 �������Ӧ���޸���׿����ʱ������Χ�����bug��
 */
var TouchSlide = function(a){
	
	a = a||{};
	var opts = {
		slideCell:a.slideCell || "#touchSlide", //����Ч�������󣬱�����id�������� slideCell:"#touchSlide"
		titCell:a.titCell || ".hd li", // �������󣬵��Զ���ҳ��ΪtrueʱΪ��������������㡱
		mainCell:a.mainCell || ".bd", // �л����������
		effect:a.effect || "left", // Ч����֧�� left��leftLoop
		autoPlay:a.autoPlay || false, // �Զ�����
		delayTime:a.delayTime || 200, // Ч������ʱ��
		interTime:a.interTime ||2500, // �Զ����м��
		defaultIndex:a.defaultIndex ||0, // Ĭ�ϵĵ�ǰλ��������0�ǵ�һ���� defaultIndex:1 ʱ���൱�ڴӵ�2����ʼִ��
		titOnClassName:a.titOnClassName ||"on", // ��ǰ����������ӵ�className
		autoPage:a.autoPage || false, // �Զ���ҳ����ΪtrueʱtitCellΪ��������������㡱
		prevCell:a.prevCell ||".prev", // ǰһҳ��ť
		nextCell:a.nextCell ||".next", // ��һҳ��ť
		pageStateCell:a.pageStateCell ||".pageState", // ��ҳ״̬����������ʾ��ҳ״̬�����磺2/3
		pnLoop:a.pnLoop=='undefined '?true:a.pnLoop , // ǰ��ť����Ƿ����ִ��Ч������Ϊ��ǰ/��ҳ�ǻ��Զ���ӡ�prevStop��/��nextStop��������ɫ
		startFun:a.startFun || null, // ÿ���л�Ч����ʼʱִ�к��������ڴ�����������򴴽�����Ч�����÷� satrtFun:function(i,c){ }�� ����iΪ��ǰ��ҳ��cΪ��ҳ��
		endFun:a.endFun || null, // ÿ���л�Ч������ʱִ�к��������ڴ�����������򴴽�����Ч�����÷� endFun:function(i,c){ }�� ����iΪ��ǰ��ҳ��cΪ��ҳ��
		switchLoad:a.switchLoad || null //ÿ���л�Ч������ʱִ�к��������ڴ�����������򴴽�����Ч�����÷� endFun:function(i,c){ }�� ����iΪ��ǰ��ҳ��cΪ��ҳ��
	};

	var slideCell = document.getElementById(opts.slideCell.replace("#",""));
	if( !slideCell ) return false;


	//��ģ��jqueryѡ����
	var obj = function(str,parEle){ 
		str = str.split(" ");
		var par = [];
		parEle = parEle||document;
		var retn = [ parEle ] ;
		for( var i in str ){ if(str[i].length!=0) par.push(str[i]) } //ȥ���ظ��ո�
		for( var i in par ){ 
			if( retn.length==0 ) return false;
			var _retn = [];
			for ( var r in retn )
			{
				if( par[i][0] =="#" ) _retn.push( document.getElementById( par[i].replace("#","") ) );
				else if( par[i][0] =="." ){
					var tag = retn[r].getElementsByTagName('*');
					for( var j=0; j<tag.length; j++ ){
						var cln = tag[j].className;
						if( cln && cln.search(new RegExp("\\b" + par[i].replace(".","") + "\\b"))!=-1 ){ _retn.push( tag[j] ); }
					}
				}
				else { var tag = retn[r].getElementsByTagName( par[i] ); for( var j=0; j<tag.length; j++ ){ _retn.push( tag[j] ) } }
			}
			retn =_retn;
		}
		
		return retn.length==0 || retn[0] == parEle ? false:retn;
	};// obj E

	// ����������
	var wrap = function(el, v){
			var tmp = document.createElement('div');
			tmp.innerHTML = v;
			tmp = tmp.children[0];
			var _el = el.cloneNode(true);
			tmp.appendChild(_el);
			el.parentNode.replaceChild(tmp, el);
			conBox = _el; // ����conBox
			return tmp;
	};

	// ��ȡ��ɫ��ֵ
	var getStyleVal =function(el, attr){ var v=0; if(el.currentStyle){ v= el.currentStyle[attr] } else { v= getComputedStyle(el,false)[attr]; } return parseInt(v.replace("px","")) } ;

	// class����
	var addClass =function(ele, className){
		 if (!ele || !className || (ele.className && ele.className.search(new RegExp("\\b" + className + "\\b")) != -1)) return;
		 ele.className += (ele.className ? " " : "") + className;
	};

	var removeClass = function(ele, className){
		 if (!ele || !className || (ele.className && ele.className.search(new RegExp("\\b" + className + "\\b")) == -1)) return;
		 ele.className = ele.className.replace(new RegExp("\\s*\\b" + className + "\\b", "g"), "");
	};

	//ȫ�ֶ���
	var effect = opts.effect;
	var prevBtn = obj( opts.prevCell,slideCell )[0];
	var nextBtn = obj( opts.nextCell,slideCell )[0];
	var pageState = obj( opts.pageStateCell )[0];
	var conBox = obj( opts.mainCell,slideCell )[0];//����Ԫ�ظ������
	if( !conBox ) return false;
	var conBoxSize= conBox.children.length;
	var navObj = obj( opts.titCell,slideCell );//������Ԫ�ؽ��
	var navObjSize = navObj?navObj.length:conBoxSize;
	var sLoad=opts.switchLoad;

	/*�ַ���ת��*/
	var index=parseInt(opts.defaultIndex);
	var delayTime=parseInt(opts.delayTime);
	var interTime=parseInt(opts.interTime);
	var autoPlay = (opts.autoPlay=="false"||opts.autoPlay==false)?false:true;
	var autoPage = (opts.autoPage=="false"||opts.autoPage==false)?false:true;
	var loop = (opts.pnLoop=="false"||opts.pnLoop==false)?false:true;
	var oldIndex = index;
	var inter=null;// autoPlay��setInterval
	var timeout = null; // leftLoop��setTimeout
	var endTimeout = null;  //translate��setTimeout
	
	var startX = 0;
	var startY = 0;
	var distX = 0;
	var distY = 0;
	var dist = 0; //��ָ��������
	var isTouchPad = (/hp-tablet/gi).test(navigator.appVersion);
	var hasTouch = 'ontouchstart' in window && !isTouchPad;
	var touchStart = hasTouch ? 'touchstart' : 'mousedown';
	//var touchMove = hasTouch ? 'touchmove' : 'mousemove';
	var touchMove = hasTouch ? 'touchmove' : '';
	var touchEnd = hasTouch ? 'touchend' : 'mouseup';
	var slideH=0;
	var slideW= conBox.parentNode.clientWidth;// mainCell��������
	var twCell;
	var scrollY ;
	var tempSize = conBoxSize;
	
	//�����ҳ
	if( navObjSize==0 )navObjSize=conBoxSize;
	if( autoPage ){
		navObjSize=conBoxSize;
		navObj=navObj[0];
		navObj.innerHTML=""; 
		var str="";

		if( opts.autoPage==true|| opts.autoPage=="true" ){ for( var i=0; i<navObjSize; i++ ){ str+="<li>"+(i+1)+"</li>" } }
		else{ for( var i=0; i<navObjSize; i++ ){ str+=opts.autoPage.replace("$",(i+1))  } }
		
		navObj.innerHTML=str;
		navObj = navObj.children;//����navObj
	}

	
	
	if( effect == "leftLoop" ){
		tempSize +=2;
		conBox.appendChild( conBox.children[0].cloneNode(true) );
		conBox.insertBefore( conBox.children[conBoxSize-1].cloneNode(true),conBox.children[0] );
	}
	twCell = wrap(conBox,'<div class="tempWrap" style="overflow:hidden; position:relative;"></div>');
	conBox.style.cssText="width:"+tempSize*slideW+"px;"+"position:relative;overflow:hidden;padding:0;margin:0;";
	for ( var i =0; i<tempSize; i++ ){  conBox.children[i].style.cssText="display:table-cell;vertical-align:top;width:"+slideW+"px"  }
	

	var doStartFun=function(){ if ( typeof opts.startFun =='function' ){ opts.startFun( index,navObjSize ) } };
	var doEndFun=function(){ if (  typeof opts.endFun =='function' ){ opts.endFun( index,navObjSize ) } };
	var doSwitchLoad=function( moving ){ 
				var curIndex = ( effect=="leftLoop"?index+1:index ) + moving;
				var changeImg = function( ind ){
						var img = conBox.children[ind].getElementsByTagName("img");
						for ( var i=0; i<img.length ; i++ )
						{
							if ( img[i].getAttribute(sLoad) ){ 
								img[i].setAttribute("src", img[i].getAttribute(sLoad) ); 
								img[i].removeAttribute( sLoad );
							} 
						}
				}// changeImg E
				changeImg( curIndex );
				if( effect=="leftLoop" ){
					switch ( curIndex )
					{
						case 0: changeImg( conBoxSize );break;
						case 1: changeImg( conBoxSize+1 );break;
						case conBoxSize: changeImg( 0 );break;
						case conBoxSize+1: changeImg( 1 );break;
					}
				}
	};// doSwitchLoad E

	//��̬���û������
	var orientationChange = function(){ 
		slideW = twCell.clientWidth;
		conBox.style.width = tempSize*slideW +"px";
		for ( var i =0; i<tempSize; i++ ){  conBox.children[i].style.width=slideW+"px";  }
		var ind = effect == "leftLoop"? index+1:index;
		translate(  -ind*slideW ,0 );
	} ;
	window.addEventListener("resize", orientationChange, false); 


	//����Ч��
	var translate = function( dist, speed, ele ) {
		if( !!ele ){ ele=ele.style; }else{ ele=conBox.style; }
		ele.webkitTransitionDuration =  ele.MozTransitionDuration = ele.msTransitionDuration = ele.OTransitionDuration = ele.transitionDuration =  speed + 'ms';
		ele.webkitTransform = 'translate(' + dist + 'px,0)' + 'translateZ(0)';
		ele.msTransform = ele.MozTransform = ele.OTransform = 'translateX(' + dist + 'px)';		
	};

	//Ч������
	var doPlay=function(isTouch){
		
		switch (effect)
		{
			case "left": 
				if ( index >= navObjSize) { index = isTouch?index-1:0; } else if( index < 0) { index = isTouch?0:navObjSize-1; } 
				if( sLoad!=null ){ doSwitchLoad(0) } 
				translate(  (-index*slideW),delayTime ); oldIndex=index; break;
				
				
			case "leftLoop":
				if( sLoad!=null ){ doSwitchLoad(0) }
				translate(  -(index+1)*slideW ,delayTime );
				if ( index==-1){ 
						timeout= setTimeout( function(){ translate( -navObjSize*slideW ,0 ); }, delayTime );
						index = navObjSize-1; 
				}
				else if( index==navObjSize ){ timeout= setTimeout( function(){ translate( -slideW ,0 ); }, delayTime );
						index = 0; 
				}
				oldIndex=index;
				break;// leftLoop end

		}//switch end
		doStartFun();
		endTimeout= setTimeout( function(){ doEndFun() }, delayTime );

		//����className
		for ( var i=0; i<navObjSize; i++ )
		{
			removeClass(navObj[i],opts.titOnClassName);
			if( i == index ){ addClass(navObj[i],opts.titOnClassName) }
		}

		if( loop==false ){ //loop�����Ƿ����ѭ��
			removeClass( nextBtn,"nextStop" );removeClass( prevBtn,"prevStop" );
			if (index==0 ){ addClass( prevBtn,"prevStop" ) }
			else if (index==navObjSize-1 ){ addClass( nextBtn,"nextStop" ) }
		}
		if(pageState){ pageState.innerHTML= "<span>"+(index+1)+"</span>/"+navObjSize; }

	};// doPlay end

	//��ʼ��ִ��
	doPlay();
	
	//�Զ�����
	if (autoPlay) {
		 inter=setInterval(function(){ index++; doPlay() }, interTime); 
	}

	//����¼�
	if( navObj ){
		for ( var i=0; i<navObjSize; i++ )
		{		
			(function(){
				var j = i;
				navObj[j].addEventListener('click', function(e){ clearTimeout( timeout ); clearTimeout( endTimeout );  index=j; doPlay();  })
			})()
			
		}
	}
	if(nextBtn){ nextBtn.addEventListener('click', function(e){ if ( loop==true || index!=navObjSize-1 ){ clearTimeout( timeout ); clearTimeout( endTimeout ); index++; doPlay(); } }) }
	if(prevBtn){ prevBtn.addEventListener('click', function(e){ if ( loop==true || index!=0 ){ clearTimeout( timeout ); clearTimeout( endTimeout ); index--; doPlay(); } }) }



	//������ʼ����
	var tStart = function(e){
		clearTimeout( timeout );clearTimeout( endTimeout );
		scrollY = undefined;
		distX = 0;
		var point = hasTouch ? e.touches[0] : e;
		startX =  point.pageX;
		startY =  point.pageY;

		//��ӡ������ƶ����¼�����
		conBox.	addEventListener(touchMove, tMove,false);
		//��ӡ������������¼�����
		conBox.addEventListener(touchEnd, tEnd ,false);
	};

	//�����ƶ�����
	var tMove = function(e){
		if( hasTouch ){ if ( e.touches.length > 1 || e.scale && e.scale !== 1) return }; //��������

		var point = hasTouch ? e.touches[0] : e;
		distX = point.pageX-startX;
		distY = point.pageY-startY;

		if ( typeof scrollY == 'undefined') { scrollY = !!( scrollY || Math.abs(distX) < Math.abs(distY) ); }
		if( !scrollY ){ 
			e.preventDefault(); if(autoPlay){clearInterval(inter) }
			switch (effect){
				case "left":
					if( (index==0 && distX>0) || (index>=navObjSize-1&&distX<0 )){ distX=distX*0.4 }
					translate( -index*slideW+distX ,0 );
				break;
				case "leftLoop":translate( -(index+1)*slideW+distX ,0 );break;
			}
			
			if(  sLoad!=null && Math.abs(distX)>slideW/3 ){ 
				doSwitchLoad( distX>-0?-1:1 ) 
			}
		}
	};

	//������������
	var tEnd = function(e){
		if(distX==0) return;
		e.preventDefault(); 
		if( !scrollY )
		{
			if( Math.abs(distX) > slideW/10  ){ distX>0? index--: index++; }
			doPlay( true );  
			if (autoPlay) {
				 inter=setInterval(function(){ index++; doPlay() }, interTime); 
			}
		}

		conBox.removeEventListener(touchMove, tMove, false);
		conBox.removeEventListener(touchEnd, tEnd, false);
	};


	//��ӡ�������ʼ���¼�����
	conBox.addEventListener(touchStart, tStart ,false);


};// TouchSlide E

