package everyos.bot.luwu.run;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import everyos.bot.luwu.core.Configuration;
import everyos.bot.luwu.util.FileUtil;
import reactor.core.publisher.Hooks;

public final class Main {
	private Main() {}
	
	public static void main(String[] args) {
		//Set logger verbosity
		Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		Logger mongoLogger = (Logger) LoggerFactory.getLogger("org.mongodb.driver.cluster");
		logger.setLevel(Level.INFO);
		mongoLogger.setLevel(Level.WARN);
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-v")||args[i].equals("--verbose")) {
				logger.setLevel(Level.ALL);
				mongoLogger.setLevel(Level.ALL);
				return;
			}
		}
		
		//Set Reactor logging
		Hooks.onOperatorDebug();
		
		//Load configurations and launch
		Configuration configuration;
		try {
			configuration = Configuration.loadFrom(FileUtil.getAppData("config.json"));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("An error occured while loading configurations");
			return;
		}
		new Luwu().execute(configuration, logger).block();
	}
}
