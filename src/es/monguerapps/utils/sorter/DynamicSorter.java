package es.monguerapps.utils.sorter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The {@code DynamicSorter<T>} class provides dynamic, easy and stable ordering
 * for lists.
 * 
 * @author Pahgo
 * 
 * @param <T>
 */
public class DynamicSorter<T> extends DynamicComparator<T> {

	private String[] fields;
	private Boolean[] order;
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
		super(clazz);
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
		super(clazz);
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
	public void reorderList(List<T> list, final String[] fields, final Boolean[] order, final Boolean[] stringAsNumber) {
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

}
