package loc4atnt.phoban.menu.menuedit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.dungeon.reward.Reward;
import loc4atnt.phoban.dungeon.reward.RewardType;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class SixthEditDungeonMenu extends XMenu {

	public SixthEditDungeonMenu(Player p, Reward r, String id, CoreDungeon dun) {
		super(p, "editdun_6", "§bPhó bản: " + id, 1, 9, new SixthEditDungeonMenuProvider(r, id, dun));
	}
}

class SixthEditDungeonMenuProvider implements InventoryProvider {

	private CoreDungeon dun;
	private Reward r;
	private String id;

	public SixthEditDungeonMenuProvider(Reward r, String id, CoreDungeon dun) {
		this.r = r;
		this.id = id;
		this.dun = dun;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		for (RewardType type : RewardType.values()) {
			Material m = null;
			String name = null;
			if (type.equals(RewardType.I)) {
				m = Material.DIAMOND_SWORD;
				name = "§aVật phẩm";
			} else if (type.equals(RewardType.FR)) {
				m = Material.CHEST;
				name = "§aBảng chọn quà ngẫu nhiên";
			} else if (type.equals(RewardType.C)) {
				m = Material.DISPENSER;
				name = "§aChạy lệnh";
			} else if (type.equals(RewardType.P)) {
				m = Material.MAGMA_CREAM;
				name = "§aPoint";
			} else if (type.equals(RewardType.M)) {
				m = Material.PAPER;
				name = "§aTiền";
			} else if (type.equals(RewardType.E)) {
				m = Material.EXPERIENCE_BOTTLE;
				name = "§aKinh nghiệm";
			}
			ItemStack item = new ItemX(m).setName(name).toItemStack();
			ClickableItem clickable = ClickableItem.of(item, e -> {
				r.setType(type);
				cont.inventory().close(p);
				new FifthEditDungeonMenu(p, r, id, dun).open();
			});
			cont.add(clickable);
		}
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}