package org.bool.lunch;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class LunchItemTest {

	@Test
	public void testLunchItem() {
		LunchItem item = new LunchItem();
		item.setCommand("TestClass1");
		
		ArrayList<String> args1 = new ArrayList<>();
		args1.add("test arg1");
		args1.add("test arg2");
		
		item.setArgs(args1);
		
		LunchItem instance = lunchItem();
		Assert.assertEquals(instance, item);
		Assert.assertEquals(instance.hashCode(), item.hashCode());
	}
	
	private LunchItem lunchItem() {
		LunchItem item1 = new LunchItem();
		item1.setCommand("TestClass1");
		item1.setArgs(Arrays.asList("test arg1", "test arg2"));
		return item1;
	}
	
	@Test
	public void testEmptyItems() {
		LunchItem item1 = new LunchItem();
		LunchItem item2 = new LunchItem();
		
		Assert.assertEquals(item1, item2);
		Assert.assertEquals(item1.hashCode(), item2.hashCode());
	}
	
	@Test
	public void testSingleCommandItems() {
		LunchItem item1 = new LunchItem("TestCommand");
		LunchItem item2 = new LunchItem("TestCommand");
		
		Assert.assertEquals(item1, item2);
		Assert.assertEquals(item1.hashCode(), item2.hashCode());
	}
}
