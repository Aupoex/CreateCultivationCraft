package euphy.upo.create_cultivation.registry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import euphy.upo.create_cultivation.content.cultivation_base.CultivationBaseBlockEntity;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static euphy.upo.create_cultivation.CreateCultivationCraft.REGISTRATE;

public class CCBlockEntities {

    public static final BlockEntityEntry<CultivationBaseBlockEntity> CULTIVATION_BASE = REGISTRATE
            .blockEntity("cultivation_base", CultivationBaseBlockEntity::new)
            .validBlocks(CCBlocks.CULTIVATION_BASE)
            .register();

    public static final BlockEntityEntry<CultivationTankBlockEntity> CULTIVATION_TANK = REGISTRATE
            .blockEntity("cultivation_tank", CultivationTankBlockEntity::new)
            .validBlocks(CCBlocks.CULTIVATION_TANK)
            .register();

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CCBlockEntities.CULTIVATION_BASE.get(),
                (blockEntity, context) -> blockEntity.getItemHandler()
        );
    }

    public static void register() {}
}
