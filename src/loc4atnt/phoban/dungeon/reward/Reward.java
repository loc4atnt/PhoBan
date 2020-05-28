package loc4atnt.phoban.dungeon.reward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import loc4atnt.xlibs.item.ItemUtil;
import loc4atnt.xlibs.money.MoneyManager;
import loc4atnt.xlibs.playerpoints.PlayerPointsManager;

public class Reward implements ConfigurationSerializable {

	private RewardType type;
	private String rewardCont;
	private List<Integer> appliedTeam;
	private ItemStack item;

	public Reward() {
		this.type = null;
		this.rewardCont = "";
		this.appliedTeam = new ArrayList<Integer>();
	}

	private Reward(String typeName, String rewardCont, List<Integer> appliedTeam) {
		this.type = RewardType.getFromName(typeName);
		this.rewardCont = rewardCont;
		this.appliedTeam = appliedTeam;
	}

	public RewardType getType() {
		return type;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public void setType(RewardType type) {
		this.type = type;
		this.rewardCont = "";
		this.item = null;
	}

	public String getRewardCont() {
		return rewardCont;
	}

	public void setRewardCont(String rewardCont) {
		this.rewardCont = rewardCont;
	}

	public List<Integer> getAppliedTeam() {
		return appliedTeam;
	}

	public void setAppliedTeam(List<Integer> appliedTeam) {
		this.appliedTeam = appliedTeam;
	}

	public boolean applyReward(Player p, int teamSize) {
		if (!canApplyReward(p, teamSize))
			return false;
		if (type.equals(RewardType.I)) {
			if (item != null) {
				ItemUtil.giveToInvOrDrop(p, item.clone());
			}
		} else if (type.equals(RewardType.FR)) {
			// FlipReward API
		} else if (type.equals(RewardType.C)) {
			String cmd = new String(rewardCont);
			while (cmd.contains("{player}")) {
				cmd = cmd.replace("{player}", p.getName());
			}
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		} else {
			int amount = -1;
			try {
				amount = Integer.parseInt(rewardCont);
			} catch (NumberFormatException e) {
				//
			}
			if (amount < 0)
				return false;
			if (type.equals(RewardType.P)) {
				PlayerPointsManager.getInst().give(p, amount);
			} else if (type.equals(RewardType.M)) {
				MoneyManager.getInst().give(p, amount);
			} else if (type.equals(RewardType.E)) {
				p.giveExp(amount);
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static Reward deserialize(Map<String, Object> map) {
		Reward r = new Reward((String) map.get("type"), (String) map.get("cont"),
				(List<Integer>) map.getOrDefault("team", null));
		r.setItem((ItemStack) map.get("item"));
		return r;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", type.name());
		map.put("cont", rewardCont);
		if (appliedTeam != null)
			map.put("team", appliedTeam);
		if (item != null)
			map.put("item", item);
		return map;
	}

	public boolean canApplyReward(Player p, int teamSize) {
		if (type == null)
			return false;
		if (rewardCont == null)
			return false;
		if (appliedTeam != null) {
			if (!appliedTeam.contains(teamSize)) {
				if (appliedTeam.size() > 0)
					return false;
			}
		}
		return true;
	}
}
