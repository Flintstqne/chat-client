import java.net.ServerSocket
import kotlin.concurrent.thread
import java.io.*


fun mapToString(map : HashMap<String, Any>) : String {
	var finishedString = ""
	for ((key, value) in map){
		var addedString = key + "=" + value
		finishedString += addedString + ","
	}	
	return finishedString
}

fun remapString(mapString : String) : HashMap<String, Any>{
	var hash = HashMap<String, Any>()
	var keyValuePairs = mapString.split(",")
	for (keyValue in keyValuePairs){
		try{
			var split = keyValue.split("=")
			var key = split[0]
			var value =  split[1]
			hash.put(key, value)
		} catch (e: IndexOutOfBoundsException){
			println("Skipping")
		}
		
	}
	return hash
}



fun main() {
	
    val server = ServerSocket(8080)
    
    println("Listening on port 8080...")
    while (true){
		val socket = server.accept()
		val clientInformation = socket.getRemoteSocketAddress().toString()
		thread { registerUser(server, clientInformation) }
		print("Client $clientInformation ")
		val text = BufferedReader(InputStreamReader(socket.getInputStream())).readLine()
		if (text != "null"){
			var map = remapString(text)
			println("Subject:" + map.get("Subject"))
			println("Content:" + map.get("Message"))
			println("From:")
			println(map.get("From"))
		} else {
			println("Client Disconnected...")
		}
	}
}

// user handling function

fun userFunc(socketInformation : String){
	 
}


fun registerUser(server : ServerSocket, IP : String) : String{

	var registeredUsersFile = File("./serverData/registeredUsers.txt")
	var isRegistered = "null"
	registeredUsersFile.forEachLine{s -> 
		var keyValue = s.split("=")
		var name = keyValue[0]
		var ip = keyValue[1]
		if (IP.toString() == ip.toString()){
			println("This user has been registered before.")
		
			isRegistered = name
		}
	}
	return isRegistered
}
