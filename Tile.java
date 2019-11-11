package dissertation6;

import java.util.ArrayList;
import java.util.Collections;

public class Tile {
	//Tile holds all Instances of Company and Agent and is responsible for calling their tick methods
	//references to various objects
	Market market;
	Professions jobs;
	GoodPrices goods;
	OutputText out;
	//variables used for tracking information
	int landInTile;
	int tickNum=0;
	int averageMoney=0;
	int[] tileProduction;
	int[] supply;
	int[] demand;
	//ArrayLists of Agent and Company objects 
	ArrayList<Agent> people= new ArrayList<Agent>();
	ArrayList<Company> companies= new ArrayList<Company>();
	//Class constructor, takes GoodPrices g, Professions j, Market m, Integer Agents, Integer land, Integer money
	public Tile(GoodPrices g, Professions j, Market m, OutputText o, int p, int land, int money) {
		market=m;
		jobs=j;
		goods=g;
		averageMoney=money;
		out=o;
		tileProduction=new int[goods.getAllGoodsNum()];
		supply=new int[goods.getAllGoodsNum()];
		demand=new int[goods.getAllGoodsNum()];
		landInTile=land;
		addPeople(p,money);
	}
	//method used to add people to ArrayList people, takes Integer peopleToMake, Integer money
	private void addPeople(int p, int money) {
		for(int i=0; i<p;i++) {
			//GoodAmount array created for starting land
			ArrayList<GoodAmount> startingGoods= new ArrayList<GoodAmount>();
			//if land can be evenly divided then everyone gets a fraction, if not some get more
			if(landInTile%p>i) {
				startingGoods.add(new GoodAmount(goods.getLandIndex(), landInTile/p+1));
			}
			else {
				startingGoods.add(new GoodAmount(goods.getLandIndex(), landInTile/p));
			}
			//person added with references, land, and money
			people.add(new Agent(market, jobs, goods, this, startingGoods, money));
		}
	}
	//tick method, calls other subtick methods
	public void tick() {
		long startTime=System.currentTimeMillis();
		resetSupply();
		resetDemand();
		resetProduction();
		Collections.shuffle(people);
		Collections.shuffle(companies);
		//checks money total, for debugging
		int totalMoney=0;
		for(int i=0; i<companies.size();i++) {
			totalMoney+=companies.get(i).getMoney();
		}
		for(int i=0; i<people.size(); i++) {
			totalMoney+=people.get(i).getMoney();
		}
		if(totalMoney!=people.size()*averageMoney) {
			System.out.println("Money Changed Before Tick");
		}
		tickClearLedgers();
		tickProduceRaw();
		tickProduceRefined();
		tickBuyLifeNeeds();
		tickBuyOther();
		tickOther();
		//prices updated
		market.updatePrices(supply, demand);
		totalMoney=0;
		//checks money total, for debugging
		for(int i=0; i<companies.size();i++) {
			totalMoney+=companies.get(i).getMoney();
		}
		for(int i=0; i<people.size(); i++) {
			totalMoney+=people.get(i).getMoney();
		}
		if(totalMoney!=people.size()*averageMoney) {
			System.out.println("Money Changed After Tick");
		}
		System.out.println("BuyLedger Size: "+market.buyLedger.size());
		System.out.println("SellLedger Size: "+market.sellLedger.size());
		updateOut();
		tickNum++;
		//tick time and number printed
		long endTime=System.currentTimeMillis();
		System.out.println("tick "+tickNum+"  "+(endTime-startTime));
	}
	private void updateOut() {
		out.updateHappiness(getOutHappiness(), tickNum);
		out.updateCapital(getOutCapital(), tickNum);
		out.updateLand(getOutLand(), tickNum);
		out.updateLife(getOutLife(), tickNum);
		out.updateMoney(getOutMoney(), tickNum);
		out.updateProduction(getOutProd(), tickNum);
		out.updateProfessions(getJobs(), tickNum);
		out.updateProduction(tileProduction, tickNum);
	}
	private boolean[] getOutProd() {
		boolean[] out=new boolean[people.size()];
		for(int i=0; i<out.length;i++) {
			out[i]=people.get(i).getProductionNeedsMet();
		}
		return out;
	}
	private boolean[] getOutLife() {
		boolean[] out=new boolean[people.size()];
		for(int i=0; i<out.length;i++) {
			out[i]=people.get(i).getLifeNeedsMet();
		}
		return out;
	}
	private int[] getOutMoney() {
		int[] out=new int[people.size()];
		for(int i=0; i<out.length;i++) {
			out[i]=people.get(i).getMoney();
			if(people.get(i).getProfession().equals(jobs.getEmployer())) {
				out[i]+=people.get(i).getCompany().getMoney();
			}
		}
		return out;
	}
	private int[] getOutLand() {
		int[] out=new int[people.size()];
		for(int i=0; i<out.length;i++) {
			out[i]=people.get(i).getLand();
			if(people.get(i).getProfession().equals(jobs.getEmployer())) {
				out[i]+=people.get(i).getCompany().getLand();
			}
		}
		return out;
	}
	private int[] getOutCapital() {
		int[] out=new int[people.size()];
		for(int i=0; i<out.length;i++) {
			out[i]=people.get(i).getCapital();
			if(people.get(i).getProfession().equals(jobs.getEmployer())) {
				out[i]+=people.get(i).getCompany().getCapital();
			}
		}
		return out;
	}
	private int[] getOutHappiness() {
		int[] out=new int[people.size()];
		for(int i=0; i<out.length;i++) {
			out[i]=people.get(i).getHappiness();
		}
		return out;
	}
	//calls the tickOther in Agents and Companies
	private void tickOther() {
		for(int i=0; i<companies.size(); i++) {
			companies.get(i).tickOther();
		}
		for(int i=0; i<people.size(); i++) {
			people.get(i).tickOther();
		}
	}
	//calls the tickBuyOther in Agents, used to buy capital and luxury goods
	private void tickBuyOther() {
		for(int i=0; i<people.size(); i++) {
			people.get(i).tickBuyOther();
		}
	}
	//calls the tickBuyLifeNeeds in Agents, used to buy life goods
	private void tickBuyLifeNeeds() {
		for(int i=0; i<people.size(); i++) {
			people.get(i).tickBuyLifeNeeds();
		}
	}
	//causes Agents and Companies with RefinedProfession to buy input goods, produce, and sell output
	private void tickProduceRefined() {
		for(int i=0; i<companies.size(); i++) {
			companies.get(i).tickProduceRefined();
		}
		for(int i=0; i<people.size(); i++) {
			people.get(i).tickProduceRefined();
		}
	}
	//causes Agents and Companies with RawProfession to produce and sell output
	private void tickProduceRaw() {
		for(int i=0; i<companies.size(); i++) {
			companies.get(i).tickProduceRaw();
		}
		for(int i=0; i<people.size(); i++) {
			people.get(i).tickProduceRaw();
		}
	}
	//causes all Agents and Companies to clear their ledgers
	private void tickClearLedgers() {
		for(int i=0; i<companies.size(); i++) {
			companies.get(i).tickClearLedgers();
		}
		for(int i=0; i<people.size(); i++) {
			people.get(i).tickClearLedgers();
		}
	}
	//resets tile production counters
	private void resetProduction() {
		for(int i=0; i<tileProduction.length;i++) {
			tileProduction[i]=0;
		}
	}
	//used by Agents and Companies to log production
	public void logProduction(GoodAmount output) {
		tileProduction[output.getType()]+=output.getAmount();
	}
	//returns an int[], index 0 is Agents with life needs met, index 1 with life needs not met
	public int[] getLifeNeedsMet() {
		int[] needsMet = new int[2];
		for(int i=0; i<people.size();i++) {
			if(people.get(i).getLifeNeedsMet()) {
				needsMet[0]++;
			}
			else {
				needsMet[1]++;
			}
		}
		return needsMet;
	}
	//returns an int[], index 0 is Agents with life production met, index 1 with production needs not met
	public int[] getProductionNeedsMet() {
		int[] needsMet = new int[2];
		for(int i=0; i<people.size();i++) {
			if(people.get(i).getProductionNeedsMet()) {
				needsMet[0]++;
			}
			else {
				needsMet[1]++;
			}
		}
		return needsMet;
	}
	//returns int[] of number of Agents with different professions
	public int[] getJobs() {
		int[] j=new int[jobs.getProfessionsSize()];
		for(int i=0; i<people.size();i++) {
			j[people.get(i).getProfession().getIndex()]++;
		}
		return j;
	}
	//returns int[] of Agents money sorted from least to most
	public int[] getAgentMoney() {
		int[] peopleMoney=new int[people.size()];
		for(int i=0; i<people.size();i++) {
			peopleMoney[i]=people.get(i).getMoney();
		}
		peopleMoney=mergeSortDown(peopleMoney);
		return peopleMoney;
	}
	//returns int[] of Company money sorted from least to most
	public int[] getCompanyMoney() {
		int[] companyMoney=new int[companies.size()];
		for(int i=0; i<companies.size();i++) {
			companyMoney[i]=companies.get(i).getMoney();
		}
		companyMoney=mergeSortDown(companyMoney);
		return companyMoney;
	}
	//merge sort method that sorts from least to most
	private int[] mergeSortDown(int[] peopleMoney) {
		if(peopleMoney.length>1) {
			int[] firstHalf = new int[peopleMoney.length/2];
			int[] secondHalf;
			if(peopleMoney.length%2==1) {
				secondHalf=new int[peopleMoney.length/2+1];
			}
			else {
				secondHalf=new int[peopleMoney.length/2];
			}
			int[] sorted = new int[peopleMoney.length];
			for(int i=0; i<firstHalf.length;i++) {
				firstHalf[i]=peopleMoney[i];
			}
			for(int i=firstHalf.length; i<peopleMoney.length;i++) {
				secondHalf[i-firstHalf.length]=peopleMoney[i];
			}
			if(firstHalf.length>1) {
				firstHalf=mergeSortDown(firstHalf);
			}
			if(secondHalf.length>1) {
				secondHalf=mergeSortDown(secondHalf);
			}
			int j=0;
			int k=0;
			for(int i=0; i<peopleMoney.length;i++) {
				if(j<firstHalf.length&&k<secondHalf.length) {
					if(firstHalf[j]<=secondHalf[k]) {
						sorted[i]=firstHalf[j];
						j++;
					}
					else if(firstHalf[j]>secondHalf[k]){
						sorted[i]=secondHalf[k];
						k++;
					}
				}
				else if(j==firstHalf.length){
					sorted[i]=secondHalf[k];
					k++;
				}
				else if(k==secondHalf.length) {
					sorted[i]=firstHalf[j];
					j++;
				}
			}
			return sorted;
		}
		return peopleMoney;
	}
	//used to add a company to the Tile, takes Company toAdd
	public void addCompany(Company toAdd) {
		companies.add(toAdd);
	}
	//used to remove a company from the Tile, takes Company toRemove
	public void removeCompany(Company toRemove) {
		if(companies.contains(toRemove)) {
			companies.remove(companies.indexOf(toRemove));
		}
	}
	//returns the average money in the tile, is equal Agents starting money
	public int getAverageMoney() {
		return averageMoney;
	}
	//returns the number of companies in the tile
	public int getCompaniesNum() {
		return companies.size();
	}
	//returns the int[] tileProduction
	public int[] getTileProduction() {
		return tileProduction;
	}
	//returns an int[] showing company capital sorted from least to most
	public int[] getCompanyCapital() {
		int[] capital= new int[companies.size()];
		for(int i=0; i<companies.size(); i++) {
			capital[i]=companies.get(i).getCapital();
		}
		return mergeSortDown(capital);
	}
	//returns an int[] showing agent capital sorted from least to most
	public int[] getAgentCapital() {
		int[] capital= new int[people.size()];
		for(int i=0; i<people.size(); i++) {
			capital[i]=people.get(i).getCapital();
		}
		return mergeSortDown(capital) ;
	}
	//returns an int[][] showing Agent luxury possesion
	public int[][] getLuxury(){
		int[][] luxuries=new int[people.size()][];
		for(int i=0; i<luxuries.length;i++) {
			luxuries[i]=people.get(i).getLuxuries();
		}
		return luxuries;
	}
	//returns an int[] showing Agent happiness sorted from least to most
	public int[] getHappiness() {
		int[] happiness= new int[people.size()];
		for(int i=0; i<happiness.length;i++) {
			happiness[i]=people.get(i).getHappiness();
		}
		happiness=mergeSortDown(happiness);
		return happiness;
	}
	//returns an int[] showing Agent land sorted from least to most
	public int[] getLand() {
		int[] land=  new int[people.size()];
		for(int i=0; i<land.length;i++) {
			land[i]=people.get(i).getLand();
		}
		land=mergeSortDown(land);
		return land;
	}
	//returns the amount of people
	public int getPeopleSize() {
		return people.size();
	}
	//returns the string of a specific Agent
	public String getPersonID(int index) {
		return people.get(index).toString();
	}
	//returns a specific Agent
	public Agent getAgent(int agent) {
		return people.get(agent);
	}
	//returns an Agent with a specific string, 
	public Agent getAgent(String id) {
		for(int i=0; i<people.size(); i++) {
			if(people.get(i).toString().equals(id)) {
				return people.get(i);
			}
		}
		return null;
	}
	//returns an ArrayList of Agents sorted from least to most money
	public ArrayList<Agent> getAgentsByMoney() {
		ArrayList<Agent> toReturn = new ArrayList<Agent>();
		toReturn.addAll(people);
		sortPeople(toReturn);
		return toReturn;
	}
	//takes an ArrayList of Agents and sorts it from least money to most money and returns the result
	private ArrayList<Agent> sortPeople(ArrayList<Agent> toReturn) {
		if(toReturn.size()>1) {
			ArrayList<Agent> firstHalf= new ArrayList<Agent>();
			ArrayList<Agent> secondHalf= new ArrayList<Agent>();
			firstHalf.addAll(toReturn.subList(0, toReturn.size()/2));
			secondHalf.addAll(toReturn.subList(toReturn.size()/2, toReturn.size()));
			if(firstHalf.size()>1) {
				sortPeople(firstHalf);
			}
			if(secondHalf.size()>1) {
				sortPeople(secondHalf);
			}
			toReturn.clear();
			while(firstHalf.size()>0||secondHalf.size()>0) {
				if(firstHalf.size()==0) {
					toReturn.addAll(secondHalf);
					secondHalf.clear();
				}
				else if(secondHalf.size()==0) {
					toReturn.addAll(firstHalf);
					firstHalf.clear();
				}
				else if(firstHalf.get(0).getMoney()<=secondHalf.get(0).getMoney()) {
					toReturn.add(firstHalf.get(0));
					firstHalf.remove(0);
				}
				else if(secondHalf.get(0).getMoney()<=firstHalf.get(0).getMoney()) {
					toReturn.add(secondHalf.get(0));
					secondHalf.remove(0);
				}
			}
			return toReturn;
		}
		return toReturn;
	}
	//returns the landInTile
	public int getAllLand() {
		return landInTile;
	}
	//adds the supply int[] type is the index and amount is howmuch, takes Integer type, Integer amount
	public void addToSupply(int type, int amount) {
		supply[type]+=amount;
	}
	//adds the demand int[] type is the index and amount is howmuch, takes Integer type, Integer amount
	public void addToDemand(int type, int amount) {
		demand[type]+=amount;
	}
	//resets all entries in demand int[] to 0
	private void resetDemand() {
		for(int i=0; i<demand.length; i++) {
			demand[i]=0;
		}
	}
	//resets all entries in supply int[] to 0
	private void resetSupply() {
		for(int i=0; i<supply.length; i++) {
			supply[i]=0;
		}
	}
	public int getTick() {
		return tickNum;
	}
}
