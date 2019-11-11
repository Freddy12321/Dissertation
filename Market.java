package dissertation6;

import java.util.ArrayList;
import java.util.Collections;

public class Market {
	//Class is used by Agents and Companies to trade goods
	//reference to GoodPrices
	GoodPrices goods;
	//ArrayLists of LedgerEntry objects, buy and sell ledger
	ArrayList<LedgerEntry> buyLedger=new ArrayList<LedgerEntry>();
	ArrayList<LedgerEntry> sellLedger=new ArrayList<LedgerEntry>();
	//Class constructor, takes GoodPrices
	public Market(GoodPrices g) {
		goods=g;
	}
	//method used to update good prices, takes Integer[] supply, Integer[] demand
	public void updatePrices(int[] supply, int[] demand){
		for(int i=0; i<supply.length;i++) {
			if(supply[i]>demand[i]&&goods.getGoodPrice(i)>1) {
				goods.getGood(i).decreasePrice();
			}
			if(supply[i]<demand[i]&&goods.getGoodPrice(i)<1000) {
				goods.getGood(i).increasePrice();
			}
		}
	}
	//checks to see if buyLedger contains the LedgerEntry, LedgerEntry toCheck, returns boolean
	public boolean containsBuyEntry(LedgerEntry toCheck) {
		if(buyLedger.contains(toCheck)) {
			return true;
		}
		return false;
	}
	//removes given LedgerEntry from buyLedger, LedgerEntry toRemove
	public void removeBuyEntry(LedgerEntry toRemove) {
		if(buyLedger.contains(toRemove)) {
			buyLedger.remove(buyLedger.indexOf(toRemove));
		}
	}
	//checks to see if sellLedger contains the LedgerEntry, LedgerEntry toCheck, returns boolean
	public boolean containsSellEntry(LedgerEntry toCheck) {
		if(sellLedger.contains(toCheck)) {
			return true;
		}
		return false;
	}
	//removes given LedgerEntry from sellLedger, LedgerEntry toRemove
	public void removeSellEntry(LedgerEntry toRemove) {
		if(sellLedger.contains(toRemove)) {
			sellLedger.remove(sellLedger.indexOf(toRemove));
		}
	}
	//is called when Agents or Companies submit a sellRequest, takes an LedgerEntry sellerEntry
	public void submitSellEntry(LedgerEntry sellerEntry) {
		//ArrayList of potential buyRequests found using sortByType
		ArrayList<LedgerEntry> potential=sortByType(sellerEntry.getType(), buyLedger);
		if(potential.size()>0) {
			//potential entries shuffled
			Collections.shuffle(potential);
			if(potential.size()>0) {
				//method passes sellerEntry to sellToPotential to be fulfilled
				sellerEntry=sellToPotential(sellerEntry, potential);
				if(sellerEntry.getAmount()>0) {
					//if request still is not fulfilled it is added to sellLedger
					sellLedger.add(sellerEntry);
				}
			}
			else {
				//if no entries are found request is added to sellLedger
				sellLedger.add(sellerEntry);
			}
		}
		//if no entries are found request is added to sellLedger
		else {
			sellLedger.add(sellerEntry);
		}
	}
	//is called when Agents or Companies submit a buyRequest, takes an LedgerEntry buyerEntry
	public void submitBuyEntry(LedgerEntry buyerEntry) {
		//ArrayList of potential sellRequests found using sortByType
		ArrayList<LedgerEntry> potential=sortByType(buyerEntry.getType(), sellLedger);
		if(potential.size()>0) {
			potential=removeSameAgent(buyerEntry.getFrom(), potential);
			//potential entries shuffled
			Collections.shuffle(potential);
			if(potential.size()>0) {
				//method passes buyEntry to buyFromPotential to be fulfilled
				buyFromPotential(buyerEntry, potential);
				if(buyerEntry.getAmount()>0) {
					//if request still is not fulfilled it is added to buyLedger
					buyLedger.add(buyerEntry);
				}
			}
			//if no entries are found request is added to buyLedger
			else {
				buyLedger.add(buyerEntry);
			}
		}
		//if no entries are found request is added to buyLedger
		else {
			buyLedger.add(buyerEntry);
		}
	}
	//separate method for labour sellRequests
	public void submitLaborSellEntry(LedgerEntry sellerEntry) {
		//ArryList of potential buyRequests compiled by sortByType
		ArrayList<LedgerEntry> potential=sortByType(sellerEntry.getType(), buyLedger);
		if(potential.size()>0) {
			//list sorted from greatest to smallest price
			potential=sortByGreaterPrice(sellerEntry.getPrice(), potential);
			if(potential.size()>0) {
				//labour sold using sellLaborToPotential
				sellLaborToPotential(sellerEntry, potential);
			}
			else {
				//if there are no potential requests, sellRqeust is added to sellLedger
				sellLedger.add(sellerEntry);
			}
		}
		else {
			//if there are no potential requests, sellRqeust is added to sellLedger
			sellLedger.add(sellerEntry);
		}
	}
	//separate method for labour buyRequests
	public void submitLaborBuyEntry(LedgerEntry buyerEntry) {
		//ArryList of potential sellRequests compiled by sortByType
		ArrayList<LedgerEntry> potential=sortByType(buyerEntry.getType(), sellLedger);
		if(potential.size()>0) {
			//list sorted from smallest to greatest price
			potential=sortByLesserPrice(buyerEntry.getPrice(), potential);
			if(potential.size()>0) {
				//labour bought using buyLaborFromPotential
				buyLaborFromPotential(buyerEntry, potential);
				if(buyerEntry.getAmount()>0) {
					//if request is still unfulfilled it is added to buyLedger
					buyLedger.add(buyerEntry);
				}
			}
			else {
				//if there are no potential requests, buyRqeust is added to buyLedger
				buyLedger.add(buyerEntry);
			}
		}
		else {
			//if there are no potential requests, buyRqeust is added to buyLedger
			buyLedger.add(buyerEntry);
		}
	}
	//method used to fulfil buyRequests, takes LedgerEntry buyerEntry, ArrayList<LedgerEntry> potential
	private LedgerEntry buyFromPotential(LedgerEntry buyerEntry, ArrayList<LedgerEntry> potential) {
		for(int i=0; i<potential.size(); i++) {
			LedgerEntry sellerEntry=potential.get(i);
			//checks to see if both buyer and seller can fulfil requests
			if(sellerEntry.getFrom().canFullfillSellRequest(sellerEntry, buyerEntry)&&buyerEntry.getFrom().canFullfillBuyRequest(buyerEntry, sellerEntry)) {
				//informs buyer and seller that requests have been fulfilled
				sellerEntry.getFrom().sellRequestFullfilled(sellerEntry, buyerEntry);
				buyerEntry.getFrom().buyRequestFullfilled(buyerEntry, sellerEntry);
				//reduces the amounts in sell and buy entries by smaller of the 2
				if(sellerEntry.getAmount()>buyerEntry.getAmount()) {	
					i=potential.size();
					buyerEntry.reduceAmount(buyerEntry.getAmount());
					sellerEntry.reduceAmount(buyerEntry.getAmount());
				}
				else if(buyerEntry.getAmount()==sellerEntry.getAmount()){
					i=potential.size();
					buyerEntry.reduceAmount(buyerEntry.getAmount());
					sellerEntry.reduceAmount(buyerEntry.getAmount());
					sellLedger.remove(sellLedger.indexOf(sellerEntry));
				}
				else {
					buyerEntry.reduceAmount(sellerEntry.getAmount()); 
					sellerEntry.reduceAmount(sellerEntry.getAmount());
					sellLedger.remove(sellLedger.indexOf(sellerEntry));
				}
			}
		}
		//returns buyer entry
		return buyerEntry;
	}
	//method used to fulfil sellRequests, takes LedgerEntry sellerEntry, ArrayList<LedgerEntry> potential
	private LedgerEntry sellToPotential(LedgerEntry sellerEntry, ArrayList<LedgerEntry> potential) {
		for(int i=0; i<potential.size(); i++) {
			LedgerEntry buyerEntry=potential.get(i);
			//checks to see if both buyer and seller can fulfil requests
			if(sellerEntry.getFrom().canFullfillSellRequest(sellerEntry, buyerEntry)&&buyerEntry.getFrom().canFullfillBuyRequest(buyerEntry, sellerEntry)) {
				//informs buyer and seller that requests have been fulfilled
				sellerEntry.getFrom().sellRequestFullfilled(sellerEntry, buyerEntry);
				buyerEntry.getFrom().buyRequestFullfilled(buyerEntry, sellerEntry);
				//reduces the amounts in sell and buy entries by smaller of the 2
				if(sellerEntry.getAmount()<buyerEntry.getAmount()) {
					i=potential.size();
					buyerEntry.reduceAmount(sellerEntry.getAmount());
					sellerEntry.reduceAmount(sellerEntry.getAmount());
				}
				else if(buyerEntry.getAmount()==sellerEntry.getAmount()){
					i=potential.size();
					sellerEntry.reduceAmount(sellerEntry.getAmount());
					buyerEntry.reduceAmount(sellerEntry.getAmount());
					buyLedger.remove(buyLedger.indexOf(buyerEntry));
				}
				else {
					sellerEntry.reduceAmount(buyerEntry.getAmount());
					buyerEntry.reduceAmount(buyerEntry.getAmount());
					buyLedger.remove(buyLedger.indexOf(buyerEntry));
				}
			}
		}
		//returns seller entry
		return sellerEntry;
	}
	//separate method used to fulfil labour sellRequests
	private void sellLaborToPotential(LedgerEntry sellerEntry, ArrayList<LedgerEntry> potential) {
		for(int i=0; i<potential.size(); i++) {
			LedgerEntry buyerEntry=potential.get(i);
			//checks to see if both buyer and seller can fulfil requests
			if(((Company) buyerEntry.getFrom()).canFullfillLaborBuyRequest(buyerEntry, sellerEntry)){
				//requests are fulfilled
				((Agent) sellerEntry.getFrom()).laborSellRequestFullFilledBuyPrice(buyerEntry, sellerEntry);
				((Company) buyerEntry.getFrom()).laborBuyRequestFullfilled(buyerEntry, sellerEntry);
				//buyerEntry amount is reduced by one if it is above 0
				if(buyerEntry.getAmount()>0) {
					buyerEntry.reduceAmount(1);
				}
				i=potential.size();
			}
		}
	}
	//separate method used to fulfil labour buyRequests
	private LedgerEntry buyLaborFromPotential(LedgerEntry buyerEntry, ArrayList<LedgerEntry> potential) {
		for(int i=0; i<potential.size(); i++) {
			LedgerEntry sellerEntry=potential.get(i);
			//checks to see if both buyer and seller can fulfil requests
			if(((Company) buyerEntry.getFrom()).canFullfillLaborBuyRequest(buyerEntry, sellerEntry)){
				//requests are fulfilled
				((Agent) sellerEntry.getFrom()).laborSellRequestFullFilledSellPrice(buyerEntry, sellerEntry);
				((Company) buyerEntry.getFrom()).laborBuyRequestFullfilled(buyerEntry, sellerEntry);
				//buyerEntry amount is reduced by one if it is above 0
				if(buyerEntry.getAmount()>0) {
					buyerEntry.reduceAmount(1);
				}
				//if buyRequest is fulfilled then loop is broken
				else {
					i=potential.size();
				}
			}
		}
		//buyRequest is returned
		return buyerEntry;
	}
	//returns a list of potential LedgerEntry ArrayList of the same type, takes Integer type, ArrayList<LedgerEntry> toSearch
	private ArrayList<LedgerEntry> sortByType(int type, ArrayList<LedgerEntry> toSearch){
		ArrayList<LedgerEntry> potential= new ArrayList<LedgerEntry>();
		for(int i=0; i<toSearch.size(); i++) {
			if(toSearch.get(i).getType()==type) {
				potential.add(toSearch.get(i));
			}
		}
		return potential;
	}
	//removes any entry in the potential ArrayList of LedgerEntry from the same Agent or Company
	private ArrayList<LedgerEntry> removeSameAgent(MarketOperator from, ArrayList<LedgerEntry> potential) {
		for(int i=0;i<potential.size();i++) {
			if(potential.get(i).getFrom().equals(from)) {
				potential.remove(i);
			}
		}
		return potential;
	}
	//removes any entry of a smaller than the given price, sorts the remaining ArrayList from greatest to smallest price
	private ArrayList<LedgerEntry> sortByGreaterPrice(int price, ArrayList<LedgerEntry> toSearch) {
		ArrayList<LedgerEntry> potential = new ArrayList<LedgerEntry>();
		for(int i=0; i<toSearch.size();i++) {
			if(toSearch.get(i).getPrice()>=price) {
				potential.add(toSearch.get(i));
			}
		}
		//merge sort from greatest to smallest
		mergeSortUp(potential);
		return potential;
	}
	//sorts ArrayList of LedgerEnties from greatest to smallest
	private ArrayList<LedgerEntry> mergeSortUp(ArrayList<LedgerEntry> potential) {
		if(potential.size()>1) {
			ArrayList<LedgerEntry> sorted = new ArrayList<LedgerEntry>();
			ArrayList<LedgerEntry> firstHalf=new ArrayList<LedgerEntry>();
			firstHalf.addAll(potential.subList(0, potential.size()/2));
			ArrayList<LedgerEntry> secondHalf=new ArrayList<LedgerEntry>();
			secondHalf.addAll(potential.subList(potential.size()/2, potential.size()));
			if(firstHalf.size()>1) {
				firstHalf=mergeSortUp(firstHalf);
			}
			if(secondHalf.size()>1) {
				secondHalf=mergeSortUp(secondHalf);
			}
			for(int i=0; i<potential.size();i++) {
				if(0==firstHalf.size()) {
					sorted.addAll(secondHalf);
					return sorted;
				}
				else if(0==secondHalf.size()) {
					sorted.addAll(firstHalf);
					return sorted;
				}
				else if(firstHalf.get(0).getPrice()>=secondHalf.get(0).getPrice()) {
					sorted.add(firstHalf.get(0));
					firstHalf.remove(0);
				}
				else if(firstHalf.get(0).getPrice()<secondHalf.get(0).getPrice()) {
					sorted.add(secondHalf.get(0));
					secondHalf.remove(0);
				}
			}
			return sorted;
		}
		return potential;
	}
	//removes any entry of a greater than the given price, sorts the remaining ArrayList from smallest to greatest price
	private ArrayList<LedgerEntry> sortByLesserPrice(int price, ArrayList<LedgerEntry> toSearch) {
		ArrayList<LedgerEntry> potential = new ArrayList<LedgerEntry>();
		for(int i=0; i<toSearch.size();i++) {
			if(toSearch.get(i).getPrice()<=price) {
				potential.add(toSearch.get(i));
			}
		}
		//merge sort from smallest to greatest
		mergeSortDown(potential);
		return potential;
	}
	//sorts ArrayList of LedgerEnties from smallest to greatest
	private ArrayList<LedgerEntry> mergeSortDown(ArrayList<LedgerEntry> potential) {
		if(potential.size()>1) {
			ArrayList<LedgerEntry> sorted = new ArrayList<LedgerEntry>();
			ArrayList<LedgerEntry> firstHalf=new ArrayList<LedgerEntry>();
			firstHalf.addAll(potential.subList(0, potential.size()/2));
			ArrayList<LedgerEntry> secondHalf=new ArrayList<LedgerEntry>();
			secondHalf.addAll(potential.subList(potential.size()/2, potential.size()));
			if(firstHalf.size()>1) {
				firstHalf=mergeSortDown(firstHalf);
			}
			if(secondHalf.size()>1) {
				secondHalf=mergeSortDown(secondHalf);
			}
			for(int i=0; i<potential.size();i++) {
				if(0==firstHalf.size()) {
					sorted.addAll(secondHalf);
					return sorted;
				}
				if(0==secondHalf.size()) {
					sorted.addAll(firstHalf);
					return sorted;
				}
				if(firstHalf.get(0).getPrice()<=secondHalf.get(0).getPrice()) {
					sorted.add(firstHalf.get(0));
					firstHalf.remove(0);
				}
				else if(firstHalf.get(0).getPrice()>secondHalf.get(0).getPrice()) {
					sorted.add(secondHalf.get(0));
					secondHalf.remove(0);
				}
			}
			return sorted;
		}
		return potential;
	}
	//returns the amount of sellRequests of a given type
	public int getSellAmount(int type) {
		int amount=0;
		for(int i=0; i<sellLedger.size();i++) {
			if(sellLedger.get(i).getType()==type) {
				amount+=sellLedger.get(i).getAmount();
			}
		}
		return amount;
	}
	//returns the amount of buyRequests of a given type
	public int getBuyAmount(int type) {
		int amount=0;
		for(int i=0; i<buyLedger.size();i++) {
			if(buyLedger.get(i).getType()==type) {
				amount+=buyLedger.get(i).getAmount();
			}
		}
		return amount;
		
	}
	//returns int[] of all sellAmounts
	public int[] getSellAmounts() {
		int[] totalSellGoods=new int[goods.getAllGoodsNum()];
		for(int i=0;i<sellLedger.size();i++) {
			totalSellGoods[sellLedger.get(i).getType()]+=sellLedger.get(i).getAmount();
		}
		return totalSellGoods;
	}
	//returns int[] of all buyAmounts
	public int[] getBuyAmounts() {
		int[] totalBuyGoods=new int[goods.getAllGoodsNum()];
		for(int i=0;i<buyLedger.size();i++) {
			totalBuyGoods[buyLedger.get(i).getType()]+=buyLedger.get(i).getAmount();
		}
		return totalBuyGoods;
	}
	//returns int[] of all goodPrices
	public double[] getPrices() {
		double[] prices= new double[goods.getAllGoodsNum()];
		for(int i=0;i<prices.length;i++) {
			prices[i]=goods.getGood(i).getPrice();
		}
		return prices;
	}
}
