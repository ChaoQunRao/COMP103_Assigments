public class Mystack{
    private Object data[];
    private int top;
    public Mystack(){
        data = new Object[10];
        top = -1;
    }
    public Mystack(int size){
        data = new Object[size];
        top = -1;
    }
    public void push(Object obj){
        // if(this.needExpend()){
            // Object[] newData = new Object[data.length*2+2]; 
            // for(int i=0;i<data.length;i++){
                // newData[i] = data[i];
            // }
            // data = newData;
        // }
        top++;
        data[top] = obj;
    }
    public Object pop(){
        return data[top--];
    }
    public boolean isEmpty(){
        return top==-1;
    }
    public boolean needExpend(){
        return data.length <= top+1;
    }
}
