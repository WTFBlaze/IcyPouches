package me.wtfblaze.icypouches;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class IcyPouches extends JavaPlugin
{
    public PouchManager pouchManager;
    public EventManager eventManager;
    public CommandManager commandManager;
    public Metrics metrics;

    @Override
    public void onEnable()
    {
        pouchManager = new PouchManager(this);
        eventManager = new EventManager(this);
        commandManager = new CommandManager(this);
        metrics = new Metrics(this, 16445);
    }

    public void Log(String msg)
    {
        getServer().getConsoleSender().sendMessage(String.format("%s[%sIcy%sPouches%s] %s", ChatColor.WHITE, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.WHITE, msg));
    }

    public void Error(String msg)
    {
        getServer().getConsoleSender().sendMessage(String.format("%s[%sIcy%sPouches%s] %sERROR %s%s", ChatColor.WHITE, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.WHITE, ChatColor.RED, ChatColor.WHITE, msg));
    }
}
