package everyos.discord.bot.object;

public class Promise {
    private int resolvers = 0;
    private boolean ready = false;
    private Runnable runnable;
    public Promise(Runnable after) {
        runnable = after;
    }
    public void createResolver() {
        resolvers++;
    }
    public void resolve() {
    	resolvers--;
        if (resolvers<=0&&ready) runnable.run();
    }
    public void ready(){ready=true;}
}