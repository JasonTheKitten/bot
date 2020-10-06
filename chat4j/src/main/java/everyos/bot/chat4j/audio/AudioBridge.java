package everyos.bot.chat4j.audio;

import java.nio.ByteBuffer;

public abstract class AudioBridge {
	private ByteBuffer buffer;
	
	public AudioBridge(ByteBuffer buffer) {
		this.buffer = buffer;
	}
	public ByteBuffer getBuffer() {
		return this.buffer;
	}
	
	public abstract boolean provide();
}
