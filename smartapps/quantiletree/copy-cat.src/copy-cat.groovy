/**
 *  Dims a collection of dimmers together. Changing any one will send events to all of the others.
 *  This is unlike the "Dim With Me" app, which is one-to-many. This is many-to-many.
 *
 *  Copyright 2015 Michael Barnathan (michael@barnathan.name)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

definition(
        name: "Copy Cat",
        namespace: "quantiletree",
        author: "Michael Barnathan",
        description: "Dims a collection of dimmers together. Changing any one will dim all of the others.",
        category: "Convenience",
        iconUrl: "http://cdn.device-icons.smartthings.com/Weather/weather13-icn@2x.png",
        iconX2Url: "http://cdn.device-icons.smartthings.com/Weather/weather13-icn@2x.png"
)

preferences {
    section("Select devices to dim together:") {
        input "dimmers", "capability.switch", required:true, title:"Switches", multiple:true
    }
}

def installed() {
    log.info "Dimmers tied! Settings: ${settings}"
    initialize()
}

def updated() {
    log.info "Dimmer ties updated: ${settings}"
    initialize()
}

def initialize() {
    unsubscribe()
    subscribeTo(dimmers ?: [])
    atomicState.deviceQueue = [:]
    atomicState.rootDevice = ""
}

private subscribeTo(devices) {
    subscribe(devices, "switch.on", tieOn)
    subscribe(devices, "switch.off", tieOff)

}

def tieOn(event) {
    sendNotificationEvent("${event.displayName} on, turning on ${dimmers.size()} dimmers")
    dimmers?.on()
}

def tieOff(event) {
    dimmers?.off()
}