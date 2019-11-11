package dissertation6;

import java.util.ArrayList;

public class Agent extends MarketOperator{
	//The Agent class is used to create Agent objects, which are the basic unit of the economy.
	//references to other objects
	Profession profession;
	Company company=null;
	Professions professions;
	//integers used to keep track of information
	int happiness=0;
	int salary=0;
	int timeLifeNeedsNotMet=0;
	//behavior variable, used to give Agents a bit of individuality
	double behavior=Math.random();
	//booleans used to determine the Agents state
	boolean lifeNeedsMet, productionNeedsMet, isEmployee, isEmployer, mustChange;
	//boolean array of lifeNeeds met this round used for debugging
	boolean[] needsMetThisRound;
	//Class constructor, takes in Market m, Professions j, GoodPrices g, Tile t, ArrayList<GoodAmount> startingGoods, Integer money
	public Agent(Market m, Professions j, GoodPrices g, Tile t, ArrayList<GoodAmount> startingGoods, int money) {
		//using super MarketOperator constructor
		super(m, g, t, money);
		professions=j;
		lifeNeedsMet=false;
		isEmployee=false;
		isEmployer=false;
		needsMetThisRound= new boolean[goods.getLifeGoods().size()];
		currentGoods.addAll(startingGoods);
		setUpProfession();
	}
	//picks a random Raw- or RefinedProfession as the Agents starting profession
	private void setUpProfession() {
		int random;
		if(currentGoodsContainsType(goods.getLandIndex())!=-1) {
			if(currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount()>0) {
				random=(int)(professions.getProfessionsSizeWOEmployee()*Math.random());
			}
			else {
				random=(int)(goods.getRawGoodNum()+(goods.getRefinedGoodNum()*Math.random()));
			}
		}
		else {
			random=(int)(goods.getRawGoodNum()+(goods.getRefinedGoodNum()*Math.random()));
		}
		profession=professions.getProfession(random);
	}
	//tick method called to clear ledgers
	public void tickClearLedgers() {
		clearLedgers();
	}
	//tick method called to produce for RawProfessions, produces goods then sells output
	public void tickProduceRaw() {
		if(profession.getClass().equals(RawProfession.class)) {
			produceRaw();
			sell();
		}
	}
	//tick method called to produce for RefinedProfessions, buy input goods, produces goods, and then sells output
	public void tickProduceRefined() {
		if(profession.getClass().equals(RefinedProfession.class)){
			buyRefinedProfession();
			produceRefined();
			sell();
		}
	}
	//tick method for buying lifeNeeds
	public void tickBuyLifeNeeds() {
		buyLifeGoods();
	}
	//tick method for buying other goods, which are luxuries, capital, and land, depending on profession
	public void tickBuyOther() {
		if(lifeNeedsMet) {
			if(profession.getClass().equals(RawProfession.class)) {
				buyOtherRaw();
			}
			else if(profession.getClass().equals(RefinedProfession.class)) {
				buyOtherRefined();
			}
			else if(profession.getClass().equals(EmployProfession.class)) {
				buyOtherEmploy();
			}
		}
	}
	//method for handling other aspects of tick, such as consuming life and luxury goods, changing professions,
	//starting a company, decay of goods, cleaning up output/input goods, and jobHunting
	public void tickOther() {
		if(isEmployer) {
			if(company==null) {
				System.out.println(toString());
			}
			manageCompany();
		}
		if(profession.getIndex()==professions.getEmployee().getIndex()) {
			jobHunt();
		}
		consume();
		cleanUpOutputGoods();
		cleanUpCurrentGoods();
		changeProfessionV2();
		if(!isEmployer) {
			startCompany();
		}
		checkToSellLand();
		decayGoods();
	}
	//method for purchasing input goods for refined professions
	private void buyRefinedProfession() {
		//production cycle is the cost of all input goods
		int productionCycleCost=0;
		int[] productionShoppingList= new int[1];//must be changed for multiple inputs
		for(int i=0; i<productionShoppingList.length;i++) {
			productionCycleCost=(int) (productionCycleCost+profession.getInputAmount()*getMultiplierRefined()*goods.getGoodPrice(profession.getInputType()));
		}
		//if the agent can afford a full production cycle it requests it, if it can't it buyt the fraction it can
		for(int i=0; i<productionShoppingList.length;i++) {
			if(money>=productionCycleCost) {
				productionShoppingList[i]=productionShoppingList[i]+profession.getInputAmount();
			}
			else {
				productionShoppingList[i]=productionShoppingList[i]+(int)(profession.getInputAmount()*(double)money/productionCycleCost);
			}
		}
		//placing buy request for input goods
		for(int i=0; i<productionShoppingList.length;i++) {
			if(productionShoppingList[i]>0) {
				placeBuyRequest(profession.getInputType(), productionShoppingList[i]);
			}
		}
	}
	//methods for buying land, capital, and luxury goods for raw professions
	private void buyOtherRaw() {
		//portion of money for luxury goods and capital goods determined by total money and behavior
		int capMoney=(int) (money/2*(1-behavior));
		int luxMoney=(int) (money/2*behavior);
		ArrayList<GoodPrice> luxuries= goods.getLuxuryGoods();
		//shopping lists for capital/land/luxuries
		int[] luxuriesShoppingList= new int[luxuries.size()];
		int[] capitalShoppingList= new int[1];//to change if multiple capital professions created
		int landToBuy=0;
		int capitalCycleCost=goods.getGoodPrice(profession.getCapitalType())*10;//also to change
		int bestLuxury=0;
		double bestLuxuryCost=0;
		boolean done=false;
		//keeps adding the luxury good that gives the most happiness for price to shopping list
		//note every aditional luxury good of the same type gives one less happiness, so first gives 10,
		//next gives 9, 8 ect. this is repeated until luxMoney is all assigned.
		while(!done) {
			for(int i=0; i<luxuries.size();i++) {
				if((10-luxuriesShoppingList[i])/luxuries.get(i).getPrice()>bestLuxuryCost) {
					bestLuxury=i;
					bestLuxuryCost=(10-luxuriesShoppingList[i])/luxuries.get(i).getPrice();
				}
			}
			if(luxMoney>=luxuries.get(bestLuxury).getPrice()) {
				luxuriesShoppingList[bestLuxury]++;
				luxMoney-=luxuries.get(bestLuxury).getPrice();
			}
			else {
				done=true;
			}
		}
		done=false;
		//determines whether it is most cost effective to purchase more land or more capital, then adds whichever to the 
		//shopping list, deducting the price from capMoney. this is repeated until all capMoney is gone
		while(!done) {
			double costPerUnitPorducedFromMarginalLand=getProfitofMarginalLand();
			double costPerUnitPorducedFromMarginalCapital=getProfitofMarginalCapital();
			if(currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount()+landToBuy>=tile.getAllLand()/2/goods.getRawGoodNum()) {
				costPerUnitPorducedFromMarginalLand=Integer.MAX_VALUE;
			}
			if(costPerUnitPorducedFromMarginalLand<=costPerUnitPorducedFromMarginalCapital&&capMoney>=goods.getLandPrice()) {
				landToBuy++;
				capMoney-=goods.getLandPrice();
			}
			else if(capMoney>=capitalCycleCost&&costPerUnitPorducedFromMarginalCapital>=costPerUnitPorducedFromMarginalLand) {
				capitalShoppingList[0]+=10;
				capMoney-=capitalCycleCost;
			}
			else {
				done=true;
			}
		}
		//shopping list items are bought
		for(int i=0; i<luxuriesShoppingList.length;i++) {
			if(luxuriesShoppingList[i]>0) {
				placeBuyRequest(luxuries.get(i).getID(), luxuriesShoppingList[i]);
			}
		}
		for(int i=0; i<capitalShoppingList.length;i++) {
			if(capitalShoppingList[i]>0) {
				placeBuyRequest(profession.getCapitalType(), capitalShoppingList[i]);
			}
		}
		if(landToBuy>0) {
			placeBuyRequest(goods.getLandIndex(), landToBuy);
		}
	}
	//buyOther method from RefinedProfession, determines which adn how much of luxury and capital goods are to be bought
	private void buyOtherRefined() {
		//how money is split between luxury/capitalGoods is determined by behavior
		int capMoney=(int) (money/2*(1-behavior));
		int luxMoney=(int) (money/2*behavior);
		ArrayList<GoodPrice> luxuryGoods= goods.getLuxuryGoods();
		int[] luxuriesShoppingList= new int[luxuryGoods.size()];
		int[] capitalShoppingList= new int[1];//to change if multiple capital professions created
		int capitalCycleCost=goods.getGoodPrice(profession.getCapitalType())*10;//also to change
		boolean done=false;
		int bestLuxury=0;
		double bestLuxuryCost=0;
		//keeps adding the luxury good that gives the most happiness for price to shopping list
		//note every aditional luxury good of the same type gives one less happiness, so first gives 10,
		//next gives 9, 8 ect. this is repeated until luxMoney is all assigned.
		while(!done) {
			for(int i=0; i<luxuryGoods.size();i++) {
				if((10-luxuriesShoppingList[i])/luxuryGoods.get(i).getPrice()>bestLuxuryCost) {
					bestLuxury=i;
					bestLuxuryCost=(10-luxuriesShoppingList[i])/luxuryGoods.get(i).getPrice();
				}
			}
			if(luxMoney>=luxuryGoods.get(bestLuxury).getPrice()) {
				luxuriesShoppingList[bestLuxury]++;
				luxMoney-=luxuryGoods.get(bestLuxury).getPrice();
			}
			else {
				done=true;
			}
		}
		//keeps adding capital to capital shopping list so long as there is money
		while(!done) {
			if(capMoney>=capitalCycleCost) {
				capitalShoppingList[0]+=10;
			}
			else {
				done=true;
			}
		}
		//requests for items on shopping lists placed
		for(int i=0; i<luxuriesShoppingList.length;i++) {
			if(luxuriesShoppingList[i]>0) {
				placeBuyRequest(luxuryGoods.get(i).getID(), luxuriesShoppingList[i]);
			}
		}
		for(int i=0; i<capitalShoppingList.length;i++) {
			if(capitalShoppingList[i]>0) {
				placeBuyRequest(profession.getCapitalType(), capitalShoppingList[i]);
			}
		}
	}
	//method for buying luxuryGoods for EmployProfession
	private void buyOtherEmploy() {
		//amount of money spent determined by behavior
		int luxMoney=(int) (money*behavior);
		ArrayList<GoodPrice> luxuryGoods= goods.getLuxuryGoods();
		int[] sL= new int[luxuryGoods.size()];
		int bestLuxury=0;
		double bestLuxuryCost=0;
		boolean done=false;
		//keeps adding the luxury good that gives the most happiness for price to shopping list
		//note every aditional luxury good of the same type gives one less happiness, so first gives 10,
		//next gives 9, 8 ect. this is repeated until luxMoney is all assigned.
		while(!done) {
			for(int i=0; i<luxuryGoods.size();i++) {
				if((10-sL[i])/luxuryGoods.get(i).getPrice()>bestLuxuryCost) {
					bestLuxury=i;
					bestLuxuryCost=(10-sL[i])/luxuryGoods.get(i).getPrice();
				}
			}
			if(luxMoney>=luxuryGoods.get(bestLuxury).getPrice()) {
				sL[bestLuxury]++;
				luxMoney-=luxuryGoods.get(bestLuxury).getPrice();
			}
			else {
				done=true;
			}
		}
		//buyRequests placed for items on shopping list
		for(int i=0; i<sL.length;i++) {
			if(sL[i]>0) {
				placeBuyRequest(luxuryGoods.get(i).getID(), sL[i]);
			}
		}
	}
	//method for Agents to buy lifeGoods
	private void buyLifeGoods() {
		ArrayList<GoodPrice> lifeNeeds= goods.getLifeGoods();
		//totalSpent is used to keep track of how much money has been spent
		int totalSpent=0;
		for(int i=0; i<lifeNeeds.size(); i++) {
			if(money>=lifeNeeds.get(i).getPrice()+totalSpent) {
				//if money is greater than the cost of the next life good +totalSpent then a request for one of it is placed
				placeBuyRequest(lifeNeeds.get(i).getID(), 1);
				totalSpent+=lifeNeeds.get(i).getPrice();
			}
		}
	}
	//checks to see if the Agent is going to sell land, this is done when Agents are not able
	//to meet their needs for an extended period of time
	private void checkToSellLand() {
		if(tile.tickNum>100&&timeLifeNeedsNotMet>25) {
			int index=currentGoodsContainsType(goods.getLandIndex());
			if(index>=0) {
				if(currentGoods.get(index).getAmount()>0) {
					placeSellRequest(goods.getLandIndex(), 1);
				}
			}
		}
		//used by Agents of above average wealth to buy land, even if they don't need it, used 
		//to prevent all land concentrating
		else if(lifeNeedsMet&&getLand()==0&&money>tile.getAverageMoney()&&money>goods.getLandPrice()) {
			placeBuyRequest(goods.getLandIndex(),1);
		}
	}
	//method used to determine if an Agent should start a company
	private void startCompany() {
		//if an Agents life needs are met and it has more than twice the money of an average Agent
		//it is allowed to start a company
		if(lifeNeedsMet&&productionNeedsMet&&money>2*tile.getAverageMoney()) {
			//ArrayList of the Company's starting goods is compiled based off of input, capital, and land
			ArrayList<GoodAmount> startingGoods= new ArrayList<GoodAmount>();
			int index=-1;
			if(profession.getCapital()!=null) {
				index=currentGoodsContainsType(profession.getCapitalType());
				if(index>=0) {
					startingGoods.add(currentGoods.get(index));
					currentGoods.remove(index);
				}
			}
			index=currentGoodsContainsType(goods.getLandIndex());
			if(index>=0) {
				startingGoods.add(currentGoods.get(index));
				currentGoods.remove(index);
			}
			if(profession.getClass().equals(RefinedProfession.class)) {
				index=currentGoodsContainsType(((RefinedProfession) profession).getInputType());
				if(index>=0) {
					startingGoods.add(currentGoods.get(index));
					currentGoods.remove(index);
				}
			}
			//company is created using startingGoods and half the agents money
			makeCompany(money/2,startingGoods);
			//error checking
			if(company==null) {
				System.out.println("Bad Company Made "+this.toString());
			}
		}
	}
	//used to decay goods if a good amount holds more than 100 of a good, used to
	//prevent agents from stockpiling
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
	//if the cost of labour is higher than the Agents salary it looks for jobs that pay better =
	private void jobHunt() {
		if(!isEmployee||!lifeNeedsMet) {
			placeLaborSellEntry();
		}
	}
	//method used to produce for RefinedProfessions
	private void produceRefined() {
		productionNeedsMet=true;
		//multiplier, by default 1, is retrieved, it is based off of capital
		double multiplier=getMultiplierRefined();
		//checks to see if currentGoods contains enough input goods
		for(int i=0; i<1;i++) {
			int index=currentGoodsContainsType(profession.getInputType());
			if(index>=0) {
				//if there are not enough goods for the current multiplier then the multuplier is set
				//to what there is enough input for
				if((double)(currentGoods.get(index).getAmount())/profession.getInputAmount()<multiplier) {
					multiplier=(double)(currentGoods.get(index).getAmount())/profession.getInputAmount();
				}
			}
			else {
				productionNeedsMet=false;
			}
		}
		//if the multiplier is 0 that means there is no input goods
		if(multiplier==0||multiplier<0){
			productionNeedsMet=false;
		}
		if(productionNeedsMet==true) {
			//input goods are consumed
			for(int i=0; i<1;i++) {
				int index=currentGoodsContainsType(profession.getInputType());
				currentGoods.get(index).decreaseAmount((int) Math.round(profession.getInputAmount()*multiplier));
			}
			//GoodAmount in outputGoods is either incremented by output or a new GoodAmount is created
			int index=outputGoodsContainsType(profession.getOutputType());
			if(index>=0) {
				outputGoods.get(index).increaseAmount((int)Math.round(profession.getOutputAmount()*multiplier));
			}
			else {
				outputGoods.add(new GoodAmount(profession.getOutputType(), (int)Math.round(profession.getOutputAmount()*multiplier)));
			}
			//production logged in tile
			tile.logProduction(new GoodAmount(profession.getOutputType(), (int)Math.round(profession.getOutputAmount()*multiplier)));
		}
	}
	//method for producing with a RawProfession
	private void produceRaw() {
		productionNeedsMet=true;
		//get multiplier, by default 1, but can be lower if an agent has alot of land and not much capital
		double multiplier=getMultiplierRaw();
		int land=0;
		int index= currentGoodsContainsType(goods.getLandIndex());
		//checks to see if there is land
		if(index>=0) {
			if(currentGoods.get(index).getAmount()>0) {
				land=currentGoods.get(index).getAmount();
			}
		}
		//if land is 0 then the agent must change profession
		if(land==0) {
			mustChange=true;
			productionNeedsMet=false;
		}
		//if there is land the agent produces, either increments a GoodAmount is outputGoods or creates a new one
		if(land>0) {
			index=outputGoodsContainsType(profession.getOutputType());
			if(index>=0) {
				outputGoods.get(index).increaseAmount((int) (land*multiplier*profession.getOutputAmount()));
			}
			else {
				outputGoods.add(new GoodAmount(profession.getOutputType(), (int)(land*multiplier*profession.getOutputAmount())));
			}
			//production logged in tile
			tile.logProduction(new GoodAmount(profession.getOutputType(), (int)(land*multiplier*profession.getOutputAmount())));
		}
	}
	//used by employers to either give or take money from their company, or close it down
	private void manageCompany() {
		//if the Agent and company have too little money or their life needs are not met then they have to close their company
		if((company.getHundredTickProfit()<0||!lifeNeedsMet)&&company.getInExistance()>100) {
			closeCompany();
		}
		else if(company.getMoney()+money<2*tile.getAverageMoney()&&company.getInExistance()>100) {
			closeCompany();
		}
		//if the agent cannot fulfil its life needs and the company has more money it takes some from it
		else if(!lifeNeedsMet&&company.getMoney()>money) {
			int amount=(int) (company.getMoney()*behavior);
			money+=amount;
			company.ownerTakeingMoney(amount);
		}
		//if the Agent has its life needs met and the company has less money the Agent gives it some
		else if(lifeNeedsMet&&company.getMoney()<money) {
			int amount=(int)(money*(1-behavior));
			company.investMoney(amount);
			money-=amount;
		}
	}
	//method clears ledgers by passing removeSell/BuyRequest the first entry until both ledgers are empty
	private void clearLedgers() {
		while(sellLedger.size()>0){
			removeSellRequest(sellLedger.get(0));
		}
		while(buyLedger.size()>0){
			removeBuyRequest(buyLedger.get(0));
		}
	}
	//removes GoodAmount with an amount of 0 and mergers GoodAmounts of the same type
	private void cleanUpOutputGoods() {
		for(int i=0; i<outputGoods.size();i++) {
			if(outputGoods.get(i).getAmount()>0) {
				int index=outputGoodsContainsType(outputGoods.get(i).getType());
				if(index>=0&&index!=i){
					outputGoods.get(index).increaseAmount(outputGoods.get(i).getAmount());
					outputGoods.remove(i);
					i--;
				}
			}
			else {
				outputGoods.remove(i);
				i--;
			}
		}
	}
	//removes GoodAmount with an amount of 0 and mergers GoodAmounts of the same type
	private void cleanUpCurrentGoods() {
		for(int i=0; i<currentGoods.size();i++) {
			if(currentGoods.get(i).getAmount()>0) {
				int index=currentGoodsContainsType(currentGoods.get(i).getType());
				if(index>=0&&index!=i){
					currentGoods.get(index).increaseAmount(currentGoods.get(i).getAmount());
					currentGoods.remove(i);
					i--;
				}
			}
			else if(currentGoods.get(i).getAmount()==0){
				currentGoods.remove(i);
				i--;
			}
		}
	}
	//used by Agents to change profession, is very complex since it is a complex decision
	private void changeProfessionV2() {
		//if the agents life needs are not met it has a chance of changing profession
		//if mustChange is true then is must change profession
		//if it is an employer it cannot change profession
		if(((!lifeNeedsMet&&Math.random()>0.9)||mustChange)&&!isEmployer) {
			int currentProfitability=0;
			int currentCapitalInvestment=0;
			int currentMarketSaturation=0;
			//current profitability, marketSaturation, and capitalInvestment of current profession found
			if(profession.getCapital()!=null) {
				currentProfitability=potentialProfitability(profession);
				currentCapitalInvestment=getCapital()*goods.getGoodPrice(profession.getCapitalType());
				currentMarketSaturation=potentialMarketSaturation(profession);
			}
			//if an Agent is an employee then its current profitability is its salary
			else {
				currentProfitability=salary;
			}
			//list of potential professions compiled
			ArrayList<Profession> potential= assessProfitability();
			ArrayList<Integer> potentailMarketSaturation= new ArrayList<Integer>();
			ArrayList<Integer> potentialProfitability= new ArrayList<Integer>();
			//potential professions have profitability and marketSaturation assessed
			for(int i=0; i<potential.size();i++) {
				potentailMarketSaturation.add(potentialMarketSaturation(potential.get(i)));
				potentialProfitability.add(potentialProfitability(potential.get(i)));
			}
			//entry added for being an employee
			potential.add(professions.getEmployee());
			potentialProfitability.add(goods.getLaborPrice());
			potentailMarketSaturation.add(market.getSellAmount(goods.getLaborIndex())-market.getBuyAmount(goods.getLaborIndex()));
			for(int i=0; i<potential.size();i++) {
				//if a potential profession has a higher marketSaturation then it is removed
				if(potentailMarketSaturation.get(i)>currentMarketSaturation||
						currentProfitability-(currentCapitalInvestment*10)>potentialProfitability.get(i)) {
					potential.remove(i);
					potentailMarketSaturation.remove(i);
					potentialProfitability.remove(i);
					i--;
				}
				//if the Agent cannot afford the input of a potential profession then it removed
				else if(potential.get(i).getInput()!=null) {
					if(potential.get(i).getInputAmount()*goods.getGoodPrice(potential.get(i).getInputType())>money) {
						potential.remove(i);
						potentailMarketSaturation.remove(i);
						potentialProfitability.remove(i);
						i--;
					}
				}
			}
			//profitability of professions added up
			int totalProfitability=0;
			for(int i=0; i<potential.size();i++) {
				totalProfitability+=potentialProfitability.get(i);
			}
			double random=Math.random();
			//profession randomly selected from potential weighted by profitability
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
	//returns marketSaturation of profession, marketSaturation=sellRequests-buyRequests, lower is better, takes Profession toEvaluate
	private int potentialMarketSaturation(Profession potentail) {
		return market.getSellAmount(potentail.getOutputType())-market.getBuyAmount(potentail.getOutputType());
	}
	//evaluates the profitability of a profession, this =totalOutputCost-totalInputCost for RefinedProfessions
	//this =totalOuputCost for RawProfessions, takes Profession toEvaluate
	private int potentialProfitability(Profession potential) {
		int profitability=0;
		if(potential.getClass().equals(RefinedProfession.class)) {
			profitability=(goods.getGoodPrice(potential.getOutputType())*potential.getOutputAmount())-
					(((RefinedProfession)potential).getInputAmount()*goods.getGoodPrice(((RefinedProfession) potential).getInputType()));
		}
		else if(potential.getClass().equals(RawProfession.class)){
			profitability= (int) (goods.getGoodPrice(potential.getOutputType())*potential.getOutputAmount()*getPotentialRawMultiplier(potential));
		}
		return profitability;
	}
	//Returns a potential multiplier for RawProfessions, this is because land is transferable between raw professions
	public double getPotentialRawMultiplier(Profession profession) {
		int index=currentGoodsContainsType(goods.getLandIndex());
		if(index>=0) {
			int land=currentGoods.get(index).getAmount();
			if(land>0) {
				index=currentGoodsContainsType(profession.getCapitalType());
				int capital=1;
				if(index>=0) {
					if(currentGoods.get(index).getAmount()>0) {
						capital+=currentGoods.get(index).getAmount()/10;
					}
				}
				return (Math.log((double)(capital+1)/land+1)/Math.log(2)+1)*land;
			}
			return 0;
		}
		return 0;
	}
	//return an arrayList of potential professions with positive profitability, RawProfessions are only included if the Agent has land
	private ArrayList<Profession> assessProfitability() {
		ArrayList<Profession> potential=new ArrayList<Profession>();
		for(int i=0; i<professions.getProfessionsSizeWOEmployee();i++) {
			if(i!=profession.getIndex()) {
				if(professions.getProfession(i).getClass().equals(RefinedProfession.class)) {
					RefinedProfession temp= (RefinedProfession) professions.getProfession(i);
					if(goods.getGoodPrice(temp.getOutputType())*temp.getOutputAmount()-temp.getInputAmount()*goods.getGoodPrice(temp.getInputType())>0) {
						if(temp.getInputAmount()*goods.getGoodPrice(temp.getInputType())<money){
							potential.add(professions.getProfession(i));
						}
					}
				}
				else if(professions.getProfession(i).getClass().equals(RawProfession.class)) {
					if(currentGoodsContainsType(goods.getLandIndex())>=0) {
						if(currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount()>0) {
							potential.add(professions.getProfession(i));
						}
					}
				}
			}
		}
		return potential;
	}
	//used by Agents to consume life and luxury Goods
	private void consume() {
		lifeNeedsMet=true;
		happiness=0;
		//needs met is set to true, if one need is not met then it is set to false
		for(int i=0;i<goods.getLifeGoods().size();i++) {
			//array for needsMetThisRound is updated
			needsMetThisRound[i]=false;
			int type=goods.getLifeGoods().get(i).getID();
			int currentIndex=currentGoodsContainsType(type);
			if(currentIndex>=0) {
				//if the lifeNeed good amount is greater than 1, one is consumed
				if(currentGoods.get(currentIndex).getAmount()>=1) {
					currentGoods.get(currentIndex).decreaseAmount(1);
					needsMetThisRound[i]=true;
				}
				else {
					lifeNeedsMet=false;
				}
			}
			else {
				lifeNeedsMet=false;
			}
		}
		//if life needs are not met then the amount of time without lifeNeeds is incremented by one
		if(!lifeNeedsMet) {
			timeLifeNeedsNotMet++;
		}
		if(lifeNeedsMet) {
			//if all life needs are met then timeLifeNeedsNotMet is reset to 0
			//luxuryGoods are then consumed for happiness
			timeLifeNeedsNotMet=0;
			for(int i=10; i>0; i--) {
				//up to 10 of every luxuryGood is consumed, the first gives 10 happiness,
				//the next 9, 8 ect
				for(int j=0; j<goods.getLuxuryGoods().size();j++) {
					int type=goods.getLuxuryGoods().get(j).getID();
					int index=currentGoodsContainsType(type);
					if(index>=0) {
						if(currentGoods.get(index).getAmount()>0) {
							currentGoods.get(index).decreaseAmount(1);
							happiness=happiness+i;
						}
					}
				}
			}
		}
	}
	//method used to sell good from outputGoods
	private void sell() {
		for(int i=0; i<outputGoods.size(); i++) {
			int type=outputGoods.get(i).getType();
			int index=currentGoodsContainsType(type);
			int keep=0;
			//if a goods is a luxury good then some of it is kept
			if(goods.lifeGoodsContainsType(type)) {
				keep=1;
			}
			//if the good is a luxuryGood and lifeNeeds are met some of it is kept
			if(lifeNeedsMet&&goods.luxuryGoodsContainsType(type)) {
				keep+=10;
			}
			//goods to keep are passed to currentGoods
			if(index>=0) {
				currentGoods.get(index).increaseAmount(keep-currentGoods.get(index).getAmount());
				outputGoods.get(i).decreaseAmount(keep-currentGoods.get(index).getAmount());
			}
			else {
				currentGoods.add(new GoodAmount(type, keep));
				outputGoods.get(i).decreaseAmount(keep);
			}
			//sellRequet for remaining goods is placed
			if(outputGoods.get(i).getAmount()>0) {
				placeSellRequest(type, outputGoods.get(i).getAmount());
			}
		}
	}
	//profit of marginal (increase by one) capital is calculated. 
	private double getProfitofMarginalCapital() {
		int currentCapital=1;
		int currentLand=0;
		int index=currentGoodsContainsType(goods.getLandIndex());
		if(index>=0) {
			currentLand=currentGoods.get(index).getAmount();
		}
		index=currentGoodsContainsType(profession.getCapitalType());
		if(index>=0) {
			currentCapital+=currentGoods.get(index).getAmount()/10;
		}
		if(currentLand>0) {
			double currentProduction=(Math.log((double)currentCapital/currentLand+1)/Math.log(2)+1)*currentLand*profession.getOutputAmount();
			double marginalProduction=(Math.log((double)currentCapital+1/currentLand+1)/Math.log(2)+1)*currentLand*profession.getOutputAmount();
			double cost=10*goods.getGoodPrice(profession.getCapitalType());
			return cost/(currentProduction-marginalProduction);
		}
		return 0;
	}
	//profit of marginal (increase by one) land is calculated. 
	private double getProfitofMarginalLand() {
		int currentCapital=1;
		int currentLand=0;
		int index=currentGoodsContainsType(goods.getLandIndex());
		if(index>=0) {
			currentLand=currentGoods.get(index).getAmount();
		}
		index=currentGoodsContainsType(profession.getCapitalType());
		if(index>=0) {
			currentCapital+=currentGoods.get(index).getAmount()/10;
		}
		if(currentLand>0) {
			double currentProduction=(Math.log((double)currentCapital/currentLand+1)/Math.log(2)+1)*currentLand*profession.getOutputAmount();
			double marginalProduction=(Math.log((double)currentCapital/currentLand+1)/Math.log(2)+1)*(currentLand+1)*profession.getOutputAmount();
			double cost=10*goods.getGoodPrice(profession.getCapitalType());
			return cost/(currentProduction-marginalProduction);
		}
		return 0;
	}
	//method used to check if currentGoods contains a GoodAmount with a specific type
	//if it does not then -1 is returned, if it does int index is returned, takes int type
	public int currentGoodsContainsType(int type) {
		for(int i=0; i<currentGoods.size();i++) {
			if(currentGoods.get(i).getType()==type) {
				return i;
			}
		}
		return -1;
	}
	//method used to check if outputGoods contains a GoodAmount with a specific type
	//if it does not then -1 is returned, if it does int index is returned, takes int type
	public int outputGoodsContainsType(int type) {
		for(int i=0; i<outputGoods.size();i++) {
			if(outputGoods.get(i).getType()==type) {
				return i;
			}
		}
		return -1;
	}
	//method used to check if sellLedger contains a GoodAmount with a specific type
	//if it does not then -1 is returned, if it does int index is returned, takes int type
	public int sellLedgerContainsType(int type) {
		for(int i=0; i<sellLedger.size();i++) {
			if(sellLedger.get(i).getType()==type) {
				return i;
			}
		}
		return -1;
	}
	//method used to check if buyLedger contains a GoodAmount with a specific type
	//if it does not then -1 is returned, if it does int index is returned, takes int type
	public int buyLedgerContainsType(int type) {
		for(int i=0; i<buyLedger.size();i++) {
			if(buyLedger.get(i).getType()==type) {
				return i;
			}
		}
		return -1;
	}
	//method used to remove a LedgerEntry form Agent sellLedger and Market sellLedger, takes LedgerEntry toRemove
	public void removeSellRequest(LedgerEntry entry) {
		if(market.containsSellEntry(entry)) {
			market.removeSellEntry(entry);
		}
		if(sellLedger.contains(entry)){
			sellLedger.remove(entry);
		}
	}
	//method used to remove a LedgerEntry form Agent buyLedger and Market buyLedger, takes LedgerEntry toRemove
	public void removeBuyRequest(LedgerEntry entry) {
		if(market.containsBuyEntry(entry)) {
			market.removeBuyEntry(entry);
		}
		if(buyLedger.contains(entry)) {
			buyLedger.remove(entry);
		}
	}
	//method used to place buyRequest, takes int type, int amount
	//uses this to create a new LedgerEntry that is passed to market.submitBuyEntry and added to buyLedger
	public void placeBuyRequest(int type, int amount) {
		if(amount>0) {
			tile.addToDemand(type, amount);
			LedgerEntry temp=new LedgerEntry(type, amount, goods.getGoodPrice(type), this);
			buyLedger.add(temp);
			market.submitBuyEntry(temp);
		}
	}
	//method used to place sellRequest, takes int type, int amount
		//uses this to create a new LedgerEntry that is passed to market.submitSellEntry and added to sellLedger
	public void placeSellRequest(int type, int amount) {
		if(amount>0) {
			tile.addToSupply(type, amount);
			LedgerEntry temp=new LedgerEntry(type, amount, goods.getGoodPrice(type), this);
			sellLedger.add(temp);
			market.submitSellEntry(temp);
		}
	}
	//method used to place labor sellEntry, creates new LedgerEntry and passes it to market.submitLaborSellEntry
	private void placeLaborSellEntry() {
		LedgerEntry temp=new LedgerEntry(goods.getLaborIndex(), 1, goods.getLaborPrice(), this);
		sellLedger.add(temp);
		market.submitLaborSellEntry(temp);
	}
	//method used when labour sellEntry is fulfilled, takes LedgerEnry buyRequest, LedgerEntry sellRequest
	//if Agent is in a company it leaves it and becomes an employee of the company from the buyRequest
	//salary becomes sellRequest price
	public void laborSellRequestFullFilledSellPrice(LedgerEntry buyRequest, LedgerEntry sellRequest) {
		if(company!=null) {
			leaveCompany();
		}
		company=(Company) buyRequest.getFrom();
		salary=sellRequest.getPrice();
		isEmployee=true;
		removeSellRequest(sellRequest);
	}
	//method used when labour sellEntry is fulfilled, takes LedgerEnry buyRequest, LedgerEntry sellRequest
	//if Agent is in a company it leaves it and becomes an employee of the company from the buyRequest
	//salary becomes buyRequest price
	public void laborSellRequestFullFilledBuyPrice(LedgerEntry buyRequest, LedgerEntry sellRequest) {
		if(company!=null) {
			leaveCompany();
		}
		company=(Company) buyRequest.getFrom();
		salary=buyRequest.getPrice();
		isEmployee=true;
		removeSellRequest(sellRequest);
	}
	//checks to see if agent has enough money to fulfil buyRequest, takes LedgerEntry buyRequest, LedgerEntry sellRequest
	//takes the smaller of the two entries amounts, takes sell entry price, checks if agent has more money than price*amount
	//if it does true is returned, else false is returned
	public boolean canFullfillBuyRequest(LedgerEntry buyRequest, LedgerEntry sellRequest) {
		if(buyLedger.contains(buyRequest)) {
			int amount;
			if(buyRequest.getAmount()>sellRequest.getAmount()) {
				amount=sellRequest.getAmount();
			}
			else {
				amount=buyRequest.getAmount();
			}
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
	//method used to fulfil buyRequest, takes LedgerEntry buyRequest, LedgerEntry sellRequest
	//amount is the smallest of the two LedgerEntries, price is sellRequest's price,
	//money is decreased by price*amount, if there is a GoodAmount of the right type in 
	//current goods it is incremented by amount, if not a new GoodAmount is made
	public void buyRequestFullfilled(LedgerEntry buyRequest, LedgerEntry sellRequest) {
		int amount=0;
		if(buyRequest.getAmount()>sellRequest.getAmount()) {
			amount= sellRequest.getAmount();
		}
		else {
			amount=buyRequest.getAmount();
		}
		if(buyLedger.contains(buyRequest)&&money>=amount*sellRequest.getPrice()) {
			money=money-amount*sellRequest.getPrice();
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
	//checks to see if Agent can fulfil a sellRequest, takes LedgerEntry sellRequest, LedgerEntry buyRequest
	//amount is the smaller amount of the two LedgerEntries, if the Agent has a GoodAmount in outputGoods
	//of the same type with an amount equal to or greater than the requested amount true is returned,
	//if not false is returned
	public boolean canFullfillSellRequest(LedgerEntry sellRequest, LedgerEntry buyRequest) {
		if(sellLedger.contains(sellRequest)) {
			int amount;
			if(sellRequest.getAmount()>buyRequest.getAmount()) {
				amount=buyRequest.getAmount();
			}
			else {
				amount=sellRequest.getAmount();
			}
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
	//is used to fulfil sellRequests, takes LedgerEntry sellRequest, LedgerEntry buyReqeust
	//amount to sell is equal to the smaller of the two LedgerEntries, price is equal to sellRequest price,
	//if the Agent has a GoodAmount in outputGoods of the same type with an equal or greater amount value then
	//the amount to sell, then it is decremented by amount to sell, money is then incremented by amount*price
	public void sellRequestFullfilled(LedgerEntry sellRequest, LedgerEntry buyRequest) {
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
					outputGoods.get(index).decreaseAmount(amount);
					if(sellRequest.getAmount()==0) {
						sellLedger.remove(sellLedger.indexOf(sellRequest));
					}
				}
			}
		}
	}
	//method used to create company, takes an integer investment, ArrayList<GoodAmount> startingGoods
	//the agent leaves the company it is in if it is an employee, a new company object is created 
	//with the parameters passed, money is decremented by investment, tile is informed of the new company,
	//profession is set to Employe, information is printed to console.
	private void makeCompany(int investment, ArrayList<GoodAmount> startingGoods) {
		if(isEmployee&&company!=null) {
			leaveCompany();
		}
		company= new Company(this, profession, professions, market, goods, tile, startingGoods, investment);
		money=money-investment;
		tile.addCompany(company);
		isEmployer=true;
		profession=professions.getEmployer();
		System.out.println("Opening Company  "+company.toString()+"  "+this.toString());
	}
	//method used to close company, prints message to console, tells the company to close down,
	//then sets company to null, employer to false, employee to false, and makes profession equal employee
	private void closeCompany() {
		System.out.println("Closing Company"+company.toString()+"  "+this.toString());
		if(company!=null) {
			company.closeDown(this);
		}
		company=null;
		isEmployer=false;
		isEmployee=false;
		profession=professions.getEmployee();
	}
	//method used to recieve goods from a closing down company, goods are added to currentGoods
	public void reciveGoods(ArrayList<GoodAmount> goods) {
		for(int i=0; i<goods.size();i++) {
			int index=currentGoodsContainsType(goods.get(i).getType());
			if(index>=0) {
				currentGoods.get(index).increaseAmount(goods.get(i).getAmount());
			}
			else {
				currentGoods.add(goods.get(i));
			}
		}
	}
	//method used to get multiplier for RefinedProfession, multiplier is affected by capital goods
	//with every 10 increasing it
	public double getMultiplierRefined() {
		int index=currentGoodsContainsType(profession.getCapitalType());
		int capital=0;
		if(index>=0) {
			if(currentGoods.get(index).getAmount()>0) {
				capital=currentGoods.get(index).getAmount();
			}
		}
		return Math.log((double)capital+1)/Math.log(2)+1;
	}
	//method used to get multiplier for RawProfession, multiplier is affected by capital goods
	//with every 10 increasing it, multiplier is also decreased by land, this is because the 
	//total output is then multiplier by land, if land it 0 then the multiplier is 0
	public double getMultiplierRaw() {
		int index=currentGoodsContainsType(goods.getLandIndex());
		if(index>=0) {
			int land=currentGoods.get(index).getAmount();
			if(land>0) {
				index=currentGoodsContainsType(profession.getCapitalType());
				int capital=1;
				if(index>=0) {
					if(currentGoods.get(index).getAmount()>0) {
						capital+=currentGoods.get(index).getAmount()/10;
					}
				}
				return Math.log((double)capital/land+1)/Math.log(2)+1;
			}
			return 0;
		}
		return 0;
	}
	//method used when an Agent is fired by a company. company is set to null, 
	//isEmployee is set to false, and salary is set to 0
	public void getFired() {
		company=null;
		isEmployee=false;
		salary=0;
	}
	//method used when Agent leaves company for a better paid job, company is told employee is leaving,
	//salary is set to 0, company to null, and isEmployee to false
	private void leaveCompany() {
		company.employeeLeave(this);
		salary=0;
		company=null;
		isEmployee=false;
	}
	//returns boolean lifeNeedsMet
	public boolean getLifeNeedsMet() {
		return lifeNeedsMet;
	}
	//returns boolean productionNeedsMet
	public boolean getProductionNeedsMet() {
		return productionNeedsMet;
	}
	//returns Profession profession
	public Profession getProfession() {
		return profession;
	}
	//returns integer money
	public int getMoney() {
		return money;
	}
	//used to increase money, takes integer toAdd. toAdd is added to money
	public void addMoney(int toAdd) {
		money=money+toAdd;
	}
	//returns the double behavior
	public double getBehavior() {
		return behavior;
	}
	//method used to pay company owner, takes intger pay. money gets pay added to it
	public void payOwner(int pay) {
		money=money+pay;
	}
	//method used to pay company employee, takes intger salary. money gets salarys added to it
	public void paySalery(int salery) {
		money=money+salery;
	}
	//returns integer happiness
	public int getHappiness() {
		return happiness;
	}
	//returns intger salary
	public int getSalary() {
		return salary;
	}
	//returns if the Agent has land, true if it does, false if it does not
	public boolean hasLand() {
		if(currentGoodsContainsType(goods.getLandIndex())>=0) {
			if(currentGoods.get(currentGoodsContainsType(goods.getLandIndex())).getAmount()>0) {
				return true;
			}
		}
		return false;
	}
	//returns the integer capital, which is the amount of capital the Agent has for its current Profession
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
	//returns an int[] of the Agents current luxuries
	public int[] getLuxuries() {
		int[] luxuries= new int[goods.getLuxuryGoods().size()];
		for(int i=0; i<luxuries.length;i++) {
			int index=currentGoodsContainsType(goods.getLuxuryGoods().get(i).getID());
			if(index>=0) {
				luxuries[i]=currentGoods.get(index).getAmount();
			}
			else {
				luxuries[i]=0;
			}
		}
		return luxuries;
	}
	//returns a Company refernce to the Agents current company
	public Company getCompany() {
		return company;
	}
	//returns integer land, which is the Agents current land
	public int getLand() {
		int index=currentGoodsContainsType(goods.getLandIndex());
		if(index>=0) {
			return currentGoods.get(index).getAmount();
		}
		return 0;
	}
	//returns boolean[] needsMet|ThisRound, this is array shows which needs have been met
	public boolean[] getNeedsMetThisRound() {
		return needsMetThisRound;
	}
}
