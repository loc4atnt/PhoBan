package loc4atnt.phoban.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import loc4atnt.phoban.game.Game;
import loc4atnt.phoban.main.PhoBan;

public class SummonNextRoundCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (arg.length == 0) {
				Game g = PhoBan.getInst().getGameMnger().getGame(p);
				if (g == null)
					p.sendMessage("§cLệnh này chỉ dùng trong phòng phó bản!");
				else {
					if ((g.getLeader().equals(p))) {
						if (g.getGameStatus() == ((byte) 1)) {
							g.nextRound(p);
						} else
							p.sendMessage("§cLệnh này chỉ dùng khi đang tham chiến phó bản!");
					} else
						p.sendMessage("§cLệnh này chỉ dành cho chủ phòng!");
				}

			}
		} else {
			sender.sendMessage("§cIngame please!");
		}
		return true;
	}

}
