package everyos.nertivia.nertivia4j.event;

import everyos.nertivia.nertivia4j.NertiviaClient;

public class MessageEvent extends Event {
	public MessageEvent(NertiviaClient client) {
		super(client);
	}
}
