package everyos.nertivia.nertivia4j.event;

import everyos.nertivia.nertivia4j.NertiviaClient;

public class ReadyEvent extends Event {
	public ReadyEvent(NertiviaClient client) {
		super(client);
	}
}
