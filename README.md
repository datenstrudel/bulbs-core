![Build Status](https://api.travis-ci.org/datenstrudel/bulbs-core.svg?branch=refactoring/integrateSpringData)

# Bulbs-Core

Bulbs-Core is a __domestic lighting control middleware__ written in Java.

It provides a RESTful API, abstracting from illuminant vendor specific controller details. The abstraction is supposed to
    be used to build special purpose applications on top of it, _without_ beeing dependent on specific hardware details. 
    However, the API provided adds functionality on top of common light actuation commands, such as group controls, fluent state transitions or scheduling.
    
_Illuminant_ in this context means (potentially) any kind of (RGB/HSL) light bulb or light source that is capable of processing commands over a local area network, 
like [LIFX](http://lifx.co/) or [Phlips Hue](http://meethue.com).
Currently there is just an adapter implementation for _Philips Hue_. The _LIFX_ implementation will follow.

This module is supposed to be deployable on __single-board computers__, capable of running a JVM like the [Raspberry Pi](http://www.raspberrypi.org/) or [Banana Pi](bananapi.org) in order 
    to provide a stateful, always-available, local control system with low energy consumption.

It ships with a simple, browser based, API trialing user interface that is mainly based on [AngularJs](https://angularjs.org/), [D3.js](http://http://d3js.org/) and [BootStrap](getbootstrap.com).

## Overview
<img width="800" height="274" src="/doc/presentation/assets/bulbs_bigPic_dark_full.png" alt="Big Pic"/>

## Purpose

 * Vendor independent control adapter serving as control facade for client applications.
    
    This artifact can be seen as a locally running service, accepting commands from client applications that
    could run anywhere. Once authenticated, having an API key, the service accepts applications' commands.
    A priority coordination mechanism helps to prevent interfering commands coming from different apps.
    

 * Keep centralized state to facilitate user interface (device) independent features.
 
    Keeping users' configurations, such as `Presets` or `Schedules` at a centralized service simplifies synchronization 
    between instances of different UIs or applications.
    The module allows to keep different UIs or applications in sync at almost 
    realtime depending on processing speed of the hosting hardware. 


 * Allow definition and execution of (more complex) light state transitions, whose processing and execution cannot
   be triggered from a UI client.

## Features
The API is still work in progress. So far some vital functionality has been implemented.

### Functional
1. Security - User/App authentication and (future) rights management
2. Bulb actuation
    * Single
    * Transitions
3. Groups for multiple actuation
4. Presets - store bulbs', groups' states and state transitions
5. Schedules - Trigger commands for bulbs, groups, presets by temporal rules

#### User Interface
After startup of the application, navigating to its root context in the browser will bring up the 
web application, mentioned above..

<img width="200" height="291" src="/doc/assets/bulbs_hc.png" alt="Big Pic"/>


### Technical

#### API
Besides the swagger documentation that is available (with Maven Profile `debug` activated) within the module at runtime (`/swagger/index.html`) 
a brief API description will be given here.. 

---

---
## General Architectual Overview
// TODO

## Development
### Build and start the application
 * Checkout with submodules by `git clone --recursive git://github.com/datenstrudel/bulbs-core.git`
 * In order to build and start this project run `vagrant up`. ([Vagrant](https://www.vagrantup.com) required!)
        This will start a virtual machine, hosting required infrastructure modules, such as running instances of MongoDB and RabbitMq.
 * Run `mvn clean install`
 * Alternatively run `mvn clean install -Pdebug` to skip integration tests. For building and unit tests only, no vagrant box is required.
 * Run the `Main` class that can be found in package `net.datenstrudel.bulbs.core.main`
 * Browse to `http://localhost:8084`, create an account and an emulated gateway to play around. If you already have Philips Hue bulbs, you can of course start directly with these.

### Debug with usage of [Spring-Loaded](https://github.com/spring-projects/spring-loaded)
Working with spring-loaded allows you to make changes to the code at runtime, recompile and have them ultimatively available without the
 need to restart the application.

 By running

 `mvn spring-boot:run -Pdebug`
 
 the application starts in debug mode, waiting for a debugger connection at port 5005.
 Within your IDE you might find the spring-boot plugin somewhere along the build plugins and just start it from there (The mvn profile 'debug' must be active!).
 Now, within your IDE you can connect a debugger (e.g. remote runner in IntelliJ f.ex.) against that port 5005. When connected the application fully starts.
 Now breakpoints and immediate code changes should work in your debug session.

## Deployment
// TODO


