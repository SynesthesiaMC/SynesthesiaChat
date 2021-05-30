package services.synesthesia.synesthesia_chat.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import services.synesthesia.synesthesia_chat.Main;
import services.synesthesia.synesthesia_chat.Utils;

public class ChatManager {

	private Main plugin;
	private FileConfiguration config;
	private List<String> bannedwords;
	private HashMap<UUID, Integer> playerstrikes;
	private HashMap<UUID, Long> chatCooldowns;
	private int cooldown;

	public ChatManager(Main plugin) {
		this.plugin = plugin;
		this.config = this.plugin.getConfig();
		this.bannedwords = new ArrayList<String>();
		this.playerstrikes = new HashMap<UUID, Integer>();
		this.chatCooldowns = new HashMap<UUID, Long>();
		this.cooldown = this.config.getInt("chat_cooldown");
		startChatCooldown();
	}

	public void addStrike(UUID u, Integer strikes, boolean main) {
		int strike;
		if (this.playerstrikes.containsKey(u)) {
			strike = playerstrikes.get(u);
			if (strike >= this.config.getInt("strikes.strikes_needed")
					|| strike + strikes >= this.config.getInt("strikes.strikes_needed")) {
				Player player = Bukkit.getPlayer(u);
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				String command = this.config.getString("punishment.command");
				command = command.replace("%player%", player.getName());
				Bukkit.dispatchCommand(console, command);
				this.playerstrikes.remove(u);
				this.playerstrikes.put(u, 0);
				player.sendMessage(Utils.chat(this.config.getString("messages.strike_alert")).replace("%prefix%",
						Utils.chat(this.config.getString("prefix"))));
			} else {
				strike = this.playerstrikes.get(u);
				strike += strikes;
				this.playerstrikes.remove(u);
				this.playerstrikes.put(u, strike);
				Player player = Bukkit.getPlayer(u);
				player.sendMessage(Utils.chat(this.config.getString("messages.strike_alert")).replace("%prefix%",
						Utils.chat(this.config.getString("prefix"))));
			}
		} else {
			this.playerstrikes.put(u, strikes);
			Player player = Bukkit.getPlayer(u);
			if (!main) {
				player.sendMessage(Utils.chat(this.config.getString("messages.strike_alert")).replace("%prefix%",
						Utils.chat(this.config.getString("prefix"))));
			}
		}
	}

	public void removeStrike(UUID u, Integer strikes) {
		int strike;
		if (this.playerstrikes.containsKey(u)) {
			strike = playerstrikes.get(u);
			if (strike - strikes <= 0) {
				this.playerstrikes.remove(u);
			} else {
				strike -= strikes;
				this.playerstrikes.remove(u);
				this.playerstrikes.put(u, strike);
			}
		} else {
			Player player = Bukkit.getPlayer(u);
			player.sendMessage(Utils.chat(this.config.getString("messages.no_strike_player")).replace("%prefix%",
					this.config.getString("prefix")));
		}
	}

	public void removeBadWord(Player player, String badword) {
		int i = 0;
		for (String badw : this.bannedwords) {
			if (badw.equalsIgnoreCase(badword)) {
				this.bannedwords.remove(i);
				break;
			}
			i++;
		}

		if (i == this.bannedwords.size()) {
			player.sendMessage(Utils.chat(this.config.getString("messages.no_bad_word")).replace("%prefix%",
					this.config.getString("prefix")));
		}

	}

	public boolean checkCooldown(UUID u) {
		if (this.chatCooldowns.containsKey(u)) {
			
			this.plugin.getLogger().info(""+(((this.chatCooldowns.get(u) / 1000) + this.cooldown ) - ( System.currentTimeMillis() / 1000 )));
			
			if ((((this.chatCooldowns.get(u) / 1000) + this.cooldown ) - ( System.currentTimeMillis() / 1000 )) > 0) {
				return true;
			} else {
				this.chatCooldowns.remove(u);
				return false;
			}
		} else {
			this.chatCooldowns.put(u, System.currentTimeMillis());
			return false;
		}
	}

	private void startChatCooldown() {

		final BukkitScheduler scheduler = Bukkit.getScheduler();
		scheduler.runTaskTimerAsynchronously((Plugin) this.plugin, (Runnable) new Runnable() {
			@Override
			public void run() {

				if (!chatCooldowns.isEmpty()) {

					for (Iterator<Entry<UUID, Long>> iterator = chatCooldowns.entrySet().iterator(); iterator
							.hasNext();) {

						Entry<UUID, Long> player = iterator.next();
						player.setValue(player.getValue() - 1000);
					}

				}

			}
		}, 0L, 20L);

	}

	public String getCooldown(UUID u) {

		int cooldownInSeconds = this.chatCooldowns.get(u).intValue() / 1000;
		return Utils.formatTime(cooldownInSeconds);

	}

	public void addBadWord(String badword) {
		this.bannedwords.add(badword);
	}

	public List<String> getBannedWords() {
		return this.bannedwords;
	}

	public HashMap<UUID, Integer> getStrikes() {
		return this.playerstrikes;
	}

}
