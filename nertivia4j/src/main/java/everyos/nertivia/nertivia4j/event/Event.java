package everyos.nertivia.nertivia4j.event;

import everyos.nertivia.nertivia4j.NertiviaClient;

public class Event {
	private NertiviaClient client;

	public Event(NertiviaClient client) {
		this.client = client;
	}
	
	public NertiviaClient getClient() {
		return this.client;
	}
}
