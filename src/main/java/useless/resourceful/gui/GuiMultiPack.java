package useless.resourceful.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTexturedButton;
import net.minecraft.client.gui.options.GuiOptions;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPageRegistry;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.gui.popup.GuiPopup;
import net.minecraft.client.gui.popup.PopupBuilder;
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
	protected GuiTexturedButton refreshPackButton;
	private GuiButton pageButton;
	GuiButton pageLeftButton;
	GuiButton pageRightButton;
	private final OptionsPage selectedPage;
	protected static Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
	protected OptionsPage nextPage = null;
	public GuiMultiPack(GuiScreen parent) {
		super(parent);
		this.selectedPage = OptionsPages.TEXTURE_PACKS;
	}
	@Override
	public void init() {
		this.top = 44;
		this.bottom = this.height - 28;
		this.scrollRegionHeight = this.bottom - this.top;

		I18n i18n = I18n.getInstance();
		this.pageButton = new GuiButton(20, this.width / 2 - 70, 20, 140, 20, i18n.translateKey(this.selectedPage.getTranslationKey()));
		this.pageLeftButton = new GuiButton(21, this.width / 2 - 70 - 24, 20, 20, 20, "<");
		this.pageRightButton = new GuiButton(22, this.width / 2 + 70 + 4, 20, 20, 20, ">");
		this.controlList.add(this.pageButton);
		this.controlList.add(pageLeftButton);
		this.controlList.add(pageRightButton);
		GuiTexturedButton searchButton = new GuiTexturedButton(23, "/gui/gui.png", this.width - 20 - 4, 20, 20, 86, 20, 20);
		this.controlList.add(searchButton);

		refreshPackButton = new GuiTexturedButton(8, "/assets/resourceful/gui/packManager.png", (width + centerWidth)/2 + (width - centerWidth)/4 - 100, height - 24,100, 0, 20, 20);
		this.controlList.add(new GuiButton(1, refreshPackButton.getX() + refreshPackButton.getWidth() + 2, refreshPackButton.getY(), 200 - refreshPackButton.getWidth() - 2, 20, i18n.translateKey("gui.options.button.done")));
		this.controlList.add(new GuiButton(2, (width - centerWidth)/4 - 100, height - 24,200, 20, i18n.translateKey("gui.options.page.texture_packs.button.open_folder")));

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
		this.controlList.add(refreshPackButton);

		createButtons();
	}
	@Override
	public void onClosed(){
		TexturePackManager.refreshTextures(false);
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
		boolean isShifted = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		if (button.id == 1){
			mc.displayGuiScreen(this.getParentScreen());
		}
		if (button.id == 2){
			Utils.openDirectory(new File(mc.getMinecraftDir(), "texturepacks"));
		}
		if (button.id == 5){
			TexturePackManager.movePack(selectedTexturePackButton.texturePack, isShifted ? -9999 : -1);
			createButtons();
		}
		if (button.id == 6){
			TexturePackManager.movePack(selectedTexturePackButton.texturePack, isShifted ? 9999 : 1);
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
		if (button.id == 8){
			TexturePackManager.refreshTextures(true);
		}
		if (button.id == 20){
			String[] buttons = new String[OptionsPageRegistry.getInstance().getPages().size()];
			for (int i = 0; i < buttons.length; ++i) {
				buttons[i] = I18n.getInstance().translateKey(OptionsPageRegistry.getInstance().getPages().get(i).getTranslationKey());
			}
			GuiPopup popup = new PopupBuilder(this, 148).closeOnEsc(0).closeOnClickOut(0).withLabel("gui.options.label.select_page").withList("page", 142, buttons, null, OptionsPageRegistry.getInstance().getPageIndex(this.selectedPage), true).withOnCloseListener((statusCode, results) -> {
				if (statusCode != 0) {
					nextPage = OptionsPageRegistry.getInstance().getPages().get((Integer)results.get("page"));
				}
			}).build();
			mc.displayGuiScreen(popup);
		}
		if (button.id == 21){
			switchPage(-1);
		}
		if (button.id == 22){
			switchPage(1);
		}
		if (button.id == 23){
			mc.displayGuiScreen(new GuiOptions(getParentScreen(), mc.gameSettings, OptionsPages.SEARCH));
		}
	}
	private void switchPage(int offset){
		int numPages = OptionsPageRegistry.getInstance().getPages().size();
		int nextPage = OptionsPageRegistry.getInstance().getPageIndex(this.selectedPage) + offset;
		mc.displayGuiScreen(new GuiOptions(getParentScreen(), mc.gameSettings, offset < 0 ? OptionsPageRegistry.getInstance().getPages().get(nextPage < 0 ? numPages - 1 : nextPage) : OptionsPageRegistry.getInstance().getPages().get(nextPage >= numPages ? 0 : nextPage)));
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
		I18n i18n = I18n.getInstance();

		if (nextPage != null){
			mc.displayGuiScreen(new GuiOptions(getParentScreen(), mc.gameSettings, nextPage));
		}

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

		drawStringCentered(fontRenderer, i18n.translateKey("resourceful.pack.label.available"), pageLeftButton.getX()/2, pageLeftButton.getY() + pageLeftButton.getHeight() - fontRenderer.fontHeight, 0xFFFFFF);
		drawStringCentered(fontRenderer, i18n.translateKey("resourceful.pack.label.selected"),  width - ((width - pageRightButton.getX())/2), pageRightButton.getY() + pageRightButton.getHeight() - fontRenderer.fontHeight, 0xFFFFFF);
		super.drawScreen(mouseX, mouseY, partialTick);
		this.drawStringCentered(this.fontRenderer, i18n.translateKey("gui.options.title"), this.width / 2, 5, 0xFFFFFF);
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
