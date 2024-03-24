package useless.resourceful.mixin;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.options.GuiOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import useless.resourceful.TexturePackManager;

@Mixin(value = GuiOptions.class, remap = false)
public class GuiOptionsMixin {
	@Inject(method = "buttonPressed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V", shift = At.Shift.AFTER))
	private void refreshOnLeave(GuiButton guibutton, CallbackInfo ci){
		TexturePackManager.refreshTextures(false);
	}
	@Inject(method = "mouseClicked(III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;playSound(Ljava/lang/String;Lnet/minecraft/core/sound/SoundCategory;FF)V", shift = At.Shift.AFTER))
	private void refreshWhenPageChanged(int mouseX, int mouseY, int mouseButton, CallbackInfo ci){
		TexturePackManager.refreshTextures(false);
	}
}
