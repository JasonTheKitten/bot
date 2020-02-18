package everyos.storage.database.functional;

import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;

public class OtherCase {
	public boolean complete = false;
	public void elsedo(@Nonnull Procedure p) {
		if (!complete) p.execute();
	}
	public OtherCase elsedo(@Nonnull BooleanSupplier p) {
		OtherCase elsedo = new OtherCase();
		elsedo.complete = p.getAsBoolean();
		return elsedo;
	}
}