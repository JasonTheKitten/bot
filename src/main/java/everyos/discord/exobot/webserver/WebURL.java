package everyos.discord.exobot.webserver;

import java.util.HashMap;

import everyos.discord.exobot.util.StringUtil;

public class WebURL {
	private ParseState parseState;
	
	public String path;
	public String fragment;
	public HashMap<String, String> query;
	public WebURL(String url) {
		StringBuilder bpath = new StringBuilder();
		StringBuilder bfragment = new StringBuilder();
		StringBuilder qf = null;
		StringBuilder qv = null;
		query = new HashMap<String, String>();
		parseState = ParseState.PATH;
		for (int i=0; i<=url.length(); i++) {
			char ch;
			ch=(i==url.length())?'\0':url.charAt(i);
			switch(parseState) {
				case PATH:
					if (ch=='\0') {
					} else if (ch=='#') {
						parseState = ParseState.FRAGMENT;
					} else if (ch == '?') {
						qf = new StringBuilder();
						parseState = ParseState.QUERY_FIELD;
					} else {
						bpath.append(ch);
					}
					break;
				case FRAGMENT:
					if (ch=='\0') {
						fragment = bfragment.toString();
					} else {
						bfragment.append(ch);
					}
					break;
				case QUERY_FIELD:
					if (ch=='\0') {
						query.put(qf.toString(), "");
					} else if (ch=='=') {
						qv = new StringBuilder();
						parseState = ParseState.QUERY_VALUE;
					} else {
						qf.append(ch);
					}
					break;
				case QUERY_VALUE:
					if (ch=='\0') {
						query.put(qf.toString(), qv.toString());
					} else if (ch=='&') {
						query.put(qf.toString(), qv.toString());
						qf = new StringBuilder();
						parseState = ParseState.QUERY_FIELD;
					} else {
						qv.append(ch);
					}
					break;
				default:
					break;
			}
		}
		
		path = bpath.toString();
		path = path.replaceAll("/+", "/");
		if (path.startsWith("/")) path = StringUtil.sub(path,  1);
		if (path.endsWith("/")) path = StringUtil.sub(path, 0, path.length()-1);
		if (path.equals("/")) path = "/";
	}
	
	enum ParseState {PATH, QUERY_FIELD, FRAGMENT, QUERY_VALUE}
}