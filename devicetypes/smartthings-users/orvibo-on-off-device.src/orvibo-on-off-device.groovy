/**
 *  Orvibo Switch
 *
 *  Copyright 2015 Adam Clark
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
    input("serverIP", "text", title: "Node Server IP Address", description: "IP Address of the Server")
    input("deviceMac", "text", title: "Device Mac Address", description: "The MAC address of the device.")
    input(name: "switchType", type: "enum", title: "Switch Type", options: ["Orvibo","ECO Plugs","RM2 Pro"])
    input("deviceIP", "text", title: "Device IP Address", description: "IP Address of the device")
}

metadata {
    definition (name: "Orvibo On Off Device", namespace: "smartthings-users", author: "Adam Clark") {
        capability "Switch"
        capability "Refresh"
        capability "Polling"
        command "subscribe"
        command "setOffline"
    }

    simulator {}

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"off"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"On"
                attributeState "offline", label:'${name}', icon:"st.switches.switch.off", backgroundColor:"#cccccc"
            }
        }
		
        standardTile("refresh", "device.switch", inactiveLabel: false, height: 2, width: 2, decoration: "flat") {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        
        main "switch"
        details(["switch","refresh"])
    }

}

// parse events into attributes
def parse(String description) {

	log.debug "PARSE $description"
}
def initialize() {
	log.debug "Initialize ECO."
    
 
}

// handle commands
def on() {
    sendEvent(name: "switch", value: 'on')
    apiGet('/switch', [ state : 'on' ])
}

def off() {
    sendEvent(name: "switch", value: 'off')
    apiGet('/switch', [ state : 'off' ])
}

def setOffline() {
    sendEvent(name: "switch", value: "offline", descriptionText: "The device is offline")
}

private apiGet(path, query) {

	query['mac'] = settings.deviceMac
    query['switchType'] = settings.switchType
    query['deviceIP'] = settings.deviceIP

	def result = new physicalgraph.device.HubAction(
        method:     'GET',
        path:       path,
        headers:    [
            HOST:       settings.serverIP + ':8001',
            Accept:     "*/*"
        ],
        query: query
    )

    return sendHubCommand(result)

}

def refresh() {
 	log.debug "Executing ECO/Orvibo refresh'"
	poll()
}

def updated() {
 	log.debug "Executing ECO/Orvibo UPDATED'"
    unschedule()
    runEvery10Minutes(poll)
}

def poll() {
 	log.debug "Executing ECO/Orvibo POLL'"
    //sendEvent(name: "switch", value: 'on')
  	def query = [
        'mac': settings.deviceMac,
    	'switchType':  settings.switchType,
    	'deviceIP': settings.deviceIP
    ]
    def cmd = new physicalgraph.device.HubAction([
        method:     'GET',
        path:       '/device',
        headers:    [
            HOST:       settings.serverIP + ':8001',
            Accept:     "*/*"
        ],
        query: query
    ], '', [callback: calledBackHandler])
	
    def result = sendHubCommand(cmd)
    
    
   
    log.debug "cmd: $cmd"
    log.debug "result: $result"
}

def subscribe() {
	log.debug "i am subscribe"
}




void calledBackHandler(physicalgraph.device.HubResponse hubResponse) {
	def body = hubResponse.json;
    	log.debug "body $body"
        if(body.state == 'ON'){
        	sendEvent(name: "switch", value: 'on')
        }else if(body.state == 'OFF'){
        	sendEvent(name: "switch", value: 'off')
        }else{
        	log.debug "OFFFFFFF"
        	setOffline()
        }
}