package dev.matthe815.deathreimagined.gui;

import dev.matthe815.deathreimagined.DeathReimagined;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DyingUI {
    @SubscribeEvent
    public void onRenderGameOverlay (RenderGameOverlayEvent.Pre event)
    {
        // Show no UI when dying
        if (!DeathReimagined.LOCAL_DATA.IsDying()) return;
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) event.setCanceled(true);
    }
}
