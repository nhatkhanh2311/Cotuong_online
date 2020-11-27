package danhbai;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Card {
	Image img=null;
	String[] Set = {"Spades", "Clubs", "Diamonds", "Hearts"};
	String[] Value = {"3","4","5","6","7","8","9","10","Jack","Queen","King","Ace","2"};
	static String imgsrc = "F:\\Files\\Do_an_mang\\DA_CSNM\\png\\";
	boolean isChoosing = false;
	public int ID;
	public String Name;
	 Card(int ID){
		this.ID = ID;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int cardw = screenSize.width/10;
		int cardh = screenSize.height/4;
		try{
			File f = new File(imgsrc+ID+".png");
			img  = new BufferedImage(cardw, cardh, BufferedImage.TYPE_INT_ARGB);
			img = ImageIO.read(f);
			img = img.getScaledInstance(cardw, cardh, Image.SCALE_SMOOTH);
		}
		catch(IOException e){
			System.out.println("Load image fail!");
		}
		this.Name = Value[ID/4]+" of " +Set[ID%4]+"\tID:"+ID;
	}
}
