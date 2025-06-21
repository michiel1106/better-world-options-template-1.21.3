package bikerboys.quickrightclickoptions.mixin.client.mp;


import bikerboys.quickrightclickoptions.QuickConfig;
import bikerboys.quickrightclickoptions.RightClickMenuAccess;
import bikerboys.quickrightclickoptions.RightClickMenuWidget;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@SuppressWarnings("ALL")
@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen implements RightClickMenuAccess {


    @Shadow protected MultiplayerServerListWidget serverListWidget;

    @Shadow public abstract void connect();

    @Shadow protected abstract void removeEntry(boolean confirmedAction);

    @Shadow protected abstract void refresh();

    @Shadow private ServerInfo selectedEntry;

    @Shadow protected abstract void editEntry(boolean confirmedAction);

    @Shadow protected abstract void directConnect(boolean confirmedAction);

    @Shadow @Final private Screen parent;
    @Unique
    @Nullable
    private RightClickMenuWidget rightClickMenu;

    @Override
    public @Nullable RightClickMenuWidget getRightClickMenu() {
        return this.rightClickMenu;
    }


    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if(QuickConfig.MPSCREEN) {

            MultiplayerScreen multiplayerScreen = (MultiplayerScreen) (Object) this;


            if (rightClickMenu != null && rightClickMenu.mouseClicked(mouseX, mouseY, button)) {
                rightClickMenu = null; // Close menu after option selected
                return true;
            }

            if (rightClickMenu != null) {
                rightClickMenu = null;
            }

            if (button == 1) {
                MultiplayerServerListWidget.Entry rawEntry = getEntryAt(mouseX, mouseY);
                if (rawEntry instanceof MultiplayerServerListWidget.ServerEntry entry) {
                    this.rightClickMenu = new RightClickMenuWidget(
                            (int) mouseX, (int) mouseY,
                            List.of(
                                    Pair.of("Edit", () -> {
                                        this.serverListWidget.setSelected(entry);

                                        ServerInfo serverInfo = entry.getServer();
                                        multiplayerScreen.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, ServerInfo.ServerType.OTHER);
                                        multiplayerScreen.selectedEntry.copyWithSettingsFrom(serverInfo);
                                        this.client.setScreen(new AddServerScreen(multiplayerScreen, multiplayerScreen::editEntry, multiplayerScreen.selectedEntry));
                                    }),

                                    Pair.of("Delete", () -> {
                                        String string = entry.getServer().name;
                                        this.serverListWidget.setSelected(entry);

                                        if (string != null) {

                                            Text text = Text.translatable("selectServer.deleteQuestion");
                                            Text text2 = Text.translatable("selectServer.deleteWarning", string);
                                            Text text3 = Text.translatable("selectServer.deleteButton");
                                            Text text4 = ScreenTexts.CANCEL;
                                            this.client.setScreen(new ConfirmScreen(multiplayerScreen::removeEntry, text, text2, text3, text4));

                                        }
                                    }),

                                    Pair.of("Join", () -> {
                                        this.serverListWidget.setSelected(entry);
                                        this.connect();
                                    }),
                                    Pair.of("Reload List", this::refresh)
                            )
                    );

                    return true;


                } else {
                    this.rightClickMenu = new RightClickMenuWidget((int) mouseX, (int) mouseY, List.of(
                            Pair.of("Add server", (Runnable) () -> {
                                this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", ServerInfo.ServerType.OTHER);
                                this.client.setScreen(new AddServerScreen(multiplayerScreen, multiplayerScreen::addEntry, multiplayerScreen.selectedEntry));
                            }),
                            Pair.of("Reload List", this::refresh),
                            Pair.of("Direct Connect", (Runnable) () -> {
                                if (this.selectedEntry == null) {
                                    this.selectedEntry = new ServerInfo("Direct Connect", "", ServerInfo.ServerType.OTHER);
                                }
                                this.client.setScreen(new DirectConnectScreen(multiplayerScreen, this::directConnect, this.selectedEntry));
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

    @Unique
    public MultiplayerServerListWidget.Entry getEntryAt(double x, double y) {
        for (MultiplayerServerListWidget.Entry entry : this.serverListWidget.children()) {
            if (entry.isMouseOver(x, y)) {
                return entry;
            }
        }
        return null;
    }

}
