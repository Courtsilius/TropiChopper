package tropichopper.common.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import tropichopper.common.config.ConfigurationHandler;
import tropichopper.common.network.ServerSettingsMessage;
import tropichopper.core.TropiChopper;

public class EventHandler {

    @SubscribeEvent
    public void OnServerConnect(PlayerEvent.PlayerLoggedInEvent loggedInEvent) {
        TropiChopper.m_Network.sendTo(new ServerSettingsMessage(ConfigurationHandler.reverseShift, ConfigurationHandler.disableShift), (EntityPlayerMP) loggedInEvent.player);
    }
}
