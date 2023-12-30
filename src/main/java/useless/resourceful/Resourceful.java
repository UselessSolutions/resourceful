package useless.resourceful;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.ShortcutComponent;
import net.minecraft.client.gui.options.data.OptionsPages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import useless.resourceful.mixin.OptionsCategoryAccessor;


public class Resourceful implements ClientStartEntrypoint {
    public static final String MOD_ID = "resourceful";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		mc.texturePackList.selectedTexturePack = new TexturePackManager();
		OptionsPages.TEXTURE_PACKS.getComponents().removeIf(component -> component instanceof OptionsCategory && ((OptionsCategoryAccessor)component).getTranslationKey().equals("gui.options.page.texture_packs.category.texture_packs")); // Remove vanilla pack list component
		OptionsPages.TEXTURE_PACKS.withComponent(new ShortcutComponent("resourceful.pack.manager.open", () -> mc.displayGuiScreen(new GuiMultiPack(mc.currentScreen))));
		LOGGER.info("Resourceful initialized.");
	}
}
