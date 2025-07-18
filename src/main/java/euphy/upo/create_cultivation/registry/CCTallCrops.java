package euphy.upo.create_cultivation.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.HashSet;
import java.util.Set;

//废弃
public class CCTallCrops {

    private static final Set<Item> TALL_CROPS = new HashSet<>();

    public static void register() {
        TALL_CROPS.add(Items.SUNFLOWER);
        TALL_CROPS.add(Items.LILAC);
        TALL_CROPS.add(Items.ROSE_BUSH);
        TALL_CROPS.add(Items.PEONY);
        TALL_CROPS.add(Items.SMALL_DRIPLEAF);
        TALL_CROPS.add(Items.PITCHER_POD);
    }

    public static boolean isTallCrop(Item item) {
        return TALL_CROPS.contains(item);
    }
}