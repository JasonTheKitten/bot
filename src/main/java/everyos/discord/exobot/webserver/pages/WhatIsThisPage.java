package everyos.discord.exobot.webserver.pages;

import java.io.IOException;
import java.net.Socket;

import everyos.discord.exobot.webserver.WebRequest;

public class WhatIsThisPage implements IServerPage {

	@Override
	public void processResponse(Socket sock, WebRequest sentRequest) {
		String content = 
				"<h1>What is this page?</h1>"+
				"<p>This is a static HTML page. It is part of ExoBot's built-in webserver</p><br>"+
				"<h3>Why does this webserver exist?</h3>"+
				"<p>This webserver allows for dynamic processing of requests. "+
				"This webserver can be used as a webhook target, which is useful for service integrations</p>";
		String response = "HTTP/1.1 200 Success\r\nContent-Length:"+content.length()+"\r\n\r\n"+content;
		try {
			sock.getOutputStream().write(response.getBytes());
		} catch (IOException e) { e.printStackTrace(); }
	}

}
