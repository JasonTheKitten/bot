package everyos.discord.bot;

import discord4j.core.DiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import everyos.storage.database.Database;

public class ShardInstance {
    public DiscordClient client;
    public Database db;
    public String clientID;
    public long uptime = -1;
    public BotInstance instance;

    public ShardInstance(BotInstance instance, DiscordClient client) {
        this.client = client;
        this.db = instance.db;
        this.client = client;
        this.clientID = instance.clientID;
        this.instance = instance;
        
        new Thread(()->{
        	int oldRecount = 0;
        	while (!instance.shutdown.get()) {
        		try {
        			Thread.sleep(5000); //TODO: Use System.currentTimeMillis instead
        		} catch (Exception e) {e.printStackTrace();}
        		if (oldRecount!=instance.serverCount.get()) {
        			oldRecount = instance.serverCount.get();
        			onRecount(oldRecount);
        		}
        	}
        }).start();
    }
    
    public void onWakeup() {
    	client.updatePresence(Presence.online(Activity.playing("sleepyhead"))).subscribe();
    }
    private void onRecount(int c) {
    	client.updatePresence(Presence.online(Activity.watching(c+" server"+(c!=1?"s":"")+" | --- help | ---!"))).subscribe();
    }
}
