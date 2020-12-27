package danhbai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProPlayer {
	static String[] Set = {"Spades", "Clubs", "Diamonds", "Hearts"};
	static String[] Value = {"3","4","5","6","7","8","9","10","Jack","Queen","King","Ace","2"};
	String Name;
	public int ID;
	public Card[] myDesk = new Card[13];
	public boolean isMyTurn = false;
	
	public ProPlayer(String Name){
		this.Name = Name;
	}
	
	
	public String Display() {
		String s="";
		for(int i=0;i<13;i++){			
			s+=myDesk[i].ID+" ";
		}
		return s;
	}
	public void SortCard()
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
    boolean isKind(Card[] current){
    	for(int i=0;i<current.length-1;i++)
    		if(current[i].ID/4!=current[i+1].ID/4) return false;
    	return true;
    }
    boolean isStraight(Card[] current){
    	int len = current.length;
    	if(len<=2) return false;
    	List<Integer> straight = new ArrayList<Integer>();
    	for(Card i : current) {
    		straight.add(i.ID);
    	}
    	Collections.sort(straight);
    	if(current[len-1].ID/4>=12) return false;
    	for(int i=1; i<len; i++) {
			if(straight.get(i) - straight.get(i-1) != 1) return false;
		}
    	return true;
    }
    boolean isStraightPair(Card[] current) {
    	int len = current.length;
		if(len % 2 != 0 || len < 6) return false;
		List<Integer> straight = new ArrayList<Integer>();
		for(Card i : current) {
			straight.add(i.ID);
		}
		Collections.sort(straight);
    	if(current[len-1].ID/4==12) return false;
		for(int i=1; i<len; i+=2) {
			if(straight.get(i) - straight.get(i-1) != 0) return false;
			if(i>2)
				if(straight.get(i) - straight.get(i-2) != 1) return false;
		}
		return true;
	}
    int check(Card[] current,Card[] move){   	
    	if(move==null) return 0;
    	if(current==null && move!=null){
    		if(move.length==1) return 1;
    		else if(move.length==2)
    		{
    			if(move[0].ID/4 == move[1].ID/4) return 1;
    			else return 0;
    		}
    		else if(move.length>=3)
    		{
    			if(isKind(move) || isStraight(move)) return 1;
    		}
    		else return 0;
    	}
    	else if(current.length==1 && move.length==1){
    		if(current[0].ID<move[0].ID) return 1;
    		else return 0;
    	}
    	else if(current.length==2 && move.length==2){
    		if((current[0].ID/4==current[1].ID/4) && (move[0].ID/4==move[1].ID/4))
    			if(max(move)>max(current)) return 1;
    		else return 0;
    	}
    	else if((current.length==3 && move.length==3) || (current.length==4 && move.length==4)){
    		if(isKind(current) && isKind(move)){
    			if(max(move)>max(current)) return 1;
    			else return 0;
    		}
    		if(isStraight(current) && isStraight(move)){
    			if(max(move)>max(current)) return 1;
    			else return 0;
    		}
    		else return 0;
    	}
    	else if(current.length==move.length){
    		if(isStraight(current) && isStraight(move)){
    			if(max(move)>max(current)) return 1;
    			else return 0;
    		}
    		if(isStraightPair(current) && isStraightPair(move)) {
    			if(max(move)>max(current)) return 1;
    			else return 0;
    		}
    		else return 0;
    	}
    	return 0;
    }
    Card[] Pop(Card[] current){
    	for(int i=0; i<current.length; i++){
    		for(int k=0; k<myDesk.length; k++){
    			if(current[i].ID==myDesk[k].ID)
    			{
    				for(int m=k; m<myDesk.length-1; m++)
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
		return (check(current, move)==1) ? true : false;
	}
}
