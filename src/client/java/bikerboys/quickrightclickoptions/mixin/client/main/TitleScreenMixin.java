package bikerboys.quickrightclickoptions.mixin.client.main;


import bikerboys.quickrightclickoptions.QuickConfig;
import bikerboys.quickrightclickoptions.RightClickMenuAccess;
import bikerboys.quickrightclickoptions.RightClickMenuWidget;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.*;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen implements RightClickMenuAccess {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Unique
    @Nullable
    private RightClickMenuWidget rightClickMenu;

    @Override
    public @Nullable RightClickMenuWidget getRightClickMenu() {
        return this.rightClickMenu;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (QuickConfig.TITLESCREEN) {
            TitleScreen titleScreen = (TitleScreen) (Object) this;

            if (rightClickMenu != null && rightClickMenu.mouseClicked(mouseX, mouseY, button)) {
                rightClickMenu = null; // Close menu after option selected
                return true;
            }

            if (rightClickMenu != null) {
                rightClickMenu = null;
            }

            if (button == 1) {


                this.rightClickMenu = new RightClickMenuWidget(
                        (int) mouseX, (int) mouseY,
                        List.of(
                                Pair.of("Multiplayer", (Runnable) () -> {
                                    Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(titleScreen) : new MultiplayerWarningScreen(titleScreen);
                                    this.client.setScreen((Screen) screen);
                                }),

                                Pair.of("Singleplayer", (Runnable) () -> {
                                    this.client.setScreen(new SelectWorldScreen(titleScreen));
                                }),

                                Pair.of("Options", (Runnable) () -> {
                                    this.client.setScreen(new OptionsScreen(titleScreen, this.client.options));
                                }),
                                Pair.of("Realms", (Runnable) () -> {
                                    this.client.setScreen(new RealmsMainScreen(titleScreen));
                                })
                        )
                );

                return true;


            }


            return super.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

}
