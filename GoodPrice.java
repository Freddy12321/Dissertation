package dissertation6;

public class GoodPrice {
	//basic object for holding information about the prices of certain goods
	//integer for price and ID
	int price;
	int id;
	//description used to identify land and labour
	String description;
	//constructor used for most randomised goods
	public GoodPrice(int id, int p) {
		this.id=id;
		price=p;
		description=null;
	}
	//constructor used for generating land and labour
	public GoodPrice(String des, int id, int p) {
		this.id=id;
		price=p;
		description=des;
	}
	//get method for price value
	public int getPrice() {
		return price;
	}
	//set method
	public void setPrice(int p) {
		price=p;
	}
	//increment by one method
	public void increasePrice() {
		price=price+1;
	}
	//decrement by one method
	public void decreasePrice() {
		price=price-1;
	}
	//method to return ID
	public int getID() {
		return id;
	}
	public String getDes() {
		return description;
	}
}
