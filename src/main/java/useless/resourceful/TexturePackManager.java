package useless.resourceful;

import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackCustom;
import net.minecraft.client.render.texturepack.TexturePackDefault;
import useless.resourceful.mixin.TexturePackCustomAccessor;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

public class TexturePackManager extends TexturePack {
	public TexturePackManager() {
		super();
	}
	public static List<TexturePack> selectedPacks = new ArrayList<>();
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
}
