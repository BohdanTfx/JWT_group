package com.epam.test.transformer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatementManager {

	public void fillSave(PreparedStatement statement,
			List<ColumnItem> columnItems, List<ColumnItem> ids)
	{
		int j = 0;
		List<ColumnItem> customIds = getCustomId(ids);
		for (ColumnItem item : columnItems) {
			try {
				statement.setObject(++j, item.getValue());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		for (ColumnItem item : customIds) {
			try {
				statement.setObject(++j, item.getValue());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String getSaveStatement(List<ColumnItem> columnItems,
			List<ColumnItem> ids, String table)
	{
		StringBuilder builder = new StringBuilder("insert into " + table + " (");
		List<ColumnItem> customIds = getCustomId(ids);
		for (int i = 0; i < columnItems.size(); i++) {
			ColumnItem item = columnItems.get(i);
			if (i + 1 < columnItems.size()) {
				builder.append(item.getColumnName());
				builder.append(", ");
			} else {
				builder.append(item.getColumnName());
			}
		}

		if (customIds.size() > 0)
			builder.append(", ");
		for (int i = 0; i < customIds.size(); i++) {
			ColumnItem item = customIds.get(i);
			if (i + 1 < customIds.size()) {
				builder.append(item.getColumnName());
				builder.append(", ");
			} else {
				builder.append(item.getColumnName());
			}
		}
		
		builder.append(" ) values( ");
		for (int i = 0; i < customIds.size() + columnItems.size(); i++) {
			if (i + 1 < customIds.size() + columnItems.size()) {
				builder.append("?, ");
			} else {
				builder.append("? )");
			}
		}
		return builder.toString();
	}

	private List<ColumnItem> getCustomId(List<ColumnItem> ids) {
		List<ColumnItem> list = new ArrayList<ColumnItem>();
		for (ColumnItem columnItem : ids) {
			if (!columnItem.isAutoIncrementedId())
				list.add(columnItem);
		}
		return list;
	}

	public void fillUpdate(PreparedStatement statement,
			List<ColumnItem> columnItems, List<ColumnItem> ids)
	{
		int j = 0;
		for (ColumnItem item : columnItems) {
			try {
				statement.setObject(++j, item.getValue());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		for (ColumnItem item : ids) {
			try {
				statement.setObject(++j, item.getValue());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String getUpdateStatement(List<ColumnItem> columnItems,
			List<ColumnItem> ids, String table)
	{
		StringBuilder builder = new StringBuilder("update " + table + " set ");
		for (int i = 0; i < columnItems.size(); i++) {
			ColumnItem item = columnItems.get(i);
			if (i + 1 < columnItems.size()) {
				builder.append(item.getColumnName());
				builder.append(" = ?, ");
			} else {
				builder.append(item.getColumnName());
				builder.append(" = ? where ");
			}
		}
		for (int i = 0; i < ids.size(); i++) {
			ColumnItem item = ids.get(i);
			if (i + 1 < ids.size()) {
				builder.append(item.getColumnName());
				builder.append(" = ? and ");
			} else {
				builder.append(item.getColumnName());
				builder.append(" = ?");
			}
		}

		return builder.toString();
	}

	public void fillDelete(PreparedStatement statement, List<ColumnItem> ids) {
		for (int i = 0; i < ids.size(); i++) {
			ColumnItem item = ids.get(i);
			try {
				statement.setObject(i + 1, item.getValue());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String getDeleteStatement(List<ColumnItem> ids, String table) {
		StringBuilder builder = new StringBuilder("delete from " + table
				+ " where ");
		for (int i = 0; i < ids.size(); i++) {
			ColumnItem item = ids.get(i);
			if (i + 1 < ids.size()) {
				builder.append(item.getColumnName());
				builder.append(" = ? and ");
			} else {
				builder.append(item.getColumnName());
				builder.append(" = ?");
			}
		}

		return builder.toString();
	}

	public void fillSelect(PreparedStatement statement, List<ColumnItem> ids) {
		for (int i = 0; i < ids.size(); i++) {
			ColumnItem item = ids.get(i);
			try {
				statement.setObject(i + 1, item.getValue());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String getSelectStatement(List<ColumnItem> ids, String table) {
		StringBuilder builder = new StringBuilder("select * from " + table
				+ " where ");
		for (int i = 0; i < ids.size(); i++) {
			ColumnItem item = ids.get(i);
			if (i + 1 < ids.size()) {
				builder.append(item.getColumnName());
				builder.append(" = ? and ");
			} else {
				builder.append(item.getColumnName());
				builder.append(" = ?");
			}
		}

		return builder.toString();
	}
}
