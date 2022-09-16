package me.wtfblaze.icypouches;

import me.wtfblaze.icypouches.objects.PouchObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class EventManager implements Listener
{
    private final IcyPouches plugin;
    public EventManager(IcyPouches plugin)
    {
        this.plugin = plugin;
        try {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            plugin.Log("Successfully registered Events!");
        }
        catch (Exception ex){
            plugin.Error(ex.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent e)
    {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            ItemStack itemStack = e.getItem();
            if (itemStack != null)
            {
                NamespacedKey key = new NamespacedKey(plugin, "IcyPouch");
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null) return;
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                if (pdc.has(key, PersistentDataType.STRING))
                {
                    String pouchType = pdc.get(key, PersistentDataType.STRING);

                    for (PouchObject pouchObject : plugin.pouchManager.pouches)
                    {
                        if (pouchObject.identifier.equalsIgnoreCase(pouchType))
                        {
                            e.setCancelled(true);
                            Player player = e.getPlayer();

                            if (player.hasPermission(pouchObject.permission) || player.hasPermission("icypouches.*"))
                            {
                                long result = new Random().nextLong(pouchObject.maximum - pouchObject.minimum + 1) + pouchObject.minimum;
                                for (String cmd : pouchObject.commands)
                                {
                                    if (cmd.contains("%player%"))
                                        cmd = cmd.replace("%player%", player.getName());

                                    if (cmd.contains("%random_amount%"))
                                        cmd = cmd.replace("%random_amount%", String.valueOf(result));

                                    Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
                                    player.getInventory().remove(itemStack);
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', pouchObject.message.replace("%random_amount%", String.valueOf(result))));
                                }
                            }
                            else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bIcy&ePouches&7] You do not have permission to open this pouch!"));
                            }
                        }
                    }
                }
            }
        }
    }
}
