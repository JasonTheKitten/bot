package everyos.discord.bot.parser;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import everyos.discord.bot.util.StringUtil;
import everyos.storage.database.functional.OtherCase;

public class ArgumentParser {
	public static String getIfPrefix(@Nonnull String entry, @Nonnull String prefix) {
		if (entry.startsWith(prefix)) return StringUtil.sub(entry, prefix.length());
		return null;
	}
	public static OtherCase ifPrefix(@Nonnull String entry, @Nonnull String prefix, @Nonnull Consumer<String> func) {
		OtherCase elsedo = new OtherCase();
		elsedo.complete = entry.startsWith(prefix);
		if (elsedo.complete) func.accept(StringUtil.sub(entry, prefix.length()));
		return elsedo;
	}
	public static OtherCase ifPrefix(@Nonnull String entry, @Nonnull String prefix, @Nonnull Function<String, Boolean> func) {
		OtherCase elsedo = new OtherCase();
		if(entry.startsWith(prefix))
			elsedo.complete = func.apply(StringUtil.sub(entry, prefix.length()));
		return elsedo;
	}
}
