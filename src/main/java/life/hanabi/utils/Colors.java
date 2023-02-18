package life.hanabi.utils;

import java.awt.*;
import java.text.NumberFormat;

public enum Colors {
    BLACK(-16711423),
    BLUE(-12028161),
    DARKBLUE(-12621684),
    GREEN(-9830551),
    DARKGREEN(-9320847),
    WHITE(-65794),
    AQUA(-7820064),
    DARKAQUA(-12621684),
    GREY(-9868951),
    DARKGREY(-14342875),
    RED(-65536),
    DARKRED(-8388608),
    ORANGE(-29696),
    DARKORANGE(-2263808),
    YELLOW(-256),
    DARKYELLOW(-2702025),
    MAGENTA(-18751),
    DARKMAGENTA(-2252579);

    public int c;

    Colors(int co) {
        this.c = co;
    }

    public static int getColor(final Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(final int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(final int brightness, final int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(final int red, final int green, final int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        Color color;
        if (fractions != null) {
            if (colors != null) {
                if (fractions.length == colors.length) {
                    int[] indicies = getFractionIndicies(fractions, progress);
                    if (indicies[0] < 0 || indicies[0] >= fractions.length || indicies[1] < 0 || indicies[1] >= fractions.length) {
                        return colors[0];
                    }
                    float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
                    Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
                    float max = range[1] - range[0];
                    float value = progress - range[0];
                    float weight = value / max;
                    color = blend(colorRange[0], colorRange[1], 1f - weight);
                } else {
                    throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
                }
            } else {
                throw new IllegalArgumentException("Colours can't be null");
            }
        } else {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        return color;
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = (float) 1.0 - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0) {
            red = 0;
        } else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 255) {
            blue = 255;
        }
        Color color = null;
        try {
            color = new Color(red, green, blue);
        } catch (IllegalArgumentException exp) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            exp.printStackTrace();
        }
        return color;
    }

    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int[] range = new int[2];
        int startPoint = 0;
        while (startPoint < fractions.length && fractions[startPoint] <= progress) {
            startPoint++;
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color getDarker(Color before, int dark, int alpha) {
        int rDank = Math.max(before.getRed() - dark, 0);
        int gDank = Math.max(before.getGreen() - dark, 0);
        int bDank = Math.max(before.getBlue() - dark, 0);
        return new Color(rDank, gDank, bDank, alpha);
    }


}
