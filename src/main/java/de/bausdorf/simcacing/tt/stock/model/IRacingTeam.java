package de.bausdorf.simcacing.tt.stock.model;

import java.security.acl.Owner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class IRacingTeam {

	public static final String NAME = "Name";
	public static final String TEAM_ID = "TeamId";
	public static final String OWNER_ID = "OwnerId";
	public static final String AUTHORIZED_DRIVERS = "AuthorizedDrivers";

	private String name;
	private String id;
	private String ownerId;
	private List<String> authorizedDriverIds;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(NAME, name);
		map.put(TEAM_ID, id);
		map.put(OWNER_ID, ownerId);
		map.put(AUTHORIZED_DRIVERS, authorizedDriverIds);
		return map;
	}
}
