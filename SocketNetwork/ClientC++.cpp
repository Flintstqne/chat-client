#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <cstring>
#include <thread>

void receiveMessages(int clientSocket) {
    char buffer[1024];
    while (true) {
        int bytesReceived = recv(clientSocket, buffer, sizeof(buffer), 0);
        if (bytesReceived <= 0) {
            std::cerr << "Connection closed by server\n";
            break;
        }
        buffer[bytesReceived] = '\0';
        std::cout << "\nReceived: " << buffer << std::endl;
    }
}

int main() {
    //create socket
    int clientSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (clientSocket == -1) {
        std::cerr << "Error: Could not create socket\n";
        return 1;
    }


    //address and port
    struct sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(8080); // Change this to the port your server is listening on
    inet_pton(AF_INET, "192.168.2.6", &serverAddr.sin_addr);

    //connect to server
    if (connect(clientSocket, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) == -1) {
        std::cerr << "Error: Connection failed\n";
        close(clientSocket);
        return 1;
    }

    //thread to receive messages
    std::thread receiveThread(receiveMessages, clientSocket);

    std::string Username;
    std::cout << "Username:";
    std::getline(std::cin, Username);
    
    //send and receive
    std::string message;
    while (true) {
        std::getline(std::cin, message);
        if (message == "quit") {
            break;
        }        
        
        //format message
        std::string formattedMessage = "To=ALL,Message=" + message + ",From=TestUser";
        formattedMessage += '\n'; // Add a return symbol
        
        //send message
        send(clientSocket, formattedMessage.c_str(), formattedMessage.size(), 0);
    }

    //close socket
    close(clientSocket);

    //receive thread
    receiveThread.join();

    return 0;
}
