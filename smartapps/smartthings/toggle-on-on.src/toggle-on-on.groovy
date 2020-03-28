/**
 *  Copyright 2015 SmartThings
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
 *  Turn It On For 3 Minutes
 *  Turn on a switch when a contact sensor opens and then turn it back off 2 minutes later.
 *
 *  Author: SmartThings
 */
definition(
    name: "Toggle on On",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Toggles things when things are on",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section("When it is on"){
		input "contact1", "capability.switch"
	}
	section("Toggle something"){
		input "switch1", "capability.switch"
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe(contact1, "switch.on", contactOpenHandler)
}

def updated(settings) {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	subscribe(contact1, "switch.on", contactOpenHandler)
}

def contactOpenHandler(evt) {
        log.debug "oh yeah ${switch1.currentValue('switch')}"
        if(switch1.currentValue('switch').contains('on')){
        	switch1.off()
        }else{
        	switch1.on()
        }
        contact1.off();
        

}