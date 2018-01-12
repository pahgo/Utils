package es.monguerapps.utils.sorter;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DynamicComparator<T> {

	protected Class<T> clazz;

	public DynamicComparator(Class<T> clazz) {
		this.clazz = clazz;
	}

	void checkLengths(String[] fields, Boolean[] order) {
		if (fields.length != order.length)
			throw new IllegalArgumentException("'fields' and 'order' arrays should have same length");
	}

	void checkFields(String[] fields) {
		Field[] classFields = clazz.getDeclaredFields();
		for (String field : fields) {
			checkField(classFields, field);
		}
	}

	private void checkField(Field[] classFields, String field) {
		Boolean exists = false;
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

	Field getFielAccesible(String name) throws NoSuchFieldException {
		Field f = clazz.getDeclaredField(name);
		f.setAccessible(true);
		return f;
	}

	protected int compare(Class<?> t, Field f, T arg0, T arg1, Boolean stringAsNumber) throws IllegalAccessException {
		if (f.get(arg0) == f.get(arg1)) {
			return 0;
		} else if (f.get(arg0) == null) {
			return 1;
		} else if (f.get(arg1) == null) {
			return -1;
		} else {
			return compare(t, f.get(arg0), f.get(arg1), stringAsNumber);
		}
	}

	private int compare(Class<?> t, Object c0, Object c1, Boolean stringAsNumber) {
		if (t.isAssignableFrom(Boolean.class)) {
			return ((Boolean) c0).compareTo((Boolean) c1);
		} else if (t.isAssignableFrom(Byte.class)) {
			return ((Byte) c0).compareTo((Byte) c1);
		} else if (t.isAssignableFrom(Character.class)) {
			return ((Character) c0).compareTo((Character) c1);
		} else if (t.isAssignableFrom(Short.class)) {
			return ((Short) c0).compareTo((Short) c1);
		} else if (t.isAssignableFrom(Integer.class)) {
			return ((Integer) c0).compareTo((Integer) c1);
		} else if (t.isAssignableFrom(Long.class)) {
			return ((Long) c0).compareTo((Long) c1);
		} else if (t.isAssignableFrom(Double.class)) {
			return ((Double) c0).compareTo((Double) c1);
		} else if (t.isAssignableFrom(Float.class)) {
			return ((Float) c0).compareTo((Float) c1);
		} else if (t.isAssignableFrom(String.class)) {
			if (stringAsNumber)
				return (Double.valueOf((String) c0).compareTo(Double.valueOf((String) c1)));
			return ((String) c0).compareTo((String) c1);
		} else if (t.isAssignableFrom(Date.class)) {
			return ((Date) c0).compareTo((Date) c1);
		} else if (t.isPrimitive()) {
			return compare(primitiveWrapperMap.get(t), c0, c1, stringAsNumber);
		} else
			throw new UnsupportedOperationException(String.format("Camparison not yet implemented for '%s'", t));
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