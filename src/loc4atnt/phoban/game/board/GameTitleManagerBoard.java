package loc4atnt.phoban.game.board;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import loc4atnt.phoban.game.Game;

public class GameTitleManagerBoard extends GameBoard {

	private TitleManagerAPI plugin;

	public GameTitleManagerBoard(Game g) {
		super(g);
		plugin = (TitleManagerAPI) Bukkit.getServer().getPluginManager().getPlugin("TitleManager");
	}

	@Override
	public void removePlayer(Player p) {
		super.removePlayer(p);
		plugin.giveDefaultScoreboard(p);
	}

	@Override
	public void addPlayer(Player p) {
		plugin.removeScoreboard(p);
		super.addPlayer(p);
	}

	@Override
	public List<OfflinePlayer> destroy() {
		List<OfflinePlayer> teamList = super.destroy();
		teamList.forEach(p -> {
			if (p instanceof Player)
				plugin.giveDefaultScoreboard((Player) p);
		});
		return teamList;
	}
}
