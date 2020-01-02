package everyos.discord.exobot.webserver.pages;

import java.io.IOException;
import java.net.Socket;

import everyos.discord.exobot.webserver.WebRequest;

public class ErrorPage404 implements IServerPage {
	@Override public void processResponse(Socket sock, WebRequest sentRequest) {
		String response = "HTTP/1.1 404 Not Found\r\nContent-Length:3\r\n\r\n404";
		try {
			sock.getOutputStream().write(response.getBytes());
		} catch (IOException e) { e.printStackTrace(); }
	}
}
