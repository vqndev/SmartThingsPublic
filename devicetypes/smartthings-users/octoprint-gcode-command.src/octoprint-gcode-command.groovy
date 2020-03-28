/**
 *  Octoprint GCode Command Switch
 *
 *  Copyright 2018 Viet Nguyen
 *  For any information or help please contact ad@mclark.co
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

import java.security.MessageDigest

preferences {
    input("serverIP", "text", title: "Octoprint Server IP Address", description: "IP Address of the Server")
    input("steps", "text", title: "Steps", description: "Steps in MM")
    input("octoAPIKey", "text", title: "API Key", description: "The OctoPrint API Key.")
}

metadata {
    definition (name: "OctoPrint GCode Command", namespace: "smartthings-users", author: "Viet Nguyen") {
        capability "Switch"
        capability "Switch Level"
    }

    simulator {}

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"off"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"On"
            }
        }
		controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
			state "level", action:"switch level.setLevel"
		}
        valueTile("level", "device.level", inactiveLabel: false, decoration: "flat") {
			state "level", label: 'Level ${currentValue}%'
		}
        main "switch"
        details(["switch", "level", "levelSliderControl"])
    }

}

// parse events into attributes
def parse(String description) {


}

def setLevel(value) {
	log.trace "latest level" + device.latestValue("level")
	log.trace "setLevel($value)"
    log.trace "device"
    log.trace device.latestValue("switch")
    sendEvent(name: "level", value: value)
    
    def percen = ( (value/100)*settings.steps.toInteger() ) - settings.steps.toInteger();
    percen = percen.abs()
    apiGet(percen)
    log.trace "percen ($percen)"
    
}

// handle commands
def on() {
    
    
    	apiGet(0)
        sendEvent(name: "switch", value: 'on')
    
    
    
}

def off() {
    
   
    	apiGet(settings.steps)
        sendEvent(name: "switch", value: 'off')
    
    
}

private apiGet(path) {


    log.debug "Posting Body: asdf"
	def result = new physicalgraph.device.HubAction(
        method:     'POST',
        path:       '/api/printer/command',
        headers:    [
            HOST:       settings.serverIP + ':80',
            'Content-Type': 'application/json',
            'X-Api-Key': settings.octoAPIKey
        ],
        body: [ "commands": [ "G90", "G1 X" + path + " F800", "M84" ] ]
    )
	log.debug "aaa ${result}"

    return sendHubCommand(result)

}