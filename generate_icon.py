#!/usr/bin/env python3
"""The mod icon: a burst of particles leaving a point, drawn rather than borrowed."""

import math
import random
from pathlib import Path

from PIL import Image

HERE = Path(__file__).parent
OUT = HERE / "src/main/resources/assets/particle-emote/icon.png"
SIZE = 128
BACKGROUND = (24, 22, 34, 255)
# Four of the emote colours: a heart, a happy villager, a note and a flame.
COLOURS = [(214, 62, 92), (108, 196, 84), (94, 156, 226), (232, 158, 54)]


def main() -> None:
    icon = Image.new("RGBA", (SIZE, SIZE), BACKGROUND)
    pixels = icon.load()
    random.seed(7)

    centre = SIZE / 2
    for ring in range(4):
        # Further out means smaller and sparser, which is what a burst looks like.
        radius = 14 + ring * 13
        count = 6 + ring * 4
        dot = max(1, 5 - ring)
        for step in range(count):
            angle = (step / count) * math.tau + ring * 0.4 + random.uniform(-0.12, 0.12)
            distance = radius + random.uniform(-4, 4)
            x = round(centre + math.cos(angle) * distance)
            y = round(centre + math.sin(angle) * distance)
            colour = COLOURS[(step + ring) % len(COLOURS)]
            for dy in range(-dot, dot + 1):
                for dx in range(-dot, dot + 1):
                    px, py = x + dx, y + dy
                    if 0 <= px < SIZE and 0 <= py < SIZE:
                        pixels[px, py] = colour

    OUT.parent.mkdir(parents=True, exist_ok=True)
    icon.save(OUT)
    print(f"{OUT.relative_to(HERE)}  {SIZE}x{SIZE}")


if __name__ == "__main__":
    main()
