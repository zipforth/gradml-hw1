import java.util.ArrayList;

public class Entropy
{
	public static int pickSplit(ArrayList<int[]> arr)// this is called by the tree builder
	{
		int index = -1;
		double hold = 0;
		for (int i = 0; i < arr.get(0).length - 1; i++)
		{
			double eachgain = gainOfSplit(arr, i);
			if (eachgain > hold)// selects the maximum gain index
			{
				index = i;
				hold = eachgain;

			}

		}
		return index;// returns the index that splits the data best
	}

	public static double gainOfSplit(ArrayList<int[]> arr, int index)
	{
		ArrayList<Integer> index0 = new ArrayList<Integer>();
		ArrayList<Integer> index1 = new ArrayList<Integer>();
		ArrayList<Integer> classnow = new ArrayList<Integer>();
		for (int i = 0; i < arr.size(); i++)
		{
			classnow.add(arr.get(i)[arr.get(i).length - 1]);// gets target class
			if (arr.get(i)[index] == 0)// splits the target column in 2 based on index given
			{
				index0.add(arr.get(i)[arr.get(i).length - 1]);
			} else
				index1.add(arr.get(i)[arr.get(i).length - 1]);

		}
		double a, b, c, d, e;// gets entropy for each array of target classes
		a = entropyOfSplit(classnow);
		b = ((double) index0.size() / classnow.size());
		c = entropyOfSplit(index0);
		d = ((double) index1.size() / classnow.size());
		e = entropyOfSplit(index1);

		double hold = a - (b * c) - (d * e);
		// current E - prob of E0 - prob of E1

		return hold;// returns gain of split
	}

	public static double entropyOfSplit(ArrayList<Integer> arr)
	{
		double split0 = 0, split1 = 0;
		for (int i = 0; i < arr.size(); i++)
		{
			if (arr.get(i) == 0)
			{
				split0++;
			} else
				split1++;
		}
		// sums all 1s and 0s
		double frac0 = (split0 + 1) / (split0 + split1 + 2);// calculates the likelihood of 1s and 0s
		double frac1 = (split1 + 1) / (split0 + split1 + 2);// laplace smoothing for smaller data sets

		double entropy = -(frac0 * (Math.log(frac0) / Math.log(2))) - (frac1 * (Math.log(frac1) / Math.log(2)));
		return entropy;// returns the entropy
	}
}
