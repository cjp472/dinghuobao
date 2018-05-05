/**
 * 模仿的Map对象
 * @author zhaihl
 */
function Map() {
	//构建对象
    this._hash = new Object();
    //模仿keySet
    this.keySet = new Array();
    //模仿entrySet
    this.entrySet = new Array();
}

//添加元素
Map.prototype.put = function(key,value){
    if(typeof(key)!="undefined"){
        this._hash[key]=typeof(value)=="undefined"?null:value;//如果有值，则覆盖
        if(!this.containsKey(key)){
        	//如果指定键不存在，则将此键值放入keySet、entrySet
            this.keySet.push(key);
            this.entrySet.push(value);
        } else{
        	//如果存在该键，则更新entrySet的值
        	for(var i in this.entrySet){
	    		if(key==this.keySet[i]){
	    			this.entrySet[i] = value;//此键对应的entry值
	    		}
			}
        }
        return true;     
    } else {
    	throw new Error('the key is undefined');
    }
}

//移除元素
Map.prototype.remove = function(key){
	for(var i in this.keySet){
		if(this.keySet[i]==key){
			this.keySet.splice(i,1);
		}
	}
	for(var j in this.entrySet){
		if(this.entrySet[j]==this._hash[key]){
			this.entrySet.splice(j,1);
		}
	}
	delete this._hash[key];
}

//元素个数
Map.prototype.size = function(){
	var i=0;
	for(var k in this._hash){
		i++;
	} 
	return i;
}

//取元素
Map.prototype.get = function(key){
	return this._hash[key];
}

//是否包含指定值
Map.prototype.containsValue = function(key){
	 return typeof(this._hash[key])!="undefined";
}

Map.prototype.containsKey = function(key){
	 for(var i in this.keySet){
		if(this.keySet[i]==key){
			return true;
		}
	}
	return false;
}

//清空
Map.prototype.clear = function(){
	this.keySet = new Array();
    this.entrySet = new Array();
    
	for(var k in this._hash){
		delete this._hash[k];
	}
}

Map.prototype.isEmpty = function(){
	return this.size() == 0;
}

/**
 * 返回此map对象所有键值对形式的数组集合
 * [[key:value],[key:value]]
 */
Map.prototype.arraylist = function(){
	var list = new Array();
	for(var i in this.keySet){
		var val = this.keySet[i]+':'+this.entrySet[i];
		list.push(val);
	}
	return list;
}

/**
 * 获取此map的值集合，形式如val1,val2,val3
 */
Map.prototype.getValue = function(){
	var values = "";
	for(var i in this.entrySet){
		values += this.entrySet[i]+",";
	}
	return values.substring(0, values.length-1);
}