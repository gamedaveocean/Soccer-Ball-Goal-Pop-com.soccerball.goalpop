from PIL import Image
from pathlib import Path

project = Path(__file__).resolve().parents[1]
src = project / "_design_extract" / "F2B4~1.PNG"
res = project / "app" / "src" / "main" / "res"
assets = project / "assets" / "icons"
assets.mkdir(parents=True, exist_ok=True)

img = Image.open(src).convert("RGBA")
w, h = img.size
if w != h:
    side = min(w, h)
    left = (w - side) // 2
    top = (h - side) // 2
    img = img.crop((left, top, left + side, top + side))

img.save(assets / "app_icon_design_512.png")

launcher_sizes = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}
foreground_sizes = {
    "mipmap-mdpi": 108,
    "mipmap-hdpi": 162,
    "mipmap-xhdpi": 216,
    "mipmap-xxhdpi": 324,
    "mipmap-xxxhdpi": 432,
}

for folder, size in launcher_sizes.items():
    out_dir = res / folder
    out_dir.mkdir(parents=True, exist_ok=True)
    resized = img.resize((size, size), Image.Resampling.LANCZOS)
    resized.save(out_dir / "ic_launcher.png")
    resized.save(out_dir / "ic_launcher_round.png")

for folder, size in foreground_sizes.items():
    out_dir = res / folder
    out_dir.mkdir(parents=True, exist_ok=True)
    resized = img.resize((size, size), Image.Resampling.LANCZOS)
    resized.save(out_dir / "ic_launcher_foreground.png")

img.resize((1024, 1024), Image.Resampling.LANCZOS).save(assets / "app_icon_1024.png")
print("Launcher icons generated")
