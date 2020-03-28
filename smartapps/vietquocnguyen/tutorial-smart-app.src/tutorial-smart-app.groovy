/**
 *  Tutorial Smart App
 *
 *  Copyright 2018 Viet Nguyen
 *
 */
definition(
    name: "Tutorial Smart App",
    namespace: "vietquocnguyen",
    author: "Viet Nguyen",
    description: "Hi!",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


    
preferences {
	page(name: "getPref")
}


def getPref() {    
    dynamicPage(name: "getPref", install:true, uninstall: true) {
    	section("Switches...") {
			input "switches", "capability.switch", title: "Switches", multiple: true, required: false
    	}
        section("Presence Sensors...") {
			input "presenceSensors", "capability.presenceSensor", title: "Presence", multiple: true, required: false
    	}
        section("Motion Sensors...") {
			input "motionSensors", "capability.motionSensor", title: "Motion", multiple: true, required: false
    	}
   
    	def phrases = location.helloHome?.getPhrases()*.label
			if (phrases) {
        		phrases.sort()
				section("Routines...") {
					input "routines", "enum", title: "Routines", options: phrases, multiple: true, required: false
				}
			}
		
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}

mappings {
  path("/presense") {
    action: [
      GET: "listPresense"
    ]
  }
  path("/presense/:presense/:command") {
    action: [
      GET: "getPresence"
    ]
  }
  path("/switches") {
    action: [
      GET: "listSwitches"
    ]
  }
  path("/routines") {
    action: [
      GET: "getRoutines"
    ]
  }
  path("/switch/:switch/:command") {
    action: [
      PUT: "putSwitch"
    ]
  }
  path("/motion/:motion/:command") {
    action: [
      PUT: "putMotion"
    ]
  }
  path("/routine/:routine") {
    action: [
      PUT: "putRoutine"
    ]
  }
  path("/test") {
    action: [
      GET: "testing"
    ]
  }
  path("/switches/:command") {
    action: [
      PUT: "updateSwitches"
    ]
  }
}

def getRoutines() {
	def resp = []
    settings.routines.each { routine ->
      resp << routine
    }
    return resp
}
def testing() {
	return [foo: "bar"];
}

def putRoutine() {
	def theRoutine = params.routine;
	def foundRoutine;
    settings.routines.each { routine ->
      if(routine == theRoutine){
      	foundRoutine = routine;
      }
    }
    
    if(foundRoutine){
    	log.debug "Found: ${foundRoutine}"
        location.helloHome.execute(foundRoutine)
    }else{
    	httpError(400, "Routine '${theRoutine}' was not found")
    }
}

def putSwitch() {
	def theSwitch = params.switch
    def command = params.command
    log.debug "Switch: ${theSwitch}";
    def foundSwitch;
    switches.each { object ->
    	if(object.displayName == theSwitch){
			foundSwitch = object;
        }
	}
    log.debug "Switch: ${foundSwitch}"
    if(foundSwitch){
    	if(command == "on"){
        	foundSwitch.on()
        }else if(command == "off"){
        	foundSwitch.off()
        } else if(command == "toggle"){
        	log.debug "current value: ${foundSwitch.currentState('switch').value}"
        	if(foundSwitch.currentState("switch").value == "on"){
            	foundSwitch.off()
            }else{
            	foundSwitch.on()
            }
        }
    	return [ switch: theSwitch, command: command ]
    }else{
    	httpError(400, "Switch '${theSwitch}' was not found")
    }
}

def putMotion() {
	def theMotion = params.motion
    def command = params.command
    log.debug "Motion: ${theMotion}";
    def foundMotion;
    motionSensors.each { object ->
    	if(object.displayName == theMotion){
			foundMotion = object;
        }
	}
    log.debug "Found Motion: ${foundMotion}"
    if(foundMotion){
    	if(command == "active"){
        	foundMotion.active()
        }else if(command == "inactive"){
        	foundMotion.inactive()
        } 
    	return [ motionSensor: theMotion, command: command ]
    }else{
    	httpError(400, "Motion Sensor '${theMotion}' was not found")
    }
}
// returns a list like
// [[name: "kitchen lamp", value: "off"], [name: "bathroom", value: "on"]]
def listSwitches() {
    def resp = []
    switches.each {
      resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    return resp
}

def listPresense() {
	def resp = []
    presenceSensors.each {
      resp << [name: it.displayName, value: it.currentValue("presence")]
    }
    return resp
}

def getPresence() {
	def thePre = params.presense
    def command = params.command
    def foundP;
    presenceSensors.each { object ->
    	if(object.displayName == thePre){
			foundP = object;
        }
	}

    if(foundP){
    	if(command == "arrived" && foundP.currentValue("presence") == "not present"){
        	foundP.arrived()
        }else if(command == "departed" && foundP.currentValue("presence") == "present"){
        	foundP.departed()
        }
    	return [ name: thePre, value: foundP.currentValue("presence"), command: command ]
    }else{
    	httpError(400, "Switch '${theSwitch}' was not found")
    }
}





void updateSwitches() {
    // use the built-in request object to get the command parameter
    def command = params.command

    // all switches have the command
    // execute the command on all switches
    // (note we can do this on the array - the command will be invoked on every element
    switch(command) {
        case "on":
            switches.on()
            break
        case "off":
            switches.off()
            break
        default:
            httpError(400, "$command is not a valid command for all switches specified")
    }
}

// TODO: implement event handlers

