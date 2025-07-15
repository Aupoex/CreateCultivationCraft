package euphy.upo.create_cultivation.ponder;

import euphy.upo.create_cultivation.CreateCultivationCraft;
import euphy.upo.create_cultivation.registry.CCCreativeModeTabs;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CCPonderPlugin implements PonderPlugin {
    @Override
    public @NotNull String getModId() { return CreateCultivationCraft.MODID; }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CCPonderScenes.register(helper);
    }
    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        CCPonderTags.register(helper);
    }
}
