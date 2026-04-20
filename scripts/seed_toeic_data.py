"""
Seed TOEIC data from data.json into MySQL database.

DB Schema (mapped from JPA entities):
  - book_collections: id, title, yearPublished
  - tests: id, name, isFreeTrial, book_collection_id
  - parts: id, partNumber, instructionText
  - test_parts: test_id, part_id  (ManyToMany join table)
  - question_groups: id, passageText, audioUrl, imageUrl, transcript, translation, groupOrder, test_id, part_id
  - questions: id, question_type (discriminator), content, score, explanationText, skillType, question_number, question_group_id,
               correctOption, audioUrl, imageUrl (MultipleChoice),
               transcript, translation, hint (Dictation),
               correctText, hint (FillBlank),
               correctSequence (Ordering)
  - multiple_choice_question_options: question_id, option_order, option_text
"""

import json
import uuid
import mysql.connector

# ========== CONFIG ==========
DB_CONFIG = {
    "host": "mysql-9c700f1-toeic-fullstack.e.aivencloud.com",
    "port": 12818,
    "user": "avnadmin",
    "password": "***REMOVED***",
    "database": "toeic_fullstack",
    "ssl_disabled": False,
    "use_pure": True,
    "charset": "utf8mb4",
}

DATA_FILE = "data.json"
BOOK_TITLE = "ETS 2026"
BOOK_YEAR = 2026
# =============================


def gen_uuid():
    return str(uuid.uuid4())


def get_skill_type(part_num):
    """Part 1-4 = LISTENING, Part 5-7 = READING"""
    if part_num <= 4:
        return "LISTENING"
    return "READING"


def parse_img_field(img_value):
    """
    Distinguish between image URL and HTML passage content.
    Returns (image_url, passage_text)
    """
    if img_value is None:
        return None, None
    img_str = str(img_value).strip()
    if img_str.startswith("http"):
        return img_str, None  # It's an image URL
    else:
        return None, img_str  # It's HTML passage content


def main():
    # Load data
    with open(DATA_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)

    # Connect DB
    conn = mysql.connector.connect(**DB_CONFIG)
    conn.autocommit = False
    cursor = conn.cursor()

    cursor.execute("SET SESSION sql_require_primary_key = OFF")
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0")

    try:
        # ===== Clean existing data =====
        print("[*] Cleaning existing data...")
        cursor.execute("DELETE FROM multiple_choice_question_options")
        cursor.execute("DELETE FROM ordering_question_shuffled_words")
        cursor.execute("DELETE FROM questions")
        cursor.execute("DELETE FROM question_groups")
        cursor.execute("DELETE FROM test_parts")
        cursor.execute("DELETE FROM tests")
        cursor.execute("DELETE FROM parts")
        cursor.execute("DELETE FROM book_collections")
        print("[*] Clean done.")

        # ===== 1. Create BookCollection =====
        book_id = gen_uuid()
        cursor.execute(
            "INSERT INTO book_collections (id, title, yearPublished) VALUES (%s, %s, %s)",
            (book_id, BOOK_TITLE, BOOK_YEAR),
        )
        print(f"[+] BookCollection: {BOOK_TITLE} ({book_id})")

        # ===== 2. Create Parts (1-7) =====
        part_ids = {}
        for pn in range(1, 8):
            pid = gen_uuid()
            cursor.execute(
                "INSERT INTO parts (id, partNumber, instructionText) VALUES (%s, %s, %s)",
                (pid, pn, None),
            )
            part_ids[pn] = pid
        print(f"[+] Parts 1-7 created")

        # ===== 3. Group data by topic (test) =====
        tests_data = {}
        for item in data:
            topic = item.get("topic", 1)
            if topic not in tests_data:
                tests_data[topic] = []
            tests_data[topic].append(item)

        total_qg = 0
        total_q = 0

        for topic_num, items in sorted(tests_data.items()):
            # ===== 4. Create Test =====
            test_id = gen_uuid()
            test_name = f"Test {topic_num}"
            cursor.execute(
                "INSERT INTO tests (id, name, isFreeTrial, book_collection_id) VALUES (%s, %s, %s, %s)",
                (test_id, test_name, False, book_id),
            )
            print(f"\n[+] Test: {test_name}")

            # ===== 5. Link Test <-> Parts =====
            parts_in_test = set()
            for item in items:
                parts_in_test.add(item.get("part"))
            for pn in sorted(parts_in_test):
                cursor.execute(
                    "INSERT INTO test_parts (test_id, part_id) VALUES (%s, %s)",
                    (test_id, part_ids[pn]),
                )
            print(f"    Parts: {sorted(parts_in_test)}")

            # ===== 6. Process each item =====
            group_order = 0
            for item in items:
                part_num = item.get("part")
                part_id = part_ids[part_num]
                skill_type = get_skill_type(part_num)
                group_order += 1
                total_qg += 1

                # --- Parse img: URL vs HTML passage ---
                raw_img = item.get("group_img") or item.get("img")
                image_url, passage_from_img = parse_img_field(raw_img)

                audio_url = item.get("group_audio") or item.get("audio")
                script_text = item.get("script")
                voca_text = item.get("voca")
                content_text = item.get("content")  # e.g. "Questions 131-134 refer to..."

                # passageText = HTML passage (from img) OR content field
                passage_text = passage_from_img or content_text

                # Create QuestionGroup
                qg_id = gen_uuid()
                cursor.execute(
                    """INSERT INTO question_groups 
                    (id, passageText, audioUrl, imageUrl, transcript, translation, groupOrder, test_id, part_id) 
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                    (qg_id, passage_text, audio_url, image_url, script_text, voca_text, group_order, test_id, part_id),
                )

                q_count = 0

                if part_num in (1, 2):
                    q_count = seed_part12(cursor, item, qg_id, skill_type)
                elif part_num in (3, 4):
                    q_count = seed_part34(cursor, item, qg_id, skill_type)
                elif part_num == 5:
                    q_count = seed_part5(cursor, item, qg_id, skill_type)
                elif part_num in (6, 7):
                    q_count = seed_part67(cursor, item, qg_id, skill_type)

                total_q += q_count

        cursor.execute("SET FOREIGN_KEY_CHECKS = 1")
        conn.commit()
        print(f"\n=== SEED COMPLETED ===")
        print(f"  QuestionGroups: {total_qg}")
        print(f"  Questions: {total_q}")

    except Exception as e:
        conn.rollback()
        print(f"\n[ERROR] {e}")
        raise
    finally:
        cursor.close()
        conn.close()


# ================= SEED FUNCTIONS =================

def insert_mc_question(cursor, qg_id, skill_type, q_no, content, key, options, explanation=None):
    """Insert a MULTIPLE_CHOICE question with options. Returns question id."""
    q_id = gen_uuid()
    cursor.execute(
        """INSERT INTO questions 
        (id, question_type, content, score, explanationText, skillType, question_number, question_group_id,
         correctOption, audioUrl, imageUrl)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
        (q_id, "MULTIPLE_CHOICE", content, 5, explanation, skill_type, q_no, qg_id,
         key, None, None),
    )
    for idx, opt_text in enumerate(options):
        cursor.execute(
            "INSERT INTO multiple_choice_question_options (question_id, option_order, option_text) VALUES (%s, %s, %s)",
            (q_id, idx, opt_text),
        )
    return q_id


def insert_dictation_questions(cursor, qg_id, skill_type, base_no, dictations, dictation_keys):
    """Insert DICTATION questions. Returns count."""
    count = 0
    for i, (d_text, d_key) in enumerate(zip(dictations, dictation_keys)):
        dq_id = gen_uuid()
        cursor.execute(
            """INSERT INTO questions 
            (id, question_type, content, score, explanationText, skillType, question_number, question_group_id,
             transcript, translation, hint, audioUrl)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
            (dq_id, "DICTATION", d_text, 5, None, skill_type, base_no * 1000 + i + 1, qg_id,
             d_key, None, d_text, None),
        )
        count += 1
    return count


def seed_part12(cursor, item, qg_id, skill_type):
    """Part 1 & 2: 1 MC question + dictation."""
    q_no = item.get("no")
    key = item.get("key")
    count = 0

    # MC question (Part 1-2 options are in audio, no text options)
    q_id = gen_uuid()
    cursor.execute(
        """INSERT INTO questions 
        (id, question_type, content, score, explanationText, skillType, question_number, question_group_id,
         correctOption, audioUrl, imageUrl)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
        (q_id, "MULTIPLE_CHOICE", None, 5, None, skill_type, q_no, qg_id,
         key, None, None),
    )
    count += 1
    print(f"    [Q{q_no}] MC key={key}")

    # Dictation
    dictations = item.get("dictation", [])
    dictation_keys = item.get("dictation_key", [])
    count += insert_dictation_questions(cursor, qg_id, skill_type, q_no, dictations, dictation_keys)
    return count


def seed_part34(cursor, item, qg_id, skill_type):
    """Part 3 & 4: Multiple MC + dictation."""
    count = 0

    for gq in item.get("group", []):
        q_no = gq.get("no")
        options = [gq.get("a", ""), gq.get("b", ""), gq.get("c", ""), gq.get("d", "")]
        insert_mc_question(cursor, qg_id, skill_type, q_no, gq.get("content"), gq.get("key"), options)
        count += 1
        print(f"    [Q{q_no}] MC key={gq.get('key')}")

    # Dictation
    dictations = item.get("dictation", [])
    dictation_keys = item.get("dictation_key", [])
    base_no = item.get("no", 0)
    count += insert_dictation_questions(cursor, qg_id, skill_type, base_no, dictations, dictation_keys)
    return count


def seed_part5(cursor, item, qg_id, skill_type):
    """Part 5: MC questions (reading fill-in-blank)."""
    count = 0
    group_questions = item.get("group", [])

    if group_questions:
        for gq in group_questions:
            q_no = gq.get("no")
            options = [gq.get("a", ""), gq.get("b", ""), gq.get("c", ""), gq.get("d", "")]
            insert_mc_question(cursor, qg_id, skill_type, q_no, gq.get("content"), gq.get("key"), options)
            count += 1
            print(f"    [Q{q_no}] MC key={gq.get('key')}")
    else:
        q_no = item.get("no")
        insert_mc_question(cursor, qg_id, skill_type, q_no, item.get("content"), item.get("key"), [],
                           explanation=item.get("script"))
        count += 1
        print(f"    [Q{q_no}] MC key={item.get('key')}")
    return count


def seed_part67(cursor, item, qg_id, skill_type):
    """Part 6 & 7: Passage (HTML in passageText) + MC questions."""
    count = 0

    for gq in item.get("group", []):
        q_no = gq.get("no")
        options = [gq.get("a", ""), gq.get("b", ""), gq.get("c", ""), gq.get("d", "")]
        insert_mc_question(cursor, qg_id, skill_type, q_no, gq.get("content"), gq.get("key"), options)
        count += 1
        print(f"    [Q{q_no}] MC key={gq.get('key')}")
    return count


if __name__ == "__main__":
    main()
