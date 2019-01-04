package ct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/9/18.
 */
public class TestNC {
    static Log logger = LogFactory.getLog(TestNC.class);

    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
        Scanner read = new Scanner(System.in);
        System.out.println("Input HSM Host IP:");
        String ip = read.next();
        System.out.println("Input Hsm Port:[1,65535]:");
        int port = read.nextInt();
        System.out.println("Input Hsm Head Length:[0,99]:");
        int header = read.nextInt();
        System.out.println("input number of threads[1,100]:");
        int POOL_NUM = read.nextInt();
        System.out.println("input number of task:");
        int count = read.nextInt();
        System.out.println("input sleep time:[ms]");
        long time = read.nextLong();

        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String head = str.substring(0, header);
        logger.info("HSM connection test..");
        Conexct conexct = null;
        try {
            conexct = new Conexct(ip, port, head, POOL_NUM, POOL_NUM * 2);
        } catch (Exception e) {
            logger.info("HSM connection test fail!");
            e.printStackTrace();
        }
        conexct.setCount(count / POOL_NUM);
        conexct.setTime(time);
        logger.info("HSM connection test successful!");
        logger.info("task:" + count);
        logger.info("threads:" + POOL_NUM);
        logger.info("sleep time:" + time + " ms");
        logger.info("Task tests...");
        final CountDownLatch latch = new CountDownLatch(POOL_NUM);
        long start = System.currentTimeMillis();
        for (int i = 0; i < POOL_NUM; i++) {
            Thread thread = new Thread(new TaskNC(conexct, latch));
            thread.start();
        }
        latch.await();
        long end = System.currentTimeMillis();
        logger.info("end of the task!");
        logger.info("time:" + (end - start) / 1000.0 + " s");
        logger.info("average :" + (count) / ((end - start) / 1000.0) + " ´Î/Ãë");


    }
}
