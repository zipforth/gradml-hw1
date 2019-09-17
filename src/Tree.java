import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Tree
{
	public static int[] depths =
	{ 5, 10, 15, 20, 50, 100 };// this is for depth based

	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub

		ArrayList<int[]> train = readcsv(args[0]);
		ArrayList<int[]> valid = readcsv(args[1]);
		ArrayList<int[]> test = readcsv(args[2]);

		Node decisions = new Node(-5);// becomes decision tree
		boolean isV = (args[3].contains("v") || args[3].contains("V"));// does user want VI
		boolean isPrune = (args[3].contains("p") || args[3].contains("P"));// does user want pruning
		boolean isDepth = (args[3].contains("d") || args[3].contains("D"));// does user want depth based
		if (args.length == 3)// no params passed
			decisions = makeTree(train, true, Integer.MAX_VALUE);// train only with entropy

		if (args.length == 4)// params passed
		{
			if (args[3].contains("f") || args[3].contains("F"))
			{

				System.out.println(randForest(args[0], args[1], args[2]));

			} else
			{
				if (!isDepth)
				{
					if (isV)// trains on VI
					{
						decisions = makeTree(train, false, Integer.MAX_VALUE);
					} else// trains on entropy
					{
						decisions = makeTree(train, true, Integer.MAX_VALUE);
						System.out.print("entropy");
					}
					if (isPrune)// additionally checks validity with pruning
					{
						decisions = Prune(decisions, valid);

					}
				} else// depth based validation
				{
					Node[] vardepths = new Node[6];
					for (int i = 0; i < 6; i++)
					{
						if (isV)// for VI
							vardepths[i] = makeTree(train, false, depths[i]);
						else// for entropy
							vardepths[i] = makeTree(train, true, depths[i]);
					}
					double[] accdepths = new double[6];
					for (int i = 0; i < 6; i++)// checks accuracy of each model on the validation set
					{
						accdepths[i] = checkTree(vardepths[i], valid);
					}
					int index = -1;
					double acc = 0;
					for (int i = 5; i >= 0; i--)// picks most accurate model
					{
						if (accdepths[i] >= acc)
							acc = accdepths[i];
						index = i;
					}
					decisions = vardepths[index];// sets tree to that model

				}
				System.out.println(checkTree(decisions, test));// returns the accuracy of the chosen model on the test
																// data
			}

		}

	}

	public static String randForest(String file1, String file2, String file3) throws Exception
	{
		CSVLoader train = new CSVLoader();
		train.setNoHeaderRowPresent(true);
		train.setSource(new File(file1));

		CSVLoader valid = new CSVLoader();
		valid.setNoHeaderRowPresent(true);
		valid.setSource(new File(file2));

		CSVLoader test = new CSVLoader();
		test.setNoHeaderRowPresent(true);
		test.setSource(new File(file3));

		Instances traindata = train.getDataSet();
		int hold = traindata.numAttributes() - 1;
		traindata.setClassIndex(hold);

		Instances validdata = valid.getDataSet();
		hold = validdata.numAttributes() - 1;
		validdata.setClassIndex(hold);

		Instances testdata = test.getDataSet();
		hold = testdata.numAttributes() - 1;
		testdata.setClassIndex(hold);

		Classifier randomForest = new RandomForest();
		randomForest.buildClassifier(traindata);

		Evaluation eval = new Evaluation(validdata);
		eval.evaluateModel(randomForest, testdata);

		double err = eval.pctCorrect();
		return eval.toSummaryString();
	}

	public static Node Prune(Node node, ArrayList<int[]> arr)
	{
		boolean flag = true;
		ArrayList<Double> path = new ArrayList<Double>();// this lets the function trace back to the node to be removed
		ArrayList<Double> ans;// this gets the accuracy (index 0) class (index 1) and path(index 2..)
		while (flag)
		{

			path.add(-1.0);// placeholder for accuracy
			path.add(-3.0);// placeholder for class
			ans = prunePath(node, arr, path);

			if (ans.get(0) > -1)// if >-1, the algorithm found a best node that benefits from pruning
			{
				ans.remove(0);// trims the accuracy, don't need anymore
				node = nodeRemover(node, ans);// removes the node from the tree
			} else
				flag = false;// didn't find another better one, so drops out
			path.clear();// resetting arraylists
			ans.clear();
		}
		return node;// returns the pruned tree
	}

	public static Node nodeRemover(Node node, ArrayList<Double> path)
	{

		if (path.size() == 1)// it's reached the node to remove
		{
			return new Node((int) Math.round(path.get(0)));// uses previously generated class
		}
		if (path.get(1) == 0.0)// go left
		{
			path.remove(1);// sets up next direction
			node.left = nodeRemover(node.left, path);
		} else// go right
		{
			path.remove(1);// sets up next direction
			node.right = nodeRemover(node.right, path);
		}
		return node;// returns the tree with the newly pruned node

	}

	public static ArrayList<Double> prunePath(Node node, ArrayList<int[]> arr, ArrayList<Double> path)
	{
		if (node.n < 0)// therefore leaf, don't have to check to prune
		{
			return path;
		}

		ArrayList<int[]> index0 = new ArrayList<int[]>();
		ArrayList<int[]> index1 = new ArrayList<int[]>();
		int num1in0 = 0;
		int num1in1 = 0;
		int numtot = 0;
		for (int i = 0; i < arr.size(); i++)// splits data points on node and calculates the number of ones in the set
		{
			if (arr.get(i)[arr.get(i).length - 1] == 1)
				numtot++;
			if (arr.get(i)[node.n] == 0)// if index ==0, put into 0 split
			{
				index0.add(arr.get(i));
				if (arr.get(i)[arr.get(i).length - 1] == 1)
					num1in0++;
			} else// same as above, but with ones in the 1 split
			{
				index1.add(arr.get(i));
				if (arr.get(i)[arr.get(i).length - 1] == 1)
					num1in1++;
			}

		}
		ArrayList<Double> pathL = new ArrayList<Double>();

		ArrayList<Double> pathR = new ArrayList<Double>();

		for (int i = 0; i < path.size(); i++)// new paths based on old to avoid shallow copy
		{
			pathL.add(path.get(i));
			pathR.add(path.get(i));
		}
		pathL.add(0.0);// adds a left direction
		pathR.add(1.0);// adds a right direction
		ArrayList<Double> lefterr = prunePath(node.left, index0, pathL);// gets the max of left and right
		ArrayList<Double> righterr = prunePath(node.right, index1, pathR);
		double accuracy = checkTree(node, arr);
		Node temp = new Node((int) Math.round((double) numtot / arr.size()) - 2);
		temp.left = null;
		temp.right = null;
		double check = checkTree(temp, arr);
		if (check >= accuracy)// if old accuracy is < accuracy as a leaf
		{

			path.set(0, check);// set accuracy
			path.set(1, (double) temp.n);// set class

		}
		double a = lefterr.get(0);
		double b = path.get(0);
		double c = righterr.get(0);
		double ans = Math.max(Math.max(a, b), c);// gets the max accuracy increase
		if (ans == a)
		{

			return lefterr;
		} else
		{
			if (ans == c)
			{
				return righterr;
			} else if (ans == b)
				return path;

		}

		return path;// returns the path to the largest accuracy so far
	}

	public static double checkTree(Node tree, ArrayList<int[]> arr)
	{
		double viratio = 0;
		for (int i = 0; i < arr.size(); i++)
		{
			int a = Math.abs(arr.get(i)[arr.get(i).length - 1] - checkData(tree, arr.get(i)));
			// if the target classification matches the prediction, a will be 0, else a is 1

			viratio += a;// this tallies all the wrong guesses
		}
		return 1 - (viratio / arr.size());// gets the accuracy
	}

	public static ArrayList<int[]> readcsv(String name) throws FileNotFoundException
	{
		Scanner file = new Scanner(new File(name));
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
		return arr;// gives an arraylist of arrays to be used as data in the tree making
	}

	public static int checkData(Node node, int[] data)
	{
		// keeps going through the tree until a leaf is found, return the leaf value
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

	public static Node makeTree(ArrayList<int[]> arr, boolean isEntropy, int depth)// wrapper for recursion
	{
		return makeTreeR(null, arr, isEntropy, depth);
	}

	public static Node makeTreeR(Node node, ArrayList<int[]> arr, boolean isEntropy, int depth)
	{

		if (arr.size() <= 1)// there's only one data point, so make a leaf with that value
			return new Node(arr.get(0)[arr.get(0).length - 1] - 2);
		if (depth == 1)// for depth restriction,
		{
			int sumi0 = 0;
			for (int i = 0; i < arr.size(); i++)
			{
				sumi0 += arr.get(i)[arr.get(i).length - 1];
			}
			node = new Node((int) Math.round((double) sumi0 / arr.size()) - 2);
			return node;// assigns most likely value from the set to this leaf
		}
		int index;
		if (isEntropy)// entropy or VI
		{
			index = Entropy.pickSplit(arr);
		} else
			index = VI.pickSplit(arr);
		if (index == -1)
		{
			// there are no more better splits, so create a leaf
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

		if (sumi0 == 0)// if the values are all the same, do these
		{

			node.left = new Node(-2);
		} else
		{// or continue building the tree
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
