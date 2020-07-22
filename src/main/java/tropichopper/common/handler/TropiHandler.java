package tropichopper.common.handler;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import tropichopper.common.config.ConfigurationHandler;
import tropichopper.common.tropi.Tropi;

import java.util.*;

public class TropiHandler {

    private static Map<UUID, Tropi> m_Tropis = new HashMap<>();
    private Tropi tropi;

    private static <T> T getLastElement(final Iterable<T> elements) {
        final Iterator<T> itr = elements.iterator();
        T lastElement = itr.next();

        while (itr.hasNext()) {
            lastElement = itr.next();
        }

        return lastElement;
    }

    public int AnalyzeTropi(World world, BlockPos blockPos, EntityPlayer entityPlayer) {

        Queue<BlockPos> queuedBlocks = new LinkedList<>();
        Set<BlockPos> tmpBlocks = new HashSet<>();
        Set<BlockPos> checkedBlocks = new HashSet<>();
        BlockPos currentPos;
        Block logBlock = world.getBlockState(blockPos).getBlock();
        tropi = new Tropi();

        queuedBlocks.add(blockPos);
        tropi.InsertWood(blockPos);

        while (!queuedBlocks.isEmpty()) {

            currentPos = queuedBlocks.remove();
            checkedBlocks.add(currentPos);

            tmpBlocks.addAll(LookAroundBlock(logBlock, currentPos, world, checkedBlocks));
            queuedBlocks.addAll(tmpBlocks);
            checkedBlocks.addAll(tmpBlocks);
            tmpBlocks.clear();
        }

        Set<BlockPos> tmpLeaves = new HashSet<>();
        tmpLeaves.addAll(tropi.GetM_Leaves());

        for (BlockPos blockPos1 : tmpLeaves) {
            checkedBlocks.add(blockPos1);
            LookAroundBlock(null, blockPos1, world, checkedBlocks);
        }

        tropi.setM_Position(blockPos);
        m_Tropis.put(entityPlayer.getPersistentID(), tropi);

        return tropi.GetLogCount();
    }

    private Queue<BlockPos> LookAroundBlock(Block logBlock, BlockPos currentPos, World world, Set<BlockPos> checkedBlocks) {

        Queue<BlockPos> queuedBlocks = new LinkedList<>();
        BlockPos tmpPos;

        for (int i = -1; i <= 1; i++) {
            tmpPos = new BlockPos(currentPos.getX() + 1, currentPos.getY() + i, currentPos.getZ());
            if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
                queuedBlocks.add(tmpPos);
            }

            tmpPos = new BlockPos(currentPos.getX(), currentPos.getY() + i, currentPos.getZ() + 1);
            if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
                queuedBlocks.add(tmpPos);
            }

            tmpPos = new BlockPos(currentPos.getX() - 1, currentPos.getY() + i, currentPos.getZ());
            if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
                queuedBlocks.add(tmpPos);
            }

            tmpPos = new BlockPos(currentPos.getX(), currentPos.getY() + i, currentPos.getZ() - 1);
            if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
                queuedBlocks.add(tmpPos);
            }

            tmpPos = new BlockPos(currentPos.getX() + 1, currentPos.getY() + i, currentPos.getZ() + 1);
            if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
                queuedBlocks.add(tmpPos);
            }

            tmpPos = new BlockPos(currentPos.getX() - 1, currentPos.getY() + i, currentPos.getZ() - 1);
            if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
                queuedBlocks.add(tmpPos);
            }

            tmpPos = new BlockPos(currentPos.getX() - 1, currentPos.getY() + i, currentPos.getZ() + 1);
            if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
                queuedBlocks.add(tmpPos);
            }

            tmpPos = new BlockPos(currentPos.getX() + 1, currentPos.getY() + i, currentPos.getZ() - 1);
            if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
                queuedBlocks.add(tmpPos);
            }

            tmpPos = new BlockPos(currentPos.getX(), currentPos.getY() + i, currentPos.getZ());
            if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
                queuedBlocks.add(tmpPos);
            }
        }

        return queuedBlocks;
    }

    private boolean CheckBlock(World world, BlockPos blockPos, Set<BlockPos> checkedBlocks, Block originBlock) {

        if (checkedBlocks.contains(blockPos)) {
            return false;
        }

        if (world.getBlockState(blockPos).getBlock() != originBlock) {

            if (ConfigurationHandler.plantSapling && world.getBlockState(blockPos).getBlock().isLeaves(world.getBlockState(blockPos), world, blockPos) && tropi.GetM_Leaves().isEmpty()) {
                tropi.InsertLeaf(blockPos);
            }

            if (ConfigurationHandler.decayLeaves && ConfigurationHandler.leafWhiteList.contains(world.getBlockState(blockPos).getBlock().getUnlocalizedName())) {
                tropi.InsertLeaf(blockPos);

                return false;
            }

            if (ConfigurationHandler.decayLeaves && world.getBlockState(blockPos).getBlock().isLeaves(world.getBlockState(blockPos), world, blockPos)) {
                tropi.InsertLeaf(blockPos);

                return false;
            } else {
                return false;
            }
        }

        tropi.InsertWood(blockPos);

        return true;
    }

    public void DestroyTropi(World world, EntityPlayer entityPlayer) {

        int soundReduced = 0;

        if (m_Tropis.containsKey(entityPlayer.getPersistentID())) {

            Tropi tmpTropi = m_Tropis.get(entityPlayer.getPersistentID());

            for (BlockPos blockPos : tmpTropi.GetM_Wood()) {

                if (soundReduced <= 1) {
                    world.destroyBlock(blockPos, true);
                } else {
                    world.getBlockState(blockPos).getBlock().dropBlockAsItem(world, blockPos, world.getBlockState(blockPos), 1);
                }

                world.setBlockToAir(blockPos);

                soundReduced++;
            }

            if (ConfigurationHandler.plantSapling && !tmpTropi.GetM_Leaves().isEmpty()) {

                BlockPos tmpPosition = getLastElement(tmpTropi.GetM_Leaves());
                PlantSapling(world, tmpPosition, tmpTropi.getM_Position());
            }

            soundReduced = 0;

            if (ConfigurationHandler.decayLeaves) {

                for (BlockPos blockPos : tmpTropi.GetM_Leaves()) {

                    if (soundReduced <= 1) {
                        world.destroyBlock(blockPos, true);
                    } else {
                        world.getBlockState(blockPos).getBlock().dropBlockAsItem(world, blockPos, world.getBlockState(blockPos), 1);
                    }

                    world.setBlockToAir(blockPos);

                    soundReduced++;
                }
            }
        }
    }

    private void PlantSapling(World world, BlockPos blockPos, BlockPos originPos) {

        Set<ItemStack> leafDrop = new HashSet<>();
        BlockPos plantPos1 = new BlockPos(originPos.getX() - 1, originPos.getY(), originPos.getZ() - 1);
        int counter = 0;

        while (leafDrop.isEmpty() && counter <= 100) {
            NonNullList<ItemStack> tmpList = NonNullList.create();
            world.getBlockState(blockPos).getBlock().getDrops(tmpList, world, blockPos, world.getBlockState(blockPos), 3);
            leafDrop.addAll(tmpList);

            counter++;
        }

        if (leafDrop.isEmpty()) {
            return;
        }

        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((WorldServer) world);
        fakePlayer.setHeldItem(EnumHand.MAIN_HAND, leafDrop.iterator().next());

        for (ItemStack itemStack : leafDrop) {
            itemStack.onItemUse(fakePlayer, world, plantPos1, EnumHand.MAIN_HAND, EnumFacing.NORTH, 0, 0, 0);
        }
    }
}
