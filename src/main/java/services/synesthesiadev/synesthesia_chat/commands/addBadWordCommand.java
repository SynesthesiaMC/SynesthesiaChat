package services.synesthesiadev.synesthesia_chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import services.synesthesiadev.synesthesia_chat.Main;
import services.synesthesiadev.synesthesia_chat.managers.ChatManager;

public class addBadWordCommand implements CommandExecutor{
	
	private Main plugin;
	private ChatManager ChatManager;
	
	public addBadWordCommand(Main plugin) {
		this.plugin = plugin;
		this.ChatManager = this.plugin.getChatManager();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;

		if (!(player.hasPermission("synchat.badwords.add"))) {
			return true;
		}

		if (!(command.getName().equalsIgnoreCase("synchat"))) {
			return true;
		}
		
		if (args.length < 2 || !(args[0].equals("badwords"))) {
			return true;
		}
		
		if (args.length < 2 || !(args[0].equals("add"))) {
			return true;
		}
		
		this.ChatManager.addBadWord(args[2]);
		
		return true;
		
	}

}
