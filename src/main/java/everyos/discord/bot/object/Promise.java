package everyos.discord.bot.object;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

public class Promise { // Dunno if I'll actually use this class
    private int resolvers = 0;
    private boolean ready = false;
    private Runnable runnable;
    public Promise(Runnable after) {
        runnable = after;
    }
    public Runnable createResolver() {
        resolvers++;
        return ()->{
            resolvers--;
            if (resolvers==0&&ready) runnable.run();
        };
    }
    public Consumer<Object> createResolver(@Nonnull Consumer<Object> done) { //I doubt this works, due to weird casting
        resolvers++;
        return val->{
            resolvers--;
            done.accept(val);
            if (resolvers==0&&ready) runnable.run();
        };
    }
    public void ready(){ready=true;}
}