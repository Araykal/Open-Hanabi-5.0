package life.hanabi.utils.math;

import com.google.common.collect.Lists;
import life.hanabi.utils.Button;
import life.hanabi.utils.Utils;

import java.util.List;

public class CpsUtil {
    private final List<Button> leftCounter = Lists.newArrayList();
    private final List<Button> rightCounter = Lists.newArrayList();

    public int getLeftCps() {
        Utils.cpsUtil.update();
        return this.leftCounter.size();
    }

    public int getRightCps() {
        Utils.cpsUtil.update();
        return this.rightCounter.size();
    }

    public void update() {
        this.leftCounter.removeIf(Button::canBeReduced);
        this.rightCounter.removeIf(Button::canBeReduced);
    }

    public void update(int type) {
        switch (type) {
            case 0:
                this.leftCounter.add(new Button(System.currentTimeMillis()));
                break;
            case 1:
                this.rightCounter.add(new Button(System.currentTimeMillis()));
                break;
        }
    }
}
