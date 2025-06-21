package bikerboys.quickrightclickoptions.mixin.client.world;



import bikerboys.quickrightclickoptions.QuickConfig;
import bikerboys.quickrightclickoptions.QuickRightClickOptions;
import bikerboys.quickrightclickoptions.RightClickMenuAccess;
import bikerboys.quickrightclickoptions.RightClickMenuWidget;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("ALL")
@Mixin(SelectWorldScreen.class)
public abstract class Selectworldscreenmixin extends Screen implements RightClickMenuAccess {

    @Unique
    private TextFieldWidget textFieldWidget;

    @Unique
    private WorldListWidget.WorldEntry coolentry;



    @Unique
    private ButtonWidget confirmName;

    @Unique
    private String name = "";

    @Override
    public RightClickMenuWidget getRightClickMenu() {
        return this.rightClickMenu;
    }

    @Unique
    public boolean renamingworld = false;

    @Shadow private WorldListWidget levelList;

    @Shadow @Final protected Screen parent;

    @Shadow protected TextFieldWidget searchBox;

    protected Selectworldscreenmixin(Text title) {
        super(title);
    }

    @Nullable
    private RightClickMenuWidget rightClickMenu;


    @Inject(method = "render", at = @At("HEAD"))
    private void whatevs(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {

        this.textFieldWidget.setVisible(renamingworld);
        this.confirmName.visible = renamingworld;

    }

    @Inject(method = "init", at = @At("TAIL"))
    private void whatevsguys(CallbackInfo ci) {
        this.textFieldWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 20, this.height-400, 150, 15, Text.literal(""));



        this.textFieldWidget.setChangedListener(newText -> this.name = newText);

        this.confirmName = this.addDrawableChild(ButtonWidget.builder(Text.translatable("Confirm"), (button) -> {
            if (levelList != null) {
                WorldListWidget.Entry rawEntry = coolentry;
                if (rawEntry instanceof WorldListWidget.WorldEntry entry) {
                    Path oldPath = entry.level.getIconPath().getParent();
                    File newFile = oldPath.resolveSibling(getName()).toFile();


                    if (oldPath.toFile().renameTo(newFile)) {
                        QuickRightClickOptions.LOGGER.info("Renamed successfully to: " + newFile.getName());
                        MinecraftClient.getInstance().getToastManager().add(new SystemToast(new SystemToast.Type(), Text.literal("Rename succeeded"), Text.literal("Renamed " + '"' + oldPath.toFile().getName() + '"' + " to " + '"' + newFile.getName() + '"')));

                    } else {
                        QuickRightClickOptions.LOGGER.info("Rename failed!");
                        MinecraftClient.getInstance().getToastManager().add(new SystemToast(new SystemToast.Type(), Text.literal("Rename failed"), Text.literal("Rename failed, please try again.")));
                    }

                    renamingworld = false;
                    this.textFieldWidget.setText("");
                    MinecraftClient.getInstance().setScreen(new SelectWorldScreen(this.parent)); // Reload screen
                }
            }
        }).dimensions(20, this.height - 380, 150, 20).build());

        this.addDrawableChild(textFieldWidget);

    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (QuickConfig.SINGLEPLAYERSCREEN) {

            SelectWorldScreen selectWorldScreen = (SelectWorldScreen) (Object) this;

            if (!textFieldWidget.mouseClicked(mouseX, mouseY, button)) {
                renamingworld = false;
            }

            if (rightClickMenu != null && rightClickMenu.mouseClicked(mouseX, mouseY, button)) {
                rightClickMenu = null; // Close menu after option selected
                return true;
            }

            if (rightClickMenu != null) {
                rightClickMenu = null;
            }

            if (button == 1) {
                WorldListWidget.Entry rawEntry = getEntryAt(mouseX, mouseY);
                if (rawEntry instanceof WorldListWidget.WorldEntry entry) {
                    this.rightClickMenu = new RightClickMenuWidget(
                            (int) mouseX, (int) mouseY,
                            List.of(
                                    Pair.of("Edit", entry::edit),
                                    Pair.of("Delete", entry::deleteIfConfirmed),
                                    Pair.of("Recreate", entry::recreate),
                                    Pair.of("Refresh", () -> {
                                        MinecraftClient.getInstance().setScreen(new SelectWorldScreen(this.parent));
                                    }),
                                    Pair.of("Rename World Folder", () -> {

                                        renamingworld = true;
                                        entry.level.getIconPath().getParent().toFile().renameTo(new File(getName()));

                                        this.coolentry = entry;


                                    }),
                                    Pair.of("Back", (Runnable) () -> {
                                        this.client.setScreen(this.parent);
                                    })
                            )
                    );
                    return true;
                } else {
                    this.rightClickMenu = new RightClickMenuWidget((int) mouseX, (int) mouseY, List.of(
                            Pair.of("Create New World", (Runnable) () -> {
                                CreateWorldScreen.show(this.client, selectWorldScreen);
                            }),
                            Pair.of("Back", (Runnable) () -> {
                                this.client.setScreen(this.parent);
                            })
                    ));
                }
            }


            return super.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Nullable
    public WorldListWidget.Entry getEntryAt(double x, double y) {
        for (WorldListWidget.Entry entry : this.levelList.children()) {
            if (entry.isMouseOver(x, y)) {
                return entry;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
