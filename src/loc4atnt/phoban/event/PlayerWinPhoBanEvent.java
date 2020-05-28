package loc4atnt.phoban.event;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import loc4atnt.phoban.data.match.MatchResult;

public class PlayerWinPhoBanEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private HashMap<Player, MatchResult> winner;
	private String dungeonId;

	public PlayerWinPhoBanEvent(HashMap<Player, MatchResult> winner, String dungeonId) {
		this.winner = winner;
		this.dungeonId = dungeonId;
	}

	public HashMap<Player, MatchResult> getWinner() {
		return winner;
	}

	public String getDungeonId() {
		return dungeonId;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
