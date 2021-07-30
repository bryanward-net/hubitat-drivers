/**
 *  Alexa VirtualButtons Driver
 *  Forked From: https://raw.githubusercontent.com/code-in-progress/hubitat-drivers/master/alexa-notifications/alexa-notifications.groovy
 *  Copyright 2019 CoreRootedXB
 
 *  https://github.com/bryanward-net/hubitat-drivers/blob/master/alexa-virtualbuttons/alexa-virtualbuttons.groovy
 *  Copyright 2021 Bryan Ward
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
 *  Change History:
 *
 *    Date        Who             	What
 *    ----        ---             	----
 *    2019-03-25  CoreRootedXB   	Original Creation
 *    2021-07-29  Bryan Ward            Modified for VirtualButtons
 * 
 */

metadata {
    definition (name: "Alexa VirtualButtons Driver", namespace: "corerootedxb", author: "CoreRootedXB") {
        capability "Notification"
    }
}

preferences {
    input name: "accessCode", type: "string", title: "Access Code", description: "Visit VirtualButtons before using this", required: true
	input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
}


def logsOff(){
    log.warn "Debug logging disabled."
    device.updateSetting("logEnable",[value:"false",type:"bool"])
}

def installed() {
    updated()
}

def updated() {
	if (logEnable) {
		runIn(1800,logsOff)
		log.debug "Updated access code: $accessCode"
	}
}

def parse(String description) {
}

def deviceNotification(String msg) {
	if (logEnable) log.debug "sending message: $msg"
	
	def params = [
    	uri: "https://api.virtualbuttons.com/v1",
    	body: [
        	virtualButton: Integer.valueOf(msg),
			accessCode: "$accessCode"
    	]
	]

	try {
		httpPostJson(params) { resp ->
			resp.headers.each {
				if (logEnable) log.debug "${it.name} : ${it.value}"
			}
			if (logEnable) log.debug "response contentType: ${resp.contentType}"
		}
	} 
	catch (e) {
		log.error "something went wrong: $e"
	}
}
