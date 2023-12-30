package useless.resourceful.mixin;

import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import useless.resourceful.TexturePackManager;

import java.util.List;
import java.util.Objects;

@Mixin(value = TexturePackList.class, remap = false)
public abstract class TexturePackListMixin {
	@Shadow
	@Final
	private TexturePack defaultTexturePack;
	@Shadow
	public TexturePack selectedTexturePack;

	@Shadow
	private List<TexturePack> availableTexturePacks;

	@Inject(method = "setTexturePack(Lnet/minecraft/client/render/texturepack/TexturePack;)V", at = @At("HEAD"), cancellable = true)
	private void customTexturepackBehavior(TexturePack newPack, CallbackInfo ci){
		selectedTexturePack = new TexturePackManager();
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
			TexturePackManager.removePack(newPack);
			ci.cancel();
			return;
		}
		if (newPack != defaultTexturePack && !TexturePackManager.selectedPacks.contains(newPack)){
			TexturePackManager.addPack(newPack);
			ci.cancel();
			return;
		}
		ci.cancel();
	}
	@Inject(method = "updateAvailableTexturePacks()Z", at = @At("RETURN"))
	private void neverUnsetSelected(CallbackInfoReturnable<Boolean> cir){
		selectedTexturePack = new TexturePackManager();
		TexturePackManager.loadPacksFromString(availableTexturePacks, (TexturePackList)(Object) this);
	}
}
