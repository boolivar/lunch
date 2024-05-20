package org.bool.lunch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LunchItem {

	private String name;

	private String type;

	private String command;

	private List<String> args;

}
