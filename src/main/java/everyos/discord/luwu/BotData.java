package everyos.discord.luwu;

import java.util.HashMap;
import java.util.Objects;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import reactor.core.publisher.Mono;

public class BotData {
	public HashMap<ReactionTrigger, Mono<?>> triggers = new HashMap<ReactionTrigger, Mono<?>>();
	
	public Mono<?> registerReaction(Message message, String reaction, Mono<?> trigger) {
		triggers.put(new ReactionTrigger(message.getChannelId().asLong(), message.getId().asLong(), reaction), trigger);
		return
			message.addReaction(ReactionEmoji.unicode(reaction))
			.onErrorResume(e->Mono.empty());
	}
	
	public static class ReactionTrigger {
		private long cid;
		private long mid;
		private String s;

		public ReactionTrigger(long cid, long mid, String reaction) {
			this.cid = cid;
			this.mid = mid;
			this.s = reaction;
		}
		
		@Override public boolean equals(Object o) {
			if (!(o instanceof ReactionTrigger)) return false;
			ReactionTrigger t = (ReactionTrigger) o;
			return cid==t.cid&&mid==t.mid&&s.equals(t.s);
		}
		
		@Override public int hashCode() {
			return Objects.hash(cid, mid, s.hashCode());
		}
	}
}
