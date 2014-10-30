import java.util.HashMap;


public class VWord 
{
	VWord parent;
	VWord next;
	String word;
	HashMap<String, Double> tagProbs;
	
	String maxTag = "";
	Double maxTagProb;
	
	public VWord(String word) 
	{
		this.word = word;
		this.maxTagProb = 0.0;
		
		tagProbs = new HashMap<String, Double>();
	}

	@Override
	public String toString() {
		return ""; //word + ", " + tag + ", " + prob;
	}

	public void findMaxTag()
	{
		double currMax = -5000;
		String currMaxTag = "";
		/*System.out.println("");
		System.out.println("========================");
		System.out.println(word);
		System.out.println("========================");
		*/
		for (String tag : tagProbs.keySet()) 
		{
			//System.out.println(tag + " = " + tagProbs.get(tag));
			if(tagProbs.get(tag) > currMax)
			{
				currMax = tagProbs.get(tag);
				currMaxTag = tag;
			}
		}
		
		maxTag = currMaxTag;
		maxTagProb = currMax;
		
		//System.out.println(maxTag + " = " + maxTagProb);
	}
	
	public String getTagForValue(double maxTagValue)
	{
		for (String tag : tagProbs.keySet()) 
		{
			if(tagProbs.get(tag) == maxTagValue)
				return tag;
		}
		
		return "";
	}
}
