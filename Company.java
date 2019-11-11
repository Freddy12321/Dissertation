package dissertation6;

import java.util.ArrayList;

public class Company extends MarketOperator{
	//This class is for Companies, these are created by agents and have the advantage of being able to hire employees
	//reference to owner and other objects
	Agent owner;
	Profession profession;
	Professions professions;
	//list of employees
	ArrayList<Agent> employees=new ArrayList<Agent>();
	//integers to keep track of profits
	int tickCost=0;
	int tickProfit=0;
	int tenTickProfit=0;
	int hundredTickProfit=0;
	int inputCosts=0;
	//how long the company has existed
	int inExistance=0;
	//if there is enough input
	boolean insuficentInput=false;
	//production this tick
	int tickProduction=0;
	//Class constructor, takes Agent owner, Profession profession, Market market, GoodPrices goods, Tile t, ArrayList<GoodAmount> startingGoods, int money
	//passes market, goods, t, and money to super. startingGoods are added to currentGoods, 
	//if profession is an EmployProfession then a new one is randomly selected from Raw/RefinedProfessions
	public Company(Agent owner, Profession profession, Professions professions, Market market, GoodPrices goods, Tile t, ArrayList<GoodAmount> startingGoods, int money) {
		super(market, goods, t, money);
		currentGoods.addAll(startingGoods);
		this.owner=owner;
		if(this.owner==null) {
			System.out.println("owner null on creation");
		}
		this.profession=profession;
		this.professions=professions;
		if(profession.getIndex()>=professions.getProfessionsSizeWOEmployee()) {
			this.profession=professions.getProfession((int)(professions.getProfessionsSizeWOEmployee()*Math.random()));
		}
	}
	//tick method to clear ledgers
	public void tickClearLedgers() {
		clearLedgers();
		insuficentInput=false;
	}
	//tick method used to produce and sell if Profession is a RawProfession
	public void tickProduceRaw() {
		if(profession.getClass().equals(RawProfession.class)){
			produceRaw();
			sellOutput();
		}
	}
	//tick method used to buy input, produce, and sell if profession is a RefinedProfession
	public void tickProduceRefined() {
		if(profession.getClass().equals(RefinedProfession.class)){
			buyProductionNeeds();
			produceRefined();
			sellOutput();
		}
	}
	//tick method for other things, purchasing capital, paying employees, paying the owner, managing tickProfit variables
	//changing profession, good decay, and clearing up ledgers
	public void tickOther() {
		sellOutput();
		payEmployees();
		payOwner();
		manageCapital();
		tickProfit();
		clearUp();
		changeProfession();
		decayGoods();
		inExistance++;
	}
	//method used to manage Capital, if the company has too little input then an employee is fired,
	//if the profit over the last ten ticks was positive then capital is bought,
	//depending on if the profession is Raw or Refined a different method is called
	private void manageCapital() {
		if(insuficentInput&&employees.size()>0) {
			Agent toFire=findHighestPaidEmployee();
			toFire.getFired();
			employees.remove(employees.indexOf(toFire));
		}
		else if(tenTickProfit>0){
			if(profession.getClass().equals(RawProfession.class)) {
				manageCapitalRaw();
			}
			if(profession.getClass().equals(RefinedProfession.class)) {
				manageCapitalRefined();
			}
		}
	}
	//method used to manage capital for RefinedProfessions
	private void manageCapitalRefined() {
		int index=outputGoodsContainsType(profession.getOutputType());
		boolean overProduction=false;
		int totalSalery=getTotalSalery();
		if(index>=0) {
			//if a GoodAmount of output type is greater than 1.5 tickProduction then the company is overproducing
			//in which case an employee is fired 
			if(outputGoods.get(index).getAmount()>tickProduction*1.5&&outputGoods.get(index).getAmount()>100&&employees.size()>0) {
				overProduction=true;
				Agent toFire=findHighestPaidEmployee();
				toFire.getFired();
				employees.remove(employees.indexOf(toFire));
			}
			//if the company cannot afford to buy next rounds production and pay salaries then it will fire an employee
			else if(money<totalSalery+goods.getGoodPrice(profession.getInputType())*getRefinedMultiplier()&&employees.size()>0) {
				overProduction=true;
				Agent toFire=findHighestPaidEmployee();
				toFire.getFired();
				employees.remove(employees.indexOf(toFire));
			}
		}
		totalSalery=getTotalSalery();
		//if the company has enough money to cover next production rounds costs and was overproducing it will buy capital
		if(money>profession.getInputAmount()*goods.getGoodPrice(profession.getInputType())*getRefinedMultiplier()+totalSalery&&overProduction) {
			int capitalMoney= (money-(int)(profession.getInputAmount()*goods.getGoodPrice(profession.getInputType())*getRefinedMultiplier())-totalSalery)/2;
			int capitalAmount=capitalMoney/goods.getGoodPrice(profession.getCapitalType());
			if(capitalAmount>0) {
				placeBuyRequest(profession.getCapitalType(), capitalAmount);
			}
		}
		//if the company was not overproducing and has enough money to cover costs then it calls buyCapitalHireRefined to decide what to buy
		if(money>profession.getInputAmount()*goods.getGoodPrice(profession.getInputType())*getRefinedMultiplier()+totalSalery&&!overProduction) {
			buyCapitalHireRefined(totalSalery);
		}
	}	
	//method is used to detemine whether it is more cost effective to hire employees or buy capital, takes integer totalSalary
	private void buyCapitalHireRefined(int totalSalery) {
		//amount of money for capital is calculated
		int capitalMoney= (money-(int)(profession.getInputAmount()*goods.getGoodPrice(profession.getInputType())*getRefinedMultiplier())-totalSalery)/2;
		//shopping list is made
		int[] sL= new int[2];
		boolean done= false;
		//while there is money for capital it well keep evaluating
		while(!done) {
			//using items already in shopping list marginal multipliers are calculated
			double marginalCapitalMultiplier=getMarginalRefinedMultiplier(sL[0]+10, sL[1])-getMarginalRefinedMultiplier(sL[0], sL[1]);
			double marginalEmployeeMultiplier=getMarginalRefinedMultiplier(sL[0], sL[1]+1)-getMarginalRefinedMultiplier(sL[0], sL[1]);
			//price of multiplier increse is calculated
			double priceOfMarginalCapital=goods.getGoodPrice(profession.getCapitalType())*10/marginalCapitalMultiplier;
			double priceOfMarginalLabor=goods.getLaborPrice()/marginalEmployeeMultiplier;
			//cheaper of options is added and capitalMoney is decreased
			if(priceOfMarginalCapital<=priceOfMarginalLabor&&capitalMoney>=goods.getGoodPrice(profession.getCapitalType())*10) {
				sL[0]+=10;
				capitalMoney-=goods.getGoodPrice(profession.getCapitalType())*10;
			}
			else if(priceOfMarginalLabor<=priceOfMarginalCapital&&capitalMoney>=goods.getLaborPrice()){
				sL[1]++;
				capitalMoney-=goods.getLaborPrice();
			}
			//if nothing is added to shopping list then the loop is broken
			else {
				done=true;
			}
		}
		//buyRequests placed for shopping list entries
		if(sL[0]>0) {
			placeBuyRequest(profession.getCapitalType(), sL[0]);
		}
		if(sL[1]>0) {
			placeBuyRequest(goods.getLaborIndex(), sL[1]);
		}
	}
	//method used to find multiplier with altered values, takes in integer capital, integer employee
	private double getMarginalRefinedMultiplier(int i, int j) {
		int capital=0;
		int index=currentGoodsContainsType(profession.getCapitalType());
		if(index>-1) {
			capital=currentGoods.get(index).getAmount();
		}
		return (Math.log((capital+i)/(employees.size()+1+j/10)+1)/Math.log(2)+1)*(employees.size()+1+j);
	}
	//method used to manage capital for raw methods
	private void manageCapitalRaw() {
		int index=outputGoodsContainsType(profession.getOutputType());
		boolean overProduction=false;
		int totalSalery=getTotalSalery();
		if(index>=0) {
			//checks to see if there is a GoodAmount with more than 1.5 times tickProduction, in which case there is overproduction
			//and the highest paid employee is fired
			if(outputGoods.get(index).getAmount()>tickProduction*1.5&&outputGoods.get(index).getAmount()>100&&employees.size()>0) {
				overProduction=true;
				Agent toFire=findHighestPaidEmployee();
				toFire.getFired();
				employees.remove(employees.indexOf(toFire));
			}
			//if the company cannot cover next ticks salaries then the hightest paid employee is fired
			else if(money<totalSalery&&employees.size()>0) {
				overProduction=true;
				Agent toFire=findHighestPaidEmployee();
				toFire.getFired();
				employees.remove(employees.indexOf(toFire));
			}
		}
		totalSalery=getTotalSalery();
		int capitalMoney=(money-totalSalery)/2;
		//depending on whether or not there is overproduction buyCapitalLandRaw or buyCapitalLandHireRaw is called
		if(capitalMoney>0&&overProduction) {
			buyCapitalLandRaw(capitalMoney);
		}
		if(capitalMoney>0&&!overProduction) {
			buyCapitalLandHireRaw(capitalMoney);
		}
		
	}
	//method is used to buy capital, land, or hire employees, takes integer capitalMoney
	private void buyCapitalLandHireRaw(int capitalMoney) {
		//shopping list made
		int[] sL= new int[3];
		boolean done=false;
		while(!done) {
			//marginal multiplier for capital, land, and employees found
			double marginalCapitalMultiplier=getMarginalRawMultiplier(sL[0]+10, sL[1], sL[2])-getMarginalRawMultiplier(sL[0], sL[1], sL[2]);
			double marginalLandMultiplier=getMarginalRawMultiplier(sL[0], sL[1]+1, sL[2])-getMarginalRawMultiplier(sL[0], sL[1], sL[2]);
			double marginalLaborMultiplier=getMarginalRawMultiplier(sL[0], sL[1], sL[2]+1)-getMarginalRawMultiplier(sL[0], sL[1], sL[2]);
			//cost of marginal increase gound
			double priceOfMarginalCapital=goods.getGoodPrice(profession.getCapitalType())*10/marginalCapitalMultiplier;
			double priceOfMarginalLand= goods.getLandPrice()/marginalLandMultiplier;
			double priceOfMarginalLabor=goods.getLaborPrice()/marginalLaborMultiplier;
			//if the company has the maximum amount of land allowed it is no allowed to buy more
			if(currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount()+sL[1]>=tile.getAllLand()/(2*goods.getRawGoodNum())) {
				priceOfMarginalLand=Integer.MAX_VALUE;
			}
			//if capital is the cheapest per multiplier increase it is added to the shopping list, capitalMoney is decreased by the cost
			if(priceOfMarginalCapital<=priceOfMarginalLand&&priceOfMarginalCapital<=priceOfMarginalLabor
					&&capitalMoney>=goods.getGoodPrice(profession.getCapitalType())*10) {
				sL[0]+=10;
				capitalMoney-=goods.getGoodPrice(profession.getCapitalType())*10;
			}
			//if land is the cheapest per multiplier increase it is added to the shopping list, capitalMoney is decreased by the cost
			else if(priceOfMarginalLand<=priceOfMarginalCapital&&priceOfMarginalLand<=priceOfMarginalLabor&&capitalMoney>=goods.getLandPrice()) {
				sL[1]++;
				capitalMoney-=goods.getLandPrice();
			}
			//if labor is the cheapest per multiplier increase it is added to the shopping list, capitalMoney is decreased by the costs
			else if(priceOfMarginalLabor<=priceOfMarginalCapital&&priceOfMarginalLabor<=priceOfMarginalLand&&capitalMoney>=goods.getLaborPrice()) {
				sL[2]++;
				capitalMoney-=goods.getLaborPrice();
			}
			//if there is not enough money to add to the shopping list then the loop is broken
			else {
				done=true;
			}
		}
		//buyRequests are placed for entries in the shopping list
		if(sL[0]>0) {
			placeBuyRequest(profession.getCapitalType(), sL[0]);
		}
		if(currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount()+sL[1]>=tile.getAllLand()/2/goods.getRawGoodNum()) {
			sL[1]=tile.getAllLand()/2/goods.getRawGoodNum()-currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount();
		}
		if(sL[1]>0) {
			placeBuyRequest(goods.getLandIndex(), sL[1]);
		}
		if(sL[2]>0) {
			placeBuyRequest(goods.getLaborIndex(), sL[2]);
		}
	}
	//method used to buy capital and land
	private void buyCapitalLandRaw(int capitalMoney) {
		//creation of shopping list, 2 is set to 0 because no employees will be hired
		int[] sL= new int[3];
		sL[2]=0;
		boolean done=false;
		//
		while(!done) {
			//marginal multiplier calculated
			double marginalCapitalMultiplier=getMarginalRawMultiplier(sL[0]+10, sL[1], sL[2])-getMarginalRawMultiplier(sL[0], sL[1], sL[2]);
			double marginalLandMultiplier=getMarginalRawMultiplier(sL[0], sL[1]+1, sL[2])-getMarginalRawMultiplier(sL[0], sL[1], sL[2]);
			//cost of margrinal multiplier calculated
			double priceOfMarginalCapital=goods.getGoodPrice(profession.getCapitalType())*10/marginalCapitalMultiplier;
			double priceOfMarginalLand= goods.getLandPrice()/marginalLandMultiplier;
			if(sL[1]<0) {
				System.out.println("negative");
			}
			//if Company has too much land it cannot buy more
			if(currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount()+sL[1]>=tile.getAllLand()/2/goods.getRawGoodNum()) {
				priceOfMarginalLand=Integer.MAX_VALUE;
			}
			//if capital is cheaper it is added, capitalMoney is decreased by the cost
			if(priceOfMarginalCapital<=priceOfMarginalLand&&capitalMoney>=goods.getGoodPrice(profession.getCapitalType())*10) {
				sL[0]+=10;
				capitalMoney-=goods.getGoodPrice(profession.getCapitalType())*10;
			}
			//if land is cheaper it is added, capitalMoney is decreased by the cost
			else if(priceOfMarginalLand<=priceOfMarginalCapital&&capitalMoney>=goods.getLandPrice()) {
				sL[1]++;
				capitalMoney-=goods.getLandPrice();
			}
			//if there is not enough money to buy more the loop is broken
			else {
				done=true;
			}
		}
		//buyRequests are placed for entries in the shopping list
		if(sL[0]>0) {
			placeBuyRequest(profession.getCapitalType(), sL[0]);
		}
		if(currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount()+sL[1]>=tile.getAllLand()/2/goods.getRawGoodNum()) {
			sL[1]=tile.getAllLand()/2/goods.getRawGoodNum()-currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount();
		}
		if(sL[1]>0) {
			placeBuyRequest(goods.getLandIndex(), sL[1]);
		}
	}
	//method to get multiplier if variables are altered, takes integer capital, integer land, integer employees
	private double getMarginalRawMultiplier(int i, int j, int k) {
		int capital=0;
		int index=currentGoodsContainsType(profession.getCapitalType());
		if(index>=0) {
			if(currentGoods.get(index).getAmount()>0) {
				capital=currentGoods.get(index).getAmount()/10;
			}
		}
		index=currentGoodsContainsType(goods.getLandIndex());
		int land=0;
		if(index>=0) {
			if(currentGoods.get(index).getAmount()>0) {
				land=currentGoods.get(index).getAmount();
			}
			else {
				return 0;
			}
		}
		int people=1+employees.size();
		capital+=i/10;
		land+=j;
		people+=k;
		return (1+(Math.log((capital+people)/land+1)/Math.log(2)))*(land+1);
	}
	//method used to produce for RawProfessions
	private void produceRaw() {
		//multiplier retrieved
		double multiplier= getRawMultiplier();
		//if the multiplier is greater than 0 then the Company can produce
		if(multiplier>0) {
			int output=(int) (profession.getOutputAmount()*multiplier);
			int index=outputGoodsContainsType(profession.getOutputType());
			//production is logged
			tickProduction=output;
			tile.logProduction(new GoodAmount(profession.getOutputType(), output));
			//if outputGoods contains a GoodAmount with the same type as output then production added to that,
			//if not then a new GoodAmount is added
			if(index>=0) {
				outputGoods.get(index).increaseAmount(output);
			}
			else {
				outputGoods.add(new GoodAmount(profession.getOutputType(), output));
			}
		}
	}
	//method used to produce for RefinedProfessions
	private void produceRefined() {
		//multiplier is retrieved
		double multiplier= getRefinedMultiplier();
		int index=currentGoodsContainsType(((RefinedProfession) profession).getInputType());
		if(index>=0) {
			//if there are enough input goods for production then the multiplier is unchanged, if there are not 
			//enough input goods insufficent input is set to true and the multiplier is altered to be small enough for the amount of input
			if(((RefinedProfession) profession).getInputAmount()*multiplier>currentGoods.get(index).getAmount()) {
				multiplier=currentGoods.get(index).getAmount()/((RefinedProfession) profession).getInputAmount();
				insuficentInput=true;
			}
			int output=(int) (profession.getOutputAmount()*multiplier);
			index=outputGoodsContainsType(profession.getOutputType());
			//production is logged
			tickProduction=output;
			tile.logProduction(new GoodAmount(profession.getOutputType(), output));
			//if outputGoods contains a GoodAmount with the same type as output then production added to that,
			//if not then a new GoodAmount is added
			if(index>=0) {
				outputGoods.get(index).increaseAmount(output);
			}
			else {
				outputGoods.add(new GoodAmount(profession.getOutputType(), output));
			}
		}
	}
	//method used for the decay of goods in currentGoods and outputGoods, used to stop Companies stockpiling
	private void decayGoods() {
		for(int i=0; i<currentGoods.size();i++) {
			if(currentGoods.get(i).getAmount()>100) {
				currentGoods.get(i).decreaseAmount(currentGoods.get(i).getAmount()/20);
			}
		}
		for(int i=0; i<outputGoods.size();i++) {
			if(outputGoods.get(i).getAmount()>100) {
				outputGoods.get(i).decreaseAmount(outputGoods.get(i).getAmount()/20);
			}
		}
	}
	//methods used to clear ledgers, works buy passing removeBuy/SellRequest the first entry of
	//they buy/sellLedger until both ledgers are empty
	private void clearLedgers() {
		while(sellLedger.size()>0){
			removeSellRequest(sellLedger.get(0));
		}
		while(buyLedger.size()>0){
			removeBuyRequest(buyLedger.get(0));
		}
	}
	//method used to change profession
	private void changeProfession() {
		//if profits over the last ten ticks is negative there is a chance for the company to change profession
		if(tenTickProfit<0&&Math.random()>0.9) {
			//current profitability is calculated
			int currentProfitability=potentialProfitability(profession);
			//current capital investment is calculated
			int currentCapitalInvestment=getCapital()*goods.getGoodPrice(profession.getCapitalType());
			//a list of potential professions is compiled
			ArrayList<Profession> potential= assessProfitability();
			ArrayList<Integer> potentailMarketSaturation= new ArrayList<Integer>();
			ArrayList<Integer> potentialProfitability= new ArrayList<Integer>();
			//marketSaturation and profitability for these professions is calculated
			for(int i=0; i<potential.size();i++) {
				potentailMarketSaturation.add(potentialMarketSaturation(potential.get(i)));
				potentialProfitability.add(potentialProfitability(potential.get(i)));
			}
			//potential professions are evaluated, if they have positive market saturation or less profitability then
			//the current profession they are removed
			for(int i=0; i<potential.size();i++) {
				if(potential.get(i).getCapitalType()==profession.getCapitalType()) {
					if(potentailMarketSaturation.get(i)>0&&currentProfitability>potentialProfitability.get(i)) {
						potential.remove(i);
						potentailMarketSaturation.remove(i);
						potentialProfitability.remove(i);
						i--;
					}
				}
				else {
					if(potentailMarketSaturation.get(i)>0&&currentProfitability-(currentCapitalInvestment/10)>potentialProfitability.get(i)) {
						potential.remove(i);
						potentailMarketSaturation.remove(i);
						potentialProfitability.remove(i);
						i--;
					}
				}
			}
			//totalProfitability is calculated
			int totalProfitability=0;
			for(int i=0; i<potential.size();i++) {
				totalProfitability+=potentialProfitability.get(i);
			}
			//profession is randomly selected from list weighted by profitability
			double random=Math.random();
			if(potential.size()>0) {
				int addUp=0;
				for(int i=0; i<potential.size(); i++) {
					if(random<((double)addUp+potentialProfitability.get(i))/totalProfitability) {
						profession=potential.get(i);
						i=potential.size();
					}
					else {
						addUp+=potentialProfitability.get(i);
					}
				}
			}
		}
	}
	//potential profitability of a profession is assessed here, takes Profession toAssess
	private int potentialProfitability(Profession potential) {
		int profitability=0;
		if(potential.getClass().equals(RefinedProfession.class)) {
			profitability=(int) ((goods.getGoodPrice(potential.getOutputType())*potential.getOutputAmount())-
					(((RefinedProfession)potential).getInputAmount()*goods.getGoodPrice(((RefinedProfession) potential).getInputType())*getPotentialRefinedMultiplier(potential)));
		}
		else {
			profitability= (int) (goods.getGoodPrice(potential.getOutputType())*potential.getOutputAmount()*getPotentialRawMultiplier(potential));
		}
		return profitability;
	}
	//potential multiplier for a RawProfession is retrieved here, takes Profession toAssess
	private double getPotentialRawMultiplier(Profession profession) {
		int capital=0;
		int index=currentGoodsContainsType(profession.getCapitalType());
		if(index>=0) {
			if(currentGoods.get(index).getAmount()>0) {
				capital=currentGoods.get(index).getAmount()/10;
			}
		}
		index=currentGoodsContainsType(goods.getLandIndex());
		int land=0;
		if(index>=0) {
			if(currentGoods.get(index).getAmount()>0) {
				land=currentGoods.get(index).getAmount();
			}
			else {
				return 0;
			}
		}
		else {
			return 0;
		}
		int people=1+employees.size();
		return (1+(Math.log((capital+people)/land+1)/Math.log(2)))*(land);
	}
	//potential refinedMultiplier calculated here, takes Profession toAssess
	private double getPotentialRefinedMultiplier(Profession profession) {
		int capital=0;
		int index=currentGoodsContainsType(profession.getCapitalType());
		if(index>-1) {
			capital=currentGoods.get(index).getAmount();
		}
		return (Math.log(capital/(employees.size()+1)+1)/Math.log(2)+1)*(employees.size()+1);
	}
	//marketSaturation of a profession is calculated, takes Profession toAsses
	//market saturation is market supply- market demand, lower is better
	private int potentialMarketSaturation(Profession potentail) {
		return market.getSellAmount(potentail.getOutputType())-market.getBuyAmount(potentail.getOutputType());
	}
	//compiles a list of potential professions 
	private ArrayList<Profession> assessProfitability() {
		ArrayList<Profession> potential=new ArrayList<Profession>();
		for(int i=0; i<professions.getProfessionsSizeWOEmployee();i++) {
			if(i!=profession.getIndex()) {
				//if output price is greater than input price it is added to the list
				if(professions.getProfession(i).getClass().equals(RefinedProfession.class)) {
					if((goods.getGoodPrice(professions.getProfession(i).getOutputType())*professions.getProfession(i).getOutputAmount())-
							(((RefinedProfession)professions.getProfession(i)).getInputAmount()*goods.getGoodPrice(((RefinedProfession) professions.getProfession(i)).getInputType()))>0) {
						potential.add(professions.getProfession(i));
					}
				}
				//if the Company has land RawProfessions are added to the list
				else if(professions.getProfession(i).getClass().equals(RawProfession.class)) {
					if(currentGoodsContainsType(goods.getLandIndex())>=0) {
						if(currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount()>0) {
							potential.add(professions.getProfession(i));
						}
					}
				}
			}
		}
		//list is returned
		return potential;
	}
	//method used to pay owner
	private void payOwner() {
		//if the company has made a profit over the last 10 ticks then the owner is paid
		if(tenTickProfit>0&&inExistance>10) {
			int payout=0;
			//payout is based off of profit and owner behavior
			payout=(int) (tenTickProfit*owner.getBehavior());
			if(money>payout) {
				//owner is payed and Company money is decreased
				owner.payOwner(payout);
				money=money-payout;
			}
		}
	}
	//tickProfit is updated, not quite the average but is good enough
	private void tickProfit() {
		if(inExistance%10==0) {
			hundredTickProfit=(int)((double)(hundredTickProfit)*0.9+tenTickProfit);
		}
		tenTickProfit=(int)((double)(tenTickProfit)*0.9+tickProfit);
		tickProfit=0;
	}
	//method used to clear up output and input Goods, mergers GoodAmount of the same type and removes 
	//GoodAmounts with an amount of 0
	private void clearUp() {
		for(int i=0;i<currentGoods.size();i++) {
			if(currentGoods.get(i).getAmount()>0) {
				if(profession.getClass().equals(RefinedProfession.class)) {
					if(((RefinedProfession) profession).getInputType()!=currentGoods.get(i).getType()&&
							profession.getCapitalType()!=currentGoods.get(i).getType()) {
						outputGoods.add(currentGoods.get(i));
						currentGoods.remove(i);
						i--;
					}
				}
				else if(currentGoods.get(i).getType()!=goods.getAllGoodsNum()-1&&
						profession.getCapitalType()!=currentGoods.get(i).getAmount()) {
					outputGoods.add(currentGoods.get(i));
					currentGoods.remove(i);
					i--;
				}
			}
			else {
				currentGoods.remove(i);
				i--;
			}
		}
		for(int i=0;i<outputGoods.size();i++) {
			if(outputGoods.get(i).getAmount()==0) {
				outputGoods.remove(i);
				i--;
			}
		}
	}
	//returns Agent highestPaid, highestPaid is an Agent from employees with the highest salary
	private Agent findHighestPaidEmployee() {
		Agent highestPaid=employees.get(0);
		for(int i=1; i<employees.size();i++){
			if(highestPaid.getSalary()<employees.get(i).getSalary()) {
				highestPaid=employees.get(i);
			}
		}
		return highestPaid;
	}
	//method used to pay employees, goes through employees and transfers each money equal to their salary
	private void payEmployees() {
		for(int i=0; i<employees.size();i++) {
			//checks to see if company can afford to pay salary
			if(money>=employees.get(i).getSalary()) {
				int salery=employees.get(i).getSalary();
				//employee paid and Company money decreased
				employees.get(i).paySalery(salery);
				money=money-salery;
				//tickProfit updated
				tickProfit=tickProfit-salery;
			}
		}
	}
	//method used to sell GoodAmounts in outputGoods
	private void sellOutput() {
		for(int i=0; i<outputGoods.size();i++) {
			boolean check=true;
			//checks to see the GoodAmount is not input
			if(profession.getClass().equals(RefinedProfession.class)) {
				if(((RefinedProfession) profession).getInputType()==outputGoods.get(i).getType()) {
					check=false;
				}
			}
			//checks to see the GoodAmount is not caputal
			if(outputGoods.get(i).getType()==profession.getCapitalType()) {
				check=false;
			}
			//checks the GoodAmount is not land
			if(profession.getClass().equals(RawProfession.class)&&outputGoods.get(i).getType()==goods.getAllGoodsNum()-1) {
				check=false;
			}
			//if all checks are passed then sellRequest is placed
			if(check) {
				placeSellRequest(outputGoods.get(i).getType(), outputGoods.get(i).getAmount());
			}
			//if check is failed it is moved to currentGoods
			else {
				if(!check) {
					currentGoods.add(outputGoods.get(i));
					outputGoods.remove(i);
				}
			}
		}
	}
	//method used to buy production needs to buy RefinedProfession
	private void buyProductionNeeds() {
		int type=((RefinedProfession) profession).getInputType();
		int index=currentGoodsContainsType(type);
		//amount to buy is calculated
		int amount=(int) (((RefinedProfession) profession).getInputAmount()*getRefinedMultiplier());
		//if Company can afford it, it will try to stockpile a bit of input
		if(amount*goods.getGood(type).getPrice()*2<money) {
			double multiply=money/2/goods.getGood(type).getPrice()/amount;
			if(multiply<=5&&multiply>1) {
				amount=(int) (amount*multiply);
			}
			else if(multiply>5){
				amount=amount*5;
			}
		}
		//amount is decreased by amount in currentGoods
		if(index>=0) {
			amount=amount-currentGoods.get(index).getAmount();
		}
		if(amount*goods.getGood(type).getPrice()>money){
			amount=money/goods.getGood(type).getPrice();
		}
		//buyRequest placed
		placeBuyRequest(type, amount);
	}
	//method used to get refinedMultiplier, returns double multiplier
	private double getRefinedMultiplier() {
		int capital=0;
		int index=currentGoodsContainsType(profession.getCapitalType());
		if(index>-1) {
			capital=currentGoods.get(index).getAmount();
		}
		return (Math.log(capital/(employees.size()+1)+1)/Math.log(2)+1)*(employees.size()+1);
	}
	//method used to get rawMultiplier, returns double multiplier
	private double getRawMultiplier() {
		int capital=0;
		int index=currentGoodsContainsType(profession.getCapitalType());
		if(index>=0) {
			if(currentGoods.get(index).getAmount()>0) {
				capital=currentGoods.get(index).getAmount()/10;
			}
		}
		index=currentGoodsContainsType(goods.getLandIndex());
		int land=0;
		if(index>=0) {
			if(currentGoods.get(index).getAmount()>0) {
				land=currentGoods.get(index).getAmount();
			}
			else {
				return 0;
			}
		}
		else {
			return 0;
		}
		int people=1+employees.size();
		return (1+(Math.log((capital+people)/land+1)/Math.log(2)))*(land);
	}
	//method used by Owner to put money in company, takes integer investment, increses money by investment 
	public void investMoney(int investment) {
		money=money+investment;
	}
	//returns integer money
	public int getMoney() {
		return money;
	}
	//returns Agent owner
	public Agent getOwner() {
		return owner;
	}
	//returns ArrayList<Agen> employees
	public ArrayList<Agent> getEmployees(){
		return employees;
	}
	//method used to close down Company, takes Agent a
	public void closeDown(Agent a) {
		System.out.println("Owner: "+owner.toString()+" Request: "+a.toString());
		//checks to see if a is the same Agent as owner
		if(a.equals(owner)) {
			//all employees are fired
			while(employees.size()>0) {
				employees.get(0).getFired();
				employees.remove(0);
			}
			if(owner==null) {
				System.out.println("Owner==null");
			}
			//all goods and money  are transfered to owner
			owner.reciveGoods(currentGoods);
			owner.reciveGoods(outputGoods);
			//ledgers are cleared
			clearLedgers();
			owner.payOwner(money);
			owner=null;
			//tile is told to remove the company
			tile.removeCompany(this);
		}
	}
	
	//checks to see if Company can fulfil buyRequest, takes LedgerEntry buyRequest, LedgerEntry sellRequest
	//the smaller amount of the two requests is the amount to be bought, price is the price of the sellRequest
	//if money is greater than amount*price then true is returned, if not false is returned
	public boolean canFullfillBuyRequest(LedgerEntry buyRequest, LedgerEntry sellRequest) {
		if(owner==null) {
			System.out.println("Selling when meant to be closed");
		}
		int amount;
		if(buyRequest.getAmount()>sellRequest.getAmount()) {
			amount =sellRequest.getAmount();
		}
		else {
			amount=buyRequest.getAmount();
		}
		if(buyLedger.contains(buyRequest)) {
			if(money>=amount*sellRequest.getPrice()) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	//method used to fulfil buyRequests, takes LedgerEntry buyRequest, LedgerEntry sellReques
	//the smaller amount of the two requests is the amount to be bought, price is the price of the sellRequest
	//money is decreased by price*amount, amount is added to a GoodAmount of the same type or a new GoodAmount is created
	public void buyRequestFullfilled(LedgerEntry buyRequest, LedgerEntry sellRequest) {
		if(owner==null) {
			System.out.println("Selling when meant to be closed");
		}
		int amount=0;
		if(buyRequest.getAmount()>sellRequest.getAmount()) {
			amount= sellRequest.getAmount();
		}
		else {
			amount=buyRequest.getAmount();
		}
		if(buyLedger.contains(buyRequest)&&money>=amount*sellRequest.getPrice()) {
			money=money-amount*sellRequest.getPrice();
			tickProfit=tickProfit-amount*sellRequest.getPrice();
			int index=currentGoodsContainsType(buyRequest.getType());
			if(index>=0) {
				currentGoods.get(index).increaseAmount(amount);
			}
			else {
				currentGoods.add(new GoodAmount(buyRequest.getType(), amount));
			}
			if(buyRequest.getAmount()==0) {
				buyLedger.remove(buyLedger.indexOf(buyRequest));
			}
		}
	}
	//check to see if labour buyRequest is present in buyLedger, takes LedgerEntry buyRequest, LedgerEntry sellRequest
	public boolean canFullfillLaborBuyRequest(LedgerEntry buyRequest, LedgerEntry sellRequest) {
		if(buyLedger.contains(buyRequest)) {
			return true;
		}
		return false;
	}
	//adds Agent from sellRequest to employees, takes LedgerEntry buyRequest, LedgerEntry sellRequest
	public void laborBuyRequestFullfilled(LedgerEntry buyRequest, LedgerEntry sellRequest) {
		if(owner==null) {
			System.out.println("Labor Buy Requet Fullfilled When Owner Null "+this.toString());
		}
		employees.add((Agent) sellRequest.getFrom());
		if(buyRequest.getAmount()==0) {
			removeBuyRequest(buyRequest);
		}
	}
	//checks to see if Company can fulfil a sellRequest, takes LedgerEntry buyRequest, LedgerEntry sellRequest
	//the smaller amount of the two requests is the amount to be bought, if a GoodAmount of the type and with
	// a greater or equal amount is in outputGoods then true is returned, if not false is returned
	public boolean canFullfillSellRequest(LedgerEntry sellRequest, LedgerEntry buyRequest) {
		if(owner==null) {
			System.out.println("Selling when meant to be closed");
		}
		int amount;
		if(buyRequest.getAmount()>sellRequest.getAmount()) {
			amount =sellRequest.getAmount();
		}
		else {
			amount=buyRequest.getAmount();
		}
		if(sellLedger.contains(sellRequest)) {
			int index=outputGoodsContainsType(sellRequest.getType());
			if(index>=0) {
				if(outputGoods.get(index).getAmount()>=amount) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	//fulfils sellReqeust for Company, takes LedgerEntry buyRequest, LedgerEntry sellRequest
	//the smaller amount of the two requests is the amount to be bought, price is the price of the sellRequest
	//money is increased by price*amount, GoodAmount of the same type in outputGoods is decreased by amount
	public void sellRequestFullfilled(LedgerEntry sellRequest, LedgerEntry buyRequest) {
		if(owner==null) {
			System.out.println("Selling when meant to be closed");
		}
		int amount=0;
		if(sellRequest.getAmount()>buyRequest.getAmount()) {
			amount=buyRequest.getAmount();
		}
		else {
			amount=sellRequest.getAmount();
		}
		if(sellLedger.contains(sellRequest)) {
			int index=outputGoodsContainsType(sellRequest.getType());
			if(index>=0) {
				if(outputGoods.get(index).getAmount()>=amount) {
					money=money+amount*sellRequest.getPrice();
					tickProfit=tickProfit+amount*sellRequest.getPrice();
					outputGoods.get(index).decreaseAmount(amount);
					if(sellRequest.getAmount()==0) {
						sellLedger.remove(sellLedger.indexOf(sellRequest));
					}
				}
			}
		}
	}
	//returns int index of GoodAmount in currentGoods of the same type, if not present returns -1, takes int type
	private int currentGoodsContainsType(int type) {
		for(int i=0; i<currentGoods.size();i++) {
			if(currentGoods.get(i).getType()==type) {
				return i;
			}
		}
		return -1;
	}
	//returns int index of GoodAmount in outputGoods of the same type, if not present returns -1, takes int type
	private int outputGoodsContainsType(int type) {
		for(int i=0; i<outputGoods.size();i++) {
			if(outputGoods.get(i).getType()==type) {
				return i;
			}
		}
		return -1;
	}
	//returns int index of LedgerEntry in sellLedger of the same type, if not present returns -1, takes int type
	public int sellLedgerContainsType(int type) {
		for(int i=0; i<sellLedger.size();i++) {
			if(sellLedger.get(i).getType()==type) {
				return i;
			}
		}
		return -1;
	}
	//returns int index of LedgerEntry in buyLedger of the same type, if not present returns -1, takes int type
	public int buyLedgerContainsType(int type) {
		for(int i=0; i<buyLedger.size();i++) {
			if(buyLedger.get(i).getType()==type) {
				return i;
			}
		}
		return -1;
	}
	//removes given sellRequest form Company and market sellLedger, takes LedgerEntry toRemove
	public void removeSellRequest(LedgerEntry entry) {
		if(market.containsSellEntry(entry)) {
			market.removeSellEntry(entry);
		}
		if(sellLedger.contains(entry)){
			sellLedger.remove(sellLedger.indexOf(entry));
		}
	}
	//removes given buyRequest form Company and market buyLedger, takes LedgerEntry toRemove
	public void removeBuyRequest(LedgerEntry entry) {
		if(market.containsBuyEntry(entry)) {
			market.removeBuyEntry(entry);
		}
		if(buyLedger.contains(entry)) {
			buyLedger.remove(buyLedger.indexOf(entry));
		}
	}
	//places a buyRequest by making a new LedgerEntry to pass to market.submitBuyEntry and store in buyLedger
	//takes int type, int amount
	public void placeBuyRequest(int type, int amount) {
		if(amount>0) {
			tile.addToDemand(type, amount);
			LedgerEntry temp=new LedgerEntry(type, amount, goods.getGoodPrice(type), this);
			buyLedger.add(temp);
			market.submitBuyEntry(temp);
		}
	}
	//places a sellRequest by making a new LedgerEntry to pass to market.submitSellEntry and store in sellLedger
	//takes int type, int amount
	public void placeSellRequest(int type, int amount) {
		if(amount>0) {
			tile.addToSupply(type, amount);
			LedgerEntry temp=new LedgerEntry(type, amount, goods.getGoodPrice(type), this);
			sellLedger.add(temp);
			market.submitSellEntry(temp);	
		}
	}
	//places a labor buyReuqest by making a new LedgerEntry to pass to market.submitLaborBuyEntry and store in sellLedger
	//takes int amount
	public void placeLaborBuyRequest(int amount) {
		if(amount>0) {
			LedgerEntry temp=new LedgerEntry(goods.getLaborIndex(), amount, goods.getLaborPrice(), this);
			sellLedger.add(temp);
			market.submitLaborBuyEntry(temp);
		}
	}
	//method used when employee leaves company, removes Agent from employees, take Agent toRemove
	public void employeeLeave(Agent agent) {
		if(employees.contains(agent)) {
			employees.remove(employees.indexOf(agent));
		}
	}
	//returns int inExistance
	public int getInExistance() {
		return inExistance;
	}
	//returns int hundredTickProfit
	public int getHundredTickProfit() {
		return hundredTickProfit;
	}
	//return int capital of current profession in currentGoods
	public int getCapital() {
		if(profession.getCapital()!=null) {
			int index= currentGoodsContainsType(profession.getCapitalType());	
			if(index>=0) {
				return currentGoods.get(index).getAmount();
			}
			return 0;
		}
		return 0;
	}
	//used when the owner takes money from the Company, decreases money by given amount, takes int amount
	public void ownerTakeingMoney(int amount) {
		money=money-amount;
	}
	//returns int land in currentGoods
	public int getLand() {
		int index=currentGoodsContainsType(goods.getLandIndex());
		if(index>=0) {
			return currentGoods.get(index).getAmount();
		}
		return 0;
	}
	//returns int totalSalary, is the total of all salaries of Agents in employees
	private int getTotalSalery() {
		int total=0;
		for(int i=0; i<employees.size();i++) {
			total+=employees.get(i).getSalary();
		}
		return total;
	}
}
