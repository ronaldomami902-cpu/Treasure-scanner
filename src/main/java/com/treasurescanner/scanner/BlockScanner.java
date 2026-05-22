package com.treasurescanner.scanner;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockScanner {

    // Tarama yarıçapı (chunk cinsinden) - 8 chunk = 128 blok
    private static final int SCAN_RADIUS_CHUNKS = 8;

    // Y sınırları
    private static final int Y_MAX = 200;
    private static final int Y_MIN = -55;

    public enum BlockType {
        SPAWNER("§c", "Spawner"),
        CHEST("§e", "Sandık"),
        SHULKER("§d", "Shulker");

        public final String color;
        public final String displayName;

        BlockType(String color, String displayName) {
            this.color = color;
            this.displayName = displayName;
        }
    }

    public static class FoundBlock {
        public final BlockPos pos;
        public final BlockType type;

        public FoundBlock(BlockPos pos, BlockType type) {
            this.pos = pos;
            this.type = type;
        }
    }

    public static List<FoundBlock> scan(World world, BlockPos playerPos) {
        List<FoundBlock> found = new ArrayList<>();

        ChunkPos playerChunk = new ChunkPos(playerPos);

        for (int cx = -SCAN_RADIUS_CHUNKS; cx <= SCAN_RADIUS_CHUNKS; cx++) {
            for (int cz = -SCAN_RADIUS_CHUNKS; cz <= SCAN_RADIUS_CHUNKS; cz++) {
                ChunkPos chunkPos = new ChunkPos(playerChunk.x + cx, playerChunk.z + cz);

                // Chunk yüklü mü kontrol et
                if (!world.isChunkLoaded(chunkPos.x, chunkPos.z)) continue;

                int startX = chunkPos.getStartX();
                int startZ = chunkPos.getStartZ();

                // Chunk içindeki her bloğu tara (Y: -55 ile 200 arası)
                for (int x = startX; x < startX + 16; x++) {
                    for (int z = startZ; z < startZ + 16; z++) {
                        for (int y = Y_MIN; y <= Y_MAX; y++) {
                            BlockPos checkPos = new BlockPos(x, y, z);
                            Block block = world.getBlockState(checkPos).getBlock();

                            if (block instanceof SpawnerBlock) {
                                found.add(new FoundBlock(checkPos.toImmutable(), BlockType.SPAWNER));
                            } else if (block instanceof ChestBlock || block instanceof TrappedChestBlock || block instanceof BarrelBlock) {
                                found.add(new FoundBlock(checkPos.toImmutable(), BlockType.CHEST));
                            } else if (block instanceof ShulkerBoxBlock) {
                                found.add(new FoundBlock(checkPos.toImmutable(), BlockType.SHULKER));
                            }
                        }
                    }
                }
            }
        }

        return found;
    }
}
