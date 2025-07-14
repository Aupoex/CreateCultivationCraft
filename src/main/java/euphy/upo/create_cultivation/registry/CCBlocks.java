package euphy.upo.create_cultivation.registry;

import com.tterrag.registrate.util.entry.BlockEntry;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlock;
import net.minecraft.world.level.block.SoundType;
import euphy.upo.create_cultivation.content.cultivation_base.CultivationBaseBlock ;
import net.minecraft.world.level.material.MapColor;

import static euphy.upo.create_cultivation.CreateCultivationCraft.REGISTRATE;

public class CCBlocks {

    public static final BlockEntry<CultivationTankBlock> CULTIVATION_TANK = REGISTRATE.block("cultivation_tank", CultivationTankBlock::new)
            .lang("Cultivation Tank")
            .properties(p -> p
                    .mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.METAL)
                    .strength(2.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
            )
            .blockstate((ctx, prov) -> prov.simpleBlock(
                    ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName()))
            ))
            .item()
            .build()
            .register();

    public static final BlockEntry<CultivationBaseBlock> CULTIVATION_BASE = REGISTRATE.block("cultivation_base", CultivationBaseBlock::new)
            .lang("Cultivation Base")
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
            .build()
            .register();


    public static void register() {}
}