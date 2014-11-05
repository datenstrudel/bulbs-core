// @Deprecated
function ColorSelector_HueCircle(width, parentDomElement, colorChangedCb, colorChangedOngoingCb){
    var self = this;
    this.WIDTH = width;
    this.HEIGHT = this.WIDTH;
    this.MODEL_COLOR_CIRCLE = [];
    this.RAD_2_DEG = (Math.PI / 180 );
    this.COUNT_SEGMENTS = 120; //120;
    this.COLOR_STEP = 360 / this.COUNT_SEGMENTS;
    this.SLICE_WIDTH = this.RAD_2_DEG * this.COLOR_STEP;
    this.innerElId = Math.random();
    for (var i = 0; i < this.COUNT_SEGMENTS ; i++){
        var tmpColor = 0;
        var currAngle = i * this.COLOR_STEP * this.RAD_2_DEG;
        tmpColor = d3.hsl(i * this.COLOR_STEP, 1, 0.5);
        this.MODEL_COLOR_CIRCLE[i] = {index : i, angle : i * this.COLOR_STEP, start: currAngle, color: tmpColor };
    }
    
    this.interactionActive = false;
    
    this.colorChooserWrp = d3.select(parentDomElement); 
    this.bulbCircleWrp = this.colorChooserWrp
            .append("svg:svg")
            .attr("width", this.WIDTH)
            .attr("height", this.HEIGHT)
            .style("fill", "none")
            .style("cursor", "crosshair")
    ;
    this.outerLimitCircle = this.bulbCircleWrp
            .append("svg:circle")
            .attr("cx", "0")
            .attr("cy", "0")
            .attr("r", this.WIDTH/2 - 1)
            .attr("transform", "translate("+this.WIDTH/2 + "," + this.WIDTH/2 + ")")
//                .attr("d", innerColorCircleDef)
            .style("fill", "none")
            .style("stroke-width", "1px")
            .style("stroke", "#DEDEDE")
    ;
    this.outerColorCircle = this.bulbCircleWrp
            .append("svg:circle")
            .attr("cx", "0")
            .attr("cy", "0")
            .attr("r", this.WIDTH/7 )
            .attr("transform", "translate("+this.WIDTH/2 + "," + this.WIDTH/2 + ")")
//                .attr("d", innerColorCircleDef)
            .style("fill", "none")
            .style("stroke-width", "1px")
            .style("stroke", "#EFEFEF")
    ;
    this.innerColorCircle = this.bulbCircleWrp
            .append("svg:circle")
            .attr("cx", "0")
            .attr("cy", "0")
            .attr("r", this.WIDTH/7)
            .attr("transform", "translate("+this.WIDTH/2 + "," + this.WIDTH/2 + ")")
//                .attr("d", innerColorCircleDef)
            .style("stroke-width", "0px")
//                .style("stroke", "#444444")
    ;
    this.innerColorCircle_brightEffect = this.bulbCircleWrp
            .append("svg:circle")
            .attr("cx", "0")
            .attr("cy", "0")
            .attr("r", this.WIDTH/7)
//                .style("stroke-width", "0px")
            .attr("transform", "translate("+this.WIDTH/2+","+ this.WIDTH/2 +")")
//                .style("fill", "rgba(255, 255, 255, 0.5)")
    ;
    this.innerColorCircle_brightEffect_gradient = this.bulbCircleWrp
            .append("radialGradient")
            .attr("gradientUnits", "objectBoundingBox")
            .attr("id", "grd"+self.innerElId)
            .attr("cx", "60%")
            .attr("cy", "38%")
    ;
    this.innerColorCircle_brightEffect_gradient
            .append("stop")
            .attr("offset", "5%")
            .attr("stop-color", "white")
            .attr("stop-opacity", 0.5)
    ;
    this.innerColorCircle_brightEffect_gradient
            .append("stop")
            .attr("offset", "99%")
            .attr("stop-color", "#555555")
            .attr("stop-opacity", 0)
    ;
    this.innerColorCircle_brightEffect.attr("fill", "url(#grd"+ self.innerElId + ")");
    
    this.colorCircle = this.bulbCircleWrp
            .append("svg:g")
            .attr("transform", "translate("+this.WIDTH/2+","+ this.WIDTH/2 +")")
    ;
    
    // /////////////////////////////////////////////////////////////////////////
    var SLICE_WIDTH = this.SLICE_WIDTH;
    this.arc_selector = d3.svg.arc()
        .innerRadius(self.WIDTH/5)
        .outerRadius( self.WIDTH/3 + self.WIDTH/32 )
        .startAngle(function(d, i){return d.start; })
        .endAngle(function(d, i){return d.start + self.SLICE_WIDTH;})
        ;
    this.arc_Selected = d3.svg.arc()
        .innerRadius(self.WIDTH/3 - self.WIDTH/30)
        .outerRadius(self.WIDTH/2)
        .startAngle(function(d, i){return d.start - 1.6 * self.SLICE_WIDTH; })
        .endAngle(function(d, i){return d.start + self.SLICE_WIDTH + 1.6 * self.SLICE_WIDTH;})
        ;

    function createArc_Selected(outerRadius){
        var arc_Selected = d3.svg.arc()
            .innerRadius( (self.WIDTH/6) - (self.WIDTH/40) )
            .outerRadius(outerRadius)
            .startAngle(function(d, i){return d.start - 10 * self.SLICE_WIDTH; })
            .endAngle(function(d, i){return d.start + self.SLICE_WIDTH + 10 * self.SLICE_WIDTH;})
        ;
         return arc_Selected;
    }

    this.colorCircle.selectAll("path")
        .data( self.MODEL_COLOR_CIRCLE, function(d){ return d.index;} )
        .enter().append("svg:path")
        .style("fill", function(d, i){
            return d.color;
        })
        .attr("d", self.arc_selector)
        .style("stroke-width", "0px")
//            .style("stroke", "white")
        ;
        
    this.sliderCb = function(newSliderValue, isOngoing){
        var newColor = self.color_selected_final;
        newColor.l = newSliderValue;
        setColorInternal(newColor);
//        colorSelectOngoing(newColor);
        if(isOngoing) colorSelectOngoing(newColor);
        else{
            colorSelectOngoing(newColor);
            colorSelectFinal(newColor);
        }
    };
    this.sliderBar = new SliderBar(40, this.HEIGHT - 25, this.colorChooserWrp, this.sliderCb);
    
    this.prevSelIdx = null;
    this.prevSelection = null;
    this.currentSelection = null;
    this.currentOldEmphasized = null;
    this.currSelIdx = 0;
    this.currEmphIdx = 0;

    this.color_selected = this.MODEL_COLOR_CIRCLE[0].color;
    this.color_selected_final = this.MODEL_COLOR_CIRCLE[0].color;
    function colorSelectFinal(color){
        self.color_selected_final = color;
        colorChangedCb(color);
    }
    function colorSelectOngoing(color){
        self.color_selected = color;
        colorChangedOngoingCb(color);
    }
    this.color_selected_final_idx = 0;
    this.snappedBack = false;

    var scaleDist_to_HslMember_sc = d3.scale.linear()
        .domain([self.WIDTH/6, self.WIDTH/2])
        .range([0,1]);
    function scaleDist_to_HslMember(input){
        var res = scaleDist_to_HslMember_sc(input);
        if(res > 1) return 1;
        if(res < 0)return 0;
        return res;
    }
    
    function selectColor(circleElIdx, distFromCtr, externallySet){
        var currSel = null;
        var selPath = self.colorCircle.selectAll("path")[0][circleElIdx];
        var currSel = d3.select(selPath);
//                currSel = d3.select(target);

        self.prevSelection = self.currentSelection;
        self.currentSelection = currSel;

        self.prevSelIdx = self.currSelIdx;
        self.currSelIdx = circleElIdx % self.COUNT_SEGMENTS;
        var color = self.color_selected_final;
        color.h = self.MODEL_COLOR_CIRCLE[self.currSelIdx].color.h;
        color.s = scaleDist_to_HslMember(distFromCtr);
        if(distFromCtr != null){
            if(distFromCtr > self.WIDTH/2){
                self.snappedBack = true;
                handleSnapBackToOldColor();
                return ;
            }
            self.snappedBack = false;
        }

        if(!externallySet){
            emphazizeSinglePathEl(circleElIdx, currSel, distFromCtr, color, {"opacity" : 1});
            colorSelectOngoing(color);
        }else{
            self.color_selected = color;
            emphazizeSinglePathEl(circleElIdx, currSel, distFromCtr, color, {"opacity" : 0.5});
        }
        
        self.innerColorCircle.attr("fill", toActualDisplayColor(color) );
        if(externallySet && self.prevSelection != null ){ 
            self.prevSelection
                 = emphazizeSinglePathEl(self.currSelIdx, currSel, distFromCtr, color, {"opacity" : 0.5});
//////                        .attr("d", self.arc_selector)
////                        .style("stroke-width", "0px")
////                        .style("opacity", "0.6")
////                        .style("fill", toActualDisplayColor(self.color_selected_final));
//            self.prevSelection.style("fill", toActualDisplayColor(color) );
        }
//        self.currentSelection.attr("fill", toActualDisplayColor(color) );
    };
    function handleSnapBackToOldColor(){
        var color = self.color_selected_final;
        var selPath = self.colorCircle.selectAll("path")[0][self.color_selected_final_idx];
        var currSel = d3.select(selPath);
        resetSelectors(false);
//        emphasizeOldSelected();
        self.prevSelection = self.currentSelection;
        self.currentSelection = currSel;
        self.prevSelIdx = self.currSelIdx;
        self.currSelIdx = self.color_selected_final_idx % self.COUNT_SEGMENTS;

        emphazizeSinglePathEl(i, currSel, 
                scaleDist_to_HslMember_sc.invert(self.color_selected.l), 
                toActualDisplayColor(color), {"opacity" : 1})
        ;
        colorSelectOngoing(color);
        self.innerColorCircle.attr("fill", toActualDisplayColor(color) );
    }

    function emphazizeSinglePathEl(idx, pathEl, distFromCtr, color, opts){
		var el = pathEl.attr("d", createArc_Selected(distFromCtr))
                .style("stroke-width", "0.5px")
                .style("stroke", "white")
                .style("fill", toActualDisplayColor(color));
		if(opts['opacity']){
			el.style("opacity", opts['opacity']);
		};
        return el;
//                lastSelection[idx] = pathEl;
     }
    function toActualDisplayColor(color){
        // Intended to propably inappropriately compensate differences regarding color displayed and actually glowing
        return d3.hsl(
                color.h, 
                color.s, 
                0.2 + color.l * 0.6 );
    }

    function resetSelectors(includingOldSelection){
        if(self.prevSelection != null && (self.prevSelIdx != self.currSelIdx)){
            if(includingOldSelection) {
                self.prevSelection.transition().delay(5)
                        .attr("d", self.arc_selector)
                        .style("stroke-width", "0px")
                        .style("opacity", "1")
                        .style("fill", self.MODEL_COLOR_CIRCLE[self.prevSelIdx].color)
                ;
            }else{
                self.prevSelection
                        .attr("d", self.arc_selector)
                        .style("stroke-width", "0px")
                        .style("opacity", "1")
                        .style("fill", self.MODEL_COLOR_CIRCLE[self.prevSelIdx].color)
                ;
            }
        }
//        if(self.prevSelection != null && (self.prevSelIdx == self.currSelIdx)){
//            if(includingOldSelection) {
//                self.prevSelection
////                        .attr("d", self.arc_selector)
//                        .style("stroke-width", "0px")
//                        .style("opacity", "0.6")
//                        .style("fill", toActualDisplayColor(self.color_selected_final));
//            }
//        }
        if(includingOldSelection && self.currentOldEmphasized != null && self.currEmphIdx != self.currSelIdx ){
            self.currentOldEmphasized.transition().delay(5)
                    .attr("d", self.arc_selector)
                    .style("stroke-width", "0px")
                    .style("opacity", "1")
                    .style("fill", self.MODEL_COLOR_CIRCLE[self.currEmphIdx].color)
            ;
        }
    }

    this.mouseDown = false;
    this.bulbCircleWrp
        .on("mouseup", function(d,i){
            self.mouseDown = false;
            colorSelectFinal(self.color_selected);
            self.color_selected_final_idx = self.currSelIdx;
            resetSelectors(true);
            self.interactionActive = false;
        }, true)
        .on("mousemove", function(d,i){
            if(!self.mouseDown)return ;
            var event = d3.event;
            selectColor(self.currSelIdx, distFromCenter(event.clientX, event.clientY), false);
            resetSelectors(false);
            self.interactionActive = true;
            event.preventDefault();
        })
        .on("mouseover", function(d,i){
            if(!self.mouseDown)return ;
            var event = d3.event;
            var sliceIdx = calcSegmentIndexFromPosition(event.clientX, event.clientY);
            selectColor(sliceIdx, distFromCenter(event.clientX, event.clientY), false);
            resetSelectors(false);
            self.interactionActive = true;
//                event.preventDefault();
//                resetSelectors();
        }, false)
        .on("click", function(d,i){
            d3.event.preventDefault();
            var event = d3.event;
            var sliceIdx = calcSegmentIndexFromPosition(event.clientX, event.clientY);
            selectColor(sliceIdx, distFromCenter(event.clientX, event.clientY), false);
            self.interactionActive = false;
            resetSelectors(true);
        }, true)
        .on("mousedown", function(d,i){
            self.mouseDown = true;
            var event = d3.event;
            var sliceIdx = calcSegmentIndexFromPosition(event.clientX, event.clientY);
            emphasizeOldSelected();
            selectColor(sliceIdx, distFromCenter(event.clientX, event.clientY), false);
//            selectColor(i, scaleDist_to_HslMember_sc.invert(self.color_selected.l), false);
            event.preventDefault();
            self.interactionActive = true;
        }, true)
    ;

    this.evtId = 0;
    this.bulbCircleWrp
            .on("touchstart", function(d, i){
                var event = d3.event;
                self.evtId = event.touches.length - 1;
                var touch = event.touches[self.evtId];
                if(typeof(touch) === 'undefined'){
                    resetSelectors(false);
                    return ;
                }
                event.preventDefault();
                self.interactionActive = true;
                var sliceIdx = calcSegmentIndexFromPosition(touch.clientX, touch.clientY);
                emphasizeOldSelected();
                selectColor(sliceIdx, distFromCenter(touch.clientX, touch.clientY), false);
            }, false)
            .on("touchend", function(d, i){
                var event = d3.event;
                var touch = event.changedTouches[self.evtId];
                if(typeof(touch) === 'undefined'){
                    resetSelectors(true);
                    return ;
                }
                var sliceIdx = calcSegmentIndexFromPosition(touch.clientX, touch.clientY);

                if(!self.snappedBack){
                    selectColor(sliceIdx, distFromCenter(touch.clientX, touch.clientY), false);
                    colorSelectFinal(self.color_selected);
                }
                resetSelectors(true);
                self.interactionActive = false;
                event.preventDefault();
            })
            .on("touchmove", function(d, i){
                var event = d3.event;
                var touch = event.touches[self.evtId];
                if(typeof(touch) === 'undefined'){
                    resetSelectors(false);
                    return ;
                }
                var sliceIdx = calcSegmentIndexFromPosition(touch.clientX, touch.clientY);

                selectColor(sliceIdx, distFromCenter(touch.clientX, touch.clientY), false);
                resetSelectors(false);
                event.preventDefault();
            }, false)
    ;

    function calcSegmentIndexFromPosition(clientX, clientY){
         var posX = clientX - wrpX();
         var posY = clientY - wrpY();
         
//         console.log("|-- Wrp(x,y): " + wrpX() + " | " + wrpY() );
//         console.log(" -- Clt(x,y): " + clientX + " | " + clientY);
//         console.log(" -- pOf(x,y): " + window.pageXOffset + " | " + window.pageYOffset);
//         console.log(" -- Pos(x,y): " + posX + " | " + posY);

         var angleSel =  Math.atan2(posX - self.WIDTH/2, - posY + self.WIDTH/2);
         if (angleSel < 0) {angleSel += 2 * Math.PI;}

         var sliceIdx = Math.round(angleSel / self.SLICE_WIDTH); 
//         console.log(" -- SliceIDx: " + sliceIdx);
         return sliceIdx;
    }
    function emphasizeOldSelected(){
         var idx = self.currSelIdx;
         self.currEmphIdx = self.currSelIdx;
//         var distFromCtr = scaleDist_to_HslMember_sc.invert(self.color_selected.l);
//         distFromCtr = Math.min(distFromCtr, self.WIDTH/2);
         self.currentOldEmphasized = d3.select(self.colorCircle.selectAll("path")[0][self.currEmphIdx]);
//         emphazizeSinglePathEl(idx, self.currentOldEmphasized, distFromCtr, self.color_selected, {"opacity" : 0.5});
     }
    function distFromCenter(clientX, clientY){
         var posX = clientX - wrpX();
         var posY = clientY - wrpY();
         var cX = self.WIDTH/2;             
         var cY = self.WIDTH/2;             
         var dist = Math.sqrt(((posX-cX) * (posX-cX)) + ((posY-cY) * (posY-cY)));
         return dist;
    }
    function getBoundingRect(){
        return self.colorChooserWrp[0][0].getBoundingClientRect();
    }
    function wrpX(){ return  getBoundingRect().left; }
    function wrpY(){ return  getBoundingRect().top; }
    
    function setColorInternal(newHsl){
        var idx = Math.round(newHsl.h/self.COLOR_STEP);
        selectColor(idx, scaleDist_to_HslMember_sc.invert(newHsl.s), true);
        self.sliderBar.setValue(newHsl.l);
    };

    //~ Client Accessible
    this.setColor = function setColor(newHsl, b_emphasizeOld){
        b_emphasizeOld = typeof(b_emphasizeOld !== 'undefined') ? b_emphasizeOld : true;
        resetSelectors(true);
        if(self.interactionActive || self.sliderBar.isInteractionActive())return;
		if(b_emphasizeOld){
			self.prevSelIdx = self.currSelIdx;
			emphasizeOldSelected();
		}
        var idx = Math.round(newHsl.h/self.COLOR_STEP);
//		if(!b_emphasizeOld){
//			setColorInternal(newHsl);
//		}else{
			self.color_selected_final = newHsl;
			selectColor(idx, scaleDist_to_HslMember_sc.invert(newHsl.s), true);
//		}
        self.sliderBar.setValue(newHsl.l);
    };
//            return arguments.callee;
}