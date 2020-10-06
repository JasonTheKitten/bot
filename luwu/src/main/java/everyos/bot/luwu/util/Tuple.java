package everyos.bot.luwu.util;

public class Tuple<T1, T2> {
	private T1 t1;
	private T2 t2;

	public Tuple(T1 t1, T2 t2) {
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public T1 getT1() { return t1; }
	public T2 getT2() { return t2; }
}
