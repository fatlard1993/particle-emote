#!/usr/bin/env python3
"""
The button icons: each emote's own particle, taken from the game.

Nothing here is drawn. The game states which textures a particle is made of, in
`assets/minecraft/particles/<id>.json`, so that file is the source: read it, take a frame, and the
button wears the thing it throws. An emote whose particle Mojang restyles restyles with it.

Three of them - smoke, snow and cloud - are the same grey `generic` blob in the files, because the
game tints it at draw time rather than shipping three textures. So those are tinted here too, to
what the game tints them to. Without it the last row would be three identical grey squares.
"""

import json
import zipfile
from pathlib import Path

from PIL import Image

HERE = Path(__file__).parent
CLIENT_JAR = Path.home() / ".gradle/caches/fabric-loom/26.3/minecraft-client-only.jar"
OUT = HERE / "src/main/resources/assets/particle-emote/textures/gui/sprites/emote"
SIZE = 16

# emote id -> (particle id, frame to take or None for the middle one, tint or None)
#
# A frame is named only where the middle one is a poor picture of the thing: an explosion is a
# ragged wisp at both ends of its sixteen frames and a round puff in the middle third, and the
# eight `generic` frames run from a few specks to a full cloud - so smoke takes the biggest, cloud
# a middling one and snow the sparsest, which separates three icons the files cannot.
ICONS = {
    "love":  ("heart", None, None),
    "yes":   ("happy_villager", None, None),
    "no":    ("angry_villager", None, None),
    "music": ("note", None, (150, 120, 230)),
    "party": ("totem_of_undying", "glitter_3", (255, 215, 90)),
    "magic": ("enchant", "sga_a", (190, 160, 255)),
    "spark": ("electric_spark", None, (120, 200, 255)),
    "boom":  ("explosion", "explosion_5", None),
    "fire":  ("flame", None, None),
    "smoke": ("large_smoke", "generic_7", (125, 125, 133)),
    "snow":  ("snowflake", "generic_2", (205, 235, 255)),
    "poof":  ("cloud", "generic_5", (235, 235, 240)),
}


def tinted(image: Image.Image, tint) -> Image.Image:
    if tint is None:
        return image
    out = image.copy()
    pixels = out.load()
    for y in range(out.height):
        for x in range(out.width):
            r, g, b, a = pixels[x, y]
            pixels[x, y] = (r * tint[0] // 255, g * tint[1] // 255, b * tint[2] // 255, a)
    return out


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(CLIENT_JAR) as jar:
        for emote, (particle, frame, tint) in ICONS.items():
            textures = json.loads(
                jar.read(f"assets/minecraft/particles/{particle}.json"))["textures"]
            wanted = f"minecraft:{frame}" if frame else textures[len(textures) // 2]
            if wanted not in textures:
                raise SystemExit(f"{emote}: {particle} has no frame {wanted}; it has {textures}")

            path = f"assets/minecraft/textures/particle/{wanted.split(':')[-1]}.png"
            with jar.open(path) as handle:
                art = Image.open(handle).convert("RGBA")
                art.load()

            # An animated sheet is frames stacked downwards; the first is the whole picture.
            if art.height > art.width:
                art = art.crop((0, 0, art.width, art.width))

            tinted(art, tint).resize((SIZE, SIZE), Image.NEAREST).save(OUT / f"{emote}.png")
            print(f"  {emote:6s} <- {wanted.split(':')[-1]}")

    print(f"{len(ICONS)} icons -> {OUT.relative_to(HERE)}")


if __name__ == "__main__":
    main()
