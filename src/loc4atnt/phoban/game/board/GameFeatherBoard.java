package loc4atnt.phoban.game.board;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import loc4atnt.phoban.game.Game;

public class GameFeatherBoard extends GameBoard {

	public GameFeatherBoard(Game g) {
		super(g);
	}

	@Override
	public void removePlayer(Player p) {
		super.removePlayer(p);
		FeatherBoardAPI.toggle(p, true);
		FeatherBoardAPI.resetDefaultScoreboard(p);
	}

	@Override
	public void addPlayer(Player p) {
		FeatherBoardAPI.toggle(p, false);
		super.addPlayer(p);
	}

	@Override
	public List<OfflinePlayer> destroy() {
		List<OfflinePlayer> teamList = super.destroy();
		teamList.forEach(p -> {
			if (p instanceof Player) {
				FeatherBoardAPI.toggle((Player) p, true);
				FeatherBoardAPI.resetDefaultScoreboard((Player) p);
			}
		});
		return teamList;
	}
}
