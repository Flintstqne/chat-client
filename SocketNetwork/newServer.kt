import java.net.ServerSocket
import kotlin.concurrent.thread
import java.io.*
import java.net.Socket
import java.util.*
import java.nio.charset.Charset



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
			
		}
		
	}
	return hash
}


fun mapToString(map : HashMap<String, Any>) : String {
	var finishedString = ""
	for ((key, value) in map){
		var addedString = key + "=" + value
		finishedString += addedString + ","
	}	
	return finishedString
}
var activeClients = HashMap<String, Client>()
fun main(){
	
	println("Listening on port 8080...")
	var server = ServerSocket(8080)
	while (true){
		
		println("Waiting for a client to connect...")
		val socket = server.accept()
		var newClient = Client(socket)
		thread {newClient.run()}
		activeClients.put(socket.inetAddress.hostAddress.toString(), newClient)
		
	}
	// take input from a client and find the target IP
	// preferably the client would be open on 8080 as well
	// packets : {
}

var registeredUsers = HashMap<String, String>()
class Client(client : Socket){
	private val client : Socket = client
	private val reader : Scanner = Scanner(client.getInputStream())
	private val writer: OutputStream = client.getOutputStream()
	private var running : Boolean = true
	private var currentChannel = "ALL"
	
	fun run(){
		println("${client.inetAddress.hostAddress} connected to the server.")
		running = true
		write("Welcome to our server!")
		while (running){
			
			try{
				//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
				val info = reader.nextLine()
			
				var text = remapString(info)
				println(text)
				var message = text["Message"].toString()
				// i need to unmap this reader text - it's in a hashMap string format so obviously any server functions will not work at all.
				if (message == "EXIT"){
					shutdown()
					continue
				} else if (message == "cnls"){
					var outputresp = ""
					for ((name, ip) in registeredUsers){
						outputresp += (name + "-" + ip.toString())
					}
					println(outputresp)
					println("Output of registeredUsers")
					write(outputresp)
				}
				
				try{
					
					if (text.containsKey("Registered")) {
						println("Registering new user.")
						registeredUsers.put(text["Registered"].toString(), client.inetAddress.hostAddress.toString())
						println("Registered Users: ")
						println(registeredUsers)
					}
				} catch (e: Exception) {
					println("Probably not a map string...")
				}
				
				
				println(message)
				for ((name, client) in activeClients){
					client.write(message)
				}
				
				// need to find the target of this...
				// send it to everyone bro
	
				
			}catch (ex: Exception) {
				shutdown()
			}
		}
	}
	
	private fun shutdown(){
		running = false
		client.close()
		println("${client.inetAddress.hostAddress} closed the connection.")
		activeClients.remove(client.inetAddress.hostAddress.toString())
	}
	
	private fun write(message : String){
	
		writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
	}
	
}
