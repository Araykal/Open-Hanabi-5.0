package life.hanabi.utils;

import life.hanabi.utils.render.BlurBuffer;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class StencilUtil {
    public static void start() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getFramebuffer().bindFramebuffer(false);
        if(mc.getFramebuffer().depthBuffer > -1){
            BlurBuffer.setupFBO(mc.getFramebuffer());
            mc.getFramebuffer().depthBuffer = -1;
        }
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false);
    }

    public static void end() {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);
    }

    public static void draw(Runnable start, Runnable end) {
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        Minecraft mc = Minecraft.getMinecraft();
        mc.getFramebuffer().bindFramebuffer(false);
        if(mc.getFramebuffer().depthBuffer > -1){
            BlurBuffer.setupFBO(mc.getFramebuffer());
            mc.getFramebuffer().depthBuffer = -1;
        }
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false);
        start.run();
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);
        end.run();
        GL11.glDisable(GL11.GL_STENCIL_TEST);


    }
}
