package loc4atnt.phoban.menu.menuplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.config.SimpleConfig;

public class MenuPlayManager {

	private SimpleConfig cfg;
	private List<String> phoBanIdList;
	private HashMap<String, DungeonMenuGroup> dunGroupMap = new HashMap<String, DungeonMenuGroup>();
	private String name;

	public MenuPlayManager() {
		cfg = PhoBan.getInst().getCfgMnger().getNewConfig("/menu.yml");
		if (!cfg.contains("ten_menu")) {
			cfg.set("ten_menu", "&aMenu Pho Ban");
			cfg.saveConfig();
		}
		getFromConfig();
	}

	public List<String> getPhoBanIdList() {
		return phoBanIdList;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	private void getFromConfig() {
		name = cfg.getString("ten_menu", "&aMenu Pho Ban");
		if (cfg.contains("thu_tu_pho_ban"))
			phoBanIdList = (List<String>) cfg.getList("thu_tu_pho_ban");
		else {
			phoBanIdList = new ArrayList<String>();
			phoBanIdList.add("test_1");
			cfg.set("thu_tu_pho_ban", phoBanIdList);
			cfg.saveConfig();
		}
		if (cfg.contains("dun_group")) {
			dunGroupMap.clear();
			for (String id : cfg.getConfigurationSection("dun_group").getKeys(false)) {
				String name = cfg.getString("dun_group." + id + ".name", "&cNULL");
				List<String> idList = (List<String>) cfg.getList("dun_group." + id + ".duns", new ArrayList<String>());
				DungeonMenuGroup menuGroup = new DungeonMenuGroup(name, idList);
				dunGroupMap.put(id, menuGroup);
			}
		}
	}

	public void reload() {
		cfg.reloadConfig();
		getFromConfig();
	}

	public DungeonMenuGroup getDungeonMenuGroup(String groupName) {
		return dunGroupMap.get(groupName);
	}
}
