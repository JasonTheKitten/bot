package everyos.nertivia.nertivia4j;

import everyos.nertivia.nertivia4j.NertiviaClient.NertiviaClientOptions;
import reactor.util.Loggers;

public class NertiviaClientBuilder {
	private NertiviaClientOptions options;

	public static NertiviaClientBuilder create(String token) {
		Loggers.getLogger(NertiviaClientBuilder.class)
			.info("Nertivia4J - Unversioned");
		
		NertiviaInstance instance = new NertiviaInstance();
		instance.setToken(token);
		NertiviaClientOptions options = new NertiviaClientOptions();
		options.setInstance(instance);
		NertiviaClientBuilder builder = new NertiviaClientBuilder();
		builder.options = options;
		
		return builder;
	}
	
	public NertiviaClient build() {
		return new NertiviaClient(options);
	}
}
