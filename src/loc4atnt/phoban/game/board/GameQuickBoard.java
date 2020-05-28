package loc4atnt.phoban.game.board;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import loc4atnt.phoban.game.Game;
import me.tade.quickboard.api.QuickBoardAPI;

public class GameQuickBoard extends GameBoard {

	public GameQuickBoard(Game g) {
		super(g);
	}

	@Override
	public void removePlayer(Player p) {
		super.removePlayer(p);
		QuickBoardAPI.createBoard(p, "scoreboard.default");
	}

	@Override
	public void addPlayer(Player p) {
		QuickBoardAPI.removeBoard(p);
		super.addPlayer(p);
	}

	@Override
	public List<OfflinePlayer> destroy() {
		List<OfflinePlayer> teamList = super.destroy();
		teamList.forEach(p -> {
			if (p instanceof Player)
				QuickBoardAPI.createBoard((Player) p, "scoreboard.default");
		});
		return teamList;
	}
}
