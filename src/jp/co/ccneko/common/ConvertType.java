package jp.co.ccneko.common;

public enum ConvertType {
	ZIP2EPUB("1"),
	ZIP2EPUB2MOBI("2"),
	EPUB2MOBI("3");

	private final String value;

	ConvertType(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static ConvertType getConvertType(String value) {
		if(value == null || value.trim().isEmpty()) {
			return null;
		}
		final ConvertType[] options = ConvertType.values();
		for (final ConvertType option : options) {
			if(option.getValue().equals(value)) {
				return option;
			}
		}
		return null;
	}

}
