package org.bool.lunch.scalecube;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LunchInfo {

	private final String pid;

	private final String name;

	private final Object details;

	private final Integer exitCode;

}
