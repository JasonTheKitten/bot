package everyos.bot.luwu;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import everyos.bot.luwu.util.FileUtil;
import reactor.core.publisher.Hooks;

public final class Main {
	private Main() {}
	
	public static void main(String[] args) {
		//Set logger verbosity
		Logger logger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.setLevel(Level.INFO);
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-v")||args[i].equals("--verbose")) {
				logger.setLevel(Level.ALL);
				return;
			}
		}
		
		//Set reactor logging
		Hooks.onOperatorDebug();
		
		//Load configurations and launch
		BotInstance bot;
		try {
			bot = new BotInstance(Configuration.loadFrom(FileUtil.getAppData("config.json")));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("An error occured while loading configurations");
			return;
		}
		bot.execute().block();
	}
}
