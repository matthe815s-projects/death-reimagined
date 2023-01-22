package dev.matthe815.deathreimagined.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.networking.PlayerRespawnPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;

public class DyingScreen extends Screen {
    public DyingScreen() {
        super(new StringTextComponent("You are dead..."));
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
                matrixStack, this.font, String.format("Forced respawn after %s seconds.", DeathReimagined.dyingTick / 40), this.width / 2, 20, 0xFFFFFF);

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

        this.addButton(new Button((this.width - 200) / 2, (this.height - 20) / 2, 200, 20, new StringTextComponent("Respawn"), button -> {
            DeathReimagined.network.sendToServer(new PlayerRespawnPacket());
            DeathReimagined.dyingTick = 255;
        }));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
