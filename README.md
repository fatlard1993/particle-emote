# Particle Emote

A menu of particles to throw off yourself. Press **V**, pick one, and everybody nearby sees it.

## What This Mod Does

Multiplayer has chat for what you mean and no way at all to say it without stopping to type. Say
"yes" across a build site, cheer somebody's redstone, or be visibly cross about a creeper, without
taking your hand off the mouse.

Server-side. There are no items, no blocks, no recipes and no art: the grid is a Pandorical screen
described to the client, and every particle is one the game already knows how to draw.

## The Twelve

| | | | |
|---|---|---|---|
| **Love** hearts | **Yes** green sparkle | **No** angry puff | **Music** notes |
| **Party** totem confetti | **Magic** enchanting glyphs | **Spark** electric | **Boom** explosion |
| **Fire** flames | **Smoke** smoke | **Snow** snowflakes | **Poof** cloud |

Chosen for what they read as rather than for what they are. Nobody thinks "angry villager
particle", they think the other player is cross with them - and everything in the list had to be
legible at a distance, over a shoulder, with no text beside it.

**Every button wears the particle it throws.** Not a drawn approximation of it: the game states
which textures a particle is made of, and the icons are taken straight from there at build time. So
the heart on the button is the heart you throw, and an emote whose particle Mojang restyles restyles
with it. The name is in the tooltip, for the ones where a grey cloud needs saying out loud.

## Using It

**Press V.** The grid opens, you press one, it closes, the burst happens. One keystroke in and one
click out, because an emote you have to think about is an emote nobody sends.

The key is rebindable in the normal controls screen, under Pandorical, where it shows up as "Emote
menu". It is bound to V on first join and your own change sticks after that.

**Or type it.** `/emote` opens the same grid. `/emote love` skips it and goes straight to the
burst, with the twelve names as tab completions - which is the form to put in a command block or a
macro.

## How A Burst Behaves

**It lasts a second**, thrown a little at a time over twenty ticks rather than all at once. A single
puff is over before anyone looks up; a second of it is something a person standing nearby actually
sees happen.

**It follows you.** The particles are aimed at wherever you are each tick, so emoting and then
walking off trails the burst behind you, which is most of the fun of it.

**One at a time.** Emoting again replaces the burst you were already running rather than stacking
on it, so leaning on the key cannot turn one player into a fountain the server has to send to
everybody in render distance.

## Pandorical

Pandorical is required on the server; the mod will not load without it, because the grid and the
keybind are both Pandorical's.

**A client without Pandorical sees other people's emotes perfectly** - they are ordinary particles -
but has no menu and no key of its own. `/emote love` still works from chat, so the mod is usable
from a vanilla client, just not comfortably.

## Adding One

Emotes live in one table in `Emotes.java`, one line each. The grid lays itself out from that table
and grows a row when it needs to, and the command's completions come from the same place, so an
emote is genuinely one line and not four.

Every particle in it is a `SimpleParticleType` - the kind that needs no extra data - and they are
named against the game's own constants rather than looked up by id, so a particle that leaves the
game breaks the build rather than the emote.

## Development

Installing and the icon are in [DEVELOPMENT.md](DEVELOPMENT.md).

## License

MIT, see [LICENSE](LICENSE).
