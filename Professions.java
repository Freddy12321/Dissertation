package dissertation6;

import java.util.ArrayList;

public class Professions {
	//The class Professions is used to hold and manage all of the Profession objects
	//Reference to GoodPrices
	GoodPrices goods;
	//ArrayList of Profession objects
	ArrayList<Profession> professions= new ArrayList<Profession>();
	//Class constructor, takes GoodPrices, Integer RawProfession output,
	//Integer RefinedProfession input, Integer RefinedProfession output
	public Professions(GoodPrices g, int rawOut, int refIn, int refOut) {
		goods=g;
		int temp=goods.getRawGoodNum();
		//creation of a RawProfession for every raw good
		for(int i=0; i<temp;i++) {
			setUpRawProfessions(professions.size(),rawOut);
		}
		//creation of a RefinedProfession for every refined good
		temp=goods.getRefinedGoodNum();
		for(int i=0; i<temp;i++) {
			setUpRefinedProfessions(professions.size(),refIn,refOut);
		}
		//creation of employee and employer professions, they are identical
		professions.add(new EmployProfession(professions.size()));
		professions.add(new EmployProfession(professions.size()));
	}
	//method used to build RefinedProfessions, takes in Int index, Int input, Int output
	private void setUpRefinedProfessions(int num, int refIn, int refOut) {
		//Input good is randomly selected from already created professions
		int inputGoodNum=(int)(Math.random()*num);
		int inputGoodAmount=refIn;
		//output good is equal to index
		int outputGoodNum=num;
		int outputGoodAmount=refOut;
		//capital good selected using this method
		int capitalGoodNum=getPotentialCapitalGoods(num);
		int capitalGoodAmount=10;
		professions.add(new RefinedProfession(num,
				new GoodAmount(inputGoodNum, inputGoodAmount),
				new GoodAmount(outputGoodNum, outputGoodAmount),
				new GoodAmount(capitalGoodNum,capitalGoodAmount)));
	}
	//method used to set up RawProfessions
	private void setUpRawProfessions(int num, int rawOut) {
		//output good equal to index
		int outputGoodNum=num;
		int outputGoodAmount=rawOut;
		//capital good selected by this method
		int capitalGoodNum=getPotentialCapitalGoods(num);
		int capitalGoodAmount=10;
		professions.add(new RawProfession(num,
				new GoodAmount(outputGoodNum, outputGoodAmount),
				new GoodAmount(capitalGoodNum,capitalGoodAmount)));		
	}
	//used to select a capital good, finds a good that is not the same 
	//as the passed good, takes Integer
	private int getPotentialCapitalGoods(int num) {
		while(true) {
			int random=(int)(Math.random()*goods.getRawRefinedNum());
			if(num!=random) {
				return random;
			}
		}
	}
	//takes an integer as input and returns the corresponding Profession
	public Profession getProfession(int num) {
		return professions.get(num);
	}
	//returns the size of professions
	public int getProfessionsSize() {
		return professions.size();
	}
	//returns the size of professions without Employee and Employer
	public int getProfessionsSizeWOEmployee() {
		return professions.size()-2;
	}
	//returns the index of employee
	public Profession getEmployee() {
		return professions.get(professions.size()-1);
	}
	//returns the index of employer
	public Profession getEmployer() {
		return professions.get(professions.size()-2);
	}
	//returns an Integer array of inputs form all professions, if input is null then it is set to 0
	public int[] getInputArray() {
		int[] input=new int[professions.size()];
		for(int i=0; i<professions.size()-2;i++) {
			if(professions.get(i).getClass().equals(RawProfession.class)) {
				input[i]=goods.getAllGoodsNum();
			}
			if(professions.get(i).getClass().equals(RefinedProfession.class)) {
				input[i]=((RefinedProfession) professions.get(i)).getInputType();
			}
		}
		input[input.length-2]=goods.getAllGoodsNum();
		input[input.length-1]=goods.getAllGoodsNum();
		return input;
	}
	//returns Integer array of all professions output, if output is null then it is set to 0
	public int[] getOutputArray() {
		int[] output=new int[professions.size()];
		for(int i=0; i<professions.size()-2;i++) {
			output[i]=professions.get(i).getOutputType();
		}
		output[output.length-2]=goods.getAllGoodsNum();
		output[output.length-1]=goods.getAllGoodsNum();
		return output;
	}
	//returns Integer array of all professions capital, if capital is null then it is set to 0
	public int[] getCapitalArray() {
		int[] capital=new int[professions.size()];
		for(int i=0; i<professions.size()-2;i++) {
			capital[i]=professions.get(i).getCapitalType();
		}
		capital[capital.length-2]=goods.getAllGoodsNum();
		capital[capital.length-1]=goods.getAllGoodsNum();
		return capital;
	}
}
