package tropichopper.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import tropichopper.common.command.TCHCommand;
import tropichopper.common.config.ConfigurationHandler;
import tropichopper.common.handler.EventHandler;
import tropichopper.common.network.ClientSettingsMessage;
import tropichopper.common.network.ServerSettingsMessage;
import tropichopper.proxy.CommonProxy;

import java.io.File;

import static tropichopper.core.TropiChopper.*;

@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, dependencies = MOD_DEPENDENCIES, guiFactory = GUI_FACTORY, acceptableRemoteVersions = "*")

public class TropiChopper {

    public static final String MOD_ID = "tropichopper";
    public static final String MOD_NAME = "Tropi Chopper";
    public static final String MOD_VERSION = "1.2.4";
    public static final String GUI_FACTORY = "tropichopper.client.gui.GuiTCHFactory";
    public static final String MOD_DEPENDENCIES = "required-after:forge@[14.23.0.2486,)";
    public static SimpleNetworkWrapper m_Network;

    @SidedProxy(serverSide = "tropichopper.proxy.ServerProxy", clientSide = "tropichopper.proxy.CommonProxy")
    private static CommonProxy commonProxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(new File(new File(event.getModConfigurationDirectory(), "tropichopper"), "tropichopper.cfg"));

        m_Network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        m_Network.registerMessage(ServerSettingsMessage.MsgHandler.class, ServerSettingsMessage.class, 0, Side.CLIENT);
        m_Network.registerMessage(ClientSettingsMessage.MsgHandler.class, ClientSettingsMessage.class, 1, Side.SERVER);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(commonProxy);
        MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new TCHCommand());
    }
}
