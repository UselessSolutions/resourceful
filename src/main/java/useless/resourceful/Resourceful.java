package useless.resourceful;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class Resourceful implements ModInitializer, ClientStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "resourceful";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Resourceful initialized.");
    }


	@Override
	public void onRecipesReady() {

	}

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {
		Minecraft.getMinecraft(Minecraft.class).texturePackList.selectedTexturePack = new TexturePackManager();
	}
}
