package everyos.storage.database;

import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;

public class OtherCase {
	public boolean complete = false;
    
    public OtherCase() {}
    public OtherCase(boolean b) { complete = b; }

    public void elsedo(@Nonnull Runnable p) {
		if (!complete) p.run();
	}
	public OtherCase elsedo(@Nonnull BooleanSupplier p) {
		OtherCase elsedo = new OtherCase();
		elsedo.complete = p.getAsBoolean();
		return elsedo;
	}
}