package everyos.discord.bot.adapter;

import everyos.discord.bot.database.DBDocument;
import reactor.core.publisher.Mono;

public interface IAdapter {
    public Mono<DBDocument> getDocument();
}