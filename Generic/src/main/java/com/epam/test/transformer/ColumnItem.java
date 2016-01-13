package com.epam.test.transformer;


public class ColumnItem {
	private String columnName;
	private String attributeName;
	private boolean autoIncrementedId = false;
	private Object value;
	private Class<?> type;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public boolean isAutoIncrementedId() {
		return autoIncrementedId;
	}

	public void setAutoIncrementedId(boolean autoIncrementedId) {
		this.autoIncrementedId = autoIncrementedId;
	}

	@Override
	public String toString() {
		return "ColumnValue ["
				+ (columnName != null ? "columnName=" + columnName + ", " : "")
				+ (value != null ? "value=" + value + ", " : "")
				+ (type != null ? "type=" + type : "") + "]";
	}
}
