package life.hanabi.utils.math;

import life.hanabi.modules.settings.ClientSettings;

public class AnimationUtils {
    private final float defaultSpeed = 0.125f;
    private final TimerUtil timer = new TimerUtil();

    public double animate(double target, double current, double speed, boolean force) {
        return animate((float) target, (float) current, (float) speed, force);
    }

    public double animate(double target, double current, double speed) {
        return animate((float) target, (float) current, (float) speed, false);
    }

    public float animate(float target, float current, float speed) {
        return animate(target, current, speed, false);
    }

    public float animate(float target, float current, float speed, boolean force) {
        if(timer.hasReached(10)) {
            if (!ClientSettings.screenAnimation.getValue() && !force) {
                return target;
            }
            boolean larger = target > current;
            if (speed < 0.0f) {
                speed = 0.0f;
            } else if (speed > 1.0) {
                speed = 1.0f;
            }
            float dif = Math.max(target, current) - Math.min(target, current);
            float factor = dif * speed;
            if (factor < 0.1f) {
                factor = 0.1f;
            }
            current = larger ? current + factor : current - factor;
            timer.reset();
            if (Math.abs(current - target) < 0.2) {
                return target;
            } else {
                return current;
            }
        }
        return current;
    }
}
