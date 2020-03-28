package everyos.discord.bot.localization;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

//What would be cool is per-server localization overrides
public class LocalizationProvider {
    private ILocalizationLookup lookup;
    public Localization locale;

    public LocalizationProvider(Localization locale) {
        this.locale = locale;
        switch (locale) {
            case en_US:
                lookup = new LocalizationLookupEnUS(); break;
        }
    }

    public String localize(LocalizedString label) {
        if (lookup == null) return "???";
        return lookup.lookup(label);
    }

    public String localize(LocalizedString label, HashMap<String, String> fillins) {
        AtomicReference<String> text = new AtomicReference<String>(localize(label));
        if (fillins != null)
            fillins.forEach((k, v) -> {
                text.set(text.get().replace("${" + k + "}", v));
            });
        return text.get().replace("${#", "${");
    }
}