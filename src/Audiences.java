import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.field.network.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Audiences extends SimState{

	private static final long serialVersionUID = 1;
	
	public DoubleGrid2D trails;
	public SparseGrid2D humans;
	public IntGrid2D exhibition;
	public DoubleGrid2D toHome;
	public DoubleGrid2D toStuff;
	public IntGrid2D sites;
	
	public int numAudiences = 300;
	public int numStuff = 12;
	
	public int gridWidth = 300;
	public int gridHeight = 310;
		
	
	public int doorLength = 10;
	public int HOME = 2;
	public int STUFF = 10;
	public int WALL = 1;
	public double AUDIENCE_RANDOMIZE = 0.5;
	public double INTEREST_CREATING = 0.5;

	
	public int centerWidth = gridWidth / 2;
	public int centerHeight = (gridHeight - doorLength) / 2;

	
	public Bag Zone1;
	public Bag Zone2;
	public Bag Zone3;
	public Bag Zone4;
	public Bag Zone5;
	public Bag Zone6;
	public Bag Zone7;
	public Bag Zone8;
	public Bag Zone9;
	public Bag Zone10;
	public Bag Zone11;
	public Bag Zone12;
	
	
	public int getNumAudiences() { return numAudiences; }
	public void setNumAudiences(int val) { numAudiences = val;}
	
	public int getNumStuff() { return numStuff; }
	public void setNumStuff(int val) { numStuff = val;}
	
	public Double getAUDIENCE_RANDOMIZE() { return AUDIENCE_RANDOMIZE; }
	public void setAUDIENCE_RANDOMIZE(Double val){	AUDIENCE_RANDOMIZE = val; }
	
	public Double getINTEREST_CREATING() { return INTEREST_CREATING; }
	public void setINTEREST_CREATING(Double val){ INTEREST_CREATING = val; }
	
	public int homePosX;
	public int homePosY;
	public Int2D[] home;
	public Bag stuffLoc;
	
	static BufferedWriter zone1;
	static BufferedWriter zone2;
	static BufferedWriter zone3;
	static BufferedWriter zone4;
	static BufferedWriter zone5;
	static BufferedWriter zone6;
	static BufferedWriter zone7;
	static BufferedWriter zone8;
	static BufferedWriter zone9;
	static BufferedWriter zone10;
	static BufferedWriter zone11;
	static BufferedWriter zone12;
	static BufferedWriter zoneTotal;
	static BufferedWriter zonePopular;
	
	static long timeHour = 3600;
	static long time3Hour = 21600;
	static long time15Min = 900;
	static long timeStep = 32400; // 9H 9-18 'o' clock
	static long decTime;
	static int addPeople = 100; 
	static double ADDPERCENT;
	
	static double Activity[] = { 0.85, 0.75, 0.65, 0.50, 0.65, 0.75, 0.90, 0.10, 0.05, 0.01, 0.01, 0.01};
	static int Count[] = { 0, 0,  0, 0, 0, 0, 0, 0, 0, 0};
	static int peopleDensity[] = { 0, 0,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	static ArrayList<ArrayList<Integer>> raw = new ArrayList<ArrayList<Integer>>();
	static ArrayList<ArrayList<Integer>> zoneDensity = new ArrayList<ArrayList<Integer>>();	
	
	
	
	
	public Int2D makeMap() // make walls and exhibition...
	{
		
		sites = new IntGrid2D(gridWidth, gridHeight, 0);
		toHome = new DoubleGrid2D(gridWidth, gridHeight, 0);
		toStuff = new DoubleGrid2D(gridWidth, gridHeight, 0);
		exhibition = new IntGrid2D(gridWidth, gridHeight, 0);
		stuffLoc = new Bag();
		
		
		for(int i=0; i < gridWidth; i++){
			exhibition.field[i][0] = WALL;
			exhibition.field[gridWidth-1][i] = WALL;
		}
		for(int i=0; i < gridHeight; i++){
			exhibition.field[0][i] = WALL;
		}
		
		home = new Int2D[1];
		for(int i=0; i< 1; i ++)
		{
			homePosX = gridWidth - 75;
			homePosY = gridHeight-1;
			home[i] = new Int2D(homePosX,homePosY);
			
			// make home (start or end point)
			sites.field[homePosX][homePosY] = HOME;

		}
		
		// make Stuff for watching		
		/*
		for(int i=0; i< numStuff; i++)
		{	
			int x = random.nextInt(gridWidth-1);
			int y = random.nextInt(gridHeight-doorLength-1);
			sites.field[x][y] = STUFF;
			
			Int2D loc = new Int2D(x,y);
			stuffLoc.add(loc);
			
			for(int dx=-1; dx< 2; dx++)
				for(int dy= -1; dy < 2; dy++)
				{
					int px = x + dx; int py = y + dy;
					
					if(px <= 0 || py <= 0 || px >= gridWidth || py >= gridHeight) continue;
					
					sites.field[px][py] = STUFF;
				}
		}
		*/
		int x = -25; 
		
		for(int i=0; i< numStuff; i++)
		{
			int y = (i % 2) * 150 + 75;
			if((i % 2) == 0 ) x += 50;
			
			sites.field[x][y] = STUFF;
			Int2D loc = new Int2D(x,y);
			stuffLoc.add(loc);
	
		}
		
		
		Int2D starting = new Int2D(centerWidth, centerHeight);
		
		return starting;
	}
	
	public static void rdDensity(int i )
	{
		for(int j=0; j< 12; j++)	peopleDensity[j] = zoneDensity.get(i).get(j);
		
	}
	
	public Audiences(long seed)
	{
		super(seed);
	}
	
	public Int2D createPos(int zoneNum)
	{
		int posY= ( zoneNum % 2 ) * 150 + 75;
		int posX = ( zoneNum / 2 ) * 50;
		
		Int2D res = new Int2D(posX, posY);

		return res;
	}
	
	public void start()
	{
		super.start();
		
		trails = new DoubleGrid2D(gridWidth, gridHeight);
		humans = new SparseGrid2D(gridWidth, gridHeight);
		
		Audience audience;
		
		
		Int2D start = makeMap();
		
		Zone1 = new Bag();
		Zone2 = new Bag();
		Zone3 = new Bag();
		Zone4 = new Bag();
		Zone5 = new Bag();
		Zone6 = new Bag();
		Zone7 = new Bag();
		Zone8 = new Bag();
		Zone9 = new Bag();
		Zone10 = new Bag();
		Zone11 = new Bag();
		Zone12 = new Bag();

		for (int z =0; z < 12; z++)
		{
			try {
				zonePopular.write(Integer.toString(peopleDensity[z]) + '\t');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			zonePopular.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/****/
		
		int cantStart = random.nextInt(12);
		
		int createTotal =  (int) (Count[0] * 0.25);
		
		for ( int z=0; z < 12; z++ )
		{
			Int2D cZone = createPos(z);


			
			
			int newAgents = (int)(( peopleDensity[z] * createTotal) / 100);
			
			
			if(newAgents == 0 && z == cantStart){
				newAgents = 1;
				audience = new Audience(99999999);
				humans.setObjectLocation(audience, cZone);
				schedule.scheduleRepeating(audience);
				break;
			}
			
			
			
			
			for (int c = 0; c < newAgents; c++)
			{
				audience = new Audience(20);
				humans.setObjectLocation(audience, cZone);
				schedule.scheduleRepeating(audience);
			}
		}
		


	}
	
	public void add(long num)
	{
		Audience audience;


//		if( createTotal == 0) createTotal = 1;
		for ( int z=0; z < 12; z++ )
		{
			Int2D cZone = createPos(z);
			
			int newAgents = (int)(Math.ceil(( peopleDensity[z] * num) / 100));
			//System.out.print(newAgents + "\t");

			for (int c = 0; c < newAgents; c++)
			{
				audience = new Audience(20);
				humans.setObjectLocation(audience, cZone);
				schedule.scheduleRepeating(audience);
			}
		}
		//System.out.println();
		
	}
	
	public int ZoneCnt(long stepCnt) throws IOException
	{
		int total =0;

		if(stepCnt % 900 == 0)
		{
			
			total = Zone1.numObjs + Zone2.numObjs + Zone3.numObjs + Zone4.numObjs +
					Zone5.numObjs + Zone6.numObjs + Zone7.numObjs + Zone8.numObjs +
					Zone9.numObjs + Zone10.numObjs + Zone11.numObjs + Zone12.numObjs;

			int time = (int) (stepCnt / time15Min);
			
//			System.out.println("stepCnt - " + time + " Total : " + total);
			
			String n1 = Integer.toString(Zone1.numObjs);
			String n2 = Integer.toString(Zone2.numObjs);
			String n3 = Integer.toString(Zone3.numObjs);
			String n4 = Integer.toString(Zone4.numObjs);
			String n5 = Integer.toString(Zone5.numObjs);
			String n6 = Integer.toString(Zone6.numObjs);
			String n7 = Integer.toString(Zone7.numObjs);
			String n8 = Integer.toString(Zone8.numObjs);
			String n9 = Integer.toString(Zone9.numObjs);
			String n10 = Integer.toString(Zone10.numObjs);
			String n11 = Integer.toString(Zone11.numObjs);
			String n12 = Integer.toString(Zone12.numObjs);
			String nTotal = Integer.toString(total);

			zone1.write(n1);zone2.write(n2);zone3.write(n3);zone4.write(n4);
			zone5.write(n5);zone6.write(n6);zone7.write(n7);zone8.write(n8);
			zone9.write(n9);zone10.write(n10);zone11.write(n11);zone12.write(n12);
			zoneTotal.write(nTotal);
			
			zone1.newLine();zone2.newLine();zone3.newLine();zone4.newLine();
			zone5.newLine();zone6.newLine();zone7.newLine();zone8.newLine();
			zone9.newLine();zone10.newLine();zone11.newLine();zone12.newLine();
			zoneTotal.newLine();;
			
		}
		Zone1.clear(); Zone2.clear(); Zone3.clear(); Zone4.clear();
		Zone5.clear(); Zone6.clear(); Zone7.clear(); Zone8.clear();
		Zone9.clear(); Zone10.clear(); Zone11.clear(); Zone12.clear();
		
		
		
		return total;
	}
	
	public void incPercent(double inc)
	{
		if( inc ==0 )	ADDPERCENT += 0.000002;
		else ADDPERCENT = 0.5;
	}
	
	public void decPercent()
	{
		ADDPERCENT = 0.0001;
	}
	
	public void controlPercent(long step, long decTime)
	{
		if ( step >= decTime)
		{
			incPercent(0);
		}
		else
		{
			decPercent();
		}
	}
	
	public static void readRAWCSV(String fileName)
	{
		try{
			File csv = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(csv));
			String line ="";
			int row =0, i;
			
			while((line = br.readLine()) != null)
			{
				ArrayList<Integer> input = new ArrayList<Integer>();
				String token[] = line.split(",", -1);
				
				for(i =0 ;i < token.length ; i++)	input.add(Integer.parseInt(token[i]));
				raw.add(input);
			}
			br.close();
		}
		catch (FileNotFoundException e){	e.printStackTrace();}
		catch (IOException e){	e.printStackTrace();}
	}
	
	public static void readDenseCSV(String fileName)
	{
		try {
			File csv = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(csv));
			String line = "";
			int row = 0, i;
			
			while((line = br.readLine()) != null)
			{
				ArrayList<Integer> input = new ArrayList<Integer>();
				String token[] = line.split(",", -1);
				
				for(i =0; i < token.length; i++) input.add(Integer.parseInt(token[i]));
				zoneDensity.add(input);
			}
			br.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) throws IOException
	{

		readRAWCSV("rawData.csv");
		readDenseCSV("zoneDensity.csv");
		
		for(int i=0; i < raw.size(); i++)
		{
			rdDensity(i);
			Arrays.fill(Count, 0);
			for (int j=1; j < raw.get(i).size(); j++)
			{
				Count[j-1] = raw.get(i).get(j);
				//System.out.print(raw.get(i).get(j) + "\t");

			}
			System.out.println("==> " + raw.get(i).get(0) + "\n");
			
			for(int c =0; c < Count.length; c++)	System.out.print(Count[c] + "\t");
			System.out.println();
			try {
				String path = "C://Users//user//Desktop//R&S_coPaper//Simulation//" + raw.get(i).get(0) + "//"; 
				File file = new File(path);
				file.mkdir();
				
				zone1 = new BufferedWriter(new FileWriter(path + "zone1.txt"));
				zone2 = new BufferedWriter(new FileWriter(path + "zone2.txt"));
				zone3 = new BufferedWriter(new FileWriter(path + "zone3.txt"));
				zone4 = new BufferedWriter(new FileWriter(path + "zone4.txt"));
				zone5 = new BufferedWriter(new FileWriter(path + "zone5.txt"));
				zone6 = new BufferedWriter(new FileWriter(path + "zone6.txt"));
				zone7 = new BufferedWriter(new FileWriter(path + "zone7.txt"));
				zone8 = new BufferedWriter(new FileWriter(path + "zone8.txt"));
				zone9 = new BufferedWriter(new FileWriter(path + "zone9.txt"));
				zone10 = new BufferedWriter(new FileWriter(path + "zone10.txt"));
				zone11 = new BufferedWriter(new FileWriter(path + "zone11.txt"));
				zone12 = new BufferedWriter(new FileWriter(path + "zone12.txt"));
				zoneTotal = new BufferedWriter(new FileWriter(path + "zoneTotal.txt"));
				zonePopular = new BufferedWriter(new FileWriter(path + "zonePopular.txt"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
			}
			
			SimState state = new Audiences(System.currentTimeMillis());
			state.start();
			
	/*		
			decTime = state.random.nextLong(3600) + time3Hour;
	
	 		double INCREASE_PERCENT = 0.00001;
			ADDPERCENT = 0.00001;
	
			do {
	
				
				if ( !state.schedule.step(state)) break;
				
				((Audiences)state).ZoneCnt(state.schedule.getSteps()); // per 10 s
				
				if(state.random.nextBoolean(INCREASE_PERCENT))
				{
					((Audiences) state).add(state.random.nextInt(addPeople));
				}
				
				
				((Audiences) state).controlPercent( state.schedule.getSteps(), decTime);
				
				
				
			} while( state.schedule.getSteps() < timeStep );
	*/
			/** Refer to Changes in Seoul Metropolitan Area Activity **/
	/*		
	  	    double Activity[] = { 0.61, 0.75, 0.79, 0.74, 0.81, 0.85, 0.90, 0.95, 1, 0.98};
			
			do {
				
				if( !state.schedule.step(state)) break;
				long step = state.schedule.getSteps();
	
				((Audiences)state).ZoneCnt(step);
				if(state.random.nextBoolean( Activity[(int) (step/timeHour)] ))	((Audiences) state).add(1);
				
			} while( state.schedule.getSteps() < timeStep );
	
	*/
	
			/** edit Activity Table **/
			// Different Activity Percentage
			do {
				
				if( !state.schedule.step(state)) break;
				long step = state.schedule.getSteps() - 900;
	
				((Audiences)state).ZoneCnt(step);
			
				// Create Humans's Percentage
				// if(state.random.nextBoolean( Activity[(int) (step/timeHour)] ))	((Audiences) state).add(1);
	
				// Create Human's Counting
				
				if(step % time15Min == 0 && step < timeStep){	

					((Audiences) state).add( (int)Math.ceil(Count[(int)(step/timeHour)] * 0.25) );
					
				}
				
			} while( state.schedule.getSteps() < timeStep+900);

			
			
			
			state.finish();
			zone1.close();zone2.close();zone3.close();zone4.close();
			zone5.close();zone6.close();zone7.close();zone8.close();
			zone9.close();zone10.close();zone11.close();zone12.close();
			zoneTotal.close();
			state = null;
			}
	}
}
