package loc4atnt.phoban.game.board;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import loc4atnt.phoban.game.Game;
import loc4atnt.xlibs.spigotplayerinfor.XTeamInfo;

public abstract class GameBoard {

	protected Game g;
	protected XTeamInfo xteam;

	public GameBoard(Game g) {
		this.g = g;
		this.xteam = new XTeamInfo(g.getLeader());
	}

	protected String getPlayerStatStatus(int i) {
		long dmgPercent = Math.round(g.getAtkDmgAr()[i] / g.getAtkDmgSum() * 100);
		String s = "§b%.St: §f" + String.valueOf(dmgPercent) + " §bG: §f" + String.valueOf(g.getKillAr()[i])
				+ " §bC: §f" + String.valueOf(g.getDeadAr()[i]);
		return s;
	}

	protected String getPlayerStatus(int i) {
		String s = "§f (";
		if (g.getGameStatus() == ((byte) 0))
			return "";
		if (g.getPlayerStatus()[i] == ((byte) 0)) {
			s += "Hồi";
		} else if (g.getPlayerStatus()[i] == ((byte) 1)) {
			s += "Sống";
		} else if (g.getPlayerStatus()[i] == ((byte) 2)) {
			s += "Thoát";
		} else {
			s += "Chết";
		}
		s += ")";
		return s;
	}

	public void updateBoard() {
		if (g.getTeamList().size() == 0) {
			destroy();
			return;
		}

		List<String> text = new ArrayList<String>();
		text.add("§8---------------");

		int minutes = (int) (g.getRemainTime() / 60);
		int seconds = g.getRemainTime() % 60;
		String remainTimeString = "§bThời gian còn lại§7:§a " + minutes + ":" + seconds;
		text.add(remainTimeString);

		text.add("");
		text.add("§e" + g.getTeamList().get(0).getName() + getPlayerStatus(0));
		if (g.getGameStatus() == ((byte) 1))
			text.add(getPlayerStatStatus(0));
		for (int i = 1; i < g.getTeamList().size(); i++) {
			text.add("§a" + g.getTeamList().get(i).getName() + getPlayerStatus(i));
			if (g.getGameStatus() == ((byte) 1))
				text.add(getPlayerStatStatus(i));
		}
		text.add("§8---------------");

		xteam.updateBoard(g.getDungeon().getCore().getName(), text);
	}

	public void removePlayer(Player p) {
		xteam.removeMember(p);
		updateBoard();
	}

	public void addPlayer(Player p) {
		xteam.addMember(p);
		updateBoard();
	}

	public List<OfflinePlayer> destroy() {
		return xteam.destroyTeam();
	}
}
