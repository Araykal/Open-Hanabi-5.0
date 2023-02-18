package life.hanabi.gui.clickgui;

import life.hanabi.utils.math.AnimationUtils;

public class Slider {
    public float top, bottom;
    public float top1, bottom1;

    float s1, s2;
    private AnimationUtils animation = new AnimationUtils();
    private AnimationUtils animation2 = new AnimationUtils();

    public void update() {
        top = animation.animate(top1, top, s1, false);
        bottom = animation2.animate(bottom1, bottom, s2, false);
    }

    public void change(float newTop, float newBottom) {
        s1 = s2 = 0.3f;
        this.top1 = newTop;
        this.bottom1 = newBottom;
    }

}
