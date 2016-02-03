var DateUtil = {
    format:function(o){
        var s = "";
        if(typeof(o) == "string"){
            s = o;
        }else{
            s = o.value;
        }
        if(s.length == 4){
            o.value = s+"-";
        }
        if(s.length == 7){
            o.value = s+"-";
        }
        if(s.length > 10){
            o.value = s.substring(0,10);
        }
    }
};
