package test;

import send.SocketPool;

import java.io.IOException;

/**
 * Created by Administrator on 2018/9/19.
 */
public class TestPool {
    public static void main(String[] args) throws IOException {
        SocketPool socketPool = new SocketPool("192.168.0.189",8,10,50);
        System.out.println(socketPool.getInitSize());

    }
}
