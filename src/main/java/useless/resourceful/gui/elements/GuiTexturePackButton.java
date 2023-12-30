package useless.resourceful.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.util.helper.Colors;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import org.lwjgl.opengl.GL11;

public class GuiTexturePackButton extends GuiButton {
	public TexturePack texturePack;
	public GuiTexturePackButton(int id, int xPosition, int yPosition, int width, int height, TexturePack texturePack) {
		super(id, xPosition, yPosition, width, height, "");
		this.texturePack = texturePack;
	}
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		Tessellator tessellator = Tessellator.instance;
		FontRenderer fontRenderer = mc.fontRenderer;
		if (mc.texturePackList.selectedTexturePack == this.texturePack) {
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glDisable(3553);
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(2139127936);
			tessellator.addVertexWithUV(xPosition - 2, yPosition + 32 + 2, 0.0, 0.0, 1.0);
			tessellator.addVertexWithUV(xPosition + width + 2, yPosition + 32 + 2, 0.0, 1.0, 1.0);
			tessellator.addVertexWithUV(xPosition + width + 2, yPosition - 2, 0.0, 1.0, 1.0);
			tessellator.addVertexWithUV(xPosition - 2, yPosition - 2, 0.0, 0.0, 0.0);
			tessellator.setColorOpaque_I(0);
			tessellator.addVertexWithUV(xPosition - 1, yPosition + 32 + 1, 0.0, 0.0, 1.0);
			tessellator.addVertexWithUV(xPosition + width + 1, yPosition + 32 + 1, 0.0, 1.0, 1.0);
			tessellator.addVertexWithUV(xPosition + width + 1, yPosition - 1, 0.0, 1.0, 1.0);
			tessellator.addVertexWithUV(xPosition - 1, yPosition - 1, 0.0, 0.0, 0.0);
			tessellator.draw();
			GL11.glEnable(3553);
		}
		this.texturePack.bindThumbnailTexture(mc);

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(0xFFFFFF);
		tessellator.addVertexWithUV(xPosition, yPosition + 32, 0.0, 0.0, 1.0);
		tessellator.addVertexWithUV(xPosition + 32, yPosition + 32, 0.0, 1.0, 1.0);
		tessellator.addVertexWithUV(xPosition + 32, yPosition, 0.0, 1.0, 0.0);
		tessellator.addVertexWithUV(xPosition, yPosition, 0.0, 0.0, 0.0);
		tessellator.draw();
		int rgbVersion = 0x808080;
		int format = this.texturePack.manifest.getFormat();
		if (format == 1) {
			fontRenderer.drawString(this.texturePack.manifest.getName(), xPosition + 32 + 2, yPosition + 1, 0xFFFFFF);
			fontRenderer.drawString(this.texturePack.manifest.getDescriptionLine1(), xPosition + 32 + 2, yPosition + 12, 0x808080);
			fontRenderer.drawString(this.texturePack.manifest.getDescriptionLine2(), xPosition + 32 + 2, yPosition + 22, 0x808080);
		} else if (format == -1) {
			fontRenderer.drawString(TextFormatting.RED + this.texturePack.manifest.getName(), xPosition + 32 + 2, yPosition + 1, 0xFFFFFF);
			fontRenderer.drawString(this.texturePack.manifest.getDescriptionLine1(), xPosition + 32 + 2, yPosition + 12, 0x808080);
			fontRenderer.drawString(this.texturePack.manifest.getDescriptionLine2(), xPosition + 32 + 2, yPosition + 22, 0x808080);
			rgbVersion = Colors.allChatColors[TextFormatting.RED.id].getARGB();
		} else {
			I18n i18n = I18n.getInstance();
			fontRenderer.drawString(TextFormatting.RED + this.texturePack.manifest.getName(), xPosition + 32 + 2, yPosition + 1, 0xFFFFFF);
			fontRenderer.drawString(i18n.translateKey("gui.options.page.texture_packs.label.outdated_pack.1"), xPosition + 32 + 2, yPosition + 12, 0x808080);
//				fontRenderer.drawString(i18n.translateKey("gui.options.page.texture_packs.label.outdated_pack.2"), xPosition + 32 + 2, y + 22, 0x808080);
			rgbVersion = Colors.allChatColors[TextFormatting.RED.id].getARGB();
		}
		String version = this.texturePack.manifest.getPackVersion();
		int versionWidth = fontRenderer.getStringWidth(version);
		fontRenderer.drawString(this.texturePack.manifest.getPackVersion(), xPosition + width - versionWidth - 2, yPosition + 1, rgbVersion);
	}
}
