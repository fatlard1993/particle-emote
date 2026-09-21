package justfatlard.particle_emote.gametest;

import justfatlard.pandorical.gametest.Smoke;
import justfatlard.particle_emote.Emotes;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

/**
 * An emote is thrown and runs to the end of itself.
 *
 * <p>The burst is the only part of this mod with any state: it counts itself down over a second,
 * on the server, once per tick. Firing one and then waiting is the whole test, because an uncaught
 * exception on any of those ticks ends the run - which is exactly how this mod took the server down
 * the first time anybody emoted on it, and exactly what nothing was watching for.
 */
public final class EmotesThrow implements FabricClientGameTest {

	@Override
	public void runTest(ClientGameTestContext context) {
		Smoke.run(context, "particle-emote", session -> {
			// One emote, and long enough for every tick of it: the crash was on the first.
			session.command("emote love");
			session.waitTicks(30);

			// The grid, which is the way anybody actually sends one. Opening it is a Pandorical
			// screen arriving from the server, so this proves that path too.
			session.command("emote");
			session.waitTicks(5);
			context.getInput().pressKey(com.mojang.blaze3d.platform.InputConstants.KEY_ESCAPE);
			session.waitTicks(5);

			if (!session.full()) return;

			// All twelve, since each carries its own particle and spread and any of them could be
			// the one that is wrong.
			for (String id : Emotes.all().keySet()) {
				session.command("emote " + id);
				session.waitTicks(4);
			}
			session.waitTicks(30);
			context.takeScreenshot("emotes");
		});
	}
}
