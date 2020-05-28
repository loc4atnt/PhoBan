package loc4atnt.phoban.menu.menuedit;

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

public class TenthEditDungeonMenu extends XMenu {

	public TenthEditDungeonMenu(Player p, DungeonRoom room, String id, CoreDungeon dun) {
		super(p, "editdun_10", "§bPhó bản: " + id, 1, 9, new TenthEditDungeonMenuProvider(room, dun, id));
	}
}

class TenthEditDungeonMenuProvider implements InventoryProvider {

	private DungeonRoom room;
	private CoreDungeon dun;
	private String id;

	public TenthEditDungeonMenuProvider(DungeonRoom room, CoreDungeon dun, String id) {
		this.room = room;
		this.dun = dun;
		this.id = id;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		Location playerSpawnLoca = room.getPlayerSpawnLoca();
		ItemStack changeSpawnPlayerLoca = new ItemX(Material.GRASS, 1).setName("§aChỉnh vị trí đầu của người chơi")
				.addLoreLine(
						"§eThế giới: "
								+ ((playerSpawnLoca != null) ? playerSpawnLoca.getWorld().getName() : "Không có"),
						"§eX: " + ((playerSpawnLoca != null) ? String.valueOf(playerSpawnLoca.getBlockX())
								: "Không có"),
						"§eY: " + ((playerSpawnLoca != null) ? String.valueOf(playerSpawnLoca.getBlockY())
								: "Không có"),
						"§eZ: " + ((playerSpawnLoca != null) ? String.valueOf(playerSpawnLoca.getBlockZ())
								: "Không có"),
						"§cClick chuột phải để chỉnh", "§bClick chuột trái để dịch chuyển tới đây")
				.toItemStack();
		ClickableItem changePlayerSpawnClickableItem = ClickableItem.of(changeSpawnPlayerLoca, e -> {
			if (e.getClick().equals(ClickType.RIGHT)) {
				PhoBan.getInst().getEditListener().putInteractConsumer(p, c -> {
					Location saveLoca = EditDungeonMenuListener.getInteractLocation(c);
					room.setPlayerSpawnLoca(saveLoca);
					new TenthEditDungeonMenu(p, room, id, dun).open();
				});
				cont.inventory().close(p);
				p.sendMessage("§aClick chuột trái vào block dưới vị trí cần chỉnh cho phó bản.");
			} else if (e.getClick().equals(ClickType.LEFT)) {
				if (playerSpawnLoca != null) {
					p.teleport(playerSpawnLoca);
					p.sendMessage("§aGo!");
					new TenthEditDungeonMenu(p, room, id, dun).open();
				} else {
					p.sendMessage("§cChưa thiết lập vị trí");
				}
			}
		});
		cont.add(changePlayerSpawnClickableItem);

		ItemStack spawnMobEditItem = new ItemX(Material.COBBLESTONE, 1).setName("§aChỉnh sửa vị trí spawn mob")
				.toItemStack();
		ClickableItem spawnMobEditClickable = ClickableItem.of(spawnMobEditItem, e -> {
			new ThirdEditDungeonMenu(p, dun, id, room).open();
		});
		cont.add(spawnMobEditClickable);

		ItemStack backItem = new ItemX(Material.BIRCH_SIGN).setName("§aTrở lại").toItemStack();
		ClickableItem backClickable = ClickableItem.of(backItem, c -> {
			new NinethEditDungeonMenu(p, dun, id).open();
		});
		cont.set(SlotPos.of(0, 8), backClickable);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}