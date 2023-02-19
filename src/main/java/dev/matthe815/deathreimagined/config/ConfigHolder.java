package dev.matthe815.deathreimagined.config;

import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
    public static class Common
    {
        public final ForgeConfigSpec.ConfigValue<Boolean> broadcastMessageOnDeath;
        public final ForgeConfigSpec.ConfigValue<Integer> reviveHealth;
        public final ForgeConfigSpec.ConfigValue<Boolean> restoreHunger;

        public Common(ForgeConfigSpec.Builder builder)
        {
            builder.push("death-effects");
            this.broadcastMessageOnDeath = builder.comment("Whether or not to broadcast a message to the server upon dying.")
                    .define("Broadcast Dying Message", true);

            builder.push("reviving");
            this.reviveHealth = builder.comment("The amount of health to be restored upon being revived by any means.")
                    .define("Revive Health", 10);
            this.restoreHunger = builder.comment("Whether or not to restore hunger upon being revived.")
                    .define("Restore Hunger", true);
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static
    {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
