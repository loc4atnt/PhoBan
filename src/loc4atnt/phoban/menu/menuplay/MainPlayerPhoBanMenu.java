package loc4atnt.phoban.menu.menuplay;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class MainPlayerPhoBanMenu extends XMenu {

	public MainPlayerPhoBanMenu(Player p) {
		super(p, "playerpb_1", "§aMenu Phó Bản", 1, 9, new MainPlayerPhoBanMenuProvider());
	}
}

class MainPlayerPhoBanMenuProvider implements InventoryProvider {

	@Override
	public void init(Player p, InventoryContents cont) {
		ClickableItem fillClickable = ClickableItem.empty(new ItemX(Material.MAGENTA_STAINED_GLASS_PANE).toItemStack());
		cont.fill(fillClickable);

		ItemStack achieItem = new ItemX(Material.BOOK).setName("§eThành tích cá nhân").toItemStack();
		ClickableItem achieClick = ClickableItem.of(achieItem, e -> {
			new SecondPlayerPhoBanMenu(p).open();
		});
		cont.set(0, 3, achieClick);

		int playedTurn = PhoBan.getInst().getPlayTurnMnger().getTurnAmount(p);
		int maxTurn = PhoBan.getInst().getPlayTurnMnger().getMaxTurns(p);
		ItemX playItemX = new ItemX(Material.DIAMOND_SWORD).setName("§eTham chiến phó bản").addLoreLine("",
				"§aHôm nay bạn đã chơi§7: " + playedTurn + "/§c" + maxTurn + " §aphó bản");
		ClickableItem playClick;
		if (playedTurn >= maxTurn) {
			playItemX.addLoreLine("§eHãy quay lại vào ngày mai!");
			playClick = ClickableItem.empty(playItemX.toItemStack());
		} else {
			playClick = ClickableItem.of(playItemX.toItemStack(), e -> {
				new ThirdPlayerPhoBanMenu(p).open();
			});
		}
		cont.set(0, 5, playClick);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}