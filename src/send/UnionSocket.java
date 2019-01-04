package send;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


public class UnionSocket {
    private final int TIME_OUT = 1* 1000;
    public int serial;
    public int HsmIdx;
    private Socket sock;
    private InputStream is;
    private OutputStream os;
    private int iTimeOut = 0;
    private boolean TimeOutFlag;

    private boolean IsConn;
    private String HostIP = null;
    private int HostPort = -1;
    public UnionSocket(int idx, int iserial) {
        sock = null;
        is = null;
        os = null;
        IsConn = false;
        iTimeOut = TIME_OUT;
        TimeOutFlag = false;
        serial = iserial;
        HsmIdx = idx;
    }

    public void setHostPort(String ip, int port) throws Exception {
        this.HostIP = ip;
        this.HostPort = port;
    }

    public String getHost() {
        return HostIP;
    }

    public int getPort() {
        if (HostPort < 0 || HostPort > 65535) {
            return HostPort;
        }
        return HostPort;
    }

    public boolean connectHSM() {
        return (null != HostIP) && (-1 != HostPort) && this.connectHSM(HostIP, HostPort);
    }

    protected boolean connectHSM(String ip, int port) {
        try {
            sock = new Socket();
            // sock.setTcpNoDelay(false);
            sock.setSoLinger(true, 2);
            sock.connect(new InetSocketAddress(ip, port), this.iTimeOut);
            is = sock.getInputStream();
            os = sock.getOutputStream();
            IsConn = true;
            sock.setSoTimeout(this.iTimeOut);
        } catch (InterruptedIOException iioe) {
            TimeOutFlag = true;
            IsConn = false;
            iioe.printStackTrace();
        } catch (Exception e) {
            IsConn = false;
            e.printStackTrace();
        }
        return IsConn;
    }


    public byte[] exchangeData(String in) throws IOException {
        byte[] msg = in.getBytes("ISO-8859-1");
        sendToHSM(msg);
        return recvFromHSM();
    }

    public byte[] exchangeData(byte[] in) throws IOException {
        sendToHSM(in);
        return recvFromHSM();
    }

    private void sendToHSM(byte[] msg) throws IOException {
        ByteArrayOutputStream buff = new ByteArrayOutputStream(msg.length + 2);
        buff.write(msg.length / 256);
        buff.write(msg.length % 256);
        buff.write(msg);
        os.write(buff.toByteArray());
        os.flush();
    }

    private byte[] recvFromHSM() throws IOException {
        byte[] out1;
        byte[] out2;
        int iRcvLen;

        out1 = new byte[2];
        iRcvLen = is.read(out1);
        if (iRcvLen != 2) throw new IllegalStateException("HSM does't return 2 bytes header of Length");
        final int nextLen = (out1[0] & 0xff) * 256 + (out1[1] & 0xff);
        out2 = new byte[nextLen];
        int realLen = 0;
        for (int i = 0; i < 3; i++) {
            realLen += is.read(out2, realLen, nextLen - realLen);
            if (realLen == nextLen) {
                break;
            }
        }

        if (realLen > 0) {
            return out2;
        } else {
            out1 = null;
            out2 = null;
            throw new IllegalStateException("Hsm Message Protocol Error");
        }
    }

    /**
     *
     */
    public void close() {
        try {
            if (os != null) os.close();
            if (is != null) is.close();
            if (sock != null) sock.close();
            this.IsConn = false;
        } catch (Exception e) {
            this.IsConn = false;
        }
    }

    protected boolean IsConnected() {
        return this.IsConn;
    }

    public void setTimeOut(int iVal) {
        if (iVal >= 0)
            this.iTimeOut = iVal * 1000;
        else
            this.iTimeOut = this.TIME_OUT;
    }

    protected boolean getTimeOutFlag() {
        return this.TimeOutFlag;
    }
}
