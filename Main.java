package dissertation6;

public class Main {
	static String outputName=null;
	public static void main(String[] args) {
		//input array gotten form getStartingParameters
		int[] input= getStartingParameters();
		//GoodPrices created
		GoodPrices goods= new GoodPrices(input[2],input[3],input[4],input[5]);
		//Professions created
		Professions prof= new Professions(goods, input[7], input[8], input[9]);
		//Market created
		Market market=new Market(goods);
		//output class
		OutputText out= new OutputText(input[10], outputName);
		//Tile created
		Tile t = new Tile(goods, prof, market, out, input[0], input[6], input[1]);
		//GUIs created
		GUI3 gui3= new GUI3(t, prof, goods);
		GUI1 gui= new GUI1(t,market,goods, prof, gui3);
		GUI2 gui2= new GUI2(prof, goods);
		//method loops based off of input
		System.out.println(input[10]);
		for(int i=0; i<input[10];) {
			//if start or step variable in GUI1 are true then the method calls tick in tile and refreshes GUI1
			if(gui.START||gui.STEP) {
				t.tick();
				gui.refresh();
				//if step is true it is set to false
				if(gui.STEP) {
					gui.STEP=false;
				}
				i++;
			}
			//thread sleeps for a brief time between ticks
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		out.outputToFile();
		System.out.println("DONE");
		System.exit(0);
	}
	//method creates an instance of StartingGUI and checks to see if the boolean clicked is true
	//if the boolean is true it retrieves the input array from StartingGUI and returns it to main
	private static int[] getStartingParameters() {
		StartingGUI sGUI= new StartingGUI();
		while(sGUI.clicked==false) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int [] toReturn= sGUI.getInput();
		outputName=sGUI.getOutputName();
		return toReturn;
	}
}
