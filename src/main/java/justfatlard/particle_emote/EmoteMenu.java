package justfatlard.particle_emote;

import java.util.Map;

import justfatlard.pandorical.api.ComponentType;
import justfatlard.pandorical.api.PandoricalApi;
import justfatlard.pandorical.api.ScreenApi;
import justfatlard.pandorical.api.ScreenBuilder;
import net.minecraft.server.level.ServerPlayer;

/**
 * The grid: twelve squares, one press, screen closes, particles happen.
 *
 * <p>Built from {@link Emotes} rather than laid out by hand, so adding an emote is one line there
 * and the grid grows to fit it. Four across, because four is as wide as a word like "Party" reads
 * at button size and still leaves the menu narrower than a chest.
 *
 * <p>Each button wears the particle it throws. The pictures cost nothing to keep: they are lifted
 * out of the game's own particle definitions at build time rather than drawn, so a button always
 * shows the thing it actually does, and the name sits in the tooltip for the ones that need it.
 */
public final class EmoteMenu {
	private EmoteMenu() {}

	public static final String TYPE = "particle-emote:menu";

	private static final int COLUMNS = 4;
	/**
	 * Square, because the button stretches its icon to fill itself: a wider button than it is tall
	 * would hand every emote a squashed particle.
	 */
	private static final int BUTTON = 22;
	private static final int GAP = 4;
	private static final int MARGIN = 8;
	private static final int TITLE_H = 14;

	private static final int WIDTH =
		MARGIN * 2 + COLUMNS * BUTTON + (COLUMNS - 1) * GAP;

	private static int rows() {
		return (Emotes.all().size() + COLUMNS - 1) / COLUMNS;
	}

	private static int height() {
		return MARGIN * 2 + TITLE_H + rows() * BUTTON + (rows() - 1) * GAP;
	}

	/**
	 * One handler for the whole grid rather than one per button.
	 *
	 * <p>The button's own id is the emote's id, so the fallback already knows which was pressed.
	 * Registering twelve handlers to do the same twelve things differs only in how much there is to
	 * keep in step.
	 */
	public static void register() {
		PandoricalApi.screens().onActionFallback(TYPE, (player, data) -> {
			String id = data.get(ScreenApi.FALLBACK_COMPONENT_ID_KEY);
			if (id == null) return;
			Emotes.Emote emote = Emotes.of(id);
			if (emote == null) return;

			Bursts.start(player, emote);
			// Closed on the way out: an emote is a gesture, and leaving the menu open over it
			// would mean pressing escape after every single one.
			PandoricalApi.screens().close(player, screenIdOf(player));
		});
	}

	/** Stable per player, so the close above names the screen this player has open. */
	private static String screenIdOf(ServerPlayer player) {
		return TYPE + ":" + player.getUUID();
	}

	public static void open(ServerPlayer player) {
		ScreenBuilder screen = new ScreenBuilder(TYPE)
			.id(screenIdOf(player))
			.size(WIDTH, height())
			.title("Emote");

		screen.panel("frame", 0, 0, WIDTH, height(), Map.of());

		int index = 0;
		for (Emotes.Emote emote : Emotes.all().values()) {
			int column = index % COLUMNS;
			int row = index / COLUMNS;
			screen.button(emote.id(),
				MARGIN + column * (BUTTON + GAP),
				MARGIN + TITLE_H + row * (BUTTON + GAP),
				BUTTON, BUTTON,
				Map.of(
					ComponentType.PROP_ICON, sprite(emote),
					// The word it lost when the icon took its place. A heart needs no caption;
					// a grey cloud very much does.
					ComponentType.PROP_TOOLTIP, emote.label()));
			index++;
		}

		PandoricalApi.screens().open(player, screen.build());
	}

	/** The particle's own picture, lifted from the game by generate_icons.py. */
	static String sprite(Emotes.Emote emote) {
		return Main.MOD_ID + ":emote/" + emote.id();
	}

	/** The same picture, as an action menu's button wears it. */
	static String icon(Emotes.Emote emote) {
		return "sprite:" + sprite(emote);
	}
}
