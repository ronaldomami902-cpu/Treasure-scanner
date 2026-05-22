package com.treasurescanner.renderer;

import com.treasurescanner.scanner.BlockScanner;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.List;

public class ESPRenderer {

    // Maksimum görüntüleme mesafesi
    private static final double MAX_DISTANCE = 200.0;

    public static void render(WorldRenderContext context, List<BlockScanner.FoundBlock> blocks) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;

        Entity camera = client.getCameraEntity();
        if (camera == null) return;

        Vec3d camPos = context.camera().getPos();

        matrices.push();

        // Kamera pozisyonunu sıfırla (render origin)
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer lineConsumer = immediate.getBuffer(RenderLayer.getLines());

        for (BlockScanner.FoundBlock block : blocks) {
            double dist = camPos.distanceTo(Vec3d.ofCenter(block.pos));
            if (dist > MAX_DISTANCE) continue;

            // Renk seç
            float r, g, b;
            switch (block.type) {
                case SPAWNER -> { r = 1.0f; g = 0.2f; b = 0.2f; } // Kırmızı
                case CHEST   -> { r = 1.0f; g = 0.9f; b = 0.0f; } // Sarı
                case SHULKER -> { r = 0.8f; g = 0.2f; b = 1.0f; } // Mor
                default      -> { r = 1.0f; g = 1.0f; b = 1.0f; }
            }

            // Mesafeye göre alpha (uzakta daha saydam)
            float alpha = (float) Math.max(0.3, 1.0 - (dist / MAX_DISTANCE));

            drawBlockBox(matrices, lineConsumer, block.pos, r, g, b, alpha);
        }

        immediate.draw(RenderLayer.getLines());
        matrices.pop();
    }

    private static void drawBlockBox(MatrixStack matrices, VertexConsumer consumer,
                                      BlockPos pos, float r, float g, float b, float a) {
        Box box = new Box(pos).expand(0.002);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float x1 = (float) box.minX, y1 = (float) box.minY, z1 = (float) box.minZ;
        float x2 = (float) box.maxX, y2 = (float) box.maxY, z2 = (float) box.maxZ;

        // 12 kenar çiz (kutu)
        // Alt yüz
        line(consumer, matrix, x1,y1,z1, x2,y1,z1, r,g,b,a);
        line(consumer, matrix, x2,y1,z1, x2,y1,z2, r,g,b,a);
        line(consumer, matrix, x2,y1,z2, x1,y1,z2, r,g,b,a);
        line(consumer, matrix, x1,y1,z2, x1,y1,z1, r,g,b,a);
        // Üst yüz
        line(consumer, matrix, x1,y2,z1, x2,y2,z1, r,g,b,a);
        line(consumer, matrix, x2,y2,z1, x2,y2,z2, r,g,b,a);
        line(consumer, matrix, x2,y2,z2, x1,y2,z2, r,g,b,a);
        line(consumer, matrix, x1,y2,z2, x1,y2,z1, r,g,b,a);
        // Dikey kenarlar
        line(consumer, matrix, x1,y1,z1, x1,y2,z1, r,g,b,a);
        line(consumer, matrix, x2,y1,z1, x2,y2,z1, r,g,b,a);
        line(consumer, matrix, x2,y1,z2, x2,y2,z2, r,g,b,a);
        line(consumer, matrix, x1,y1,z2, x1,y2,z2, r,g,b,a);
    }

    private static void line(VertexConsumer consumer, Matrix4f matrix,
                               float x1, float y1, float z1,
                               float x2, float y2, float z2,
                               float r, float g, float b, float a) {
        // Normal vektör (line render için gerekli)
        float nx = x2 - x1, ny = y2 - y1, nz = z2 - z1;
        float len = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
        if (len == 0) return;
        nx /= len; ny /= len; nz /= len;

        consumer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(nx, ny, nz).next();
        consumer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(nx, ny, nz).next();
    }
}
