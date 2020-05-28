package loc4atnt.phoban.data.player;

import java.util.HashMap;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import loc4atnt.phoban.data.match.MatchResult;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.config.SimpleConfig;

public class PlayerDataManager implements Listener {

	private SimpleConfig file;
	private HashMap<Player, PlayerData> playerDataMap;

	public PlayerDataManager() {
		ConfigurationSerialization.registerClass(MatchResult.class);
		ConfigurationSerialization.registerClass(PlayerData.class);
		file = PhoBan.getInst().getCfgMnger().getNewConfig("/data/player.yml");
		playerDataMap = new HashMap<Player, PlayerData>();
	}

	public PlayerData getPlayerData(Player p) {
		return playerDataMap.get(p);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PlayerData data = (PlayerData) file.get(p.getName(), new PlayerData());
		playerDataMap.put(p, data);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		PlayerData data = playerDataMap.get(p);
		if (data != null) {
			file.set(p.getName(), data);
			file.saveConfig();
			playerDataMap.remove(p);
		}
	}

	public void savePlayerData(Player p) {
		PlayerData data = playerDataMap.get(p);
		if (data != null) {
			file.set(p.getName(), data);
			file.saveConfig();
		}
	}
}
