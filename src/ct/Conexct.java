package ct;

import send.SocketPool;

/**
 * Created by Administrator on 2018/9/19.
 */
public class Conexct {
    private byte[] ngc;
    private int count;
    private long sleepTime;
    private SocketPool socketPool;
    public Conexct(String host, int port, String head, int links, int maxSize) throws Exception {
        this.ngc = (head + "NC").getBytes("ISO-8859-1");
        socketPool = new SocketPool(host, port, links, maxSize);
    }
    public SocketPool getSocketPool() {
        return socketPool;
    }
    public void setTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }
    public long getTime() {
        return this.sleepTime;
    }
    public byte[] getNgn() {
        return this.ngc;
    }
    public int getCount() {
        return this.count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
