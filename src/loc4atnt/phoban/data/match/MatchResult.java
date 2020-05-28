package loc4atnt.phoban.data.match;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class MatchResult implements ConfigurationSerializable {
	
	private boolean isWin;
	private float damagedPercent;
	private int deathAmount;
	private int killingAmount;
	private boolean isMVP;
	private String id;

	public String getId() {
		return id;
	}

	public boolean isWin() {
		return isWin;
	}

	public float getDamagedPercent() {
		return damagedPercent;
	}

	public int getDeathAmount() {
		return deathAmount;
	}

	public int getKillingAmount() {
		return killingAmount;
	}

	public boolean isMVP() {
		return isMVP;
	}
	
	public MatchResult(boolean isWin, float damagedPercent, int deathAmount, int killingAmount, boolean isMVP, String id) {
		this.isWin = isWin;
		this.damagedPercent = damagedPercent;
		this.deathAmount = deathAmount;
		this.killingAmount = killingAmount;
		this.isMVP = isMVP;
		this.id = id;
	}
	
	public static MatchResult deserialize(Map<String, Object> map) {
		boolean isWin = (boolean) map.get("win");
		float damagedPercent = (float) ((double) map.get("dmg_percent"));
		int deathAmount = (int) map.get("death");
		int killingAmount = (int) map.get("kill");
		boolean isMVP = (boolean) map.get("mvp");
		String id = (String) map.get("id");
		return new MatchResult(isWin, damagedPercent, deathAmount, killingAmount, isMVP, id);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("win", isWin);
		map.put("dmg_percent", damagedPercent);
		map.put("death", deathAmount);
		map.put("kill", killingAmount);
		map.put("mvp", isMVP);
		map.put("id", id);
		return map;
	}
}
