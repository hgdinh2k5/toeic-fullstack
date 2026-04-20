"""
Script chuyển đổi data.json:
- Part 1-2: Mỗi question có img/audio riêng → chuyển lên thành group-level (questionGroup)
- Part 3-7: Cấu trúc đã đúng (img/audio ở cấp group), giữ nguyên

Output: data_transformed.json
"""
import json
import copy

# Đọc file data.json
with open("data.json", "r", encoding="utf-8") as f:
    data = json.load(f)

transformed = []

for item in data:
    part = item.get("part")

    if part in (1, 2):
        # Part 1 & 2: mỗi item là 1 câu hỏi đơn lẻ
        # Chuyển img/audio từ question lên questionGroup
        new_item = copy.deepcopy(item)

        # Tạo cấu trúc group-level chứa img/audio
        new_item["group_img"] = new_item.pop("img", None)
        new_item["group_audio"] = new_item.pop("audio", None)

        transformed.append(new_item)

    elif part in (3, 4, 5, 6, 7):
        # Part 3-7: img/audio/content đã ở cấp group
        # Giữ nguyên, chỉ rename cho nhất quán
        new_item = copy.deepcopy(item)

        # audio ở cấp group → rename thành group_audio
        if "audio" in new_item:
            new_item["group_audio"] = new_item.pop("audio")
        # img ở cấp group → rename thành group_img
        if "img" in new_item:
            new_item["group_img"] = new_item.pop("img")

        transformed.append(new_item)
    else:
        # Trường hợp khác, giữ nguyên
        transformed.append(copy.deepcopy(item))

# Ghi file output
with open("data_transformed.json", "w", encoding="utf-8") as f:
    json.dump(transformed, f, ensure_ascii=False, indent=2)

# Thống kê
part_counts = {}
for item in transformed:
    p = item.get("part", "unknown")
    part_counts[p] = part_counts.get(p, 0) + 1

print("=== Transform completed ===")
print(f"Total items: {len(transformed)}")
print(f"Items per Part: {part_counts}")

still_has_img = sum(1 for item in transformed if "img" in item)
still_has_audio = sum(1 for item in transformed if "audio" in item)
has_group_img = sum(1 for item in transformed if "group_img" in item)
has_group_audio = sum(1 for item in transformed if "group_audio" in item)

print(f"\nAfter transform:")
print(f"  - Items still having 'img':       {still_has_img}")
print(f"  - Items still having 'audio':     {still_has_audio}")
print(f"  - Items with 'group_img':         {has_group_img}")
print(f"  - Items with 'group_audio':       {has_group_audio}")
