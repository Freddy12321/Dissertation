package dissertation6;

import java.util.ArrayList;

public abstract class MarketOperator {
	//This abstract class is needed so that LedgerEntry can contain either
	//a Company or an Agent 
	//References to other objects
	Market market;
	GoodPrices goods;
	Tile tile;
	//CLass buy and sell ledger
	ArrayList<LedgerEntry> sellLedger= new ArrayList<LedgerEntry>();
	ArrayList<LedgerEntry> buyLedger= new ArrayList<LedgerEntry>();
	//class GoodAmount lists
	ArrayList<GoodAmount> currentGoods= new ArrayList<GoodAmount>();
	ArrayList<GoodAmount> outputGoods= new ArrayList<GoodAmount>();
	//class money
	int money;
	//constructor, takes Market, GoodPrices, Tile, integer money
	MarketOperator(Market market, GoodPrices goods, Tile t, int money){
		this.market=market;
		this.goods=goods;
		this.money=money;
		this.tile=t;
	}
	//method to search sellLedger
	public abstract int sellLedgerContainsType(int type);
	//method to search buyLedger
	public abstract int buyLedgerContainsType(int type);
	//method to remove entry form sellLedger
	public abstract void removeSellRequest(LedgerEntry entry);
	//method to remove entry form buyLedger
	public abstract void removeBuyRequest(LedgerEntry entry);
	//method to place buyRequest
	public abstract void placeBuyRequest(int type, int amount);
	//method to place sellRequest
	public abstract void placeSellRequest(int type, int amount);
	//method to check if buyReqeuest can be fulfilled
	public abstract boolean canFullfillBuyRequest(LedgerEntry buyRequest, LedgerEntry sellRequest);
	//method to fulfil buyReqeust
	public abstract void buyRequestFullfilled(LedgerEntry buyRequest, LedgerEntry sellRequest);
	//method to check if sellReqeuest can be fulfilled
	public abstract boolean canFullfillSellRequest(LedgerEntry sellRequest, LedgerEntry buyRequest);
	//method to fulfil sellReqeust
	public abstract void sellRequestFullfilled(LedgerEntry sellRequest, LedgerEntry buyRequest);
}
