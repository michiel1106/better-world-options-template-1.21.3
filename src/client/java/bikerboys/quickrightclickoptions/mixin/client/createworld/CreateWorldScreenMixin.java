package bikerboys.quickrightclickoptions.mixin.client.createworld;


import bikerboys.quickrightclickoptions.QuickConfig;
import bikerboys.quickrightclickoptions.RightClickMenuAccess;
import bikerboys.quickrightclickoptions.RightClickMenuWidget;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen implements RightClickMenuAccess {
    @Shadow @Final private WorldCreator worldCreator;

    @Shadow @Final @Nullable private Screen parent;

    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Nullable
    private RightClickMenuWidget rightClickMenu;

    @Override
    public @Nullable RightClickMenuWidget getRightClickMenu() {
        return this.rightClickMenu;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if(QuickConfig.MPSCREEN) {

            CreateWorldScreen createWorldScreen = (CreateWorldScreen) (Object) this;


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
                                    Pair.of("Toggle Gamemode", (Runnable) () -> {

                                        if (this.worldCreator.getGameMode() == WorldCreator.Mode.SURVIVAL) {
                                            this.worldCreator.setGameMode(WorldCreator.Mode.HARDCORE);
                                        } else if (this.worldCreator.getGameMode() == WorldCreator.Mode.HARDCORE) {
                                            this.worldCreator.setGameMode(WorldCreator.Mode.CREATIVE);
                                        } else if (this.worldCreator.getGameMode() == WorldCreator.Mode.CREATIVE) {
                                            this.worldCreator.setGameMode(WorldCreator.Mode.SURVIVAL);
                                        }
                                    }),

                                    Pair.of("Toggle Difficulty", (Runnable) () -> {
                                        if (this.worldCreator.getDifficulty() == Difficulty.EASY) {
                                            this.worldCreator.setDifficulty(Difficulty.NORMAL);
                                        } else if (this.worldCreator.getDifficulty() == Difficulty.NORMAL) {
                                            this.worldCreator.setDifficulty(Difficulty.HARD);
                                        } else if (this.worldCreator.getDifficulty() == Difficulty.HARD) {
                                            this.worldCreator.setDifficulty(Difficulty.PEACEFUL);
                                        } else if (this.worldCreator.getDifficulty() == Difficulty.PEACEFUL) {
                                            this.worldCreator.setDifficulty(Difficulty.EASY);
                                        }
                                    }),

                                    Pair.of("Back", (Runnable) () -> {
                                        this.client.setScreen(this.parent);
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
