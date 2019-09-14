import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Tree {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner file= new Scanner(new File("all_data\\test_c300_d100.csv"));//test only, will move to command line
		String hold= file.nextLine();//gets string of data
		String[] holdarr = hold.split(",");
		int[] arrholdint = new int[holdarr.length];
		for(int i =0; i<arrholdint.length;i++)
		{
			arrholdint[i]= Integer.parseInt(holdarr[i]);
		}
		ArrayList arr = new ArrayList<int[]>();
		arr.add(arrholdint);//puts array of ints representing a data point into arr
		
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
		}//does the same for the rest of the file
		
		//test data split
		double check =gainOfSplit(arr,5);
		
		System.out.println(check);
	}
	public static int pickSplit(ArrayList<int[]> arr)
	{
		for(int i=0;i<arr.size();i++)
		{
			
		}
		return 0;
	}
	public static double gainOfSplit(ArrayList<int[]> arr, int index)
	{
		ArrayList index0 = new ArrayList<Integer>();
		ArrayList index1 = new ArrayList<Integer>();
		ArrayList classnow = new ArrayList<Integer>();
		for(int i=0;i<arr.size();i++)
		{
			classnow.add(arr.get(i)[arr.get(i).length-1]);
			if(arr.get(i)[index]==0)
			{
				index0.add(arr.get(i)[arr.get(i).length-1]);
			}
			else
				index1.add(arr.get(i)[arr.get(i).length-1]);
			
		}
		double hold=entropyOfSplit(classnow)-(index0.size()/classnow.size())*entropyOfSplit(index0)-(index1.size()/classnow.size())*entropyOfSplit(index1);
		
		
		
		
		return hold;
	}
	public static double entropyOfSplit(ArrayList<Integer> arr)
	{
		double split0=0,split1=0;
		for(int i=0;i<arr.size();i++)
		{
			if(arr.get(i)==0)
			{
				split0++;
			}
			else
				split1++;
		}
		
		double frac0= split0/(split0+split1);
		double frac1= split1/(split0+split1);
		double entropy= -(frac0*(Math.log(frac0)/Math.log(2)))- (frac1*(Math.log(frac1)/Math.log(2)));
		return entropy;
	}
}
