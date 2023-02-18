package life.hanabi.gui.musicplayer;

import life.hanabi.Hanabi;
import life.hanabi.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;

import java.awt.*;


class Lyric{
    private String lyric;
    private int time;
    private float y;
    private float scale;

    Lyric(String lyric, int time){
        this.lyric = lyric;
        this.time = time;
    }
    public String getLyric() {
        return lyric;
    }
    public int getTime() {
        return time;
    }
}
public class MusicPlayer extends Gui {
    public float width=600;
    public float height = 400;



    public void render(float x, float  y){
        RenderUtil.drawRoundRect5(x, y, width, height, new Color(0, 0, 0, 100));
        Hanabi.INSTANCE.fontLoaders.syFont18.drawString("Music Player", x + 10, y + 10, new Color(255, 255, 255).getRGB());

    }

    public void mouseClicked(float mouseX, float mouseY, int mouseButton){

    }
}
