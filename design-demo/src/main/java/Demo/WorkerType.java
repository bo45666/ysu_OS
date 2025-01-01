package Demo;

public enum WorkerType {
    INPUTBOX1(0,"放入Box1的工人"),
    INPUTBOX2(1,"放入Box2的工人"),
    GETBOXANDBOX2(2,"放入Box3的工人")
    ;
    int value;
    String desc;
    WorkerType(int value,String desc){
        this.value = value;
        this.desc = desc;
    }
    public boolean common(WorkerType type){
        if(this.value==type.value) return true;
        return false;
    }
}
