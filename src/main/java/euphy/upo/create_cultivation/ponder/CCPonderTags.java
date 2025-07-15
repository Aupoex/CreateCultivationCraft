package euphy.upo.create_cultivation.ponder;

import com.tterrag.registrate.util.entry.RegistryEntry;
import euphy.upo.create_cultivation.registry.CCBlocks;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.KINETIC_APPLIANCES;

public class CCPonderTags {

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> entryHelper = helper.withKeyFunction(RegistryEntry::getId);

        entryHelper.addToTag(KINETIC_APPLIANCES)
                .add(CCBlocks.CULTIVATION_TANK)
                .add(CCBlocks.CULTIVATION_BASE);

    }
}
