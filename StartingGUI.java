package dissertation6;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class StartingGUI extends JFrame implements ActionListener{
	//The starting GUI is used to pull user input to provide model parameters
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//JLabels of variables
	JLabel people, startingMoney, rawGoods, refinedGoods, lifeGoods, luxuryGoods, land, rawProfessionOut, refProfessionInput, 
		refProfessionOut, ticks, textName;
	//corresponding input fields
	JTextField peopleIn, startingMoneyIn, rawGoodsIn, refinedGoodsIn, lifeGoodsIn, luxuryGoodsIn, landIn, 
		rawProfessionOutIn, refProfessionInputIn, refProfessionOutIn, ticksIn, textNameIn;
	//array to pass to main
	int[] input= new int[11];
	//string name of output text file
	String outputText=null;
	//button to change start model
	JButton start;
	//layout
	GridLayout layout= new GridLayout(13,2);
	//boolean tracks if button has been clicked
	boolean clicked=false;
	//method constructor sets up all JLabels, JTextAreas, and Button and adds them to the GUI
	public StartingGUI() {
		this.setLayout(layout);
		layout.setHgap(1);
		layout.setVgap(1);
		people=new JLabel("People");
		startingMoney=new JLabel("Starting Money Per Person");
		rawGoods= new JLabel("Number of Raw Goods");
		refinedGoods= new JLabel("Number of Refined Goods");
		lifeGoods= new JLabel("Number of Life Goods");
		luxuryGoods= new JLabel("Number of Luxury Goods");
		land=new JLabel("Starting Land");
		rawProfessionOut= new JLabel("Raw Profession Output");
		refProfessionInput= new JLabel("Refined Profession Input");
		refProfessionOut= new JLabel("Refined Profession Output");
		ticks= new JLabel("Number of Ticks to Run For");
		textName= new JLabel("Ouput Text Document Name");
		peopleIn=new JTextField(5);
		peopleIn.setText("100");
		startingMoneyIn=new JTextField(5);
		startingMoneyIn.setText("10000");
		rawGoodsIn=new JTextField(5);
		rawGoodsIn.setText("5");
		refinedGoodsIn=new JTextField(5);
		refinedGoodsIn.setText("5");
		lifeGoodsIn=new JTextField(5);
		lifeGoodsIn.setText("5");
		luxuryGoodsIn=new JTextField(5);
		luxuryGoodsIn.setText("5");
		landIn=new JTextField(5);
		landIn.setText("50");
		rawProfessionOutIn=new JTextField(5);
		rawProfessionOutIn.setText("15");
		refProfessionInputIn=new JTextField(5);
		refProfessionInputIn.setText("15");
		refProfessionOutIn=new JTextField(5);
		refProfessionOutIn.setText("15");
		textNameIn= new JTextField(5);
		textNameIn.setText("Test");
		ticksIn=new JTextField(5);
		ticksIn.setText("1000");
		start=new JButton("START");
		start.addActionListener(this);
		this.add(people);
		this.add(peopleIn);
		this.add(startingMoney);
		this.add(startingMoneyIn);
		this.add(rawGoods);
		this.add(rawGoodsIn);
		this.add(rawProfessionOut);
		this.add(rawProfessionOutIn);
		this.add(refinedGoods);
		this.add(refinedGoodsIn);
		this.add(refProfessionInput);
		this.add(refProfessionInputIn);
		this.add(refProfessionOut);
		this.add(refProfessionOutIn);
		this.add(lifeGoods);
		this.add(lifeGoodsIn);
		this.add(luxuryGoods);
		this.add(luxuryGoodsIn);
		this.add(land);
		this.add(landIn);
		this.add(ticks);
		this.add(ticksIn);
		this.add(textName);
		this.add(textNameIn);
		this.add(start);
		this.setSize(400,400);
		this.setLocation(700, 300);
		this.setVisible(true);
	}
	@Override
	//action performed is triggered when the button is clicked
	public void actionPerformed(ActionEvent a) {
		if(a.getSource().equals(start)) {
			try {
				//integers are parsed from JTextFields
				input[0]=Integer.parseInt(peopleIn.getText());
				input[1]=Integer.parseInt(startingMoneyIn.getText());
				input[2]=Integer.parseInt(rawGoodsIn.getText());
				input[3]=Integer.parseInt(refinedGoodsIn.getText());
				input[4]=Integer.parseInt(lifeGoodsIn.getText());
				input[5]=Integer.parseInt(luxuryGoodsIn.getText());
				input[6]=Integer.parseInt(landIn.getText());
				input[7]=Integer.parseInt(rawProfessionOutIn.getText());
				input[8]=Integer.parseInt(refProfessionInputIn.getText());
				input[9]=Integer.parseInt(refProfessionOutIn.getText());
				input[10]=Integer.parseInt(ticksIn.getText());
				outputText=textNameIn.getText();
				for(int i=0; i<input.length;i++) {
					//boolean clicked is set to true
					clicked=true;
					if(input[i]<=0) {
						//if an input is invalid clicked is set to false and user is prompted
						clicked=false;
						JOptionPane.showMessageDialog(null, "One or more of the values is 0 or negative.");
					}
				}
				//if input is successfully taken the GUI hides itself
				if(clicked==true) {
					this.setVisible(false);
				}
			}
			//user is prompted if a field has non valid characters
			catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Error "+e.toString()+" thrown. Please make sure the contents of all text fields are numbers.");
			}
		}
	}
	//used by main to get the integer array
	public int[] getInput() {
		return input;
	}
	public String getOutputName() {
		return outputText;
	}
}
