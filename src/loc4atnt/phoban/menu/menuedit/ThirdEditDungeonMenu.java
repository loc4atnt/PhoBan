package loc4atnt.phoban.menu.menuedit;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.dungeon.DungeonRoom;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotPos;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class ThirdEditDungeonMenu extends XMenu {

	public ThirdEditDungeonMenu(Player p, CoreDungeon dun, String id, DungeonRoom room) {
		super(p, "editdun_3", "§bPhó bản: " + id,
				((int) (room.getMobSpawnLoca().size() / 9)) + ((room.getMobSpawnLoca().size() % 9 == 0) ? 1 : 2), 9,
				new ThirdEditDungeonMenuProvider(dun, id, room));
	}
}

class ThirdEditDungeonMenuProvider implements InventoryProvider {

	private CoreDungeon dun;
	private String id;
	private DungeonRoom room;

	public ThirdEditDungeonMenuProvider(CoreDungeon dun, String id, DungeonRoom room) {
		this.dun = dun;
		this.id = id;
		this.room = room;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		List<Location> locaList = room.getMobSpawnLoca();
		for (int i = 0; i < locaList.size(); i++) {
			Location l = locaList.get(i);
			int finalI = i;
			int iconIndex = i + 1;
			ItemStack item = new ItemX(Material.COBBLESTONE, 1).setName("§a" + String.valueOf(iconIndex))
					.addLoreLine("§eThế giới: " + l.getWorld().getName(), "§eX: " + String.valueOf(l.getBlockX()),
							"§eY: " + String.valueOf(l.getBlockY()), "§eZ: " + String.valueOf(l.getBlockZ()),
							"§cClick trái để chỉnh sửa", "§cClick phải để xóa")
					.toItemStack();
			ClickableItem clickable = ClickableItem.of(item, e -> {
				if (e.getClick().equals(ClickType.LEFT)) {
					PhoBan.getInst().getEditListener().putInteractConsumer(p, c -> {
						Location saveLoca = EditDungeonMenuListener.getInteractLocation(c);
						locaList.set(finalI, saveLoca);
						new ThirdEditDungeonMenu(p, dun, id, room).open();
					});
					cont.inventory().close(p);
					p.sendMessage("§aClick chuột trái vào block dưới vị trí cần chỉnh cho phó bản.");
				} else if (e.getClick().equals(ClickType.RIGHT)) {
					locaList.remove(finalI);
					new ThirdEditDungeonMenu(p, dun, id, room).open();
				}
			});
			cont.add(clickable);
		}

		ItemStack fillItem = new ItemX(Material.LIGHT_BLUE_STAINED_GLASS_PANE).toItemStack();
		cont.fillRow(cont.inventory().getRows() - 1, ClickableItem.empty(fillItem));

		ItemStack addItem = new ItemX(Material.ANVIL, 1).setName("§aThêm vị trí spawn!").toItemStack();
		ClickableItem addClickable = ClickableItem.of(addItem, e -> {
			if (locaList.size() >= 45)
				return;
			PhoBan.getInst().getEditListener().putInteractConsumer(p, c -> {
				Location saveLoca = EditDungeonMenuListener.getInteractLocation(c);
				locaList.add(saveLoca);
				new ThirdEditDungeonMenu(p, dun, id, room).open();
			});
			cont.inventory().close(p);
			p.sendMessage("§aClick chuột trái vào block dưới vị trí cần chỉnh cho phó bản.");
		});
		cont.set(SlotPos.of(cont.inventory().getRows() - 1, 4), addClickable);

		ItemStack backItem = new ItemX(Material.BIRCH_SIGN).setName("§aTrở lại").toItemStack();
		ClickableItem backClickable = ClickableItem.of(backItem, c -> {
			new TenthEditDungeonMenu(p, room, id, dun).open();
		});
		cont.set(SlotPos.of(cont.inventory().getRows() - 1, 8), backClickable);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}