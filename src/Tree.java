import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Tree {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner file= new Scanner(new File("all_data\\test_c300_d100.csv"));
		String hold= file.nextLine();
		String[] holdarr = hold.split(",");
		int[] arrholdint = new int[holdarr.length];
		for(int i =0; i<arrholdint.length;i++)
		{
			arrholdint[i]= Integer.parseInt(holdarr[i]);
		}
		ArrayList arr = new ArrayList<Integer[]>();
		arr.add(arrholdint);
		
		while(file.hasNextLine())
		{
			hold= file.nextLine();
			holdarr = hold.split(",");
			arrholdint = new int[holdarr.length];
			for(int i =0; i<arrholdint.length;i++)
			{
				arrholdint[i]= Integer.parseInt(holdarr[i]);
			}
			arr.add(arrholdint);
		}
		System.out.println(arr.size());
	}

}
