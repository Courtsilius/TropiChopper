package tropichopper.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import tropichopper.common.config.ConfigurationHandler;
import tropichopper.core.TropiChopper;

public class GuiTCHFactory extends DefaultGuiFactory {

    public GuiTCHFactory() {
        super(TropiChopper.MOD_ID, TropiChopper.MOD_NAME);
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen guiScreen) {
        return new GuiConfig(guiScreen, new ConfigElement(ConfigurationHandler.config.getCategory("Settings")).getChildElements(), modid, false, false, title);
    }
}