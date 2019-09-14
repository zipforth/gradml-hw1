import java.util.ArrayList;

public class Entropy
{
	public static int pickSplit(ArrayList<int[]> arr)
	{
		int index = -1;
		double hold = 0;
		for (int i = 0; i < arr.get(0).length - 1; i++)
		{
			double eachgain = gainOfSplit(arr, i);
			if (eachgain > hold)
			{
				index = i;
				hold = eachgain;
				// System.out.println("index "+i+ "gain "+eachgain);
			}
		}
		return index;
	}

	public static double gainOfSplit(ArrayList<int[]> arr, int index)
	{
		ArrayList<Integer> index0 = new ArrayList<Integer>();
		ArrayList<Integer> index1 = new ArrayList<Integer>();
		ArrayList<Integer> classnow = new ArrayList<Integer>();
		for (int i = 0; i < arr.size(); i++)
		{
			classnow.add(arr.get(i)[arr.get(i).length - 1]);
			if (arr.get(i)[index] == 0)
			{
				index0.add(arr.get(i)[arr.get(i).length - 1]);
			} else
				index1.add(arr.get(i)[arr.get(i).length - 1]);

		}
		double hold = entropyOfSplit(classnow) - ((double) index0.size() / classnow.size()) * entropyOfSplit(index0)
				- ((double) index1.size() / classnow.size()) * entropyOfSplit(index1);

		return hold;
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

		double frac0 = split0 / (split0 + split1);
		double frac1 = split1 / (split0 + split1);
		double entropy = -(frac0 * (Math.log(frac0) / Math.log(2))) - (frac1 * (Math.log(frac1) / Math.log(2)));
		return entropy;
	}
}
