package life.hanabi.core.cloudmusic.impl;


import life.hanabi.utils.math.AnimationUtils;

public class Lyric {
	public long time;
	public float progress,y;
	public String text;
	public String tText;
	public AnimationUtils animationUtils  = new AnimationUtils();
	public AnimationUtils animationUtils2  = new AnimationUtils();


	public Lyric(String text, long time) {
		this.text = text;
		this.time = time;
	}

	public void setTransLyric(String text) {
		this.tText = text;
	}
}
