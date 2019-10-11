import ecs100.*;
public class Dynamic{
    public void printAllCost(int n){
        int[] Allcost = new int[n+1];
        int cost = 0;
        Allcost[0] = 0;
        for(int i = 1;i<=n;i++){
            cost = i;
            if(i - 1 >= 0){
                cost = Math.min(cost, Allcost[i-1] + 1);
            }
            if(i - 5 >= 0){
                cost = Math.min(cost, Allcost[i-5] + 1);
            }
            if(i - 11 >= 0){
                cost = Math.min(cost, Allcost[i-11] + 1);
            }
            Allcost[i] = cost;
            UI.println("f[" + i + "]: " + cost);
        }
    }
}
