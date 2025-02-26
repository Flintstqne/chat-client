import java.net.Socket
import kotlin.concurrent.thread
import java.io.*
import java.util.Scanner
import java.net.ServerSocket
import java.lang.*
// NOTE: sudo apt install kotlinc
// ^ this is the installation cmd for the kotlin cmdline compiler
// When compiling a kotlin script, run the following (replacing outPutFile and yourScriptName with appropriate information): kotlinc nameOfYourScript.kt -include-runtime  -d outPutFile.jar
// NOTE: You must use the -include-runtime for the script to compile correctly and include all in-script functions. (kotlinc client.kt -include-runtime  -d client.jar)
// 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
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
fun mainSocket(){
	println("Connecting to server")
	var scanner = Scanner(System.`in`)
	while (true){
		val client = Socket("127.0.0.1", 8080)
		val output = PrintWriter(client.getOutputStream(), true)
		println("Your message:")
		val input = scanner.nextLine()
		
		output.println(input)
	}
}


// git: https://github.com/Flintstqne/chat-client.git

fun recieveServer(){
	// needs to manage recieving from the server
	val clientServerSocket = ServerSocket(8081)
}
// example hashmap hiearchy

// {"Subject" : "Testining","Content" : "Content", "From" : "UserName", "IP": "127.0.0.1"}
// same hiearchy transformed into a string
// Subject=Testining,Content=Content,From=UserName,IP=127.0.0.1

fun main(){
	// testing JSON objects
	var hashMap = HashMap<String, Any>()
	hashMap.put("Subject", "Hello world!")
	println(hashMap.get("Subject"))
	println("Changing map to string...")
	var finishedString = ""
	for ((key, value) in hashMap){
		var addedString = key + "=" + value
		finishedString += addedString + ","
	}	
	var stringedMap = mapToString(hashMap)
	println(stringedMap)
	println("Converting stringed map back to map...")
	var newMap = remapString(stringedMap)
	println(newMap)
	println("Assign your username:")
	var scanner = Scanner(System.`in`)
	var name = scanner.nextLine()
	println("Connecting to server")
	while (true){
		val client = Socket("127.0.0.1", 8080)
		// serverClient and serverServer are both meant for sending information to the server and recieving back
		val serverclient = Socket("127.0.0.1", 8081)
		val serverServer = ServerSocket(8081)
		val serverOutput = BufferedReader(InputStreamReader(serverServer.getInputStream())).readLine()
		val output = PrintWriter(client.getOutputStream(), true)
		println("Your message (chkO to check online users):")
		val input = scanner.nextLine()
		if (input.toString() == "chkO"){
			val server = PrintWriter(server.getOutputStream(), true)
			server.println("Request=ConnectedClients")
		}
		// changelog as of 2:30 on 4/5
		// added a possible request to get the active connected users (will ping whenever someone asks for this so it's up to date)
		// also not sure if this thing works
		if (serverOutput != "null"){
			try{
				// should theoretically remap the users and their IPs that are currently active to the server
				var newMap = remapString(serverOutput)
				println(newMap)
			} catch (e: Exception) {
				println("Ignoring this 8081 traffic")
			}
		// this map is what should be sent whenever the client sends a message - includes a subject, message content, and from who it is. will contain a 'TO' field so we know how to send information to each client.
		var subjectMap = HashMap<String, Any>()
		subjectMap.put("Subject", "Subjecty!")
		subjectMap.put("From", name)
		subjectMap.put("Message", input)
		output.println(mapToString(subjectMap))	
	}
}
