package loc4atnt.phoban.data.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import loc4atnt.phoban.data.match.MatchResult;

public class PlayerData implements ConfigurationSerializable {

	private float dmgPercentAvr;
	private float winPercent;
	private int killAmount;
	private int deathAmount;
	private int winAmount;
	private int loseAmount;
	private String lastMatch;
	private HashMap<String, MatchResult> matchMap;

	public PlayerData() {
		dmgPercentAvr = 0;
		winPercent = 0;
		killAmount = 0;
		deathAmount = 0;
		winAmount = 0;
		loseAmount = 0;
		lastMatch = "";
		matchMap = new HashMap<String, MatchResult>();
	}

	public float getDmgPercentAvr() {
		return dmgPercentAvr;
	}

	public float getWinPercent() {
		return winPercent;
	}

	public int getKillAmount() {
		return killAmount;
	}

	public int getDeathAmount() {
		return deathAmount;
	}

	public int getWinAmount() {
		return winAmount;
	}

	public int getLoseAmount() {
		return loseAmount;
	}

	public MatchResult getLastMatch() {
		return matchMap.get(lastMatch);
	}

	public MatchResult getMatchResult(String id) {
		return matchMap.get(id);
	}

	public void addMatchResult(MatchResult r) {
		String id = r.getId();
		matchMap.put(id, r);
		lastMatch = id;
		if (r.isWin())
			winAmount++;
		else
			loseAmount++;
		deathAmount += r.getDeathAmount();
		killAmount += r.getKillingAmount();
		winPercent = ((float) winAmount) / ((float) (winAmount + loseAmount)) * 100f;
		int nowSize = matchMap.size();
		int lastSize = nowSize - 1;
		dmgPercentAvr = (dmgPercentAvr * ((float) lastSize) + r.getDamagedPercent()) / ((float) nowSize);
	}

	@SuppressWarnings("unchecked")
	private PlayerData(Map<String, Object> map) {
		this.matchMap = new HashMap<String, MatchResult>();
		this.dmgPercentAvr = (float) ((double) map.get("dmgavr"));
		this.winPercent = (float) ((double) map.get("winrate"));
		this.killAmount = (int) map.get("kill");
		this.deathAmount = (int) map.get("death");
		this.winAmount = (int) map.get("win");
		this.loseAmount = (int) map.get("lose");
		this.lastMatch = (String) map.get("lastmatch");
		List<MatchResult> matchList = (List<MatchResult>) map.get("match");
		for (MatchResult r : matchList) {
			matchMap.put(r.getId(), r);
		}
	}

	public static PlayerData deserialize(Map<String, Object> map) {
		return new PlayerData(map);
	}

	@Override
	public Map<String, Object> serialize() {
		List<MatchResult> matchList = new ArrayList<MatchResult>(matchMap.values());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dmgavr", dmgPercentAvr);
		map.put("winrate", winPercent);
		map.put("kill", killAmount);
		map.put("death", deathAmount);
		map.put("win", winAmount);
		map.put("lose", loseAmount);
		map.put("lastmatch", lastMatch);
		map.put("match", matchList);
		return map;
	}
}
