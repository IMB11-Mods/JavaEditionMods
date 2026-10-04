package dev.imb11.sounds.util;

import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
//? if <26.3 {
import org.lwjgl.glfw.GLFW;
//?} else {
/*import com.mojang.blaze3d.platform.InputConstants;
*///?}

import java.util.HashSet;

public class MixinStatics {
    public static Slot CURRENT_SLOT;
    public static Slot DELETE_ITEM_SLOT;
    public static boolean temporarilyDisableInventorySounds = false;
    public static boolean hasOpenedInventorioScreen = false;
    public static final HashSet<Identifier> FOUND_SOUND_EVENTS = new HashSet<>();
    public static ContainerInput previousAction;

    public static boolean isNotSpecialKey(int keycode) {
        return switch (keycode) {
            //? if <26.3 {
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL, GLFW.GLFW_KEY_CAPS_LOCK, GLFW.GLFW_KEY_TAB, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_INSERT, GLFW.GLFW_KEY_DELETE, GLFW.GLFW_KEY_END, GLFW.GLFW_KEY_HOME, GLFW.GLFW_KEY_PAGE_DOWN, GLFW.GLFW_KEY_PAGE_UP, GLFW.GLFW_KEY_PAUSE, GLFW.GLFW_KEY_SCROLL_LOCK, GLFW.GLFW_KEY_PRINT_SCREEN, GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT, GLFW.GLFW_KEY_NUM_LOCK, GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_F1, GLFW.GLFW_KEY_F2, GLFW.GLFW_KEY_F3, GLFW.GLFW_KEY_F4, GLFW.GLFW_KEY_F5, GLFW.GLFW_KEY_F6, GLFW.GLFW_KEY_F7, GLFW.GLFW_KEY_F8, GLFW.GLFW_KEY_F9, GLFW.GLFW_KEY_F10, GLFW.GLFW_KEY_F11, GLFW.GLFW_KEY_F12 ->
            //?} else {
            /*case InputConstants.KEY_LSHIFT, InputConstants.KEY_RSHIFT, InputConstants.KEY_LCONTROL, InputConstants.KEY_RCONTROL, InputConstants.KEY_CAPSLOCK, InputConstants.KEY_TAB, InputConstants.KEY_RETURN, InputConstants.KEY_INSERT, InputConstants.KEY_DELETE, InputConstants.KEY_END, InputConstants.KEY_HOME, InputConstants.KEY_PAGEDOWN, InputConstants.KEY_PAGEUP, InputConstants.KEY_PAUSE, InputConstants.KEY_SCROLLLOCK, InputConstants.KEY_PRINTSCREEN, InputConstants.KEY_ESCAPE, InputConstants.KEY_LALT, InputConstants.KEY_RALT, InputConstants.KEY_NUMLOCK, InputConstants.KEYCODE_NUMPADENTER, InputConstants.KEY_F1, InputConstants.KEY_F2, InputConstants.KEY_F3, InputConstants.KEY_F4, InputConstants.KEY_F5, InputConstants.KEY_F6, InputConstants.KEY_F7, InputConstants.KEY_F8, InputConstants.KEY_F9, InputConstants.KEY_F10, InputConstants.KEY_F11, InputConstants.KEY_F12 ->
            *///?}
                    false;
            default -> true;
        };
    }
}
