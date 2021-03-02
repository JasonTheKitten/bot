package everyos.bot.luwu.run.command.modules.music;

import java.util.ArrayList;

public class MusicQueue {
	private ArrayList<MusicTrack> queue = new ArrayList<>();
	
	public void queue(MusicTrack track) {
		synchronized(queue) {
			queue.add(queue.size(), track);
		}
	}
	public void queue(int i, MusicTrack track) {
		synchronized(queue) {
			queue.add(i, track);
		}
	}
	
	public int size() {
		synchronized(queue) {
			return queue.size();
		}
	}
	
	public MusicTrack pop() {
		synchronized(queue) {
			MusicTrack first = queue.get(0);
			queue.remove(0);
			return first;
		}
	}
	
	public MusicTrack read(int i) {
		synchronized(queue) {
			return queue.get(i);
		}
	}
	
	public boolean isEmpty() {
		synchronized(queue) {
			return queue.isEmpty();
		}
	}
	public MusicTrack remove(int i) {
		return queue.remove(i);
	}
	public void put(int i, MusicTrack track) {
		queue.add(i, track);
	}
	public boolean has(int i) {
		return queue.get(i)!=null;
	}
}
