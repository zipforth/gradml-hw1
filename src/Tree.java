import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Tree
{
	static boolean isEntropy=true;
	public static void main(String[] args) throws FileNotFoundException
	{
		// TODO Auto-generated method stub
		Scanner file = new Scanner(new File("all_data\\train_c300_d100.csv"));// test only, will move to command line
		String hold = file.nextLine();// gets string of data
		String[] holdarr = hold.split(",");
		int[] arrholdint = new int[holdarr.length];
		for (int i = 0; i < arrholdint.length; i++)
		{
			arrholdint[i] = Integer.parseInt(holdarr[i]);
		}
		ArrayList<int[]> arr = new ArrayList<int[]>();
		arr.add(arrholdint);// puts array of ints representing a data point into arr

		while (file.hasNextLine())
		{
			hold = file.nextLine();
			holdarr = hold.split(",");
			arrholdint = new int[holdarr.length];
			for (int i = 0; i < arrholdint.length; i++)
			{
				arrholdint[i] = Integer.parseInt(holdarr[i]);
			}
			arr.add(arrholdint);
		} // does the same for the rest of the file

		// test data split
		Node check = makeTree(arr);
		double ratio = 0;
		for (int i = 0; i < arr.size(); i++)
		{
			int a= Math.abs(arr.get(i)[arr.get(i).length - 1] - checkData(check, arr.get(i)));
			System.out.println(a);
			ratio += a;
		}
		System.out.println(1 - ratio / arr.size());

	}

	public static int checkData(Node node, int[] data)
	{
		if (node == null)
			return -1;
		if (node.n == -1)
		{
			return 1;
		}
		if (node.n == -2)
			return 0;
		if (data[node.n] == 0)
			return checkData(node.left, data);
		else
			return checkData(node.right, data);
	}

	public static Node makeTree(ArrayList<int[]> arr)
	{
		return makeTreeR(null, arr);
	}

	public static Node makeTreeR(Node node, ArrayList<int[]> arr)
	{
		if (arr.size() <= 1)
			return new Node(arr.get(0)[arr.get(0).length-1]-2);
		int index;
		if(isEntropy)
		{
			index = Entropy.pickSplit(arr);
		}
		else
			index = VI.pickSplit(arr);
		if (index == -1)
		{
			System.out.println("hello");
			int sumi0 = 0;
			for (int i = 0; i < arr.size(); i++)
			{
				sumi0 += arr.get(i)[arr.get(i).length - 1];
			}
			node = new Node((int) Math.round((double) sumi0 / arr.size()) - 2);
			return node;

		}
		
		node = new Node(index);
		ArrayList<int[]> index0 = new ArrayList<int[]>();
		ArrayList<int[]> index1 = new ArrayList<int[]>();
		for (int i = 0; i < arr.size(); i++)
		{

			if (arr.get(i)[index] == 0)
			{
				index0.add(arr.get(i));
			} else
				index1.add(arr.get(i));

		}

		int sumi0 = 0, sumi1 = 0;
		for (int i = 0; i < index0.size(); i++)
		{
			sumi0 += index0.get(i)[index0.get(i).length - 1];
		}
		for (int i = 0; i < index1.size(); i++)
		{
			sumi1 += index1.get(i)[index1.get(i).length - 1];
		}

		if (sumi0 == 0)
		{

			node.left = new Node(-2);
		} else
		{
			if (sumi0 == index0.size())
			{

				node.left = new Node(-1);
			} else
			{
				
				node.left = makeTreeR(node.left, index0);
			}
		}

		if (sumi1 == 0)
			node.right = new Node(-2);
		else
		{
			if (sumi1 == index1.size())
				node.right = new Node(-1);
			else
				node.right = makeTreeR(node.right, index1);
		}

		return node;
	}

	
}
