import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Tree
{
	public static int[] depths =
	{ 5, 10, 15, 20, 50, 100 };

	public static void main(String[] args) throws FileNotFoundException
	{
		// TODO Auto-generated method stub

		ArrayList<int[]> train = readcsv(args[0]);
		ArrayList<int[]> valid = readcsv(args[1]);
		ArrayList<int[]> test = readcsv(args[2]);

		Node decisions = new Node(-5);
		boolean isV = (args[3].contains("v")||args[3].contains("V"));;
		boolean isPrune = (args[3].contains("p")||args[3].contains("P"));
		boolean isDepth = (args[3].contains("d")||args[3].contains("D"));
		if (args.length == 3)
			decisions = makeTree(train, true, Integer.MAX_VALUE);

		if (args.length == 4)
		{
			if (!isDepth)
			{
				if (isV)
				{
					decisions = makeTree(train, false, Integer.MAX_VALUE);
				} else
				{
					decisions = makeTree(train, true, Integer.MAX_VALUE);
					System.out.print("entropy");
				}
				if (isPrune)
				{
					decisions = Prune(decisions, valid);
					System.out.print("pruning");
				}
			} else
			{
				Node[] vardepths = new Node[6];
				for (int i = 0; i < 6; i++)
				{
					if (isV)
						vardepths[i] = makeTree(train, false, depths[i]);
					else
						vardepths[i] = makeTree(train, true, depths[i]);
				}
				double[] accdepths = new double[6];
				for (int i = 0; i < 6; i++)
				{
					accdepths[i] = checkTree(vardepths[i], valid);
				}
				int index = -1;
				double acc = 0;
				for (int i = 5; i >= 0; i--)
				{
					if (accdepths[i] >= acc)
						acc = accdepths[i];
					index = i;
				}
				decisions = vardepths[index];

				// depth options go here
			}

		}
		// test data split
		System.out.println(checkTree(decisions, test));

	}

	public static Node Prune(Node node, ArrayList<int[]> arr)
	{
		boolean flag = true;
		ArrayList<Double> path = new ArrayList<Double>();
		ArrayList<Double> ans;
		while (flag)
		{
			
			path.add(-1.0);
			path.add(-3.0);
			ans = placehold(node, arr, path);
			//System.out.println(ans.toString());
			if (ans.get(0) > -1)
			{
				ans.remove(0);
				node = nodeRemover(node, ans);
			} else
				flag = false;
			path.clear();
			ans.clear();
		}
		return node;
	}

	public static Node nodeRemover(Node node, ArrayList<Double> path)
	{
		//System.out.println("removal "+path.toString());
		if (path.size() == 1)
		{
			return new Node((int) Math.round(path.get(0)));
		}
		if (path.get(1) == 0.0)
		{
			path.remove(1);
			node.left= nodeRemover(node.left, path);
		} else
		{
			path.remove(1);
			node.right= nodeRemover(node.right, path);
		}
		return node;

	}

	public static ArrayList<Double> placehold(Node node, ArrayList<int[]> arr, ArrayList<Double> path)
	{
		if (node.n < 0)
		{
			//System.out.println("dropped "+path.toString());
			return path;
		}

		ArrayList<int[]> index0 = new ArrayList<int[]>();
		ArrayList<int[]> index1 = new ArrayList<int[]>();
		int num1in0 = 0;
		int num1in1 = 0;
		int numtot = 0;
		for (int i = 0; i < arr.size(); i++)
		{
			if (arr.get(i)[arr.get(i).length-1] == 1)
				numtot++;
			if (arr.get(i)[node.n] == 0)
			{
				index0.add(arr.get(i));
				if (arr.get(i)[arr.get(i).length - 1] == 1)
					num1in0++;
			} else
			{
				index1.add(arr.get(i));
				if (arr.get(i)[arr.get(i).length - 1] == 1)
					num1in1++;
			}

		}
		ArrayList<Double> pathL = new ArrayList<Double>();

		ArrayList<Double> pathR = new ArrayList<Double>();

		for (int i = 0; i < path.size(); i++)
		{
			pathL.add(path.get(i));
			pathR.add(path.get(i));
		}
		pathL.add(0.0);
		pathR.add(1.0);
		ArrayList<Double> lefterr = placehold(node.left, index0, pathL);
		ArrayList<Double> righterr = placehold(node.right, index1, pathR);
		double accuracy = checkTree(node, arr);
		Node temp = new Node((int) Math.round((double) numtot / arr.size()) - 2);
		temp.left = null;
		temp.right = null;
		double check = checkTree(temp, arr);
		if (check >= accuracy)
		{
			
			path.set(0, check);
			path.set(1, (double) temp.n);
			//System.out.println("in "+path.toString());
		}
		double a = lefterr.get(0);
		double b = path.get(0);
		double c = righterr.get(0);
		double ans = Math.max(Math.max(a, b), c);
		if (ans == a)
		{
			//System.out.println("chose left from "+a+" "+b+" "+c);
			return lefterr;
		} else
		{
			if (ans == c)
			{
				return righterr;
			} else
				if(ans==b)
					return path;

		}
		System.out.println("not allowed");
		return path;
	}

	public static Node postPrune(Node node, ArrayList<int[]> arr)
	{
		if (node.n < 0)
			return node;

		ArrayList<int[]> index0 = new ArrayList<int[]>();
		ArrayList<int[]> index1 = new ArrayList<int[]>();
		int num1in0 = 0;
		int num1in1 = 0;
		for (int i = 0; i < arr.size(); i++)
		{

			if (arr.get(i)[node.n] == 0)
			{
				index0.add(arr.get(i));
				if (arr.get(i)[arr.get(i).length - 1] == 1)
					num1in0++;
			} else
			{
				index1.add(arr.get(i));
				if (arr.get(i)[arr.get(i).length - 1] == 1)
					num1in1++;
			}

		}
		node.left = postPrune(node.left, index0);
		node.right = postPrune(node.right, index1);
		double accuracy = checkTree(node, arr);
		Node temp = new Node(node.n);
		temp.left = new Node((int) Math.round((double) num1in0 / index0.size()) - 2);
		temp.right = new Node((int) Math.round((double) num1in1 / index1.size()) - 2);
		double check = checkTree(temp, arr);
		if (check >= accuracy)
		{
			node.left = new Node((int) Math.round((double) num1in0 / index0.size()) - 2);
			node.right = new Node((int) Math.round((double) num1in1 / index1.size()) - 2);
		}
		return node;

	}

	public static double checkTree(Node tree, ArrayList<int[]> arr)
	{
		double viratio = 0;
		for (int i = 0; i < arr.size(); i++)
		{
			int a = Math.abs(arr.get(i)[arr.get(i).length - 1] - checkData(tree, arr.get(i)));

			viratio += a;
		}
		return 1 - viratio / arr.size();
	}

	public static ArrayList<int[]> readcsv(String name) throws FileNotFoundException
	{
		Scanner file = new Scanner(new File(name));// test only, will move to command line
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
		return arr;
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

	public static Node makeTree(ArrayList<int[]> arr, boolean isEntropy, int depth)
	{
		return makeTreeR(null, arr, isEntropy, depth);
	}

	public static Node makeTreeR(Node node, ArrayList<int[]> arr, boolean isEntropy, int depth)
	{

		if (arr.size() <= 1)
			return new Node(arr.get(0)[arr.get(0).length - 1] - 2);
		if (depth == 1)
		{
			int sumi0 = 0;
			for (int i = 0; i < arr.size(); i++)
			{
				sumi0 += arr.get(i)[arr.get(i).length - 1];
			}
			node = new Node((int) Math.round((double) sumi0 / arr.size()) - 2);
			return node;
		}
		int index;
		if (isEntropy)
		{
			index = Entropy.pickSplit(arr);
		} else
			index = VI.pickSplit(arr);
		if (index == -1)
		{
			// System.out.println("hello");
			int sumi0 = 0;
			for (int i = 0; i < arr.size(); i++)
			{
				sumi0 += arr.get(i)[arr.get(i).length - 1];
			}
			node = new Node((int) Math.round((double) sumi0 / arr.size()) - 2);
			return node;

		}
		// System.out.println("goodbye");
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

				node.left = makeTreeR(node.left, index0, isEntropy, depth - 1);
			}
		}

		if (sumi1 == 0)
			node.right = new Node(-2);
		else
		{
			if (sumi1 == index1.size())
				node.right = new Node(-1);
			else
				node.right = makeTreeR(node.right, index1, isEntropy, depth - 1);
		}

		return node;
	}

}
