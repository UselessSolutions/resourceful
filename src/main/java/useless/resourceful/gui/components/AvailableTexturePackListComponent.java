package useless.resourceful.gui.components;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.render.Tessellator;
import net.minecraft.core.sound.SoundCategory;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.util.helper.Colors;
import net.minecraft.core.Global;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import useless.resourceful.TexturePackManager;

public class AvailableTexturePackListComponent
    implements OptionsComponent {
    private static final int BUTTON_HEIGHT = 32;

    private final List<TexturePackButton> availablePacks = new ArrayList<>();

    private int updateTickCount = 0;
    private int size;

	protected static final Minecraft mc = Minecraft.getMinecraft(SelectedTexturePackListComponent.class);
    public AvailableTexturePackListComponent() {

    }

    @Override
    public void init(Minecraft mc) {
    	createTexturePackButtons();
    }

    @Override
    public void tick() {
        if (size != TexturePackManager.selectedPacks.size()){
            size = TexturePackManager.selectedPacks.size();
            createTexturePackButtons();
            return;
        }
    	updateTickCount++;
    	if(updateTickCount >= 40) {
    		updateTickCount = 0;
			if (mc.texturePackList.updateAvailableTexturePacks()){
				createTexturePackButtons();
			}
    	}
    }

    public void createTexturePackButtons() {
        availablePacks.clear();

        List<TexturePack> texturePacks = mc.texturePackList.availableTexturePacks();
        for (int i = 0; i < texturePacks.size(); i++) {
            TexturePack texturePack = texturePacks.get(i);
            if (TexturePackManager.selectedPacks.contains(texturePack)) continue;
            if (texturePack == mc.texturePackList.getDefaultTexturePack()) continue;
            availablePacks.add(new TexturePackButton(i, texturePack));
        }
    }

    @Override
    public int getHeight() {
        if (availablePacks.isEmpty()) return 20;
        return 3 + availablePacks.size() * (BUTTON_HEIGHT + 3);
    }

    @Override
    public void render(int x, int y, int width, int relativeMouseX, int relativeMouseY) {
        if (availablePacks.isEmpty()) {
            mc.fontRenderer.drawCenteredString(I18n.getInstance().translateKey("gui.options.page.texture_packs.label.no_packs"), x + width / 2, y + 4, 0x5F7F7F7F);
        }
        for (int i = 0; i < availablePacks.size(); i++) {
            TexturePackButton button = availablePacks.get(i);
            button.render(x, 3 + y + i * (BUTTON_HEIGHT + 3), width, relativeMouseX, relativeMouseY,
                    relativeMouseX >= 0 && relativeMouseX <= width && relativeMouseY >= 3 + i * (BUTTON_HEIGHT + 3) && relativeMouseY <= 3 + i * (BUTTON_HEIGHT + 3) + BUTTON_HEIGHT);
        }
    }

    @Override
    public void onMouseClick(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
        for (int i = 0; i < availablePacks.size(); i++) {
            TexturePackButton button = availablePacks.get(i);
            if (button.isClickable() && relativeMouseX >= 0 && relativeMouseX <= width && relativeMouseY >= 3 + i * (BUTTON_HEIGHT + 3) && relativeMouseY <= 3 + i * (BUTTON_HEIGHT + 3) + BUTTON_HEIGHT) {
            	mc.texturePackList.setTexturePack(button.texturePack);
                createTexturePackButtons();
                mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
                break;
            }
        }
    }

    @Override
    public void onMouseMove(int x, int y, int width, int relativeMouseX, int relativeMouseY) { }

    @Override
    public void onMouseRelease(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) { }

    @Override
    public void onKeyPress(int keyCode, char character) { }

    @Override
    public boolean matchesSearchTerm(String term) {
        return false;
    }

    private static class TexturePackButton {
        private GuiButton button;
        public final int index;
        public final TexturePack texturePack;
        public final int height = BUTTON_HEIGHT;

        public TexturePackButton(int index, TexturePack texturePack) {
            this.index = index;
            this.texturePack = texturePack;
            button = new GuiButton(0, 0, 0, 20, 20, "+");
        }

        public boolean isClickable() {
            return true;
        }

        public void render(int x, int y, int width, int mouseX, int mouseY, boolean hovered) {
            Tessellator tessellator = Tessellator.instance;
            FontRenderer fontRenderer = mc.fontRenderer;

            setupButton(x, y, width, mouseX, mouseY);

            if (hovered) {
                // Draw background
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                tessellator.startDrawingQuads();

                tessellator.setColorOpaque_I(0x7F808080);
                tessellator.addVertexWithUV(x - 2, y + height + 2, 0, 0, 1);
                tessellator.addVertexWithUV(x + width + 2, y + height + 2, 0, 1, 1);
                tessellator.addVertexWithUV(x + width + 2, y - 2, 0, 1, 1);
                tessellator.addVertexWithUV(x - 2, y - 2, 0, 0, 0);

                tessellator.setColorOpaque_I(0);
                tessellator.addVertexWithUV(x - 1, y + height + 1, 0, 0, 1);
                tessellator.addVertexWithUV(x + width + 1, y + height + 1, 0, 1, 1);
                tessellator.addVertexWithUV(x + width + 1, y - 1, 0, 1, 1);
                tessellator.addVertexWithUV(x - 1, y - 1, 0, 0, 0);

                tessellator.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            // Draw texture pack info
            texturePack.bindThumbnailTexture(mc);
            GL11.glColor4f(1, 1, 1, 1);

            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(0xFFFFFF);
            tessellator.addVertexWithUV(x, y + height, 0, 0, 1);
            tessellator.addVertexWithUV(x + height, y + height, 0, 1, 1);
            tessellator.addVertexWithUV(x + height, y, 0, 1, 0);
            tessellator.addVertexWithUV(x, y, 0, 0, 0);
            tessellator.draw();

            int rgbVersion = 0x808080;
            int format = texturePack.manifest.getFormat();

			if(format == Global.TEXTURE_PACK_FORMAT) {
				// Correct version
				fontRenderer.drawString(texturePack.manifest.getName(), x + height + 2, y + 1, 0xFFFFFF);
				fontRenderer.drawString(texturePack.manifest.getDescriptionLine1(), x + height + 2, y + 12, 0x808080);
				fontRenderer.drawString(texturePack.manifest.getDescriptionLine2(), x + height + 2, y + 22, 0x808080);
			}else if(format == -1) {
				// Manifest error
				fontRenderer.drawString(TextFormatting.RED + texturePack.manifest.getName(), x + height + 2, y + 1, 0xFFFFFF);
				fontRenderer.drawString(texturePack.manifest.getDescriptionLine1(), x + height + 2, y + 12, 0x808080);
				fontRenderer.drawString(texturePack.manifest.getDescriptionLine2(), x + height + 2, y + 22, 0x808080);
				rgbVersion = Colors.allChatColors[TextFormatting.RED.id].getARGB();
			}else {
				// Wrong texture pack version
				I18n i18n = I18n.getInstance();
				fontRenderer.drawString(TextFormatting.RED + texturePack.manifest.getName(), x + height + 2, y + 1, 0xFFFFFF);
				fontRenderer.drawString(i18n.translateKey("gui.options.page.texture_packs.label.outdated_pack.1"), x + height + 2, y + 12, 0x808080);
				fontRenderer.drawString(i18n.translateKey("gui.options.page.texture_packs.label.outdated_pack.2"), x + height + 2, y + 22, 0x808080);
				rgbVersion = Colors.allChatColors[TextFormatting.RED.id].getARGB();
			}

            if (hovered){
                button.drawButton(mc, mouseX + x, mouseY + y);
                x -= button.width + 3;
            }

            String version = texturePack.manifest.getPackVersion();
            int versionWidth = fontRenderer.getStringWidth(version);
            fontRenderer.drawString(texturePack.manifest.getPackVersion(), x + width - versionWidth - 2, y + 1, rgbVersion);
        }
        public void setupButton(int x, int y, int width, int mouseX, int mouseY){
            button.setX(x + width - button.width - 1);
            button.setY(y + (height - button.height)/2);
        }
    }
}
