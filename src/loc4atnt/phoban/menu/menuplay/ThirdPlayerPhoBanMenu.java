package loc4atnt.phoban.menu.menuplay;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.data.match.MatchResult;
import loc4atnt.phoban.data.player.PlayerData;
import loc4atnt.phoban.dungeon.CoreDungeon;
import loc4atnt.phoban.dungeon.Dungeon;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.Pagination;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotIterator;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;
import loc4atnt.xlibs.stringutil.Color;

public class ThirdPlayerPhoBanMenu extends XMenu {

	public ThirdPlayerPhoBanMenu(Player p) {
		super(p, "playerpb_3", Color.convert(PhoBan.getInst().getMenuPlayMnger().getName()), 3, 9,
				new ThirdPlayerPhoBanMenuProvider());
	}
}

class ThirdPlayerPhoBanMenuProvider implements InventoryProvider {

	@Override
	public void init(Player p, InventoryContents cont) {
		ClickableItem fillClick = ClickableItem.empty(new ItemX(Material.ORANGE_STAINED_GLASS_PANE).toItemStack());
		cont.fillBorders(fillClick);

		List<String> pbList = PhoBan.getInst().getMenuPlayMnger().getPhoBanIdList();
		Pagination page = cont.pagination();
		ClickableItem[] iconClicks = new ClickableItem[pbList.size()];
		for (int i = 0; i < pbList.size(); i++) {
			String id = pbList.get(i);
			ClickableItem click = null;
			if (id.charAt(0) != '!') {
				CoreDungeon coreDun = PhoBan.getInst().getDungeonMnger().getCoreDungeon(id);
				if (coreDun == null) {
					click = ClickableItem.empty(new ItemX(Material.BARRIER).setName("§cKhông tồn tại").toItemStack());
				} else {
					PlayerData data = PhoBan.getInst().getPlayerDataMnger().getPlayerData(p);
					MatchResult matchResult = data.getMatchResult(id);
					ItemX item = new ItemX(Material.PURPLE_WOOL);
					item.setName(coreDun.getName());
					boolean isHasEmptyRoom = PhoBan.getInst().getDungeonMnger().isHasEmptyDungeonRoom(id);
					if (matchResult == null) {
						item.addLoreLine("§aThành tích gần đây: §cKhông có");
					} else {
						item.addLoreLine("§aThành tích gần đây:",
								"§b- Kết quả: " + (matchResult.isWin() ? "§aThắng" : "§cThua"),
								"§b- Tỉ lệ sát thương gây ra: " + String.valueOf((int) matchResult.getDamagedPercent())
										+ "%",
								"§b- Số mạng: " + String.valueOf(matchResult.getKillingAmount()),
								"§b- Số lần chết: " + String.valueOf(matchResult.getDeathAmount()));
						if (matchResult.isMVP())
							item.addLoreLine("§c§l~~~~~~~ §b§lMVP §c§l~~~~~~~");
					}
					item.addLoreLine("§eTình trạng: " + (isHasEmptyRoom ? "§aCòn phòng" : "§cHết phòng"));
					item.addLoreLine("§eClick để tạo phòng đấu phó bản");
					click = ClickableItem.of(item.toItemStack(), e -> {
						if (isHasEmptyRoom) {
							Dungeon dun = PhoBan.getInst().getDungeonMnger().getNewDungeonRoom(id);
							cont.inventory().close(p);
							PhoBan.getInst().getGameMnger().prepairNewGame(p, id, dun);
						} else {
							p.sendMessage("§cHết phòng đấu phó bản " + coreDun.getName());
						}
					});
				}
			} else {
				String groupName = id.substring(1);
				DungeonMenuGroup group = PhoBan.getInst().getMenuPlayMnger().getDungeonMenuGroup(groupName);
				ItemX item = new ItemX(Material.PURPLE_WOOL).setName(Color.convert(group.getDisplayName()));
				click = ClickableItem.of(item.toItemStack(), c -> {
					new FourthPlayerPhoBanMenu(p, group).open();
				});
			}
			iconClicks[i] = click;
		}
		page.setItems(iconClicks);
		page.setItemsPerPage(7);
		page.addToIterator(cont.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));

		if (!page.isFirst()) {
			ItemStack previousPage = new ItemX(Material.ARROW).setName("§aTrang trước").toItemStack();
			ClickableItem previousClick = ClickableItem.of(previousPage, e -> {
				cont.inventory().open(p, page.previous().getPage());
			});
			cont.set(2, 2, previousClick);
		}

		if (!page.isLast()) {
			ItemStack nextPage = new ItemX(Material.ARROW).setName("§aTrang sau").toItemStack();
			ClickableItem nextClick = ClickableItem.of(nextPage, e -> {
				cont.inventory().open(p, page.next().getPage());
			});
			cont.set(2, 6, nextClick);
		}

		ItemStack closeItem = new ItemX(Material.BARRIER).setName("§cTrở lại").toItemStack();
		ClickableItem closeClick = ClickableItem.of(closeItem, c -> {
			cont.inventory().close(p);
			new MainPlayerPhoBanMenu(p).open();
		});
		cont.set(2, 4, closeClick);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}