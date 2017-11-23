package nu.nerd.NuVote;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NuVote extends JavaPlugin {

    private static final String[] ORDINALS = new String[] {
            "first",
            "second",
            "third",
            "fourth",
            "fifth",
            "sixth",
            "seventh",
            "eighth",
            "ninth",
            "tenth",
    };
    private static ChatColor PRIMARY = ChatColor.GOLD;
    private static ChatColor SECONDARY = ChatColor.GREEN;
    private static ChatColor TERTIARY = ChatColor.LIGHT_PURPLE;

    private int allowedVotes;
    private String question;
    private Map<String, String> choices;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadOptions();
    }

    private void reloadOptions() {
        reloadConfig();
        loadOptions();
    }

    private void loadOptions() {
        allowedVotes = getConfig().getInt("allowed-votes");
        question = getConfig().getString("question");
        ConfigurationSection questionsSection = getConfig().getConfigurationSection("choices");
        Map<String, Object> questionValues = questionsSection.getValues(false);
        choices = new LinkedHashMap<String, String>();
        for (Map.Entry<String, Object> entry : questionValues.entrySet()) {
            String key = entry.getKey().toLowerCase();
            Object value = entry.getValue();
            if (!(value instanceof String)) {
                getLogger().warning("Invalid description for choice '" + key + "'");
                continue;
            }
            choices.put(key, (String) value);
        }
    }

    private List<String> getVotes(Player player) {
        return getConfig().getStringList("vote." + player.getUniqueId().toString() + ".votes");
    }

    private void setVotes(Player player, List<String> choices) {
        if (!checkDupes(player, choices) || !checkValidChoices(player, choices)) {
            return;
        }

        getConfig().set("vote." + player.getUniqueId().toString() + ".name", player.getName());
        getConfig().set("vote." + player.getUniqueId().toString() + ".votes", choices);
        saveConfig();
        player.sendMessage(PRIMARY + "Vote recorded!");
    }

    private boolean checkDupes(Player player, List<String> choices) {
        for (int i = 0; i < choices.size(); i++) {
            String choice = choices.get(i);
            for (int j = 0; j < i; j++) {
                if (choice.equalsIgnoreCase(choices.get(j))) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(PRIMARY).append("Duplicate choice ").append(SECONDARY).append(choice)
                            .append(PRIMARY).append(". You can only select each choice once.");
                    player.sendMessage(builder.toString());
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkValidChoices(Player player, List<String> choices) {
        for (String choice : choices) {
            if (!this.choices.containsKey(choice.toLowerCase())) {
                StringBuilder builder = new StringBuilder();
                builder.append(PRIMARY).append("No choice ").append(SECONDARY).append(choice)
                        .append(PRIMARY).append(". Type ").append(SECONDARY).append("/vote")
                        .append(PRIMARY).append(" to see a list of available choices.");
                player.sendMessage(builder.toString());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (command.getName().equalsIgnoreCase("vote")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You are console, y u need vote?");
                return true;
            }
            Player player = (Player) sender;
            if (args.length != allowedVotes) {
                return printUsage(player);
            }

            List<String> choices = Arrays.asList(args);
            setVotes(player, choices);
            return true;
        } else if (command.getName().equalsIgnoreCase("vote-reload")) {
            reloadOptions();
            sender.sendMessage(PRIMARY + "NuVote config reloaded.");
            return true;
        }
        return false;
    }
    
    private boolean printUsage(Player player) {
        player.sendMessage(TERTIARY + question);
        sendCurrentVotes(player);
        sendUsageString(player);
        sendChoiceList(player);
        return true;
    }

    private void sendCurrentVotes(Player player) {
        List<String> votes = getVotes(player);
        if (!votes.isEmpty()) {
            StringBuilder votesString = new StringBuilder("You voted for:").append(SECONDARY);
            for (String choice : votes) {
                votesString.append(" ").append(choice);
            }
            player.sendMessage(PRIMARY + votesString.toString());
        }
    }

    private void sendUsageString(Player player) {
        StringBuilder usage = new StringBuilder("Usage: ").append(SECONDARY).append("/vote");
        for (int i = 0; i < allowedVotes; i++) {
            usage.append(" <");
            if (allowedVotes == 1) {
                usage.append("choice");
            } else if (i < ORDINALS.length) {
                usage.append(ORDINALS[i]);
            } else {
                usage.append("choice").append(i + 1);
            }
            usage.append(">");
        }
        player.sendMessage(PRIMARY + usage.toString());
    }

    private void sendChoiceList(Player player) {
        StringBuilder choiceString = new StringBuilder("Available choices:");
        for (Map.Entry<String, String> entry : choices.entrySet()) {
            String choice = entry.getKey();
            String description = entry.getValue();
            choiceString.append("\n")
                    .append(SECONDARY).append(choice).append(": ")
                    .append(TERTIARY).append(description);
        }
        player.sendMessage(PRIMARY + choiceString.toString());
    }

}
