package danhbai;

import java.util.Random;

public class Desk {
	Card[] Desk = new Card[52];
	String[] Set = {"Spades", "Clubs", "Diamonds", "Hearts"};
	String[] Value = {"3","4","5","6","7","8","9","10","Jack","Queen","King","Ace","2"};
	Desk(){	
		for(int i=0;i<52;i++){
			Desk[i] = new Card(i);
		}
	}
	
	//xáo bài
	public void Shuffle() {
		int index;
		Card temp;
	    Random random = new Random();
	    for (int i = Desk.length - 1; i > 0; i--)
	    {
	        index = random.nextInt(i + 1);
	        temp = Desk[index];
	        Desk[index] = Desk[i];
	        Desk[i] = temp;
	    }
	}
	
	
	public void Display(){
		for(int i=0;i<52;i++){
			String ten = "";
			ten = Value[Desk[i].ID/4]+" of "+Set[Desk[i].ID%4];
			System.out.println(ten);
		}
	}
	
//	public static void main(String[] args) {
//		Desk z = new Desk();
//		z.Shuffle();
//		z.Display();
//	}
}
