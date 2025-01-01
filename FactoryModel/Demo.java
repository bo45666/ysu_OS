package FactoryModel;

import com.sun.corba.se.spi.orbutil.threadpool.Work;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2025/1/1 17:59
 */
public class Demo {
    public static void main(String[] args) {
        resource.box1Capacity = 10;
        resource.box2Capacity = 20;
        int inputBox1 = 4;
        int inputBox2 = 4;
        int get = 5;
        for(int i=0;i<inputBox1;++i){
            IWorkerFactory.getWorker(WorkerType.INPUTBOX1,i+1).start();
        }
        for(int i=0;i<inputBox2;++i){
            IWorkerFactory.getWorker(WorkerType.INPUTBOX2,i+1).start();
        }
        for(int i=0;i<get;++i){
            IWorkerFactory.getWorker(WorkerType.GETBOXANDBOX2,i+1).start();
        }
    }
}
class resource{
    public static ReentrantLock box1MutiLock =new ReentrantLock();
    public static ReentrantLock box2MutiLock =new ReentrantLock();
    public static int box1Capacity;
    public static int box2Capacity;
    public static volatile int  box1Num=0;
    public static volatile int box2Num=0;
    public static int inputBox1Time = 100;
    public static int inputBox2Time = 200;
    public static int getBox1AndBox2Time = 500;
    public static int desk = 0;
    public static int deskMax = 30;
}
class inputBox1Worker implements Worker{
    private Thread thread;
    private String name;
    inputBox1Worker(String name){
        thread = new Thread(()->{
            while(true) {
                if(resource.box1Num>=resource.box1Capacity) continue;
                boolean getlock = resource.box1MutiLock.tryLock();
                try {
                    if (getlock) {
                        if (resource.box1Num >= resource.box1Capacity) {
                            System.out.println("Box1已满");
                        }else{
                            resource.box1Num+=1;
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
                if(resource.desk>=resource.deskMax) break;
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
    inputBox2Worker(String name){
        thread = new Thread(()->{
            while(true) {
                if(resource.box2Num>= resource.box2Capacity-1) continue;
                boolean getlock = resource.box2MutiLock.tryLock();
                try {
                    if (getlock) {
                        if(resource.box2Num >= resource.box2Capacity-1){
                            System.out.println("Box2已满");
                        }else{
                            resource.box2Num+=2;
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
                if(resource.desk>=resource.deskMax) break;
            }
        },name);
    }
    public void start(){
        thread.start();
    }
}
class getBox1AndBox2Worker implements Worker {
    private Thread thread;
    private String name;
    getBox1AndBox2Worker(String name){
        thread = new Thread(()->{
            while(true) {
                boolean getLock1 = resource.box1MutiLock.tryLock();
                boolean getLock2 = resource.box2MutiLock.tryLock();
                try {
                    if(getLock1&&getLock2){
                        if(resource.box1Num<1){
                            System.out.println("Box1不足1");
                            throw new RuntimeException();
                        }
                        if(resource.box2Num<2){
                            System.out.println("Box2不足2");
                            throw new RuntimeException();
                        }
                        resource.box2Num-=2;
                        resource.box1Num-=1;
                        resource.desk++;
                        System.out.println(Thread.currentThread().getName()+"组装成功"+"Box1="+resource.box1Num+" Box2="+resource.box2Num+" desk="
                                +resource.desk);
                    }
                } catch (RuntimeException e) {
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

