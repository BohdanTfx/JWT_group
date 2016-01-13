package com.epam.test.transformer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.epam.test.transformer.annotation.Column;
import com.epam.test.transformer.annotation.Id;
import com.epam.test.transformer.annotation.Table;

public class DataTransformer<T> {
	private StatementManager statementManager = new StatementManager();
	private List<ColumnItem> columns;
	private List<ColumnItem> ids;
	private String tableName;
	private boolean fieldType;
	private Class<T> clazz;

	public DataTransformer(Class<T> clazz) throws Exception {
		this.clazz = clazz;

		columns = new ArrayList<>();
		ids = new ArrayList<>();
		configTransformer();
		getColumns();
	}

	private void configTransformer() throws Exception {
		Annotation annotation = clazz.getDeclaredAnnotation(Table.class);
		Method name = annotation.getClass().getDeclaredMethod("name");
		Method type = annotation.getClass().getDeclaredMethod(
				"getValuesByField");
		tableName = (String) name.invoke(annotation);
		fieldType = (boolean) type.invoke(annotation);
	}

	private void getColumns() throws Exception {
		if (fieldType)
			getColumnByFields();
		else
			getColumnByGetters();
	}

	private void getColumnByGetters() throws Exception {
		columns.clear();
		ids.clear();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			method.setAccessible(true);
			Annotation annotation = method.getDeclaredAnnotation(Column.class);
			if (annotation != null) {
				ColumnItem columnValue = new ColumnItem();
				Method name = annotation.getClass().getDeclaredMethod("name");
				String itemName = (String) name.invoke(annotation);
				if ("".equals(itemName))
					itemName = getNameFromGetter(method.getName());
				columnValue.setColumnName(itemName);
				columnValue.setAttributeName(method.getName().substring(1));
				columnValue.setType(method.getReturnType());

				columns.add(columnValue);
			}

			annotation = method.getDeclaredAnnotation(Id.class);
			if (annotation != null) {
				ColumnItem columnValue = new ColumnItem();
				Method name = annotation.getClass().getDeclaredMethod("name");
				String itemName = (String) name.invoke(annotation);
				if ("".equals(itemName))
					itemName = getNameFromGetter(method.getName());
				columnValue.setColumnName(itemName);
				columnValue.setAttributeName(method.getName().substring(1));
				columnValue.setType(method.getReturnType());

				ids.add(columnValue);
			}
		}
	}

	private String getNameFromGetter(String getter) {
		if (getter == null || !getter.startsWith("get"))
			return null;
		return Character.toString(getter.charAt(3)).toLowerCase()
				+ getter.substring(4);
	}

	private void getColumnByFields() throws Exception {
		columns.clear();
		ids.clear();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Annotation annotation = field.getDeclaredAnnotation(Column.class);
			if (annotation != null) {
				ColumnItem columnValue = new ColumnItem();
				Method name = annotation.getClass().getDeclaredMethod("name");
				String itemName = (String) name.invoke(annotation);
				if ("".equals(itemName))
					itemName = field.getName();
				columnValue.setColumnName(itemName);
				columnValue.setAttributeName(field.getName());
				columnValue.setType(field.getType());

				columns.add(columnValue);
			}

			annotation = field.getDeclaredAnnotation(Id.class);
			if (annotation != null) {
				ColumnItem columnValue = new ColumnItem();
				Method name = annotation.getClass().getDeclaredMethod("name");
				Method incremenetId = annotation.getClass().getDeclaredMethod(
						"autoIncrement");
				columnValue.setAutoIncrementedId((Boolean) incremenetId
						.invoke(annotation));
				String itemName = (String) name.invoke(annotation);
				if ("".equals(itemName))
					itemName = field.getName();
				columnValue.setColumnName(itemName);
				columnValue.setAttributeName(field.getName());
				columnValue.setType(field.getType());

				ids.add(columnValue);
			}
		}
	}

	public T getObject(ResultSet set) throws Exception {
		T obj = clazz.newInstance();
		setData(obj, set);
		return obj;
	}

	private void setData(T obj, ResultSet set) throws Exception {
		if (fieldType)
			setByFields(obj, set);
		else
			setBySetters(obj, set);
	}

	private void setBySetters(T obj, ResultSet set) throws Exception {
		for (ColumnItem columnItem : columns) {
			Method method = clazz.getDeclaredMethod(
					"s" + columnItem.getAttributeName(), columnItem.getType());
			method.setAccessible(true);
			method.invoke(obj, set.getObject(columnItem.getColumnName()));
		}

		for (ColumnItem columnItem : ids) {
			Method method = clazz.getDeclaredMethod(
					"s" + columnItem.getAttributeName(), columnItem.getType());
			method.setAccessible(true);
			method.invoke(obj, set.getObject(columnItem.getColumnName()));
		}
	}

	private void setByFields(T obj, ResultSet set) throws Exception {
		for (ColumnItem columnItem : columns) {
			Field field = clazz.getDeclaredField(columnItem.getAttributeName());
			field.setAccessible(true);
			field.set(obj, set.getObject(columnItem.getColumnName()));
		}

		for (ColumnItem columnItem : ids) {
			Field field = clazz.getDeclaredField(columnItem.getAttributeName());
			field.setAccessible(true);
			field.set(obj, set.getObject(columnItem.getColumnName()));
		}
	}

	public String getSaveStatement() {
		return statementManager.getSaveStatement(columns, ids, tableName);
	}

	public String getUpdateStatement() {
		return statementManager.getUpdateStatement(columns, ids, tableName);
	}

	public String getDeleteStatement() {
		return statementManager.getDeleteStatement(ids, tableName);
	}

	public String getSelectStatement() {
		return statementManager.getSelectStatement(ids, tableName);
	}

	public void fillSave(PreparedStatement statement, T obj) throws Exception {
		fillAttributes(obj, false);
		statementManager.fillSave(statement, columns, ids);
	}

	public void fillUpdate(PreparedStatement statement, T obj) throws Exception
	{
		fillAttributes(obj, false);
		statementManager.fillUpdate(statement, columns, ids);
	}

	public void fillDelete(PreparedStatement statement, T obj) throws Exception
	{
		fillAttributes(obj, true);
		statementManager.fillDelete(statement, ids);
	}

	public void fillSelect(PreparedStatement statement, T obj) throws Exception
	{
		fillAttributes(obj, true);
		statementManager.fillSelect(statement, ids);
	}

	private void fillAttributes(T obj, boolean idOnly) throws Exception {
		if (fieldType)
			fillByFields(obj, idOnly);
		else
			fillByGetter(obj, idOnly);
	}

	private void fillByFields(T obj, boolean idOnly) throws Exception {
		if (!idOnly)
			for (ColumnItem item : columns) {
				Field field = clazz.getDeclaredField(item.getAttributeName());
				field.setAccessible(true);
				item.setValue(field.get(obj));
			}

		for (ColumnItem item : ids) {
			Field field = clazz.getDeclaredField(item.getAttributeName());
			field.setAccessible(true);
			item.setValue(field.get(obj));
		}
	}

	private void fillByGetter(T obj, boolean idOnly) throws Exception {
		if (!idOnly)
			for (ColumnItem item : columns) {
				Method method = clazz.getDeclaredMethod("g"
						+ item.getAttributeName());
				method.setAccessible(true);
				item.setValue(method.invoke(obj));
			}

		for (ColumnItem item : ids) {
			Method method = clazz.getDeclaredMethod("g"
					+ item.getAttributeName());
			method.setAccessible(true);
			item.setValue(method.invoke(obj));
		}
	}
}
