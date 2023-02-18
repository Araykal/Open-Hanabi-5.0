package life.hanabi.irc.management;

public class User {
    public String username;
    public String password;
    public String hwid;
    public String text;

    public RankManager.Ranks rank;

    public String rankInGame;
    public String ingame;


    public User(String username, String password, String hwid, String text) {
        this.username = username;
        this.password = password;
        this.hwid = hwid;
        this.text = text;
    }
}
