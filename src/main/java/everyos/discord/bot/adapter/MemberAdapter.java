package everyos.discord.bot.adapter;

import java.util.function.Consumer;

import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.Main;
import everyos.discord.bot.standards.MemberDocumentCreateStandard;
import everyos.discord.bot.util.ObjectStore;
import everyos.storage.database.DBCollection;
import everyos.storage.database.DBDocument;

public class MemberAdapter implements IAdapter {
    private IAdapter padapter;
    @SuppressWarnings("unused") private UserAdapter uadapter;
    private String uid;
    private Member member;
    
    public MemberAdapter(IAdapter padapter, UserAdapter uadapter) {
    	this(padapter, uadapter.uid);
    	this.uadapter = uadapter;
    }
   
    public MemberAdapter(IAdapter padapter, String uid) {
    	this.padapter = padapter;
        this.uid = uid;
	}
    
    public void require(Consumer<MemberAdapter> func) {
    	if (!(padapter instanceof GuildAdapter)) {
    		func.accept(this);
    		return;
    	}
    	Main.client.getMemberById(Snowflake.of(((GuildAdapter) padapter).guildID), Snowflake.of(uid)).subscribe(member->{
    		this.member = member;
    		func.accept(this);
    	});
    }
    
    public String getUserID() {
		return uid;
	}
    
    public void getDisplayName(Consumer<String> func) {
		require(madapter->{
			if (member == null) {
                getUserAdapter(uadapter->{
                    uadapter.getUsername(name->func.accept(name));
                });
			} else {
				func.accept(member.getDisplayName());
			}
		});
    }
    
    public void getUserAdapter(Consumer<UserAdapter> func) {
        func.accept(UserAdapter.of(uid));
    }
    
    public void hasPermission(Permission permission, Consumer<Boolean> func) {
    	if (padapter instanceof ChannelAdapter) {
    		func.accept(true); return;
    	}
    	require(madapter-> {
	    	member.getBasePermissions().subscribe(perms->{
	    		if (perms.contains(permission)||perms.contains(Permission.ADMINISTRATOR)) {
	    			func.accept(true);
	    		} else {
	    			//TODO: Internal permissions system HERE
	    			func.accept(false);
	    		}
	    	});
    	});
    }
    public void checkHigherThan(MemberAdapter member, Consumer<Boolean> func) {
    	require(madapter->{
    		member.require(memadapter->{
    			madapter.member.isHigher(memadapter.member).subscribe(func);
    		});
    	});
    }

    @Override public DBDocument getDocument() {
        return padapter.getDocument().subcollection("members").getOrSet(uid, MemberDocumentCreateStandard.standard);
    }

    public static MemberAdapter of(IAdapter padapter, UserAdapter uadapter) {
        ObjectStore rtn = new ObjectStore();
        DBCollection collection = padapter.getDocument().subcollection("members");
        collection.getIfPresent(uadapter.uid, channelo -> {
            rtn.object = channelo.getMemoryOrNull("adapter");
            return rtn.object != null;
        }).elsedo(() -> {
            rtn.object = collection.getOrSet(uadapter.uid, MemberDocumentCreateStandard.standard)
                .getMemoryOrSet("adapter", ()->{return new MemberAdapter(padapter, uadapter);});
        });

        return (MemberAdapter) rtn.object;
    }
	public static MemberAdapter of(IAdapter padapter, String memberID) {
		ObjectStore rtn = new ObjectStore();
        DBCollection collection = padapter.getDocument().subcollection("members");
        collection.getIfPresent(memberID, channelo -> {
            rtn.object = channelo.getMemoryOrNull("adapter");
            return rtn.object != null;
        }).elsedo(() -> {
            rtn.object = collection.getOrSet(memberID, MemberDocumentCreateStandard.standard)
                .getMemoryOrSet("adapter", ()->{return new MemberAdapter(padapter, memberID);});
        });

        return (MemberAdapter) rtn.object;
	}

	public void ban(Consumer<Boolean> func) {
		require(madapter->member.ban(spec->{}).subscribe(voi->func.accept(true), thro->func.accept(false)));
	}
	public void kick(Consumer<Boolean> func) {
		require(madapter->member.kick().subscribe(voi->func.accept(true), thro->func.accept(false)));
	}
}