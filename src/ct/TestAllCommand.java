package ct;

import send.SocketMember;
import send.SocketPool;
import send.UnionSocket;
import util.UnionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Administrator on 2018/9/11.
 */
public class TestAllCommand {


//    static final String t1[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
//
//    public static void main(String[] args) throws Exception {
//        Scanner read = new Scanner(System.in);
//        System.out.println("��������ܻ�ip��");
//        String host = read.next();
//        System.out.println("������˿ںţ�");
//        int port = read.nextInt();
//        System.out.println("������ͷ��");
//        String header = read.next();
//        System.out.println("���ڼ��...");
//        List<String> exist = new ArrayList<>();
//        for (int i = 0; i < 16; i++) {
//            for (int j = 0; j < 16; j++)
//                for (int m = 0; m < 16; m++)
//                    for (int n = 0; n < 16; n++) {
//                        String nt = t1[i] + t1[j] + t1[m] + t1[n];
//                        if (eq(UnionUtil.AllRightZreoTo16Multiple(header), nt, host, port)) {
//                            exist.add(nt);
//                        }
//                    }
//        }
//        System.out.println("���ڵ�ָ��:");
//        exist.forEach(e -> {
//            System.out.print(e + "(" + UnionUtil.HexString2BytesStr(e.substring(0, 2)) + UnionUtil.HexString2BytesStr(e.substring(2)) + ")��");
//        });
//        System.out.println("\n������" + exist.size());
//
//    }



    public static boolean eq(String head, String request, String host, int port) throws Exception {
        byte[] ngc = UnionUtil.hex2byte(head + request);
        byte[] ngn = exchange(ngc, host, port);
        String core = UnionUtil.byte2hex(ngn).substring(20);
        if ("3630".equals(core)) return false;
        return true;
    }

    public static byte[] exchange(byte[] request, String host, int port) throws Exception {
        SocketPool socketPool = new SocketPool(host,port,10,50);
        SocketMember socketmember = socketPool.getMemberFromPool();
        UnionSocket sock = socketmember.getSocket();
        if (sock.connectHSM()) {
            byte[] result = sock.exchangeData(request);
            sock.close();
            if (result != null) return result;
            throw new IOException(String.format("exchange HSM[%s:%d] failed", host, port));
        }
        throw new IOException(String.format("connect HSM[%s:%d] failed", host, port));
    }

}
