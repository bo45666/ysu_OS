package Demo;

public enum MessageType {
    Box1Input(0),
    Box2Input(1),
    Output(2)
    ;
    int value;
    MessageType(int value){
        this.value = value;
    }
    public boolean common(MessageType type){
        if(this.value==type.value) return true;
        return false;
    }
}
