package everyos.discord.bot;

import discord4j.core.object.Embed;
import discord4j.core.object.Embed.Image;
import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.api.Google;
import everyos.discord.bot.localization.LocalizedString;
import everyos.storage.database.DBObject;

public class AutoModeration {
    public ModerationData scan(Message message, MessageAdapter adapter, MemberAdapter madapter) {
        ModerationData data = null;
        for (Embed embed: message.getEmbeds()) {
            Image image = embed.getImage().get();
            if (image==null) continue;
            try {
                if(!Google.isImageSafe(image.getUrl())) {
                    data = new ModerationData(LocalizedString.RatedImage, madapter);
                }
            } catch (Exception e) {e.printStackTrace();}
        }
        
        //TODO: Ping chain counting
        
        return data;
    }
}

class ModerationData {
    public LocalizedString reason;
    public long kickback;
    public boolean acceptable;

    public ModerationData(LocalizedString reason, MemberAdapter madapter) {
        this.reason = reason;
        DBObject obj = madapter.getDocument().getObject();
        long lastWrite = obj.getOrDefaultLong("automodtime", -(1000*60*60));
        kickback = obj.getOrDefaultLong("automodkickback", 30)*2;
        if (System.currentTimeMillis()-lastWrite>=(1000*60*60)) kickback = 0;
        
    }
}