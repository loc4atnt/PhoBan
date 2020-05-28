package loc4atnt.phoban.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import loc4atnt.phoban.main.PhoBan;
import loc4atnt.phoban.menu.menuedit.MainEditDungeonMenu;
import loc4atnt.xlibs.config.SimpleConfig;

public class DungeonManager {

	private SimpleConfig cfg;
	private HashMap<String, CoreDungeon> dungeonMap = new HashMap<String, CoreDungeon>();
	private HashMap<String, List<Integer>> playingRoomList = new HashMap<String, List<Integer>>();

	public DungeonManager() {
		ConfigurationSerialization.registerClass(DungeonRoom.class);
		ConfigurationSerialization.registerClass(Round.class);
		ConfigurationSerialization.registerClass(CoreDungeon.class);
		cfg = PhoBan.getInst().getCfgMnger().getNewConfig("/phoban.yml");
		getDungeonFromConfig();
	}

	private void getDungeonFromConfig() {
		dungeonMap.clear();
		for (String k : cfg.getConfigurationSection("").getKeys(false)) {
			CoreDungeon dungeon = (CoreDungeon) cfg.get(k);
			dungeonMap.put(k, dungeon);
		}
	}

	public void reload() {
		cfg.reloadConfig();
		getDungeonFromConfig();
	}

	public boolean create(String id, boolean isNeedingPerm) {
		boolean temp = dungeonMap.containsKey(id);
		if (temp)
			return false;
		CoreDungeon dungeon = new CoreDungeon("", isNeedingPerm);
		putDungeon(id, dungeon);
		return true;
	}

	public boolean create(String id) {
		return create(id, false);
	}

	public boolean delete(String id) {
		return removeDungeon(id);
	}

	public boolean edit(Player p, String id) {
		CoreDungeon dun = getCoreDungeon(id);
		if (dun == null)
			return false;
		new MainEditDungeonMenu(p, dun, id).open();
		return true;
	}

	public CoreDungeon getCoreDungeon(String id) {
		return dungeonMap.get(id);
	}

	public Dungeon getNewDungeonRoom(String id) {
		CoreDungeon coreDun = dungeonMap.get(id);
		if (coreDun != null) {
			List<Integer> playingRoomIdList = playingRoomList.getOrDefault(id, new ArrayList<Integer>());
			int dunRoomAmount = coreDun.getRoom().size();
			if (playingRoomIdList.size() >= dunRoomAmount)
				return null;
			for (int i = 0; i < dunRoomAmount; i++) {
				DungeonRoom room = coreDun.getRoom().get(i);
				if (playingRoomIdList.contains(room.getId()))
					continue;
				playingRoomIdList.add(room.getId());
				this.playingRoomList.put(id, playingRoomIdList);
				return new Dungeon(coreDun, room);
			}
		}
		return null;

	}

	public boolean isHasEmptyDungeonRoom(String id) {
		CoreDungeon coreDun = dungeonMap.get(id);
		if (coreDun != null) {
			List<Integer> playingRoomIdList = playingRoomList.getOrDefault(id, null);
			if (playingRoomIdList == null)
				return true;
			int dunRoomAmount = coreDun.getRoom().size();
			return (playingRoomIdList.size() < dunRoomAmount);
		}
		return false;
	}

	public void putDungeon(String id, CoreDungeon dungeon) {
		dungeonMap.put(id, dungeon);
		if (!this.playingRoomList.containsKey(id))
			this.playingRoomList.put(id, new ArrayList<Integer>());
		cfg.set(id, dungeon);
		cfg.saveConfig();
	}

	private boolean removeDungeon(String id) {
		boolean temp = dungeonMap.containsKey(id);
		if (!temp)
			return false;
		dungeonMap.remove(id);
		cfg.removeKey(id);
		cfg.saveConfig();
		return true;
	}

	public void resetRoom(String id, Dungeon dun) {
		List<Integer> playingRoomIdList = this.playingRoomList.getOrDefault(id, null);
		if (playingRoomIdList == null)
			return;
		playingRoomIdList.remove(((Integer) dun.getRoomId()));
		this.playingRoomList.put(id, playingRoomIdList);
	}
}
