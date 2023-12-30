package useless.resourceful.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTexturedButton;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.helper.Utils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import useless.resourceful.TexturePackManager;
import useless.resourceful.gui.elements.GuiScrollbar;
import useless.resourceful.gui.elements.GuiTexturePackButton;
import useless.resourceful.mixin.GuiTexturedButtonAccessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiMultiPack extends GuiScreen {
	private final List<GuiTexturePackButton> packButtons = new ArrayList<>();
	private final List<GuiTexturePackButton> selectedPackButtons = new ArrayList<>();
	private int scrollRegionHeight;
	private int updateTickCount = 0;
	private int top;
	private int bottom;
	private final int sidePadding = 4;
	private final int centerWidth = 22;
	protected GuiScrollbar allPackBar;
	protected GuiScrollbar selectedPackBar;
	protected GuiTexturePackButton selectedTexturePackButton;
	protected TexturePack selectedTexturesPack;
	protected GuiTexturedButton moveUpButton;
	protected GuiTexturedButton moveDownButton;
	protected GuiTexturedButton togglePackButton;
	protected static Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
	public GuiMultiPack(GuiScreen parent) {
		super(parent);
	}
	@Override
	public void init() {
		this.top = 44;
		this.bottom = this.height - 28;
		this.scrollRegionHeight = this.bottom - this.top;
		this.controlList.add(new GuiButton(1, (width + centerWidth)/2 + (width - centerWidth)/4 - 100, height - 24,200, 20, I18n.getInstance().translateKey("gui.options.button.done")));
		this.controlList.add(new GuiButton(2, (width - centerWidth)/4 - 100, height - 24,200, 20, I18n.getInstance().translateKey("gui.options.page.texture_packs.button.open_folder")));

		allPackBar = new GuiScrollbar(3, (width - centerWidth)/2 - 8, top, 8,bottom - top, packButtons.size() * 35);
		selectedPackBar = new GuiScrollbar(4, width - 8, top, 8, bottom - top, selectedPackButtons.size() * 35);
		this.controlList.add(allPackBar);
		this.controlList.add(selectedPackBar);

		int xPos = width/2 - centerWidth/2 + (centerWidth-20)/2;
		moveUpButton = new GuiTexturedButton(5, "/assets/resourceful/gui/packManager.png", xPos, height/2 - 22,20, 0, 20, 20);
		moveDownButton = new GuiTexturedButton(6, "/assets/resourceful/gui/packManager.png", xPos, height/2,60, 0, 20, 20);
		togglePackButton = new GuiTexturedButton(7, "/assets/resourceful/gui/packManager.png", xPos, height/2 + 22,40, 0, 20, 20);
		togglePackButton.enabled = false;
		this.controlList.add(moveUpButton);
		this.controlList.add(moveDownButton);
		this.controlList.add(togglePackButton);
//		((GuiTexturedButtonAccessor)togglePackButton).setU(40);

		createButtons();
	}
	@Override
	public void tick() {
		doubleClickCounter--;
		++this.updateTickCount;
		if (this.updateTickCount >= 40) {
			this.updateTickCount = 0;
			if (mc.texturePackList.updateAvailableTexturePacks()) {
				this.createButtons();
			}
		}
	}

	public void createButtons() {
		if (selectedTexturePackButton != null){
			selectedTexturesPack = selectedTexturePackButton.texturePack;
		}
		selectedTexturePackButton = null;
		this.packButtons.clear();
		this.selectedPackButtons.clear();
		List<TexturePack> texturePacks = mc.texturePackList.availableTexturePacks();
		texturePacks.sort(Comparator.comparing(pack -> pack.fileName));

		for (int i = 0; i < TexturePackManager.selectedPacks.size(); ++i) {
			TexturePack texturePack = TexturePackManager.selectedPacks.get(i);
			GuiTexturePackButton button = new GuiTexturePackButton(i, (width + centerWidth)/2 + sidePadding, 0, width/2 - centerWidth/2 - (sidePadding * 2) - selectedPackBar.getWidth(), 32, texturePack);
			if (texturePack == selectedTexturesPack){
				selectedTexturePackButton = button;
			}
			this.selectedPackButtons.add(button);
		}
		selectedPackButtons.add(new GuiTexturePackButton(selectedPackButtons.size(), (width + centerWidth)/2 + sidePadding, 0, width/2 - centerWidth/2 - (sidePadding * 2) - selectedPackBar.getWidth(), 32, mc.texturePackList.getDefaultTexturePack()));

		for (int i = 0; i < texturePacks.size(); ++i) {
			TexturePack texturePack = texturePacks.get(i);
			if (texturePack == mc.texturePackList.getDefaultTexturePack()) continue;
			if (TexturePackManager.selectedPacks.contains(texturePack)) continue;
			GuiTexturePackButton button = new GuiTexturePackButton(i, sidePadding,(int) (top + 3 + i * 35 - allPackBar.getScrollAmount()), width/2 - centerWidth/2 - (sidePadding * 2) - allPackBar.getWidth(), 32, texturePack);
			if (texturePack == selectedTexturesPack){
				selectedTexturePackButton = button;
			}
			this.packButtons.add(button);
		}
		allPackBar.setScrollAreaHeight(packButtons.size() * 35);
		selectedPackBar.setScrollAreaHeight(selectedPackButtons.size() * 35);
	}
	@Override
	protected void buttonPressed(GuiButton button) {
		if (button.id == 1){
			mc.displayGuiScreen(this.getParentScreen());
		}
		if (button.id == 2){
			Utils.openDirectory(new File(mc.getMinecraftDir(), "texturepacks"));
		}
		if (button.id == 5){
			TexturePackManager.movePack(selectedTexturePackButton.texturePack, -1);
			createButtons();
		}
		if (button.id == 6){
			TexturePackManager.movePack(selectedTexturePackButton.texturePack, 1);
			createButtons();
		}
		if (button.id == 7){
			if (selectedPackButtons.contains(selectedTexturePackButton)){
				TexturePackManager.removePack(selectedTexturePackButton.texturePack);
			} else {
				TexturePackManager.addPack(selectedTexturePackButton.texturePack);
			}
			createButtons();
		}
	}
	private int doubleClickCounter = -1;
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			for (GuiButton guiButton : this.controlList) {
				if (!guiButton.mouseClicked(mc, mouseX, mouseY)) continue;
				if (guiButton instanceof GuiScrollbar){
					this.selectedButton = guiButton;
					if (guiButton.listener != null) {
						guiButton.listener.listen(guiButton);
					} else {
						this.buttonPressed(guiButton);
					}
					doubleClickCounter = 5;
					return;
				}
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiTexturePackButton button : this.packButtons) {
            if (!button.mouseClicked(mc, mouseX, mouseY)) continue;
			if (button.texturePack == mc.texturePackList.getDefaultTexturePack()) continue;
			selectedTexturePackButton = button;
			if (doubleClickCounter > 0){
				TexturePackManager.addPack(button.texturePack);
				createButtons();
			}
			doubleClickCounter = 5;
            return;
        }
        for (GuiTexturePackButton button : this.selectedPackButtons) {
            if (!button.mouseClicked(mc, mouseX, mouseY)) continue;
			if (button.texturePack == mc.texturePackList.getDefaultTexturePack()) continue;
			selectedTexturePackButton = button;
			if (doubleClickCounter > 0){
				TexturePackManager.removePack(button.texturePack);
				createButtons();
			}
			doubleClickCounter = 5;
            return;
        }
		doubleClickCounter = 5;
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		moveUpButton.enabled = false;
		moveDownButton.enabled = false;
		togglePackButton.enabled = false;
		if (selectedTexturePackButton != null){
			togglePackButton.enabled = true;
			((GuiTexturedButtonAccessor)togglePackButton).setU(40);
			if (selectedPackButtons.contains(selectedTexturePackButton)){
				moveUpButton.enabled = true;
				moveDownButton.enabled = true;
				((GuiTexturedButtonAccessor)togglePackButton).setU(80);
			}
		}

		scroll(mouseX, mouseY);

		drawDefaultBackground();

		drawStringCentered(fontRenderer, I18n.getInstance().translateKey("resourceful.pack.label.available"), (width - centerWidth)/4, 20, 0xFFFFFF);
		drawStringCentered(fontRenderer, I18n.getInstance().translateKey("resourceful.pack.label.selected"),  (width + centerWidth)/2 + ((width - centerWidth)/4), 20, 0xFFFFFF);
		super.drawScreen(mouseX, mouseY, partialTick);
		GL11.glEnable(3089);
		GL11.glScissor(0, (this.height - this.bottom) * mc.resolution.scale, this.width * mc.resolution.scale, this.scrollRegionHeight * mc.resolution.scale);
		if (this.packButtons.isEmpty()) {
			mc.fontRenderer.drawCenteredString(I18n.getInstance().translateKey("gui.options.page.texture_packs.label.no_packs"), (width - centerWidth)/4, top + 4, 0x5F7F7F7F);
		}
		for (int i = 0; i < this.packButtons.size(); ++i) {
			GuiTexturePackButton button = this.packButtons.get(i);
			button.setY((int) (top + 3 + i * 35 - allPackBar.getScrollAmount()));
			button.drawButton(mc, mouseX, mouseY);
			if (button == selectedTexturePackButton){
				drawBoxRoundButton(button);
			}
		}
		for (int i = 0; i < this.selectedPackButtons.size(); ++i) {
			GuiTexturePackButton button = this.selectedPackButtons.get(i);
			button.setY((int) (top + 3 + i * 35 - selectedPackBar.getScrollAmount()));
			button.drawButton(mc, mouseX, mouseY);
			if (button == selectedTexturePackButton){
				drawBoxRoundButton(button);
			}
		}
	}
	private void drawBoxRoundButton(GuiButton button){
		int color = 0xFFFFFFFF;
		int lineWidth = 1;
		int xMin = button.getX();
		int xMax = xMin + button.getWidth();
		int yMin = button.getY();
		int yMax = yMin + button.getHeight();
		drawRect(xMin + lineWidth, yMin + lineWidth, xMax - lineWidth, yMin, color); // Top
		drawRect(xMin + lineWidth, yMin, xMin, yMax - lineWidth, color); // Left
		drawRect(xMin, yMax, xMax - lineWidth, yMax - lineWidth, color); // Bottom
		drawRect(xMax, yMin, xMax - lineWidth, yMax, color); // Right
	}
	public void scroll(int mouseX, int mouseY){
		GuiScrollbar barToScroll = null;
		if (mouseY < bottom && mouseY > top){
			if (mouseX < (width/2 - centerWidth / 2)){
				barToScroll = allPackBar;
			} else if (mouseX > (width/2 + centerWidth / 2)) {
				barToScroll = selectedPackBar;
			}
		}

		if (barToScroll != null){
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
				barToScroll.scroll((float)Mouse.getDWheel() * -1.0f);
			} else {
				barToScroll.scroll((float)Mouse.getDWheel() / -5.0f);
			}
		}
	}
	@Override
	public void mouseMovedOrButtonReleased(int mouseX, int mouseY, int mouseButton) {
		super.mouseMovedOrButtonReleased(mouseX, mouseY, mouseButton);
	}
	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
		if (mc.theWorld == null) {
			Tessellator tessellator = Tessellator.instance;
			GL11.glBindTexture(3553, mc.renderEngine.getTexture("/gui/background.png"));
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			float f1 = 32.0f;
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(0x202020);
			// Left
			tessellator.addVertexWithUV(0.0, this.bottom, 0.0, 0.0, (float)(this.bottom + (int)allPackBar.getScrollAmount()) / f1);
			tessellator.addVertexWithUV((width - centerWidth)/2f, this.bottom, 0.0, ((width - centerWidth)/2f) / f1, (float)(this.bottom + (int)allPackBar.getScrollAmount()) / f1);
			tessellator.addVertexWithUV((width - centerWidth)/2f, this.top, 0.0, ((width - centerWidth)/2f) / f1, (float)(this.top + (int)allPackBar.getScrollAmount()) / f1);
			tessellator.addVertexWithUV(0.0, this.top, 0.0, 0.0, (float)(this.top + (int)allPackBar.getScrollAmount()) / f1);

			// Right
			tessellator.addVertexWithUV((width + centerWidth)/2f, this.bottom, 0.0, ((width + centerWidth)/2f) / f1, (float)(this.bottom + (int)selectedPackBar.getScrollAmount()) / f1);
			tessellator.addVertexWithUV(this.width, this.bottom, 0.0, (width) / f1, (float)(this.bottom + (int)selectedPackBar.getScrollAmount()) / f1);
			tessellator.addVertexWithUV(this.width, this.top, 0.0, (width) / f1, (float)(this.top + (int)selectedPackBar.getScrollAmount()) / f1);
			tessellator.addVertexWithUV((width + centerWidth)/2f, this.top, 0.0, ((width + centerWidth)/2f) / f1, (float)(this.top + (int)selectedPackBar.getScrollAmount()) / f1);

			tessellator.draw();
		} else {
			this.drawRect(0, 0, this.width, this.top, 0x5F000000);
			this.drawRect(0, this.bottom, this.width, this.height, 0x5F000000);
			this.drawRect((width - centerWidth)/2, this.bottom, (width + centerWidth)/2, this.top, 0x5F000000);
		}
	}
}
