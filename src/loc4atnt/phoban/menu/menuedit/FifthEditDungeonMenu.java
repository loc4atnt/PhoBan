package loc4atnt.phoban.menu.menuedit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.dungeon.reward.Reward;
import loc4atnt.phoban.dungeon.reward.RewardType;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotPos;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class FifthEditDungeonMenu extends XMenu {

	public FifthEditDungeonMenu(Player p, Reward r, String id, CoreDungeon dun) {
		super(p, "editdun_5", "§bPhó bản: " + id, 1, 9, new FifthEditDungeonMenuProvider(r, id, dun));
	}
}

class FifthEditDungeonMenuProvider implements InventoryProvider {

	private CoreDungeon dun;
	private Reward r;
	private String id;
	private EditDungeonMenuListener editListener = PhoBan.getInst().getEditListener();

	public FifthEditDungeonMenuProvider(Reward r, String id, CoreDungeon dun) {
		this.r = r;
		this.id = id;
		this.dun = dun;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		ItemStack fillItem = new ItemX(Material.LIGHT_BLUE_STAINED_GLASS_PANE).toItemStack();
		cont.fillRow(0, ClickableItem.empty(fillItem));

		ItemStack editTypeItem = new ItemX(Material.CHEST).setName("§aChỉnh sửa loại quà").toItemStack();
		ClickableItem editTypeClickable = ClickableItem.of(editTypeItem, e -> {
			cont.inventory().close(p);
			new SixthEditDungeonMenu(p, r, id, dun).open();
		});
		cont.set(SlotPos.of(0, 4), editTypeClickable);

		ItemStack editTeamSizeItem = new ItemX(Material.BROWN_SHULKER_BOX).setName("§aChỉnh sửa loại đội được nhận quà")
				.toItemStack();
		ClickableItem editItemSizeClickable = ClickableItem.of(editTeamSizeItem, e -> {
			cont.inventory().close(p);
			new SeventhEditDungeonMenu(p, r, id, dun).open();
		});
		cont.set(SlotPos.of(0, 3), editItemSizeClickable);

		ItemX editContItem = SecondEditDungeonMenu.getRewardItemX(r);
		editContItem.addLoreLine("§eClick để chỉnh sửa");
		ClickableItem editContClickable = ClickableItem.of(editContItem.toItemStack(), e -> {
			RewardType t = r.getType();
			if (t == null)
				return;
			if (t.equals(RewardType.I)) {
				cont.inventory().close(p);
				new EighthEditDungeonMenu(p, r, id, dun).open();
			} else if (t.equals(RewardType.FR)) {
				cont.inventory().close(p);
				enterFlipRewardName(p);
			} else if (t.equals(RewardType.C)) {
				p.sendMessage("§aNhập lệnh vào ô chat, Lưu ý: Không nhập dấu /");
				cont.inventory().close(p);
				editListener.putChatConsumer(p, c -> {
					r.setRewardCont(c.getMessage());
					new FifthEditDungeonMenu(p, r, id, dun).open();
				});
			} else {// P,M,E,RES
				cont.inventory().close(p);
				enterNumberForEachRewardType(p);
			}
		});
		cont.set(SlotPos.of(0, 5), editContClickable);

		ItemStack backItem = new ItemX(Material.OAK_SIGN).setName("§aLưu & Trở lại").toItemStack();
		ClickableItem backClickable = ClickableItem.of(backItem, c -> {
			cont.inventory().close(p);
			new SecondEditDungeonMenu(p, dun, id).open();
		});
		cont.set(SlotPos.of(0, 8), backClickable);
	}

	private void enterFlipRewardName(Player p) {
		editListener.putChatConsumer(p, c -> {
			String mess = c.getMessage();
			if (!mess.contains(" ")) {
				r.setRewardCont(c.getMessage());
				new FifthEditDungeonMenu(p, r, id, dun).open();
			} else {
				p.sendMessage("§cTên không được chứa dấu cách");
				enterFlipRewardName(p);
			}
		});
		p.sendMessage("§aNhập tên bảng mở quà ngẫu nhiên");
	}

	private void enterNumberForEachRewardType(Player p) {
		editListener.putChatConsumer(p, c -> {
			String mess = c.getMessage();
			try {
				int amount = Integer.parseInt(mess);
				if (amount >= 0) {
					r.setRewardCont(mess);
					new FifthEditDungeonMenu(p, r, id, dun).open();
				} else {
					p.sendMessage("§cNhập 1 số lớn hơn 0");
					enterNumberForEachRewardType(p);
				}
			} catch (NumberFormatException excep) {
				p.sendMessage("§c" + mess + " không phải là số!");
				enterNumberForEachRewardType(p);
			}
		});
		p.sendMessage("§aNhập số lượng vào ô chat rồi nhấn Enter");
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}