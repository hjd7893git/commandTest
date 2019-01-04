package send;

import java.net.Socket;

/**
 * Created by Administrator on 2018/9/18.
 */
public class SocketMember {
    //socket对象
    private UnionSocket unionsocket;
    //是否正被使用
    private boolean inUse=false;
    public UnionSocket getSocket() {
        return unionsocket;
    }
    public void setSocket(UnionSocket socket) {
        this.unionsocket = socket;
    }
    public boolean isInUse() {
        return inUse;
    }
    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

}
