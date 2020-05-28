package loc4atnt.phoban.menu.menuedit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotPos;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;
import loc4atnt.xlibs.stringutil.Color;

public class MainEditDungeonMenu extends XMenu {

	public MainEditDungeonMenu(Player p, CoreDungeon dun, String id) {
		super(p, "editdun_0", "§bPhó bản: " + id, 1, 9, new EditDungeonMenuProvider(dun, id));
	}
}

class EditDungeonMenuProvider implements InventoryProvider {

	private CoreDungeon dun;
	private String id;

	public EditDungeonMenuProvider(CoreDungeon dun, String id) {
		this.dun = dun;
		this.id = id;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		ItemStack changeNameItem = new ItemX(Material.NAME_TAG, 1).setName("§aChỉnh tên phó bản")
				.addLoreLine("§eTên: " + dun.getName()).toItemStack();
		ClickableItem changeNameClickableItem = ClickableItem.of(changeNameItem, e -> {
			PhoBan.getInst().getEditListener().putChatConsumer(p, c -> {
				String name = c.getMessage();
				dun.setName(Color.convert(name));
				PhoBan.getInst().getDungeonMnger().edit(p, id);
			});
			cont.inventory().close(p);
			p.sendMessage("§aNhập tên phó bản vào khung chat rồi nhấn Enter.");
		});
		cont.set(SlotPos.of(0, 0), changeNameClickableItem);

		ItemStack editRoomItem = new ItemX(Material.IRON_DOOR).setName("§aChỉnh sửa các phòng chơi").toItemStack();
		ClickableItem editRoomClick = ClickableItem.of(editRoomItem, e -> {
			new NinethEditDungeonMenu(p, dun, id).open();
		});
		cont.set(SlotPos.of(0, 1), editRoomClick);

		ItemStack editRoundsItem = new ItemX(Material.ZOMBIE_SPAWN_EGG, 1).setName("§aChỉnh các đợt quái")
				.toItemStack();
		ClickableItem editRoundsClickableItem = ClickableItem.of(editRoundsItem, e -> {
			cont.inventory().close(p);
			new FirstEditDungeonMenu(p, dun, id).open();
		});
		cont.set(SlotPos.of(0, 2), editRoundsClickableItem);

		ItemStack rewardEditItem = new ItemX(Material.CHEST, 1).setName("§aChỉnh sửa phần thưởng").toItemStack();
		ClickableItem rewardEditClickable = ClickableItem.of(rewardEditItem, e -> {
			cont.inventory().close(p);
			new SecondEditDungeonMenu(p, dun, id).open();
		});
		cont.set(SlotPos.of(0, 3), rewardEditClickable);

		ClickableItem doneClickable = ClickableItem
				.of(new ItemX(Material.CRAFTING_TABLE).setName("§aLưu").toItemStack(), e -> {
					cont.inventory().close(p);
					PhoBan.getInst().getDungeonMnger().putDungeon(id, dun);
				});
		cont.set(0, 8, doneClickable);
	}

	@Override
	public void update(Player p, InventoryContents cont) {
		//
	}

}