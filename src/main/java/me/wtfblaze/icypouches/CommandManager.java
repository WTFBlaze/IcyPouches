package me.wtfblaze.icypouches;

import me.wtfblaze.icypouches.objects.PouchObject;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor
{
    private final IcyPouches plugin;
    public CommandManager(IcyPouches plugin)
    {
        this.plugin = plugin;
        PluginCommand cmd = plugin.getCommand("IcyPouches");
        if (cmd == null){
            plugin.Error("Failed to get command information from plugin.yml! Command will not work!");
        }
        else{
            cmd.setExecutor(this);
            plugin.Log("Successfully registered Commands!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length != 0)
        {
            switch (args[0].toLowerCase())
            {
                case "give":
                {
                    if (args.length >= 2)
                    {
                        String target = args[1];
                        Player targetPlayer = plugin.getServer().getPlayer(target);
                        if (targetPlayer == null){
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bIcy&ePouches&7] Could not find a player with that name!"));
                        }
                        else
                        {
                            if (args.length >= 3)
                            {
                                String pouch = args[2];
                                PouchObject selectedPouch = null;

                                for (PouchObject pouchData : plugin.pouchManager.pouches)
                                {
                                    if (pouchData.identifier.equalsIgnoreCase(pouch))
                                    {
                                        selectedPouch = pouchData;
                                    }
                                }

                                if (selectedPouch == null){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bIcy&ePouches&7] There is no pouch with that identifier!"));
                                }
                                else
                                {
                                    int amount = 1;
                                    if (args.length >= 4)
                                        amount = Integer.parseInt(args[3]);

                                    ItemStack itemStack = new ItemStack(selectedPouch.material);
                                    itemStack.setAmount(amount);

                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    if (itemMeta == null)
                                    {
                                        plugin.Error(String.format("Failed to retrieve ItemMeta for pouch %s!", selectedPouch.identifier));
                                        break;
                                    }

                                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', selectedPouch.itemName));

                                    NamespacedKey key = new NamespacedKey(plugin, "IcyPouch");
                                    PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
                                    pdc.set(key, PersistentDataType.STRING, selectedPouch.identifier);

                                    List<String> tmpLore = new ArrayList<>();

                                    for (String currentLore : selectedPouch.lore) {
                                        tmpLore.add(ChatColor.translateAlternateColorCodes('&', currentLore));
                                    }

                                    itemMeta.setLore(tmpLore);
                                    itemStack.setItemMeta(itemMeta);

                                    targetPlayer.getInventory().addItem(itemStack);
                                }
                            }
                            else {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bIcy&ePouches&7] You need to specify a pouch to give!"));
                            }
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bIcy&ePouches&7] You need to specify a target to give a pouch to!"));
                    }
                }
                break;

                case "list":
                {
                    if (plugin.pouchManager.pouches.size() == 0)
                    {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bIcy&ePouches&7] There are no pouches!"));
                    }
                    else
                    {
                        List<String> pouches = new ArrayList<>();
                        for (PouchObject pouch : plugin.pouchManager.pouches) {
                            pouches.add(pouch.identifier);
                        }
                        String result = Arrays.toString(pouches.toArray());
                        result = result.substring(1, result.length() - 1);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bIcy&ePouches&7] " + result));
                    }
                }
                break;

                case "reload":
                {
                    plugin.pouchManager.Reload(sender);
                }
                break;
            }
        }
        else
        {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bIcy&ePouches&7] Help Menu\n&b(-) &7| &eGive &7/ip give [name] [identifier] (amount)\n&b(-) &7| &eList &7/ip list\n&b(-) &7| &ereload &7/ip reload"));
        }
        return true;
    }
}
