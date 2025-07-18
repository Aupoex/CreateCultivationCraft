package euphy.upo.create_cultivation.compat.jade;

import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlock;
import euphy.upo.create_cultivation.content.cultivation_tank.CultivationTankBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {

        registration.registerBlockDataProvider(CultivationTankJadeProvider.INSTANCE, CultivationTankBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {

        registration.registerBlockComponent(CultivationTankJadeProvider.INSTANCE, CultivationTankBlock.class);
    }
}