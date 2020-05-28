package loc4atnt.phoban.menu.menuedit;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.dungeon.reward.Reward;
import loc4atnt.phoban.dungeon.reward.RewardType;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotPos;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class SecondEditDungeonMenu extends XMenu {

	public SecondEditDungeonMenu(Player p, CoreDungeon dun, String id) {
		super(p, "editdun_2", "§bPhó bản: " + id,
				((int) (dun.getRewardList().size() / 9)) + ((dun.getRewardList().size() % 9 == 0) ? 1 : 2), 9,
				new SecondEditDungeonMenuProvider(dun, id));
	}

	public static ItemX getRewardItemX(Reward reward) {
		RewardType type = reward.getType();
		ItemX item = null;
		String rewardCont = reward.getRewardCont();
		if (type == null) {
			item = new ItemX(Material.BARRIER).setName("§cRỗng");
		} else if (type.equals(RewardType.I)) {
			if (reward.getItem() != null)
				item = new ItemX(reward.getItem());
			else
				item = new ItemX(Material.BARRIER).setName("§cRỗng");
		} else if (type.equals(RewardType.FR)) {
			item = new ItemX(Material.CHEST, 1).setName("§bMở bảng chọn quà ngẫu nhiên")
					.addLoreLine("§eTên bảng quà: " + reward.getRewardCont());
		} else if (type.equals(RewardType.C)) {
			item = new ItemX(Material.PAPER, 1).setName("§bChạy lệnh").addLoreLine("§e/" + rewardCont);
		} else if (type.equals(RewardType.P)) {
			item = new ItemX(Material.MAGMA_CREAM, 1).setName("§bĐưa point cho người chơi")
					.addLoreLine("§eSố point: " + rewardCont);
		} else if (type.equals(RewardType.M)) {
			item = new ItemX(Material.GOLD_INGOT, 1).setName("§bĐưa tiền cho người chơi")
					.addLoreLine("§eSố tiền: " + rewardCont);
		} else if (type.equals(RewardType.E)) {
			item = new ItemX(Material.EXPERIENCE_BOTTLE, 1).setName("§bĐưa exp cho người chơi")
					.addLoreLine("§eSố exp: " + rewardCont);
		}
		return item;
	}
}

class SecondEditDungeonMenuProvider implements InventoryProvider {

	private CoreDungeon dun;
	private String id;

	public SecondEditDungeonMenuProvider(CoreDungeon dun, String id) {
		this.dun = dun;
		this.id = id;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		List<Reward> rewardList = dun.getRewardList();
		for (int i = 0; i < rewardList.size(); i++) {
			Reward reward = rewardList.get(i);
			ItemX item = SecondEditDungeonMenu.getRewardItemX(reward);
			String sizeString = "";
			for (int j = 0; j < reward.getAppliedTeam().size(); j++) {
				int size = reward.getAppliedTeam().get(j);
				sizeString += String.valueOf(size);
				if (j < (reward.getAppliedTeam().size() - 1))
					sizeString += ", ";
			}
			item.addLoreLine("§e§lÁp dụng cho đội: §r§a" + sizeString + " người.", "§cClick trái để sửa quà này",
					"§cClick phải để xóa quà này");
			ClickableItem clickable = ClickableItem.of(item.toItemStack(), e -> {
				ClickType clickType = e.getClick();
				if (clickType.equals(ClickType.RIGHT)) {
					rewardList.remove(reward);
					cont.inventory().close(p);
					new SecondEditDungeonMenu(p, dun, id).open();
				} else if (clickType.equals(ClickType.LEFT)) {
					cont.inventory().close(p);
					new FifthEditDungeonMenu(p, reward, id, dun).open();
				}
			});
			cont.add(clickable);
		}

		ItemStack fillItem = new ItemX(Material.LIGHT_BLUE_STAINED_GLASS_PANE).toItemStack();
		cont.fillRow(cont.inventory().getRows() - 1, ClickableItem.empty(fillItem));

		ItemStack addRewardItem = new ItemX(Material.ANVIL, 1).setName("§aThêm quà").toItemStack();
		ClickableItem addRewardClickableItem = ClickableItem.of(addRewardItem, e -> {
			if (rewardList.size() >= 45)
				return;
			cont.inventory().close(p);
			Reward rw = new Reward();
			rewardList.add(rw);
			new FifthEditDungeonMenu(p, rw, id, dun).open();
		});
		cont.set(SlotPos.of(cont.inventory().getRows() - 1, 4), addRewardClickableItem);

		ItemStack backItem = new ItemX(Material.BIRCH_SIGN).setName("§aTrở lại").toItemStack();
		ClickableItem backClickable = ClickableItem.of(backItem, c -> {
			cont.inventory().close(p);
			new MainEditDungeonMenu(p, dun, id).open();
		});
		cont.set(SlotPos.of(cont.inventory().getRows() - 1, 8), backClickable);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}