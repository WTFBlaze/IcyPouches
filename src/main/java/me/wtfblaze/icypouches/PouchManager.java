package me.wtfblaze.icypouches;

import me.wtfblaze.icypouches.objects.PouchObject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PouchManager
{
    public List<PouchObject> pouches = new ArrayList<>();
    public IcyPouches plugin;
    private final File pouchesFolder;

    public PouchManager(IcyPouches plugin)
    {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        pouchesFolder = new File(plugin.getDataFolder() + "\\Pouches");
        if (!pouchesFolder.exists())
            if (pouchesFolder.mkdir()) {
                plugin.Log("Successfully created Pouches directory!");
                try (InputStream inputStream = plugin.getResource("ExamplePouch.yml"))
                {
                    if (inputStream == null)
                    {
                        plugin.Error("There was an issue copying the Example Pouch from resources!");
                        return;
                    }
                    File exampleFile = new File(pouchesFolder + "\\ExamplePouch.yml");
                    InputStreamToFile(inputStream, exampleFile);
                } catch (IOException e) {
                    plugin.Error(e.getMessage());
                }
            }
            else {
                plugin.Error("Failed to create Pouches directory! Disabling plugin...");
                plugin.getPluginLoader().disablePlugin(plugin);
            }
        LoadPouches();
    }


    public void LoadPouches()
    {
        File[] files = pouchesFolder.listFiles();
        if (files != null)
        {
            if (files.length != 0)
            {
                for (File child : files)
                {
                    String fileName = child.getName();
                    int dotIndex = fileName.lastIndexOf('.');
                    if (fileName.substring(dotIndex + 1).equals("yml"))
                    {
                        ParsePouch(fileName, YamlConfiguration.loadConfiguration(child));
                    }
                }
                plugin.Log(String.format("Successfully loaded %s pouches!", pouches.size()));
            }
            else
            {
                plugin.Log("No pouch configs found! Skipped loading them.");
            }
        }
    }

    public void Reload(CommandSender sender)
    {
        try {
            pouches.clear();
            LoadPouches();
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&7[&bIcy&ePouches&7] &aSuccessfully &7reloaded! Loaded &b%s &7pouches.", pouches.size())));
            } else {
                plugin.Log(String.format("Reloaded configs successfully! Loaded %s pouches!", pouches.size()));
            }
        }
        catch (Exception ex){
            if (sender instanceof Player){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bIcy&ePouches&7] &cFailed &7to reload plugin!"));
            }
            plugin.Error(String.format("Failed to reload plugin! Error Message: %s", ex.getMessage()));
            ex.printStackTrace();
        }
    }

    private void ParsePouch(String fileName, FileConfiguration config)
    {
        if (!config.contains("Identifier"))
        {
            plugin.Error(String.format("Failed to load pouch %s! Missing Identifier!", fileName));
            return;
        }

        String Identifier = config.getString("Identifier");
        if (Identifier == null || Identifier.isEmpty())
        {
            plugin.Error(String.format("Failed to load pouch %s! Identifier is empty or null!", fileName));
            return;
        }
        Identifier = Identifier.replace(' ', '_');

        boolean duplicateID = false;
        for (PouchObject pouchObject : pouches)
        {
            if (pouchObject.identifier.equalsIgnoreCase(Identifier))
            {
                duplicateID = true;
                break;
            }
        }

        if (duplicateID)
        {
            plugin.Error(String.format("Failed to load pouch %s! Identifier is already in use!", fileName));
            return;
        }

        if (!config.contains("Item-Name"))
        {
            plugin.Error(String.format("Failed to load pouch %s! Missing Item-Name!", fileName));
            return;
        }

        String ItemName = config.getString("Item-Name");
        if (ItemName == null || ItemName.isEmpty())
        {
            plugin.Error(String.format("Failed to load pouch %s! Item-Name is empty or null!", fileName));
            return;
        }

        if (!config.contains("Material"))
        {
            plugin.Error(String.format("Failed to load pouch %s! Missing Material!", fileName));
            return;
        }

        String materialName = config.getString("Material");
        if (materialName == null || materialName.isEmpty())
        {
            plugin.Error(String.format("Failed to load pouch %s! Material is empty or null!", fileName));
            return;
        }

        Material material = Material.getMaterial(materialName);
        if (material == null)
        {
            plugin.Error(String.format("Failed to load pouch %s! Invalid Material Type!", fileName));
            return;
        }

        if (!config.contains("Lore"))
        {
            plugin.Error(String.format("Failed to load pouch %s! Missing Lore!", fileName));
            return;
        }

        List<String> Lore = config.getStringList("Lore");

        if (!config.contains("Minimum"))
        {
            plugin.Error(String.format("Failed to load pouch %s! Missing Minimum!", fileName));
            return;
        }

        long Minimum = config.getLong("Minimum");
        if (Minimum < 0)
        {
            plugin.Error(String.format("Failed to load pouch %s! Minimum is a negative number!", fileName));
            return;
        }

        if (!config.contains("Maximum"))
        {
            plugin.Error(String.format("Failed to load pouch %s! Missing Minimum!", fileName));
            return;
        }

        long Maximum = config.getLong("Maximum");
        if (Maximum < 0)
        {
            plugin.Error(String.format("Failed to load pouch %s! Maximum is a negative number!", fileName));
            return;
        }

        if (!config.contains("Commands"))
        {
            plugin.Error(String.format("Failed to load pouch %s! Missing Commands!", fileName));
            return;
        }

        List<String> Commands = config.getStringList("Commands");

        if (!config.contains("Permission"))
        {
            plugin.Error(String.format("Failed to load pouch %s! Missing Permission!", fileName));
            return;
        }

        String Permission = config.getString("Permission");
        if (Permission == null || Permission.isEmpty())
        {
            plugin.Error(String.format("Failed to load pouch %s! Permission is empty or null!", fileName));
            return;
        }

        boolean duplicatePerm = false;
        for (PouchObject pouchObject : pouches)
        {
            if (pouchObject.identifier.equalsIgnoreCase(Permission))
            {
                duplicatePerm = true;
                break;
            }
        }

        if (duplicatePerm)
        {
            plugin.Error(String.format("Failed to load pouch %s! Permission is already in use!", fileName));
            return;
        }

        if (!config.contains("Message"))
        {
            plugin.Error(String.format("Failed to load pouch %s! Missing Message!", fileName));
            return;
        }

        String Message = config.getString("Message");
        if (Message == null || Message.isEmpty())
        {
            plugin.Error(String.format("Failed to load pouch %s! Message is empty or null!", fileName));
            return;
        }

        pouches.add(new PouchObject(Identifier, ItemName, material, Lore, Minimum, Maximum, Commands, Permission, Message));
    }

    private void InputStreamToFile(InputStream inputStream, File file) throws IOException
    {
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[8192];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }
}
