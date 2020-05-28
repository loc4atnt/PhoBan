package loc4atnt.phoban.playturn;

import java.util.Calendar;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.config.SimpleConfig;
import loc4atnt.xlibs.permission.PermissionUtil;

public class PlayTurnManager {

	private PhoBan plugin;

	private SimpleConfig data;
	private int lastDay, lastMonth;
	private HashMap<String, Integer> turnMap;

	public PlayTurnManager() {
		plugin = PhoBan.getInst();

		turnMap = new HashMap<String, Integer>();

		data = plugin.getCfgMnger().getNewConfig("turns.yml");
		getDataFromFile();

		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {

			@Override
			public void run() {
				checkNewDate();
			}
		}, 0, 2400);
	}

	private void checkNewDate() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		if (day == lastDay && month == lastMonth)
			return;

		lastDay = day;
		lastMonth = month;
		turnMap.clear();

		data.set("last_day", day);
		data.set("last_month", month);
		data.removeKey("turns");
		data.saveConfig();
	}

	private void getDataFromFile() {
		lastDay = data.getInt("last_day", 0);
		lastMonth = data.getInt("last_month", 0);
		if (data.contains("turns"))
			for (String name : data.getConfigurationSection("turns").getKeys(false)) {
				turnMap.put(name, data.getInt("turns." + name));
			}
	}

	public int getTurnAmount(Player p) {
		return getTurnAmount(p.getName());
	}

	public int getTurnAmount(String name) {
		return turnMap.getOrDefault(name, 0);
	}

	public int getMaxTurns(Player p) {
		int permMaxTurn = PermissionUtil.getInst().getMaxIntFromPermission(p, PhoBan.MAX_TURN_PERMISSION);
		return permMaxTurn < 0 ? 0 : permMaxTurn;
	}

	public void addPlayedTurn(Player p) {
		int lastTurn = getTurnAmount(p);
		lastTurn++;
		setTurnAmount(p, lastTurn);
	}

	private void setTurnAmount(Player p, int lastTurn) {
		turnMap.put(p.getName(), lastTurn);
		data.set("turns." + p.getName(), lastTurn);
		data.saveConfig();
	}
}
