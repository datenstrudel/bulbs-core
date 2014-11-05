/**
 * 
 * Translates between server side colors (e.g. net.datenstrudel.bulbs.shared.domain.model.color.Color)
 * and client side colors, which are types of D3 specific color objects like 
 * d3.hsl(..) or d3.rgb(..)
 * @returns {ColorConverter}
 */
function ColorConverter(){
    
    this.colorHsl = {};
    var self = this;

    this.fromServerColor = function fromServerColor(color){
        if(typeof(color.colorScheme) ==='undefined')throw new Error("Malicious color used to invoke 'fromServerColor': " + color);
//        if(typeof(color.COLOR_SCHEME) ==='undefined')color.COLOR_SCHEME = 'HSB';
        var res = new ColorConverter();
        switch(color.colorScheme){
            case 'RGB':
                res.colorHsl = d3.rgb(color.red, color.green, color.blue).hsl();
                if( isNaN(res.colorHsl.h) )res.colorHsl.h = 0;
                if( isNaN(res.colorHsl.s) )res.colorHsl.s = 0;
                if( isNaN(res.colorHsl.l) )res.colorHsl.l = 0;
                break;
            case 'HSB':
                res.colorHsl = d3.hsl(color.hue, color.saturation/255, color.brightness/255);
                break;
            default:
                throw new Error("Malicious color used to invoke 'fromServerColor': " + color);
        }
        return res;
    };
    this.toServerColor = function toServerColor(type){
        switch(type){
            case 'HSB':
                return {
                    colorScheme : 'HSB',
                    hue : Math.round(self.colorHsl.h),
                    brightness : Math.round( self.colorHsl.l * 255 ),
                    saturation : Math.round( self.colorHsl.s * 255 )
                };
            case 'RGB':
            default :
                throw new Error("Not supported yet!");
        }
    };
    
    //~ Factory Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    this.fromHslD3 = function fromHslD3(color){
        var res = new ColorConverter();
        res.colorHsl = color;
        return res;
    };
    this.fromRgbD3 = function fromRgbD3(color){
        var res = new ColorConverter();
        res.colorHsl = color.hsl();
        return res;
    };
}
