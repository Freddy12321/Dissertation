package dissertation6;

public class LedgerEntry {
	//object used to bundle all information to be submitted to the market
	//integer class variables for amount, type, and price
	int amount;
	int type;
	int price;
	//Company or Agent that the request if from
	MarketOperator from;
	//constructor that takes in; int type, int amount, int price, MarketOperator agent
	public LedgerEntry(int t, int a, int p, MarketOperator agent){
		amount=a;
		type=t;
		price=p;
		from=agent;
	}
	//returns amount
	public int getAmount() {
		return amount;
	}
	//returns type
	public int getType() {
		return type;
	}
	//returns price
	public int getPrice() {
		return price;
	}
	//returns MarketOperator
	public MarketOperator getFrom() {
		return from;
	}
	//reduces amount by input amount
	public void reduceAmount(int reduce) {
		amount=amount -reduce;
	}
}
