package es.monguerapps.utils.sorter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicSorter<T> {

	private String[] fields;
	private Boolean[] order;
	private Class<T> clazz;

	public DynamicSorter(final Class<T> clazz, String[] fields, Boolean[] order) {
		this.clazz = clazz;
		this.fields = fields;
		this.order = order;
	}

	public void reorderList(List<T> list) {
		reorderList(list, fields, order);
	}

	public void reorderList(List<T> list, final String[] fields, final Boolean[] order) {
		checkFields(fields);
		checkLengths(fields, order);
		Comparator<T> c = new Comparator<T>() {
			public int compare(T arg0, T arg1) {
				int result = 0;
				for (int i = 0; i < fields.length; i++) {
					try {
						Field f = clazz.getDeclaredField(fields[i]);
						f.setAccessible(true);
						result = (order[i] ? 1 : -1) * DynamicSorter.this.compare(f.getType(), f, arg0, arg1);
						if (result != 0)
							break;
					} catch (Exception e) {
						throw new IllegalStateException("Exception ordering.", e);
					}
				}
				return result;
			}
		};
		Collections.sort(list, c);
	}
	private int compare(Class<?> t, Field f, T arg0, T arg1)
			throws IllegalAccessException {
		if (t.isAssignableFrom(Boolean.class)) {
			return ((Boolean) f.get(arg0)).compareTo((Boolean) f.get(arg1));
		} else if (t.isAssignableFrom(Byte.class)) {
			return ((Byte) f.get(arg0)).compareTo((Byte) f.get(arg1));
		} else if (t.isAssignableFrom(Character.class)) {
			return ((Character) f.get(arg0)).compareTo((Character) f.get(arg1));
		} else if (t.isAssignableFrom(Short.class)) {
			return ((Short) f.get(arg0)).compareTo((Short) f.get(arg1));
		} else if (t.isAssignableFrom(Integer.class)) {
			return ((Integer) f.get(arg0)).compareTo((Integer) f.get(arg1));
		} else if (t.isAssignableFrom(Long.class)) {
			return ((Long) f.get(arg0)).compareTo((Long) f.get(arg1));
		} else if (t.isAssignableFrom(Double.class)) {
			return ((Double) f.get(arg0)).compareTo((Double) f.get(arg1));
		} else if (t.isAssignableFrom(Float.class)) {
			return ((Float) f.get(arg0)).compareTo((Float) f.get(arg1));
		} else if (t.isAssignableFrom(String.class)) {
			return ((String) f.get(arg0)).compareTo((String) f.get(arg1));
		} else if (t.isAssignableFrom(Date.class)) {
			return ((Date) f.get(arg0)).compareTo((Date) f.get(arg1));
		} else if (t.isPrimitive()) {
			return compare(primitiveWrapperMap.get(f.getType()), f, arg0, arg1);
		} else
			throw new UnsupportedOperationException(
					String.format("Camparison not yet implemented for '%s'", f.getType()));
	}

	private void checkLengths(String[] fields, Boolean[] order) {
		if (fields.length != order.length)
			throw new IllegalArgumentException("'fields' and 'order' arrays should have same length");
	}

	private void checkFields(String[] fields) {
		Field[] classFields = clazz.getDeclaredFields();
		for (String field : fields) {
			checkField(classFields, field);
		}
	}

	private void checkField(Field[] classFields, String field) {
		boolean exists = false;
		for (Field f1 : classFields) {
			if (f1.getName().equals(field)) {
				exists = true;
				break;
			}
		}
		if (!exists)
			throw new IllegalArgumentException(
					String.format("Field %s does not exists in class %s.", field, clazz.getName()));
	}

	private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();
	static {
		primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
		primitiveWrapperMap.put(Byte.TYPE, Byte.class);
		primitiveWrapperMap.put(Character.TYPE, Character.class);
		primitiveWrapperMap.put(Short.TYPE, Short.class);
		primitiveWrapperMap.put(Integer.TYPE, Integer.class);
		primitiveWrapperMap.put(Long.TYPE, Long.class);
		primitiveWrapperMap.put(Double.TYPE, Double.class);
		primitiveWrapperMap.put(Float.TYPE, Float.class);
		primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
	}

}
