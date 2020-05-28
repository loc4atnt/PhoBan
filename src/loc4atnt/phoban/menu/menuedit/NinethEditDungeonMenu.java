package loc4atnt.phoban.menu.menuedit;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.dungeon.DungeonRoom;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotPos;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class NinethEditDungeonMenu extends XMenu {

	public NinethEditDungeonMenu(Player p, CoreDungeon dun, String id) {
		super(p, "editdun_9", "§bPhó bản: " + id,
				((int) (dun.getRoom().size() / 9)) + ((dun.getRoom().size() % 9 == 0) ? 1 : 2), 9,
				new NinethEditDungeonMenuProvider(dun, id));
	}
}

class NinethEditDungeonMenuProvider implements InventoryProvider {

	private CoreDungeon dun;
	private String id;

	public NinethEditDungeonMenuProvider(CoreDungeon dun, String id) {
		this.dun = dun;
		this.id = id;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		List<DungeonRoom> roomList = dun.getRoom();
		for (int i = 0; i < roomList.size(); i++) {
			DungeonRoom room = roomList.get(i);
			ItemStack item = new ItemX(Material.IRON_DOOR, i + 1)
					.setName("§d§lID: " + id + String.valueOf(room.getId()))
					.addLoreLine("§cClick trái để chỉnh sửa phòng này", "§cClick phải để xóa phòng này",
							"§aTạo region (WorldGuard) cho phòng có id như trên",
							"§ađể ngăn người chơi đi ra khỏi khu vực phòng đấu!")
					.toItemStack();
			ClickableItem click = ClickableItem.of(item, e -> {
				ClickType clickType = e.getClick();
				if (clickType.equals(ClickType.RIGHT)) {
					roomList.remove(room);
					new NinethEditDungeonMenu(p, dun, id).open();
				} else if (clickType.equals(ClickType.LEFT)) {
					new TenthEditDungeonMenu(p, room, id, dun).open();
				}
			});
			cont.add(click);
		}

		ItemStack fillItem = new ItemX(Material.LIGHT_BLUE_STAINED_GLASS_PANE).toItemStack();
		cont.fillRow(cont.inventory().getRows() - 1, ClickableItem.empty(fillItem));

		ItemStack addItem = new ItemX(Material.ANVIL, 1).setName("§aThêm phòng").toItemStack();
		ClickableItem addClickableItem = ClickableItem.of(addItem, e -> {
			if (roomList.size() >= 18)
				return;
			cont.inventory().close(p);
			int roomId = roomList.size();
			DungeonRoom newRoom = new DungeonRoom(roomId, null, null);
			roomList.add(newRoom);
			new TenthEditDungeonMenu(p, newRoom, id, dun).open();
		});
		cont.set(SlotPos.of(cont.inventory().getRows() - 1, 4), addClickableItem);

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