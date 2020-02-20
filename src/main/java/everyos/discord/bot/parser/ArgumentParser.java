package everyos.discord.bot.parser;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import everyos.discord.bot.util.StringUtil;
import everyos.storage.database.OtherCase;

public class ArgumentParser {
	public static String getIfPrefix(@Nonnull String entry, @Nonnull String[] prefixes) {
		for (int i=0; i<prefixes.length; i++) {
			String prefix = prefixes[i];
			if (entry.startsWith(prefix)) return StringUtil.sub(entry, prefix.length());
		}
		return null;
	}
	public static OtherCase ifPrefix(@Nonnull String entry, @Nonnull String[] prefixes, @Nonnull Consumer<String> func) {
		OtherCase elsedo = new OtherCase();
		String arg = getIfPrefix(entry, prefixes);
		elsedo.complete = arg!=null;
		if (elsedo.complete) func.accept(arg);
		return elsedo;
	}
	public static OtherCase ifPrefix(@Nonnull String entry, @Nonnull String prefixes[], @Nonnull Function<String, Boolean> func) {
		OtherCase elsedo = new OtherCase();
		String arg = getIfPrefix(entry, prefixes);
		elsedo.complete = arg!=null;
		if (elsedo.complete) elsedo.complete = func.apply(arg);
		return elsedo;
	}

    public static String getCommand(String content) {
        return StringUtil.split1(content, " ");
    }
    public static String getArgument(String content) {
        return StringUtil.split2(content, " ");
    }
}
