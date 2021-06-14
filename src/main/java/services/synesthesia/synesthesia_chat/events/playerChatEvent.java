package services.synesthesia.synesthesia_chat.events;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.milkbowl.vault.chat.Chat;
import services.synesthesia.synesthesia_chat.managers.ChatManager;

import services.synesthesia.synesthesia_chat.Main;
import services.synesthesia.synesthesia_chat.Utils;

public class playerChatEvent implements Listener {

	private Main plugin;
	private ChatManager ChatManager;
	private List<String> badwords;
	private FileConfiguration config;
	private Chat chat;
	
	public playerChatEvent(Main plugin) {
		this.plugin = plugin;
		this.ChatManager = this.plugin.getChatManager();
		this.config = this.plugin.getConfig();
		this.chat = this.plugin.getChat();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMessage(AsyncPlayerChatEvent e) {
		
		Player player = e.getPlayer();
		
		String msg = e.getMessage();
		
		if(this.config.getBoolean("ChatPrefixes.enabled"))
		for(String group : this.chat.getPlayerGroups(player)) {
			String prefix = this.config.getString("ChatPrefixes.prefixes." + group );
			if(prefix != null) {
				msg = prefix + " " + msg;
				msg = Utils.chat(msg);
				e.setMessage(msg);
			}
		}
		
		e.setMessage(msg);
		
		if (player.hasPermission("synchat.bypass")) {
			return;
		}
		
		if(player.hasPermission("synchat.notalk")) {
			e.setCancelled(true);
			return;
		}
		
		if(Utils.ipChecker(e.getMessage())) {
			this.ChatManager.addStrike(player.getUniqueId(), 1, false);
			player.sendMessage(Utils.chat(this.config.getString("messages.cant_send_ip")).replace("%prefix%",
					Utils.chat(this.config.getString("prefix"))));
			e.setCancelled(true);
			return;
		}
		
		if(Utils.urlChecker(e.getMessage())) {
			this.ChatManager.addStrike(player.getUniqueId(), 1, false);
			player.sendMessage(Utils.chat(this.config.getString("messages.cant_send_url")).replace("%prefix%",
					Utils.chat(this.config.getString("prefix"))));
			e.setCancelled(true);
			return;
		}
		
		if (this.ChatManager.checkCooldown(player.getUniqueId())) {
			String cooldown = this.ChatManager.getCooldown(player.getUniqueId());
			this.plugin.getLogger().info(cooldown);
			String message = Utils.chat(this.config.getString("messages.chat_cooldown")).replace("%prefix%",
					Utils.chat(this.config.getString("prefix")));
			player.sendMessage(Utils.chat(message.replace("%time%", cooldown)));
			e.setCancelled(true);
			return;
		}
		
		this.badwords = this.ChatManager.getBannedWords();
		String message;
		message = e.getMessage();
		message.toLowerCase();
		
		for (String badword : badwords) {
			if (message.contains(badword)) {
				this.ChatManager.addStrike(player.getUniqueId(), 1, false);
				message = message.replace(badword, "*****");
				break;
			}
		}
		
		e.setMessage(message);

	}

}
