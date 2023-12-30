package useless.resourceful.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.ShortcutComponent;

public class RedirectComponent extends ShortcutComponent {
	private final Runnable action;
	private static long lastActivateTime = -1;
	public RedirectComponent(String translationKey, Runnable action) {
		super(translationKey, action);
		this.action = action;
	}
	public void init(Minecraft mc) {
		if (System.currentTimeMillis() - lastActivateTime > 1){
			lastActivateTime = System.currentTimeMillis();
			action.run();
		}
	}
	@Override
	public void render(int x, int y, int width, int relativeMouseX, int relativeMouseY) {
		action.run();
	}
}
