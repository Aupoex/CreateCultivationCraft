package euphy.upo.create_cultivation.datagen;

import com.simibubi.create.AllTags;
import euphy.upo.create_cultivation.CreateCultivationCraft;
import euphy.upo.create_cultivation.registry.CCBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CCTagProvider {

    public static class Blocks extends BlockTagsProvider {

        public Blocks(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, CreateCultivationCraft.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            //Cultivation Base
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CCBlocks.CULTIVATION_BASE.get());
            tag(BlockTags.MINEABLE_WITH_AXE).add(CCBlocks.CULTIVATION_BASE.get());
            tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag).add(CCBlocks.CULTIVATION_BASE.get());

            //Cultivation Tank-
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CCBlocks.CULTIVATION_TANK.get());
            tag(BlockTags.MINEABLE_WITH_AXE).add(CCBlocks.CULTIVATION_TANK.get());
            tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag).add(CCBlocks.CULTIVATION_TANK.get());
        }
    }


    public static class Items extends ItemTagsProvider {

        public Items(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, blockTagsProvider, CreateCultivationCraft.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
        }
    }
}