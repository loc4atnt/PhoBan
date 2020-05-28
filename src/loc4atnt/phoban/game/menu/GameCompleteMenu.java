package loc4atnt.phoban.game.menu;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import loc4atnt.phoban.data.match.MatchResult;
import loc4atnt.phoban.dungeon.reward.Reward;
import loc4atnt.phoban.dungeon.reward.RewardType;
import loc4atnt.phoban.main.PhoBan;

import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.ClickableItem;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.InventoryListener;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryContents;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.InventoryProvider;
import loc4atnt.xlibs.external.smartinv.fr.minuskube.inv.content.SlotPos;
import loc4atnt.xlibs.inv.XMenu;
import loc4atnt.xlibs.item.ItemX;
import loc4atnt.xlibs.item.skull.PlayerSkull;

public class GameCompleteMenu extends XMenu {

	public GameCompleteMenu(Player p, HashMap<Player, MatchResult> resultMap, List<Reward> rewardList, int teamSize) {
		super(p, "dungc", "§cTổng kết trận đấu",
				(rewardList != null)
						? ((int) (getCanDisplayReward(rewardList) / 9))
								+ ((getCanDisplayReward(rewardList) % 9 == 0) ? 2 : 3)
						: 1,
				9, new GameCompleteMenuProvider(resultMap, rewardList),
				new InventoryListener<>(InventoryCloseEvent.class, e -> {
					if (rewardList != null) {
						for (Reward r : rewardList) {
							r.applyReward(p, teamSize);
						}
					}
					PhoBan.getInst().getPlayerDataMnger().savePlayerData((Player) e.getPlayer());
				}));
	}

	private static int getCanDisplayReward(List<Reward> rewardList) {
		int amount = 0;
		for (Reward r : rewardList) {
			RewardType type = r.getType();
			if (type != null) {
				if (type.equals(RewardType.I)) {
					if (r.getItem() != null)
						amount++;
				} else if (!type.equals(RewardType.C)) {
					amount++;
				}
			}
		}
		return amount;
	}
}

class GameCompleteMenuProvider implements InventoryProvider {

	private HashMap<Player, MatchResult> resultMap;
	private List<Reward> rewardList;

	public GameCompleteMenuProvider(HashMap<Player, MatchResult> resultMap, List<Reward> rewardList) {
		this.resultMap = resultMap;
		this.rewardList = rewardList;
	}

	@Override
	public void init(Player p, InventoryContents cont) {
		for (Player player : resultMap.keySet()) {
			MatchResult r = resultMap.get(player);
			ItemX item = ItemX.toItemX(new PlayerSkull(player).toItem());
			item.setName("§a" + player.getName());
			if (r == null)
				item.addLoreLine("§8Thoát trận!");
			else {
				item.addLoreLine("§6→ Kết quả: " + (r.isWin() ? "§aThắng" : "§cThua"));
				item.addLoreLine("§e%.Sát thương: §b" + String.valueOf((int) r.getDamagedPercent()) + "%",
						"§eGiết: §b" + String.valueOf(r.getKillingAmount()),
						"§eChết: §b" + String.valueOf(r.getDeathAmount()));
				if (r.isMVP())
					item.addLoreLine("§c§l~~~~~~~ §b§lMVP §c§l~~~~~~~");
			}
			ClickableItem click = ClickableItem.empty(item.toItemStack());
			cont.add(click);
		}

		if (rewardList != null) {
			cont.fillRow(1, ClickableItem
					.empty(new ItemX(Material.GREEN_STAINED_GLASS_PANE).setName("§a↓Phần thưởng").toItemStack()));

			int index = 0;
			for (Reward r : rewardList) {
				ItemX item = convertRewardToViewItem(r);
				if (item != null) {
					ClickableItem click = ClickableItem.empty(item.toItemStack());
					cont.set(SlotPos.of(2 + ((int) (index / 9)), index % 9), click);
					index++;
				}
			}
		}
	}

	private ItemX convertRewardToViewItem(Reward reward) {
		RewardType type = reward.getType();
		ItemX item = null;
		String rewardCont = reward.getRewardCont();
		if (type == null) {
			item = null;
		} else if (type.equals(RewardType.I)) {
			if (reward.getItem() != null)
				item = new ItemX(reward.getItem());
			else
				item = null;
		} else if (type.equals(RewardType.FR)) {
			item = new ItemX(Material.CHEST, 1).setName("§bBảng chọn quà ngẫu nhiên");
		} else if (type.equals(RewardType.C)) {
			item = null;
		} else if (type.equals(RewardType.P)) {
			item = new ItemX(Material.MAGMA_CREAM, 1).setName("§bPoint: " + rewardCont);
		} else if (type.equals(RewardType.M)) {
			item = new ItemX(Material.GOLD_INGOT, 1).setName("§bTiền: " + rewardCont);
		} else if (type.equals(RewardType.E)) {
			item = new ItemX(Material.EXPERIENCE_BOTTLE, 1).setName("§bKinh nghiệm: " + rewardCont);
		}
		return item;
	}

	@Override
	public void update(Player arg0, InventoryContents arg1) {
		//
	}

}