package me.wtfblaze.icypouches.objects;

import org.bukkit.Material;

import java.util.List;

public class PouchObject
{
    public PouchObject(String identifier, String itemName, Material material, List<String> lore, long minimum, long maximum, List<String> commands, String permission, String message)
    {
        this.identifier = identifier;
        this.itemName = itemName;
        this.material = material;
        this.lore = lore;
        this.minimum = minimum;
        this.maximum = maximum;
        this.commands = commands;
        this.permission = permission;
        this.message = message;
    }

    public String identifier;
    public String itemName;
    public Material material;
    public List<String> lore;
    public long minimum;
    public long maximum;
    public List<String> commands;
    public String permission;
    public String message;
}
