import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class ViterbiTester2
{
	static HashMap<String, Double> tagProbabilities = new HashMap<String, Double>();
	static HashMap<String, Double> observationProbabilities = new HashMap<String, Double>();
	static HashMap<String, Double> transitionProbabilities = new HashMap<String, Double>();
	static ArrayList<String> tagList = new ArrayList<String>();
	static String testingFile, hmmModel, output;
	
	public ViterbiTester2(String testingFile, String hmmModel,String output ) 
	{	
		this.testingFile = testingFile;
		this.hmmModel = hmmModel;
		this.output = output;
	}

	public static void main(String[] args) throws Exception
	{
		
		if(args.length < 3)
		{
			System.out.println("\nSyntax Error: Path of input files expected.\nUsage: java -jar HMM_POS_Tester.jar <TESTING_FILE_Path> <HMM_MODEL_PATH> <OUTPUT_FILE_Path>\n");
			return;
		}
		
		new ViterbiTester2(args[0],args[1],args[2]);
		
		//BufferedReader brTestingFile = new BufferedReader(new FileReader("input/test"));
		BufferedReader brTestingFile = new BufferedReader(new FileReader(testingFile));
		BufferedReader brHMMPOSModel = new BufferedReader(new FileReader(new File(hmmModel)));


		FileWriter fw = new FileWriter(new File(output));
		BufferedWriter bw = new BufferedWriter(fw);


		// reading tag probabilities from HMM
		brHMMPOSModel.readLine();
		String line;
		while (!(line = brHMMPOSModel.readLine()).equals("Transition probabilities: "))
		{
			String[] str = line.split(" ");
			tagProbabilities.put(str[0], Double.parseDouble(str[1]));
			tagList.add(str[0]);
		}

		// reading transitional probabilities from HMM
		brHMMPOSModel.readLine();
		while(!(line= brHMMPOSModel.readLine()).equals("Observation Probablities: "))
		{
			String[] str = line.split(" ");
			transitionProbabilities.put(str[0] +  " "+ str[1], Double.parseDouble(str[2]));
		}

		// reading observation probabilities from HMM
		brHMMPOSModel.readLine();
		while((line = brHMMPOSModel.readLine())!=  null)
		{
			String[] str = line.split(" ");
			observationProbabilities.put(str[0]+" "+ str[1], Double.parseDouble(str[2]));
		}

		String currentLine = "";

		// FOR EACH LINE

		int lineCount = 0;
		while((currentLine = brTestingFile.readLine()) !=  null)
		{

			lineCount++;
			if(lineCount%100 == 0)
				System.out.println("");
			System.out.print(".");

			currentLine = "<StartingWord> "+ currentLine + " <EndingWord>";
			String wordsOfLine[] = currentLine.split(" ");

			VWord prevVWord = null;
			for (String word : wordsOfLine) {
				VWord vWord = new VWord(word);

				if(prevVWord!=null)
				{
					vWord.parent = prevVWord;
					prevVWord.next = vWord;
				}

				prevVWord = vWord;
			}

			VWord lastVWord = prevVWord;
			VWord firstVWord = null;

			VWord currVWord = prevVWord;
			while(currVWord.parent != null)
			{	
				currVWord = currVWord.parent;
			}
			firstVWord = currVWord;

			// initialize probs of first word
			for (String tag : tagList) 
			{
				String key = firstVWord.word + " " + tag;
				if(observationProbabilities.containsKey(key))
				{
					Double p = observationProbabilities.get(key);
					firstVWord.tagProbs.put(tag, p);
				}
			}


			VWord secondVWord = firstVWord.next;
			VWord cVWord = secondVWord;
			while(cVWord.next != null)
			{
				VWord parentVWord = cVWord.parent;


				String currWord = cVWord.word;
				for (String tag : tagList) 
				{
					String currTag = tag;

					double obsProb = 0;

					if(observationProbabilities.containsKey(currWord + " " + currTag))
						obsProb = observationProbabilities.get(currWord + " " + currTag);
					else 
						obsProb = (double)((((double)(1.0 / tagList.size()) - 2) * 0.1) / (tagProbabilities.get(currTag)));

					double vMax_CurrWord_CurrTag = 0;
					for (String pTag : parentVWord.tagProbs.keySet()) 
					{
						double v_CurrParentWord_CurrTag = 0;

						if(transitionProbabilities.containsKey(pTag + " " + currTag))
							v_CurrParentWord_CurrTag = parentVWord.tagProbs.get(pTag) * obsProb * transitionProbabilities.get(pTag + " " + currTag);
						else
						{
							v_CurrParentWord_CurrTag = 0; // unseen probs
							//System.out.print(".");
						}

						if(v_CurrParentWord_CurrTag > vMax_CurrWord_CurrTag)
						{
							vMax_CurrWord_CurrTag = v_CurrParentWord_CurrTag;
						}
					}

					cVWord.tagProbs.put(currTag, vMax_CurrWord_CurrTag);
				}

				cVWord.findMaxTag();

				// move on to next word in line
				cVWord = cVWord.next;

			}// end of all words in line ... moving on to next line

			VWord ccCurrWord = lastVWord.parent;

			String finalstr = "";
			while(ccCurrWord.parent != null)
			{
				finalstr = (ccCurrWord.word + "/" + ccCurrWord.maxTag) + " " + finalstr;
				ccCurrWord = ccCurrWord.parent;
			}


			bw.write(finalstr);
			bw.newLine();


		}// end of for each line from input file

		bw.close();

		System.out.println("Model Created");

		brHMMPOSModel.close();
		brTestingFile.close();
	}


}
