package loc4atnt.phoban.game.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.game.Game;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;
import loc4atnt.xlibs.item.skull.PlayerSkull;

public class KickMenu extends XMenu {

	public KickMenu(Player p, Game g) {
		super(p, "pbkick", "§cKick người chơi trong phòng", 1, 9, new KickMenuProvider(g));
	}
}

class KickMenuProvider implements InventoryProvider {

	private Game g;

	public KickMenuProvider(Game g) {
		this.g = g;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		List<Player> team = g.getTeamList();
		if (team.size() > 1) {
			for (int i = 1; i < team.size(); i++) {
				Player member = team.get(i);
				ItemStack skullItem = ItemX.toItemX(new PlayerSkull(member).toItem()).setName("§a" + member.getName())
						.toItemStack();
				ClickableItem click = ClickableItem.of(skullItem, e -> {
					PhoBan.getInst().getGameMnger().kick(member);
					new KickMenu(p, g).open();
				});
				cont.add(click);
			}
		}

		ItemStack closeItem = new ItemX(Material.BARRIER).setName("§cTrở lại").toItemStack();
		ClickableItem closeClick = ClickableItem.of(closeItem, c -> {
			new TeamManagerMenu(p, g).open();
		});
		cont.set(0, 8, closeClick);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}