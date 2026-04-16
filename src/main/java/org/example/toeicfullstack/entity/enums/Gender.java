package org.example.toeicfullstack.entity.enums;

public enum Gender {
	NAM("Nam"),
	NU("Nữ");

	private final String displayName;

	Gender(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static Gender fromInput(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		String normalized = value.trim();
		for (Gender gender : values()) {
			if (gender.displayName.equalsIgnoreCase(normalized)
					|| gender.name().equalsIgnoreCase(normalized)) {
				return gender;
			}
		}

		throw new IllegalArgumentException("Gender must be 'Nam' or 'Nữ'");
	}

}

