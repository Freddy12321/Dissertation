package dissertation6;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUI1 extends JFrame implements ActionListener, ChangeListener{
	/**
	 * class is used to display information about a variety of 
	 * factors in the model
	 */
	private static final long serialVersionUID = 1L;
	//references to other objects
	Tile tile;
	Market market;
	GoodPrices goods;
	Professions professions;
	//reference to GUI3
	GUI3 gui3;
	JButton start, step, getAgent, previousAgent, nextAgent;
	JSlider slider;
	//start and step variables
	boolean START=false;
	boolean STEP=true;
	JLabel tick, marketSellAmounts, marketBuyAmounts, marketPrices, jobs, needs, production, companies, tileProduction, sliderPerson;
	JTextArea money, capital, happiness, land;
	//ArrayList of agents
	ArrayList<Agent> agents= new ArrayList<Agent>();
	//layout
	//Class constructor takes in Tile, Market, GoodPrices, Professions, GUI3
	//used to set up class variables
	//sets up and add GUI parts and calls refresh once this is done 
	public GUI1(Tile tile, Market market, GoodPrices goods, Professions professions, GUI3 gui3) {
		this.tile=tile;
		this.market=market;
		this.goods=goods;
		this.professions=professions;
		this.gui3=gui3;
		this.setLayout(new GridBagLayout());
		GridBagConstraints c= new GridBagConstraints();
		c.insets=new Insets(10,10,10,10);
		c.ipadx=10;
		c.ipady=10;
		start= new JButton("Start");
		start.addActionListener(this);
		c.gridx=0;
		c.gridy=0;
		c.anchor=GridBagConstraints.CENTER;
		this.add(start,c);
		step= new JButton("Step");
		step.addActionListener(this);
		c.gridx=1;
		c.gridy=0;
		c.anchor=GridBagConstraints.CENTER;
		this.add(step,c);
		tick=new JLabel();
		c.gridx=2;
		c.gridy=0;
		c.anchor=GridBagConstraints.CENTER;
		this.add(tick,c);
		getAgent= new JButton("Select");
		getAgent.addActionListener(this);
		c.gridx=3;
		c.gridy=0;
		c.anchor=GridBagConstraints.CENTER;
		this.add(getAgent,c);
		previousAgent= new JButton("Previous Agent");
		previousAgent.addActionListener(this);
		c.gridx=4;
		c.gridy=0;
		c.anchor=GridBagConstraints.CENTER;
		this.add(previousAgent,c);
		slider= new JSlider();
		slider.setMinimum(0);
		slider.setMaximum(tile.getPeopleSize()-1);
		slider.setValue(slider.getMaximum()/2);
		slider.addChangeListener(this);
		c.gridx=5;
		c.gridy=0;
		c.gridwidth=2;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(slider,c);
		sliderPerson=new JLabel();
		c.gridx=7;
		c.gridy=0;
		c.anchor=GridBagConstraints.CENTER;
		this.add(sliderPerson,c);
		nextAgent= new JButton("Next Agent");
		nextAgent.addActionListener(this);
		c.gridx=8;
		c.gridy=0;
		c.anchor=GridBagConstraints.CENTER;
		this.add(nextAgent,c);
		needs= new JLabel();
		c.gridx=0;
		c.gridy=1;
		c.gridwidth=4;
		c.anchor=GridBagConstraints.CENTER;
		this.add(needs,c);
		production= new JLabel();
		c.gridx=4;
		c.gridy=1;
		c.gridwidth=4;
		c.anchor=GridBagConstraints.CENTER;
		this.add(production,c);
		jobs= new JLabel();
		c.gridx=0;
		c.gridy=2;
		c.gridwidth=4;
		c.anchor=GridBagConstraints.CENTER;
		this.add(jobs,c);
		marketPrices= new JLabel();
		c.gridx=4;
		c.gridy=2;
		c.gridwidth=4;
		c.anchor=GridBagConstraints.CENTER;
		this.add(marketPrices,c);
		marketBuyAmounts=new JLabel();
		c.gridx=0;
		c.gridy=3;
		c.gridwidth=4;
		c.anchor=GridBagConstraints.CENTER;
		this.add(marketBuyAmounts,c);
		marketSellAmounts= new JLabel();
		c.gridx=4;
		c.gridy=3;
		c.gridwidth=4;
		c.anchor=GridBagConstraints.CENTER;
		this.add(marketSellAmounts,c);
		companies= new JLabel();
		c.gridx=0;
		c.gridy=4;
		c.gridwidth=4;
		c.anchor=GridBagConstraints.CENTER;
		this.add(companies,c);
		tileProduction= new JLabel();
		c.gridx=4;
		c.gridy=4;
		c.gridwidth=4;
		c.anchor=GridBagConstraints.CENTER;
		this.add(tileProduction,c);
		money=new JTextArea();
		c.gridx=0;
		c.gridy=5;
		c.gridwidth=2;
		c.anchor=GridBagConstraints.CENTER;
		this.add(money,c);
		capital= new JTextArea();
		c.gridx=2;
		c.gridy=5;
		c.gridwidth=2;
		c.anchor=GridBagConstraints.CENTER;
		this.add(capital,c);
		happiness= new JTextArea();
		c.gridx=4;
		c.gridy=5;
		c.gridwidth=2;
		c.anchor=GridBagConstraints.CENTER;
		this.add(happiness,c);
		land= new JTextArea();
		c.gridx=6;
		c.gridy=5;
		c.gridwidth=2;
		c.anchor=GridBagConstraints.CENTER;
		this.add(land,c);
		refresh();
		this.pack();
		this.setLocation(200, 100);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	//refresh method sets the string values of all GUI parts
	//this is done by calling a different method for every part
	//that returns a string, method also calls refresh in GUI3
	public void refresh() {
		needs.setText(needs());
		production.setText(production());
		jobs.setText(jobs());
		marketPrices.setText(marketPrices());
		marketSellAmounts.setText(marketSellAmounts());
		marketBuyAmounts.setText(marketBuyAmounts());
		companies.setText(companies());
		money.setText(money());
		tileProduction.setText(tileProduction());
		capital.setText(capital());
		happiness.setText(happiness());
		land.setText(land());
		//ArrayList of Agents sorted from lowest to highest in money is retrieved from Tile
		agents=tile.getAgentsByMoney();
		gui3.refresh();
		sliderPerson.setText(""+agents.get(slider.getValue()).getMoney());
		tick.setText(tick());
		this.pack();
	}
	//method for updating JLabel tick
	private String tick() {
		String out="Tick: "+tile.getTick();
		return out;
	}
	//method for getting square of Agent land
	private String land() {
		int[] temp=tile.getLand();
		String out="Tile Land: \n\n";
		int square=(int)Math.sqrt(temp.length);
		if(square*square<temp.length) {
			square++;
		}
		for(int i=0; i<temp.length;) {
			for(int j=0; j<square; j++) {
				if(i<temp.length) {
					out=out.concat(String.format("%4s", temp[i]));
					i++;
				}
				else {
					j=square;
				}
			}
			out=out.concat("\n");
		}
		return out;
	}
	//method for getting a square of Agent happiness values
	private String happiness() {
		int[] temp=tile.getHappiness();
		String out="Tile Happiness: \n\n";
		int square=(int)Math.sqrt(temp.length);
		if(square*square<temp.length) {
			square++;
		}
		for(int i=0; i<temp.length;) {
			for(int j=0; j<square; j++) {
				if(i<temp.length) {
					out=out.concat(String.format("%4s", temp[i]));
					i++;
				}
				else {
					j=square;
				}
			}
			out=out.concat("\n");
		}
		return out;
	}
	//method for getting square of Agent and Company capital values
	private String capital() {
		String out="Companies Capital: \n\n";
		int[] temp= tile.getCompanyCapital();
		int square=(int)Math.sqrt(temp.length);
		if(square*square<temp.length) {
			square++;
		}
		for(int i=0; i<temp.length;) {
			for(int j=0; j<square; j++) {
				if(i<temp.length) {
					out=out.concat(String.format("%4s", temp[i]));
					i++;
				}
				else {
					j=square;
				}
			}
			out=out.concat("\n");
		}
		out=out.concat("\n\nAgent Capital: \n\n");
		temp=tile.getAgentCapital();
		square=(int)Math.sqrt(temp.length);
		if(square*square<temp.length) {
			square++;
		}
		for(int i=0; i<temp.length;i++) {
			for(int j=0; j<square; j++) {
				if(i<temp.length) {
					out=out.concat(String.format("%4s", temp[i]));
					i++;
				}
				else {
					j=square;
				}
			}
			out=out.concat("\n");
		}
		return out;
	}
	//method for getting total tile production
	private String tileProduction() {
		String out="Tile Production: ";
		int[] temp= tile.getTileProduction();
		int total=0;
		for(int i=0; i<temp.length;i++) {
			out=out.concat(i+": "+temp[i]+" || ");
			total+=temp[i];
		}
		out=out.concat("\n Total: "+total);
		return out;
	}
	//method for getting company number
	private String companies() {
		int num=tile.getCompaniesNum();
		String out="Total Companies: "+num;
		return out;
	}
	//method for getting Agent and Company money in two squares
	private String money() {
		int[] m =tile.getAgentMoney();
		String out ="Agent Money: \n";
		int total=0;
		int square=(int)Math.sqrt(m.length);
		if(square*square<m.length) {
			square++;
		}
		for(int i=0; i<m.length;) {
			for(int j=0; j<square; j++) {
				if(i<m.length) {
					out=out.concat(String.format("%8s", m[i]+""));
					total=total+m[i];
					i++;
				}
				else {
					j=square;
				}
			}
			out=out.concat("\n");
		}
		out=out.concat("Agent Total: "+total);
		int companyTotal=0;
		out=out.concat("\n\nCompany Money: \n");
		m=tile.getCompanyMoney();
		square=(int)Math.sqrt(m.length);
		if(square*square<m.length) {
			square++;
		}
		for(int i=0; i<m.length;) {
			for(int j=0; j<square; j++) {
				if(i<m.length) {
					out=out.concat(String.format("%8s", m[i]));
					companyTotal=companyTotal+m[i];
					i++;
				}
				else {
					j=square;
				}
			}
			out=out.concat("\n");
		}
		out=out.concat("Company Total: "+companyTotal);
		out=out.concat("\nTotal total: "+(total+companyTotal));
		if(total+companyTotal!=tile.getPeopleSize()*tile.getAverageMoney()) {
			START=false;
		}
		return out;
	}
	//method for getting buy requests 
	private String marketBuyAmounts() {
		int[] amounts= market.getBuyAmounts();
		String out = "Buy Requests Are: ";
		for(int i=0; i<amounts.length;i++) {
			out=out.concat(i+": "+amounts[i]+" || ");
		}
		return out;
	}
	//method for getting sell requests
	private String marketSellAmounts() {
		int[] amounts= market.getSellAmounts();
		String out = "Sell Requests Are: ";
		for(int i=0; i<amounts.length;i++) {
			out=out.concat(i+": "+amounts[i]+" || ");
		}
		return out;
	}
	//method for getting market prices
	private String marketPrices() {
		double[] prices=market.getPrices();
		String out = "Prices Are: ";
		for(int i=0; i<prices.length;i++) {
			out=out.concat(i+": "+String.format("%.2f", prices[i])+" || ");
		}
		return out;
	}
	//method for getting current amount of Agents in each Profession
	private String jobs() {
		int[] j=tile.getJobs();
		String out = "Jobs Are: ";
		for(int i=0; i<j.length;i++) {
			out=out.concat(i+": "+(j[i])+" || ");
		}
		return out;
	}
	//Method for getting Agents with production needs met vs prodction needs not met
	private String production() {
		int[] prod=tile.getProductionNeedsMet();
		String out="Production Met: "+prod[0]+" || Production Not Met: "+prod[1];
		return out;
	}
	//method for getting number of Agents with life needs met vs life needs not met
	private String needs() {
		int[] needs=tile.getLifeNeedsMet();
		String out="Needs Met: "+needs[0]+" || Needs Not Met: "+needs[1];
		return out;
	}
	@Override
	//method for when buttons are clicked
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(start)) {
			//changes start to the opposite of what it is now
			START=!START;
		}
		else if(e.getSource().equals(step)) {
			//sets step to true
			STEP=true;
		}
		else if(e.getSource().equals(getAgent)){
			//calls GUI3 refesh and passes the current slider value
			gui3.refresh(agents.get(slider.getValue()));
		}
		else if(e.getSource().equals(nextAgent)&&slider.getValue()<slider.getMaximum()) {
			slider.setValue(slider.getValue()+1);
			sliderPerson.setText(""+agents.get(slider.getValue()).getMoney());
		}
		else if(e.getSource().equals(previousAgent)&&slider.getValue()>slider.getMinimum()) {
			slider.setValue(slider.getValue()-1);
			sliderPerson.setText(""+agents.get(slider.getValue()).getMoney());
		}
	}
	@Override
	//used to update the slidder
	public void stateChanged(ChangeEvent e) {
		if(e.getSource().equals(slider)) {
			//sets slider to current person
			sliderPerson.setText(""+agents.get(slider.getValue()).getMoney());
		}
	}
}
