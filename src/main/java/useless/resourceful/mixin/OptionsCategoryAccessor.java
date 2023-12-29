package useless.resourceful.mixin;

import net.minecraft.client.gui.options.components.OptionsCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = OptionsCategory.class, remap = false)
public interface OptionsCategoryAccessor {
	@Accessor
	String getTranslationKey();
}
