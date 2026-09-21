package justfatlard.particle_emote;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import org.jspecify.annotations.Nullable;

/**
 * The twelve things you can say without typing.
 *
 * <p>Chosen for what they read as rather than for what they are: nobody thinks "angry villager
 * particle", they think the other player is cross with them. Every one had to be legible at a
 * distance, across a shoulder, with no text - which rules out most of the game's particles, and is
 * why there are twelve rather than the hundred the registry could offer.
 *
 * <p>Every one is a {@link SimpleParticleType}, which is the kind that needs no extra data to be
 * sent. Naming them here rather than looking them up by id means a particle that leaves the game
 * breaks the build instead of the emote.
 */
public final class Emotes {
	private Emotes() {}

	/**
	 * @param id       what the button and the command call it
	 * @param label    the word on the button, short enough for a small square
	 * @param particle what is actually thrown
	 * @param spread   how wide the burst sits around the player, in blocks
	 * @param rising   whether it climbs on its own; the ones that do are given less push
	 * @param item     what an action-menu button wears for it: those take an item, not a particle
	 */
	public record Emote(String id, String label, SimpleParticleType particle, double spread,
			boolean rising, String item) {}

	private static final Map<String, Emote> BY_ID = new LinkedHashMap<>();

	private static void add(String id, String label, SimpleParticleType particle, double spread,
			boolean rising, String item) {
		BY_ID.put(id, new Emote(id, label, particle, spread, rising, "minecraft:" + item));
	}

	static {
		// Row one: the four you will actually use, in the corner your hand lands on first.
		add("love", "Love", ParticleTypes.HEART, 0.4, true, "poppy");
		add("yes", "Yes", ParticleTypes.HAPPY_VILLAGER, 0.5, true, "emerald");
		add("no", "No", ParticleTypes.ANGRY_VILLAGER, 0.3, true, "redstone");
		add("music", "Music", ParticleTypes.NOTE, 0.5, true, "note_block");

		// Row two: bigger feelings.
		add("party", "Party", ParticleTypes.TOTEM_OF_UNDYING, 0.5, false, "totem_of_undying");
		add("magic", "Magic", ParticleTypes.ENCHANT, 0.8, false, "enchanting_table");
		add("spark", "Spark", ParticleTypes.ELECTRIC_SPARK, 0.4, false, "lightning_rod");
		add("boom", "Boom", ParticleTypes.EXPLOSION, 0.3, false, "tnt");

		// Row three: weather and smoke, for standing about looking like something happened.
		add("fire", "Fire", ParticleTypes.FLAME, 0.4, true, "blaze_powder");
		add("smoke", "Smoke", ParticleTypes.LARGE_SMOKE, 0.3, true, "campfire");
		add("snow", "Snow", ParticleTypes.SNOWFLAKE, 0.6, false, "snowball");
		add("poof", "Poof", ParticleTypes.CLOUD, 0.4, false, "white_wool");
	}

	public static @Nullable Emote of(String id) {
		return BY_ID.get(id);
	}

	/** Every emote, in the order the grid lays them out. */
	public static Map<String, Emote> all() {
		return BY_ID;
	}
}
