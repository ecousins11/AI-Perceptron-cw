import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class StandardiseDate {
	public static ArrayList<InputNode> inputDataRaw = new ArrayList<InputNode>();
	public static ArrayList<InputNode> inputDataStandardised = new ArrayList<InputNode>();
	public static ArrayList<InputNode> validationData = new ArrayList<InputNode>();
	public static ArrayList<InputNode> testData = new ArrayList<InputNode>();
	
	//function to round the number to 4 dp
	public static double round3(double input) {
		return Math.round(input * 100000d) / 100000d;
	}
	
	//standardising ([0.1,0.9] to allow for unseen extremes)
	//Input example : inputDataStandardised.get(number).temperature, minTemp, maxTemp
	//Output: value between 0.1 and 0.9 of the standardised value
	public static double standardise(double attribute, double min, double max ) {
		return round3(0.8 * ((attribute - min ) / (max-min)) +0.1);
	}
	
	//reversing the formula for destandardising
	public static double destandardise(double output, double min, double max) {
		return (((output -0.1) / 0.8) * (max - min) ) + min;
	}
	
	public static void main(String[] args) throws IOException {
		//Temperature, wind, SR, AirPress, Humid
		double sumHumid = 0;
		//inputting the raw data (interpolated previously)
		File inputFile = new File ("C:\\Users\\Emily Cousins\\OneDrive - Loughborough University\\Eclipse workspace\\AIPerceptronCoursework\\src\\rawInputData.txt");
		BufferedReader br = new BufferedReader(new FileReader(inputFile)); 
		try {
			String line; 
			while((line = br.readLine() ) != null ) {
				String[] node = line.split(",");
				InputNode newNode = new InputNode(node[0].replace(" ", ""), Double.parseDouble(node[1].replace(" ", "")), Double.parseDouble(node[2].replace(" ", "")),  
						Double.parseDouble(node[3].replace(" ", "")),  Double.parseDouble(node[4].replace(" ", "")),  Double.parseDouble(node[5].replace(" ", "")), 
						Double.parseDouble(node[6].replace(" ", "")), Double.parseDouble(node[6].replace(" ", "")));
				inputDataRaw.add(newNode);
			}
			br.close(); 
		} catch (IOException e){
			e.printStackTrace();
		}
		
		//work out the mean for humidity
		for (int count = 0; count<inputDataRaw.size(); count++) {
			sumHumid = sumHumid + inputDataRaw.get(count).humidity;
		}
		
		//finding average for humid
		int inputDataSize = inputDataRaw.size();
		double averageHumid = sumHumid/inputDataSize;
		
		//Eliminate data based on observations and mean for humidity
		for (int count = 0; count<inputDataRaw.size(); count++) {
			if ( inputDataRaw.get(count).temperature <50 
				&& inputDataRaw.get(count).wind > -100
				&& (inputDataRaw.get(count).solarRad >0)
				&& inputDataRaw.get(count).airPress >0
				&& (inputDataRaw.get(count).humidity < averageHumid + ((averageHumid )*0.90) && inputDataRaw.get(count).humidity > averageHumid - ((averageHumid )*0.90))) {
					inputDataStandardised.add(inputDataRaw.get(count));
			} 
		}
		
		//for working out maximum and minimum
		double maxTemp =inputDataStandardised.get(0).temperature;
		double minTemp = inputDataStandardised.get(0).temperature;
		
		double maxWind = inputDataStandardised.get(0).wind;
		double minWind = inputDataStandardised.get(0).wind;
		
		double maxSR =inputDataStandardised.get(0).solarRad;
		double minSR = inputDataStandardised.get(0).solarRad;
		
		double maxAirPress = inputDataStandardised.get(0).airPress;
		double minAirPress = inputDataStandardised.get(0).airPress;
		
		//initial value is 0
		sumHumid = 0;
		double maxHumid = inputDataStandardised.get(0).humidity;
		double minHumid = inputDataStandardised.get(0).humidity;
		
		double maxPanE = inputDataStandardised.get(0).panE;
		double minPanE = inputDataStandardised.get(0).panE;
		
		//find the sum (for average), max and min of each attribute of the node
		for (int count = 0; count<inputDataStandardised.size(); count++) {
			if (inputDataStandardised.get(count).temperature > maxTemp) {
				maxTemp = inputDataStandardised.get(count).temperature;
			} else if (inputDataStandardised.get(count).temperature<minTemp) {
				minTemp = inputDataStandardised.get(count).temperature;
			} 
			if (inputDataStandardised.get(count).wind > maxWind) {
				maxWind = inputDataStandardised.get(count).wind;
			} else if (inputDataStandardised.get(count).wind<minWind) {
				minWind = inputDataStandardised.get(count).wind;
			}
			if (inputDataStandardised.get(count).solarRad > maxSR) {
				maxSR = inputDataStandardised.get(count).solarRad;
			} else if (inputDataStandardised.get(count).solarRad<minSR) {
				minSR = inputDataStandardised.get(count).solarRad;
			}
			if (inputDataStandardised.get(count).airPress > maxAirPress) {
				maxAirPress = inputDataStandardised.get(count).airPress;
			} else if (inputDataStandardised.get(count).airPress<minAirPress) {
				minAirPress = inputDataStandardised.get(count).airPress;
			}
			if (inputDataStandardised.get(count).humidity > maxHumid) {
				maxHumid = inputDataStandardised.get(count).humidity;
			} else if (inputDataStandardised.get(count).humidity<minHumid) {
				minHumid = inputDataStandardised.get(count).humidity;
			}
			if (inputDataStandardised.get(count).panE > maxPanE) {
				maxPanE = inputDataStandardised.get(count).panE;
			} else if (inputDataStandardised.get(count).panE<minPanE) {
				minPanE = inputDataStandardised.get(count).panE;
			} 
		}
	
		File file = new File("trainingdata-output.txt");
		PrintWriter printtraining = new PrintWriter(file);
		//361 = test data (20%); 	360 = validation data (20%); 	720 = training data (60%)
		for (int count2 = 0; count2<inputDataStandardised.size(); count2++) {	
			//standardising data
			inputDataStandardised.get(count2).temperature = standardise(inputDataStandardised.get(count2).temperature, minTemp,maxTemp );
			inputDataStandardised.get(count2).wind = standardise(inputDataStandardised.get(count2).wind, minWind,maxWind );
			inputDataStandardised.get(count2).solarRad = standardise(inputDataStandardised.get(count2).solarRad, minSR,maxSR );
			inputDataStandardised.get(count2).airPress = standardise(inputDataStandardised.get(count2).airPress, minAirPress,maxAirPress );
			inputDataStandardised.get(count2).humidity = standardise(inputDataStandardised.get(count2).humidity, minHumid,maxHumid );
			inputDataStandardised.get(count2).panE = standardise(inputDataStandardised.get(count2).panE, minPanE,maxPanE );
			
			if (count2  % 4 == 0) {
				validationData.add(inputDataStandardised.get(count2));
				} else if (count2 % 3 == 0) {
				testData.add(inputDataStandardised.get(count2));
				} else {
				printtraining.println(inputDataStandardised.get(count2).date +','+ inputDataStandardised.get(count2).temperature + ',' + inputDataStandardised.get(count2).wind + ','
						+ inputDataStandardised.get(count2).solarRad +','
						+ inputDataStandardised.get(count2).airPress + ',' + inputDataStandardised.get(count2).humidity + ',' + inputDataStandardised.get(count2).panE 
						+ ',' + + inputDataStandardised.get(count2).deStan);
			}
		}
		
		File file2 = new File("validationdata-output.txt");
		PrintWriter printvalidation = new PrintWriter(file2);
		for (int i = 0; i<validationData.size();i++) {
			printvalidation.println(validationData.get(i).date +','+ validationData.get(i).temperature + ',' + validationData.get(i).wind + ','
					+ validationData.get(i).solarRad +','
					+ validationData.get(i).airPress + ',' + validationData.get(i).humidity + ',' + validationData.get(i).panE + ',' + validationData.get(i).deStan);
		}
		
		File file3 = new File("testdata-output.txt");
		PrintWriter printtest = new PrintWriter(file3);
		for (int i = 0; i<testData.size();i++) {
			printtest.println(testData.get(i).date +','+ testData.get(i).temperature + ',' + testData.get(i).wind + ','
					+ testData.get(i).solarRad +','
					+ testData.get(i).airPress + ',' + testData.get(i).humidity + ',' + testData.get(i).panE + ',' + testData.get(i).deStan);
		}
		printtest.close();
		printvalidation.close();
		printtraining.close();
	}
}