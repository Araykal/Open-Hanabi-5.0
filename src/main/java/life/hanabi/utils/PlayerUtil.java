package life.hanabi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class PlayerUtil extends Utils {
    public static void tellPlayerWithPrefix(String message) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("\247b[Hanabi] \247r" + message));
    }
    public static void tellPlayerWithoutPrefix(String message) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));
    }
    public static int getSlotByItem(Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

            if (stack != null && stack.getItem() == item)
                return i;
        }

        return -1;
    }
}
