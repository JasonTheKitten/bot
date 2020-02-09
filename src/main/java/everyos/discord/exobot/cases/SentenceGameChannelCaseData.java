package everyos.discord.exobot.cases;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.SaveUtil.JSONObject;
import everyos.discord.exobot.util.StringUtil;

public class SentenceGameChannelCaseData implements IChannelCaseData {  
    public static JsonObject words;

    static {
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream("whitelist.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            words = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void execute(Message message) {
        GuildObject guild = GuildHelper.getGuildData(message.getGuild());
        ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());

        if (message.getAuthor().get().isBot()) return;
                
        String content = message.getContent().orElse("");
        SentenceGameChannelCaseData data = (SentenceGameChannelCaseData) channel.data;
        long uid = message.getAuthor().get().getId().asLong();

        if (data.lastUser == uid || content=="") {
            message.delete().subscribe(); return;
        }

        if (!(words.has(content.toLowerCase())||content.matches("[-+]?[0-9]*\\.?[0-9]+$"))) {
            message.delete().subscribe();
            channel.send("I don't quite recognize this word (yet)!", true);
            return;
        }

        data.sentence += ((words.get(content.toLowerCase()).getAsInt()==2)?"":" ")+content;
        data.lastUser = uid;

        StaticFunctions.save();

        message.getChannel().subscribe(e -> {
            e.createMessage("The new sentence is:").subscribe();
            for (int i = 0; i < data.sentence.length(); i += 2000)
                e.createMessage(StringUtil.sub(data.sentence, i, i + 1999)).subscribe();
        });
    }

    public String sentence;
    public long lastUser;
	
	public SentenceGameChannelCaseData() {
        sentence = "";
        lastUser = -1;
    }

	public SentenceGameChannelCaseData(JsonObject save) {
        this.sentence = save.get("sentence").getAsString();
        this.lastUser = save.get("lastuser").getAsLong();
	}

    @Override public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        save.put("sentence", this.sentence);
        save.put("lastuser", this.lastUser);
        return save;
    }
}
