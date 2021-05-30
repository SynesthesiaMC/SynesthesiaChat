package services.synesthesia.synesthesia_chat;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import services.synesthesia.synesthesia_chat.events.playerChatEvent;
import services.synesthesia.synesthesia_chat.commands.synchatCommand;
import services.synesthesia.synesthesia_chat.managers.ChatManager;

public class Main extends JavaPlugin {

	private boolean LuckPerms;
	private File badwordsFile, strikesFile;
	private FileConfiguration badwords, strikes;
	private ChatManager ChatManager;

	@Override
	public void onEnable() {

		PluginManager manager = this.getServer().getPluginManager();
		Logger logger = this.getLogger();

		final Date date = new Date();
		final long startTime = date.getTime();

		logger.info("------------[SYNESTHESIACHAT]-------------");
		
		this.ChatManager = new ChatManager(this);
		saveDefaultConfig();
		loadBadwordsConfig();
		loadStrikesConfig();
		
		synchatCommand synchat = new synchatCommand(this);
		this.getCommand("synchat").setExecutor(synchat);
		getServer().getPluginManager().registerEvents(new playerChatEvent(this), this);
		
		if (manager.getPlugin("LuckPerms") != null) {
			logger.info(" - Successfully Hooked into LuckPerms");
			this.LuckPerms = true;
		}

		this.getLogger().info("");
		this.getLogger().info("Loading Plugin:");
		this.getLogger().info("");
		this.getLogger().info(" - Loaded ChatManager");
		this.getLogger().info(" - Loaded Config Manager");
		this.getLogger().info(" - Loaded BadWordsFile");
		this.getLogger().info(" - Loaded StrikesFile");		
		this.getLogger().info("");
		this.getLogger().info("SynesthesiaChat v1.0 has successfully loaded!");
		final Date date2 = new Date();
		final long endTime = date2.getTime();
		this.getLogger().info("Plugin successfully loaded in " + (endTime - startTime) + "ms.");
		this.getLogger().info("------------[SYNESTHESIACHAT]-------------");

	}
	
	@Override
	public void onDisable() {
		writeOnDisk();
	}

	private void loadBadwordsConfig() {
		
		boolean no_load = false;
		
		badwordsFile = new File(getDataFolder(), "badwords.yml");
		if (!badwordsFile.exists()) {
			badwordsFile.getParentFile().mkdirs();
			saveResource("badwords.yml", false);
		}

		badwords = new YamlConfiguration();
		try {
			badwords.load(badwordsFile);
			if (!no_load) {
				Bukkit.getScheduler().runTask(this, () -> {
					loadBadWords();
				});
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void loadStrikesConfig() {

		boolean no_load = false;

		strikesFile = new File(getDataFolder(), "strikes.yml");
		if (!strikesFile.exists()) {
			strikesFile.getParentFile().mkdirs();
			saveResource("strikes.yml", false);
			no_load = true;
		}

		strikes = new YamlConfiguration();
		try {
			strikes.load(strikesFile);
			if (!no_load) {
				Bukkit.getScheduler().runTask(this, () -> {
					loadStrikes();
				});
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void loadBadWords() {
		for(String bad_word : this.badwords.getStringList("BannedWords")) {
			this.ChatManager.addBadWord(bad_word);
		}
	}
	
	private void loadStrikes() {
		for (String key : this.strikes.getConfigurationSection("strikes").getKeys(false)) {
			ConfigurationSection keySection = this.strikes.getConfigurationSection("strikes")
					.getConfigurationSection(key);
			UUID u = UUID.fromString(key);
			int strikes = keySection.getInt("strike");
			this.ChatManager.addStrike(u, strikes, true);
		}
	}
	
	private void writeOnDisk() {
		
		strikesFile.delete();
		badwordsFile.delete();
		
		HashMap<UUID, Integer> strikedplayers = this.ChatManager.getStrikes();
		for (Iterator<Entry<UUID, Integer>> iterator = strikedplayers.entrySet().iterator(); iterator.hasNext();) {
			Entry<UUID, Integer> strikedplayer = iterator.next();
			this.strikes.createSection("strikes");
			this.strikes.createSection("strikes."+strikedplayer.getKey().toString());
			this.strikes.set("strikes."+strikedplayer.getKey().toString()+".strike", strikedplayer.getValue());
			
			try {
				this.strikes.save(strikesFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		List<String> badwords = this.ChatManager.getBannedWords();
		this.badwords.set("BannedWords", badwords);
		
		try {
			this.badwords.save(badwordsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public ChatManager getChatManager() {
		return this.ChatManager;
	}
	
	public FileConfiguration getBadwords() {
		return this.badwords;
	}

	public FileConfiguration getStrikes() {
		return this.strikes;
	}
	
	public boolean getLuckPerms() {
		return this.LuckPerms;
	}

}
