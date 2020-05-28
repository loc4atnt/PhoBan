package loc4atnt.phoban.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import loc4atnt.phoban.dungeon.reward.Reward;

public class CoreDungeon implements ConfigurationSerializable {

	private List<Round> roundList;
	private List<Reward> rewardList;
	private boolean isNeedingPerm;
	private String name;
	private List<DungeonRoom> room;

	public CoreDungeon(String name, boolean isNeedingPerm) {
		roundList = new ArrayList<Round>();
		rewardList = new ArrayList<Reward>();
		room = new ArrayList<DungeonRoom>();
		this.isNeedingPerm = isNeedingPerm;
		this.name = name;
	}

	public CoreDungeon(CoreDungeon dungeon) {
		this.roundList = dungeon.roundList;
		this.rewardList = dungeon.rewardList;
		this.isNeedingPerm = dungeon.isNeedingPerm;
		this.name = dungeon.name;
		this.room = dungeon.room;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNeedingPerm() {
		return isNeedingPerm;
	}

	public void setNeedingPerm(boolean isNeedingPerm) {
		this.isNeedingPerm = isNeedingPerm;
	}

	public boolean isReadyToPlay() {
		if (room != null)
			if (room.size() > 0)
				return true;
		return false;
	}

	public List<Round> getRoundList() {
		return roundList;
	}

	public void setRoundList(List<Round> roundList) {
		this.roundList = roundList;
	}

	public List<Reward> getRewardList() {
		return rewardList;
	}

	public void setRewardList(List<Reward> rewardList) {
		this.rewardList = rewardList;
	}

	public List<DungeonRoom> getRoom() {
		return room;
	}

	public void setRoom(List<DungeonRoom> room) {
		this.room = room;
	}

	////////////////

	private CoreDungeon(String name, List<DungeonRoom> room, List<Round> roundList, List<Reward> rewardList,
			boolean isNeedingPerm) {
		this.roundList = roundList;
		this.rewardList = rewardList;
		this.isNeedingPerm = isNeedingPerm;
		this.name = name;
		this.room = new ArrayList<DungeonRoom>(room);
	}

	@SuppressWarnings("unchecked")
	public static CoreDungeon deserialize(Map<String, Object> map) {
		List<Round> roundList = (List<Round>) map.get("play");
		List<Reward> rewardList = (List<Reward>) map.get("reward");
		boolean isNeedingPerm = (boolean) map.get("needing_perm");
		String name = (String) map.get("name");
		List<DungeonRoom> room = (List<DungeonRoom>) map.get("room");
		return new CoreDungeon(name, room, roundList, rewardList, isNeedingPerm);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("room", room);
		map.put("play", roundList);
		map.put("reward", rewardList);
		map.put("needing_perm", isNeedingPerm);
		map.put("name", name);
		return map;
	}
}
