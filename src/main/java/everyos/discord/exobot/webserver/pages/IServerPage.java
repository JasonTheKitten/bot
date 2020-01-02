package everyos.discord.exobot.webserver.pages;

import java.net.Socket;

import everyos.discord.exobot.webserver.WebRequest;

public interface IServerPage {
	public void processResponse(Socket sock, WebRequest sentRequest);
}
