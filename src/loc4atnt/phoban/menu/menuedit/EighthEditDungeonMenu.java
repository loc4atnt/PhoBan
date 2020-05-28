package loc4atnt.phoban.menu.menuedit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.dungeon.reward.Reward;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class EighthEditDungeonMenu extends XMenu {

	public EighthEditDungeonMenu(Player p, Reward r, String id, CoreDungeon dun) {
		super(p, "editdun_8", "§bPhó bản: " + id, 3, 9, new EighthEditDungeonMenuProvider(r, id, dun));
	}

}

class EighthEditDungeonMenuProvider implements InventoryProvider {

	private Reward r;
	private String id;
	private CoreDungeon dun;

	public EighthEditDungeonMenuProvider(Reward r, String id, CoreDungeon dun) {
		this.r = r;
		this.id = id;
		this.dun = dun;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		ItemStack fillItem = new ItemX(Material.BLUE_STAINED_GLASS_PANE).toItemStack();
		ClickableItem fillClickable = ClickableItem.empty(fillItem);
		cont.fill(fillClickable);

		ClickableItem putClickable = XMenu.getInteractClickableItem(cont, r.getItem(), 1, 4);
		cont.set(1, 4, putClickable);

		ClickableItem doneClickable = ClickableItem
				.of(new ItemX(Material.CRAFTING_TABLE).setName("§aLưu").toItemStack(), e -> {
					ItemStack item = e.getInventory().getItem(13);
					r.setItem(item);
					cont.inventory().close(p);
					new FifthEditDungeonMenu(p, r, id, dun).open();
				});
		cont.set(2, 4, doneClickable);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}