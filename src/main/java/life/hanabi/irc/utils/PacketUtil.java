package life.hanabi.irc.utils;

import cn.qiriyou.IIiIIiiiIiii;
import com.google.gson.Gson;
import life.hanabi.irc.packets.Packet;
@IIiIIiiiIiii
public class PacketUtil {

    public static <T extends Packet> T unpack(String content, Class<T> type) {
        Gson gson = new Gson();
        T result = null;
        try {

            result = gson.fromJson(content, type);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Json transform failed:" + content);
        }
        return result;
    }

    public static String pack(Packet packet) {
        Gson gson = new Gson();
        String s = gson.toJson(packet);

        return s;
    }


}
