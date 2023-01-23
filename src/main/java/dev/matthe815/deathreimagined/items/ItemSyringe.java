package dev.matthe815.deathreimagined.items;

import dev.matthe815.deathreimagined.DeathReimagined;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemSyringe extends Item {
    public ItemSyringe() {
        super(new Properties());
        this.setRegistryName(new ResourceLocation(DeathReimagined.MODID, "syringe"));
    }
}
