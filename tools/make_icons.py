"""Generate SmartFin launcher banner/icon/logo assets from the source artwork in Logo/.

Usage (from the repo root):  py -3 tools/make_icons.py
The source PNG (black background) is git-ignored; the generated PNGs in smarttubetv/src/*/res are committed.
Existing asset sizes are preserved (flavors use 640x360 banners, 320x320 icons, 180x180 logos).
"""
import glob
import os

from PIL import Image, ImageChops, ImageDraw

SRC = glob.glob('Logo/*.png')[0]
CUT = 540  # the "SmartFin" wordmark (and its glow) starts below this line in the source artwork


def to_alpha(img):
    """Black background -> transparency; the strongest colour channel drives alpha so saturated red stays opaque."""
    r, g, b, _ = img.split()
    strength = ImageChops.lighter(ImageChops.lighter(r, g), b)
    return Image.merge('RGBA', (r, g, b, strength.point(lambda v: min(255, int(v * 2.0)))))


def fit(img, size, margin):
    mw, mh = size[0] * (1 - 2 * margin), size[1] * (1 - 2 * margin)
    s = min(mw / img.width, mh / img.height)
    return img.resize((max(1, int(img.width * s)), max(1, int(img.height * s))), Image.LANCZOS)


def main():
    src = Image.open(SRC).convert('RGBA')
    w, _ = src.size
    gray = src.convert('L')
    gb = gray.crop((0, 0, w, CUT)).point(lambda v: 255 if v > 60 else 0).getbbox()
    glyph = to_alpha(src.crop((max(gb[0] - 12, 0), max(gb[1] - 12, 0), gb[2] + 12, CUT)))
    fb = gray.point(lambda v: 255 if v > 40 else 0).getbbox()
    full = src.crop((fb[0] - 30, fb[1] - 20, fb[2] + 30, fb[3] + 20))

    def banner(size):
        bg = Image.new('RGB', size, (0, 0, 0))
        f = fit(full, size, 0.04)
        bg.paste(f.convert('RGB'), ((size[0] - f.width) // 2, (size[1] - f.height) // 2))
        return bg

    def icon(size):
        im = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        ImageDraw.Draw(im).rounded_rectangle((0, 0, size - 1, size - 1), radius=int(size * 0.18), fill=(0, 0, 0, 255))
        g = fit(glyph, (size, size), 0.08)
        im.alpha_composite(g, ((size - g.width) // 2, (size - g.height) // 2))
        return im

    def logo(size, alpha=1.0, grey=False):
        r, g, b, a = glyph.split()
        if grey:
            r = g = b = Image.merge('RGB', (r, g, b)).convert('L')
        if alpha < 1.0:
            a = a.point(lambda v: int(v * alpha))
        img = Image.merge('RGBA', (r, g, b, a))
        im = Image.new('RGBA', size, (0, 0, 0, 0))
        f = fit(img, size, 0.04)
        im.alpha_composite(f, ((size[0] - f.width) // 2, (size[1] - f.height) // 2))
        return im

    for path in glob.glob('smarttubetv/src/*/res/mipmap*/*.png'):
        name = os.path.basename(path)
        old = Image.open(path)
        size = old.size
        if name == 'app_banner.png':
            new = banner(size)
        elif name in ('app_icon.png', 'app_icon_alt.png'):
            new = icon(size[0])
        elif name == 'app_logo.png':
            new = logo(size)
        elif name == 'app_logo_semi_red.png':
            new = logo(size, 0.75)
        elif name == 'app_logo_semi_grey.png':
            new = logo(size, 0.75, grey=True)
        else:
            continue  # app_logo_none stays transparent
        if old.mode == 'RGB':
            new = new.convert('RGB')
        new.save(path, optimize=True)
        print('wrote', path, size)


if __name__ == '__main__':
    main()
