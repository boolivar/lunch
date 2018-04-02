package org.bool.lunch;

import java.util.ArrayList;
import java.util.Arrays;

import org.bool.lunch.Lunch;
import org.bool.lunch.LunchItem;
import org.junit.Assert;
import org.junit.Test;

public class LunchTest {
	
	@Test
	public void testLunch() {
		Lunch lunch = new Lunch();
		
		LunchItem item1 = new LunchItem();
		item1.setClassName("testItem1");
		
		ArrayList<String> args1 = new ArrayList<>();
		args1.add("item1 arg1");
		args1.add("item1 arg2");
		
		item1.setArgs(args1);
		
		LunchItem item2 = new LunchItem();
		item2.setClassName("testItem2");
		
		ArrayList<String> args2 = new ArrayList<>();
		args2.add("item2 arg1");
		args2.add("item2 arg2");
		
		item2.setArgs(args2);
		
		ArrayList<LunchItem> items = new ArrayList<>();
		items.add(item1);
		items.add(item2);
		
		lunch.setItems(items);
		
		Lunch instance = makeLunch();
		
		Assert.assertEquals(instance, lunch);
		Assert.assertEquals(instance.hashCode(), lunch.hashCode());
	}
	
	private Lunch makeLunch() {
		Lunch lunch = new Lunch();
		
		LunchItem item1 = new LunchItem();
		item1.setClassName("testItem1");
		item1.setArgs(Arrays.asList("item1 arg1", "item1 arg2"));
		
		LunchItem item2 = new LunchItem();
		item2.setClassName("testItem2");
		item2.setArgs(Arrays.asList("item2 arg1", "item2 arg2"));
		
		lunch.setItems(Arrays.asList(item1, item2));
		return lunch;
	}
}
