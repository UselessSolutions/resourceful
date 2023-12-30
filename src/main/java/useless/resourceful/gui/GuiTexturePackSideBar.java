package useless.resourceful.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTexturedButton;
import net.minecraft.client.gui.options.components.OptionsComponent;
import net.minecraft.client.gui.options.components.ShortcutComponent;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiTexturePackSideBar extends GuiScreen {
	protected GuiButton parentButton;
	private final int sidePadding = 4;
	public int xSize;
	public int ySize;
	private OptionsComponent clickedComponent;
	private int clickedComponentY;
	private final List<OptionsComponent> components;
	public GuiTexturePackSideBar(GuiScreen parent) {
		super(parent);
		components = new ArrayList<>(OptionsPages.TEXTURE_PACKS.getComponents());
		for(OptionsComponent component : OptionsPages.TEXTURE_PACKS.getComponents()){
			if (component instanceof ShortcutComponent){
				components.remove(component);
			}
		}
	}

	@Override
	public void init() {
		xSize = width/2;
		ySize = height;
		super.init();
		this.parentButton = new GuiTexturedButton(0, "/assets/resourceful/gui/packManager.png", sidePadding, 20, 120, 0, 20, 20);
		controlList.add(parentButton);
	}

	@Override
	protected void buttonPressed(GuiButton button) {
		if (button.id == 0){
			closeMenu();
		}
	}
	private void closeMenu(){
		mc.displayGuiScreen(getParentScreen());
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		this.getParentScreen().drawScreen(-1, -1, partialTick);
		int color = this.mc.gameSettings.guiBackgroundColor.value.getARGB();
		this.drawGradientRect(0, 0, this.width, this.height, color, color);

		Tessellator tessellator = Tessellator.instance;
		GL11.glBindTexture(3553, mc.renderEngine.getTexture("/gui/background.png"));
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		float f1 = 32.0f;
		tessellator.startDrawingQuads();

		tessellator.setColorOpaque_I(0x404040);
		// Left
		tessellator.addVertexWithUV(0.0, height, 0.0, 0.0, (float)(height) / f1);
		tessellator.addVertexWithUV(xSize, height, 0.0, xSize / f1, (float)(height) / f1);
		tessellator.addVertexWithUV(xSize, 0, 0.0, xSize / f1, 0);
		tessellator.addVertexWithUV(0.0, 0, 0.0, 0.0, 0);

		tessellator.draw();

		super.drawScreen(mouseX, mouseY, partialTick);
		drawPageItems(sidePadding, parentButton.getY() + parentButton.getHeight() + 8, xSize - sidePadding * 2, mouseX, mouseY);
		drawBox(0, 0, xSize, ySize, -6250336, 1);
	}
	private void drawPageItems(int x, int y, int width, int mouseX, int mouseY) {
		int y2 = y;
		for (OptionsComponent component : components) {
			component.render(x, y2, width, mouseX - x, mouseY - y2);
			y2 += component.getHeight();
		}
	}
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseX > xSize){
			closeMenu();
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
		int componentY = parentButton.getY() + parentButton.getHeight() + 8;
		for (OptionsComponent component : components) {
			if (mouseX >= 0 && mouseX <= xSize && mouseY >= componentY && mouseY <= componentY + component.getHeight()) {
				component.onMouseClick(mouseButton, 0, componentY, xSize, mouseX, mouseY - componentY);
				this.clickedComponent = component;
				this.clickedComponentY = componentY;
				break;
			}
			componentY += component.getHeight();
		}
	}
	@Override
	public void mouseMovedOrButtonReleased(int mouseX, int mouseY, int mouseButton) {
		super.mouseMovedOrButtonReleased(mouseX, mouseY, mouseButton);
		int left = 0;
		if (mouseY >= 0 && mouseY <= height && this.clickedComponent != null) {
			if (mouseButton >= 0) {
				this.clickedComponent.onMouseRelease(mouseButton, left, this.clickedComponentY, xSize, mouseX - left, mouseY - this.clickedComponentY);
				this.clickedComponent = null;
			} else {
				this.clickedComponent.onMouseMove(left, this.clickedComponentY, xSize, mouseX - left, mouseY - this.clickedComponentY);
			}
		}
	}
}
