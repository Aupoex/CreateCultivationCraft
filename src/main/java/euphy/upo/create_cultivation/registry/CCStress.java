package euphy.upo.create_cultivation.registry;

import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.world.level.block.Block;

public class CCStress {

    public static void registerAllStressValues() {

        Block cultivationBaseInstance = CCBlocks.CULTIVATION_BASE.get();
        double stressImpact = 4.0;
        BlockStressValues.IMPACTS.register(cultivationBaseInstance, () -> stressImpact);
    }
}
