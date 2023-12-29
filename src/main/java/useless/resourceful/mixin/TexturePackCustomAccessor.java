package useless.resourceful.mixin;

import net.minecraft.client.render.texturepack.TexturePackCustom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;
import java.util.zip.ZipFile;

@Mixin(value = TexturePackCustom.class, remap = false)
public interface TexturePackCustomAccessor {
	@Accessor
	File getFile();
	@Accessor
	ZipFile getZipFile();
}
