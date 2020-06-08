package everyos.discord.luwu.filter;

public class FilterProvider {
	public static Filter of(String name) {
		switch(name) {
			case "everyone":
				return EveryoneFilter.filter;
			case "strict":
				return StrictFilter.filter;
			case "none":
				return NoFilter.filter;
			default:
				return null;
		}
	}
}
