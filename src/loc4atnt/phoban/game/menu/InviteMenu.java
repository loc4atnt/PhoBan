package loc4atnt.phoban.game.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.game.Game;
import loc4atnt.phoban.game.GameManager.InviteStatus;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.Pagination;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotIterator;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;
import loc4atnt.xlibs.item.skull.PlayerSkull;

public class InviteMenu extends XMenu {

	public InviteMenu(Player p, Game g) {
		super(p, "pbinvite", "§cMời người chơi vào phòng", 4, 9, new InviteMenuProvider(g));
	}
}

class InviteMenuProvider implements InventoryProvider {

	private Game g;

	public InviteMenuProvider(Game g) {
		this.g = g;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		ClickableItem fillClick = ClickableItem.empty(new ItemX(Material.MAGENTA_STAINED_GLASS_PANE).toItemStack());
		cont.fillRow(3, fillClick);

		Pagination page = cont.pagination();

		List<Player> playerList = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		playerList.removeAll(g.getTeamList());
		ClickableItem[] playerClickArray = new ClickableItem[playerList.size()];

		for (int i = 0; i < playerList.size(); i++) {
			Player player = playerList.get(i);
			ItemStack item = ItemX.toItemX(new PlayerSkull(player).toItem()).setName("§a" + player.getName())
					.toItemStack();
			ClickableItem click = ClickableItem.of(item, e -> {
				InviteStatus inviteStatus = PhoBan.getInst().getGameMnger().invite(g, player);
				if (inviteStatus.equals(InviteStatus.SENDED)) {
					p.sendMessage("§aĐã gửi lời mời cho " + player.getName());
				} else if (inviteStatus.equals(InviteStatus.WAITING)) {
					p.sendMessage("§eHãy chờ họ đồng ý!");
				} else {
					p.sendMessage("§c" + player.getName() + " đang trong trận!");
				}
			});
			playerClickArray[i] = click;
		}

		page.setItems(playerClickArray);
		page.setItemsPerPage(27);
		page.addToIterator(cont.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

		if (!page.isFirst()) {
			ItemStack previousPage = new ItemX(Material.ARROW).setName("§aTrang trước").toItemStack();
			ClickableItem previousClick = ClickableItem.of(previousPage, e -> {
				cont.inventory().open(p, page.previous().getPage());
			});
			cont.set(3, 2, previousClick);
		}

		if (!page.isLast()) {
			ItemStack nextPage = new ItemX(Material.ARROW).setName("§aTrang sau").toItemStack();
			ClickableItem nextClick = ClickableItem.of(nextPage, e -> {
				cont.inventory().open(p, page.next().getPage());
			});
			cont.set(3, 6, nextClick);
		}

		ItemStack closeItem = new ItemX(Material.BARRIER).setName("§cTrở lại").toItemStack();
		ClickableItem closeClick = ClickableItem.of(closeItem, c -> {
			new TeamManagerMenu(p, g).open();
		});
		cont.set(3, 4, closeClick);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}