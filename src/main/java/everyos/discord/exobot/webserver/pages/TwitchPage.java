package everyos.discord.exobot.webserver.pages;

import java.io.IOException;
import java.net.Socket;

import everyos.discord.exobot.webserver.WebRequest;

public class TwitchPage implements IServerPage {
	@Override public void processResponse(Socket sock, WebRequest sentRequest) {
		String challenge = sentRequest.url.query.getOrDefault("hub.challenge", "");
		String response = 
				"HTTP/1.1 200 Success\r\n"+
				"Content-Length: "+challenge.length()+"\r\n"+
				"Content-Type: text/plain\r\n"+
				"\r\n"+challenge;
		try {
			sock.getOutputStream().write(response.getBytes());
			sentRequest.url.query.forEach((k, v)->{
				System.out.println(k+", "+v);
			});
		} catch (IOException e) { e.printStackTrace(); }
	}
}
