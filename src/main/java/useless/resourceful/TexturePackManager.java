package useless.resourceful;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.texturepack.Manifest;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackCustom;
import net.minecraft.client.render.texturepack.TexturePackDefault;
import net.minecraft.client.render.texturepack.TexturePackList;
import useless.resourceful.mixin.TexturePackCustomAccessor;
import useless.resourceful.mixin.TexturePackListAccessor;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;

public class TexturePackManager extends TexturePack {
	public static Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
	public static List<TexturePack> selectedPacks = new ArrayList<>();
	public TexturePackManager() {
		this.fileName = "Manager";
		this.manifest = new Manifest(null, Objects.requireNonNull(TexturePackDefault.class.getResourceAsStream("/manifest.json")));
	}
	public static void movePack(TexturePack pack, int offset){
		if (!selectedPacks.contains(pack)) return;
		int currentIndex = selectedPacks.indexOf(pack);
		int newIndex = currentIndex + offset;
		if (newIndex < 0){
			newIndex = 0;
		}
		if (newIndex >= selectedPacks.size()){
			newIndex = selectedPacks.size();
		}
		selectedPacks.remove(pack);
		selectedPacks.add(newIndex, pack);
		refreshTextures();
	}

	public static void addPack(TexturePack pack){
		TexturePackManager.selectedPacks.add(0,pack);
		pack.readZipFile();
		refreshTextures();
	}
	public static void removePack(TexturePack pack){
		selectedPacks.remove(pack);
		pack.closeTexturePackFile();
		refreshTextures();
	}
	public static void refreshTextures(){
		((TexturePackListAccessor)mc.texturePackList).setCurrentTexturePackName(getPackCollectionString());
		mc.gameSettings.skin.value = ((TexturePackListAccessor)mc.texturePackList).getCurrentTexturePackName();
		mc.gameSettings.saveOptions();
		mc.fontRenderer = new FontRenderer(mc.gameSettings, "/font/default.png", mc.renderEngine);
		mc.renderEngine.refreshTexturesAndDisplayErrors();
		mc.renderGlobal.loadRenderers();
		mc.currentScreen.refreshFontRenderer();
		mc.renderEngine.updateDynamicTextures();
	}
	@Override
	public boolean hasFile(String string) {
		for (TexturePack pack : selectedPacks){
			if (pack.hasFile(string)){
				return true;
			}
		}
		try {
			InputStream stream = TexturePackDefault.class.getResourceAsStream(string);
			boolean a = stream != null;
            if (a) {
				stream.close();
			}
			return a;
		} catch (Exception e) {
			return false;
		}
	}
	public InputStream getResourceAsStream(String s) {
		InputStream in;
		for (TexturePack pack : selectedPacks){
			if (pack instanceof TexturePackCustom){
				in = customPackStream((TexturePackCustomAccessor)pack, s);
				if (in != null){
					return in;
				}
			}

		}
		return TexturePack.class.getResourceAsStream(s);
	}
	public InputStream customPackStream(TexturePackCustomAccessor custom, String path){
		block8: {
			if (custom.getFile().isFile()) {
				String zipEntryPath = path;
				if (path.startsWith("/")) {
					zipEntryPath = path.substring(1);
				}
				try {
					ZipEntry zipentry = custom.getZipFile().getEntry(zipEntryPath);
					if (zipentry != null) {
						return custom.getZipFile().getInputStream(zipentry);
					}
					break block8;
				} catch (Exception ignored) {
					return null;
				}
			}
			try {
				File file = new File(custom.getFile(), path);
				if (file.exists()) {
					return Files.newInputStream(file.toPath());
				}
			} catch (Exception ignored) {
				return null;
			}
		}
		return null;
	}
	public static String getPackCollectionString(){
		String[] names = new String[selectedPacks.size()];
		for (int i = 0; i < names.length; i++){
			names[i] = selectedPacks.get(i).fileName;
		}
		return String.join("-splitter-",names);
	}
	public static void loadPacksFromString(List<TexturePack> availablePacks, TexturePackList texturePackList){
		selectedPacks.clear();
		String[] names = ((TexturePackListAccessor)texturePackList).getCurrentTexturePackName().split("-splitter-");
		for (String name : names){
			for (TexturePack pack : availablePacks){
				if (pack.fileName.equals(name) && !selectedPacks.contains(pack) && !pack.fileName.equals("default") && !(pack instanceof TexturePackDefault)){
					selectedPacks.add(pack);
				}
			}
		}

	}
}
