# Development

## Building

```
./gradlew build
```

or, from anywhere in the suite, `mc-build particle-emote`.

Compiles against Pandorical's working tree as a subproject. There are no
optional integrations: this mod talks to nothing but Pandorical.

## Installing

Server-side. Drop the jar in the server's `mods/` alongside Pandorical. Players
want Pandorical client-side for the menu and the keybind; without it they still
see everyone else's emotes and can still use `/emote <name>`.

## The art

```
python3 generate_icons.py   # the twelve button icons
python3 generate_icon.py    # the mod icon
```

`generate_icons.py` reads `assets/minecraft/particles/<id>.json` out of the Loom
cache to find which textures a particle is really made of, then lifts a frame
straight from the client jar. Nothing is drawn, and a button cannot drift away
from the particle it throws.

Three emotes - smoke, snow and cloud - share one grey `generic` texture in the
files, because the game tints it at draw time rather than shipping three. Those
are tinted here to match, and given different frames from the eight, or the last
row would be three identical grey squares.

The mod icon is drawn, and seeded so re-running gives the same scatter.

## What to check by hand

There are no tests, and the parts that only exist at runtime are:

- **The grid opens and the press lands.** `/emote` with no argument, then press
  one. The screen should close by itself.
- **The keybind claims a slot.** Join a Pandorical client and look for "Emote
  menu" in the controls screen under Pandorical.
- **A burst follows.** Emote and walk: the particles should come with you for
  about a second.
