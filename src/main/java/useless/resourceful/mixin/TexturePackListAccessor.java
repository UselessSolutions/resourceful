package useless.resourceful.mixin;

import net.minecraft.client.render.texturepack.TexturePackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value = TexturePackList.class, remap = false)
public interface TexturePackListAccessor {
	@Accessor("currentTexturePackName")
	String getCurrentTexturePackName();
	@Accessor("currentTexturePackName")
	void setCurrentTexturePackName(String newName);
}
