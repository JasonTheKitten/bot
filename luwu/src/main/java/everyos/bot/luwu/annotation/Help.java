package everyos.bot.luwu.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Help {
	String help();
	String ehelp();
	String usage();
}
