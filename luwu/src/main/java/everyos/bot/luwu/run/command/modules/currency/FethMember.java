package everyos.bot.luwu.run.command.modules.currency;

import java.util.Map;
import java.util.function.Function;

import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Member;
import reactor.core.publisher.Mono;

public class FethMember extends Member {
	protected FethMember(Connection connection, ChatMember member, Map<String, DBDocument> documents) {
		super(connection, member, documents);
	}
	
	public Mono<FethMemberInfo> getInfo() {
		return getLocalDocument()
			.map(document->{
				DBObject memberObject = document.getObject();
				DBObject currencyObject = memberObject.getOrCreateObject("feth", obj->{});
				return new FethMemberInfoImp(currencyObject);
			});
	}
	
	public Mono<Void> edit(Function<FethMemberEditSpec, Mono<Void>> func) {
		return getLocalDocument()
			.flatMap(document->{
				DBObject memberObject = document.getObject();
				DBObject currencyObject = memberObject.getOrCreateObject("feth", obj->{});
				Mono<Void> m1 = func.apply(new FethMemberEditSpecImp(currencyObject));
				
				return m1.then(document.save());
			});
	}
	
	private class FethMemberInfoImp implements FethMemberInfo {
		private DBObject object;

		public FethMemberInfoImp(DBObject object) {
			this.object = object;
		}

		@Override
		public long getCurrency() {
			return object.getOrDefaultLong("feth", 0);
		}

		@Override
		public long getTimeout() {
			return object.getOrDefaultLong("lmsg", -1000*60*60*24);
		}

		@Override
		public long getCooldown() {
			return object.getOrDefaultLong("cooldown", -1000*60*60*24);
		}
	}
	
	private class FethMemberEditSpecImp implements FethMemberEditSpec {
		private DBObject object;

		public FethMemberEditSpecImp(DBObject object) {
			this.object = object;
		}

		@Override
		public void setCurrency(long currency) {
			object.set("feth", currency);
		}

		@Override
		public void setTimeout(long timeout) {
			object.set("lmsg", timeout);
		}

		@Override
		public void setCooldown(long cooldown) {
			object.set("cooldown", cooldown);
		}
		
		@Override
		public FethMemberInfo getInfo() {
			return new FethMemberInfoImp(object);
		}
	}

	public static FethMemberFactory type = new FethMemberFactory();
}
