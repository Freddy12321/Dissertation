package dissertation6;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class GUI2 extends JFrame {
	/**
	 * this class is used to display information about Professions, lifeGoods, and luxryGoods
	 */
	private static final long serialVersionUID = 1L;
	//references to Professions and GoodPrices
	Professions professions;
	GoodPrices goods;
	//JTextArea to display info in
	JTextArea input, output, capital;
	JLabel lifeNeeds, luxuryNeeds;
	//layout
	GridBagLayout layout= new GridBagLayout();
	//constructor takes in references, sets up text area, 
	//and calls professions method to create text to display
	public GUI2(Professions p, GoodPrices g) {
		this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		professions=p;
		goods=g;
		this.setLayout(layout);
		GridBagConstraints c= new GridBagConstraints();
		c.insets=new Insets(10,10,10,10);
		c.ipadx=10;
		c.ipady=10;
		input=new JTextArea();
		input.setText(input());
		c.gridx=0;
		c.gridy=0;
		this.add(input,c);
		output=new JTextArea();
		output.setText(output());
		c.gridx=1;
		c.gridy=0;
		this.add(output,c);
		capital=new JTextArea();
		capital.setText(capital());
		c.gridx=2;
		c.gridy=0;
		this.add(capital,c);
		lifeNeeds=new JLabel();
		lifeNeeds.setText(lifeNeeds());
		c.weightx=0.5;
		c.weighty=0.5;
		c.gridx=0;
		c.gridy=1;
		this.add(lifeNeeds,c);
		luxuryNeeds=new JLabel();
		luxuryNeeds.setText(luxuryNeeds());
		c.weightx=0.5;
		c.weighty=0.5;
		c.gridx=1;
		c.gridy=1;
		this.add(luxuryNeeds,c);
		//Dimension d=this.getPreferredSize();
		//d.setSize(d.getWidth()+20, d.getHeight()+50);
		//this.setSize(d);
		this.pack();
		Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)screen.getWidth()-this.getWidth(), 50);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	//methods create special Strings to be printed to JTextAreas and JLabels
	private String luxuryNeeds() {
		String out="Life Needs: ";
		ArrayList<GoodPrice> life=goods.getLifeGoods();
		for(int i=0; i<life.size(); i++) {
			out=out.concat(life.get(i).getID()+" | ");
		}
		return out;
	}
	private String lifeNeeds() {
		String out="Luxury Needs: ";
		ArrayList<GoodPrice> luxury=goods.getLuxuryGoods();
		for(int i=0; i<luxury.size(); i++) {
			out=out.concat(luxury.get(i).getID()+" | ");
		}
		return out;
	}
	private String capital() {
		String out="Professions Capital: \n";
		int[] temp=professions.getCapitalArray();
		out=out.concat(String.format("%4s", ""));
		for(int j=0;j<goods.getAllGoodsNum();j++) {
			out=out.concat(String.format("%4s", j));
		}
		out=out.concat("\n");
		for(int i=0; i<temp.length;i++) {
			out=out.concat(String.format("%4s", i+"|"));
			for(int j=0; j<goods.getAllGoodsNum();j++) {
				if(temp[i]==j) {
					out=out.concat(String.format("%4s", professions.getProfession(i).getCapitalAmount()));
				}
				else {
					out=out.concat(String.format("%4s", 0));
				}
			}
			out=out.concat("\n");
		}
		return out;
	}
	private String output() {
		String out="Professions Output: \n";
		int[] temp=professions.getOutputArray();
		out=out.concat(String.format("%4s", ""));
		for(int j=0;j<goods.getAllGoodsNum();j++) {
			out=out.concat(String.format("%4s", j));
		}
		out=out.concat("\n");
		for(int i=0; i<temp.length;i++) {
			out=out.concat(String.format("%4s", i+"|"));
			for(int j=0; j<goods.getAllGoodsNum();j++) {
				if(temp[i]==j) {
					out=out.concat(String.format("%4s", professions.getProfession(i).getOutputAmount()));
				}
				else {
					out=out.concat(String.format("%4s", 0));
				}
			}
			out=out.concat("\n");
		}
		return out;
	}
	private String input() {
		String out="Professions Input: \n";
		int[] temp=professions.getInputArray();
		out=out.concat(String.format("%4s", ""));
		for(int j=0;j<goods.getAllGoodsNum();j++) {
			out=out.concat(String.format("%4s", j));
		}
		out=out.concat("\n");
		for(int i=0; i<temp.length;i++) {
			out=out.concat(String.format("%4s", i+"|"));
			for(int j=0; j<goods.getAllGoodsNum();j++) {
				if(temp[i]==j) {
					out=out.concat(String.format("%4s", professions.getProfession(i).getInputAmount()));
				}
				else {
					out=out.concat(String.format("%4s", 0));
				}
			}
			out=out.concat("\n");
		}
		return out;
	}
	
}
