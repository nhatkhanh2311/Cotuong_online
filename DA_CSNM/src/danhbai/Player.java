package danhbai;
import java.util.*;
public class Player {
	String[] Set = {"Spades", "Clubs", "Diamonds", "Hearts"};
	String[] Value = {"3","4","5","6","7","8","9","10","Jack","Queen","King","Ace","2"};
	String Name;
	public int ID;
	public int Money;
	public Card[] myDesk = new Card[13];
	public int isMyTurn = 0;
	
	public Player(String Name){
		this.Name = Name;

	}
	
	public String Display() {
		String s="";
		for(int i=0;i<13;i++){			
			s+=myDesk[i].ID+" ";
		}
		return s;
	}
    public void XepBai()
    {
        int n = this.myDesk.length;
 
        for (int i = 0; i < n-1; i++)
        {
            int min_idx = i;
            for (int j = i+1; j < n; j++)
                if (myDesk[j].ID < myDesk[min_idx].ID)
                    min_idx = j;
            Card temp = myDesk[min_idx];
            myDesk[min_idx] = myDesk[i];
            myDesk[i] = temp;
        }
    }
    int max(Card[] current){
    	int max=current[0].ID;
    	for(int i=0;i<current.length;i++){
    		if(current[i].ID>max) max = current[i].ID;
    	}
    	return max;
    	
    }
    int SameValue(Card[] current){
    	int i;
    	for(i=0;i<current.length-1;i++)
    		if(current[i].ID/4!=current[i+1].ID/4) return 0;
    	return 1;
    }
    int Sanh(Card[] current){
    	if(current.length<=2) return 0; //Xếp bài bài
    	int n = current.length;
    	for (int i = 0; i < n-1; i++)
        {
            int min_idx = i;
            for (int j = i+1; j < n; j++)
                if (current[j].ID < current[min_idx].ID)
                    min_idx = j;
            Card temp = current[min_idx];
            current[min_idx] = current[i];
            current[i] = temp;
        }
    	if(current[current.length-1].ID/4>=12) return 0; //Sảnh không có lá 2 ở cuối
    	int i;
    	for(i=0;i<current.length-1;i++)
    		if(current[i].ID/4!=current[i+1].ID/4-1) return 0; // Xem thử có liên tiếp nhau nhau hay không
    	
    	return 1;
    }
    int ktra(Card[] current,Card[] move){ //Cạch lẻ
    	
    	if(move==null) return 0;
    	if(current==null&&move!=null){
    		if(move.length==1) return 1;
    		else if(move.length==2)
    		{
    			if(move[0].ID/4==move[1].ID/4) return 1;
    			else return 0;
    		}
    		else if(move.length>=3)
    		{
    			if(SameValue(move)==1||Sanh(move)==1) return 1;
    		}
    		else return 0;
    	}
    	else if(current.length==1&&move.length==1){
    		if(current[0].ID<move[0].ID) return 1;
    		else return 0;
    	}
    	else if(current.length==2&&move.length==2){// đánh đôi
    		if((current[0].ID/4==current[1].ID/4)&&(move[0].ID/4==move[1].ID/4))
    			if(max(move)>max(current)) return 1;
    		else return 0;
    	}
    	else if((current.length==3&&move.length==3)||(current.length==4&&move.length==4)){ //đánh same
    		if(SameValue(current)==1&&SameValue(move)==1){
    			if(max(move)>max(current)) return 1;
    			else return 0;
    		}
    		if(Sanh(current)==1&&Sanh(move)==1){
    			if(max(move)>max(current)) return 1;
    			else return 0;
    		}
    		else return 0;
    	}
    	else if(current.length==move.length){
    		if(Sanh(current)==1&&Sanh(move)==1){
    			if(max(move)>max(current)) return 1;
    			else return 0;
    		}
    		else return 0;
    	}
    	return 0;
    }
    
    Card[] Pop(Card[] current){
    	for(int i=0;i<current.length;i++){
    		for(int k=0;k<myDesk.length;k++){
    			if(current[i].ID==myDesk[k].ID)
    			{
    				for(int m=k;m<myDesk.length-1;m++)
    				{
    					myDesk[m]=myDesk[m+1];
    				}
    				myDesk = Arrays.copyOfRange(myDesk, 0, myDesk.length-1);
    			}
    		}
    	}
    	return myDesk;
    }
    Card getCard(int ID){
    	for(int i=0;i<myDesk.length;i++){
    		if(myDesk[i].ID==ID) return myDesk[i];
    	}
    	return null;
    }
    boolean PlayCard(Card[] current,Card[] move){
		return (ktra(current, move)==1) ? true : false;
	}
	
}