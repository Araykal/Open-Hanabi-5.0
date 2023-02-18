package life.hanabi.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import life.hanabi.utils.math.TimerUtil;

public class BlurBuffer {
	private static ShaderGroup blurShader;
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static Framebuffer buffer;
	private static float lastScale;
	private static int lastScaleWidth;
	private static int lastScaleHeight;
	private static final ResourceLocation shader = new ResourceLocation("shaders/post/blur.json");

	private static final TimerUtil updateTimer = new TimerUtil();

	public static void initFboAndShader() {
		if(mc.gameSettings.ofFastRender){
			return;
		}
		try {
			buffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
			buffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);

			blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), buffer, shader);
			blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void setShaderConfigs(float intensity, float blurWidth, float blurHeight) {
		blurShader.listShaders.get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
		blurShader.listShaders.get(1).getShaderManager().getShaderUniform("Radius").set(intensity);

		blurShader.listShaders.get(0).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
		blurShader.listShaders.get(1).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);
	}

	public static void blurArea(float x, float y, float width, float height, boolean setupOverlay) {
		ScaledResolution scale = new ScaledResolution(mc);
		float factor = scale.getScaleFactor();
		int factor2 = scale.getScaledWidth();
		int factor3 = scale.getScaledHeight();
		if(mc.gameSettings.ofFastRender){
			return;
		}
		if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
			initFboAndShader();
		}
		lastScale = factor;
		lastScaleWidth = factor2;
		lastScaleHeight = factor3;

		// 渲染
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glColorMask(false, false, false, false);
		GL11.glDepthMask(false);
		GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glStencilMask(0xFF);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		RenderUtil.drawRect(x, y, width, height, 0xff000000);
		GL11.glColorMask(true, true, true, true);
		GL11.glDepthMask(true);
		GL11.glStencilMask(0x00);
		GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);
		/* (nothing to draw) */
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
		buffer.framebufferRenderExt(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, true);
		GL11.glDisable(GL11.GL_STENCIL_TEST);

		if (setupOverlay) {
			mc.entityRenderer.setupOverlayRendering();
		}

		GlStateManager.enableDepth();
	}

	public static void blurRoundArea(float x, float y, float width, float height, float roundRadius, boolean setupOverlay) {
		ScaledResolution scale = new ScaledResolution(mc);
		float factor = scale.getScaleFactor();
		int factor2 = scale.getScaledWidth();
		int factor3 = scale.getScaledHeight();

		if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
			initFboAndShader();
		}

		lastScale = factor;
		lastScaleWidth = factor2;
		lastScaleHeight = factor3;

		// 渲染
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glColorMask(false, false, false, false);
		GL11.glDepthMask(false);
		GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);

		GL11.glStencilMask(0xFF);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
//		RenderUtil.drawRoundedRect(x, y, width, height, roundRadius, 0xff000000, 1, 0xff000000);

		RenderUtil.drawRect(x, y, width, height, 0xff000000);
		GL11.glColorMask(true, true, true, true);
		GL11.glDepthMask(true);
		GL11.glStencilMask(0x00);
		GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);
		/* (nothing to draw) */
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
		buffer.framebufferRenderExt(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, true);
		GL11.glDisable(GL11.GL_STENCIL_TEST);

		if (setupOverlay) {
			mc.entityRenderer.setupOverlayRendering();
		}

		GlStateManager.enableDepth();
	}

	public static void updateBlurBuffer(boolean setupOverlay) {
		// 以60帧每秒的速度更新 FrameBuffer
		if(mc.gameSettings.ofFastRender){
			return;
		}
		if (updateTimer.delay(1000 / 60f) && blurShader != null) {
			mc.getFramebuffer().unbindFramebuffer();

			setShaderConfigs(25, 0f, 1f);
			buffer.bindFramebuffer(true);

			mc.getFramebuffer().framebufferRenderExt(mc.displayWidth, mc.displayHeight, true);

			if(blurEnabled()) {
				GlStateManager.matrixMode(5890);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
				GlStateManager.popMatrix();
			}

			buffer.unbindFramebuffer();
			mc.getFramebuffer().bindFramebuffer(true);

			if(mc.getFramebuffer() != null && mc.getFramebuffer().depthBuffer > -1) {
				setupFBO(mc.getFramebuffer());
				mc.getFramebuffer().depthBuffer = -1;
			}

			if (setupOverlay) {
				mc.entityRenderer.setupOverlayRendering();
			}
		}
	}

	public static void setupFBO(Framebuffer fbo) {
		EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
		int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
		EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);
		EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);
		EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);
	}

	public static boolean blurEnabled() {
		if (OpenGlHelper.shadersSupported) {
			if(mc.theWorld != null && !(Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer)) {
				return false;
			}

			return true;
		}
		return false;
	}
}
