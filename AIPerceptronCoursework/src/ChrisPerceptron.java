
public class ChrisPerceptron {
	private static double [] [] data = 
		{{1,1,4,1},
				{1,2,9,1},
				{1,5,6,1},
				{1,4,5,1},
				{1,6,0.7,-1},
				{1,1,1.5,-1}
		};
	public static void main(String[] args) {
		double w0=0, w1=0, w2=0, output;
		boolean changed;
		int stable = 0 ;
		for (int i = 0; i<50; i++) {
			changed = false;
		
			for (int j= 0; j<6; j++) {
				output = w0 + (w1*data[j][1]) + (w2*data[j][2]);
				
				if (((output>=0)  && (data[j][3] == -1)) || ((output<=0) && (data[j][3] ==1)) ) {
					{ w0=w0+(data[j][3]*data[j][0]);
						w1 = w1 + (data[j][3]*data[j][1]);
						w2 = w2 + (data[j][3]*data[j][2]);
						changed = true;
					}
				}
				
			}
			if (changed) stable = i+1;
			System.out.printf("Loop: %d\tW0: %2.4f \t W1: %2.4f\t W2: %2.4f\n", i, w0, w1, w2);
			
		}
		System.out.printf("Stable after: %d epochs\n",stable);
	}

}
