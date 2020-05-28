package loc4atnt.phoban.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import loc4atnt.phoban.main.PhoBan;

public class LeaveTeamCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] arg) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (arg.length == 0) {
				PhoBan.getInst().getGameMnger().quitGame(p);
			}
		} else {
			sender.sendMessage("§cIngame please!");
		}
		return true;
	}

}
