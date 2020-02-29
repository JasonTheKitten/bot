package everyos.discord.bot.adapter;

import java.util.function.Consumer;

import discord4j.core.spec.MessageCreateSpec;
import everyos.discord.bot.Main;
import everyos.discord.bot.standards.ChatLinkDocumentCreateStandard;
import everyos.storage.database.DBArray;
import everyos.storage.database.DBDocument;

public class ChatLinkAdapter implements IAdapter {
	String chatlinkID;
	public ChatLinkAdapter(String chatlinkID) {
		this.chatlinkID = chatlinkID;
	}
	
	public void forward(String cid, String msg) {
		DBArray arr = getDocument().getObject().getOrCreateArray("links", ()->new DBArray());
		arr.forEach(i->{
			ChannelAdapter.of(arr.getString(i)).send(msg);
		});
	}
	public void forward(String cid, Consumer<MessageCreateSpec> mcs) {
		DBArray arr = getDocument().getObject().getOrCreateArray("links", ()->new DBArray());
		arr.forEach(i->{
			ChannelAdapter.of(arr.getString(i)).send(mcs);
		});
	}

	@Override public DBDocument getDocument() {
		return Main.db.collection("chatlinks").getOrSet(chatlinkID, ChatLinkDocumentCreateStandard.standard);
	}
	
	public static ChatLinkAdapter of(String linkid) {
		DBDocument linkdb = Main.db.collection("chatlinks").getOrSet(linkid, ChatLinkDocumentCreateStandard.standard);
		ChatLinkAdapter cladapter = (ChatLinkAdapter) linkdb.getMemoryOrSet("adapter", ()->{return new ChatLinkAdapter(linkdb.getName());});
		
		return cladapter;
	}
}
