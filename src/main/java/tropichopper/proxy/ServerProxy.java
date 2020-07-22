package tropichopper.proxy;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import tropichopper.common.config.ConfigurationHandler;
import tropichopper.common.handler.TropiHandler;

public class ServerProxy extends CommonProxy {

    @Override
    public void InteractWithTropi(PlayerInteractEvent interactEvent) {

        if (m_PlayerPrintNames.containsKey(interactEvent.getEntityPlayer().getPersistentID()) && m_PlayerPrintNames.get(interactEvent.getEntityPlayer().getPersistentID())) {
            interactEvent.getEntityPlayer().sendMessage(new TextComponentTranslation("Block: " + interactEvent.getWorld().getBlockState(interactEvent.getPos()).getBlock().getUnlocalizedName()));
            interactEvent.getEntityPlayer().sendMessage(new TextComponentTranslation("Main hand item: " + interactEvent.getEntityPlayer().getHeldItemMainhand().getUnlocalizedName()));
        }

        int logCount;
        boolean shifting = true;

        if (!ConfigurationHandler.disableShift) {
            if (interactEvent.getEntityPlayer().isSneaking() && !ConfigurationHandler.reverseShift) {
                shifting = false;
            }

            if (!interactEvent.getEntityPlayer().isSneaking() && ConfigurationHandler.reverseShift) {
                shifting = false;
            }
        }

        if (CheckWoodenBlock(interactEvent.getWorld(), interactEvent.getPos()) && CheckItemInHand(interactEvent.getEntityPlayer()) && shifting) {

            int axeDurability = interactEvent.getEntityPlayer().getHeldItemMainhand().getMaxDamage() - interactEvent.getEntityPlayer().getHeldItemMainhand().getItemDamage();

            if (m_PlayerData.containsKey(interactEvent.getEntityPlayer().getPersistentID()) &&
                    m_PlayerData.get(interactEvent.getEntityPlayer().getPersistentID()).m_BlockPos.equals(interactEvent.getPos()) &&
                    m_PlayerData.get(interactEvent.getEntityPlayer().getPersistentID()).m_AxeDurability == axeDurability) {
                return;
            }

            tropiHandler = new TropiHandler();
            logCount = tropiHandler.AnalyzeTropi(interactEvent.getWorld(), interactEvent.getPos(), interactEvent.getEntityPlayer());
			
			if(!CheckJungleBlock(interactEvent.getWorld(), interactEvent.getPos())){
				if (interactEvent.getEntityPlayer().getHeldItemMainhand().isItemStackDamageable() && axeDurability < logCount) {
					m_PlayerData.remove(interactEvent.getEntityPlayer().getPersistentID());
					return;
				}
			}

            if (logCount > 1) {
                m_PlayerData.put(interactEvent.getEntityPlayer().getPersistentID(), new PlayerInteract(interactEvent.getPos(), logCount, axeDurability));
            }
        } else {
            m_PlayerData.remove(interactEvent.getEntityPlayer().getPersistentID());
        }
    }
}
