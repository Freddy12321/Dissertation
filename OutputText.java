package dissertation6;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class OutputText {
	//arrays for storing data from every tick
	int[][] happiness;
	int[][] money;
	int[][] professions;
	int[][] land;
	int[][] capital;
	int[][] production;
	boolean[][] lifeNeedsMet;
	boolean[][] productionNeedsMet;
	//output object
	PrintStream writer;
	//Class constructor, takes in the amount of ticks that it needs to record for and the file to output data to
	public OutputText(int ticks, String fileName) {
		happiness= new int[ticks][];
		money= new int[ticks][];
		professions= new int[ticks][];
		land= new int[ticks][];
		capital= new int[ticks][];
		production= new int[ticks][];
		lifeNeedsMet= new boolean[ticks][];
		productionNeedsMet= new boolean[ticks][];
		if(fileName!=null) {
			try {
				writer= new PrintStream(new FileOutputStream(fileName+".txt"));
				writer.print("");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//update method for happiness
	public void updateHappiness(int[] update, int tick) {
		happiness[tick]=update;
	}
	//update method for money
	public void updateMoney(int[] update, int tick) {
		money[tick]=update;
	}
	//update method for professions
	public void updateProfessions(int[] update, int tick) {
		professions[tick]=update;
	}
	//update method for land
	public void updateLand(int[] update, int tick) {
		land[tick]=update;
	}
	//update method for capital
	public void updateCapital(int[] update, int tick) {
		capital[tick]=update;
	}
	//update method for lifeNeedsMet
	public void updateLife(boolean[] update, int tick) {
		lifeNeedsMet[tick]=update;
	}
	//update method for productionNeedsMet
	public void updateProduction(boolean[] update, int tick) {
		productionNeedsMet[tick]=update;
	}
	//update method for prodction
	public void updateProduction(int[] update, int tick) {
		production[tick]=update;
	}
	//method for outputting text to file, calls specialised method to printing text
	public void outputToFile() {
		printHappiness();
		printMoney();
		printLand();
		printCapital();
		printLife();
		printPordNeeds();
		printProduction();
		printProfessions();
		printHappinessStats();
		printMoneyStats();
		printLandStats();
		printCapitalStats();
		printLifeStats();
		printProdNeedsStats();
	}
	//method for printing the statistics of productionNeedsMet
	private void printProdNeedsStats() {
		int met,not;
		String out="Production Needs Met:\n";
		String addOut=String.format("%14s", "Tick Num:");
		out=out.concat(addOut);
		addOut=String.format("%14s", "Life Needs Met:");
		out=out.concat(addOut);
		addOut=String.format("%14s", "Not Met:");
		out=out.concat(addOut+"\n");
		writer.append(out);
		out="";
		for(int i=0; i<productionNeedsMet.length; i++) {
			met=0;
			not=0;
			for(int j=0; j<productionNeedsMet[i].length; j++) {
				if(productionNeedsMet[i][j]) {
					met++;
				}
				else {
					not++;
				}
			}
			addOut=String.format("%14s", i);
			out=out.concat(addOut);
			addOut=String.format("%14s", met);
			out=out.concat(addOut);
			addOut=String.format("%14s", not);
			out=out.concat(addOut+"\n");
			writer.append(out);
			out="";
		}
	}
	//method for printing the statistics of lifeNeedsMet
	private void printLifeStats() {
		int met,not;
		String out="Life Needs Met:\n";
		String addOut=String.format("%14s", "Tick Num:");
		out=out.concat(addOut);
		addOut=String.format("%14s", "Life Needs Met:");
		out=out.concat(addOut);
		addOut=String.format("%14s", "Not Met:");
		out=out.concat(addOut);
		writer.append(out+"\n");
		out="";
		for(int i=0; i<lifeNeedsMet.length; i++) {
			met=0;
			not=0;
			for(int j=0; j<lifeNeedsMet[i].length; j++) {
				if(lifeNeedsMet[i][j]) {
					met++;
				}
				else {
					not++;
				}
			}
			addOut=String.format("%14s", i);
			out=out.concat(addOut);
			addOut=String.format("%14s", met);
			out=out.concat(addOut);
			addOut=String.format("%14s", not);
			out=out.concat(addOut);
			writer.append(out+"\n");
			out="";
		}
	}
	//method for printing the professions[][]
	private void printProfessions() {
		String out="Professions:\n";
		String addOut="";
		for(int i=0; i<professions.length; i++) {
			addOut=String.format("%8s", i+":");
			out=out.concat(addOut);
			for(int j=0; j<professions[i].length; j++) {
				addOut=String.format("%8s", professions[i][j]);
				out=out.concat(addOut);
			}
			out=out.concat("\n");
		}
		writer.append(out);
	}
	//method for printing the production[][]
	private void printProduction() {
		String out="Production:\n";
		String addOut="";
		for(int i=0; i<production.length; i++) {
			addOut=String.format("%8s", i+":");
			out=out.concat(addOut);
			for(int j=0; j<production[i].length; j++) {
				addOut=String.format("%8s", production[i][j]);
				out=out.concat(addOut);
			}
			out=out.concat("\n");
		}
		writer.append(out);
	}
	//method for printing the productionNeedsMet[][]
	private void printPordNeeds() {
		String out="Production Needs:\n";
		String addOut="";
		for(int i=0; i<productionNeedsMet.length; i++) {
			addOut=String.format("%8s", i+":");
			out=out.concat(addOut);
			for(int j=0; j<productionNeedsMet[i].length; j++) {
				addOut=String.format("%8s", productionNeedsMet[i][j]);
				out=out.concat(addOut);
			}
			out=out.concat("\n");
		}
		writer.append(out);
	}
	//method for printing the lifeNeedsMet[][]
	private void printLife() {
		String out="Life Needs:\n";
		String addOut="";
		for(int i=0; i<lifeNeedsMet.length; i++) {
			addOut=String.format("%8s", i+":");
			out=out.concat(addOut);
			for(int j=0; j<lifeNeedsMet[i].length; j++) {
				addOut=String.format("%8s", lifeNeedsMet[i][j]);
				out=out.concat(addOut);
			}
			out=out.concat("\n");
		}
		writer.append(out);
	}
	//method for printing the capital[][]
	private void printCapital() {
		String out="Capital:\n";
		String addOut="";
		for(int i=0; i<capital.length; i++) {
			addOut=String.format("%8s", i+":");
			out=out.concat(addOut);
			for(int j=0; j<capital[i].length; j++) {
				addOut=String.format("%8s", capital[i][j]);
				out=out.concat(addOut);
			}
			out=out.concat("\n");
		}
		writer.append(out);
	}
	//method for printing the land[][]
	private void printLand() {
		String out="Land:\n";
		String addOut="";
		for(int i=0; i<land.length; i++) {
			addOut=String.format("%8s", i+":");
			out=out.concat(addOut);
			for(int j=0; j<land[i].length; j++) {
				addOut=String.format("%8s", land[i][j]);
				out=out.concat(addOut);
			}
			out=out.concat("\n");
		}
		writer.append(out);
	}
	//method for printing the money[][]
	private void printMoney() {
		String out="Money:\n";
		String addOut="";
		for(int i=0; i<money.length; i++) {
			addOut=String.format("%8s", i+":");
			out=out.concat(addOut);
			for(int j=0; j<money[i].length; j++) {
				addOut=String.format("%8s", money[i][j]);
				out=out.concat(addOut);
			}
			out=out.concat("\n");
		}
		writer.append(out);
	}
	//method for printing the happiness[][]
	private void printHappiness() {
		String out="Happiness:\n";
		String addOut="";
		for(int i=0; i<happiness.length; i++) {
			System.out.println(i);
			addOut=String.format("%8s", i+":");
			out=out.concat(addOut);
			for(int j=0; j<happiness[i].length; j++) {
				addOut=String.format("%8s", happiness[i][j]);
				out=out.concat(addOut);
			}
			out=out.concat("\n");
		}
		writer.append(out);
	}
	//method for printing the stats of happiness, it calculates the Mean, Median, Mode, Mode Count, Quartiles 1, 2, 3,and 4
	//this is done for every tick.
	private void printHappinessStats() {
		String out="Happiness Stats:\n";
		int [] sorted;
		int[][] modeNums;
		int mode,modeCount,total,modeIndex;
		double quart1,quart2,quart3,quart4,median,stdDev,mean;
		String addOut=String.format("%15s", "Tick Number:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mean:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Median:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mode:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mode Count:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Standard Deviation:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile One:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Two:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Three:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Four:");
		out=out.concat(addOut+"\n");
		writer.append(out);
		out="";
		for(int i=0;i<happiness.length;i++) {
			total=0;
			mean=0;
			median=0;
			quart1=0;
			quart2=0;
			quart3=0;
			quart4=0;
			modeNums= new int[happiness[i].length][2];
			modeIndex=0;
			sorted=mergeSortDown(happiness[i]);
			mode=sorted[0];
			modeCount=0;
			for(int j=0; j<sorted.length; j++) {
				total+=sorted[j];
				if(mode==sorted[j]) {
					modeCount++;
				}
				else {
					modeNums[modeIndex][0]=mode;
					modeNums[modeIndex][1]=modeCount;
					mode=sorted[j];
					modeCount=1;
					modeIndex++;
				}
			}
			mean=(double)total/sorted.length;
			if(sorted.length%2==1) {
				median=sorted[sorted.length];
			}
			else {
				median=(double)(sorted[sorted.length/2]+sorted[sorted.length/2-1])/2;
			}
			double totalVarience=0;
			for(int j=0; j<sorted.length; j++) {
				totalVarience+=Math.pow(sorted[j]-mean, 2);
			}
			stdDev=Math.sqrt(totalVarience/sorted.length);
			total=0;
			for(int j=0; j<sorted.length/4; j++) {
				total+=sorted[j];
			}
			quart1=total/sorted.length/4;
			total=0;
			for(int j=sorted.length/4; j<sorted.length/2; j++) {
				total+=sorted[j];
			}
			quart2=total/(sorted.length/2-sorted.length/4);
			total=0;
			for(int j=sorted.length/2; j<(double)sorted.length/4*3; j++) {
				total+=sorted[j];
			}
			quart3=total/(int)((double)sorted.length/4*3-sorted.length/2);
			total=0;
			for(int j=(int)((double)sorted.length/4*3); j<sorted.length; j++) {
				total+=sorted[j];
			}
			quart4=total/(int)(sorted.length-(double)sorted.length/4*3);
			mode=modeNums[0][0];
			modeCount=modeNums[0][1];
			for(int j=1; j<modeNums.length; j++) {
				if(modeCount<modeNums[j][1]) {
					mode=modeNums[j][0];
					modeCount=modeNums[j][1];
				}
			}
			addOut=String.format("%20s", mean);
			out=out.concat(addOut);
			addOut=String.format("%20s", median);
			out=out.concat(addOut);
			addOut=String.format("%20s", mode);
			out=out.concat(addOut);
			addOut=String.format("%20s", modeCount);
			out=out.concat(addOut);
			addOut=String.format("%20s", stdDev);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart1);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart2);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart3);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart4);
			out=out.concat(addOut+"\n");
			writer.append(out);
			out="";
		}
	}
	//method for printing the stats of money, it calculates the Mean, Median, Mode, Mode Count, Quartiles 1, 2, 3,and 4
	//this is done for every tick.
	private void printMoneyStats() {
		String out="Money Stats:\n";
		int [] sorted;
		int[][] modeNums;
		int mode,modeCount,total,modeIndex;
		double quart1,quart2,quart3,quart4,median,stdDev,mean;
		String addOut=String.format("%15s", "Tick Number:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mean:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Median:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mode:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mode Count:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Standard Deviation:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile One:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Two:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Three:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Four:");
		out=out.concat(addOut+"\n");
		writer.append(out);
		out="";
		for(int i=0;i<money.length;i++) {
			total=0;
			mean=0;
			median=0;
			quart1=0;
			quart2=0;
			quart3=0;
			quart4=0;
			modeNums= new int[money[i].length][2];
			modeIndex=0;
			sorted=mergeSortDown(money[i]);
			mode=sorted[0];
			modeCount=0;
			for(int j=0; j<sorted.length; j++) {
				total+=sorted[j];
				if(mode==sorted[j]) {
					modeCount++;
				}
				else {
					modeNums[modeIndex][0]=mode;
					modeNums[modeIndex][1]=modeCount;
					mode=sorted[j];
					modeCount=1;
					modeIndex++;
				}
			}
			mean=(double)total/sorted.length;
			if(sorted.length%2==1) {
				median=sorted[sorted.length];
			}
			else {
				median=(double)(sorted[sorted.length/2]+sorted[sorted.length/2-1])/2;
			}
			double totalVarience=0;
			for(int j=0; j<sorted.length; j++) {
				totalVarience+=Math.pow(sorted[j]-mean, 2);
			}
			stdDev=Math.sqrt(totalVarience/sorted.length);
			total=0;
			for(int j=0; j<sorted.length/4; j++) {
				total+=sorted[j];
			}
			quart1=total/sorted.length/4;
			total=0;
			for(int j=sorted.length/4; j<sorted.length/2; j++) {
				total+=sorted[j];
			}
			quart2=total/(sorted.length/2-sorted.length/4);
			total=0;
			for(int j=sorted.length/2; j<(double)sorted.length/4*3; j++) {
				total+=sorted[j];
			}
			quart3=total/(int)((double)sorted.length/4*3-sorted.length/2);
			total=0;
			for(int j=(int)((double)sorted.length/4*3); j<sorted.length; j++) {
				total+=sorted[j];
			}
			quart4=total/(int)(sorted.length-(double)sorted.length/4*3);
			mode=modeNums[0][0];
			modeCount=modeNums[0][1];
			for(int j=1; j<modeNums.length; j++) {
				if(modeCount<modeNums[j][1]) {
					mode=modeNums[j][0];
					modeCount=modeNums[j][1];
				}
			}
			addOut=String.format("%20s", mean);
			out=out.concat(addOut);
			addOut=String.format("%20s", median);
			out=out.concat(addOut);
			addOut=String.format("%20s", mode);
			out=out.concat(addOut);
			addOut=String.format("%20s", modeCount);
			out=out.concat(addOut);
			addOut=String.format("%20s", stdDev);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart1);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart2);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart3);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart4);
			out=out.concat(addOut+"\n");
			writer.append(out);
			out="";
		}
	}
	//method for printing the stats of land, it calculates the Mean, Median, Mode, Mode Count, Quartiles 1, 2, 3,and 4
	//this is done for every tick.
	private void printLandStats() {
		String out="Land Stats:\n";
		int [] sorted;
		int[][] modeNums;
		int mode,modeCount,total,modeIndex;
		double quart1,quart2,quart3,quart4,median,stdDev,mean;
		String addOut=String.format("%15s", "Tick Number:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mean:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Median:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mode:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mode Count:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Standard Deviation:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile One:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Two:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Three:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Four:");
		out=out.concat(addOut+"\n");
		writer.append(out);
		out="";
		for(int i=0;i<land.length;i++) {
			total=0;
			mean=0;
			median=0;
			quart1=0;
			quart2=0;
			quart3=0;
			quart4=0;
			modeNums= new int[land[i].length][2];
			modeIndex=0;
			sorted=mergeSortDown(land[i]);
			mode=sorted[0];
			modeCount=0;
			for(int j=0; j<sorted.length; j++) {
				total+=sorted[j];
				if(mode==sorted[j]) {
					modeCount++;
				}
				else {
					modeNums[modeIndex][0]=mode;
					modeNums[modeIndex][1]=modeCount;
					mode=sorted[j];
					modeCount=1;
					modeIndex++;
				}
			}
			mean=(double)total/sorted.length;
			if(sorted.length%2==1) {
				median=sorted[sorted.length];
			}
			else {
				median=(double)(sorted[sorted.length/2]+sorted[sorted.length/2-1])/2;
			}
			double totalVarience=0;
			for(int j=0; j<sorted.length; j++) {
				totalVarience+=Math.pow(sorted[j]-mean, 2);
			}
			stdDev=Math.sqrt(totalVarience/sorted.length);
			total=0;
			for(int j=0; j<sorted.length/4; j++) {
				total+=sorted[j];
			}
			quart1=total/sorted.length/4;
			total=0;
			for(int j=sorted.length/4; j<sorted.length/2; j++) {
				total+=sorted[j];
			}
			quart2=total/(sorted.length/2-sorted.length/4);
			total=0;
			for(int j=sorted.length/2; j<(double)sorted.length/4*3; j++) {
				total+=sorted[j];
			}
			quart3=total/(int)((double)sorted.length/4*3-sorted.length/2);
			total=0;
			for(int j=(int)((double)sorted.length/4*3); j<sorted.length; j++) {
				total+=sorted[j];
			}
			quart4=total/(int)(sorted.length-(double)sorted.length/4*3);
			mode=modeNums[0][0];
			modeCount=modeNums[0][1];
			for(int j=1; j<modeNums.length; j++) {
				if(modeCount<modeNums[j][1]) {
					mode=modeNums[j][0];
					modeCount=modeNums[j][1];
				}
			}
			addOut=String.format("%20s", mean);
			out=out.concat(addOut);
			addOut=String.format("%20s", median);
			out=out.concat(addOut);
			addOut=String.format("%20s", mode);
			out=out.concat(addOut);
			addOut=String.format("%20s", modeCount);
			out=out.concat(addOut);
			addOut=String.format("%20s", stdDev);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart1);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart2);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart3);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart4);
			out=out.concat(addOut+"\n");
			writer.append(out);
			out="";
		}
	}
	//method for printing the stats of capital, it calculates the Mean, Median, Mode, Mode Count, Quartiles 1, 2, 3,and 4
	//this is done for every tick.
	private void printCapitalStats() {
		String out="Capital Stats:\n";
		int [] sorted;
		int[][] modeNums;
		int mode,modeCount,total,modeIndex;
		double quart1,quart2,quart3,quart4,median,stdDev,mean;
		String addOut=String.format("%15s", "Tick Number:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mean:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Median:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mode:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Mode Count:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Standard Deviation:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile One:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Two:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Three:");
		out=out.concat(addOut);
		addOut=String.format("%20s", "Quartile Four:");
		out=out.concat(addOut+"\n");
		writer.append(out);
		out="";
		for(int i=0;i<capital.length;i++) {
			total=0;
			mean=0;
			median=0;
			quart1=0;
			quart2=0;
			quart3=0;
			quart4=0;
			modeNums= new int[capital[i].length][2];
			modeIndex=0;
			sorted=mergeSortDown(capital[i]);
			mode=sorted[0];
			modeCount=0;
			for(int j=0; j<sorted.length; j++) {
				total+=sorted[j];
				if(mode==sorted[j]) {
					modeCount++;
				}
				else {
					modeNums[modeIndex][0]=mode;
					modeNums[modeIndex][1]=modeCount;
					mode=sorted[j];
					modeCount=1;
					modeIndex++;
				}
			}
			mean=(double)total/sorted.length;
			if(sorted.length%2==1) {
				median=sorted[sorted.length];
			}
			else {
				median=(double)(sorted[sorted.length/2]+sorted[sorted.length/2-1])/2;
			}
			double totalVarience=0;
			for(int j=0; j<sorted.length; j++) {
				totalVarience+=Math.pow(sorted[j]-mean, 2);
			}
			stdDev=Math.sqrt(totalVarience/sorted.length);
			total=0;
			for(int j=0; j<sorted.length/4; j++) {
				total+=sorted[j];
			}
			quart1=total/sorted.length/4;
			total=0;
			for(int j=sorted.length/4; j<sorted.length/2; j++) {
				total+=sorted[j];
			}
			quart2=total/(sorted.length/2-sorted.length/4);
			total=0;
			for(int j=sorted.length/2; j<(double)sorted.length/4*3; j++) {
				total+=sorted[j];
			}
			quart3=total/(int)((double)sorted.length/4*3-sorted.length/2);
			total=0;
			for(int j=(int)((double)sorted.length/4*3); j<sorted.length; j++) {
				total+=sorted[j];
			}
			quart4=total/(int)(sorted.length-(double)sorted.length/4*3);
			mode=modeNums[0][0];
			modeCount=modeNums[0][1];
			for(int j=1; j<modeNums.length; j++) {
				if(modeCount<modeNums[j][1]) {
					mode=modeNums[j][0];
					modeCount=modeNums[j][1];
				}
			}
			addOut=String.format("%20s", mean);
			out=out.concat(addOut);
			addOut=String.format("%20s", median);
			out=out.concat(addOut);
			addOut=String.format("%20s", mode);
			out=out.concat(addOut);
			addOut=String.format("%20s", modeCount);
			out=out.concat(addOut);
			addOut=String.format("%20s", stdDev);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart1);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart2);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart3);
			out=out.concat(addOut);
			addOut=String.format("%20s", quart4);
			out=out.concat(addOut+"\n");
			writer.append(out);
			out="";
		}
	}
	//this method is used to sort the integers in an array from least to most
	private int[] mergeSortDown(int[] toSort) {
		if(toSort.length>1) {
			int[] firstHalf = new int[toSort.length/2];
			int[] secondHalf;
			if(toSort.length%2==1) {
				secondHalf=new int[toSort.length/2+1];
			}
			else {
				secondHalf=new int[toSort.length/2];
			}
			int[] sorted = new int[toSort.length];
			for(int i=0; i<firstHalf.length;i++) {
				firstHalf[i]=toSort[i];
			}
			for(int i=firstHalf.length; i<toSort.length;i++) {
				secondHalf[i-firstHalf.length]=toSort[i];
			}
			if(firstHalf.length>1) {
				firstHalf=mergeSortDown(firstHalf);
			}
			if(secondHalf.length>1) {
				secondHalf=mergeSortDown(secondHalf);
			}
			int j=0;
			int k=0;
			for(int i=0; i<toSort.length;i++) {
				if(j<firstHalf.length&&k<secondHalf.length) {
					if(firstHalf[j]<=secondHalf[k]) {
						sorted[i]=firstHalf[j];
						j++;
					}
					else if(firstHalf[j]>secondHalf[k]){
						sorted[i]=secondHalf[k];
						k++;
					}
				}
				else if(j==firstHalf.length){
					sorted[i]=secondHalf[k];
					k++;
				}
				else if(k==secondHalf.length) {
					sorted[i]=firstHalf[j];
					j++;
				}
			}
			return sorted;
		}
		return toSort;
	}
}
