package useless.resourceful.mixin;

import net.minecraft.client.gui.GuiTexturedButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiTexturedButton.class, remap = false)
public interface GuiTexturedButtonAccessor {
	@Accessor("u")
	void setU(int u);
	@Accessor("v")
	void setV(int v);
}
