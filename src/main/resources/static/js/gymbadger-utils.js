function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function shadeBlendConvert(p, from, to) {
    if(typeof(p)!="number"||p<-1||p>1||typeof(from)!="string"||(from[0]!='r'&&from[0]!='#')||(to&&typeof(to)!="string"))
    	return null;
    if(!this.sbcRip)this.sbcRip=(d)=>{
        let l=d.length,RGB={};
        if(l>9){
            d=d.split(",");
            if(d.length<3||d.length>4)
            	return null;
            RGB[0]=i(d[0].split("(")[1]),RGB[1]=i(d[1]),RGB[2]=i(d[2]),RGB[3]=d[3]?parseFloat(d[3]):-1;
        }else{
            if(l==8||l==6||l<4)
            	return null;
            if(l<6)
            	d="#"+d[1]+d[1]+d[2]+d[2]+d[3]+d[3]+(l>4?d[4]+""+d[4]:""); 
            d=i(d.slice(1),16),RGB[0]=d>>16&255,RGB[1]=d>>8&255,RGB[2]=d&255,RGB[3]=-1;
            if(l==9||l==5)
            	RGB[3]=r((RGB[2]/255)*10000)/10000,RGB[2]=RGB[1],RGB[1]=RGB[0],RGB[0]=d>>24&255;
        }
        return RGB;
    }
    var i=parseInt,r=Math.round,h=from.length>9,h=typeof(to)=="string"?to.length>9?true:to=="c"?!h:false:h,b=p<0,p=b?p*-1:p,to=to&&to!="c"?to:b?"#000000":"#FFFFFF",f=this.sbcRip(from),t=this.sbcRip(to);
    if(!f||!t)
    	return null;
    if(h)return "rgb"+(f[3]>-1||t[3]>-1?"a(":"(")+r((t[0]-f[0])*p+f[0])+","+r((t[1]-f[1])*p+f[1])+","+r((t[2]-f[2])*p+f[2])+(f[3]<0&&t[3]<0?")":","+(f[3]>-1&&t[3]>-1?r(((t[3]-f[3])*p+f[3])*10000)/10000:t[3]<0?f[3]:t[3])+")");
    else {
      var ret = "#"+(0x100000000+r((t[0]-f[0])*p+f[0])*0x1000000+r((t[1]-f[1])*p+f[1])*0x10000+r((t[2]-f[2])*p+f[2])*0x100+(f[3]>-1&&t[3]>-1?r(((t[3]-f[3])*p+f[3])*255):t[3]>-1?r(t[3]*255):f[3]>-1?r(f[3]*255):255)).toString(16).slice
(1,f[3]>-1||t[3]>-1?undefined:-2);
      return ret;
    }
}

function formatLabel(str, maxwidth){
    var sections = [];
    var words = str.split(" ");
    var temp = "";
    words.forEach(function(item, index){
        if(temp.length > 0) {
            var concat = temp + ' ' + item;
            if(concat.length > maxwidth) {
                sections.push(temp);
                temp = "";
            } else {
                if(index == (words.length-1)) {
                    sections.push(concat);
                    return;
                }
                else {
                    temp = concat;
                    return;
                }
            }
        }
        if(index == (words.length-1)) {
            sections.push(item);
            return;
        }
        if(item.length < maxwidth) {
            temp = item;
        }
        else {
            sections.push(item);
        }
    });
    return sections;
}

function getSpinnerHtml() {
    var html="<div class=\"sk-fading-circle\">" +
	  "<div class=\"sk-circle1 sk-circle\"></div>" +
	  "<div class=\"sk-circle2 sk-circle\"></div>" +
	  "<div class=\"sk-circle3 sk-circle\"></div>" +
	  "<div class=\"sk-circle4 sk-circle\"></div>" +
	  "<div class=\"sk-circle5 sk-circle\"></div>" +
	  "<div class=\"sk-circle6 sk-circle\"></div>" +
	  "<div class=\"sk-circle7 sk-circle\"></div>" +
	  "<div class=\"sk-circle8 sk-circle\"></div>" +
	  "<div class=\"sk-circle9 sk-circle\"></div>" +
	  "<div class=\"sk-circle10 sk-circle\"></div>" +
	  "<div class=\"sk-circle11 sk-circle\"></div>" +
	  "<div class=\"sk-circle12 sk-circle\"></div>" +
	"</div>";
    return html;
}