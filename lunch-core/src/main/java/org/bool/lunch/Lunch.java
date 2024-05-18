package org.bool.lunch;

import java.util.List;
import java.util.Objects;

public class Lunch {

	private List<LunchItem> items;
	
	public Lunch() {
	}
	
	public Lunch(List<LunchItem> items) {
		this.items = items;
	}
	
	public List<LunchItem> getItems() {
		return items;
	}

	public void setItems(List<LunchItem> items) {
		this.items = items;
	}
	
	@Override
	public String toString() {
		return "Lunch [items=" + items + "]";
	}
	
	@Override
	public int hashCode() {
		return 31 + Objects.hashCode(items);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && getClass() == obj.getClass()) {
			return Objects.equals(this.items, ((Lunch) obj).items);
		}
		return false;
	}
}
