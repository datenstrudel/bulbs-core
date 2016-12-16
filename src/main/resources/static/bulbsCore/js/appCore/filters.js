
angular.module('bulbFilters', [])
	.filter('bulbOnline', function() {
		return function(input) {
		  return input ? '\u2713' : '\u2718';
		};
	})
	.filter('commonOnline', function() {
		return function(input) {
		  return  input ? 'Online' : 'Offline';
		};
	})
	.filter('bulbColor_Textual_HSL', function() {
		return function(input) {
			var c = new ColorConverter().fromServerColor(input).colorHsl;
			var res = "H " + Math.round(c.h * 1)/1
					+ " | S " +Math.round(c.s * 100) / 100
					+ " | L " + Math.round(c.l * 100)/100;
			return res;
		};
	})
	.filter('millisToSeconds', function() {
        return function(input){
            return input / 1000;
        }
    })

	;
