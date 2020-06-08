package everyos.discord.luwu.adapter;

import everyos.discord.luwu.database.DBDocument;
import reactor.core.publisher.Mono;

public interface IAdapter {
    public Mono<DBDocument> getDocument();
}