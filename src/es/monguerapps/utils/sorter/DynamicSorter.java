package es.monguerapps.utils.sorter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code DynamicSorter<T>} class provides dynamic, easy and stable ordering
 * for lists.
 * 
 * @author Pahgo
 * 
 * @param <T>
 */
public class DynamicSorter<T> {

	private String[] fields;
	private Boolean[] order;
	private Class<T> clazz;
	private Boolean[] stringAsNumber;

	/**
	 * Constructor for general purpose, orders by each field ascending or
	 * descending depending of true or false in the order array and takes next
	 * field when two values are equivalent.
	 * 
	 * Booth arrays must have same length.
	 * 
	 * @param clazz
	 *            Again the parametrized class.
	 * @param fields
	 *            Name of the properties to order in the order to.
	 * @param order
	 *            Values for ascending/descending as true/false.
	 */
	public DynamicSorter(final Class<T> clazz, String[] fields, Boolean[] order) {
		this.clazz = clazz;
		this.fields = fields;
		this.order = order;
		this.stringAsNumber = new Boolean[fields.length];
		Arrays.fill(stringAsNumber, false);
	}

	/**
	 * Adds the posibility to order a string as is it where a {@code Double}.
	 * 
	 * @param clazz
	 *            Again the parametrized class.
	 * @param fields
	 *            Name of the properties to order in the order to.
	 * @param order
	 *            Values for ascending/descending as true/false.
	 * @param stringAsNumber
	 *            Take the (if it is a) {@code String} field as a Double if the
	 *            positions in both arrays are the same.
	 * @see DynamicSorter#DynamicSorter(Class, String[], Boolean[])
	 */
	public DynamicSorter(final Class<T> clazz, String[] fields, Boolean[] order, Boolean[] stringAsNumber) {
		this.clazz = clazz;
		this.fields = fields;
		this.order = order;
		this.stringAsNumber = stringAsNumber;
	}

	/**
	 * Orders the list if the fields, order and stringAsNumber provided are
	 * still valid.
	 * 
	 * @param list
	 *            The list to order.
	 */
	public void reorderList(List<T> list) {
		reorderList(list, fields, order, stringAsNumber);
	}

	/**
	 * Orders the list with new fields and order. Uses the stringAsNumber
	 * provided.
	 * 
	 * @param list
	 *            The list to order.
	 * @param fields
	 *            Name of the properties to order in the order to.
	 * @param order
	 *            Values for ascending/descending as true/false.
	 * @see #reorderList(List)
	 */
	public void reorderList(List<T> list, final String[] fields, final Boolean[] order) {
		reorderList(list, fields, order, stringAsNumber);
	}

	/**
	 * Orders the list with new fields, order and stringAsNumber.
	 * 
	 * @param list
	 *            The list to order.
	 * @param fields
	 *            Name of the properties to order in the order to.
	 * @param order
	 *            Values for ascending/descending as true/false.
	 * @param strinAsNumber
	 *            Take the (if it is a) {@code String} field as a Double if the
	 *            positions in both arrays are the same.
	 * @see #reorderList(List)
	 */
	public void reorderList(List<T> list, final String[] fields, final Boolean[] order,
			final Boolean[] stringAsNumber) {
		checkFields(fields);
		checkLengths(fields, order);
		Comparator<T> c = new Comparator<T>() {
			public int compare(T arg0, T arg1) {
				int result = 0;
				for (int i = 0; i < fields.length; i++) {
					try {
						Field f = getFielAccesible(fields[i]);
						result = (order[i] ? 1 : -1)
								* DynamicSorter.this.compare(f.getType(), f, arg0, arg1, stringAsNumber[i]);
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

	private int compare(Class<?> t, Field f, T arg0, T arg1, Boolean stringAsNumber) throws IllegalAccessException {
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
			if (stringAsNumber)
				return (Double.valueOf((String) f.get(arg0)).compareTo(Double.valueOf((String) f.get(arg1))));
			return ((String) f.get(arg0)).compareTo((String) f.get(arg1));
		} else if (t.isAssignableFrom(Date.class)) {
			return ((Date) f.get(arg0)).compareTo((Date) f.get(arg1));
		} else if (t.isPrimitive()) {
			return compare(primitiveWrapperMap.get(f.getType()), f, arg0, arg1, stringAsNumber);
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

	private Field getFielAccesible(String name) throws NoSuchFieldException {
		Field f = clazz.getDeclaredField(name);
		f.setAccessible(true);
		return f;
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
