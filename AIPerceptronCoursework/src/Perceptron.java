import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Perceptron{
	
	//VARIABLES EACH TIME
	static int numberHiddenNodes = 2; 
	//step size parameter
	static double ssP = 0.1;
	static double prevSSP;
	//number of epochs
	static int cycles = 100000;
	//hard coded minimum and maximum evaporation for destandardisation
	static double minPanE = 0;
	static double maxPanE = 15.9;
	//for annealment
	static double omega = 0.9;

	static int numberInputs = 5;
	//3 arrays for the 3 sets of data
	public static ArrayList<InputNode> trainingData = new ArrayList<InputNode>();
	public static ArrayList<InputNode> validationData = new ArrayList<InputNode>();
	public static ArrayList<InputNode> testData = new ArrayList<InputNode>();
	public static ArrayList<HiddenNode> hiddenLayer = new ArrayList<HiddenNode>();
	static double[] biasList = new double[numberHiddenNodes + 1];
	public static ArrayList<double[]> weightList = new ArrayList<>();
	static double[] outputWeightList = new double[numberHiddenNodes];
	static OutputNode currentOutNode = new OutputNode();

	public static double round2(double input) {
		return Math.round(input * 10000d) / 10000d;
	}
	
	//function to get a random number (for weights) - get number between [-2/n, 2/n] where n is the number of hidden nodes. 
	public static double getRandomNumber() {
		return round2(Math.random() * (2/numberHiddenNodes + 2/numberHiddenNodes) -2/numberHiddenNodes);
	}
	
	//function: input data to node array and set weights/biases to random
	public static void makeNodesArray() throws FileNotFoundException {
		//adding input nodes from training data
		File inputFile = new File ("C:\\Users\\Emily Cousins\\OneDrive - Loughborough University\\Eclipse workspace\\AIPerceptronCoursework\\trainingdata-output.txt");
		BufferedReader br = new BufferedReader(new FileReader(inputFile)); 
		try {
			String line; 
			while((line = br.readLine() ) != null ) {
				String[] node = line.split(",");
				InputNode newNode = new InputNode(node[0].replace(" ", ""), Double.parseDouble(node[1].replace(" ", "")), Double.parseDouble(node[2].replace(" ", "")),  Double.parseDouble(node[3].replace(" ", "")),  Double.parseDouble(node[4].replace(" ", "")), 
						Double.parseDouble(node[5].replace(" ", "")), Double.parseDouble(node[6].replace(" ", "")), Double.parseDouble(node[7].replace(" ", "")));
				trainingData.add(newNode);
			}
			br.close(); 
		} catch (IOException e){
			e.printStackTrace();
		}
		
		File inputFile3 = new File ("C:\\Users\\Emily Cousins\\OneDrive - Loughborough University\\Eclipse workspace\\AIPerceptronCoursework\\validationdata-output.txt");
		BufferedReader br3 = new BufferedReader(new FileReader(inputFile3)); 
		try {
			String line; 
			while((line = br3.readLine() ) != null ) {
				String[] node = line.split(",");
				//adding rows of data as input nodes - date, temperature, wind, solar radiation, air pressure, humidity and the de-standardised evaporation value
				InputNode newNode = new InputNode(node[0].replace(" ", ""), Double.parseDouble(node[1].replace(" ", ""))
						, Double.parseDouble(node[2].replace(" ", "")),  Double.parseDouble(node[3].replace(" ", "")),  Double.parseDouble(node[4].replace(" ", "")),  Double.parseDouble(node[5].replace(" ", ""))
						, Double.parseDouble(node[6].replace(" ", "")), Double.parseDouble(node[7].replace(" ", "")));
				validationData.add(newNode);
			}
			br3.close(); 
		} catch (IOException e){
			e.printStackTrace();
		}
		
		//assign random small weights and biases to all cells
		//bias list is random bias for every node and a bias for the output node (hence why is < or = to the number of hidden nodes)
		for (int k = 0; k<= numberHiddenNodes; k++) {
			biasList[k] = getRandomNumber();
		}
		
		//add weights to list - each hidden node has 5 weights going into it (numberInputs)
		//randomise between -2/n and 2/n 
		for (int k = 0; k<numberHiddenNodes; k++) {
			double[] weights = new double[numberInputs];
			for (int m = 0; m<numberInputs; m++) {
				weights[m] = getRandomNumber();
			}
			weightList.add(weights);
		}
		//output weights are from the hidden layer to the output node
		for (int k = 0; k<numberHiddenNodes; k++) {
			outputWeightList[k] = getRandomNumber();
		}
	}
	
	//function to destandardise data which takes the value you want to destandardise, the appropriate max and minimum
	public static double destandardise(double output, double min, double max) {
		return ((output -0.1) / 0.8) * (max - min) + min;
	} 
	
	//hiddenOutput - input is the current hidden node
	//function: computes the output of the hidden layer node with the weighted sum (si) and then 1/1+ negative exponential
	public static void hiddenOutput(HiddenNode hiddenNode) {
		int indexOfHiddenNode = hiddenLayer.indexOf(hiddenNode);
		double si = (hiddenNode.inputNode.temperature * weightList.get(indexOfHiddenNode)[0]) + (hiddenNode.inputNode.wind * weightList.get(indexOfHiddenNode)[1])
				+ (hiddenNode.inputNode.solarRad * weightList.get(indexOfHiddenNode)[2]) + (hiddenNode.inputNode.airPress * weightList.get(indexOfHiddenNode)[3])
				+ (hiddenNode.inputNode.humidity * weightList.get(indexOfHiddenNode)[4]) + biasList[indexOfHiddenNode];
		hiddenNode.output = 1/(1 +Math.exp(-si));
	}
	
	//function: calculates the output value of the one node one the output layer
	//sum of (weights * output to that node) + bias to output
	public static void outputAlgorithm() {
		double si = 0;
		for (int n = 0; n<hiddenLayer.size(); n ++) {
			si += hiddenLayer.get(n).output*outputWeightList[n];
		}
		si +=biasList[biasList.length-1];
		currentOutNode.output = (1/(1 +Math.exp(-si)));
	}
	
	//backward pass: input: correct evaporation level (standardised)
	//function: calculate delta value of output node
	public static void backwardPass( double correctPanE) {
		//Fs = current node output * (1 - current node output)
		double fS = currentOutNode.output * (1 - currentOutNode.output);
		//delta = (correct output - current output) * (current output (1 - current output))
		currentOutNode.delta = (correctPanE - currentOutNode.output) * fS;
	}
	
	//input:hidden node the program is currently processing
	//function: calculates delta for the hidden node 
	public static void deltaForHidden(HiddenNode hiddenNode) {
		//Fs = current node output * (1 - current node output)
		double fS = hiddenNode.output * (1 - hiddenNode.output );
		//delta = weight going towards output node * output node delta * fS
		hiddenNode.delta = (outputWeightList[hiddenLayer.indexOf(hiddenNode)] * currentOutNode.delta ) * fS;
	}
	
	//input: a weight value from the ArrayList, the delta of the node it is outputting to and the input value to the weight
	//function: update the weights Wij* = Wij + p * deltaj * outputi
	//bias update = current bias + step*delta of node * 1
	public static double weightUpdate(double currentWeight,double currentDelta, double inputValue) {
		//with momentum 
		double newWeight = currentWeight + (ssP *  currentDelta *inputValue);
		double change = newWeight - currentWeight; 
		return newWeight + omega * change;
	}
	
	static int m = 0; 
	static double prevRMSE = 2;
	static double validationRMSE = 2;
	//ALGORITHM: input: a row of standardised training data 
	//function: forwards and backwards pass to compute outputs and update the weight and bias values
	public static void algorithm(ArrayList<InputNode> inputData, boolean doRMSE) {
		//boolean checkRMSE = doRMSE;
		double validationrmseSum = 0;
		//for each row of data in the training table:
		for (int i = 0; i<inputData.size();i++) {
			//the hidden layer inputs attribute will change with the current row of data
			for (int count = 0; count<hiddenLayer.size(); count++) {
				hiddenLayer.get(count).inputNode = inputData.get(i);
				//the output value of the hidden node is calculated
				hiddenOutput(hiddenLayer.get(count));
			}
			//calculating the output of that forward pass (evaporation)
			outputAlgorithm();
			//for when RMSE of validation data needs to be calculated
			if (doRMSE) {
				validationrmseSum += Math.pow( round2(destandardise(currentOutNode.output, minPanE, maxPanE)) - inputData.get(i).deStan, 2);
			} 
			
			//backward pass to calculate the delta value of output node
			backwardPass( inputData.get(i).panE);
			//weights adjustment
			for (int count = 0; count<hiddenLayer.size(); count++) {
				//calculate the delta value for the current hidden node
				deltaForHidden(hiddenLayer.get(count));
				weightList.get(count)[4] = weightUpdate(weightList.get(count)[4], hiddenLayer.get(count).delta, inputData.get(i).humidity);
				weightList.get(count)[3] = weightUpdate(weightList.get(count)[3], hiddenLayer.get(count).delta, inputData.get(i).airPress);
				weightList.get(count)[2] = weightUpdate(weightList.get(count)[2], hiddenLayer.get(count).delta, inputData.get(i).solarRad);
				weightList.get(count)[1] = weightUpdate(weightList.get(count)[1], hiddenLayer.get(count).delta, inputData.get(i).wind);
				weightList.get(count)[0] = weightUpdate(weightList.get(count)[0], hiddenLayer.get(count).delta, inputData.get(i).temperature);
				outputWeightList[count] = weightUpdate(outputWeightList[count], currentOutNode.delta, hiddenLayer.get(count).output );
				//bias adjustment for that hidden node
				biasList[count] = weightUpdate(biasList[count], hiddenLayer.get(count).delta, 1);
			}		
			//output bias adjustment
			biasList[hiddenLayer.size()] = weightUpdate(hiddenLayer.size(), currentOutNode.delta, 1);	
		}
		//for when RMSE was needed (
		if (doRMSE) {
			validationRMSE = Math.sqrt(validationrmseSum/inputData.size());
		}
	}

	public static void main(String[] args) throws IOException {
		//simply importing the data to arrays and creating random weights and biases
		makeNodesArray();
		
		//set the hidden layer weights respectfully
		for (int i = 0; i<numberHiddenNodes; i++) {
			hiddenLayer.add(new HiddenNode());
		}
		/*
		 *while loop used to find optimum cycles number
		while (prevRMSE >= validationRMSE && cycles<1000000) {
			cycles++;
			System.out.println(cycles);
			algorithm(trainingData, false);
			if (cycles!=0 && cycles%1000 == 0) {
				prevRMSE = validationRMSE;
				algorithm(validationData, true);
			}
		} */
		
		//execute the algorithm for how many cycles specified 
		for (int h = 0; h<cycles; h++) {
			algorithm(trainingData, false);
			//annealing with values between 0.01 and 0.1 for ssP
			//if (0.01 + (0.1 - 0.01)*(1-(1/ 1+ Math.exp(10 -((20*h) / cycles)))) <0.1 && 0.01 + (0.1 - 0.01)*(1-(1/ 1+ Math.exp(10 -((20*h) / cycles)))) >0.01) {
				//ssP = 0.01 + (0.1 - 0.01)*(1-(1/ 1+ Math.exp(10 -((20*h) / cycles))));
			//}
			if (h!=0 && h%1000 == 0) {
				prevRMSE = validationRMSE;
				algorithm(validationData, true);
				/* Implementing bold driver 
				if (prevRMSE > validationRMSE && ssP*1.1<0.5) {
					ssP *=1.1;
				} else if (prevSSP*0.5 > 0.01){
					ssP = prevSSP*0.5;
				} */
			}
			//prevSSP = ssP;
		}	
		
		//using with validation data to validate the process
		File inputFile = new File ("C:\\Users\\Emily Cousins\\OneDrive - Loughborough University\\Eclipse workspace\\AIPerceptronCoursework\\testdata-output.txt");
		BufferedReader br = new BufferedReader(new FileReader(inputFile)); 
		try {
			String line; 
			while((line = br.readLine() ) != null ) {
				String[] node = line.split(",");
				//adding rows of data as input nodes - date, temperature, wind, solar radiation, air pressure, humidity and the de-standardised evaporation value
				InputNode newNode = new InputNode(node[0].replace(" ", ""), Double.parseDouble(node[1].replace(" ", ""))
						, Double.parseDouble(node[2].replace(" ", "")),  Double.parseDouble(node[3].replace(" ", "")),  Double.parseDouble(node[4].replace(" ", "")),  Double.parseDouble(node[5].replace(" ", ""))
						, Double.parseDouble(node[6].replace(" ", "")), Double.parseDouble(node[7].replace(" ", "")));
				testData.add(newNode);
			}
			br.close(); 
		} catch (IOException e){
			e.printStackTrace();
		}
		
		//RMSE sum for calculating RMSE of test data
		double rmseSum = 0;
		
		BufferedWriter writer2 = new BufferedWriter(new FileWriter("C:\\Users\\Emily Cousins\\OneDrive - Loughborough University\\A SECOND year\\AI\\semester2\\AIcoursework\\validationEvaporation.csv"));
		writer2.write("Date,Original,New");
		for (int i = 0; i<testData.size();i++) {
			//for each inputdata piece the hidden layer inputs will change and algorithm compute
			for (int count = 0; count<hiddenLayer.size(); count++) {
				hiddenLayer.get(count).inputNode = testData.get(i);
				hiddenOutput(hiddenLayer.get(count));
			}
			outputAlgorithm();
			//sum of ( modelled value - observed value ) **2 
			//rmseSum += Math.pow( currentOutNode.output- validationData.get(i).panE , 2);
			writer2.newLine();
			writer2.write(testData.get(i).date + "," +testData.get(i).deStan + "," + round2(destandardise(currentOutNode.output, minPanE, maxPanE)));
			rmseSum += Math.pow( round2(destandardise(currentOutNode.output, minPanE, maxPanE)) - testData.get(i).deStan, 2);
		}

		//root mean squared error - closer to 0 is better 
		double rmse = Math.sqrt(rmseSum/testData.size());
		writer2.newLine();
		writer2.write("Cycles," + cycles);
		writer2.write("Hidden Nodes," + numberHiddenNodes);
		writer2.write("RMSE," +Double.toString(rmse));
		writer2.write("ssp," + ssP);
		
		System.out.println("Cycles," + cycles);
		System.out.println("Hidden Nodes," + numberHiddenNodes);
		System.out.println("RMSE," +Double.toString(rmse));
		System.out.println("ssp," + ssP);
		
		writer2.close();
	}
}
