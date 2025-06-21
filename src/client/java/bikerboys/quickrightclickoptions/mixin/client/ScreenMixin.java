package bikerboys.quickrightclickoptions.mixin.client;


import bikerboys.quickrightclickoptions.QuickConfig;
import bikerboys.quickrightclickoptions.RightClickMenuAccess;
import bikerboys.quickrightclickoptions.RightClickMenuWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;

@Debug(export = true)
@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow @Final private List<Drawable> drawables;

    @Inject(method = "render", at = @At("TAIL"))
    private void renderOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {

        if (QuickConfig.COLOUR_VIEW) {

            context.getMatrices().pushMatrix();
            context.getMatrices().translate(0, 0);
            context.fill(0, 0, 22, 22, new Color(QuickConfig.R, QuickConfig.G, QuickConfig.B).getRGB());
            context.getMatrices().popMatrix();

        }


        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof RightClickMenuAccess accessor) {
            RightClickMenuWidget menu = accessor.getRightClickMenu();
            if (menu != null) {
                context.getMatrices().pushMatrix();
                context.getMatrices().translate(0, 0);
                menu.render(context, mouseX, mouseY, delta);
                context.getMatrices().popMatrix();
            }
        }
    }
}
