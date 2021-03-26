
public class InputNode {
	//columns from the input data standardised and a de-standardised value
	double panE;
	String date;
	double temperature;
	double wind;
	double solarRad;
	double airPress;
	double humidity;
	double deStan;
	
	//for training data (deStan not needed)
	public InputNode(String date, double temperature, double wind, double solarRad, double airPress, double humidity, double panE) {
		super();
		this.date = date;
		this.temperature = temperature;
		this.wind = wind;
		this.solarRad = solarRad;
		this.airPress = airPress;
		this.humidity = humidity;
		this.panE = panE;
	}
	
	//for validation and test data (deStan - destandardised evaporation value needed)
	public InputNode(String date, double temperature, double wind, double solarRad, double airPress, double humidity, double panE, double deStan) {
		super();
		this.date = date;
		this.temperature = temperature;
		this.wind = wind;
		this.solarRad = solarRad;
		this.airPress = airPress;
		this.humidity = humidity;
		this.panE = panE;
		this.deStan  = deStan;
	}

	public static void main(String[] args) {

	}
}
