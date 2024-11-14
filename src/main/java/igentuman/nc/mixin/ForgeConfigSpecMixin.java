package igentuman.nc.mixin;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@Mixin(ForgeConfigSpec.class)
public abstract class ForgeConfigSpecMixin {

    @Inject(method = "isCorrect*", at = @At("HEAD"), remap = false, cancellable = true)
    private void isCorrect(CommentedConfig config, CallbackInfoReturnable<Boolean> cir) {
        String file = ((CommentedFileConfig) config).getFile().toString();
        if(file.contains("NuclearCraft/materials.toml")) {
           // cir.setReturnValue(true);
           // cir.cancel();
        }
    }
}
