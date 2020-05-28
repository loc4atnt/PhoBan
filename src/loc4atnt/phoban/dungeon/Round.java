package loc4atnt.phoban.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Round implements ConfigurationSerializable {

	private int restTime;
	private HashMap<String, Integer> mobMap;

	public Round() {
		mobMap = new HashMap<String, Integer>();
		restTime = 5;
	}

	public Round(Round round) {
		mobMap = round.mobMap;
		restTime = round.restTime;
	}

	public Round(int restTime, List<String> mobFormatList) {
		this.restTime = restTime;
		this.mobMap = new HashMap<String, Integer>();
		convertMobFormatListToMap(mobFormatList);
	}

	public HashMap<String, Integer> getMobMap() {
		return mobMap;
	}

	public int getRestTime() {
		return restTime;
	}

	public void setRestTime(int restTime) {
		this.restTime = restTime;
	}

	public List<String> getMobFormatList() {
		List<String> mobFormatList = new ArrayList<String>();
		for (String key : mobMap.keySet()) {
			int val = mobMap.get(key);
			String temp = key + ": " + String.valueOf(val);
			mobFormatList.add(temp);
		}
		return mobFormatList;
	}

	public void setMobFormatList(List<String> mobFormatList) {
		this.mobMap.clear();
		convertMobFormatListToMap(mobFormatList);
	}

	public static String getMobName(String mobFormat) {
		String temp = "";
		int index = getIndex(mobFormat);
		if (index != -1) {
			temp = mobFormat.substring(0, index);
		}
		return temp;
	}

	public static int getAmountMob(String mobFormat) {
		int amount = 0;
		int index = getIndex(mobFormat);
		if (index != -1) {
			String amountString = mobFormat.substring(index + 2);
			try {
				amount = Integer.parseInt(amountString);
			} catch (NumberFormatException e) {
				//
			}
		}
		return amount;
	}

	private void convertMobFormatListToMap(List<String> mobFormatList) {
		for (String mobFormat : mobFormatList) {
			int index = getIndex(mobFormat);
			if (index == -1)
				continue;
			int amount = 1;
			String mobName = mobFormat.substring(0, index);
			String amountString = mobFormat.substring(index + 2);
			try {
				amount = Integer.parseInt(amountString);
			} catch (NumberFormatException e) {
				//
			}
			mobMap.put(mobName, amount);
		}
	}

	private static int getIndex(String mobFormat) {
		for (int i = 0; i < mobFormat.length(); i++) {
			if (mobFormat.charAt(i) == ':')
				return i;
		}
		return -1;
	}

	////////////////

	@SuppressWarnings("unchecked")
	public static Round deserialize(Map<String, Object> map) {
		int restTime = (int) map.get("rest");
		List<String> mobFormatList = (List<String>) map.get("mob");
		return new Round(restTime, mobFormatList);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mob", getMobFormatList());
		map.put("rest", restTime);
		return map;
	}
}
