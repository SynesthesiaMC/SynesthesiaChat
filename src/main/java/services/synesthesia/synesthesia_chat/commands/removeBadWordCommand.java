package services.synesthesia.synesthesia_chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import services.synesthesia.synesthesia_chat.Main;

public class removeBadWordCommand implements CommandExecutor{
	
	private Main plugin;
	private services.synesthesia.synesthesia_chat.managers.ChatManager ChatManager;
	
	public removeBadWordCommand(Main plugin) {
		this.plugin = plugin;
		this.ChatManager = this.plugin.getChatManager();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = (Player) sender;

		if (!(player.hasPermission("synchat.badwords.remove"))) {
			return true;
		}

		if (!(command.getName().equalsIgnoreCase("synchat"))) {
			return true;
		}
		
		if (args.length < 2 || !(args[0].equals("badwords"))) {
			return true;
		}
		
		if (args.length < 2 || !(args[1].equals("remove"))) {
			return true;
		}
		
		this.ChatManager.removeBadWord(player, args[2]);
		
		return true;
	}
	
}
