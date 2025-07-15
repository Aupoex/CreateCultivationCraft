package euphy.upo.create_cultivation.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import euphy.upo.create_cultivation.CreateCultivationCraft;

public class CCPartialModels {

    public static final PartialModel CULTIVATION_BASE_GLOW = PartialModel.of(
            CreateCultivationCraft.asResource("block/cultivation_base_glow"));

    public static void init() {

    }
}