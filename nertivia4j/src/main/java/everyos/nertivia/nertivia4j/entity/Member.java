package everyos.nertivia.nertivia4j.entity;

import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaInstance;

public class Member extends User {
	public Member(NertiviaClient client, NertiviaInstance instance, long userID, long serverID) {
		super(client, instance, userID);
	}
}
