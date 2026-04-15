package org.example.toeicfullstack.entity;

public enum Gender {
	MALE("Nam"),
	FEMALE("Nữ");

	private final String displayName;

	Gender(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static Gender fromDisplayName(String displayName) {
		for (Gender gender : Gender.values()) {
			if (gender.displayName.equals(displayName)) {
				return gender;
			}
		}
		throw new IllegalArgumentException("Invalid gender: " + displayName);
	}
}

