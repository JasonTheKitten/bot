package everyos.bot.luwu.core.entity.event;

import everyos.bot.luwu.core.entity.Locale;
import reactor.core.publisher.Mono;

public interface LocaleHolder {
	Mono<Locale> getLocale();
}
