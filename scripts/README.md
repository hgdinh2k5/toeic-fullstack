# TOEIC Data Seeder

## How to use

1. **Save your JSON data** as `data.json` in this `scripts/` folder
   - The JSON should be the array of TOEIC questions you have

2. **Install dependencies** (one-time):
   ```bash
   pip install cloudinary mysql-connector-python requests
   ```

3. **Run the script**:
   ```bash
   python seed_toeic_data.py
   ```

## What it does

1. **Reads** `data.json` (your TOEIC question data)
2. **Downloads** all images (`.webp`) and audio (`.mp3`) from source URLs
3. **Uploads** them to **Cloudinary** under `toeic/ets2026/testX/partY/`
4. **Inserts** all data into your **MySQL** database matching the JPA entities:
   - `BookCollection` → `Test` → `Part` → `QuestionGroup` → `Question` (MultipleChoice + FillBlank)

## Database mapping

| JSON field | DB Table | Column |
|---|---|---|
| `topic` | `tests` | `name` = "Test {topic}" |
| `part` | `parts` | `partNumber` |
| `script` | `question_groups` | `transcript` |
| `voca` | `question_groups` | `passageText` / `translation` |
| `audio` | `question_groups` | `audioUrl` (Cloudinary URL) |
| `img` | `question_groups` | `imageUrl` (Cloudinary URL) |
| `key` | `questions` | `correctOption` |
| `dictation` / `dictation_key` | `questions` (FILL_BLANK) | `content` / `correctText` |
| `group[].a/b/c/d` | `multiple_choice_question_options` | `option_text` |
