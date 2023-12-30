package useless.resourceful.mixin;

import net.minecraft.client.render.texturepack.TexturePackCustom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

@Mixin(value = TexturePackCustom.class, remap = false)
public abstract class TexturePackCustomMixin {
	@Shadow
	private BufferedImage thumbnailBuffer;
	@Shadow
	public abstract InputStream getResourceAsStream(String path);

	@Inject(method = "readTexturePackManifest()V", at = @At("TAIL"))
	private void fixThumbnail(CallbackInfo ci){
		try {
			InputStream inputstream = getResourceAsStream("pack.png");

			try {
				this.thumbnailBuffer = ImageIO.read(inputstream);
			} catch (Throwable var8) {
				try {
					inputstream.close();
				} catch (Throwable var6) {
					var8.addSuppressed(var6);
				}

				throw var8;
			}

			inputstream.close();
		} catch (Exception ignored) {
		}
	}
}
