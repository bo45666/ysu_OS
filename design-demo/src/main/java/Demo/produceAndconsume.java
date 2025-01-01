package Demo;

import com.sun.org.apache.bcel.internal.generic.LoadClass;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ：cdb
 * @description：TODO
 * @date ：2024/12/30 18:05
 */

public class produceAndconsume {
    public static void main(String[] args) {
        resource.box1Capacity = 10;
        resource.box2Capacity = 20;
        int inputBox1 = 4; //向box1中放入的工人数量
        int inputBox2 = 4; //向box2中放入的工人数量
        int get = 5; //从box1和box2中取出组装的工人数量
        for(int i=0;i<inputBox1;++i){
            IWorkerFactory.getWorker(WorkerType.INPUTBOX1,i+1).start();
        }
        for(int i=0;i<inputBox2;++i){
            IWorkerFactory.getWorker(WorkerType.INPUTBOX2,i+1).start();
        }
        for(int i=0;i<get;++i){
            IWorkerFactory.getWorker(WorkerType.GETBOXANDBOX2,i+1).start();
        }
        SwingUtilities.invokeLater(() -> new gui(resource.blockingQueue));

    }
}
class resource{
    public static ReentrantLock box1MutiLock =new ReentrantLock();
    public static ReentrantLock box2MutiLock =new ReentrantLock();
    public static int box1Capacity; //box1容量
    public static int box2Capacity; //box2容量
    public static volatile int  box1Num=0;
    public static volatile int box2Num=0;
    public static int inputBox1Time = 100; //每个人向box1放入的时间间隔，单位ms
    public static int inputBox2Time = 200; //每个人向box2放入的时间间隔，单位ms
    public static int getBox1AndBox2Time = 500; //每个人从box1和box2取出的时间间隔，单位ms
    public static int desk = 0;
    public static int deskMax = 30; //组装多少桌子停止
    public static BlockingQueue<Message> blockingQueue = new LinkedBlockingQueue<>();
}
class inputBox1Worker implements Worker{
    private Thread thread;
    private String name;
    private static MessageType type = MessageType.Box1Input;
    inputBox1Worker(String name){
        thread = new Thread(()->{
            while(true) {
                if(resource.box1Num>=resource.box1Capacity) continue;
                boolean getlock = resource.box1MutiLock.tryLock();
                try {
                    if (getlock) {
                        if (resource.box1Num >= resource.box1Capacity) {
                            resource.blockingQueue.offer(new Message(type,"Box1已满"));
                            System.out.println();
                        }else{
                            resource.box1Num+=1;
                            resource.blockingQueue.offer(new Message(type,Thread.currentThread().getName()+"放入Box1 "+resource.box1Num));
                            System.out.println(Thread.currentThread().getName()+"放入Box1 "+resource.box1Num);
                        }
                    }
                }finally{
                    if(getlock) resource.box1MutiLock.unlock();
                    try {
                        Thread.sleep(resource.inputBox1Time);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(resource.desk+resource.box1Num>=resource.deskMax) break;
            }
        },name);
    }
    public void start(){
        thread.start();
    }
}

class inputBox2Worker implements Worker{
    private Thread thread;
    private String name;
    private static MessageType type = MessageType.Box2Input;
    inputBox2Worker(String name){
        thread = new Thread(()->{
            while(true) {
                if(resource.box2Num>= resource.box2Capacity) continue;
                boolean getlock = resource.box2MutiLock.tryLock();
                try {
                    if (getlock) {
                        if(resource.box2Num >= resource.box2Capacity){
                            resource.blockingQueue.offer(new Message(type,"Box2已满"));
                            System.out.println("Box2已满");
                        }else{
                            resource.box2Num+=1;
                            resource.blockingQueue.offer(new Message(type,Thread.currentThread().getName()+"放入Box2 "+resource.box2Num));
                            System.out.println(Thread.currentThread().getName()+"放入Box2 "+resource.box2Num);
                        }
                    }
                }finally {
                    if(getlock) resource.box2MutiLock.unlock();
                    try {
                        Thread.sleep(resource.inputBox2Time);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(resource.desk+resource.box2Num>=resource.deskMax) break;
            }
        },name);
    }
    public void start(){
        thread.start();
    }
}
class getBox1AndBox2Worker implements Worker{
    private Thread thread;
    private String name;
    private static MessageType type = MessageType.Output;
    getBox1AndBox2Worker(String name){
        thread = new Thread(()->{
            while(true) {
                boolean getLock1 = resource.box1MutiLock.tryLock();
                boolean getLock2 = resource.box2MutiLock.tryLock();
                try {
                    if(getLock1&&getLock2){
                        if(resource.box1Num<1){
                            resource.blockingQueue.offer(new Message(type,"Box1不足"));
                            System.out.println("Box1不足1");
                            throw new RuntimeException();
                        }
                        if(resource.box2Num<1){
                            resource.blockingQueue.offer(new Message(type,"Box2不足"));
                            System.out.println("Box2不足");
                            throw new RuntimeException();
                        }
                        resource.box2Num-=1;
                        resource.box1Num-=1;
                        resource.desk++;
                        resource.blockingQueue.offer(new Message(type,Thread.currentThread().getName()+"组装成功"+"Box1="+resource.box1Num+" Box2="+resource.box2Num+" desk="
                                +resource.desk));
                        System.out.println(Thread.currentThread().getName()+"组装成功"+"Box1="+resource.box1Num+" Box2="+resource.box2Num+" desk="
                        +resource.desk);
                    }
                } catch (RuntimeException e) {
                    resource.blockingQueue.offer(new Message(type,"无法组装"));
                    System.out.println("无法组装");
                } finally {
                    if(getLock1) resource.box1MutiLock.unlock();
                    if(getLock2) resource.box2MutiLock.unlock();
                    try {
                        Thread.sleep(resource.getBox1AndBox2Time);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            if(resource.desk>=resource.deskMax){
                resource.blockingQueue.offer(new Message(type,"desk="+resource.desk));
                System.out.println("desk="+resource.desk);
                break;
            }
            }
        },name);
    }
    public void start(){
        thread.start();
    }
}

