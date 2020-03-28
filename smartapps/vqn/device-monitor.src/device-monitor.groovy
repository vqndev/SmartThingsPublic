definition(
    name: "Device Monitor",
    namespace: "vqn",
    author: "Viet Nguyen",
    description: "Monitors Device",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section("Device to Monitor"){
		input "contact1", "capability.contactSensor"
	}
	section("Presence"){
		input "motion1", "capability.motionSensor"
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe(contact1, "contact", contactOpenHandler)
}

def updated(settings) {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	subscribe(contact1, "contact", contactOpenHandler)
}

def contactOpenHandler(evt) {
	if(evt.value == "open"){
    	motion1.active();
    }else{
    	motion1.inactive();
    }
        log.debug "${evt.name} : ${evt.value}"

}