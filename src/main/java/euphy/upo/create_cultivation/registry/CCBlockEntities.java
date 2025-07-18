package euphy.upo.create_cultivation.registry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import euphy.upo.create_cultivation.content.cultivation_base.CultivationBaseBlockEntity;
import euphy.upo.create_cultivation.content.cultivation_base.CultivationBaseRenderer;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlockEntity;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankRenderer;

import static euphy.upo.create_cultivation.CreateCultivationCraft.REGISTRATE;

public class CCBlockEntities {

    public static final BlockEntityEntry<CultivationBaseBlockEntity> CULTIVATION_BASE = REGISTRATE
            .blockEntity("cultivation_base", CultivationBaseBlockEntity::new)
            .validBlocks(CCBlocks.CULTIVATION_BASE)
            .renderer(() -> CultivationBaseRenderer::new)
            .register();

    public static final BlockEntityEntry<CultivationTankBlockEntity> CULTIVATION_TANK = REGISTRATE
            .blockEntity("cultivation_tank", CultivationTankBlockEntity::new)
            .validBlocks(CCBlocks.CULTIVATION_TANK)
            .renderer(() -> CultivationTankRenderer::new)
            .register();


    public static void register() {}
}