package loc4atnt.phoban.menu.menuedit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.dungeon.Round;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotPos;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class FirstEditDungeonMenu extends XMenu {

	public FirstEditDungeonMenu(Player p, CoreDungeon dun, String id) {
		super(p, "editdun_1", "§bPhó bản: " + id,
				((int) (dun.getRoundList().size() / 9)) + ((dun.getRoundList().size() % 9 == 0) ? 1 : 2), 9,
				new FirstEditDungeonMenuProvider(dun, id));
	}

}

class FirstEditDungeonMenuProvider implements InventoryProvider {

	private CoreDungeon dun;
	private String id;

	public FirstEditDungeonMenuProvider(CoreDungeon dun, String id) {
		this.dun = dun;
		this.id = id;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		List<Round> roundList = dun.getRoundList();
		for (int i = 0; i < roundList.size(); i++) {
			int finalI = i;
			Round round = roundList.get(i);
			List<String> lore = new ArrayList<String>();
			for (String s : round.getMobFormatList()) {
				lore.add("§e" + s);
			}
			lore.add("§cClick trái để chỉnh sửa");
			lore.add("§cClick phải để xóa");
			int roundIndex = i + 1;
			ItemStack item = new ItemX(Material.ZOMBIE_SPAWN_EGG, 1).setName("§aĐợt " + String.valueOf(roundIndex))
					.setLore(lore).toItemStack();
			ClickableItem clickableItem = ClickableItem.of(item, e -> {
				if (e.getClick().equals(ClickType.RIGHT)) {
					roundList.remove(finalI);
					cont.inventory().close(p);
					new FirstEditDungeonMenu(p, dun, id).open();
				} else if (e.getClick().equals(ClickType.LEFT)) {
					cont.inventory().close(p);
					new FourthEditDungeonMenu(p, round, id, dun).open();
				}
			});
			cont.add(clickableItem);
		}

		ItemStack fillItem = new ItemX(Material.LIGHT_BLUE_STAINED_GLASS_PANE).toItemStack();
		cont.fillRow(cont.inventory().getRows() - 1, ClickableItem.empty(fillItem));

		ItemStack addRoundItem = new ItemX(Material.ANVIL, 1).setName("§aThêm đợt quái").toItemStack();
		ClickableItem addRoundClickableItem = ClickableItem.of(addRoundItem, e -> {
			if (roundList.size() >= 45)
				return;
			cont.inventory().close(p);
			Round newRound = new Round();
			roundList.add(newRound);
			new FourthEditDungeonMenu(p, newRound, id, dun).open();
		});
		cont.set(SlotPos.of(cont.inventory().getRows() - 1, 4), addRoundClickableItem);

		ItemStack backItem = new ItemX(Material.BIRCH_SIGN).setName("§aTrở lại").toItemStack();
		ClickableItem backClickable = ClickableItem.of(backItem, c -> {
			cont.inventory().close(p);
			new MainEditDungeonMenu(p, dun, id).open();
		});
		cont.set(SlotPos.of(cont.inventory().getRows() - 1, 8), backClickable);
	}

	@Override
	public void update(Player p, InventoryContents cont) {
		//
	}
}