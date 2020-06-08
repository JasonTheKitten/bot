package everyos.discord.luwu.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.localization.LocalizedString;

@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
	public LocalizedString help() default LocalizedString.Undocumented;
	public LocalizedString ehelp() default LocalizedString.Undocumented;
	public CategoryEnum category() default CategoryEnum.NULL;
}
