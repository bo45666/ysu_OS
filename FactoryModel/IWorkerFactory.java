package FactoryModel;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2025/1/1 18:14
 */
public class IWorkerFactory {
    public static Worker getWorker(WorkerType type,int id){
        if(type.common(WorkerType.INPUTBOX1)){
            return new inputBox1Worker(type.desc+id);
        }else if(type.common(WorkerType.INPUTBOX2)){
            return new inputBox2Worker(type.desc+id);
        }else if(type.common(WorkerType.GETBOXANDBOX2)){
            return new getBox1AndBox2Worker(type.desc+id);
        }else{
            throw new RuntimeException("can not find this type");
        }
    }
}
