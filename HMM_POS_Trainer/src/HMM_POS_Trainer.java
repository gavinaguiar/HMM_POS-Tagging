import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.StringTokenizer;

public class HMM_POS_Trainer 
{

	static HashMap<String,Integer> tagCount = new HashMap<String, Integer>();
	static HashMap<String, Integer> bigramTagCount = new HashMap<String, Integer>();
	static HashMap<String, Integer> wordAndTagCount = new HashMap<String, Integer>();
	static double totalTags;
	static String trainingFile;
	static String hmmPosModel;
	
	public HMM_POS_Trainer(String trainingFile, String hmmPosModel) 
	{
		this.trainingFile = trainingFile;
		this.hmmPosModel = hmmPosModel;
	}
	
	public static void main(String[] args) throws Exception
	{
		
		if(args.length < 2	)
		{
			System.out.println("\nSyntax Error: Path of input files expected.\nUsage: java -jar HMM_POS_Trainer.jar <TRAINING_FILE_Path> <OUTPUT_FILE_Path>\n");
			return;
		}
		
		new HMM_POS_Trainer(args[0],args[1]);
		
		//URL url = new URL( "http://www.hlt.utdallas.edu/~yangl/cs6320/homework/hw3_train");
		//BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		FileReader fr = new FileReader(trainingFile);
		BufferedReader br = new BufferedReader(fr);
		
		FileWriter fw = new FileWriter(new File(hmmPosModel));
		BufferedWriter bw = new BufferedWriter(fw);
		
		String line;

		while((line = br.readLine()) !=null)
		{
			line = "<StartingWord>/<StartingWordTag> " + line+ " <EndingWord>/<EndingWordTag>" ;
			StringTokenizer st = new StringTokenizer(line, " ");
			int counter = 0;
			String tempBigram[] = new String[2];
			
			// filing all hashmaps
			while(st.hasMoreTokens())
			{
				totalTags++;
				String currentWord = st.nextToken();
				String[] splitWord = currentWord.split("/");

				// getting count of tags
				if(tagCount.containsKey(splitWord[1]))
				{
					tagCount.put(splitWord[1], tagCount.get(splitWord[1]) +1);
				}
				else
				{
					tagCount.put(splitWord[1], 1);
				}
				
				// getting word and tag count
				if(wordAndTagCount.containsKey(splitWord[0] + " "+ splitWord[1]))
					wordAndTagCount.put(splitWord[0] + " "+ splitWord[1], wordAndTagCount.get(splitWord[0] + " "+ splitWord[1])+1);
				else
				{
					wordAndTagCount.put(splitWord[0] + " "+ splitWord[1],1);
					
				}	

				// getting bigrams of tags
				if (counter < 2)
				{
					tempBigram[counter] =  splitWord[1]; 
					counter ++;
				}
				else
				{
					if(bigramTagCount.containsKey(tempBigram[0] + " "+ tempBigram[1]))
					{	
						bigramTagCount.put(tempBigram[0] + " "+ tempBigram[1], bigramTagCount.get(tempBigram[0] + " "+ tempBigram[1]) +1);
					}
					else
					{
						bigramTagCount.put(tempBigram[0] + " "+ tempBigram[1], 1);
					}

					tempBigram[0] = tempBigram[1];
					tempBigram[1] = splitWord[1];
				}
			}

			bigramTagCount.put(tempBigram[0]+ " "+ tempBigram[1], 1); // fix this

		}
		
		
		// printing tags and probabilities to output file
		
		bw.write("Tags");
		bw.newLine();
		for(String key : tagCount.keySet())
		{
			double val=(double)tagCount.get(key);
			val = val/totalTags;
			bw.write(key + " "+ val);
			bw.newLine();
		}
		
		// printing  P(T2|T1) to output file

		bw.write("Transition probabilities: ");
		bw.newLine();
		bw.write("<Tag1> <Tag2> <P(Tag2 | Tag1)>");
		bw.newLine();
		
		for(String key: bigramTagCount.keySet())
		{
			String str[] = key.split(" ");
			double prob =  (double)bigramTagCount.get(key) / (double)tagCount.get(str[0]);
			bw.write(key + " " + prob);
			bw.newLine();
		}
		
		// printing P(W|T) to output file
		
		bw.write("Observation Probablities: ");
		bw.newLine();
		bw.write("<Word> <Tag> <P(Word | Tag)>");
		bw.newLine();
		
		for(String key : wordAndTagCount.keySet())
		{	
			String str[] = key.split(" ");
			double prob= (double)wordAndTagCount.get(key) / (double)tagCount.get(str[1]);
			bw.write(key + " "+ prob);
			bw.newLine();
		}
		
		bw.close();
		
		System.out.println("Done");

		/*for(String key : bigramTagCount.keySet())
		{
			System.out.println(key + " " + bigramTagCount.get(key));
		}*/
	}


}
