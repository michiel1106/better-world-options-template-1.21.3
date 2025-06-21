package bikerboys.quickrightclickoptions;

import eu.midnightdust.lib.config.MidnightConfig;

public class QuickConfig extends MidnightConfig {
    @Entry(name = "Colour Viewer", width = 7) public static boolean COLOUR_VIEW = false;
    @Entry(name = "Border Red", width = 7, max = 255, isSlider = true) public static int R = 26;
    @Entry(name = "Border Green", width = 7, max = 255, isSlider = true) public static int G = 113;
    @Entry(name = "Border Blue", width = 7, max = 255, isSlider = true) public static int B = 235;

    @Entry(name = "Title screen context menu", width = 7) public static boolean TITLESCREEN = true;
    @Entry(name = "Singleplayer screen context menu", width = 7) public static boolean SINGLEPLAYERSCREEN = true;
    @Entry(name = "Multiplayer screen context menu", width = 7) public static boolean MPSCREEN = true;

    @Entry(name = "Render Animation", width = 7) public static boolean RENDER_ANIMATION = true;


}
