package ct;


import com.sun.media.jfxmedia.logging.Logger;
import send.SocketMember;
import send.SocketPool;
import send.UnionSocket;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/9/18.
 */
public class TaskNC implements Runnable {

    private static SocketPool socketPool;
    private static byte[] ngc;
    private static int count;
    private static long sleepTime;
    private CountDownLatch latch;

    public TaskNC(Conexct conexct, CountDownLatch latch) throws UnsupportedEncodingException {
        this.socketPool = conexct.getSocketPool();
        this.ngc = conexct.getNgn();
        this.count = conexct.getCount();
        this.sleepTime = conexct.getTime();
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= count; i++) {
                SocketMember socketmember = socketPool.getMemberFromPool();
                UnionSocket sock = socketmember.getSocket();
                if (sock != null) {
                    byte[] result = sock.exchangeData(ngc);
                    String core = new String(result, 8, 4, "ISO-8859-1");
                    if (!"ND00".equals(core))
                        throw new Exception("The result error:"+new String(result,"ISO-8859-1"));
                }
                socketmember.setInUse(false);
//                System.out.println(">>>>>>>>>>>" + Thread.currentThread().getName());
                Thread.sleep(sleepTime);
            }
            latch.countDown();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            log.in
        }
    }


}
