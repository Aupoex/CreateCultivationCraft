package euphy.upo.create_cultivation.registry;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import euphy.upo.create_cultivation.CreateCultivationCraft;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlock;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankCTBehaviour;
import net.minecraft.world.level.block.SoundType;
import euphy.upo.create_cultivation.content.cultivation_base.CultivationBaseBlock ;
import net.minecraft.world.level.material.MapColor;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static euphy.upo.create_cultivation.CreateCultivationCraft.REGISTRATE;

public class CCBlocks {

    public static final CTSpriteShiftEntry CULTIVATION_TANK_SHIFT = new CTSpriteShiftEntry(AllCTTypes.VERTICAL);

    static {
        CULTIVATION_TANK_SHIFT.set(
                CreateCultivationCraft.asResource("block/cultivation_tank"),
                CreateCultivationCraft.asResource("block/cultivation_tank_connected")
        );
    }

    public static final BlockEntry<CultivationTankBlock> CULTIVATION_TANK = REGISTRATE.block("cultivation_tank", CultivationTankBlock::new)
            .onRegister(connectedTextures(CultivationTankCTBehaviour::new))
            .properties(p -> p
                    .mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.METAL)
                    .strength(2.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
            )
            .blockstate((ctx, prov) -> {
            })
            .item()
                .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("item/cultivation_tank_alt")))
            .build()
            .register();

    public static final BlockEntry<CultivationBaseBlock> CULTIVATION_BASE = REGISTRATE.block("cultivation_base", CultivationBaseBlock::new)
            .properties(p -> p
                    .mapColor(MapColor.PODZOL)
                    .sound(SoundType.STONE)
                    .strength(1.5f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
            )
            .blockstate((ctx, prov) -> prov.simpleBlock(
                    ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName()))
            ))
            .item()
                .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("item/cultivation_base_full")))
            .build()
            .register();


    public static void register() {}
}