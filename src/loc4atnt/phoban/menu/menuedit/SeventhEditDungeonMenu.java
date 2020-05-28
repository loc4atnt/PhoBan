package loc4atnt.phoban.menu.menuedit;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.dungeon.reward.Reward;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotPos;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class SeventhEditDungeonMenu extends XMenu {

	public SeventhEditDungeonMenu(Player p, Reward r, String id, CoreDungeon dun) {
		super(p, "editdun_7", "§bPhó bản: " + id, 1, 9, new SeventhEditDungeonMenuProvider(r, id, dun));
	}
}

class SeventhEditDungeonMenuProvider implements InventoryProvider {

	private CoreDungeon dun;
	private Reward r;
	private String id;

	public SeventhEditDungeonMenuProvider(Reward r, String id, CoreDungeon dun) {
		this.r = r;
		this.id = id;
		this.dun = dun;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		List<Integer> appliedTeam = r.getAppliedTeam();
		for (int i = 1; i <= 5; i++) {
			int finalI = i;
			boolean isRecv = appliedTeam.contains(i);
			ItemStack item = new ItemX(Material.SKELETON_SKULL, i).setName("§aĐội " + String.valueOf(i) + " người")
					.addLoreLine("§eTrạng thái: " + (isRecv ? "§bNhận" : "§cKhông nhận"),
							"§eClick để thay đổi trạng thái")
					.toItemStack();
			ClickableItem clickable = ClickableItem.of(item, e -> {
				if (isRecv)
					appliedTeam.remove((Integer) finalI);
				else
					appliedTeam.add(finalI);
				ItemStack newItem = new ItemX(Material.SKELETON_SKULL, finalI)
						.setName("§aĐội " + String.valueOf(finalI) + " người")
						.addLoreLine("§eTrạng thái: " + (!isRecv ? "§bNhận" : "§cKhông nhận"),
								"§eClick để thay đổi trạng thái")
						.toItemStack();
				cont.editItem(0, finalI - 1, newItem);
			});
			cont.add(clickable);
		}

		ItemStack backItem = new ItemX(Material.BIRCH_SIGN).setName("§aTrở lại").toItemStack();
		ClickableItem backClickable = ClickableItem.of(backItem, c -> {
			cont.inventory().close(p);
			new FifthEditDungeonMenu(p, r, id, dun).open();
		});
		cont.set(SlotPos.of(0, 8), backClickable);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}