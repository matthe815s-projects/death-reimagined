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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        drawCenteredString(matrixStack, this.font, this.title.getString(), this.width / 2, 8, 0XFFFFFF);
        drawCenteredString(
                matrixStack, this.font, String.format("Forced respawn after %s ticks.", 255 - DeathReimagined.dyingTick), this.width / 2, 16, 0xFFFFFF);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int vOffset) {
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, new Color(0, 0, 0, 255).getRGB(), new Color(0, 0, 0, DeathReimagined.dyingTick).getRGB());
    }

    @Override
    protected void init() {
        super.init();

        this.addButton(new Button((this.width - 200) / 2, 200, 200, 20, new StringTextComponent("Respawn"), button -> {
            DeathReimagined.network.sendToServer(new PlayerRespawnPacket());
        }));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
