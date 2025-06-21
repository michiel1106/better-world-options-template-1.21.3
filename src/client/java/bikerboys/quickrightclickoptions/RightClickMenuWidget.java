package bikerboys.quickrightclickoptions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundEvents;
import org.apache.commons.lang3.tuple.Pair;


import java.awt.*;
import java.util.List;

public class RightClickMenuWidget implements Drawable {
    private final List<Pair<String, Runnable>> options;
    private final int x, y, width, height;

    public RightClickMenuWidget(int x, int y, List<Pair<String, Runnable>> options) {
        this.x = x;
        this.y = y;
        this.options = options;
        this.width = 120;
        this.height = options.size() * 20;
    }
    int optionHeight = 20;
    int paddingX = 5;
    int paddingY = 5;

    private int ageTicks = 0; // Add this field to your class

    public void render(DrawContext context, int mouseX, int mouseY, float deltaticks) {

        this.ageTicks++; // Update animation timer
        context.getMatrices().pushMatrix();
        if (QuickConfig.RENDER_ANIMATION) {
            // Calculate animation scale
            float duration = 5f; // in ticks
            float progress = Math.min((ageTicks + deltaticks) / duration, 1f);
            float scale = 0.5f + 0.5f * progress; // from 0.5x to 1.0x



            // Center scaling around menu center
            float centerX = x + width / 2f;
            float centerY = y + (options.size() * optionHeight) / 2f;
            context.getMatrices().translate(centerX, centerY);
            context.getMatrices().scale(scale, scale);
            context.getMatrices().translate(-centerX, -centerY);

        }

        for (int i = 0; i < options.size(); i++) {
            String text = options.get(i).getLeft();

            int optionX = (x - 4) + paddingX;
            int optionY = y + i * optionHeight;
            int optionWidth = width - paddingX * 2;
            int optionHeight = this.optionHeight;

            // Draw background box
            context.fill(optionX, optionY, optionX + optionWidth, optionY + optionHeight, 0xAA000000);

            Color color = new Color(QuickConfig.R, QuickConfig.G, QuickConfig.B);

            try {
                context.drawBorder(optionX, optionY, optionWidth, optionHeight, color.getRGB());
            } catch (Exception ignored) {
            }

            // Draw text centered vertically in the box
            int textX = optionX + 5;
            int textY = optionY + (optionHeight - 9) / 2;
            context.drawText(MinecraftClient.getInstance().textRenderer, text, textX, textY, 0xFFFFFFFF, false);
        }

        context.getMatrices().popMatrix(); // Restore matrix stack
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int baseX = x - 4;
        for (int i = 0; i < options.size(); i++) {
            int optionX = baseX + paddingX;
            int optionY = y + i * optionHeight;
            int optionWidth = width - paddingX * 2;
            int optionHeight = this.optionHeight;

            if (mouseX >= optionX && mouseX <= optionX + optionWidth
                    && mouseY >= optionY && mouseY <= optionY + optionHeight) {

                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                options.get(i).getRight().run();
                return true;
            }
        }
        return false;
    }

}

