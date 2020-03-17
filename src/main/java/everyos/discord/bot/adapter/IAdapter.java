package everyos.discord.bot.adapter;

import everyos.storage.database.DBDocument;

public interface IAdapter {
    public DBDocument getDocument();
}