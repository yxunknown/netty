/**
 * TestUserInfo.class
 * Created in Intelij IDEA
 * <p>
 * Write Some Describe of this class here
 *
 * @author Mevur
 * @date 01/24/18 21:19
 */
package section1;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class TestUserInfo {
    public static void main(String[] args) throws Exception {
        UserInfo info = new UserInfo();
        info.buildUserId(100).BuildUserName("chongqing jiaotong university");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(info);
        oos.flush();
        oos.close();
        byte[] b = bos.toByteArray();
        System.out.println("the jdk serializable length is " + b.length);
        bos.close();
        for (byte x : b) {
            System.out.print(x);
        }
        System.out.println("---------------------------");
        System.out.println(info.toString());
        System.out.println("the byte array serializable length is " + info.codeC().length);
        for (byte x : info.codeC()) {
            System.out.print(x);
        }
        System.out.println("\n" + Integer.MAX_VALUE);
    }
}
