package loc4atnt.phoban.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import loc4atnt.phoban.main.PhoBan;

public class PhoBanAdCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] arg) {
		Player p = null;
		if (sender instanceof Player) {
			p = (Player) sender;
			if (!p.hasPermission(PhoBan.permission)) {
				p.sendMessage("§cYou are not Admin!");
				return true;
			}
		}
		int l = arg.length;
		if (l == 0) {
			sendHelp(sender);
		} else {
			if (arg[0].equalsIgnoreCase("help")) {
				if (l == 1)
					sendHelp(sender);
			} else if (arg[0].equalsIgnoreCase("create")) {
				if (l == 2 || l == 3) {
					boolean isNeedingPerm = false;
					if (l == 3) {
						if (arg[2].equalsIgnoreCase("true")) {
							isNeedingPerm = true;
						} else if (!arg[2].equalsIgnoreCase("false")) {
							sender.sendMessage("§c" + arg[2] + " không phải là giá trị hợp lệ!");
							return true;
						}
					}
					if (PhoBan.getInst().getDungeonMnger().create(arg[1], isNeedingPerm))
						sender.sendMessage("§aĐã tạo thành công phó bản có mã " + arg[1]);
					else
						sender.sendMessage("§cPhó bản " + arg[1] + " đã tồn tại!");
				}
			} else if (arg[0].equalsIgnoreCase("edit")) {
				if (p == null) {
					sender.sendMessage("§cThis command is only used in game!");
					return true;
				}
				if (l == 2) {
					if (!PhoBan.getInst().getDungeonMnger().edit(p, arg[1]))
						sender.sendMessage("§cPhó bản " + arg[1] + " không tồn tại!");
				}
			} else if (arg[0].equalsIgnoreCase("reload")) {
				if (l == 1) {
					PhoBan.getInst().reloadPlugin();
					sender.sendMessage("§aĐã reload!");
				}
			} else if (arg[0].equalsIgnoreCase("delete")) {
				if (l == 2) {
					if (PhoBan.getInst().getDungeonMnger().delete(arg[1]))
						sender.sendMessage("§aĐã xóa phó bản có mã " + arg[1]);
					else
						sender.sendMessage("§cPhó bản " + arg[1] + " không tồn tại!");
				}
			}
		}
		return true;
	}

	private void sendHelp(CommandSender s) {
		s.sendMessage("§c/pbad");
		s.sendMessage("§c/pbad help: §aHướng dẫn.");
		s.sendMessage(
				"§c/pbad create <mã_id_phó_bản> [true hoặc false]: §aTạo phó bản mới. Nếu điền true thì người chơi cần có permission"
						+ " \"phoban.<mã_id_phó_bản>\" để chơi phó bản, còn false thì không cần có permission để chơi.");
		s.sendMessage("§c/pbad edit <mã_id_phó_bản>: §aChỉnh sửa phó bản.");
		s.sendMessage("§c/pbad reload: §aReload config.");
		s.sendMessage("§c/pbad delete <mã_id_phó_bản>: §aXóa phó bản.");
	}
}
