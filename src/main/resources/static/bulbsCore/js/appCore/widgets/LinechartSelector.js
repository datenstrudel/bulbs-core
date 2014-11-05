/* 
 * SEE: http://bl.ocks.org/lgersman/5311083
 */
/**
 *
 *
 * @param {int} width
 * @param {DomElement} parentDomElement
 * @param {Array} states formatted as <pre>[ {time : tValue0, value: value0 }, {time: tValue1, value : value1}, ...]</pre>
 * @param xDomainLength
 * @param {function(states)} callbackStatesUpdated Function's argument state's values range is: [0,1]
 * @param {function(state, index)} callbackStateSelected Called when a state is selected by hovering.
 * @returns {LinechartSelector}
 */

function LinechartSelector(width, parentDomElement, states, xDomainLength, callbackStatesUpdated, callbackStateSelected, callbackStateAdded){
	this.width = width;
	this.height = width;
	if(typeof(states) === 'undefined')throw new Error("states must not be undefined!");
	this.states = states;
	if(typeof(xDomainLength) === 'undefined')throw new Error("xDomainLength must not be undefined!");
    this.xDomainLength = xDomainLength;
	if(typeof(parentDomElement) === 'undefined')throw new Error("parentDomElement must not be undefined!");
	this.parentDomElement = parentDomElement;
	if(typeof(callbackStatesUpdated) === 'undefined')throw new Error("parentDomElement must not be undefined!");
	this.callbackStatesUpdated = callbackStatesUpdated;
    this.callbackStateSelected = callbackStateSelected;
    this.callbackStateAdded = callbackStateAdded;
}
//~ Construction members
LinechartSelector.prototype.width;
LinechartSelector.prototype.height;
LinechartSelector.prototype.xDomainLength;
LinechartSelector.prototype.parentDomElement = null;
LinechartSelector.prototype.callbackStatesUpdated = null;
LinechartSelector.prototype.callbackStateSelected = null;
LinechartSelector.prototype.callbackStateAdded = null;
LinechartSelector.prototype.displayRatio = 20/9;

//~ Member(s)
LinechartSelector.prototype.touchPanel = null;
LinechartSelector.prototype.domElement = null;
LinechartSelector.prototype.states = [];

LinechartSelector.prototype.init = function(){
//    console.log("Init LineChartSelector; width: " + this.width+" | height: " + this.height+" ");
    this.enforceStatesInvariants();
    this.height = this.width / this.displayRatio;
    this.height = Math.min(this.height, 180);
	this.render();
};
LinechartSelector.prototype.sortStates = function(){
    this.states.sort(function(a, b){
        return a.time - b.time;
    });
}
LinechartSelector.prototype.enforceStatesInvariants = function(){
//    var prevTime = -1;
//    angular.forEach(this.states, function(state){
//
//    });
}
LinechartSelector.prototype.isPointAtLimit = function(state){
    return state == this.states[0] || state == this.states[this.states.length-1];
}
LinechartSelector.prototype.render = function(){
	var radius = 6;
	var self = this;
	var inBoundaries = function (x, y){
		var radius = 0;//radius + 2;
		return (x >= (0 + radius) && x <= (self.width -  radius)) 
            && (y >= (0 + radius) && y <= (self.height - radius));
	};

	var margin = {top: 20, right: 20, bottom: 20, left: 20};

	var x = d3.scale.linear()
	    .domain([0, this.xDomainLength])
		.range([0, self.width]);
	var y = d3.scale.linear()
        .domain([0,1])
		.range([self.height, 0]);

	var xAxis = d3.svg.axis()
		.scale(x)
		.orient("bottom")
		.ticks(3)
//		.tickValues([0, 80])
	;
	var yAxis = d3.svg.axis()
		.scale(y)
		.orient("left")
		.ticks(0)
	;

	var line = d3.svg.line()
		.x(function(d) { return x(d.time); })
		.y(function(d) { return y(d.value); });

	var svg = d3.select(this.parentDomElement).append("svg")
		.attr("width", this.width + margin.left + margin.right)
		.attr("height", this.height + margin.top + margin.bottom)
    ;
    this.domElement = svg;

    svg = svg.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    this.touchPanel = svg
        .append("svg:rect")
        .attr("fill", "transparent")
        .style("cursor", "crosshair")
        .attr("width", this.width + margin.left )
        .attr("height", this.height + margin.top )
        .attr("class", "touchPanel")
    ;

	//~ Apply Data
	svg.append("g")
		.attr("class", "x axis")
		.attr("transform", "translate(0," + (this.height) + ")") //  - this.height
		.call(xAxis)
        .style({
		    fill : "none",
		    stroke :  "#979797",
//		    "shape-rendering" :  "crispEdges",
		    "font-size" :  "0.8em"
        })
		.append("text")
		.style("text-anchor", "middle")
		.attr("dy", "2.6em")
		.attr("x", this.width/2)
		.text("Time")
	;

	svg.append("g")
//		.attr("class", "y axis")
		.call(yAxis)
        .style({
            fill : "none",
            stroke : "#979797",
//            "shape-rendering" : "crispEdges"
        })
//	  .append("text")
//		.attr("transform", "rotate(-90)")
//		.attr("y", 6)
//		.attr("dy", ".71em")
//		.style("text-anchor", "end")
//		.text("Price ($)")
	;
    var keyFct = function(d){
        return self.states.indexOf(d);
    }
    var initLinePath = function(){
        return svg.selectAll("path.line")
            .data([self.states])
            .enter()
            .append("svg:path")
            .attr("class", "line")
            .style({
                fill : "none",
                stroke : "steelblue",
                "stroke-width" : "1.5px"
            })
            .attr("d", line)
        ;
    }
    var updateLinePath = function(linePath){
        linePath.data([self.states])
            .attr("class", "line")
            .style({
                fill : "none",
                stroke : "steelblue",
                "stroke-width" : "1.5px"
            })
            .attr("d", line)
        ;
        self.callbackStatesUpdated(self.states);
    }

    //~ Drag functionality
    var drag = d3.behavior.drag()
        .on("drag", function( dtate, index) {
            var selection = d3.selectAll('.selected');

//            if( selection[0].indexOf(this) == -1) {
//                selection.classed( "selected", false);
                selection = d3.select(this);
                selection.classed("selected", true);
//            }
            selection.attr("transform", function( d, i) {
                var timeDisp, valueDisp;
                timeDisp = x(d.time) + (d3.event.dx);
                valueDisp = y(d.value) + (d3.event.dy);
                if(!inBoundaries(timeDisp, valueDisp))
                    return "translate(" + [ (x(d.time)), (y(d.value)) ] + ")";
                if(!self.isPointAtLimit(d)){
                    d.time = x.invert(timeDisp);
                }else{
                    timeDisp = x(d.time);
                }
                d.value = y.invert(valueDisp);
//				console.log("New Point position: [" + d.time + "; " + d.value + "]");
//				console.log("New Point(Disp): [" + timeDisp + "; " + valueDisp + "]");
                return "translate(" + [ (timeDisp), (valueDisp) ] + ")";
            });
            self.sortStates(); // in order to cause no intersected line
            updateLinePath(linePath);
            // reappend dragged element as last
            // so that it stays on top
            this.parentNode.appendChild(this);
            d3.event.sourceEvent.stopPropagation();
            d3.event.sourceEvent.preventDefault();
        });

	//~ State points
    var initSelectors = function(){
        var gStates = svg.selectAll("g.state").data([]);
        gStates.exit().remove();
        gStates = svg.selectAll("g.state").data(self.states, keyFct);
        gStates.exit().remove();
        var gState = gStates.enter().append("g")
            .attr({
                "transform" : function( d) {
                    return "translate("+ [ x(d.time), y(d.value) ] + ")";
                },
                'class' : 'state'
        });
        gState.append("circle")
            .attr({
                r       : radius + 2,
                class   : 'outer'
            })
            .style("fill", "#A4FC85")
        ;
        gState.append("circle")
            .attr({
                r       : radius,
                class   : 'inner',
                fill	: 'white'
            })
            .on("click", function( d, i) {
                //~ Remove state
                if(d3.event.defaultPrevented)return;
                if(self.isPointAtLimit(d))return;
//                if(self.states.length < 2)return;

                self.states.remove(d);
                self.sortStates();
                updateLinePath(linePath);
                initSelectors();
            })
            .on("mousedown", function(d, i){
//                d3.event.preventDefault();
//                d3.event.stopPropagation();
            })
            .on("mouseup", function(d, i){
                var selection = d3.select(this);

//                d3.event.preventDefault();
//                d3.event.stopPropagation();
            })
            .on("mousemove", function( d, i) {
                var e = d3.event,
                    g = this.parentNode,
                    isSelected = d3.select(g).classed( "selected");

                if( !e.ctrlKey) {
                    d3.selectAll('g.selected').classed("selected", false);
                }
                d3.select(g).classed("selected", !isSelected);
                // reappend dragged element as last
                // so that its stays on top
                g.parentNode.appendChild(g);
            })
            .on("mouseover", function(state, index){
                self.sortStates();
                self.callbackStateSelected(state, self.states.indexOf(state));
                d3.select(this).style({
                    fill : "#D4FCCA",
                    cursor : "pointer"
                } );
            })
            .on("mouseout", function() {
                var selection = d3.select(this);
                selection.classed("selected", false);
                selection.style("fill", "white");

            });
        gState.call(drag);
        return gState;
    };
    var linePath = initLinePath();
    initSelectors();

    this.touchPanel.on("click", function(d,i){
        if(self.xDomainLength < 1)return;
        var mousePos = d3.mouse(this);
        var timeDisp;
        if(self.states.length < 1){
            timeDisp = 0;
        }else if(self.states.length < 2){
            timeDisp = self.xDomainLength;
        }else{
            timeDisp = x.invert(mousePos[0]);
        }
        var valueDisp = y.invert(mousePos[1]);
        console.log("Add point; with value: " + valueDisp);
        var newState = {
            time : timeDisp,
            value : valueDisp
        };
        self.states.push(newState);
        self.sortStates();

        self.callbackStateAdded(self.states, self.states.indexOf(newState));

        //~ Update line
        updateLinePath(linePath);
        //~ Update selector circles
        initSelectors();
    }, true);

};
LinechartSelector.prototype.removeFromParent = function(){
    this.domElement.remove();
}

