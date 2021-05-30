package services.synesthesia.synesthesia_chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import services.synesthesia.synesthesia_chat.Main;
import services.synesthesia.synesthesia_chat.Utils;

public class removeStrikeCommand implements CommandExecutor{
	
	private Main plugin;
	private FileConfiguration config;
	private services.synesthesia.synesthesia_chat.managers.ChatManager ChatManager;
	
	public removeStrikeCommand(Main plugin) {
		this.plugin = plugin;
		this.config = this.plugin.getConfig();
		this.ChatManager = this.plugin.getChatManager();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = (Player) sender;

		if (!(player.hasPermission("synchat.strikes.take"))) {
			return true;
		}

		if (!(command.getName().equalsIgnoreCase("synchat"))) {
			return true;
		}

		if (args.length < 3 || !(args[0].equals("strikes"))) {
			return true;
		}
		
		if (args.length < 3 || !(args[0].equals("take"))) {
			return true;
		}
		
		Player target = sender.getServer().getPlayerExact(args[2]);

		if (target == null) {
			player.sendMessage(Utils.chat(this.config.getString("player_not_exists")).replace("%preifx%",
					Utils.chat(this.config.getString("preifx"))));
			return true;
		}
		
		this.ChatManager.removeStrike(target.getUniqueId(), Integer.parseInt(args[3]));
		
		return true;
	}
	
}
