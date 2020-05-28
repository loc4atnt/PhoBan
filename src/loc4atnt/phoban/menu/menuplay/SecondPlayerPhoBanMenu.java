package loc4atnt.phoban.menu.menuplay;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.phoban.data.match.MatchResult;
import loc4atnt.phoban.data.player.PlayerData;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;

public class SecondPlayerPhoBanMenu extends XMenu {

	public SecondPlayerPhoBanMenu(Player p) {
		super(p, "playerpb_2", "§aThành tích cá nhân", 1, 9, new SecondPlayerPhoBanMenuProvider());
	}
}

class SecondPlayerPhoBanMenuProvider implements InventoryProvider {

	@Override
	public void init(Player p, InventoryContents cont) {
		PlayerData data = PhoBan.getInst().getPlayerDataMnger().getPlayerData(p);

		ClickableItem fillClick = ClickableItem.empty(new ItemX(Material.ORANGE_STAINED_GLASS_PANE).toItemStack());
		cont.set(0, 7, fillClick);

		ItemStack dmgAvrRateItem = new ItemX(Material.WOODEN_SWORD).setName("§aTỉ lệ sát thương gây ra")
				.addLoreLine("§eTrung bình mỗi trận: " + String.valueOf((int) data.getDmgPercentAvr()) + "%")
				.toItemStack();
		ClickableItem dmgAvrRateClick = ClickableItem.empty(dmgAvrRateItem);
		cont.set(0, 0, dmgAvrRateClick);

		ItemStack winRateItem = new ItemX(Material.DIAMOND_AXE)
				.setName("§aTỉ lệ thắng: " + String.valueOf((int) data.getWinPercent()) + "%").toItemStack();
		ClickableItem winRateClick = ClickableItem.empty(winRateItem);
		cont.set(0, 1, winRateClick);

		ItemStack killItem = new ItemX(Material.SKELETON_SKULL)
				.setName("§aSố mạng: " + String.valueOf(data.getKillAmount())).toItemStack();
		ClickableItem killClick = ClickableItem.empty(killItem);
		cont.set(0, 2, killClick);

		ItemStack deathItem = new ItemX(Material.SOUL_SAND)
				.setName("§aSố lần chết: " + String.valueOf(data.getDeathAmount())).toItemStack();
		ClickableItem deathClick = ClickableItem.empty(deathItem);
		cont.set(0, 3, deathClick);

		ItemStack winItem = new ItemX(Material.BEACON)
				.setName("§aSố trận thắng: " + String.valueOf(data.getWinAmount())).toItemStack();
		ClickableItem winClick = ClickableItem.empty(winItem);
		cont.set(0, 4, winClick);

		ItemStack loseItem = new ItemX(Material.SHIELD)
				.setName("§aSố trận thua: " + String.valueOf(data.getLoseAmount())).toItemStack();
		ClickableItem loseClick = ClickableItem.empty(loseItem);
		cont.set(0, 5, loseClick);

		MatchResult lastMatch = data.getLastMatch();
		ItemX lastMatchItem;
		if (lastMatch != null) {
			lastMatchItem = new ItemX(Material.BOOK).setName("§aTrần gần nhất").addLoreLine(
					"§eKết quả: " + (lastMatch.isWin() ? "§bThắng" : "§fThua"),
					"§eTỉ lệ sát thương gây ra: " + String.valueOf((int) lastMatch.getDamagedPercent()) + "%",
					"§eSố mạng: " + String.valueOf(lastMatch.getKillingAmount()),
					"§eSố lần chết: " + String.valueOf(lastMatch.getDeathAmount()));
			if (lastMatch.isMVP())
				lastMatchItem.addLoreLine("§c§l~~~~~~~ §b§lMVP §c§l~~~~~~~");
		} else {
			lastMatchItem = new ItemX(Material.BARRIER).setName("§aTrần gần nhất: §cKhông có");
		}
		ClickableItem lastMatchClick = ClickableItem.empty(lastMatchItem.toItemStack());
		cont.set(0, 6, lastMatchClick);

		ItemStack closeItem = new ItemX(Material.BARRIER).setName("§cTrở lại").toItemStack();
		ClickableItem closeClick = ClickableItem.of(closeItem, c -> {
			cont.inventory().close(p);
			new MainPlayerPhoBanMenu(p).open();
		});
		cont.set(0, 8, closeClick);
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}
}