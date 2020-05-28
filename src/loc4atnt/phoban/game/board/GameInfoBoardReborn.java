package loc4atnt.phoban.game.board;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.infogroup.infoboard.InfoBoardReborn;
import com.infogroup.infoboard.scoreboard.Create;

import loc4atnt.phoban.game.Game;
import loc4atnt.phoban.main.PhoBan;

public class GameInfoBoardReborn extends GameBoard {

	public GameInfoBoardReborn(Game g) {
		super(g);
	}

	@Override
	public void removePlayer(Player p) {
		super.removePlayer(p);
		Create.createScoreBoard(p);
	}

	@Override
	public void addPlayer(Player p) {
		removeInfoBoardReborn(p);
		super.addPlayer(p);
	}

	@Override
	public List<OfflinePlayer> destroy() {
		List<OfflinePlayer> teamList = super.destroy();

		teamList.forEach(p -> {
			if (p instanceof Player)
				Create.createScoreBoard((Player) p);
		});
		return teamList;
	}

	private void removeInfoBoardReborn(Player p) {
		InfoBoardReborn plugin = PhoBan.getInst().getInfoBoardPlugin();

		// Remove any old objective from the sidebar
		if (p.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
			p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
			p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		}

		// Remove and scrolling texts that the player may have had
		plugin.getSM().reset(p);

		// Remove all changeable texts that the player may have had
		if (plugin.getCHM().getChangeables(p) != null) {
			plugin.getCHM().reset(p);
		}

		// Remove all the conditions that the player may have had
		if (plugin.getCM().getCons(p) != null) {
			plugin.getCM().reset(p);
		}
	}
}
