package dissertation6;

import java.util.ArrayList;

public class GoodPrices {
	//class used to hold all of the GoodPrice objects, also determines which 
	//goods are raw, refined, life, and luxury
	//number of raw/refined goods
	int rawGoodNum, refinedGoodNum;
	//array of all goods
	ArrayList<GoodPrice> goods= new ArrayList<GoodPrice>();
	//array of life goods
	ArrayList<GoodPrice> lifeGoods= new ArrayList<GoodPrice>();
	//array of luxury goods
	ArrayList<GoodPrice> luxuryGoods= new ArrayList<GoodPrice>();
	//Class constructor takes in Integer rawGoodNum, Integer refinedGoodNum,
	//Integer lifeGoodNum, Integer luxuryGoodNum
	//sets up arrays too
	public GoodPrices(int rawGN, int reGN, int lifeNum, int luxNum) {
		rawGoodNum=rawGN;
		refinedGoodNum=reGN;
		double rawRatio=(double)rawGN/(rawGN+reGN);
		double refRatio=(double)reGN/(rawGN+reGN);
		//raw goods added, based off of the ratio of rawGoods to total goods
		//there is a chance that the good will be added to life or luxury goods
		for(int i=0; i<rawGN;i++) {
			goods.add(new GoodPrice(goods.size(),100));
			if(Math.random()<rawRatio) {
				System.out.println("Life Added Random");
				if(lifeGoods.size()<lifeNum*rawRatio) {
					lifeGoods.add(goods.get(goods.size()-1));
				}
			}
			else if(lifeNum*rawRatio-lifeGoods.size()>rawGN-goods.size()) {
				System.out.println("Life Added NonRandom");
				lifeGoods.add(goods.get(goods.size()-1));
			}
			if(Math.random()<rawRatio) {
				System.out.println("Luxury Added Random");
				if(luxuryGoods.size()<luxNum*rawRatio) {
					luxuryGoods.add(goods.get(goods.size()-1));
				}
			}
			else if(luxNum*rawRatio-luxuryGoods.size()>rawGN-goods.size()) {
				System.out.println("Luxury Added NonRandom");
				luxuryGoods.add(goods.get(goods.size()-1));
			}
		}
		//refined goods added, based off of the ratio of refinedGoods to total goods
		//there is a chance that the good will be added to life or luxury goods
		for(int i=0; i<reGN; i++) {
			goods.add(new GoodPrice(goods.size(),100));
			if(Math.random()<refRatio) {
				System.out.println("Life Added Random");
				if(lifeGoods.size()<lifeNum) {
					lifeGoods.add(goods.get(goods.size()-1));
				}
			}
			else if(lifeNum-(lifeGoods.size())>rawGN+reGN-goods.size()) {
				System.out.println("Life Added NonRandom");
				if(lifeGoods.size()<lifeNum) {
					lifeGoods.add(goods.get(goods.size()-1));
				}
			}
			if(Math.random()<refRatio) {
				System.out.println("Luxury Added Random");
				if(luxuryGoods.size()<luxNum) {
					luxuryGoods.add(goods.get(goods.size()-1));
				}
			}
			else if(luxNum-(luxuryGoods.size())>rawGN+reGN-goods.size()) {
				System.out.println("Luxury Added NonRandom");
				if(luxuryGoods.size()<luxNum) {
					luxuryGoods.add(goods.get(goods.size()-1));
				}
			}
		}
		//labor and land added, they cannot be life or luxury goods
		goods.add(new GoodPrice("labor", goods.size(), 100));
		goods.add(new GoodPrice("land", goods.size(), 100));
	}
	//returns requested GoodPrice object, Integer index
	public GoodPrice getGood(int num) {
		return goods.get(num);
	}
	//returns total goods without land and labor
	public int getRawRefinedNum() {
		return rawGoodNum+refinedGoodNum;
	}
	//returns the number of all goods
	public int getAllGoodsNum() {
		return goods.size();
	}
	//returns the number of rawGoods
	public int getRawGoodNum() {
		return rawGoodNum;
	}
	//returns the number of refinedGoods
	public int getRefinedGoodNum() {
		return refinedGoodNum;
	}
	//returns the ArrayList of GoodPrice objects
	public ArrayList<GoodPrice> getGoods(){
		return goods;
	}
	//returns the price of land
	public int getLandPrice() {
		return goods.get(goods.size()-1).getPrice();
	}
	//returns the price of labour
	public int getLaborPrice() {
		return goods.get(goods.size()-2).getPrice();
	}
	//returns the index of land
	public int getLandIndex() {
		return goods.size()-1;
	}
	//returns the index of labour
	public int getLaborIndex() {
		return goods.size()-2;
	}
	//returns the price of the specified GoodPrice object, Integer index
	public int getGoodPrice(int good) {
		return goods.get(good).getPrice();
	}
	//increases the price of the specified GooPrice object, Integer index
	public void increaseGoodPrice(int good) {
		goods.get(good).increasePrice();
	}
	//decreases the price of the specified GooPrice object, Integer index
	public void decreaseGoodPrice(int good) {
		goods.get(good).decreasePrice();
	}
	//checks to see if the GoodPrice is in lifeGoods, Integer index, returns boolean
	public boolean lifeGoodsContainsType(int type) {
		for(int i=0; i<lifeGoods.size()-2; i++) {
			if(lifeGoods.get(i).getID()==type) {
				return true;
			}
		}
		return false;
	}
	//checks to see if the GoodPrice is in luxuryGoods, Integer index, returns boolean
	public boolean luxuryGoodsContainsType(int type) {
		for(int i=0; i<luxuryGoods.size()-2; i++) {
			if(luxuryGoods.get(i).getID()==type) {
				return true;
			}
		}
		return false;
	}
	//returns the ArrayList lifeGoods
	public ArrayList<GoodPrice> getLifeGoods(){
		return lifeGoods;
	}
	//returns the ArrayList luxuryGoods
	public ArrayList<GoodPrice> getLuxuryGoods(){
		return luxuryGoods;
	}
}
