import java.net.ServerSocket
import kotlin.concurrent.thread
import java.io.*
import java.net.Socket

fun mapToString(map : HashMap<String, Any>) : String {
	var finishedString = ""
	for ((key, value) in map){
		var addedString = key + "=" + value
		finishedString += addedString + ","
	}	
	return finishedString
}
// Initialize Maps
var clientThreads = HashMap<String, Any>()
var activeUsers = HashMap<String, Any>()

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

fun handleServerRequests(){
	while (true){
		// setup serverside socket request
		val server = ServerSocket(8081)
		val client = server.accept()
		var clientInformation = BufferedReader(InputStreamReader(client.getInputStream())).readLine()
		var clientIP = client.getRemoteSocketAddress.toString()
		println(clientIP)
		var map = remapString(clientInformation)
		
		try{
			if (map.containsKey("Request")) {
				println(" Server request incoming")
				if (map.get("Request") == "ConnectedClients"){
					val returnSocket = Socket(clientIP, 8081)
					val output = PrintWriter(returnSocket.getOutputStream(), true)
					output.println(mapToString(activeUsers))
				}
			}
		} catch (e: Exception){
			println("Not a server request traffic")
		}
	}
}

fun main() {
	
    val server = ServerSocket(8080)
    
    println("Listening on port 8080...")
    println("Listening for server requests on port 8081")
    thread { handleServerRequests() }
    
    // loop to check for incoming user information
    while (true){
		val socket = server.accept()
		val clientInformation = socket.getRemoteSocketAddress().toString()
		// if this user isn't already logged, we need to register them with our server (for this connection)
		if (activeUsers.containsKey(clientInformation) == false){
			thread { handleUser(clientInformation) }
		} else {
			clientThreads.put(clientInformation, thread { handleUser(clientInformation) })
		}
	}
}

fun handleUser(socketInformation : String){
	var clientConnected = true
	print("Client $socketInformation connected.")
	val server = ServerSocket(8080)
	
	while (clientConnected == true) {
	
		val socket = server.accept()
		val clientInformation = socket.getRemoteSocketAddress().toString()	
			
		val text = BufferedReader(InputStreamReader(socket.getInputStream())).readLine()
			
		var map = remapString(text)
		println("Subject:" + map.get("Subject"))
		println("Content:" + map.get("Message"))
		println("From:")
		println(map.get("From"))
		println("To:")
		println(map.get("To"))
		try{
			val targetClient = Socket(map.get("To").toString(), 8080)
			val output = PrintWriter(targetClient.getOutputStream(), true)
			output.println(text)
		} catch (e: Exception){
			println("Probably not a valid IP")
			println(e)
		}
		
	}
}

fun registerUser(IP : String) : String{

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
