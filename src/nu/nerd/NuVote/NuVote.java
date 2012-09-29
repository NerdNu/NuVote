package nu.nerd.NuVote;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NuVote extends JavaPlugin {
    
    public List<String> options;
    public String broadcastString;
    
    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.getConfig().addDefault("questions.list", new String[]{"Item 1", "Item 2"});
        this.getConfig().addDefault("questions.boradcast", "Vote now, use the /vote <id> command!");
        this.options = loadOptions();
        this.broadcastString = loadBroadcast();
        this.saveConfig();
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                getServer().broadcastMessage(ChatColor.GOLD + broadcastString);
            }
        }, 200L, 12000L);
    }
    
    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
    }
    
    public List<String> loadOptions() {
        List<String> opts = getConfig().getStringList("questions.list");
        return opts;
    }
    
    public String loadBroadcast() {
        String bcst = getConfig().getString("questions.boradcast");
        return bcst;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You are console, y u need vote?");
            return true;
        }
        if (command.getName().equalsIgnoreCase("vote")) {
            if (args.length != 1) {
                return printUsage(sender);
            }
            try {
                int voteId = Integer.decode(args[0]);
                if (voteId <= this.options.size() && voteId > 0) {
                    getConfig().set("vote." + sender.getName(), args[0]);
                    sender.sendMessage(ChatColor.GOLD + "Voted for: " + options.get(voteId - 1));
                    saveConfig();
                    return true;
                } else {
                    return printUsage(sender);
                }
            } catch (NumberFormatException e) {
                return printUsage(sender);
            }
        }
        return false;
    }
    
    public boolean printUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Usage: /vote <id>");
        for (int i = 0; i < this.options.size(); i++) {
            sender.sendMessage(ChatColor.GOLD + ">  " + (i + 1) + "    " + this.options.get(i));
        }
        return true;
    }
}
