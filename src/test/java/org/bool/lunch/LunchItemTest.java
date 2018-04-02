package org.bool.lunch;

import java.util.ArrayList;
import java.util.Arrays;

import org.bool.lunch.LunchItem;
import org.junit.Assert;
import org.junit.Test;

public class LunchItemTest {

	@Test
	public void testLunchItem() {
		LunchItem item = new LunchItem();
		item.setClassName("TestClass1");
		
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
		item1.setClassName("TestClass1");
		item1.setArgs(Arrays.asList("test arg1", "test arg2"));
		return item1;
	}
}
