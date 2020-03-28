package everyos.discord.bot.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import everyos.discord.bot.localization.LocalizedString;

@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
	public LocalizedString help() default LocalizedString.Undocumented;
	public LocalizedString ehelp() default LocalizedString.Undocumented;
}
