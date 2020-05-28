package loc4atnt.phoban.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class DungeonRoom implements ConfigurationSerializable {

	private Location playerSpawnLoca;
	private List<Location> mobSpawnLoca;
	private int id;

	public DungeonRoom(int id, Location playerSpawnLoca, List<Location> mobSpawnLoca) {
		this.playerSpawnLoca = playerSpawnLoca;
		this.mobSpawnLoca = (mobSpawnLoca != null) ? mobSpawnLoca : new ArrayList<Location>();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Location getPlayerSpawnLoca() {
		return playerSpawnLoca;
	}

	public void setPlayerSpawnLoca(Location playerSpawnLoca) {
		this.playerSpawnLoca = playerSpawnLoca;
	}

	public List<Location> getMobSpawnLoca() {
		return mobSpawnLoca;
	}

	public void setMobSpawnLoca(List<Location> mobSpawnLoca) {
		this.mobSpawnLoca = mobSpawnLoca;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("player_loca", playerSpawnLoca);
		map.put("mob_loca", mobSpawnLoca);
		map.put("id", id);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static DungeonRoom deserialize(Map<String, Object> map) {
		Location playerLoca = (Location) map.get("player_loca");
		List<Location> mobLoca = (List<Location>) map.get("mob_loca");
		int id = (int) map.get("id");
		return new DungeonRoom(id, playerLoca, mobLoca);
	}
}
