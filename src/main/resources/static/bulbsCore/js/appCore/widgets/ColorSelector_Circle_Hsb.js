/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Circular HSB color selector.
 * @param {number} width
 * @param {type} parentDomElement
 * @param {function} colorChangedCb
 * @param {function} colorChangedOngoingCb
 * @returns {ColorSelector_Circle_Hsb}
 */
function ColorSelector_Circle_Hsb(width, parentDomElement, colorChangedCb, colorChangedOngoingCb){
	this.width = width;
	this.height = width;
	this.parentDomElement = parentDomElement;
	this.colorChangedCb = colorChangedCb;
	this.colorChangedOngoingCb = colorChangedOngoingCb;
	
	ColorSelector_Circle_Hsb.prototype.scaleDist_to_HslMember_sc = 
		d3.scale.linear().domain([this.width/6, this.width/2]).range([0,1]);
}

//~ Construction members
ColorSelector_Circle_Hsb.prototype.width = "200px";
ColorSelector_Circle_Hsb.prototype.height = "200px";
ColorSelector_Circle_Hsb.prototype.parentDomEl = null;
ColorSelector_Circle_Hsb.prototype.colorChangedCb = null;
ColorSelector_Circle_Hsb.prototype.colorChangedOngoingCb = null;
ColorSelector_Circle_Hsb.prototype.innerElId = Math.round(1000 * Math.random());

//~ Members
ColorSelector_Circle_Hsb.prototype.initialized = false;
ColorSelector_Circle_Hsb.prototype.sliderBar = null;
ColorSelector_Circle_Hsb.prototype.sliderCb = function(newSliderValue, isOngoing){
	var self = this;
	var newColor = self.colorSelected[0];
	newColor.l = newSliderValue;
	setColor(newColor, true);
};
ColorSelector_Circle_Hsb.prototype.CIRCLE_SEGMENTS = 120;

ColorSelector_Circle_Hsb.prototype.RAD_2_DEG = (Math.PI / 180 );
ColorSelector_Circle_Hsb.prototype.colorChooserWrp = null;
ColorSelector_Circle_Hsb.prototype.bulbCircleWrp = null;
ColorSelector_Circle_Hsb.prototype.touchPanel = null;

ColorSelector_Circle_Hsb.prototype.g_colorCircle = null;
ColorSelector_Circle_Hsb.prototype.colorSelected = []; // 
ColorSelector_Circle_Hsb.prototype.g_colorSelected = null;
ColorSelector_Circle_Hsb.prototype.colorSelected_Previous = [];
ColorSelector_Circle_Hsb.prototype.g_colorSelected_Previous = null;
ColorSelector_Circle_Hsb.prototype.g_innerColorCircle = null;

ColorSelector_Circle_Hsb.prototype.interactionActive = false;
ColorSelector_Circle_Hsb.prototype.mouseDown = false;
ColorSelector_Circle_Hsb.prototype.evtId = 0;

//~ Functions //////////////////////////////////////////////////////////////////
ColorSelector_Circle_Hsb.prototype.init = function(initColor){
	var self = this;
	if(!this.initialized){
		this.render();
		this.renderColorCircle();
		this.sliderBar = new SliderBar(40, this.height - 25, this.colorChooserWrp, 
			function(newSliderValue, isOngoing){
				var newColor = self.colorSelected[0];
				newColor.l = newSliderValue;
				self.interactionActive = isOngoing;
				self.setColor(newColor, false);
			});
		this.initialized = true;
		this.initHandlers();
	}
	if(typeof(initColor) !== 'undefined'){
		this.setColor(initColor, false, true);
		this.sliderBar.setValue(initColor.l);
		this.colorSelected.push(initColor);
		this.colorSelected_Previous = [];
		this.colorSelected_Previous.push(initColor);
	}
};
ColorSelector_Circle_Hsb.prototype.setColor = function(colorHsb, emphasizePrevious, preventCallback){
	colorHsb.l = Math.round(colorHsb.l * 1000) / 1000;
	colorHsb.s = Math.round(colorHsb.s * 1000) / 1000;
//	console.log("Getting color set: h: " + colorHsb.h + "| s: " + colorHsb.s + "| l: " + colorHsb.l);
	
	preventCallback = typeof(preventCallback) !== 'undefined' ? preventCallback : false;
	if(!this.interactionActive){
		this.colorSelected_Previous = [];
		if(this.colorSelected.length > 0){
			this.colorSelected_Previous.push(this.colorSelected[0]);
		}
	}
	
	this.colorSelected = [];
	this.colorSelected.push(colorHsb);
	
	if(!preventCallback){
		if(this.interactionActive){
			this.colorChangedOngoingCb(colorHsb);
		}else{
			this.colorChangedCb(colorHsb);
			this.colorChangedOngoingCb(colorHsb);
		}
	}
	
	if(emphasizePrevious){
		this.redrawPrevious();
	}else{
		this.redrawClearPrevious();
	}
	this.redrawCurrent();
	
	this.sliderBar.setValue(colorHsb.l);
};

ColorSelector_Circle_Hsb.prototype.render = function(){
	this.colorChooserWrp = d3.select(this.parentDomElement); 
    this.bulbCircleWrp = this.colorChooserWrp
            .append("svg:svg")
            .attr("width", this.width)
            .attr("height", this.height)
            .style("fill", "none")
//            .style("cursor", "crosshair")
    ;
    var outerLimitCircle = this.bulbCircleWrp
            .append("svg:circle")
            .attr("cx", "0")
            .attr("cy", "0")
            .attr("r", this.width/2 - 1)
            .attr("transform", "translate("+this.width/2 + "," + this.width/2 + ")")
//                .attr("d", innerColorCircleDef)
            .style("fill", "none")
            .style("stroke-width", "1px")
            .style("stroke", "#DEDEDE")
    ;
    var outerColorCircle = this.bulbCircleWrp
            .append("svg:circle")
            .attr("cx", "0")
            .attr("cy", "0")
            .attr("r", this.width/7 )
            .attr("transform", "translate("+this.width/2 + "," + this.width/2 + ")")
//                .attr("d", innerColorCircleDef)
            .style("fill", "none")
            .style("stroke-width", "1px")
            .style("stroke", "#EFEFEF")
    ;
    this.g_innerColorCircle = this.bulbCircleWrp
            .append("svg:circle")
            .attr("cx", "0")
            .attr("cy", "0")
            .attr("r", this.width/7)
            .attr("transform", "translate("+this.width/2 + "," + this.width/2 + ")")
//                .attr("d", innerColorCircleDef)
            .style("stroke-width", "0px")
//                .style("stroke", "#444444")
    ;
    var innerColorCircle_brightEffect = this.bulbCircleWrp
            .append("svg:circle")
            .attr("cx", "0")
            .attr("cy", "0")
            .attr("r", this.width/7)
//                .style("stroke-width", "0px")
            .attr("transform", "translate("+this.width/2+","+ this.width/2 +")")
//                .style("fill", "rgba(255, 255, 255, 0.5)")
    ;
    var innerColorCircle_brightEffect_gradient = this.bulbCircleWrp
            .append("radialGradient")
            .attr("gradientUnits", "objectBoundingBox")
            .attr("id", "grd" + this.innerElId)
            .attr("cx", "60%")
            .attr("cy", "38%")
    ;
    innerColorCircle_brightEffect_gradient
            .append("stop")
            .attr("offset", "5%")
            .attr("stop-color", "white")
            .attr("stop-opacity", 0.5)
    ;
    innerColorCircle_brightEffect_gradient
            .append("stop")
            .attr("offset", "99%")
            .attr("stop-color", "#555555")
            .attr("stop-opacity", 0)
    ;
    innerColorCircle_brightEffect.attr("fill", "url(#grd"+ this.innerElId + ")");
    
    this.g_colorCircle = this.bulbCircleWrp
            .append("svg:g")
            .attr("transform", "translate("+this.width/2+","+ this.width/2 +")");
	
	
    this.g_colorSelected = this.bulbCircleWrp
            .append("svg:g")
            .attr("transform", "translate("+this.width/2+","+ this.width/2 +")")
    ;
    this.g_colorSelected_Previous = this.bulbCircleWrp
            .append("svg:g")
            .attr("transform", "translate("+this.width/2+","+ this.width/2 +")")
    ;
	
	this.touchPanel = this.bulbCircleWrp
            .append("svg:svg")
            .attr("width", this.width)
            .attr("height", this.height)
    ;
	this.touchPanel
			.append("svg:rect")
            .attr("width", this.width)
            .attr("height", this.height)
            .attr("fill", "transparent")
            .style("cursor", "crosshair")
	;	
};
ColorSelector_Circle_Hsb.prototype.renderColorCircle = function(){
    var colorStep = 360 / this.CIRCLE_SEGMENTS;
    var sliceWidth = this.RAD_2_DEG * colorStep;
	var modelColorCircle = [];
    for (var i = 0; i < this.CIRCLE_SEGMENTS ; i++){
        var tmpColor = 0;
        var currAngle = i * colorStep * this.RAD_2_DEG;
        tmpColor = d3.hsl(i * colorStep, 1, 0.5);
        modelColorCircle[i] = {index : i, angle : i * colorStep, start: currAngle, color: tmpColor };
    }
	var singleArc = d3.svg.arc()
        .innerRadius(this.width/5)
        .outerRadius( this.width/3 + this.width/32 )
        .startAngle(function(d, i){return d.start; })
        .endAngle(function(d, i){return d.start + sliceWidth;})
	;
	
	this.g_colorCircle.selectAll("path")
        .data( modelColorCircle, function(d){ return d.index;} )
        .enter().append("svg:path")
        .style("fill", function(d, i){
            return d.color;
        })
        .attr("d", singleArc)
        .style("stroke-width", "0px")
//            .style("stroke", "white")
        ;
	
};
ColorSelector_Circle_Hsb.prototype.createArc = function(outerRadius){
	var self = this;
	var arc = d3.svg.arc()
		.innerRadius( (this.width/6) - (this.width/40) )
		.outerRadius(outerRadius)
		.startAngle(function(d, i){return self.RAD_2_DEG * d.h - .7; })
		.endAngle(function(d, i){return self.RAD_2_DEG * d.h + .7; })
	;
	return arc;
};

ColorSelector_Circle_Hsb.prototype.redrawCurrent = function(){
	var self = this;

	var currSel = this.g_colorSelected.selectAll("path");
	currSel = currSel.data( this.colorSelected, function(d){ 
		return self.toActualDisplayColor(d);
		return d;
	});
	currSel.exit().remove();
	if(this.interactionActive){
		currSel.enter()
			.append("svg:path")
			.style("fill", function(d, i){
				return self.toActualDisplayColor(d);
			})
			.attr("d", this.createArc( self.scaleDist_to_HslMember_sc.invert(self.colorSelected[0].s) ))
			.style("stroke-width", "0px")
			.style("opacity", "1.0")
		;
	}else{
		currSel.enter()
			.append("svg:path")
			.style("fill", function(d, i){
				return self.toActualDisplayColor(d);
			})
			.attr("d", this.createArc( self.scaleDist_to_HslMember_sc.invert(self.colorSelected[0].s) ))
			.style("stroke-width", "0.5px")
			.style("stroke", "white")
			.style("opacity", "0.5")
		;
	}
	this.g_innerColorCircle.attr("fill", this.toActualDisplayColor(this.colorSelected[0]));
};
ColorSelector_Circle_Hsb.prototype.redrawClearPrevious = function(){
	var self = this;
	var color = this.colorSelected_Previous[0];
	this.colorSelected_Previous = [];
	
	var currSel = this.g_colorSelected_Previous.selectAll("path")
		.data( this.colorSelected_Previous, function(d){ return d;} );
	
	currSel.exit().remove();
	this.colorSelected_Previous.push(color);
};
ColorSelector_Circle_Hsb.prototype.redrawPrevious = function(){
	var self = this;
	
	var currSel = this.g_colorSelected_Previous.selectAll("path")
		.data( this.colorSelected_Previous, function(d){ return d;} );
	currSel.exit().remove();
	currSel.enter()
		.append("svg:path")
		.style("fill", function(d, i){
			return self.toActualDisplayColor(d);
		})
		.attr("d", this.createArc( self.scaleDist_to_HslMember_sc.invert(self.colorSelected_Previous[0].s) ))
		.style("stroke-width", "0.5px")
		.style("stroke", "white")
		.style("opacity", "0.7")
	;
};

ColorSelector_Circle_Hsb.prototype.initHandlers = function(){
	var self = this;
	this.touchPanel
        .on("mouseup", function(d,i){
            self.mouseDown = false;
//            colorSelectFinal(self.color_selected);
            self.interactionActive = false;
//			var color = d3.hsl(self.colorSelected[0].h, self.colorSelected[0].s, self.colorSelected[0].l);
			self.setColor(self.colorSelected[0], true);
//            resetSelectors(true);
        }, true)
        .on("mousemove", function(d,i){
            if(!self.mouseDown)return ;
            var event = d3.event;
			self.setColor(self.coordinatesToColor(event.clientX, event.clientY), true);
            self.interactionActive = true;
//            selectColor(self.currSelIdx, distFromCenter(event.clientX, event.clientY), false);
//            resetSelectors(false);
            event.preventDefault();
        })
        .on("mouseover", function(d,i){
            if(!self.mouseDown)return ;
            var event = d3.event;
            self.interactionActive = true;
			self.setColor(self.coordinatesToColor(event.clientX, event.clientY), true);
        }, false)
        .on("click", function(d,i){
            d3.event.preventDefault();
            var event = d3.event;
            self.interactionActive = false;
			self.setColor(self.coordinatesToColor(event.clientX, event.clientY), true);
        }, true)
        .on("mousedown", function(d,i){
            self.mouseDown = true;
            var event = d3.event;
            self.interactionActive = true;
			self.setColor(self.coordinatesToColor(event.clientX, event.clientY), true);
            event.preventDefault();
        }, true)
    ;
	this.touchPanel
		.on("touchstart", function(d, i){
			var event = d3.event;
			self.evtId = event.touches.length - 1;
			var touch = event.touches[self.evtId];
			if(typeof(touch) === 'undefined'){
//				resetSelectors(false);
				return ;
			}
			self.setColor(self.coordinatesToColor(touch.clientX, touch.clientY), true);
			event.preventDefault();
			self.interactionActive = true;
		}, true)
		.on("touchend", function(d, i){
			var event = d3.event;
			var touch = event.changedTouches[self.evtId];
			if(typeof(touch) === 'undefined'){
				return ;
			}
//			if(!self.snappedBack){
//				selectColor(sliceIdx, distFromCenter(touch.clientX, touch.clientY), false);
//				colorSelectFinal(self.color_selected);
//			}
			self.interactionActive = false;
			self.setColor(self.coordinatesToColor(touch.clientX, touch.clientY), true);
			event.preventDefault();
			
		})
		.on("touchmove", function(d, i){
			var event = d3.event;
			var touch = event.touches[self.evtId];
			if(typeof(touch) === 'undefined'){
				return ;
			}
			self.setColor(self.coordinatesToColor(touch.clientX, touch.clientY), true);
			event.preventDefault();
		}, true)
	;
};

//~ Helper Functions ///////////////////////////////////////////////////////////
ColorSelector_Circle_Hsb.prototype.toActualDisplayColor =  function(color){
	// Intended to propably inappropriately compensate differences regarding color displayed and actually glowing
	return d3.hsl(
			color.h, 
			color.s, 
			0.2 + (color.l * 0.6) );
};
ColorSelector_Circle_Hsb.prototype.coordinatesToColor =  function(clientX, clientY){
	var angle = this.coordinatesToAngle(clientX, clientY) / this.RAD_2_DEG;
	var sat = this.scaleDist_to_HslMember(this.distFromCenter(clientX, clientY));
	var res = d3.hsl(angle, sat, this.colorSelected[0].l);
	return res;
};
ColorSelector_Circle_Hsb.prototype.coordinatesToAngle = function(clientX, clientY){
	var wrp = this.wrapperCoordinates();
	var posY = clientY - wrp[1];
	var posX = clientX - wrp[0];
//         console.log("|-- Wrp(x,y): " + wrpX() + " | " + wrpY() );
//         console.log(" -- Clt(x,y): " + clientX + " | " + clientY);
//         console.log(" -- pOf(x,y): " + window.pageXOffset + " | " + window.pageYOffset);
//         console.log(" -- Pos(x,y): " + posX + " | " + posY);
	var angle =  Math.atan2(posX - this.width/2, - posY + this.width/2);
	if (angle < 0) angle += 2 * Math.PI;
	return angle;
};
ColorSelector_Circle_Hsb.prototype.distFromCenter = function(clientX, clientY){
	var wrp = this.wrapperCoordinates();
	var posX = clientX - wrp[0];
	var posY = clientY - wrp[1];
	var cX = this.width/2;             
	var cY = this.width/2;             
	var dist = Math.sqrt(((posX-cX) * (posX-cX)) + ((posY-cY) * (posY-cY)));
	return dist;
};
ColorSelector_Circle_Hsb.prototype.scaleDist_to_HslMember = function(input){
	var res = this.scaleDist_to_HslMember_sc(input);
	if(res > 1) return 1;
	if(res < 0)return 0;
	return res;
};
ColorSelector_Circle_Hsb.prototype.wrapperCoordinates = function(){
//	var bRect = this.colorChooserWrp[0][0].getBoundingClientRect();
	var bRect = this.bulbCircleWrp[0][0].getBoundingClientRect();
	return [bRect.left, bRect.top];
};