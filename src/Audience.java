import java.awt.Color;
import java.awt.Graphics2D;

import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import sim.field.network.*;
import sim.portrayal.DrawInfo2D;

public class Audience implements Steppable{

	public boolean randomize = false;
	public int xdir;
	public int ydir;
	public int interest;
	public Int2D last; // save last location
	public Bag interestSpace;
	public int centerW;
	public int centerH;
	public int gridHeight;
	
	public Audience(int interest)
	{
		this.interest = interest;
		this.interestSpace = new Bag();
	}
	
	public int getInterest()
	{
		return this.interest;
	}
	public void setInterest(int val)
	{
		this.interest = val;
	}
	
	public Int2D getLastPos()
	{
		return this.last;
	}
	public void setLastPos(Int2D val)
	{
		this.last= val;
	}	
	
	public Bag getInterestSpace()
	{
		return this.interestSpace;
	}

	
	boolean isZone1(Int2D pos)
	{
		if( pos.x <= 50 && pos.y <= 150 )
			return true;
		else return false;
	}
	boolean isZone2(Int2D pos)
	{
		if( pos.x <= 50 && (pos.y > 150 && pos.y <= 300) )
			return true;
		else return false;	
	}
	boolean isZone3(Int2D pos)
	{
		if( (pos.x <= 100 && pos.x > 50) && pos.y <= 150 )
			return true;
		else return false;	
	}
	boolean isZone4(Int2D pos)
	{
		if( (pos.x <= 100 && pos.x > 50) && (pos.y > 150 && pos.y <= 300))
			return true;
		else return false;	
	}
	boolean isZone5(Int2D pos)
	{
		if( (pos.x <= 150 && pos.x > 100) && pos.y <= 150 )
			return true;
		else return false;	
	}
	boolean isZone6(Int2D pos)
	{
		if( (pos.x <= 150 && pos.x > 100) && (pos.y > 150 && pos.y <= 300) )
			return true;
		else return false;	
	}	
	boolean isZone7(Int2D pos)
	{
		if( (pos.x <= 200 && pos.x > 150) && pos.y <= 150 )
			return true;
		else return false;	
	}
	boolean isZone8(Int2D pos)
	{
		if( (pos.x <= 200 && pos.x > 150) && (pos.y > 150 && pos.y <= 300) )
			return true;
		else return false;	
	}		
	boolean isZone9(Int2D pos)
	{
		if( (pos.x <= 250 && pos.x > 200) && pos.y <= 150 )
			return true;
		else return false;	
	}
	boolean isZone10(Int2D pos)
	{
		if( (pos.x <= 250 && pos.x > 200) && (pos.y > 150 && pos.y <= 300) )
			return true;
		else return false;	
	}		
	boolean isZone11(Int2D pos)
	{
		if( (pos.x <= 300 && pos.x > 250) && pos.y <= 150 )
			return true;
		else return false;	
	}
	boolean isZone12(Int2D pos)
	{
		if( (pos.x <= 300 && pos.x > 250) && (pos.y > 150 && pos.y <= 300) )
			return true;
		else return false;	
	}		
		
	public void step(SimState state)
	{
		Audiences audiences = (Audiences) state;

		Int2D location = audiences.humans.getObjectLocation(this);
		int newx =0;
		int newy =0;
		audiences.trails.field[location.x][location.y] = 1.0;
		
//		int homeIdx = state.random.nextInt(4);
		int homeIdx = 0;
		int homeX = audiences.home[homeIdx].getX(); int homeY = audiences.home[homeIdx].getY();;
		centerH = audiences.centerHeight;
		centerW = audiences.centerWidth;
		gridHeight = audiences.gridHeight - 10;
		
		if(this.interest > 0)
		{
	
			// make the interesting point to each agents * interesting points can be duplicated.
			if( this.interestSpace.numObjs <= interest)
			{
				int locIndex = state.random.nextInt(audiences.numStuff);
				this.interestSpace.add( audiences.stuffLoc.get(locIndex) );				
			}
			
			// GET THE DESTINATION POINT.
			int interestX =((Int2D)this.interestSpace.top()).x;
			int interestY =((Int2D)this.interestSpace.top()).y;
			
			// going to Stuff Position. greedy.
			Double length = 999999999.0;
			
			for(int dx = -1; dx < 2; dx++){
				for( int dy = -1; dy < 2; dy++)
				{
					int _x = dx+location.x;
					int _y = dy+location.y;
						
					// nowhere to go.
					if ( dx ==0 && dy ==0) continue;
					if ( _x < 0 || _y < 0) continue;
					if ( _x >= audiences.gridWidth || _y >= audiences.gridHeight) continue;
					if ( audiences.exhibition.field[_x][_y] == audiences.WALL) continue;
					
					// find the length from curPostition to destinationPostion.
					Double tmp = Math.pow(interestX - _x, 2) + Math.pow(interestY - _y, 2);
					
					// get the closest path compared.
					if(tmp < length)
					{
						newx = _x;
						newy = _y;
						length = tmp;
					}
					
				}
			}
			// move randomly
			if(state.random.nextBoolean(audiences.AUDIENCE_RANDOMIZE)){

				int _x = location.x +  audiences.random.nextInt(3)-1;;
				int _y = location.y + audiences.random.nextInt(3)-1;;
			
				
				// can't going here out of space.
				if(_x < 0 ){ _x++;}
				else if (_x >= audiences.trails.getWidth()) { _x--;}
			
				if(_y < 0){ _y++;}
				else if (_y >= audiences.trails.getHeight()){ _y--;}
				
				newx = _x;
				newy = _y;
				
			}

		}
		else if(this.interest == 0) // interest is 0 ... going home. greedy
		{
			Double length = 999999999.0;
			
			if(location.x == homeX && location.y == homeY)
			{
				newx = location.x; newy = location.y;
			}

			else{ 
				
				for(int dx = -1; dx < 2; dx++){
					for( int dy = -1; dy < 2; dy++)
					{
						int _x = dx + location.x;
						int _y = dy + location.y;
					
						// nowhere to go.
						if ( dx ==0 && dy ==0) continue;
						if ( _x < 0 || _y < 0) continue;
						if ( _x >= audiences.gridWidth || _y >= audiences.gridHeight) continue;
						if ( audiences.exhibition.field[_x][_y] == audiences.WALL) continue;
						if ( audiences.sites.field[_x][_y] == audiences.STUFF) continue;
					
						Double tmp = Math.pow(homeX - _x, 2) + Math.pow(homeY - _y, 2);
										
						if(tmp < length)
						{
							newx = _x; 
							newy = _y;
							length = tmp;
						}
					}
				}
			}

		}
		

		
		// decrease the agent's interest.		
		if( audiences.sites.field[newx][newy] == audiences.STUFF ){
			if(this.interest > 0){
				this.interest--;
				this.interestSpace.pop();
			}
			// maybe new interest created.
			if(state.random.nextBoolean(audiences.INTEREST_CREATING)){
				this.interest++;
			}
			
		}
		
		
		Int2D newloc = new Int2D(newx, newy);
		if (audiences.exhibition.field[newx][newy] == 0)
		{
			audiences.humans.setObjectLocation(this, newloc);
			
			if(isZone1(newloc)) audiences.Zone1.add(this);
			if(isZone2(newloc)) audiences.Zone2.add(this);
			if(isZone3(newloc)) audiences.Zone3.add(this);
			if(isZone4(newloc)) audiences.Zone4.add(this);
			if(isZone5(newloc)) audiences.Zone5.add(this);
			if(isZone6(newloc)) audiences.Zone6.add(this);
			if(isZone7(newloc)) audiences.Zone7.add(this);
			if(isZone8(newloc)) audiences.Zone8.add(this);
			if(isZone9(newloc)) audiences.Zone9.add(this);
			if(isZone10(newloc)) audiences.Zone10.add(this);
			if(isZone11(newloc)) audiences.Zone11.add(this);
			if(isZone12(newloc)) audiences.Zone12.add(this);
			
		}
		
		
		
		
		last = location;
	}
	
}
