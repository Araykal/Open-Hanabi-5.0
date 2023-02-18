package life.hanabi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;


public class RotationUtil {

    public static Rotation targetRotation;
    private static int keepLength;
    private static int revTick;
    private static final double RAD_TO_DEG = 180.0 / Math.PI;
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static float[] prevRotations = new float[2];


    public static Vec3 getHitOrigin(final Entity entity) {
        return new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
    }

    public static float[] getRotationToLocation(final Vec3 loc) {
        double xDiff = loc.xCoord - Minecraft.getMinecraft().thePlayer.posX;
        double yDiff = loc.yCoord - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        double zDiff = loc.zCoord - Minecraft.getMinecraft().thePlayer.posZ;

        double distance = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(yDiff, distance) * 180.0D / Math.PI));

        return new float[]{yaw, pitch};
    }

    public static void applySmoothing(final float[] lastRotations,
                                      final float smoothing,
                                      final float[] dstRotation) {
        if (smoothing > 0.0F) {
            final float yawChange = MathHelper.wrapAngleTo180_float(dstRotation[0] - lastRotations[0]);
            final float pitchChange = MathHelper.wrapAngleTo180_float(dstRotation[1] - lastRotations[1]);

            final float smoothingFactor = Math.max(1.0F, smoothing / 10.0F);

            dstRotation[0] = lastRotations[0] + yawChange / smoothingFactor;
            dstRotation[1] = Math.max(Math.min(112, lastRotations[1] + pitchChange / smoothingFactor), -90.0F);
        }
    }

    public static float[] getRotationToEntity(Entity entity) {
        double pX = Minecraft.getMinecraft().thePlayer.posX;
        double pY = Minecraft.getMinecraft().thePlayer.posY + (double) Minecraft.getMinecraft().thePlayer.getEyeHeight();
        double pZ = Minecraft.getMinecraft().thePlayer.posZ;
        double eX = entity.posX;
        double eY = entity.posY + (double) entity.getEyeHeight();
        double eZ = entity.posZ;
        double dX = pX - eX;
        double dY = pY - eY;
        double dZ = pZ - eZ;
        double dH = Math.sqrt(Math.pow(dX, 2.0D) + Math.pow(dZ, 2.0D));
        float yaw;
        float pitch;
        yaw = (float) (Math.toDegrees(Math.atan2(dZ, dX)) + 90.0D);
        pitch = (float) Math.toDegrees(Math.atan2(dH, dY));
        return new float[]{yaw, 90.0F - pitch};
    }



    public static float getRotationDifference(float current, float target) {
        return MathHelper.wrapAngleTo180_float(target - current);
    }

    private static float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }




    public static float[] rotations(Entity target) {
        double x = target.posX - mc.thePlayer.posX;
        double z = target.posZ - mc.thePlayer.posZ;
        double y = target.posY + target.getEyeHeight() * 0.75D - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());

        double distance = MathHelper.sqrt_double(x * x + z * z);

        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -((Math.atan2(y, distance) * 180.0D / Math.PI));
        return new float[]{yaw, pitch};
    }


    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;
        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-Math.atan2(yDiff, dist) * 180.0 / Math.PI);
        return new float[]{yaw, pitch};
    }


    public static float getNewAngle(float angle) {
        if ((angle %= 360.0f) >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle = Math.abs(angle1 - angle2) % 360.0f;
        if (angle > 180.0f) {
            angle = 360.0f - angle;
        }
        return angle;
    }



    public static float[] getRotations(final Vec3 start,
                                       final Vec3 dst) {
        final double xDif = dst.xCoord - start.xCoord;
        final double yDif = dst.yCoord - start.yCoord;
        final double zDif = dst.zCoord - start.zCoord;

        final double distXZ = Math.sqrt(xDif * xDif + zDif * zDif);

        return new float[]{
                (float) (Math.atan2(zDif, xDif) * RAD_TO_DEG) - 90.0F,
                (float) (-(Math.atan2(yDif, distXZ) * RAD_TO_DEG))
        };
    }

    public static float[] getRotations(final float[] lastRotations,
                                       final float smoothing,
                                       final Vec3 start,
                                       final Vec3 dst) {
        // Get rotations from start - dst
        final float[] rotations = getRotations(start, dst);
        // Apply smoothing to them
        applySmoothing(lastRotations, smoothing, rotations);
        return rotations;
    }

    public static float[] getRotations(double posX, double posY, double posZ, double eyeHeight, final BlockPos blockPos, final EnumFacing enumFacing) {
        double n = blockPos.getX() + 0.5 - posX + enumFacing.getFrontOffsetX() / 2.0;
        double n2 = blockPos.getZ() + 0.5 - posZ + enumFacing.getFrontOffsetZ() / 2.0;
        double n3 = posY + eyeHeight - (blockPos.getY() + 0.5);
        double n4 = MathHelper.sqrt_double(n * n + n2 * n2);
        float n5 = (float) (Math.atan2(n2, n) * 180.0 / 3.141592653589793) - 90.0f;
        float n6 = (float) (Math.atan2(n3, n4) * 180.0 / 3.141592653589793);
        if (n5 < 0.0f) {
            n5 += 360.0f;
        }
        return new float[]{n5, n6};
    }

}

