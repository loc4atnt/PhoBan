package loc4atnt.phoban.menu.menuplay;

import java.util.List;

public class DungeonMenuGroup {

	private String displayName;
	private List<String> idList;

	public DungeonMenuGroup(String displayName, List<String> idList) {
		this.displayName = displayName;
		this.idList = idList;
	}

	public String getDisplayName() {
		return displayName;
	}

	public List<String> getIdList() {
		return idList;
	}
}
