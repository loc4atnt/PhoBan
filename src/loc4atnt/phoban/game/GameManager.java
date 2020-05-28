package loc4atnt.phoban.game;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import loc4atnt.phoban.dungeon.Dungeon;
import loc4atnt.phoban.main.PhoBan;

public class GameManager {

	private HashMap<Player, Game> gameMap;
	private HashMap<Player, Game> invitationMap;

	public GameManager() {
		gameMap = new HashMap<Player, Game>();
		invitationMap = new HashMap<Player, Game>();
	}

	public void removeGame(Player p) {
		gameMap.remove(p);
	}

	public void addGame(Player p, Game g) {
		gameMap.put(p, g);
	}

	public boolean isInGame(Player p) {
		return gameMap.containsKey(p);
	}

	public Game getGame(Player p) {
		return gameMap.get(p);
	}

	public void kick(Player p) {
		Game g = gameMap.get(p);
		g.kick(p);
		gameMap.remove(p);
		p.sendMessage("§cBạn đã bị kick khỏi phòng!");
	}

	public InviteStatus invite(Game g, Player p) {
		if (!gameMap.containsKey(p)) {
			if (!invitationMap.containsKey(p)) {
				invitationMap.put(p, g);
				p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1f, 1f);
				p.sendMessage("§aNgười chơi §e" + g.getLeader().getName() + "§a mời bạn tham gia phó bản "
						+ g.getDungeon().getCore().getName()
						+ "§a. Gõ lệnh /dongy để tham gia (Lời mời sẽ hết hạn sau 10 giây)");
				Bukkit.getScheduler().runTaskLater(PhoBan.getInst(), new Runnable() {

					@Override
					public void run() {
						invitationMap.remove(p);
					}
				}, 200);
				return InviteStatus.SENDED;
			}
			return InviteStatus.WAITING;
		}
		return InviteStatus.INROOM;
	}

	public enum InviteStatus {
		INROOM, SENDED, WAITING
	}

	public void acceptInvitation(Player p) {
		if (invitationMap.containsKey(p)) {
			Game g = invitationMap.get(p);
			if (g.getGameStatus() == 0) {
				g.addMember(p);
				gameMap.put(p, g);
				p.sendMessage("§eBạn đã tham gia đội đấu phó bản " + g.getDungeon().getCore().getName()
						+ "§e của đội trưởng " + g.getLeader().getName());
			} else {
				p.sendMessage("§aTrận đấu đã bắt đầu!");
			}
			invitationMap.remove(p);
		} else {
			p.sendMessage("§aBạn không có lời mời phó bản nào chờ chấp nhận!");
		}
	}

	public void quitGame(Player p) {
		if (gameMap.containsKey(p)) {
			Game g = gameMap.get(p);
			g.quitGame(p);
			gameMap.remove(p);
			p.sendMessage("§cĐã thoát trận!");
		}
	}

	public void startGame(Game g) {
		g.start();
	}

	public void prepairNewGame(Player owner, String dunId, Dungeon dun) {
		Game g = new Game(dunId, dun, owner);
		gameMap.put(owner, g);
	}

	public boolean isInTeam(Player player1, Player player2) {
		Game g = gameMap.get(player1);
		if (g == null)
			return false;
		return g.getTeamList().contains(player2);
	}
}
