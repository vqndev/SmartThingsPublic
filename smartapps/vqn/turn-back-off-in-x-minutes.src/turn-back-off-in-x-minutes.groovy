definition(
    name: "Turn back off in x minutes",
    namespace: "vqn",
    author: "Viet Nguyen",
    description: "When a switch turns on, turn it back off in x minutes",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section("When it turns on..."){
		input "switch1", "capability.switch"
	}
	section("Turn it off in how many minutes"){
                input("minutes", "number")
	}
    section( "Notifications" ) {
        input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes","No"]], required:false
        input "phone1", "phone", title: "Send a Text Message?", required: false
    }
}

def installed() {
	log.debug "Installed"
        init()
}

def updated() {
	log.debug "Updated with settings: ${minutes}"
	unsubscribe()
	init()
}

def init() {
        subscribe(switch1, "switch.on", switchOnHandler)
}
def switchOnHandler(evt) {
    send("Turning back off ${switch1.displayName} in ${minutes} min(s)")
	def theDelay = 60 * minutes;
	runIn(theDelay, turnOffSwitch)
}

def turnOffSwitch() {
	send("Turned off ${switch1.displayName}")
	switch1.off()
}

private send(msg) {
    if ( sendPushMessage != "No" ) {
        log.debug( "sending push message" )
        sendPush( msg )
    }

    if ( phone1 ) {
        log.debug( "sending text message" )
        sendSms( phone1, msg )
    }

    log.debug msg
}