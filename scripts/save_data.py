"""
Quick helper: paste your JSON data and save as data.json
Usage: python save_data.py
Then paste your JSON and press Ctrl+Z (Windows) or Ctrl+D (Mac/Linux) then Enter.
"""
import sys
import json
import os

print("Paste your JSON data below, then press Ctrl+Z (Windows) + Enter to finish:")
print("-" * 50)

raw = sys.stdin.read()
data = json.loads(raw)
out_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "data.json")
with open(out_path, "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print(f"\n✅ Saved {len(data)} items to {out_path}")
