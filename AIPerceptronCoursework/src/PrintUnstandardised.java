import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class PrintUnstandardised {
	public static ArrayList<InputNode> inputDataRaw = new ArrayList<InputNode>();
	public static ArrayList<InputNode> inputDataStandardised = new ArrayList<InputNode>();
	public static ArrayList<InputNode> validationData = new ArrayList<InputNode>();
	public static ArrayList<InputNode> testData = new ArrayList<InputNode>();
	
	//function to round the number to 4 dp
	public static double round3(double input) {
		return Math.round(input * 10000d) / 10000d;
	}
	
	//standardising ([0.1,0.9] to allow for unseen extremes)
	//Input example : inputDataStandardised.get(number).temperature, minTemp, maxTemp
	//output: value between 0.1 and 0.9 of the standardised value
	public static double standardise(double attribute, double min, double max ) {
		return round3(0.8 * ((attribute - min ) / (max-min)) +0.1);
	}
	
	public static double destandardise(double output, double min, double max) {
		return (((output -0.1) / 0.8) * (max - min) ) + min;
	}
	
	public static void main(String[] args) throws IOException {
		
		//Temperature, wind, SR, AirPress, Humid
		double sumTemp = 0;
		double sumWind = 0;
		double sumSR = 0;
		double sumAirPress = 0;
		double sumHumid = 0;

		File inputFile = new File ("C:\\Users\\Emily Cousins\\OneDrive - Loughborough University\\Eclipse workspace\\AIPerceptronCoursework\\src\\rawInputData.txt");
		BufferedReader br = new BufferedReader(new FileReader(inputFile)); 
		try {
			String line; 
			while((line = br.readLine() ) != null ) {
				String[] node = line.split(",");
				InputNode newNode = new InputNode(node[0].replace(" ", ""), Double.parseDouble(node[1].replace(" ", "")), Double.parseDouble(node[2].replace(" ", "")),  Double.parseDouble(node[3].replace(" ", "")),  Double.parseDouble(node[4].replace(" ", "")),  Double.parseDouble(node[5].replace(" ", "")), Double.parseDouble(node[6].replace(" ", "")));
				inputDataRaw.add(newNode);
			}
			br.close(); 
		} catch (IOException e){
			
			e.printStackTrace();
		}
		
		double maxTemp = inputDataRaw.get(0).temperature;
		double maxWind = inputDataRaw.get(0).wind;
		double maxSR = inputDataRaw.get(0).solarRad;
		double maxAirPress = inputDataRaw.get(0).airPress;
		double maxHumid =  inputDataRaw.get(0).humidity;
		double maxPanE = inputDataRaw.get(0).panE;
		
		double minTemp = inputDataRaw.get(0).temperature;
		double minWind = inputDataRaw.get(0).wind;
		double minSR = inputDataRaw.get(0).solarRad;
		double minAirPress = inputDataRaw.get(0).airPress;
		double minHumid =  inputDataRaw.get(0).humidity;
		double minPanE = inputDataRaw.get(0).panE;
		
		//work out the mean
		for (int count = 0; count<inputDataRaw.size(); count++) {
			sumTemp = sumTemp + inputDataRaw.get(count).temperature;
			if (inputDataRaw.get(count).temperature > maxTemp) {
				maxTemp = inputDataRaw.get(count).temperature;
			} else if (inputDataRaw.get(count).temperature<minTemp) {
				minTemp = inputDataRaw.get(count).temperature;
			}
			sumWind = sumWind + inputDataRaw.get(count).wind;
			if (inputDataRaw.get(count).wind > maxWind) {
				maxWind = inputDataRaw.get(count).wind;
			} else if (inputDataRaw.get(count).wind<minWind) {
				minWind = inputDataRaw.get(count).wind;
			}
			sumSR = sumSR + inputDataRaw.get(count).solarRad;
			if (inputDataRaw.get(count).solarRad > maxSR) {
				maxSR = inputDataRaw.get(count).solarRad;
			} else if (inputDataRaw.get(count).solarRad<minSR) {
				minSR = inputDataRaw.get(count).solarRad;
			}
			sumAirPress = sumAirPress + inputDataRaw.get(count).airPress;
			if (inputDataRaw.get(count).airPress > maxAirPress) {
				maxAirPress = inputDataRaw.get(count).airPress;
			} else if (inputDataRaw.get(count).airPress<minAirPress) {
				minAirPress = inputDataRaw.get(count).airPress;
			}
			sumHumid = sumHumid + inputDataRaw.get(count).humidity;
			if (inputDataRaw.get(count).humidity > maxHumid) {
				maxHumid = inputDataRaw.get(count).humidity;
			} else if (inputDataRaw.get(count).humidity<minHumid) {
				minHumid = inputDataRaw.get(count).humidity;
			}
			if (inputDataRaw.get(count).panE > maxPanE) {
				maxPanE = inputDataRaw.get(count).panE;
			} else if (inputDataRaw.get(count).panE<minPanE) {
				minPanE = inputDataRaw.get(count).panE;
			}
			
		}
		
		int inputDataSize = inputDataRaw.size();
		double averageTemp = sumTemp/inputDataSize;
		double averageWind = sumWind/inputDataSize;
		double averageSR = sumSR/inputDataSize;
		double averageAirPress = sumAirPress/inputDataSize;
		double averageHumid = sumHumid/inputDataSize;
		
		//if > or < the (mean/2)*0.95 then dont add
		System.out.println(averageTemp);
		for (int count = 0; count<inputDataRaw.size(); count++) {
			if ( (inputDataRaw.get(count).temperature < averageTemp + ((averageTemp )*0.95) && inputDataRaw.get(count).temperature > averageTemp - ((averageTemp )*0.95))
				 && (inputDataRaw.get(count).wind < averageWind + ((averageWind )*0.95) && inputDataRaw.get(count).wind > averageWind - ((averageWind )*0.95))
				 && (inputDataRaw.get(count).solarRad < averageSR + ((averageSR )*0.95) && inputDataRaw.get(count).solarRad > averageSR - ((averageSR )*0.95))
				 && (inputDataRaw.get(count).airPress < averageAirPress + ((averageAirPress )*0.95) && inputDataRaw.get(count).airPress > averageAirPress - ((averageAirPress )*0.95))
				 && (inputDataRaw.get(count).humidity < averageHumid + ((averageHumid )*0.95) && inputDataRaw.get(count).humidity > averageHumid - ((averageHumid )*0.95))
					
					) {
				inputDataStandardised.add(inputDataRaw.get(count));
			} 
		}
		
		sumTemp = 0;
		maxTemp = 0;
		minTemp = inputDataStandardised.get(0).temperature;
		
		sumWind = 0;
		maxWind = 0;
		minWind = inputDataStandardised.get(0).wind;
		
		sumSR = 0;
		maxSR = 0;
		minSR = inputDataStandardised.get(0).solarRad;
		
		sumAirPress = 0;
		maxAirPress = 0;
		minAirPress = inputDataStandardised.get(0).airPress;
		
		sumHumid = 0;
		maxHumid = 0;
		minHumid = inputDataStandardised.get(0).humidity;
		
		//dont need evaporation to be standardised?..
		
		//find the sum (for average), max and min of each attribute of the node
		for (int count = 0; count<inputDataStandardised.size(); count++) {
			sumTemp = sumTemp + inputDataStandardised.get(count).temperature;
			if (inputDataStandardised.get(count).temperature > maxTemp) {
				maxTemp = inputDataStandardised.get(count).temperature;
			} else if (inputDataStandardised.get(count).temperature<minTemp) {
				minTemp = inputDataStandardised.get(count).temperature;
			} 
			sumWind = sumWind + inputDataStandardised.get(count).wind;
			if (inputDataStandardised.get(count).wind > maxWind) {
				maxWind = inputDataStandardised.get(count).wind;
			} else if (inputDataStandardised.get(count).wind<minWind) {
				minWind = inputDataStandardised.get(count).wind;
			}
			sumSR = sumSR + inputDataStandardised.get(count).solarRad;
			if (inputDataStandardised.get(count).solarRad > maxSR) {
				maxSR = inputDataStandardised.get(count).solarRad;
			} else if (inputDataStandardised.get(count).solarRad<minSR) {
				minSR = inputDataStandardised.get(count).solarRad;
			}
			sumAirPress = sumAirPress + inputDataStandardised.get(count).airPress;
			if (inputDataStandardised.get(count).airPress > maxAirPress) {
				maxAirPress = inputDataStandardised.get(count).airPress;
			} else if (inputDataStandardised.get(count).airPress<minAirPress) {
				minAirPress = inputDataStandardised.get(count).airPress;
			}
			sumHumid = sumHumid + inputDataStandardised.get(count).humidity;
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
	
		File file = new File("trainingdataDS-output.csv");
		PrintWriter printtraining = new PrintWriter(file);

		System.out.println("max temp" + maxTemp);
		System.out.println("min temp" + minTemp);
		//320 = test data (20%); 	319 = validation data (20%); 	638 = training data (60%)
		for (int count2 = 0; count2<inputDataStandardised.size(); count2++) {			
			inputDataStandardised.get(count2).temperature = inputDataStandardised.get(count2).temperature;
			inputDataStandardised.get(count2).wind = inputDataStandardised.get(count2).wind;
			inputDataStandardised.get(count2).solarRad = inputDataStandardised.get(count2).solarRad;
			inputDataStandardised.get(count2).airPress = inputDataStandardised.get(count2).airPress;
			inputDataStandardised.get(count2).humidity = inputDataStandardised.get(count2).humidity;
			inputDataStandardised.get(count2).panE = inputDataStandardised.get(count2).panE;
			
			if (count2  % 4 == 0) {
				testData.add(inputDataStandardised.get(count2));
				} else if (count2 % 3 == 0) {
				validationData.add(inputDataStandardised.get(count2));
				} else {
				printtraining.println(inputDataStandardised.get(count2).date +','+ inputDataStandardised.get(count2).temperature + ',' + inputDataStandardised.get(count2).wind + ','
						+ inputDataStandardised.get(count2).solarRad +','
						+ inputDataStandardised.get(count2).airPress + ',' + inputDataStandardised.get(count2).humidity + ',' + inputDataStandardised.get(count2).panE);
			}
		}
		
		File file2 = new File("validationDS-output.csv");
		PrintWriter printvalidation = new PrintWriter(file2);
		for (int i = 0; i<validationData.size();i++) {
			printvalidation.println(validationData.get(i).date +','+ validationData.get(i).temperature + ',' + validationData.get(i).wind + ','
					+ validationData.get(i).solarRad +','
					+ validationData.get(i).airPress + ',' + validationData.get(i).humidity + ',' + validationData.get(i).panE);
		}
		
		File file3 = new File("testdataDS-output.csv");
		PrintWriter printtest = new PrintWriter(file3);
		for (int i = 0; i<validationData.size();i++) {
			printtest.println(testData.get(i).date +','+ testData.get(i).temperature + ',' + testData.get(i).wind + ','
					+ testData.get(i).solarRad +','
					+ testData.get(i).airPress + ',' + testData.get(i).humidity + ',' + testData.get(i).panE);
		}
		
		printtest.close();
		printvalidation.close();
		printtraining.close();
	}
}
