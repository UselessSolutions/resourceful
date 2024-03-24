package useless.resourceful.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.util.helper.Colors;
import net.minecraft.core.Global;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.sound.SoundCategory;
import org.lwjgl.opengl.GL11;
import useless.resourceful.TexturePackManager;

import java.util.ArrayList;
import java.util.List;

public class SelectedTexturePackListComponent implements OptionsComponent {
    protected static final int BUTTON_HEIGHT = 32;

    private final List<TexturePackButton> selectedPacks = new ArrayList<>();

    private int size;
	protected static final Minecraft mc = Minecraft.getMinecraft(SelectedTexturePackListComponent.class);
    TexturePackButton draggedButton;

    public SelectedTexturePackListComponent() {

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
        }
    }

    public void createTexturePackButtons() {
        selectedPacks.clear();

        TexturePackButton prevButton = new TexturePackButton(0, 3,false, mc.texturePackList.getDefaultTexturePack());
        selectedPacks.add(prevButton);

        List<TexturePack> texturePacks = TexturePackManager.selectedPacks;
        for (TexturePack texturePack : texturePacks) {
            prevButton = new TexturePackButton(prevButton.xPos, prevButton.yPos + (BUTTON_HEIGHT + 3), true, texturePack);
            selectedPacks.add(prevButton);
        }
    }

    @Override
    public int getHeight() {
        if (selectedPacks.isEmpty()) return 20;
        return 3 + selectedPacks.size() * (BUTTON_HEIGHT + 3);
    }

    @Override
    public void render(int x, int y, int width, int relativeMouseX, int relativeMouseY) {
        if (selectedPacks.isEmpty()) {
            mc.fontRenderer.drawCenteredString(I18n.getInstance().translateKey("gui.options.page.texture_packs.label.no_packs"), x + width / 2, y + 4, 0x5F7F7F7F);
        }
        for (TexturePackButton button : selectedPacks) {
            if (button == draggedButton) continue;
            int yOff = 0;
            if (button.draggable && draggedButton != null && relativeMouseX > 0 && relativeMouseX < width && relativeMouseY > 0 && relativeMouseY < getHeight()){
                int shiftAmount = draggedButton.shiftAmount(relativeMouseY);
                if (shiftAmount < 0){
                    if (button.yPos < draggedButton.yPos && button.yPos >= draggedButton.yPos + (shiftAmount * (BUTTON_HEIGHT + 3))){
                        yOff += (BUTTON_HEIGHT + 3);
                    }
                }
                if (shiftAmount > 0) {
                    if (button.yPos > draggedButton.yPos && button.yPos <= draggedButton.yPos + (shiftAmount * (BUTTON_HEIGHT + 3))){
                        yOff -= (BUTTON_HEIGHT + 3);
                    }
                }
            }
            button.render(this, x, y + yOff, width, relativeMouseX, relativeMouseY);
        }
        if (draggedButton != null){
            draggedButton.render(this,x, y, width, relativeMouseX, relativeMouseY);
        }
    }

    @Override
    public void onMouseClick(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
        for (TexturePackButton button : selectedPacks) {
            if (button.isClickable() && button.isHovered(relativeMouseX, relativeMouseY, width)) {
                if (button.onClick(this, mouseButton, x, y, width, relativeMouseX, relativeMouseY)) {
                    break;
                }
            }
        }
    }

    @Override
    public void onMouseMove(int x, int y, int width, int relativeMouseX, int relativeMouseY) { }

    @Override
    public void onMouseRelease(int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
        for (TexturePackButton button : selectedPacks) {
            if (button.onRelease(this, mouseButton, x, y, width, relativeMouseX, relativeMouseY)){
                break;
            }
        }
    }

    @Override
    public void onKeyPress(int keyCode, char character) { }

    @Override
    public boolean matchesSearchTerm(String term) {
        return false;
    }

    private static class TexturePackButton {
        private GuiButton button;
        public boolean draggable;
        public final TexturePack texturePack;
        public final int height = BUTTON_HEIGHT;
        public int xPos;
        public int yPos;
        private int clickX = -1;
        private int clickY = -1;
        public TexturePackButton(int xPos, int yPos, boolean draggable, TexturePack texturePack) {
            this.draggable = draggable;
            this.texturePack = texturePack;
            this.xPos = xPos;
            this.yPos = yPos;
            button = new GuiButton(0, 0, 0, 20, 20, "-");
        }

        public boolean isClickable() {
            return true;
        }
        public boolean isHovered(int mouseX, int mouseY, int width){
            if (isDragged()){
                int dX = mouseX - clickX;
                int dY = mouseY - clickY;
                mouseX -= dX;
                mouseY -= dY;
            }
            return mouseX >= 0 && mouseX <= width && mouseY >= yPos && mouseY <= yPos + height;
        }
        public boolean isDragged(){
            return clickX > 0 || clickY > 0;
        }
        public boolean onClick(SelectedTexturePackListComponent component, int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY){
            setupButton(x, y, width, relativeMouseX, relativeMouseY);
            if (texturePack != mc.texturePackList.getDefaultTexturePack()){
                if (mouseButton == 0 && button.isHovered(x + relativeMouseX + 8, y + relativeMouseY)){ // I have no idea why I'd need to offset the x position with +8, but it makes it work better ¯\_(ツ)_/¯
                    unsetPack(component);
                    mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
                    component.draggedButton = null;
                    return true;
                }
                if (mouseButton == 1){
                    unsetPack(component);
                    mc.sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
                    return true;
                }
            }
            if (!draggable) return false;
            clickX = relativeMouseX;
            clickY = relativeMouseY;
            component.draggedButton = this;
            return true;
        }
        public boolean onRelease(SelectedTexturePackListComponent component, int mouseButton, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
            if (isDragged()){
                if (relativeMouseX < 0 || relativeMouseX > x + width || relativeMouseY < 0 || relativeMouseY > y + component.getHeight()){
                    unsetPack(component);
                    return true;
                }
				TexturePackManager.movePack(texturePack, shiftAmount(relativeMouseY));
                clickX = -1;
                clickY = -1;
                component.draggedButton = null;
                component.createTexturePackButtons();
                return true;
            }
            clickX = -1;
            clickY = -1;
            return false;
        }
        public void unsetPack(SelectedTexturePackListComponent component){
			TexturePackManager.removePack(texturePack);
            component.draggedButton = null;
            component.createTexturePackButtons();
        }
        public int draggedX(int mouseX){
            return mouseX - clickX;
        }
        public int draggedY(int mouseY){
            return mouseY - clickY;
        }
        public int shiftAmount(int mouseY){
            return draggedY(mouseY)/BUTTON_HEIGHT;
        }

        public void render(SelectedTexturePackListComponent component, int x, int y, int width, int mouseX, int mouseY) {
            Tessellator tessellator = Tessellator.instance;
            FontRenderer fontRenderer = mc.fontRenderer;

            setupButton(x, y, width, mouseX, mouseY);

            if (isDragged()){
                int dX = draggedX(mouseX);
                int dY = draggedY(mouseY);
                x += dX;
                y += dY;
            }

            if (isDragged() || isHovered(mouseX, mouseY, width) && component.draggedButton == null) {
                // Draw background
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                tessellator.startDrawingQuads();

                tessellator.setColorOpaque_I(0x7F808080);
                tessellator.addVertexWithUV(x + xPos - 2,           y + yPos + height + 2, 0, 0, 1);
                tessellator.addVertexWithUV(x + xPos + width + 2,   y + yPos + height + 2, 0, 1, 1);
                tessellator.addVertexWithUV(x + xPos + width + 2,   y + yPos - 2, 0, 1, 1);
                tessellator.addVertexWithUV(x + xPos - 2,           y + yPos - 2, 0, 0, 0);

                tessellator.setColorOpaque_I(0);
                tessellator.addVertexWithUV(x + xPos - 1,           y + yPos + height + 1, 0, 0, 1);
                tessellator.addVertexWithUV(x + xPos + width + 1,   y + yPos + height + 1, 0, 1, 1);
                tessellator.addVertexWithUV(x + xPos + width + 1,   y + yPos - 1, 0, 1, 1);
                tessellator.addVertexWithUV(x + xPos - 1,           y + yPos - 1, 0, 0, 0);

                tessellator.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            // Draw texture pack info
            texturePack.bindThumbnailTexture(mc);
            GL11.glColor4f(1, 1, 1, 1);

            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(0xFFFFFF);
            tessellator.addVertexWithUV(x + xPos,           y + yPos + height, 0, 0, 1);
            tessellator.addVertexWithUV(x + xPos + height,  y + yPos + height, 0, 1, 1);
            tessellator.addVertexWithUV(x + xPos + height,  y + yPos, 0, 1, 0);
            tessellator.addVertexWithUV(x + xPos,           y + yPos, 0, 0, 0);
            tessellator.draw();

            int rgbVersion = 0x808080;
            int format = texturePack.manifest.getFormat();

            if(format == Global.TEXTURE_PACK_FORMAT) {
                // Correct version
                fontRenderer.drawString(texturePack.manifest.getName(),             x + xPos + height + 2, y + yPos + 1, 0xFFFFFF);
                fontRenderer.drawString(texturePack.manifest.getDescriptionLine1(), x + xPos + height + 2, y + yPos + 12, 0x808080);
                fontRenderer.drawString(texturePack.manifest.getDescriptionLine2(), x + xPos + height + 2, y + yPos + 22, 0x808080);
            }else if(format == -1) {
                // Manifest error
                fontRenderer.drawString(TextFormatting.RED + texturePack.manifest.getName(), x + xPos + height + 2, y + yPos + 1, 0xFFFFFF);
                fontRenderer.drawString(texturePack.manifest.getDescriptionLine1(),          x + xPos + height + 2, y + yPos + 12, 0x808080);
                fontRenderer.drawString(texturePack.manifest.getDescriptionLine2(),          x + xPos + height + 2, y + yPos + 22, 0x808080);
                rgbVersion = Colors.allChatColors[TextFormatting.RED.id].getARGB();
            }else {
                // Wrong texture pack version
                I18n i18n = I18n.getInstance();
                fontRenderer.drawString(TextFormatting.RED + texturePack.manifest.getName(),                            x + xPos + height + 2, y + yPos + 1, 0xFFFFFF);
                fontRenderer.drawString(i18n.translateKey("gui.options.page.texture_packs.label.outdated_pack.1"),  x + xPos + height + 2, y + yPos + 12, 0x808080);
                fontRenderer.drawString(i18n.translateKey("gui.options.page.texture_packs.label.outdated_pack.2"),  x + xPos + height + 2, y + yPos + 22, 0x808080);
                rgbVersion = Colors.allChatColors[TextFormatting.RED.id].getARGB();
            }

            if (isHovered(mouseX, mouseY, width) && component.draggedButton == null && texturePack != mc.texturePackList.getDefaultTexturePack()){
                button.drawButton(mc, mouseX + x, mouseY + y);
                x -= button.width + 3;
            }

            String version = texturePack.manifest.getPackVersion();
            int versionWidth = fontRenderer.getStringWidth(version);
            fontRenderer.drawString(texturePack.manifest.getPackVersion(), x + xPos + width - versionWidth - 2, y + yPos + 1, rgbVersion);
        }
        public void setupButton(int x, int y, int width, int mouseX, int mouseY){
            button.setX(x + xPos + width - button.width - 1);
            button.setY(y + yPos + (height - button.height)/2);
        }
    }
}
