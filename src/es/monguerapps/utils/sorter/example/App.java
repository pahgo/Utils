package es.monguerapps.utils.sorter.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.monguerapps.utils.sorter.DynamicSorter;

public class App {
	static List<Person> people = new ArrayList<Person>();

	public static void main(String[] args) {
		generatePeople();
		String[] fields = new String[] { "name", "surname", "height", "birthday" };
		Boolean[] order = new Boolean[] { false, false, false, true };
		DynamicSorter<Person> sorter = new DynamicSorter<Person>(Person.class, fields, order);
		sorter.reorderList(people);
		for (Person p : people)
			System.out.println(p);
	}

	private static void generatePeople() {
		Person p;
		p = new Person("AAA", "AAA", 70, new Date(System.currentTimeMillis() - (23 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
		p = new Person("B", "BBB", 70, new Date(System.currentTimeMillis() - (23 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
		p = new Person("AAA", "BBB", 71, new Date(System.currentTimeMillis() - (22 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
		p = new Person("AAA", "AAA", 70, new Date(System.currentTimeMillis() - (21 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
	}
}
