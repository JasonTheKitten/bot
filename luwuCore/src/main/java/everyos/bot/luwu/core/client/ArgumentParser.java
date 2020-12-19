package everyos.bot.luwu.core.client;

public abstract class ArgumentParser {
	private final String original;
	private String argument;
	
	public ArgumentParser(String argument) {
		this.argument = argument.trim();
		this.original = argument;
	}
	
	public boolean isEmpty() {
		return argument.isEmpty();
	};
	
	public String peek(int len) {
		if (len>argument.length()) return argument;
		return argument.substring(0, len);
	};
	public String peek() {
		return peek(1);
	};
	public String eat(int len) {
		if (len==0) return "";
		String rtn = peek(len);
		argument = argument.substring(len).trim();
		return rtn;
	}
	public String eat() {
		int ind = argument.indexOf(' ');
		return eat(ind==-1?argument.length():ind);
	};
	public boolean eatString(String match) {
		if (argument.startsWith(match)) {
			argument = argument.substring(match.length()).trim();
			return true;
		}
		return false;
	};
	
	public String getOriginal() {
		return original;
	}
	public String getRemaining() {
		return argument;
	}
	
	public boolean couldBeQuote() {
		String token = argument;
    	boolean foundStart = false;
    	boolean escapeNext = false;
    	if (!token.startsWith("\"")) return false;
    	for (char b: token.toCharArray()) {
    		if (escapeNext) {
    			escapeNext = false;
    			continue;
    		}
    		escapeNext = false;
    		if (b=='\\') escapeNext = true;
    		if (b=='"' && foundStart) return true;
    		if (b=='"' && !foundStart) foundStart = true;
    	}
    	return false;
	};
	public String eatQuote() {
		StringBuilder quote = new StringBuilder();
    	boolean foundStart = false;
    	boolean escapeNext = false;
    	while (true) {
    		char b = argument.substring(0, 1).charAt(0);
    		argument = argument.length()>1?argument.substring(1):"";
    		if (escapeNext) {
    			escapeNext = false;
    			continue;
    		}
    		escapeNext = false;
    		if (b=='\\') {
    			escapeNext = true;
    			continue;
    		}
    		if (b=='"' && foundStart) break;
    		if (foundStart) quote.append(b);
    		if (b=='"' && !foundStart) foundStart = true;
    	}
    	argument = argument.trim();
    	return quote.toString();
	}
	
	public boolean isNumerical() {
        try {
            Long.valueOf(next());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public long eatNumerical() {
        return Long.valueOf(next());
    }
	
	public abstract boolean couldBeUserID();
	public abstract long eatUserID();
	
	public abstract boolean couldBeChannelID();
	public abstract long eatChannelID();
	
	public abstract boolean couldBeGuildID();
	public abstract long eatGuildID();
	
	public abstract boolean couldBeRoleID();
	public abstract long eatRoleID();
	
	public abstract boolean couldBeEmojiID();
	public abstract String eatEmojiID();
	
	protected String next() {
		int ind = argument.indexOf(' ');
		return peek(ind==-1?argument.length():ind);
	}
}
