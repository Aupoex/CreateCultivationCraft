package euphy.upo.create_cultivation.ponder;

import com.tterrag.registrate.util.entry.RegistryEntry;
import euphy.upo.create_cultivation.registry.CCBlocks;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import euphy.upo.create_cultivation.ponder.scenes.CultivationScenes;

import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.KINETIC_APPLIANCES;

public class CCPonderScenes {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<RegistryEntry<?, ?>> ENTRY_HELPER = helper.withKeyFunction(RegistryEntry::getId);

        ENTRY_HELPER.forComponents(CCBlocks.CULTIVATION_TANK)
                .addStoryBoard("cul", CultivationScenes::cultivating,KINETIC_APPLIANCES);

        ENTRY_HELPER.forComponents(CCBlocks.CULTIVATION_BASE)
                .addStoryBoard("cul", CultivationScenes::cultivating,KINETIC_APPLIANCES);
    }
}
