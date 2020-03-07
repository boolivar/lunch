package org.bool.lunch;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunchItemTest {

	@Test
	void testLunchItem() {
		LunchItem item = new LunchItem();
		item.setCommand("TestClass1");
		
		ArrayList<String> args1 = new ArrayList<>();
		args1.add("test arg1");
		args1.add("test arg2");
		
		item.setArgs(args1);
		
		LunchItem instance = lunchItem();
		assertEquals(instance, item);
		assertEquals(instance.hashCode(), item.hashCode());
	}
	
	private LunchItem lunchItem() {
		LunchItem item1 = new LunchItem();
		item1.setCommand("TestClass1");
		item1.setArgs(Arrays.asList("test arg1", "test arg2"));
		return item1;
	}
	
	@Test
	void testEmptyItems() {
		LunchItem item1 = new LunchItem();
		LunchItem item2 = new LunchItem();
		
		assertEquals(item1, item2);
		assertEquals(item1.hashCode(), item2.hashCode());
	}
	
	@Test
	void testSingleCommandItems() {
		LunchItem item1 = new LunchItem("TestCommand");
		LunchItem item2 = new LunchItem("TestCommand");
		
		assertEquals(item1, item2);
		assertEquals(item1.hashCode(), item2.hashCode());
	}
}
