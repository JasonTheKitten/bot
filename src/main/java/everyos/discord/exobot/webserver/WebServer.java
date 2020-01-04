package everyos.discord.exobot.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import everyos.discord.exobot.Statics;
import everyos.discord.exobot.webserver.pages.ErrorPage404;
import everyos.discord.exobot.webserver.pages.IServerPage;
import everyos.discord.exobot.webserver.pages.TwitchPage;
import everyos.discord.exobot.webserver.pages.WhatIsThisPage;

public class WebServer implements Runnable {
	private static WebServer webserver;
	private ServerSocket websocket;
	private Boolean running = false;
	private HashMap<String, IServerPage> pages;
	
	public static WebServer getDefaultServer() throws IOException {
		if (webserver == null) webserver = createWebServer(80);
		return webserver;
	}
	
	public static WebServer createWebServer(int port) throws IOException {
		WebServer server = new WebServer(port);
		Thread tr = new Thread(server);
		tr.start();
		Statics.servers.add(server);
		return server;
	}
	
	public WebServer(int port) throws IOException {
		pages = new HashMap<String, IServerPage>();
		pages.put("WhatIsThisPage", new WhatIsThisPage());
		pages.put("twitch", new TwitchPage());
		try {
			websocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override public void run() {
		running = true;
		while (running) {
			try {
				Socket sock = websocket.accept();
				
				StringBuilder methodBuilder = new StringBuilder();
				StringBuilder URLBuilder = new StringBuilder();
				StringBuilder versionBuilder = new StringBuilder();
				StringBuilder fieldBuilder = null;
				StringBuilder valueBuilder = null;
				HashMap<String, String> fields = new HashMap<String, String>();
				
				ParseState state = ParseState.Method;
				InputStream stream = sock.getInputStream();
				Boolean parsing = true;
				while (parsing) {
					int rch = stream.read();
					if (rch == -1) break;
					char ch = (char) rch;
					System.out.print(ch);
					switch(state) {
						case Method:
							if (ch==' ') {
								state = ParseState.URL;
							} else methodBuilder.append(ch);
							break;
						case URL:
							if (ch==' ') {
								state = ParseState.Version;
							} else URLBuilder.append(ch);
							break;
						case Version:
							if (ch=='\n') {
								state = ParseState.First_NL;
							} else versionBuilder.append(ch);
							break;
						case First_NL:
							state = ParseState.Field;
							break;
						case Field:
							if (fieldBuilder==null) fieldBuilder = new StringBuilder();
							if (ch==':') {
								state = ParseState.Field_Space;
							} else if (ch=='\n') {
								parsing = false;
							} else {
								fieldBuilder.append(ch);
							}
							break;
						case Field_Space:
							state = ParseState.Field_Value;
							break;
						case Field_Value:
							if (valueBuilder==null) valueBuilder = new StringBuilder();
							if (ch=='\n') {
								fields.put(fieldBuilder.toString(), valueBuilder.toString());
								state = ParseState.Return;
							} else {
								valueBuilder.append(ch);
							}
							break;
						case Return:
							state = ParseState.Field;
							break;
						default:
							System.out.println(ch);
							parsing = false;
							break;
					}
				}
				
				String urls = URLBuilder.toString();
				
				WebURL url = new WebURL(urls);
				
				System.out.println(url.path);
				
				WebRequest sentRequest = new WebRequest(methodBuilder.toString(), url, versionBuilder.toString(), fields);
				
				pages.getOrDefault(url.path, new ErrorPage404()).processResponse(sock, sentRequest);
				
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		running = false;
		try {
			websocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private enum ParseState {Method, URL, End, Version, Field, Field_Space, Field_Value, Return, First_NL}
}
