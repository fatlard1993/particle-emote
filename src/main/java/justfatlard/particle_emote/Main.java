package justfatlard.particle_emote;

import com.mojang.brigadier.arguments.StringArgumentType;

import java.util.ArrayList;
import java.util.List;

import justfatlard.pandorical.api.ActionMenuApi;
import justfatlard.pandorical.api.KeybindApi;
import justfatlard.pandorical.api.PandoricalApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A menu of particles to throw off yourself, and nothing else.
 *
 * <p>Multiplayer has chat for what you mean and no way at all to say it without stopping to type,
 * which is the whole gap this fills: a key, a grid, a burst everyone nearby can see, and you never
 * took your hand off the mouse.
 *
 * <p>Entirely server-side. There is no client code here, no items, no blocks and no art - the grid
 * is a Pandorical screen described to the client, and the particles are ones the game already
 * knows how to draw. See {@link Emotes} for the twelve, {@link EmoteMenu} for the grid and
 * {@link Bursts} for how long one lasts.
 */
public class Main implements ModInitializer {
	public static final String MOD_ID = "particle-emote";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** V for a gesture: near the movement keys and unused by the game. */
	private static final char DEFAULT_KEY = 'V';

	@Override
	public void onInitialize() {
		EmoteMenu.register();

		ServerTickEvents.END_SERVER_TICK.register(Bursts::tick);

		// A burst outlives the screen it was chosen from, so it has to be forgotten on the way out
		// rather than on close, or a player who logs out mid-emote stays in the map.
		ServerPlayConnectionEvents.DISCONNECT.register(
			(handler, server) -> Bursts.forget(handler.getPlayer().getUUID()));

		PandoricalApi.keybinds().register(MOD_ID + ":menu", KeybindApi.letter(DEFAULT_KEY),
			"Emote menu", EmoteMenu::open);
		PandoricalApi.keybinds().bindByDefault(MOD_ID + ":menu");
		seedActionMenu();
		PandoricalApi.commandHelp().describe("/emote", "Open the grid of emotes to throw one.");
		PandoricalApi.commandHelp().describe("/emote <emote>",
			"Throw one emote by name, without opening the grid.");

		CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) ->
			dispatcher.register(Commands.literal("emote")
				// Bare: the grid, which is the way anyone will actually use this.
				.executes(context -> {
					EmoteMenu.open(context.getSource().getPlayerOrException());
					return 1;
				})
				// Named: for a command block, a macro, or somebody who knows what they want.
				.then(Commands.argument("emote", StringArgumentType.word())
					.suggests((context, builder) -> {
						Emotes.all().keySet().forEach(builder::suggest);
						return builder.buildFuture();
					})
					.executes(context -> {
						ServerPlayer player = context.getSource().getPlayerOrException();
						String id = StringArgumentType.getString(context, "emote");
						Emotes.Emote emote = Emotes.of(id);
						if (emote == null) {
							context.getSource().sendFailure(
								Component.literal("No emote called " + id));
							return 0;
						}
						Bursts.start(player, emote);
						return 1;
					}))));

		LOGGER.info("[{}] {} emotes, on {} and /emote", MOD_ID, Emotes.all().size(), DEFAULT_KEY);
	}

	/**
	 * The twelve, as an action menu a player is offered once.
	 *
	 * <p>Pandorical's own grid is the one this mod opens on a key; an action menu is the player's,
	 * openable on a key of their choosing and editable down to the four they actually use. Both
	 * run the same command, so neither is the real one.
	 */
	private void seedActionMenu() {
		List<ActionMenuApi.Button> buttons = new ArrayList<>();
		for (Emotes.Emote emote : Emotes.all().values()) {
			buttons.add(ActionMenuApi.Button.runs(
				emote.item(), emote.label(), "emote " + emote.id()));
		}
		PandoricalApi.actionMenus().suggestMenu(MOD_ID + ":emotes", "Emotes", buttons);
	}
}
