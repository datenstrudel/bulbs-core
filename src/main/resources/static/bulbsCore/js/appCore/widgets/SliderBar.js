/**
 * A horizontal slider bar element based on D3.js
 * @param {type} width
 * @param {type} height
 * @param {type} parentDomElement
 * @param {type} valueChangeCb callback function for value changes
 * @returns {undefined}
 */
function SliderBar(width, height, parentDomElement, valueChangeCb){
    
    var self = this;
    this.HEIGHT = height;
    this.WIDTH = width;
    this.sliderCtrlWidth = 30;
    this.sliderCtrlHeight = 5;
    this.currentValue = 0;
    this.interactionActive = false;
    this.mouseDown = false;
    this.sliderDecoStrokeColor = "#DADADA";
    
    this.sliderWrp_ = parentDomElement.append("div")
        .attr("style", "border: 0px dotted black; display:inline-block ; cursor: pointer; max-width: "+(this.WIDTH + 30 )+"px"+" ")
        .attr("transform", "translate("+this.WIDTH/2 + ","+ 0 + ")");
    ;
    this.sliderWrp = this.sliderWrp_.append("svg:svg")
        .attr("width", width)
        .attr("height", height+5)
        .attr("style", "border: 0px dotted red;  cursor: pointer;")
        .style("margin", "10px")
        .style("margin-right", "0")
    ;
    this.sliderCenterline = this.sliderWrp.append("line")
        .attr("x1", 0)
        .attr("y1", 0)
        .attr("x2", 0)
        .attr("y2", this.HEIGHT + this.sliderCtrlHeight)
        .style("stroke-width", "1px")
        .style("stroke", this.sliderDecoStrokeColor )
        .attr("class", "form-control")
        .attr("transform", "translate("+this.WIDTH/2 + ", 0)");
    ;
    this.sliderTopline = this.sliderWrp.append("line")
        .attr("x1", -self.sliderCtrlWidth/3)
        .attr("y1", 1)
        .attr("x2", self.sliderCtrlWidth/3)
        .attr("y2", 1)
        .style("stroke-width", "1px")
        .style("stroke", this.sliderDecoStrokeColor)
        .attr("transform", "translate("+this.WIDTH/2 + ", 0)");
    
    ;
    this.sliderBottomline = this.sliderWrp.append("line")
        .attr("x1", -self.sliderCtrlWidth/3)
        .attr("y1", this.HEIGHT + this.sliderCtrlHeight)
        .attr("x2", self.sliderCtrlWidth/3)
        .attr("y2", this.HEIGHT + this.sliderCtrlHeight)
        .style("stroke-width", "1px")
        .style("stroke", this.sliderDecoStrokeColor)
        .attr("transform", "translate("+this.WIDTH/2 + ", 0)");
    ;
    
    this.sliderCtrl = this.sliderWrp.append("rect")
        .attr("x", - (this.sliderCtrlWidth/2) )
        .attr("y", 0)
        .attr("width", this.sliderCtrlWidth)
        .attr("height", this.sliderCtrlHeight)
        .attr("transform", "translate("+this.WIDTH/2 + "," +  0 + ")")
        .style("stroke-width", "0.5px")
        .style("stroke", "#EFEFEF")
        .style("fill", "#8A8A8A")
    ;
    //~ Event processing ///////////////////////////////////////////
    this.sliderWrp_
        .on("click", function(d,i){
            d3.event.preventDefault();
            self.interactionActive = false;
            setSliderCtrlPosByCursor(d3.event.clientY, false);
        }, true)
        .on("mousedown", function(d,i){
//            console.log("MOUSEDOWN");
            self.mouseDown = true;
            d3.event.preventDefault();
            
        }, true)
        .on("mousemove", function(d,i){
            if(!self.mouseDown)return ;
            var event = d3.event;
            self.interactionActive = true;
            setSliderCtrlPosByCursor(event.clientY, true);
            event.preventDefault();
        })
        .on("mouseup", function(d,i){
            self.mouseDown = false;
            var event = d3.event;
            if(!self.interactionActive)return ;
            self.interactionActive = false;
            setSliderCtrlPosByCursor(event.clientY, false);
            event.preventDefault();
        }, true)
    ;
    this.sliderWrp_
        .on("touchstart", function(d, i){
            var event = d3.event;
            self.evtId = event.touches.length - 1;
            var touch = event.touches[self.evtId];
            if(typeof(touch) === 'undefined'){
//                resetSelectors(false);
                return ;
            }
            event.preventDefault();
            self.interactionActive = true;
        }, false)
        .on("touchmove", function(d, i){
            if(!self.interactionActive)return ;
            var event = d3.event;
            var touch = event.touches[self.evtId];
            if(typeof(touch) === 'undefined'){
//                    resetSelectors(false);
                return ;
            }
            setSliderCtrlPosByCursor(touch.clientY, true);
            event.preventDefault();
        }, false)
        .on("touchend", function(d, i){
            var event = d3.event;
            var touch = event.changedTouches[self.evtId];
            if(typeof(touch) === 'undefined'){
                return ;
            }
            self.interactionActive = false;
            setSliderCtrlPosByCursor(touch.clientY, false);
            event.preventDefault();
        })
        
    ;
    function setSliderCtrlPosByCursor(clientY, ongoing){
        var posY = clientY - wrpY()  ;
        posY = Math.min(posY, self.HEIGHT  );
        posY = Math.max(posY, 0);
        self.sliderCtrl.attr("y", posY);
        self.currentValue = (self.HEIGHT - posY )  / (self.HEIGHT );
        valueChangeCb(self.currentValue, ongoing);
    }
    function getBoundingRect(){
        return self.sliderWrp[0][0].getBoundingClientRect();
    }
    function wrpX(){ return  getBoundingRect().left; }
    function wrpY(){ return  getBoundingRect().top; }
    
    //~ Client Accessible
    this.isInteractionActive = function(){
        return self.interactionActive;  
    };
    this.setValue = function(newValue){
        if(newValue < 0 || newValue > 1)throw new Error("newValue must be between 0 and 1");
        self.currentValue = newValue;
        var posY = (self.currentValue * self.HEIGHT) - self.HEIGHT;
        self.sliderCtrl.attr("y", -posY );
    };
    
}