package dissertation6;

public abstract class Profession {
	//Profession is an abstract superclass that holds information about every profession
	//Index of the current Profession
	int index;
	//GoodAmounts of the Profession, if one is not applicable it is set to null
	GoodAmount input, output, capital;
	//the RawProfession constructor
	public Profession(int ind, GoodAmount output, GoodAmount cap) {
		input=null;
		index=ind;
		capital=cap;
		this.output=output;
	}
	//the RefinedProfession constructor
	public Profession(int ind, GoodAmount input, GoodAmount output, GoodAmount cap) {
		this.index=ind;
		this.input=input;
		this.output=output;
		this.capital=cap;
	}
	//the EmployProfession constructor
	public Profession(int ind) {
		index=ind;
		input=null;
		capital=null;
		output=null;
	}
	//basic output methods, class has no set 
	//methods since values should not be changed
	public int getInputType() {
		return input.getType();
	}
	public int getInputAmount() {
		return input.getAmount();
	}
	public int getOutputType() {
		return output.getType();
	}
	public int getOutputAmount() {
		return output.getAmount();
	}
	public int getCapitalType() {
		return capital.getType();
	}
	public int getCapitalAmount() {
		return capital.getAmount();
	}
	public GoodAmount getCapital(){
		return capital;
	}
	public GoodAmount getOutput() {
		return output;
	}
	public GoodAmount getInput() {
		return input;
	}
	public int getIndex() {
		return index;
	}
}
