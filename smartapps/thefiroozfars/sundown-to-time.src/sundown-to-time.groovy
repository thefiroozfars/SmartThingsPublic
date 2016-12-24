
definition(
    name: "Sundown to Time",
    namespace: "thefiroozfars",
    author: "thefiroozfars",
    description: "Turn on one or more switches at sunset and turn them off at a specified time.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2x.png"
)

preferences {
    section("Lights") {
        input "switches", "capability.switch", title: "Which lights to turn on?"
        input "offset", "number", title: "Turn on this many minutes before sunset"
		input name: "stopTime", title: "Turn Off Time?", type: "time"
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    unsubscribe()
    unschedule()
    initialize()
}

def initialize() {
    subscribe(location, "sunsetTime", sunsetTimeHandler)

    //schedule it to run today too
    scheduleTurnOn(location.currentValue("sunsetTime"))
    
    //schedule to turn off
    schedule(stopTime, "stopTimerCallback")
}

def sunsetTimeHandler(evt) {
    //when I find out the sunset time, schedule the lights to turn on with an offset
    scheduleTurnOn(evt.value)
}

def scheduleTurnOn(sunsetString) {
    //get the Date value for the string
    def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)

    //calculate the offset
    def timeBeforeSunset = new Date(sunsetTime.time - (offset * 60 * 1000))

    log.debug "Scheduling for: $timeBeforeSunset (sunset is $sunsetTime)"

    //schedule this to run one time
    runOnce(timeBeforeSunset, turnOn)
}

def turnOn() {
    log.debug "turning on switches"
    switches.on()
}

def stopTimerCallback() {
	log.debug "Turning off switches"
	switches.off()
}

////////////////////////////////////////////////////////////////////////////////
