package everyos.discord.exobot.webserver;

import java.util.HashMap;

public class WebRequest {
	public String method;
	public WebURL url;
	public String version;
	public HashMap<String, String> fields;

	public WebRequest(String method, WebURL url, String version, HashMap<String, String> fields) {
		this.method = method;
		this.url = url;
		this.version = version;
		this.fields = fields;
	}
}
