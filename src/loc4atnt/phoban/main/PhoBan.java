package loc4atnt.phoban.main;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.infogroup.infoboard.InfoBoardReborn;

import loc4atnt.phoban.command.AcceptInvitationCommand;
import loc4atnt.phoban.command.LeaveTeamCommand;
import loc4atnt.phoban.command.PhoBanAdCommand;
import loc4atnt.phoban.command.PhoBanCommand;
import loc4atnt.phoban.command.SummonNextRoundCommand;
import loc4atnt.phoban.data.player.PlayerDataManager;
import loc4atnt.phoban.dungeon.DungeonManager;
import loc4atnt.phoban.dungeon.reward.Reward;
import loc4atnt.phoban.game.GameManager;
import loc4atnt.phoban.menu.menuedit.EditDungeonMenuListener;
import loc4atnt.phoban.menu.menuplay.MenuPlayManager;
import loc4atnt.phoban.playturn.PlayTurnManager;
import loc4atnt.xlibs.config.SimpleConfigManager;
import loc4atnt.xlibs.parameter.Parameter;
import loc4atnt.xlibs.parameter.ParameterManager;

public class PhoBan extends JavaPlugin {

	private InfoBoardReborn infoBoardPlugin;

	private static PhoBan plugin;
	public static final String permission = "phoban.*";
	public static final String MAX_TURN_PERMISSION = "phoban.maxturn";//phoban.maxturn.<amount>

	private SimpleConfigManager cfgMnger;
	private MenuPlayManager menuPlayMnger;
	private ParameterManager paramMng;
	private DungeonManager dungeonMng;
	private EditDungeonMenuListener editListener;
	private PlayerDataManager playerDataMnger;
	private GameManager gameMnger;
	private PlayTurnManager playTurnMnger;

	public static PhoBan getInst() {
		return plugin;
	}

	public PlayTurnManager getPlayTurnMnger() {
		return playTurnMnger;
	}

	public GameManager getGameMnger() {
		return gameMnger;
	}

	public PlayerDataManager getPlayerDataMnger() {
		return playerDataMnger;
	}

	public EditDungeonMenuListener getEditListener() {
		return editListener;
	}

	public SimpleConfigManager getCfgMnger() {
		return cfgMnger;
	}

	public ParameterManager getParamMnger() {
		return paramMng;
	}

	public DungeonManager getDungeonMnger() {
		return dungeonMng;
	}

	public MenuPlayManager getMenuPlayMnger() {
		return menuPlayMnger;
	}

	@Override
	public void onEnable() {
		plugin = this;
		ConfigurationSerialization.registerClass(Reward.class);

		cfgMnger = new SimpleConfigManager(plugin);
		paramMng = new ParameterManager(cfgMnger.getNewConfig("/dungeon/config.yml"),
				new Parameter("he_so_tg_hoi_sinh", 5), new Parameter("tg_hoi_sinh_goc", 10));
		dungeonMng = new DungeonManager();
		menuPlayMnger = new MenuPlayManager();
		editListener = new EditDungeonMenuListener();
		playerDataMnger = new PlayerDataManager();
		gameMnger = new GameManager();
		playTurnMnger = new PlayTurnManager();

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvents(editListener, plugin);
		pm.registerEvents(playerDataMnger, plugin);

		getCommand("phoban").setExecutor(new PhoBanCommand());
		getCommand("quai").setExecutor(new SummonNextRoundCommand());
		getCommand("pbad").setExecutor(new PhoBanAdCommand());
		getCommand("dongy").setExecutor(new AcceptInvitationCommand());
		getCommand("roidoi").setExecutor(new LeaveTeamCommand());

		getExtanalPlugin();
	}

	private void getExtanalPlugin() {
		PluginManager pm = getServer().getPluginManager();

		if (pm.isPluginEnabled("InfoBoardReborn"))
			infoBoardPlugin = (InfoBoardReborn) pm.getPlugin("InfoBoardReborn");
	}

	public InfoBoardReborn getInfoBoardPlugin() {
		return infoBoardPlugin;
	}

	public void reloadPlugin() {
		dungeonMng.reload();
		menuPlayMnger.reload();
		paramMng.reload();
	}

	public boolean isInTeam(Player player1, Player player2) {
		return gameMnger.isInTeam(player1, player2);
	}
}
