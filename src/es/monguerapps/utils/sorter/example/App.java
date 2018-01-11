package es.monguerapps.utils.sorter.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.monguerapps.utils.sorter.DynamicSorter;

public class App {
	static List<Person> people = new ArrayList<Person>();

	public static void main(String[] args) {
		generatePeople();
		String[] fields = new String[] { "name", "surname", "height", "birthday" };
		Boolean[] order = new Boolean[] { true, true, false, true };
		Boolean[] asNum = new Boolean[] { true, false, false, false };
		DynamicSorter<Person> sorter = new DynamicSorter<Person>(Person.class, fields, order, asNum);
		sorter.reorderList(people);
		for (Person p : people)
			System.out.println(p);
	}

	private static void generatePeople() {
		Person p;
		p = new Person("1", "BBB", 70, new Date(System.currentTimeMillis() - (23 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
		p = new Person("1.0", "BBB", 70, new Date(System.currentTimeMillis() - (24 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
		p = new Person("1", "AAA", 70, new Date(System.currentTimeMillis() - (26 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
		p = new Person("10", "BBB", 70, new Date(System.currentTimeMillis() - (23 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
		p = new Person("9", "BBB", 71, new Date(System.currentTimeMillis() - (22 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
		p = new Person("11", "AAA", 70, new Date(System.currentTimeMillis() - (21 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
		p = new Person("1.1", "AAA", 70, new Date(System.currentTimeMillis() - (21 * 365 * 24 * 60 * 60 * 1000)));
		people.add(p);
		Collections.shuffle(people);//No real need
	}
}
