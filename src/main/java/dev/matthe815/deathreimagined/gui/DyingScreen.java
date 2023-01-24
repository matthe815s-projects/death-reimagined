package dev.matthe815.deathreimagined.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.enums.EnumRespawnType;
import dev.matthe815.deathreimagined.networking.PlayerRespawnPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;

public class DyingScreen extends Screen {
    public DyingScreen() {
        super(new TranslationTextComponent("deathreimagined.title.dying"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        drawCenteredString(matrixStack, this.font, this.title.getString(), this.width / 2, 8, 0XFFFFFF);
        drawCenteredString(
                matrixStack, this.font, String.format((new TranslationTextComponent("deathreimagined.body.dying")).getString(),
                        DeathReimagined.dyingTick / 40), this.width / 2, 20, 0xFFFFFF);

        // Don't allow respawning once you black out.
        if (DeathReimagined.dyingTick <= 255) this.buttons.clear();

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int vOffset) {
        // Render a fade in effect during the last 255 ticks
        int alpha = 255 - DeathReimagined.dyingTick;
        if (alpha < 0) alpha = 0;

        this.fillGradient(matrixStack, 0, 0, this.width, this.height, new Color(0, 0, 0, alpha).getRGB(), new Color(0, 0, 0, alpha).getRGB());
    }

    @Override
    protected void init() {
        super.init();

        this.addButton(new Button((this.width - 200) / 2, (this.height - 20) / 2, 200, 20, new TranslationTextComponent("dereimagined.button.respawn"), button -> {
            DeathReimagined.network.sendToServer(new PlayerRespawnPacket());
            DeathReimagined.dyingTick = 255;
        }));

        // Add additional self revive options.
        if (!this.getMinecraft().player.inventory.hasItemStack(new ItemStack(DeathReimagined.SYRINGE))) return;

        int syringeCount = this.getMinecraft().player.inventory.getStackInSlot(
                this.getMinecraft().player.inventory.getSlotFor(new ItemStack(DeathReimagined.SYRINGE))).getCount();

        this.addButton(new Button((this.width - 200) / 2, ((this.height - 20) / 2) + 25, 200, 20,
                new TranslationTextComponent("dereimagined.button.selfrevive", syringeCount), button -> {
            DeathReimagined.network.sendToServer(new PlayerRespawnPacket(EnumRespawnType.SELF_REVIVE));
        }));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
