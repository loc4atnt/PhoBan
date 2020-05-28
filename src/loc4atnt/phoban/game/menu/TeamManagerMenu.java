package loc4atnt.phoban.game.menu;

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

public class TeamManagerMenu extends XMenu {

	public TeamManagerMenu(Player p, Game g) {
		super(p, "pbrm", "§cQuản lý phòng phó bản", 1, 9, new TeamManagerMenuProvider(g));
	}
}

class TeamManagerMenuProvider implements InventoryProvider {

	private Game g;

	public TeamManagerMenuProvider(Game g) {
		this.g = g;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		ItemStack pbIconItem = new ItemX(Material.DIAMOND_SWORD).setName(g.getDungeon().getCore().getName())
				.addLoreLine("§eClick để bắt đầu trận đấu").toItemStack();
		ClickableItem pbIconClick = ClickableItem.of(pbIconItem, e -> {
			cont.inventory().close(p);
			PhoBan.getInst().getGameMnger().startGame(g);
		});
		cont.set(0, 3, pbIconClick);

		ItemStack inviteItem = new ItemX(Material.JUNGLE_SIGN).setName("§aMời người chơi vào phòng").toItemStack();
		ClickableItem inviteClick = ClickableItem.of(inviteItem, e -> {
			if (g.getTeamList().size() < 5) {
				new InviteMenu(p, g).open();
			} else {
				p.sendMessage("§cMỗi đội chỉ chứa tối đa 5 người!");
			}
		});
		cont.set(0, 4, inviteClick);

		ItemStack kickItem = new ItemX(Material.MINECART).setName("§aKick người chơi trong phòng").toItemStack();
		ClickableItem kickClick = ClickableItem.of(kickItem, e -> {
			new KickMenu(p, g).open();
		});
		cont.set(0, 5, kickClick);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}