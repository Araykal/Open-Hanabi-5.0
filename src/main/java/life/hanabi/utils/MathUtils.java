package life.hanabi.utils;

public class MathUtils {

    public static int getRandomInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static int getRandomInRange(double min, double max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static float clampValue(final float value, final float floor, final float cap) {
        if (value < floor) {
            return floor;
        }
        return Math.min(value, cap);
    }
}
