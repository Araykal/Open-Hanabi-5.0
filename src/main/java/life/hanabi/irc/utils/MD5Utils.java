package life.hanabi.irc.utils;

import cn.qiriyou.IIiIIiiiIiii;
import org.apache.commons.codec.digest.DigestUtils;

@IIiIIiiiIiii

public class MD5Utils {
    public static String getMD5(String text) {
        return DigestUtils.md5Hex(text);
    }
}
