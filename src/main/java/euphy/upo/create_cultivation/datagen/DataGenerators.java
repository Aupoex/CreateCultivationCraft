package euphy.upo.create_cultivation.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(true, new CCRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(true, new CCCompatRecipeGenerator(packOutput));

        CCTagProvider.Blocks blockTagProvider = new CCTagProvider.Blocks(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(true, blockTagProvider);
        generator.addProvider(true, new CCTagProvider.Items(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));


    }
}
