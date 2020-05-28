package loc4atnt.phoban.menu.menuedit;

import java.util.HashMap;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import loc4atnt.phoban.main.PhoBan;

public class EditDungeonMenuListener implements Listener {

	private HashMap<Player, Consumer<AsyncPlayerChatEvent>> chatEventMap = new HashMap<Player, Consumer<AsyncPlayerChatEvent>>();
	private HashMap<Player, Consumer<PlayerInteractEvent>> interactEventMap = new HashMap<Player, Consumer<PlayerInteractEvent>>();

	public void putChatConsumer(Player p, Consumer<AsyncPlayerChatEvent> c) {
		chatEventMap.put(p, c);
	}

	public void putInteractConsumer(Player p, Consumer<PlayerInteractEvent> c) {
		interactEventMap.put(p, c);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Bukkit.getScheduler().runTask(PhoBan.getInst(), new Runnable() {

			@Override
			public void run() {
				Player p = e.getPlayer();
				if (chatEventMap.containsKey(p)) {
					e.setCancelled(true);
					Consumer<AsyncPlayerChatEvent> c = chatEventMap.get(p);
					chatEventMap.remove(p);
					c.accept(e);
				}
			}
		});
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (interactEventMap.containsKey(p) && e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			e.setCancelled(true);
			Consumer<PlayerInteractEvent> c = interactEventMap.get(p);
			interactEventMap.remove(p);
			c.accept(e);
		}
	}

	public static Location getInteractLocation(PlayerInteractEvent c) {
		Location blockLoca = c.getClickedBlock().getLocation();
		return new Location(blockLoca.getWorld(), blockLoca.getX() + 0.5d, blockLoca.getY() + 1d,
				blockLoca.getZ() + 0.5d, c.getPlayer().getEyeLocation().getYaw(), 0f);
	}
}
