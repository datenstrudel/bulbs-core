'use strict';

/* 
 * Bulbs|Core Angular Directives
 */
angular.module('bulbs_core_directives', [])
    .directive('focusMe', function() {
      return {
        link: function(scope, element, attrs) {
          scope.$watch(attrs.focusMe, function(value) {
            if(value === true) { 
              console.log('value=',value);
              //$timeout(function() {
                element[0].focus();
                scope[attrs.focusMe] = false;
              //});
            }
          });
        }
      };
    })
    .directive('signupformpasswordrepitition', function() {
      return {
        require: 'ngModel',
        link: function(scope, elm, attrs, ctrl) {
          ctrl.$parsers.unshift(function(viewValue) {
            if (viewValue === scope.user.password ) {
              // it is valid
              scope.signup_form.password.$setValidity('signupformpasswordrepitition', true);
              scope.signup_form.password_rep.$setValidity('signupformpasswordrepitition', true);
              return viewValue;
            } else {
              // it is invalid, return undefined (no model update)
              scope.signup_form.password.$setValidity('signupformpasswordrepitition', false);
              scope.signup_form.password_rep.$setValidity('signupformpasswordrepitition', false);
              return viewValue;
            }
          });
        }
      };
    })
    .directive('colorSelectorCircle', [function() {
		return  {
			replace : false,
			restrict : 'A',
			scope : {
				widgetId: '@',
				continuousTrigger: '=',
				entity : '=?',
				initColor : '=?',
				disabled : '@?',
				newColorSelected : '&'
			},
			link : function link($scope, tElement, iAttrs){
				if( (typeof($scope.initColor) === 'undefined') || $scope.initColor == null){
					$scope.initColor = {
						hue: 0,
						saturation: 0,
						brightness : 0, 
						colorScheme : 'HSB'
					};
				}
				var colorSelector = new ColorSelector_Circle_Hsb(
						150,
						tElement[0], 
						function(newColor){
							var finColor = new ColorConverter().fromHslD3(newColor);
							var colorOutput = finColor.toServerColor('HSB');
							if(!$scope.continuousTrigger ){
								$scope.newColorSelected({colorValue : colorOutput, entity : $scope.entity});
							}
						},
						function(newColorOngoing){
							var finColor =  new ColorConverter().fromHslD3(newColorOngoing);
							var colorOutput = finColor.toServerColor('HSB');
							if($scope.continuousTrigger ){
								$scope.newColorSelected({colorValue : colorOutput, entity : $scope.entity});
							}
						}
				);
				$scope.colorSelector = colorSelector;
//				if(typeof($scope.initColor) !== 'undefined' ){
////					var colorHsl = new ColorConverter().fromServerColor(scope.group.bulbs[0].state.color).colorHsl;
//					var colorHsl = new ColorConverter().fromServerColor($scope.initColor).colorHsl;
//					$scope.colorSelector.init(colorHsl);
//				}
				
				$scope.$watch('initColor', function(newColor){
					if(typeof(newColor) === 'undefined' || newColor == null)return ;
					var colorHsl = new ColorConverter().fromServerColor(newColor).colorHsl;
					$scope.colorSelector.init(colorHsl);
					
				});
				$scope.$watch('disabled', function(disabled){
					//TODO: Implement me!
				});
//				scope.$on('Evt_BulbStateUpdated', function(e, bulbStateUpdatedEvent){
//					var group = scope.group;
//					angular.forEach(group.bulbs, function(bulb){
//						if(bulb.bulbId.bridgeId === bulbStateUpdatedEvent.bulbBridgeId
//								&& bulb.bulbId.localId === bulbStateUpdatedEvent.bulbLocalId){
//							scope.colorSelector.setColor(
//									new ColorConverter().fromServerColor(
//										bulbStateUpdatedEvent.state.color).colorHsl,
//									false, true);
//						}
//
//					});
//				});
			}
		};
	}])
	.directive('colorSelectorCircleHsb', [function() {
		var directiveDefObj = {
			replace : false,
			restrict : 'A',
			scope : {
				widgetId: '@',
				continuousTrigger: '@',
				bulb: '=',
				newColorSelected : '&'
			},

			link : function link($scope, tElement, iAttrs){
				var colorSelector = new ColorSelector_Circle_Hsb(
                        150,
                        tElement[0], 
                        function(newColor){
                            var finColor = new ColorConverter().fromHslD3(newColor);
                            var colorOutput = finColor.toServerColor('HSB');
                            if($scope.continuousTrigger !== 'true'){
                                $scope.newColorSelected({colorValue : colorOutput, bulb:$scope.bulb});
                            }
                        },
                        function(newColorOngoing){
                            var finColor =  new ColorConverter().fromHslD3(newColorOngoing);
                            var colorOutput = finColor.toServerColor('HSB');
                            if($scope.continuousTrigger === 'true'){
                                $scope.newColorSelected({colorValue : colorOutput, bulb:$scope.bulb});
                            }
                        }
                );
				$scope.colorSelector = colorSelector;
                var colorHsl = new ColorConverter().fromServerColor($scope.bulb.state.color).colorHsl;
				$scope.colorSelector.init(colorHsl);
                $scope.$on('Evt_BulbStateUpdated', function(e, bulbStateUpdatedEvent){
                    var bulb = $scope.bulb;
					if($scope.colorSelector.interactionActive)return ;
                    if(bulb.bulbId === bulbStateUpdatedEvent.bulbId){
                        $scope.colorSelector.setColor(
                                new ColorConverter().fromServerColor(
                                    bulbStateUpdatedEvent.state.color).colorHsl,
							false, true);
                    }
                });
				$scope.$watch('widgetId', function(oldId, newId){
					if(oldId === newId)return ;
					if($scope.colorSelector.interactionActive)return ;
					console.log("WATCH["+ oldId +"] EXEC");
					colorSelector.setColor( 
							new ColorConverter().fromServerColor(
                            $scope.bulb.state.color).colorHsl, 
							false, true);
				});
//				scope.$watch('bulb', function(oldBulb, newBulb){
//					if(oldBulb === newBulb)return ;
//					if(scope.colorSelector.interactionActive)return ;
//					colorSelector.setColor( 
//							new ColorConverter().fromServerColor(
//                            scope.bulb.state.color).colorHsl, 
//							false, true);
//				});
			}
		};
		return directiveDefObj;
	}])

	.directive('bulbPreset', ['PresetService','BulbService', 'GroupService', 'ColorConverter', '$timeout', '$location', '$anchorScroll',
			function(PresetService, BulbService, GroupService, ColorConverter, $timeout, $location, $anchorScroll) {
		return {
			replace : true,
			restrict : 'EA',
			templateUrl : 'bulbsCore/partials/directives/preset.html',
			scope : {
				preset: '=',
				applyStateImmediately : '=',
				continuousTrigger : '=',
                editModeChanged : '&'
			},
			link : function link($scope, tElement, iAttrs){
				
				$scope.editName = typeof($scope.preset.isUnsaved) !== 'undefined' ? $scope.preset.isUnsaved : false;
				$scope.editMode = typeof($scope.preset.isUnsaved) !== 'undefined' ? $scope.preset.isUnsaved : false;
				$scope.isUnsaved = typeof($scope.preset.isUnsaved) !== 'undefined' ? $scope.preset.isUnsaved : false;
				$scope.actualAvailableDevices = [];
				$scope.actualAvailableTargetGroups = [];
				$scope.actualAvailableEntities = []; // Union of actualAvailableDevices and actualAvailableTargetGroups
				$scope.presetDevices = [];
				$scope.presetGroups = [];
				$scope.presetEntities = [];
				
				$scope.availableDevices = [];
				$scope.availableGroups = [];
				
				$scope.entitySelected = null;
				$scope.presetStateSelected = null;
                $scope.presetStates = null;

				$scope.stateSelectedIdx = null;
				$scope.entitySelectedColor = {};
				$scope.entitySelectedEnabled = true;
				$scope.presetBackup = {};
				$scope.entityStyles = [];
				
				$scope.tmpSelectTarget = {};
				
				$scope.presetIndicatorColors = [];

                $scope.entitySelectionHash = 0;

                $scope.transitionsEnabled = false;

                $scope.lastStateChangeSrc = "";

				// Head
				$scope.switchEditName = function(b_state){
					if(typeof(b_state) !== 'undefined'){
						$scope.editName = b_state;
					}else{
						$scope.editName = !$scope.editName;
					}
                    $scope.editModeChanged({preset: $scope.preset, mode: $scope.editName});
				};
				$scope.save = function(presetMember){
					if($scope.preset.isUnsaved){
						//~ Save new
						PresetService.createPreset($scope.preset).then(
							function(createdPreset){
								$scope.preset = createdPreset;
								$scope.isUnsaved = false;
								$scope.switchEditName(false);
								$scope.setEditMode(false);
							}, function(error){
								console.error("Error on saving new preset: " + error);
								console.error("Todo handle prev error !!");
							}
						);
					}else{
						//~ Save field value
						switch(presetMember){
							case 'name':
								PresetService.modifyName($scope.preset).then(function(resp){
									$scope.switchEditName(false);
								});
								break;
							case 'states':
								PresetService.modifyStates($scope.preset).then(function(resp){
									$scope.setEditMode(false);
								});
								break;
							default: throw new Error("PresetMember not known to be savable: " + presetMember);
						} 
					}
//					$scope.actualAvailableEntities = []; // Union of actualAvailableDevices and actualAvailableTargetGroups
				};
				$scope.deletePreset = function(){
					PresetService.removePreset($scope.preset).then(
						function(resp){
//							$scope.$apply();
						}, function(error){}
					);
				};
				$scope.applyPreset = function(preset, asLoop){
					PresetService.applyPreset($scope.preset.presetId, asLoop);
				};
				$scope.stopActuation = function(){
					var cancelCmd = BulbService.newCancelCmd($scope.presetDevices);
					angular.forEach($scope.presetGroups, function(presetGroup){
						cancelCmd.entityIds.addAll(presetGroup.bulbIds);
					});
					BulbService.executeCancelCmd(cancelCmd);
				};
				// Entities
				$scope.setEditMode = function(b_mode){
					var prevEditMode = $scope.editMode;
					if( $scope.editMode === b_mode)return;
					$scope.editMode = b_mode;
					if(!b_mode){
						$scope.entitySelected = null;
						$scope.entitySelectedColor = {};
					}else{
						if(!prevEditMode && $scope.entitySelected == null && $scope.presetEntities.length > 0){
							$scope.selectEntity($scope.presetEntities[0]);
						}
					}
					
					if(!prevEditMode) $scope.scrollToThis();
				};
				$scope.cancelEdit = function(){
					$scope.setEditMode(false);
					$scope.preset = PresetService.replacePreset($scope.preset, $scope.presetBackup);
				};
				$scope.cancelEditName = function(){
					    $scope.switchEditName(false);
                    if(!$scope.editName && $scope.preset.isUnsaved){
                        PresetService.removePreset($scope.preset);
                    }else{
					    $scope.preset = PresetService.replacePreset($scope.preset, $scope.presetBackup);
                    }
				};
				$scope.selectEntity = function(entity){
                    $scope.entitySelectionHash = Math.round(Math.random() * 1000);
					if($scope.isEntitySelected(entity)){
						$scope.entitySelected = null;
						$scope.presetStateSelected = null;
						$scope.stateSelectedIdx = null;
						$scope.entityStyle(entity).style = { 'background-color' : '' };
					}else{
						if(!$scope.editMode){
							$scope.setEditMode(true);
						}
						$scope.entitySelected = entity;
						var presetState = $scope.presetStateFor(entity);
						$scope.presetStateSelected = presetState;
						$scope.stateSelectedIdx = 0;
						$scope.selectPresetState($scope.stateSelectedIdx);
                        $scope.transitionsEnabled = presetState.states.length > 1;

					}
				};

                $scope.toggleTransitionEnabled = function(){
                    $scope.transitionsEnabled = !$scope.transitionsEnabled;
                    if(!$scope.transitionsEnabled){
                        $scope.presetStateSelected.states.splice(1, $scope.presetStateSelected.states.length - 1);
                        $scope.selectPresetState(0);
                    }
                };

                /**
                 * Select the state of the currently selected entity's 'presetStates'
                 * @param stateIdx
                 */
				$scope.selectPresetState = function(stateIdx){
					$scope.stateSelectedIdx = stateIdx;
					if(typeof($scope.presetStateSelected.states[stateIdx]) === 'undefined'){
						$scope.presetStateSelected.states.push({
								enabled : false,
								color : {},
								transitionDelay : 0
						});
					}else{
						$scope.entitySelectedColor = $scope.presetStateSelected.states[stateIdx].color;
						$scope.entitySelectedEnabled = $scope.presetStateSelected.states[stateIdx].enabled;
					}
					$scope.entityStyle($scope.entitySelected).style = { 'background-color' : '' };
					$scope.entityStyle($scope.entitySelected).timeoutReached = true;
					
				};
				$scope.addPresetState = function(afterIdx){
					afterIdx = typeof(afterIdx) !== 'undefined' ? afterIdx : $scope.presetStateSelected.states.length -1;
					var transitionDelay = typeof($scope.presetStateSelected.states[afterIdx] !== 'undefined') ? 
							$scope.presetStateSelected.states[afterIdx].transitionDelay : 0;
					$scope.presetStateSelected.states.splice(afterIdx + 1 , 0,
						{
							enabled : true,
							color : $scope.actualPresetStateFor($scope.entitySelected).color,
							transitionDelay : transitionDelay
						});
					$scope.selectPresetState(afterIdx + 1);
				};
				$scope.removePresetState = function(atIdx){
					if($scope.presetStateSelected.states.length < 2)return ;
					$scope.presetStateSelected.states.splice(atIdx, 1);
					$scope.selectPresetState(atIdx - 1);
				};
				
				$scope.isEntitySelected = function(entity){
//					return $scope.entitySelected.indexOf(entity) !== -1;
					return $scope.entitySelected === entity;
				};
				$scope.isEntityInactive = function(entity){
					return false;
				};
				
				$scope.handleEntityAdd = function(){
					var entity2Add = $scope.tmpSelectTarget.entity2Add;
//					var entity2Add = $scope.actualAvailableEntities[targetIndex];
					$scope.tmpSelectTarget = { entity2Add : ''};
					$scope.presetEntities.push(entity2Add);
					$scope.calcActualAvailableEntities();
					$scope.selectEntity(entity2Add);
				};
				$scope.removeEntityFromPreset = function(entity, $event){
					console.log("Remove entity from presete..");
					$event.preventDefault();
					$event.stopPropagation();
					if(entity === $scope.entitySelected ) $scope.selectEntity(entity); // actually unselect
					// Remove entity's state
					if($scope.presetStates.remove($scope.presetStateFor(entity)) < 0 )
						console.error("Error removing entity's state!");
					
					$scope.presetEntities.remove(entity);
					$scope.calcActualAvailableEntities();
//					if(typeof($scope.presetEntities[0]) !== 'undefined')
//						$scope.selectEntity($scope.presetEntities[0]);
					
				};
				$scope.calcActualAvailableEntities = function(){
					$scope.actualAvailableEntities = [];
					$scope.actualAvailableEntities.addAll($scope.availableDevices);
					$scope.actualAvailableEntities.addAll($scope.availableGroups);
					angular.forEach($scope.presetEntities, function(el){
						$scope.actualAvailableEntities.splice($scope.actualAvailableEntities.indexOf(el), 1);
					});
				};
				
				//~ Entity States
				$scope.modifyEntityState_Enabled = function(entity, enabled, modSourceDirective){
					$scope.modifyEntityState(entity, null, enabled, modSourceDirective);
				};
				$scope.modifyEntityState_Color = function(entity, color, modSourceDirective){
					$scope.modifyEntityState(entity, color, null, modSourceDirective);
				};
				$scope.modifyEntityState = function(entity, color, enabled, modSourceDirective){
					var presetState = $scope.presetStateFor(entity);
					
					var entityState = presetState.states[$scope.stateSelectedIdx];
					if(color !== null) entityState.color = color;
					if(enabled !== null) {
						entityState.enabled = enabled;
						$scope.entitySelectedEnabled = enabled;
					}

                    $scope.lastStateChangeSrc = modSourceDirective;
					//~ Handle Target Style
					var bgColor = d3.hcl(ColorConverter.fromServerColor(entityState.color).colorHsl);
					var tmpPresetStyle = $scope.entityStyle(entity);
					tmpPresetStyle.style = { 'background-color' : bgColor.toString() };
					tmpPresetStyle.timeoutReached = false;
					if( !tmpPresetStyle.timeoutReached ){
						$timeout.cancel(tmpPresetStyle.timeout);
					}
					$timeout(function(){
						tmpPresetStyle.timeout = 
							$timeout(function(){
								tmpPresetStyle.timeoutReached = true;
								// Reset actual target item's color in order that selection indicating color is shown
								tmpPresetStyle.style = { 'background-color' : ''};
							}, 1200); 
						}, 0);
					
					//~ Handle target's state immediate actuation
					if($scope.applyStateImmediately){
						var state2Apply = angular.copy(presetState);
						var state = angular.copy(entityState);
						state.transitionDelay = 0;
						state2Apply.states = [state];
						//~ Apply to actual model
						switch(state2Apply.type){
							case('BULB'):
								BulbService.executeActCmd(state2Apply);
								break;
							case('GROUP'):
								GroupService.executeActCmd(state2Apply);
								break;
						}
					}
				};
                /**
                 * Intended to be used by child directives
                 * @param updatedStates All states of currently selected entity
                 */
                $scope.updateCurrEntityStates = function(updatedStates, modSourceDirective){
                    if(typeof (updatedStates) === 'undefined' || updatedStates == null)return;
//                    console.log("PresetStates updated; new length: " + updatedStates.length);

                    $scope.presetStateSelected.states = updatedStates;
                    $scope.selectPresetState($scope.stateSelectedIdx);
                    $scope.modifyEntityState_Color(
                            $scope.entitySelected,
                            $scope.presetStateSelected.states[$scope.stateSelectedIdx].color,
                            modSourceDirective
                    );
                }

				$scope.entityStyle = function(pEntity){
					var tmpEntityStyle = null;
					angular.forEach($scope.entityStyles, function(styleObj){
						if(styleObj.entity === pEntity)tmpEntityStyle = styleObj;
					});
					if(tmpEntityStyle === null){
						tmpEntityStyle = {
							entity : pEntity,
							style : { 'background-color' : '' },
							'timeoutReached' : true
						};
						$scope.entityStyles.push(tmpEntityStyle);
					}
					
					if($scope.editMode && tmpEntityStyle.timeoutReached){
						tmpEntityStyle.style = {'background-color' : ''};
					}else{
						var pState = $scope.presetStateFor(pEntity);
						if(pState === null)
							tmpEntityStyle.style = {'background-color' : ''};
						else{
							var bgColor = d3.hcl(ColorConverter.fromServerColor(
									pState.states[$scope.stateSelectedIdx != null ? $scope.stateSelectedIdx : 0].color)
									.colorHsl);
							tmpEntityStyle.style = {'background-color' : bgColor.toString()};
						}
					}
					return tmpEntityStyle;
				};
				//
				$scope.presetStateFor = function(entity){
					var devIdx, groupIdx = null;
					var presetState;
					devIdx = $scope.availableDevices.indexOf(entity);
					groupIdx = $scope.availableGroups.indexOf(entity);
					if(devIdx != -1){
						presetState = $scope.findExistingPresetState_Device($scope.availableDevices[devIdx]);
						if(presetState === null){
							presetState = PresetService.newBulbActCmd(entity);
							$scope.presetStates.push(presetState);
							presetState.states.push(entity.state);
						}
					}else if (groupIdx != -1){
						presetState = $scope.findExistingPresetState_Group($scope.availableGroups[groupIdx]);
						if(presetState === null){
							presetState = PresetService.newGroupActCmd(entity);
							$scope.presetStates.push(presetState);
							if(typeof(entity.bulbs[0]) !== 'undefined')
								presetState.states.push(entity.bulbs[0].state);
						}
					}else{
//						throw new Error("Entity could not be found in model: " + entity);
						return null;
					}
					return presetState;
					
				};
				$scope.actualPresetStateFor = function(entity){
					if($scope.isBulb(entity)){
						return entity.state;
					}
					if($scope.isGroup(entity)){
						return entity.bulbs[0].state;
					}
					return null;
				};
				$scope.findExistingPresetState_Device = function(device){
					var res = null;
					angular.forEach($scope.presetStates, function(actCmdState){
						if('BULB' !== actCmdState.type)return;
						if(BulbService.idsEqual(actCmdState.bulbId, device.bulbId) )res = actCmdState;
					});
					return res;
				};
				$scope.findExistingPresetState_Group = function(group){
					var res = null;
					angular.forEach($scope.presetStates, function(actCmdState){
						if('GROUP' !== actCmdState.type)return;
						if(group.groupId === actCmdState.groupId )res = actCmdState;
					});
					return res;
				};
				
				$scope.isGroup = function(entity){
					return $scope.availableGroups.indexOf(entity) > -1;
				};
				$scope.isBulb = function(entity){
					return $scope.availableDevices.indexOf(entity) > -1;
				};
				$scope.isLoopable = function(){
					var res = false;
					angular.forEach($scope.preset.states, function(presetState){
						if(presetState.states.length > 1){
							res = true;
							return ;
						}
					});
					return res;
				};
                //

				//
				$scope.scrollToThis = function(){
					$timeout(function(){
						//~ Scroll to specific preset
						var old = $location.hash();
						$location.hash($scope.domIdPreset);
						$anchorScroll();
						$location.hash(old); // Prevent page reload!
					}, 0);
				};

                //~ Schedules
                $scope.showScheduler = false;
                $scope.switchShowScheduler = function(){
                    $scope.showScheduler = !$scope.showScheduler;
                };
				
				//
				$scope.initAll = function(){
					BulbService.bulbsByUser().then(
						function(dataBulbs){
							GroupService.groupsByUser().then(
								function(dataGroups){
									$scope.availableGroups = dataGroups;
									$scope.availableDevices = dataBulbs;
									PresetService.presetsByUser().then(function(presets){
										$scope.domIdPreset = "preset_" +  presets.indexOf($scope.preset);
									}, function(err){});
									$scope.init();
									$scope.calcActualAvailableEntities();
								}, function(error){
									console.log("TODO: Handle following error globally!");
									console.log(error);
							});
						}, function(error){
							console.log("TODO: Handle following error globally!");
							console.log(error);
					});
				};
				$scope.init = function(){
					$scope.presetBackup = angular.copy($scope.preset);
					if( !BulbService.isInitialized() || !GroupService.isInitialized()) return ;
					angular.forEach($scope.preset.states, function(state){
						switch(state.type){
							case 'BULB':
								$scope.presetDevices.push(BulbService.bulbById(state.bulbId));
								break;
							case 'GROUP':
								$scope.presetGroups.push(GroupService.groupById(state.groupId));
								break;
							default:
								throw new Error("Couldn't determine type of preset state.");
						}
					});
					$scope.presetEntities = [];
					$scope.presetEntities.addAll($scope.presetDevices);
					$scope.presetEntities.addAll($scope.presetGroups);
					$scope.presetStates = $scope.preset.states;
					$scope.entityStyles = [];
	
					$timeout(function(){
						$scope.presetIndicatorColors = [];
						angular.forEach(
							$scope.preset.states, function(presetState){
								$scope.presetIndicatorColors.push(presetState.states[0].color);
							} 
						);
					}, 0 );
					if($scope.editMode) $scope.scrollToThis();
				};
				$scope.initAll();
					
			}
		};
	}])

	.directive('colorSetIndicator', ['$window', function($window) {
		return  {
			replace : false,
//			template : 
			restrict : 'A',
			scope : {
				colors: '=?',
				height: '@'
			},

			link : function link($scope, tElement, iAttrs){
//				if(typeof($scope.colors) === 'undefined')return ;
				$scope.actualWidth = function(){
                    return tElement[0].offsetWidth;
                }
				$scope.init = function(){
					if(typeof($scope.colors) === 'undefined' || $scope.colors.length < 1 )return ;
                    if(typeof($scope.indicatorWrp) !== 'undefined' ) {
                        angular.element($scope.indicatorWrp[0]).children().remove();
                    }
					$scope.colorsHsl = [];
					angular.forEach($scope.colors, function(c){
						var color = new ColorConverter().fromServerColor(c).colorHsl;
						$scope.colorsHsl.push(color.toString());
					});
					$scope.indicatorWrp = d3.select(tElement[0]);
					$scope.gradientWrp = $scope.indicatorWrp
							.append("svg:svg")
							.attr("width", $scope.actualWidth())
							.attr("height", $scope.height)
							.style("fill", 'none')
							.style("border", '1px solid #e7e7e7')
//							.style("position", 'absolute')
					;
					var grdId = "ind_grd" + Math.random() * 100 ;
					$scope.pn_gradient = $scope.gradientWrp.append("svg:rect")
						.attr("width", '100%')
						.attr("height", '100%' )
//						.attr("fill", "none")
				    ;
					$scope.gradient = $scope.gradientWrp
							.append( 'linearGradient' )
							.attr("id", grdId )
							.attr( 'x1', '0%' )
							.attr( 'x2', '100%' )
							.attr( 'y1', '0' )
							.attr( 'y2', '0' );
					
					var i = 0;
					var step = 100 / $scope.colorsHsl.length; 
					angular.forEach($scope.colorsHsl, function(color){
						$scope.gradient.append( 'stop' )
							.attr( 'stop-color', color )
							.attr( 'offset', i * step + '%' );
							i++;
							
					});

					$scope.pn_gradient.attr("fill", "url(#"+ grdId + ")");
				};
				$scope.$watch('colors', function(colors){
					$scope.init();
				});
                $(window).resize(function() {
				    $scope.init();
                });
			}
		};
	}])

	.directive('linechartSelector', ['ColorConverter', '$timeout',  '$anchorScroll',
            function(ColorConverter, $timeout, $anchorScroll) {
		return {
			replace : false,
			restrict : 'A',
//            require : { ngShow: $ngShow},
			scope : {
				widgetId: '@',
				states: '=',
				valueType: '@',
                statesUpdated: '&',
                stateSelected: '&',
                lastChangeSrc : '=',
                enabled : '='
			},
			link : function link($scope, tElement, attrs){
				$scope.lcsStates = [];

                $scope.timeTotal = 1000;
                $scope.timeTotalOld = 0;
                $scope.stateIndex = 0;
                $scope.directiveId = "LCS";

                $scope.lcsModel = {
                    timeTotal : 1000,
                    timeTotalOld : 1000
                };

                $scope.actualWidth = function(){
                    var res = tElement[0].offsetWidth;
                    var offsetY = angular.element('.modeselector')[0].offsetWidth + 40;

                    return res - offsetY;
                }
//                $scope.presetLength = 0;

                $scope.valueConverterForType_FromLcsValue = function(valueType){
                    switch(valueType){
                        case "hue":
                            return function(referenceColorIn, lcsValueIn){
                                var color_hsl = ColorConverter.fromServerColor(referenceColorIn).colorHsl;
                                color_hsl.h = lcsValueIn * 360;
                                return ColorConverter.fromHslD3(color_hsl).toServerColor("HSB");
                            };
                        case "saturation":
                            return function(referenceColorIn, lcsValueIn){
                                var color_hsl = ColorConverter.fromServerColor(referenceColorIn).colorHsl;
                                color_hsl.s = lcsValueIn;
                                return ColorConverter.fromHslD3(color_hsl).toServerColor("HSB");
                            };
                        case "brightness":
                            return function(referenceColorIn, lcsValueIn){
                                var color_hsl = ColorConverter.fromServerColor(referenceColorIn).colorHsl;
                                color_hsl.l = lcsValueIn;
                                return ColorConverter.fromHslD3(color_hsl).toServerColor("HSB");
                            };
                        default:
                            throw new Error("valueType not supported: " + valueType);
                    }
                }
                $scope.valueConverterForType_ToLcsValue = function(valueType){
                    switch(valueType){
                        case "hue":
                            return function(colorIn){
                                return ColorConverter.fromServerColor(colorIn).colorHsl.h / 360;
                            };
                        case "saturation":
                            return function(colorIn){
                                return ColorConverter.fromServerColor(colorIn).colorHsl.s;
                            };
                        case "brightness":
                            return function(colorIn){
                                return ColorConverter.fromServerColor(colorIn).colorHsl.l;
                            };
                        default:
                            throw new Error("valueType not supported: " + valueType);
                    }
                };
                $scope.stateUpdateCb = function(lcsStatesUpdated){
                    if(typeof(lcsStatesUpdated) === 'undefined')return ;
                    $scope.lcsStates = lcsStatesUpdated;
                    var resStates = $scope.fromLcsStates();
                    $timeout(function(){
                        $scope.statesUpdated({states : resStates, directiveId : $scope.directiveId});
                    }, 0);
                };
                $scope.stateSelectionCb = function(lcsState, index){
                    $scope.stateIndex = index;
                    console.log("State selected: " + index);
                    $timeout(function(){
                        $scope.stateSelected({index : index});
                    }, 0);
                };
                $scope.stateAddCb = function(lcsStatesUpdated, newStateIndex){
                    var copyFromIdx = newStateIndex > 0 ? newStateIndex - 1 : 0;
                    var resState = angular.copy($scope.states[copyFromIdx]);
                    var mergeColorAndLcsValue = $scope.valueConverterForType_FromLcsValue($scope.valueType);
                    var lcsState = lcsStatesUpdated[newStateIndex];
                    resState.transitionDelay = lcsState.transitionDelay;
                    resState.color = mergeColorAndLcsValue(resState.color, lcsState.value);
                    $scope.states.splice(newStateIndex, 0,  resState);

                    $timeout(function(){
                        $scope.statesUpdated({states : $scope.states});
                    }, 0)
                }

                $scope.updateTransitionDelaysOf = function (lcsStates) {
                    var lastPtInTime = 0;
                    angular.forEach(lcsStates, function (state) {
                        state.transitionDelay = Math.round(state.time) - lastPtInTime;
                        lastPtInTime = state.time;
                    });
                };
                $scope.fromLcsStates = function(){
                    $scope.updateTransitionDelaysOf($scope.lcsStates);
                    var resStates = [];
                    var currOrigIdx = 0;
                    var currOrigState = $scope.states[currOrigIdx];
                    var currOrigTime = $scope.states[currOrigIdx].transitionDelay;
                    var mergeColorAndLcsValue = $scope.valueConverterForType_FromLcsValue($scope.valueType);

                    angular.forEach($scope.lcsStates, function(lcsState){
                        currOrigState = $scope.states[currOrigIdx];
                        if(typeof (currOrigState) === 'undefined') {
                            currOrigState = angular.copy($scope.states[$scope.states.length - 1])
                        }
                        currOrigTime += currOrigState.transitionDelay;
                        var resState;
                        resState = angular.copy(currOrigState);
                        resState.transitionDelay = lcsState.transitionDelay;
                        resState.timeKey = lcsState.time;
                        resState.color = mergeColorAndLcsValue(resState.color, lcsState.value);
                        resStates.push(resState);
                        currOrigIdx++;
                    });
                    return resStates;
                }
				$scope.toLcsStates = function(valueType){
                    $scope.lcsStates = [];
					var time = 0;
                    var valueConvert = $scope.valueConverterForType_ToLcsValue( valueType );
					angular.forEach($scope.states, function(el){
						time = time + el.transitionDelay;
                        var value = valueConvert(el.color);
						$scope.lcsStates.push({
                            //~ Required for LCS
							time : time,
							value: value,
                            //~ Additional
                            transitionDelay : el.transitionDelay
						});
					});
                    $scope.lcsModel.timeTotal = time;
                    $scope.lcsModel.timeTotalOld = time;
				};
                $scope.setAndApplyNewTotalTime = function(timeTotNew){
                    console.log("TimeTotal old/new: " + $scope.lcsModel.timeTotalOld +  "; " + timeTotNew);
                    if($scope.lcsModel.timeTotalOld === timeTotNew)return;
                    if(timeTotNew < 1)return;
                    if($scope.lcsModel.timeTotalOld < 1 || timeTotNew < 1){
                        var state = $scope.lcsStates[0];
                        state.time = 0;
                        $scope.lcsStates.removeAll();
                        $scope.lcsStates.push(state);
                    }else{
                        angular.forEach($scope.lcsStates, function (state) {
                            state.time = Math.round( (state.time / $scope.lcsModel.timeTotalOld) * timeTotNew);
                        });
                    }
                    $scope.lcsModel.timeTotal = timeTotNew;
                    $scope.lcsModel.timeTotalOld = timeTotNew;
                    $scope.updateTransitionDelaysOf($scope.lcsStates);
                    $scope.stateUpdateCb($scope.lcsStates);
                    $scope.initLcsWidget();
                };
                $scope.setValueType = function(valueType){
                    console.log("New value type selected: " + valueType);
                    $scope.valueType = valueType;
                    $scope.init();
                };

                $scope.initLcsWidget = function(){
                    if(typeof($scope.lcs) !== 'undefined'){
                        $scope.lcs.removeFromParent();
                    }
                    $scope.lcs = new LinechartSelector(
//                        200,
                        $scope.actualWidth(),
                        tElement.find(".lcs-container")[0],
                        $scope.lcsStates,
                        $scope.lcsModel.timeTotal,
                        $scope.stateUpdateCb,
                        $scope.stateSelectionCb,
                        $scope.stateAddCb
                    );
                    $scope.lcs.init();
                }

                //~ Start it up
                $scope.init = function(){
                    $scope.toLcsStates($scope.valueType);
                    $scope.initLcsWidget();
                };
                $scope.init();
                $scope.$watch('widgetId', function(srcStatesUpdated){
                    console.log("Src states updated with length: " + srcStatesUpdated.length);
                    $scope.init();
                });
                $scope.$watch('enabled', function(enabled){
                    if(enabled){
                        $scope.init();
                        if($scope.lcsModel.timeTotal <= 0) {
                            $scope.setAndApplyNewTotalTime(1000);
                            if($scope.lcsStates.length <2) {
                                // Make sure, at least 2 states exist
                                $scope.lcsStates.push({
                                    //~ Required for LCS
                                    time : $scope.lcsModel.timeTotal,
                                    value: $scope.lcsStates[0].value,
                                    //~ Additional
                                    transitionDelay : $scope.lcsModel.timeTotal
                                });
                                $scope.stateUpdateCb($scope.lcsStates);
                                $scope.initLcsWidget();
                            }
                        }
                    }else{
                        if(typeof($scope.lcs) !== 'undefined'){
                            $scope.lcs.removeFromParent();
                        }
                    }
                });
                window.onresize = function(){
                    $scope.initLcsWidget();
                };
//                $scope.$watch('lastChangeSrc', function(newVal){
//
//                    console.log("[LCS] - |-- lastStateChangeSrc: " + newVal + "| " + $scope.lastStateChangeSrc);
//                });
            }
		};
	}])

    .directive('millisToSecondsConverter',  function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attr, ngModel) {
                function fromUser(fromUser) {
                    if(fromUser == null || fromUser.length == 0)return "";
                    return fromUser * 1000;
                }

                function toUser(fromModel) {
                    if(fromModel == 0)return 0;
                    return Math.round(fromModel / 100 ) / 10;
                }
                ngModel.$parsers.push(fromUser);
                ngModel.$formatters.push(toUser);
            }
        };
    })

    .directive('modeselector', function(){
        return{
            replace : false,
            restrict : 'A',
            scope : {
                onModeChange : '&',
                mode : '='
            },
            link : function(scope, element, attr) {
                console.log("Init modeselector");
                scope.modeSelected = scope.mode;

                scope.selectMode = function(mode){
                    console.log("Going to select mode: " + mode);
                    scope.modeSelected = mode;
                    scope.onModeChange({modeSelected : scope.modeSelected});
                };
                scope.isModeSelected = function(mode){
                    if(scope.modeSelected == null)return false;
                    return scope.modeSelected === mode;
                };
                scope.$watch('mode', function(mode){
                    scope.modeSelected = mode;
                });
            }
        };
    })

    //DEPRECATED (?)
    .directive('bulbsCoreNavbarAffix', function(){
        return {
            replace : false,
            restrict : 'A',
            scope : false,
            link : function link($scope, tElement, attrs){
                $(tElement).affix({
                    offset: {
                        top: 0,
                        bottom : 0
//                        bottom: function () {
//                            return (this.bottom = $('.footer').outerHeight(true))
//                        }
                    }
                });
            }
        };
    })

    .directive('pointInTimeScheduler', ['ScheduledActuationService', function(ScheduledActuationService){
        return {
            replace : true,
            restrict : 'EA',
            templateUrl : 'bulbsCore/partials/directives/pointInTimeScheduler.html',
            scope : {
                visible : '=',
                preset : '='
            },
            link : function link($scope, tElement, attrs){
                var model = $scope;
                model.schedViewModel = {};
                model.scheduler = {};

                model.save = function(){
                    console.log("Save btn pressed.");
                    ScheduledActuationService.createSchedule(model.scheduler);
                };

                model.inputTimeChanged = function(){
                    console.debug("Model hcanged");
                    if(model.schedViewModel.hourOfDay >= 0 && model.schedViewModel.minOfHour >= 0 ){
                        var triggerTime = new Date();
                        triggerTime.setHours(model.schedViewModel.hourOfDay);
                        triggerTime.setMinutes(model.schedViewModel.minOfHour);
                        if(triggerTime.getTime() < new Date().getTime()){
                            triggerTime.setHours(triggerTime.getHours() + 24);
                        }
                        model.scheduler.trigger.startAt = triggerTime.getTime();
                        console.debug("New Trigger time set: " + triggerTime);
                    }
                };

                model.init = function(){
                    console.debug("Init PIT Scheduler.");
                    var scheduler = ScheduledActuationService.newSchedulerForPreset(model.preset);
                    model.scheduler = scheduler;
                };

                model.$watch('visible', function(){
                    if(model.visible){
                        model.init();
                    }
                });
            }
        };
    }])


;