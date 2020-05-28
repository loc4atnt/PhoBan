package loc4atnt.phoban.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import loc4atnt.phoban.game.Game;
import loc4atnt.phoban.game.menu.TeamManagerMenu;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.phoban.menu.menuplay.MainPlayerPhoBanMenu;

public class PhoBanCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] arg) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (arg.length == 0) {
				Game g = PhoBan.getInst().getGameMnger().getGame(p);
				if (g == null) {
					new MainPlayerPhoBanMenu(p).open();
				} else {
					if ((g.getGameStatus() == ((byte) 0)) && (g.getLeader().equals(p)) && (!g.isStart()))
						new TeamManagerMenu(p, g).open();
				}
			}
		} else {
			sender.sendMessage("§cIngame please!");
		}
		return true;
	}

}
