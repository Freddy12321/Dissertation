package dissertation6;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class GUI3  extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//this class displays info on individual Agent
	//references to Tile, Professions, GoodPrices, and Agent to be displayed
	Tile tile;
	Professions prof;
	GoodPrices goods;
	Agent a=null;
	//JLabels to display with, not all are visible at once
	JLabel id= new JLabel();
	JLabel money= new JLabel();
	JLabel profession= new JLabel();
	JLabel happiness= new JLabel();
	JLabel lifeNeeds= new JLabel();
	JLabel productionNeeds= new JLabel();
	JLabel land= new JLabel();
	JLabel capital= new JLabel();
	JLabel company= new JLabel();
	JLabel companyMoney= new JLabel();
	JLabel companyCapital= new JLabel();
	JLabel companyEmployees= new JLabel();
	JLabel companyLand= new JLabel();
	JLabel salery= new JLabel();
	JLabel needsThisTick=new JLabel();
	//contructor takes Tile, Professions, and GoodPrices
	//sets up JLabels 
	public GUI3(Tile tile, Professions prof, GoodPrices g){
		this.tile=tile;
		this.prof=prof;
		this.goods=g;
		this.add(id);
		this.add(money);
		this.add(profession);
		this.add(happiness);
		this.add(lifeNeeds);
		this.add(needsThisTick);
		this.add(productionNeeds);
		this.add(land);
		this.add(capital);
		this.add(company);
		this.add(companyMoney);
		this.add(companyCapital);
		this.add(companyEmployees);
		this.add(companyLand);
		this.add(salery);
		this.setVisible(true);
		this.setLayout(new FlowLayout());
		this.setSize(500, 300);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	//refresh method that takes in an Agent as input, makes class variables agent = to input Agent
	//then makes JLabels display info from Agent
	public void refresh(Agent agent) {
		a=agent;
		id.setText("ID: "+a.toString());
		money.setText("Money: "+a.getMoney());
		profession.setText("Profession: "+a.getProfession().getIndex());
		happiness.setText("Happiness: "+a.getHappiness());
		lifeNeeds.setText("Life Needs Met: "+a.getLifeNeedsMet());
		needsThisTick.setText(needsThisTick());
		productionNeeds.setText("Production Needs Met: "+a.getProductionNeedsMet());
		//if the Agents Profession has capital then capital is displayed
		if(a.getProfession().getCapital()!=null) {
			capital.setText("Captial: "+a.getCapital());
			capital.setVisible(true);
		}
		//if it does not, capital is not displayed
		else {
			capital.setVisible(false);
		}
		//if the Agent owns a company, company info is displayed, agent land is hidden
		if(a.getProfession().equals(prof.getEmployer())) {
			land.setVisible(false);
			company.setText("Company: "+a.getCompany().toString());
			company.setVisible(true);
			companyMoney.setText("Company Money: "+a.getCompany().getMoney());
			companyMoney.setVisible(true);
			companyEmployees.setText("Company Employees: "+a.getCompany().getEmployees().size());
			companyEmployees.setVisible(true);
			companyLand.setText("Company Land: "+a.getCompany().getLand());
			companyLand.setVisible(true);
		}
		//if not then company info is hidden, agent land is displayed
		else {
			land.setText("Land: "+a.getLand());
			land.setVisible(true);
			company.setVisible(false);
			companyMoney.setVisible(false);
			companyEmployees.setVisible(false);
			companyLand.setVisible(false);
		}
		//if agent is an employee salary is displayed
		if(a.getProfession().equals(prof.getEmployee())) {
			salery.setText("Salery: "+a.getSalary());
			salery.setVisible(true);
		}
		//if not salary is hidden
		else {
			salery.setVisible(false);
		}
	}
	//method used to parse and return custom string from a boolean array
	//for lifeNeeds
	private String needsThisTick() {
		boolean[] needsMet=a.getNeedsMetThisRound();
		String out="Needs Met This Round: ";
		for(int i=0; i<needsMet.length-1; i++) {
			out=out.concat(goods.getLifeGoods().get(i).getID()+": "+needsMet[i]+", ");
		}
		out=out.concat(goods.getLifeGoods().get(needsMet.length-1).getID()+": "+needsMet[needsMet.length-1]+"");
		return out;
	}
	//other refresh method with no input
	public void refresh() {
		//if agent is null nothing is done
		if(a!=null) {
			id.setText("ID: "+a.toString());
			money.setText("Money: "+a.getMoney());
			profession.setText("Profession: "+a.getProfession().getIndex());
			happiness.setText("Happiness: "+a.getHappiness());
			lifeNeeds.setText("Life Needs Met: "+a.getLifeNeedsMet());
			needsThisTick.setText(needsThisTick());
			productionNeeds.setText("Production Needs Met: "+a.getProductionNeedsMet());
			//if the Agents Profession has capital then capital is displayed
			if(a.getProfession().getCapital()!=null) {
				capital.setText("Captial: "+a.getCapital());
				capital.setVisible(true);
			}
			//if it does not, capital is not displayed
			else {
				capital.setVisible(false);
			}
			//if the Agent owns a company, company info is displayed, agent land is hidden
			if(a.getProfession().equals(prof.getEmployer())) {
				land.setVisible(false);
				company.setText("Company: "+a.getCompany().toString());
				company.setVisible(true);
				companyMoney.setText("Company Money: "+a.getCompany().getMoney());
				companyMoney.setVisible(true);
				companyEmployees.setText("Company Employees: "+a.getCompany().getEmployees().size());
				companyEmployees.setVisible(true);
				companyLand.setText("Company Land: "+a.getCompany().getLand());
				companyLand.setVisible(true);
			}
			//if not then company info is hidden, agent land is displayed
			else {
				land.setText("Land: "+a.getLand());
				land.setVisible(true);
				company.setVisible(false);
				companyMoney.setVisible(false);
				companyEmployees.setVisible(false);
				companyLand.setVisible(false);
			}
			//if agent is an employee salary is displayed
			if(a.getProfession().equals(prof.getEmployee())) {
				salery.setText("Salery: "+a.getSalary());
				salery.setVisible(true);
			}
			//if not salary is hidden
			else {
				salery.setVisible(false);
			}
		}
	}
	//used to return current Agents money
	public int getPersonMoney() {
		if(a!=null) {
			return a.getMoney();
		}
		return 0;
	}
}
