package everyos.bot.luwu.run;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import everyos.bot.luwu.core.Configuration;
import everyos.bot.luwu.util.FileUtil;
import reactor.core.publisher.Hooks;

public final class Main {
	private Main() {}
	
	public static void main(String[] args) {
		//Set logger verbosity
		System.setProperty("logging.level", "INFO");
		System.setProperty("logging.level.org.mongodb.driver.cluster", "WARN");
		System.setProperty("logging.thread_name_max_length", "20");
		System.setProperty("logging.color.thread.discord4j", "BLUE");
		System.setProperty("logging.color.thread.everyos", "YELLOW");
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-v")||args[i].equals("--verbose")) {
				System.setProperty("logging.level", "DEBUG");
				break;
			}
		}
		Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		
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
