package everyos.bot.luwu.core.exception;

public class TextException extends Exception {
	private static final long serialVersionUID = -4427777353638349243L;
	private String text;
	
	public TextException(String text) {
		this.text = text;
	}
	
	@Override public String getMessage() {
		return text;
	}
}
