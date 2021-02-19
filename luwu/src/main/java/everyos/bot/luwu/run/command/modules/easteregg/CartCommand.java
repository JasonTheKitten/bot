package everyos.bot.luwu.run.command.modules.easteregg;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class CartCommand extends CommandBase {
	public CartCommand() {
		super("command.easteregg.cart", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	static final String URL =
		"https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/460a797d-065d-4173-a09c-98d067dc8d37/d9zdnx"+
		"z-2ab96de9-6c5e-4ad9-a43f-18579de5af74.png/v1/fill/w_1080,h_585,q_80,strp/nyan_cart_by_pian0k3ysonmic"+
		"eforce_d9zdnxz-fullview.jpg";
	
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return data.getChannel().getInterface(ChannelTextInterface.class)
			.send(spec->{
				spec.setContent("This command does not exist, but take a cart anyways! (Image from WixMP)"); //TODO: Localize
				spec.addAttachment("cart.gif", URL);
			})
			.then();
	}
}
