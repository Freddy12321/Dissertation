package dissertation6;

public class GoodAmount {
	//object used to hold type and amount of goods in one place
	//type and amount integer class variables
	int type;
	int amount;
	//constructor, takes in two integers for type and amount
	public GoodAmount(int type, int a) {
		this.type=type;
		amount=a;
	}
	//get methods
	public int getType() {
		return type;
	}
	public int getAmount(){
		return amount;
	}
	//increment amount by input
	public void increaseAmount(int change) {
		amount=amount+change;
	}
	//decrement amount by input
	public void decreaseAmount(int change) {
		amount=amount-change;
	}
	//set amount to input
	public void setAmount(int set) {
		amount=set;
	}
}
