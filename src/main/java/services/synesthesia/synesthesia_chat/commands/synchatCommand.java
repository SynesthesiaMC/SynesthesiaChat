package services.synesthesia.synesthesia_chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import services.synesthesia.synesthesia_chat.Main;
import services.synesthesia.synesthesia_chat.Utils;
import services.synesthesia.synesthesia_chat.managers.*;

public class synchatCommand implements CommandExecutor {

	private Main plugin;
	private services.synesthesia.synesthesia_chat.managers.ChatManager ChatManager;
	private FileConfiguration config;

	public synchatCommand(Main plugin) {
		this.plugin = plugin;
		this.config = this.plugin.getConfig();
		this.ChatManager = this.plugin.getChatManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = (Player) sender;

		if (!(player.hasPermission("synchat.help"))) {
			return true;
		}

		if (!(command.getName().equalsIgnoreCase("synchat"))) {
			return true;
		}

		if (args.length <= 0) {
			player.sendMessage(Utils.chat("&f------------&8[&cSYNESTHESIACHAT&8]&f-------------"));
			player.sendMessage(Utils.chat("&c/synchat strikes &f<give/take> <player> <amount>"));
			player.sendMessage(Utils.chat("&c/synchat badwords &f<add/remove> <word>"));
		} else if (args.length > 3) {
			if (args[0].equals("strikes")) {
				Player target = sender.getServer().getPlayerExact(args[2]);

				if (target == null) {
					player.sendMessage(Utils.chat(this.config.getString("messages.player_not_exists")).replace("%prefix%",
							Utils.chat(this.config.getString("prefix"))));
					return true;
				}
				if (args[1].equals("give")) {
					if (player.hasPermission("synchat.strikes.give")) {
						this.ChatManager.addStrike(target.getUniqueId(), Integer.parseInt(args[3]), false);
						String msg = Utils.chat(this.config.getString("messages.strike_added")).replace("%prefix%",
								Utils.chat(this.config.getString("prefix")));
						msg = msg.replace("%player%", args[2]);
						player.sendMessage(msg);
					}
				} else if (args[1].equals("take")) {
					if (player.hasPermission("synchat.strikes.take")) {
						this.ChatManager.removeStrike(target.getUniqueId(), Integer.parseInt(args[3]));
						String msg = Utils.chat(this.config.getString("messages.strike_taken")).replace("%prefix%",
								Utils.chat(this.config.getString("prefix")));
						msg = msg.replace("%player%", args[2]);
						player.sendMessage(msg);
					}
				}
			}
		} else if (args.length == 3) {
			if (args[0].equals("badwords")) {
				if (args[1].equals("add")) {
					if (player.hasPermission("synchat.badwords.add")) {
						this.ChatManager.addBadWord(args[2]);
						String msg = Utils.chat(this.config.getString("messages.badword_added")).replace("%prefix%",
								Utils.chat(this.config.getString("prefix")));
						msg = msg.replace("%word%", args[2]);
						player.sendMessage(msg);
					}
				} else if (args[1].equals("remove")) {
					if (player.hasPermission("synchat.badwords.remove")) {
						this.ChatManager.removeBadWord(player, args[2]);
						String msg = Utils.chat(this.config.getString("messages.badword_removed")).replace("%prefix%",
								Utils.chat(this.config.getString("prefix")));
						msg = msg.replace("%word%", args[2]);
						player.sendMessage(msg);
					}
				}
			}

		}
		return true;
	}

}
