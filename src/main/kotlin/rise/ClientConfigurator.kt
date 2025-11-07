package rise

import jakarta.websocket.ClientEndpointConfig

class ClientConfigurator : ClientEndpointConfig.Configurator() {
    override fun beforeRequest(headers: MutableMap<String, List<String>>) {
        // this has been here since at most 6.1.30...
        // they only started validating it on the 1st of November LOL!
        headers["gdfg"] = listOf("fdsgh")
    }
}