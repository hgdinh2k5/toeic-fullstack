# -*- coding: utf-8 -*-
"""
TOEIC Data Seeder Script
========================
This script:
1. Reads TOEIC question data from a JSON file
2. Downloads images and audio files from the source URLs
3. Uploads them to Cloudinary
4. Inserts all data into the MySQL database matching the JPA entity schema

Prerequisites:
    pip install cloudinary mysql-connector-python requests

Usage:
    1. Place your JSON data file as `data.json` in the same directory as this script
    2. Run: python seed_toeic_data.py
"""

import json
import os
import re
import sys
import uuid
import tempfile
import requests
import cloudinary
import cloudinary.uploader
import mysql.connector
from mysql.connector import Error

# Fix Windows console encoding
if sys.platform == "win32":
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
        sys.stderr.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass

# ============================================================
# CONFIGURATION
# ============================================================

# Cloudinary config
CLOUDINARY_CONFIG = {
    "cloud_name": "dybz8iuyt",
    "api_key": "934197112982238",
    "api_secret": "qu3OvcrmNO-hd1-7b_0r6bP1Gx8"
}

# MySQL config (Aiven Cloud)
DB_CONFIG = {
    "host": "mysql-9c700f1-toeic-fullstack.e.aivencloud.com",
    "port": 12818,
    "database": "toeic_fullstack",
    "user": "avnadmin",
    "password": "***REMOVED***",
    "ssl_disabled": False,
}

# Book collection info
BOOK_TITLE = "ETS 2026"
BOOK_YEAR = 2026

# Test name pattern
TEST_NAME_PATTERN = "Test {topic}"

# ============================================================
# INIT CLOUDINARY
# ============================================================

cloudinary.config(
    cloud_name=CLOUDINARY_CONFIG["cloud_name"],
    api_key=CLOUDINARY_CONFIG["api_key"],
    api_secret=CLOUDINARY_CONFIG["api_secret"],
    secure=True
)

# ============================================================
# HELPERS
# ============================================================

def gen_uuid():
    """Generate a UUID string matching JPA's GenerationType.UUID"""
    return str(uuid.uuid4())


def download_file(url, suffix=".tmp"):
    """Download a file from URL to a temporary file, return the path"""
    try:
        print(f"    [DOWN] Downloading: {url}")
        resp = requests.get(url, timeout=60, stream=True)
        resp.raise_for_status()
        tmp = tempfile.NamedTemporaryFile(delete=False, suffix=suffix)
        for chunk in resp.iter_content(chunk_size=8192):
            tmp.write(chunk)
        tmp.close()
        return tmp.name
    except Exception as e:
        print(f"    [FAIL] Download failed: {url} -> {e}")
        return None


def upload_to_cloudinary(file_path, folder, resource_type="auto"):
    """Upload a file to Cloudinary and return the secure URL"""
    try:
        print(f"    [CLOUD] Uploading to Cloudinary: {folder}")
        result = cloudinary.uploader.upload(
            file_path,
            folder=folder,
            resource_type=resource_type,
            overwrite=True,
            unique_filename=True,
        )
        url = result.get("secure_url")
        print(f"    [OK] Uploaded: {url}")
        return url
    except Exception as e:
        print(f"    [FAIL] Upload failed: {e}")
        return None
    finally:
        # Clean up temp file
        if file_path and os.path.exists(file_path):
            os.unlink(file_path)


def download_and_upload(source_url, cloudinary_folder, resource_type="auto"):
    """Download from source URL and upload to Cloudinary, return Cloudinary URL"""
    if not source_url:
        return None

    # Determine file extension from URL
    ext = os.path.splitext(source_url.split("?")[0])[1] or ".tmp"
    tmp_path = download_file(source_url, suffix=ext)
    if not tmp_path:
        return source_url  # Fallback to original URL

    cloudinary_url = upload_to_cloudinary(tmp_path, cloudinary_folder, resource_type)
    return cloudinary_url or source_url


# ============================================================
# DATABASE OPERATIONS
# ============================================================

def get_connection():
    """Create and return a MySQL connection"""
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        if conn.is_connected():
            print("[OK] Connected to MySQL database")
        return conn
    except Error as e:
        print(f"[FAIL] MySQL connection error: {e}")
        sys.exit(1)


def ensure_book_collection(cursor, title, year):
    """Find or create a BookCollection, return its ID"""
    cursor.execute("SELECT id FROM book_collections WHERE title = %s", (title,))
    row = cursor.fetchone()
    if row:
        print(f"[BOOK] Found existing BookCollection: {title} (id={row[0]})")
        return row[0]

    bc_id = gen_uuid()
    cursor.execute(
        "INSERT INTO book_collections (id, title, yearPublished) VALUES (%s, %s, %s)",
        (bc_id, title, year)
    )
    print(f"[BOOK] Created BookCollection: {title} (id={bc_id})")
    return bc_id


def ensure_test(cursor, book_collection_id, topic_number):
    """Find or create a Test, return its ID"""
    test_name = TEST_NAME_PATTERN.format(topic=topic_number)
    cursor.execute("SELECT id FROM tests WHERE name = %s AND book_collection_id = %s",
                   (test_name, book_collection_id))
    row = cursor.fetchone()
    if row:
        print(f"  [TEST] Found existing Test: {test_name} (id={row[0]})")
        return row[0]

    test_id = gen_uuid()
    cursor.execute(
        "INSERT INTO tests (id, name, isFreeTrial, book_collection_id) VALUES (%s, %s, %s, %s)",
        (test_id, test_name, False, book_collection_id)
    )
    print(f"  [TEST] Created Test: {test_name} (id={test_id})")
    return test_id


def ensure_part(cursor, test_id, part_number):
    """Find or create a Part, return its ID"""
    cursor.execute("SELECT id FROM parts WHERE partNumber = %s AND test_id = %s",
                   (part_number, test_id))
    row = cursor.fetchone()
    if row:
        return row[0]

    part_id = gen_uuid()
    cursor.execute(
        "INSERT INTO parts (id, partNumber, instructionText, test_id) VALUES (%s, %s, %s, %s)",
        (part_id, part_number, None, test_id)
    )
    print(f"    [PART] Created Part {part_number} (id={part_id})")
    return part_id


def insert_question_group(cursor, test_id, part_id, group_order,
                           audio_url, image_url, script_html, voca_html):
    """Insert a QuestionGroup, return its ID"""
    qg_id = gen_uuid()
    cursor.execute("""
        INSERT INTO question_groups
            (id, passageText, audioUrl, imageUrl, transcript, translation, groupOrder, test_id, part_id)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
    """, (
        qg_id,
        voca_html,       # passageText = vocabulary/translation
        audio_url,       # audioUrl
        image_url,       # imageUrl
        script_html,     # transcript = script
        voca_html,       # translation = voca (includes Vietnamese translation)
        group_order,     # groupOrder
        test_id,         # test_id
        part_id,         # part_id
    ))
    return qg_id


def insert_multiple_choice_question(cursor, qg_id, question_number, content,
                                      options, correct_option, audio_url, image_url,
                                      skill_type="LISTENING"):
    """Insert a MultipleChoiceQuestion"""
    q_id = gen_uuid()
    cursor.execute("""
        INSERT INTO questions
            (id, content, score, explanationText, skillType, question_number,
             question_group_id, question_type, correctOption, audioUrl, imageUrl)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """, (
        q_id,
        content,          # content
        5,                # score (default 5 per question)
        None,             # explanationText
        skill_type,       # skillType
        question_number,  # question_number
        qg_id,            # question_group_id
        "MULTIPLE_CHOICE",# question_type discriminator
        correct_option,   # correctOption
        audio_url,        # audioUrl
        image_url,        # imageUrl
    ))

    # Insert options into the ElementCollection table
    for idx, option_text in enumerate(options):
        cursor.execute("""
            INSERT INTO multiple_choice_question_options
                (question_id, option_order, option_text)
            VALUES (%s, %s, %s)
        """, (q_id, idx, option_text))

    return q_id


def insert_fill_blank_question(cursor, qg_id, question_number, content,
                                correct_text, audio_url, hint,
                                skill_type="LISTENING"):
    """Insert a FillBlankQuestion (for dictation exercises)"""
    q_id = gen_uuid()
    cursor.execute("""
        INSERT INTO questions
            (id, content, score, explanationText, skillType, question_number,
             question_group_id, question_type, correctText, audioUrl, hint,
             correctOption, imageUrl)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """, (
        q_id,
        content,          # content (the sentence with blanks)
        5,                # score
        None,             # explanationText
        skill_type,       # skillType
        question_number,  # question_number
        qg_id,            # question_group_id
        "FILL_BLANK",     # question_type discriminator
        correct_text,     # correctText
        audio_url,        # audioUrl
        hint,             # hint
        None,             # correctOption (not used for FILL_BLANK)
        None,             # imageUrl (not used for FILL_BLANK)
    ))
    return q_id


# ============================================================
# MAIN PROCESSING LOGIC
# ============================================================

def determine_skill_type(part_number):
    """Parts 1-4 are LISTENING, Parts 5-7 are READING"""
    if part_number <= 4:
        return "LISTENING"
    return "READING"


def extract_options_from_script(script_html):
    """Extract answer options (A), (B), (C), (D) from script HTML"""
    options = []
    pattern = r'\(([A-D])\)\s*(.*?)(?:<br\s*/?>|</p>|$)'
    matches = re.findall(pattern, script_html, re.DOTALL | re.IGNORECASE)
    for label, text in matches:
        clean = re.sub(r'<[^>]+>', '', text).strip()
        if clean:
            options.append(clean)
    return options


def process_part1_part2(item, cursor, test_id, part_id, cloudinary_base_folder):
    """Process Part 1 (photos) and Part 2 (Q&R) individual questions"""
    part_number = item["part"]
    question_no = item["no"]
    skill_type = determine_skill_type(part_number)

    print(f"\n  [Q] Processing Part {part_number}, Question {question_no}")

    # --- Upload media to Cloudinary ---
    audio_url = None
    if item.get("audio"):
        audio_url = download_and_upload(
            item["audio"],
            f"{cloudinary_base_folder}/part{part_number}/audio",
            "video"  # Cloudinary uses "video" resource_type for audio
        )

    image_url = None
    if item.get("img"):
        image_url = download_and_upload(
            item["img"],
            f"{cloudinary_base_folder}/part{part_number}/images",
            "image"
        )

    script_html = item.get("script", "")
    voca_html = item.get("voca", "")

    # Group order = question number within the part
    group_order = question_no

    # --- Create QuestionGroup ---
    qg_id = insert_question_group(
        cursor, test_id, part_id, group_order,
        audio_url, image_url, script_html, voca_html
    )

    # --- Create Multiple Choice Question ---
    options = extract_options_from_script(script_html)
    if not options:
        options = ["(A)", "(B)", "(C)", "(D)"]

    # Determine the actual TOEIC question number
    # Part 1: questions 1-6, Part 2: questions 7-31
    if part_number == 1:
        actual_question_number = question_no
    else:  # part 2
        actual_question_number = question_no + 6  # Part 2 starts at Q7

    insert_multiple_choice_question(
        cursor, qg_id,
        question_number=actual_question_number,
        content=f"Question {actual_question_number}",
        options=options,
        correct_option=item.get("key", "A"),
        audio_url=audio_url,
        image_url=image_url,
        skill_type=skill_type
    )

    # --- Create Fill-in-the-blank (Dictation) Questions ---
    dictation_list = item.get("dictation", [])
    dictation_keys = item.get("dictation_key", [])

    for idx, (sentence, answer) in enumerate(zip(dictation_list, dictation_keys)):
        insert_fill_blank_question(
            cursor, qg_id,
            question_number=actual_question_number * 100 + idx + 1,  # unique number
            content=sentence,
            correct_text=answer,
            audio_url=audio_url,
            hint=sentence.replace("------", "____"),
            skill_type=skill_type
        )

    print(f"  [OK] Question {actual_question_number} inserted")


def process_part3_part4(item, cursor, test_id, part_id, cloudinary_base_folder):
    """Process Part 3 (conversations) and Part 4 (talks) with grouped questions"""
    part_number = item["part"]
    group_no = item["no"]
    skill_type = determine_skill_type(part_number)

    print(f"\n  [G] Processing Part {part_number}, Group {group_no}")

    # --- Upload media to Cloudinary ---
    audio_url = None
    if item.get("audio"):
        audio_url = download_and_upload(
            item["audio"],
            f"{cloudinary_base_folder}/part{part_number}/audio",
            "video"
        )

    image_url = None
    if item.get("img"):
        image_url = download_and_upload(
            item["img"],
            f"{cloudinary_base_folder}/part{part_number}/images",
            "image"
        )

    script_html = item.get("script", "")
    voca_html = item.get("voca", "")

    # --- Create QuestionGroup ---
    qg_id = insert_question_group(
        cursor, test_id, part_id, group_no,
        audio_url, image_url, script_html, voca_html
    )

    # --- Create Multiple Choice Questions from the "group" array ---
    group_questions = item.get("group", [])
    for gq in group_questions:
        q_no = gq.get("no", 0)
        q_content = gq.get("content", f"Question {q_no}")
        options = [gq.get("a", ""), gq.get("b", ""), gq.get("c", ""), gq.get("d", "")]
        correct = gq.get("key", "A")

        insert_multiple_choice_question(
            cursor, qg_id,
            question_number=q_no,
            content=q_content,
            options=options,
            correct_option=correct,
            audio_url=None,   # Audio is at group level, not question level
            image_url=None,
            skill_type=skill_type
        )

    # --- Create Fill-in-the-blank (Dictation) Questions ---
    dictation_list = item.get("dictation", [])
    dictation_keys = item.get("dictation_key", [])

    first_q_no = group_questions[0]["no"] if group_questions else group_no * 10
    key_idx = 0

    if isinstance(dictation_list, str):
        # Part 4 sometimes has dictation as a single string, split by blanks
        dictation_list = [dictation_list]

    for idx, sentence in enumerate(dictation_list):
        # Count blanks in this sentence
        blank_count = sentence.count("------")
        # Collect the answers for this sentence
        answers_for_sentence = []
        for _ in range(blank_count):
            if key_idx < len(dictation_keys):
                answers_for_sentence.append(dictation_keys[key_idx])
                key_idx += 1
            else:
                answers_for_sentence.append("")

        correct_text = " | ".join(answers_for_sentence)

        insert_fill_blank_question(
            cursor, qg_id,
            question_number=first_q_no * 100 + idx + 1,
            content=sentence,
            correct_text=correct_text,
            audio_url=audio_url,
            hint=sentence.replace("------", "____"),
            skill_type=skill_type
        )

    q_numbers = [q['no'] for q in group_questions] if group_questions else [group_no]
    print(f"  [OK] Group {group_no} (Questions {q_numbers}) inserted")


def main():
    # --- Load JSON data ---
    script_dir = os.path.dirname(os.path.abspath(__file__))
    json_path = os.path.join(script_dir, "data.json")

    if not os.path.exists(json_path):
        print(f"[FAIL] JSON file not found: {json_path}")
        print("   Please place your TOEIC data as 'data.json' in the scripts/ folder.")
        sys.exit(1)

    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    if not data or len(data) == 0:
        print("[FAIL] data.json is empty! Please paste your JSON data into scripts/data.json first.")
        sys.exit(1)

    print(f"[INFO] Loaded {len(data)} items from data.json")

    # --- Connect to DB ---
    conn = get_connection()
    cursor = conn.cursor()

    # Disable FK checks temporarily for bulk insert
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0")
    cursor.execute("SET SESSION sql_require_primary_key = OFF")

    # Fix SINGLE_TABLE inheritance: subclass columns must allow NULL
    alter_statements = [
        "ALTER TABLE questions MODIFY COLUMN correctOption VARCHAR(255) NULL",
        "ALTER TABLE questions MODIFY COLUMN correctText TEXT NULL",
        "ALTER TABLE questions MODIFY COLUMN hint TEXT NULL",
        # Fix URL columns: VARCHAR(255) is too short for Cloudinary URLs
        "ALTER TABLE questions MODIFY COLUMN audioUrl TEXT NULL",
        "ALTER TABLE questions MODIFY COLUMN imageUrl TEXT NULL",
        "ALTER TABLE question_groups MODIFY COLUMN audioUrl TEXT NULL",
        "ALTER TABLE question_groups MODIFY COLUMN imageUrl TEXT NULL",
    ]
    for stmt in alter_statements:
        try:
            cursor.execute(stmt)
        except Exception:
            pass
    print("[FIX] Applied column type fixes (NULL + TEXT for URLs)")

    # Clean up data from previous failed run
    print("[CLEANUP] Removing partial data from previous run...")
    cursor.execute("DELETE FROM multiple_choice_question_options WHERE question_id IN (SELECT id FROM questions WHERE question_group_id IN (SELECT id FROM question_groups WHERE test_id IN (SELECT id FROM tests WHERE book_collection_id IN (SELECT id FROM book_collections WHERE title = %s))))", (BOOK_TITLE,))
    cursor.execute("DELETE FROM questions WHERE question_group_id IN (SELECT id FROM question_groups WHERE test_id IN (SELECT id FROM tests WHERE book_collection_id IN (SELECT id FROM book_collections WHERE title = %s)))", (BOOK_TITLE,))
    cursor.execute("DELETE FROM question_groups WHERE test_id IN (SELECT id FROM tests WHERE book_collection_id IN (SELECT id FROM book_collections WHERE title = %s))", (BOOK_TITLE,))
    cursor.execute("DELETE FROM parts WHERE test_id IN (SELECT id FROM tests WHERE book_collection_id IN (SELECT id FROM book_collections WHERE title = %s))", (BOOK_TITLE,))
    cursor.execute("DELETE FROM tests WHERE book_collection_id IN (SELECT id FROM book_collections WHERE title = %s)", (BOOK_TITLE,))
    cursor.execute("DELETE FROM book_collections WHERE title = %s", (BOOK_TITLE,))
    conn.commit()
    print("[CLEANUP] Done - starting fresh insert")

    try:
        # --- Create BookCollection ---
        book_id = ensure_book_collection(cursor, BOOK_TITLE, BOOK_YEAR)

        # --- Group items by topic ---
        topics = {}
        for item in data:
            topic = item.get("topic", 1)
            if topic not in topics:
                topics[topic] = []
            topics[topic].append(item)

        # --- Process each topic (test) ---
        for topic_number in sorted(topics.keys()):
            items = topics[topic_number]
            print(f"\n{'='*60}")
            print(f"[TOPIC] Processing Topic {topic_number} ({len(items)} items)")
            print(f"{'='*60}")

            test_id = ensure_test(cursor, book_id, topic_number)
            cloudinary_base_folder = f"toeic/ets2026/test{topic_number}"

            # --- Group items by part ---
            parts_data = {}
            for item in items:
                part_num = item.get("part", 1)
                if part_num not in parts_data:
                    parts_data[part_num] = []
                parts_data[part_num].append(item)

            for part_number in sorted(parts_data.keys()):
                part_items = parts_data[part_number]
                part_id = ensure_part(cursor, test_id, part_number)

                print(f"\n  [PART] Part {part_number} ({len(part_items)} items)")

                for item in part_items:
                    if part_number in (1, 2):
                        process_part1_part2(item, cursor, test_id, part_id, cloudinary_base_folder)
                    elif part_number in (3, 4, 5, 6, 7):
                        process_part3_part4(item, cursor, test_id, part_id, cloudinary_base_folder)
                    else:
                        print(f"    [WARN] Unknown part {part_number}, skipping")

                # Commit after each part to avoid losing progress
                conn.commit()
                print(f"  [SAVE] Part {part_number} committed to database")

        # Final commit
        conn.commit()
        print(f"\n{'='*60}")
        print("[DONE] ALL DATA SEEDED SUCCESSFULLY!")
        print(f"{'='*60}")

    except Exception as e:
        conn.rollback()
        print(f"\n[FAIL] Error occurred: {e}")
        import traceback
        traceback.print_exc()
        raise
    finally:
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1")
        cursor.close()
        conn.close()
        print("[INFO] Database connection closed")


if __name__ == "__main__":
    main()
