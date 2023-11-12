package bluemoonjune.loadstone;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class LoadstoneRenderer implements BlockEntityRenderer<LoadstoneBlockEntity> {

    public static Random rand = new Random();

    private class Face {
        public Vec3d center;
        public Vec3d normal;

        public Face(Vec3d center, Vec3d normal) {
            this.center = center;
            this.normal = normal;
        }
    }

    public Face[] faces = {
            new Face(new Vec3d(1, 0.5, 0.5), new Vec3d(1, 0, 0)),
            new Face(new Vec3d(0.5, 0.5, 1), new Vec3d(0, 0, 1)),
            new Face(new Vec3d(0, 0.5, 0.5), new Vec3d(-1, 0, 0)),
            new Face(new Vec3d(0.5, 0.5, 0), new Vec3d(0, 0, -1))
    };

    public static Vec3d oneVec = new Vec3d(1, 1, 1);

    public LoadstoneRenderer(BlockEntityRendererFactory.Context ctx) {}

    public void renderTendril(World world, Vec3d start, Vec3d f, Vec3d center, double length) {
        Vec3d d = start.subtract(center);
        Vec3d n = d.crossProduct(f).normalize();
        double dl = d.length();
        double step = 1/dl * 0.05;
        for (double i = 0; i <= length/dl; i+=step) {
            Vec3d dif = d.multiply(Math.cos(i)).add(n.crossProduct(d).multiply(Math.sin(i))).add(n.multiply(n.dotProduct(d)).multiply(1-Math.cos(i)));
            Vec3d pos = dif.add(center);
            Vec3d tan = n.crossProduct(dif).normalize().multiply(0.02);
            world.addParticle(ParticleTypes.DRAGON_BREATH, pos.x, pos.y, pos.z, tan.x, tan.y, tan.z);
        }
    }

    public void renderRandomTendril(World world, Vec3d start, Vec3d f, Vec3d centerRange) {

        Vec3d centerOff = new Vec3d(rand.nextDouble(-1, 1), rand.nextDouble(-1, 1), rand.nextDouble(-1, 1)).multiply(centerRange);
        double len = centerOff.length();
        Vec3d center = start.add(centerOff.multiply(2/len/len));
        renderTendril(world, start, f, center, rand.nextDouble(0.75, 1.25));
    }

    @Override
    public void render(LoadstoneBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        BlockPos pos = entity.getPos();
        if (!Loadstone.LOADSTONE.getActive(world, pos)) return;
        Vec3d vec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
        for (Face face : faces) {
            if (rand.nextDouble() > 0.995) {
                renderRandomTendril(world, vec.add(face.center), face.normal, oneVec.subtract(Math.abs(face.normal.x), Math.abs(face.normal.y), Math.abs(face.normal.z)));
            }
        }
    }
}
