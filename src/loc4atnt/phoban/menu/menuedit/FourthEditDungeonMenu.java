package loc4atnt.phoban.menu.menuedit;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.dungeon.Round;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotPos;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class FourthEditDungeonMenu extends XMenu {

	public FourthEditDungeonMenu(Player p, Round round, String id, CoreDungeon dun) {
		super(p, "editdun_4", "§bPhó bản: " + id,
				((int) ((round.getMobFormatList().size() + 1) / 9))
						+ (((round.getMobFormatList().size() + 1) % 9 == 0) ? 1 : 2),
				9, new FourthEditDungeonMenuProvider(round, id, dun));
	}
}

class FourthEditDungeonMenuProvider implements InventoryProvider {

	private CoreDungeon dun;
	private Round r;
	private String id;

	public FourthEditDungeonMenuProvider(Round r, String id, CoreDungeon dun) {
		this.r = r;
		this.id = id;
		this.dun = dun;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		ItemStack editRestItem = new ItemX(Material.CLOCK, 1).setName("§aChỉnh thời gian nghỉ")
				.addLoreLine("§eThời gian nghỉ hiện tại: " + String.valueOf(r.getRestTime()) + " giây").toItemStack();
		ClickableItem editRestClickable = ClickableItem.of(editRestItem, e -> {
			enterRestTime(p);
			cont.inventory().close(p);
		});
		cont.add(editRestClickable);

		List<String> mobFormatList = r.getMobFormatList();
		for (int i = 0; i < mobFormatList.size(); i++) {
			int finalI = i;
			String mobFormat = mobFormatList.get(i);
			String mobName = Round.getMobName(mobFormat);
			int mobAmount = Round.getAmountMob(mobFormat);
			ItemStack item = new ItemX(Material.DRAGON_EGG, 1).setName("§a" + mobName)
					.addLoreLine("§eSố lượng: " + String.valueOf(mobAmount), "§cClick trái để chỉnh sửa số lượng",
							"§cClick phải để xóa")
					.toItemStack();
			ClickableItem clickable = ClickableItem.of(item, e -> {
				if (e.getClick().equals(ClickType.RIGHT)) {
					mobFormatList.remove(finalI);
					r.setMobFormatList(mobFormatList);
					cont.inventory().close(p);
					new FourthEditDungeonMenu(p, r, id, dun).open();
				} else if (e.getClick().equals(ClickType.LEFT)) {
					enterTheMobAmount(p, mobName, mobFormatList, finalI);
					cont.inventory().close(p);
				}
			});
			cont.add(clickable);
		}

		ItemStack fillItem = new ItemX(Material.LIGHT_BLUE_STAINED_GLASS).toItemStack();
		cont.fillRow(cont.inventory().getRows() - 1, ClickableItem.empty(fillItem));

		ItemStack addItem = new ItemX(Material.ANVIL, 1).setName("§aThêm quái!").toItemStack();
		ClickableItem addClickable = ClickableItem.of(addItem, e -> {
			if (mobFormatList.size() >= 44)
				return;
			PhoBan.getInst().getEditListener().putChatConsumer(p, c -> {
				String mess = c.getMessage();
				String newMobFormat = "";
				int index = mobFormatList.size();
				mobFormatList.add(newMobFormat);
				enterTheMobAmount(p, mess, mobFormatList, index);
			});
			cont.inventory().close(p);
			p.sendMessage("§aNhập tên mob vào ô chat rồi nhấn Enter!");
		});
		cont.set(SlotPos.of(cont.inventory().getRows() - 1, 4), addClickable);

		ItemStack backItem = new ItemX(Material.JUNGLE_SIGN).setName("§aTrở lại").toItemStack();
		ClickableItem backClickable = ClickableItem.of(backItem, c -> {
			cont.inventory().close(p);
			new FirstEditDungeonMenu(p, dun, id).open();
		});
		cont.set(SlotPos.of(cont.inventory().getRows() - 1, 8), backClickable);
	}

	private void enterRestTime(Player p) {
		PhoBan.getInst().getEditListener().putChatConsumer(p, c -> {
			String mess = c.getMessage();
			try {
				int time = Integer.parseInt(mess);
				r.setRestTime(time);
				new FourthEditDungeonMenu(p, r, id, dun).open();
			} catch (NumberFormatException excep) {
				p.sendMessage("§c" + mess + " không phải là số!");
				enterRestTime(p);
			}
		});
		p.sendMessage("§aNhập thời gian nghỉ (giây) vào ô chat rồi nhấn Enter!");
	}

	private void enterTheMobAmount(Player p, String mobName, List<String> mobFormatList, int index) {
		PhoBan.getInst().getEditListener().putChatConsumer(p, c -> {
			String mess = c.getMessage();
			try {
				int amount = Integer.parseInt(mess);
				String newFormat = mobName + ": " + String.valueOf(amount);
				mobFormatList.set(index, newFormat);
				r.setMobFormatList(mobFormatList);
				new FourthEditDungeonMenu(p, r, id, dun).open();
			} catch (NumberFormatException excep) {
				p.sendMessage("§c" + mess + " không phải là số!");
				enterTheMobAmount(p, mobName, mobFormatList, index);
			}
		});
		p.sendMessage("§aNhập số lượng mob " + mobName + " vào ô chat rồi nhấn Enter!");
	}

	@Override
	public void update(Player p, InventoryContents cont) {
		//
	}
}