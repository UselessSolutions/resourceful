package useless.resourceful;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.components.ShortcutComponent;
import net.minecraft.client.gui.options.components.ToggleableOptionComponent;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import useless.resourceful.gui.components.AvailableTexturePackListComponent;
import useless.resourceful.gui.components.SelectedTexturePackListComponent;
import useless.resourceful.mixin.OptionsCategoryAccessor;

import java.io.File;


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
		OptionsPages.TEXTURE_PACKS.withComponent(new ShortcutComponent("gui.options.page.texture_packs.button.apply", () -> TexturePackManager.refreshTextures(false)))
			.withComponent(new OptionsCategory("gui.options.page.texture_packs.category.texture_packs.selected")
				.withComponent(new SelectedTexturePackListComponent())
			)
			.withComponent(new OptionsCategory("gui.options.page.texture_packs.category.texture_packs.available")
				.withComponent(new AvailableTexturePackListComponent())
			);
		LOGGER.info("Resourceful initialized.");
	}
}
