package send;

/**
 * Created by Administrator on 2018/9/18.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SocketPool {
    Log logger = LogFactory.getLog(SocketPool.class);
    //host
    private String host;
    //port
    private int port;
    //��ʼ��socket��
    private int initSize=1;
    //���socket��
    private int maxSize=1;
    //socket��������
    private List<SocketMember> socketContainer=new ArrayList<SocketMember>(initSize);

    public SocketPool(String host,int port,int intitSize,int maxSize) throws IOException {
        this.host=host;
        this.port=port;
        this.initSize=intitSize;
        this.maxSize=maxSize;
        buildPoolPart();
    }
    //��socket�������ӳ�Ա һ������initSize����Ա
    private List<SocketMember> buildPoolPart() throws IOException {
        List<SocketMember> poolPart=new ArrayList<SocketMember>(initSize);
        SocketMember member=null;
        for(int i=0;i<initSize;i++){
            UnionSocket socket=null;
            socket = new UnionSocket(0, 0);
            socket.setTimeOut(100);
            if(!socket.connectHSM(host,port)){
                throw new IOException(String.format("connect HSM[%s:%d] failed", host, port));
            }
            member=new SocketMember();
            member.setSocket(socket);
            poolPart.add(member);
        }
        if(poolPart.size()>0){
            socketContainer.addAll(poolPart);
        }else{
            try {
                throw new Exception("���������ʧ��");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return poolPart;
    }
    //��ȡ���п��е�socket �����̰߳�ȫ����
    public SocketMember getMemberFromPool() throws IOException {
        SocketMember member=null;
        //ͬ�����ȡ����
        synchronized (this) {
            for(int i=0;i<socketContainer.size();i++){
                SocketMember memberInPool=socketContainer.get(i);
                boolean inUse=memberInPool.isInUse();
                if(inUse==false){
                    memberInPool.setInUse(true);
                    member=memberInPool;
                    logger.debug("�ɹ���ȡ����,�ڳ��е�λ��Ϊ��"+i);
                    break;
                }
            }
            //pool��û�п���
            if(member==null){
                if(socketContainer.size()<maxSize){
                    //���������
                    List<SocketMember> newPoolPart=buildPoolPart();
                    //��������Ĳ����ó�һ������
                    member=newPoolPart.get(0);
                    member.setInUse(true);
                    logger.info("�ɹ����������,��ǰsizeΪ:"+socketContainer.size());
                }
            }
        }
        //�������������� �ȴ� �ݹ�
        if(member==null){
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            member=getMemberFromPool();
        }
//        member.setInUse(false);
        return member;

    }

    public int getInitSize() {
        return initSize;
    }

    public void setInitSize(int initSize) {
        this.initSize = initSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    public List<SocketMember> getSocketContainer() {
        return socketContainer;
    }
    public String getHost() {
        return host;
    }
    public int getPort() {
        return port;
    }



}