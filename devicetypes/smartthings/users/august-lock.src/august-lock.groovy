/**
 *  Copyright 2017 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  An enhanced virtual lock that allows for testing failure modes
 *  Author: SmartThings
 *  Date: 2017-08-07
 *
 */

preferences {
    input("lockID", "text", title: "Lock ID")
}

metadata {
    // Automatically generated. Make future change here.
    definition (name: "August Lock", namespace: "smartthings/users", author: "SmartThings") {
        capability "Actuator"
        capability "Sensor"
        capability "Health Check"

        capability "Lock"
        capability "Refresh"
    }

    // Simulated lock
    tiles {
        multiAttributeTile(name:"toggle", type: "generic", width: 6, height: 4){
            tileAttribute ("device.lock", key: "PRIMARY_CONTROL") {
                attributeState "locked", label:'locked', action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#00A0DC", nextState:"unlocking"
                attributeState "unlocked", label:'unlocked', action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#FFFFFF", nextState:"locking"
                attributeState "unknown", label:'jammed', action:"lock.lock", icon:"st.secondary.activity", backgroundColor:"#E86D13"
                attributeState "locking", label:'locking', icon:"st.locks.lock.locked", backgroundColor:"#00A0DC"
                attributeState "unlocking", label:'unlocking', icon:"st.locks.lock.unlocked", backgroundColor:"#FFFFFF"
            }
        }

        standardTile("lock", "device.lock", inactiveLabel: false, decoration: "flat", width: 3, height: 2) {
            state "default", label:'lock', action:"lock.lock", icon: "st.locks.lock.locked"
        }
        standardTile("unlock", "device.lock", inactiveLabel: false, decoration: "flat", width: 3, height: 2) {
            state "default", label:'unlock', action:"lock.unlock", icon: "st.locks.lock.unlocked"
        }
        
        standardTile("refresh", "device.lock", inactiveLabel: false, height: 2, width: 2, decoration: "flat") {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main "toggle"
        details([ "toggle", "lock", "unlock", "refresh" ])
    }
}
// parse events into attributes
def parse(String description) {
    log.trace "parse $description"
}

def installed() {
    log.trace "installed()"
    initialize()
}

def updated() {
    log.trace "updated()"
    // processPreferences()
    initialize()
}

def initialize() {
    log.trace "initialize()"
    runEvery15Minutes(poll)
}

def refresh() {
 	log.debug "Refreshing"
	poll()
}


def poll() {
    //sendEvent(name: "lock", value: device.currentValue("lock"))
    //sendEvent(name: "battery", value: device.currentValue("battery"))
    log.debug "Polling"
	sendReq('status');
}

def ping() {
    refresh()
}

def lock() {
    log.trace "lock()"
    sendReq('lock');
}

def unlock() {
    log.trace "unlock()"
    sendReq('unlock');
}

def sendReq(action) {
    log.trace "Action ${action}"
    def params = [
        uri: "https://api-production.august.com/remoteoperate/32C6E38489244D6F9163BB07095E7160/" + action,
        headers: [
            'x-august-api-key': '79fd0eb6-381d-4adf-95a0-47721289d1d9',
            'x-kease-api-key': '79fd0eb6-381d-4adf-95a0-47721289d1d9',
            'Content-Type': 'application/json',
            'x-august-access-token': 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpbnN0YWxsSWQiOiIwIiwiYXBwbGljYXRpb25JZCI6IiIsInVzZXJJZCI6Ijg5MjEzYWIxLWRiYmQtNDJjOC04NWQyLTgxZjk0MzljOTI1YSIsInZJbnN0YWxsSWQiOnRydWUsInZQYXNzd29yZCI6dHJ1ZSwidkVtYWlsIjpmYWxzZSwidlBob25lIjp0cnVlLCJoYXNJbnN0YWxsSWQiOnRydWUsImhhc1Bhc3N3b3JkIjp0cnVlLCJoYXNFbWFpbCI6dHJ1ZSwiaGFzUGhvbmUiOnRydWUsImlzTG9ja2VkT3V0IjpmYWxzZSwiY2FwdGNoYSI6IiIsImVtYWlsIjpbXSwicGhvbmUiOlsicGhvbmU6KzE1ODU3NDgwMjcyIl0sImV4cGlyZXNBdCI6IjIwMjAtMDMtMTRUMTU6NTA6NDYuNzc0WiIsInRlbXBvcmFyeUFjY291bnRDcmVhdGlvblBhc3N3b3JkTGluayI6IiIsImlhdCI6MTU3MzgzMjk1NywiZXhwIjoxNTg0MjAxMDQ2LCJMYXN0TmFtZSI6Ik5ndXllbiIsIkZpcnN0TmFtZSI6IlZpZXQifQ.IE74owGSyk4c690grf_Mx3L6NB9ve8yatRHePbHl0OM'
        ]
    ]

    try {
        httpPut(params) { resp ->
            resp.headers.each {
                log.debug "${it.name} : ${it.value}"
            }
            log.debug "response contentType: ${resp.contentType}"
            log.debug "response data: ${resp.data}"
            log.debug "response status: ${resp.data.status}"
            
            if(resp.data.status == 'kAugLockState_Locked'){
            	sendEvent(name: "lock", value: "locked")
            }else{
            	sendEvent(name: "lock", value: "unlocked")
            }
        }
    } catch (e) {
        log.debug "something went wrong: $e"
    }
}
