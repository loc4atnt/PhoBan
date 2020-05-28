package loc4atnt.phoban.dungeon;

import java.util.List;

import org.bukkit.Location;

public class Dungeon {

	private CoreDungeon coreDun;
	private DungeonRoom room;

	public Dungeon(CoreDungeon coreDun, DungeonRoom room) {
		this.coreDun = coreDun;
		this.room = room;
	}

	public CoreDungeon getCore() {
		return coreDun;
	}

	public Location getSpawnLoca() {
		return room.getPlayerSpawnLoca();
	}

	public List<Location> getLocaList() {
		return room.getMobSpawnLoca();
	}

	public int getRoomId() {
		return room.getId();
	}
}
