package justfatlard.particle_emote;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Throwing the particles, over a second or so rather than all at once.
 *
 * <p>A single {@code sendParticles} is a puff that is over before anyone looks up. Spread across
 * twenty ticks it becomes something a person standing nearby actually sees happen, and because it
 * is re-aimed at the player every tick it follows them: emote and keep walking and the trail comes
 * with you, which is most of the charm.
 *
 * <p>One burst per player at a time. Emoting again replaces what you were doing rather than
 * stacking, so leaning on the key cannot turn one player into a particle fountain the server has to
 * send to everybody.
 */
public final class Bursts {
	private Bursts() {}

	/** Long enough to be seen and short enough not to be a costume. */
	private static final int TICKS = 20;

	/** Thrown per tick. Small, because this is sent to every player who can see you. */
	private static final int PER_TICK = 4;

	/** How far up the player it sits: chest height on a standing player. */
	private static final double HEIGHT = 1.2;

	private record Burst(Emotes.Emote emote, int ticksLeft) {}

	private static final Map<UUID, Burst> RUNNING = new ConcurrentHashMap<>();

	/** Start one, or replace the one already going. */
	public static void start(ServerPlayer player, Emotes.Emote emote) {
		RUNNING.put(player.getUUID(), new Burst(emote, TICKS));
	}

	public static void forget(UUID player) {
		RUNNING.remove(player);
	}

	/**
	 * Over the keys, and putting the count back rather than editing the entry in place.
	 *
	 * <p>This was a {@code removeIf} over the entry set that counted down with
	 * {@code entry.setValue}. A ConcurrentHashMap hands that predicate an immutable copy of the
	 * entry, so setValue threw and took the server down with it - on the first tick of the first
	 * emote anybody sent.
	 */
	public static void tick(MinecraftServer server) {
		if (RUNNING.isEmpty()) return;

		for (UUID id : List.copyOf(RUNNING.keySet())) {
			Burst burst = RUNNING.get(id);
			if (burst == null) continue;

			ServerPlayer player = server.getPlayerList().getPlayer(id);
			// Gone, or dead: nothing to throw particles off.
			if (player == null || !player.isAlive()) {
				RUNNING.remove(id);
				continue;
			}

			throwSome(player, burst.emote());
			int left = burst.ticksLeft() - 1;
			if (left <= 0) RUNNING.remove(id);
			else RUNNING.put(id, new Burst(burst.emote(), left));
		}
	}

	private static void throwSome(ServerPlayer player, Emotes.Emote emote) {
		if (!(player.level() instanceof ServerLevel level)) return;

		// The ones that climb on their own are given no push, or they leave the player behind in a
		// tick; the ones that do not are pushed gently outward so they read as a burst.
		double speed = emote.rising() ? 0.0 : 0.02;

		level.sendParticles(emote.particle(),
			player.getX(), player.getY() + HEIGHT, player.getZ(),
			PER_TICK, emote.spread(), emote.spread() * 0.6, emote.spread(), speed);
	}
}
