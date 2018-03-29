import sim.portrayal.network.*;
import sim.portrayal.continuous.*;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.simple.*;
import sim.portrayal.*;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.color.*;

public class AudiencesWithUI extends GUIState
{
	
	public Display2D display;
	public JFrame displayFrame;

	SparseGridPortrayal2D audiencesPortrayal = new SparseGridPortrayal2D();
	FastValueGridPortrayal2D trailsPortrayal = new FastValueGridPortrayal2D("Trail");
	FastValueGridPortrayal2D exhibitionPortrayal = new FastValueGridPortrayal2D("exhibition");
	FastValueGridPortrayal2D sitesPortrayal = new FastValueGridPortrayal2D("sites");
	
	public static void main(String[] args)
	{
		new AudiencesWithUI().createController();
	}
	public AudiencesWithUI() { super(new Audiences(System.currentTimeMillis())); }
	public AudiencesWithUI(SimState state) { super(state); }
	
	public Object getSimulationInspectedObject() {	return state; } // make model ...i can customize my audience number. set, get
	public static String getName() { return "Audience Moving"; }
	
	
	
	
	public void start()
	{
		super.start();	
		setupPortrayals();
	}
	
	public void load(SimState state)
	{
		super.load(state);
		setupPortrayals();
	}
	
	public void setupPortrayals()
	{
		Audiences audiences = (Audiences) state;
		
		trailsPortrayal.setField(
				((Audiences)state).trails);
		trailsPortrayal.setMap(
				new sim.util.gui.SimpleColorMap(0.0,1.0,Color.white,Color.white));

		exhibitionPortrayal.setField(((Audiences)state).exhibition);
		exhibitionPortrayal.setMap(
				new sim.util.gui.SimpleColorMap(
		                0,
		                1,
		                new Color(0,0,0,0),
		                Color.white));
		
		audiencesPortrayal.setField(((Audiences)state).humans);
		audiencesPortrayal.setPortrayalForAll(new sim.portrayal.simple.OvalPortrayal2D(Color.red));

		sitesPortrayal.setField(((Audiences)state).sites);
		sitesPortrayal.setMap(
				new sim.util.gui.SimpleColorMap(
						0,
						2,
				new Color(0,0,0,0),
				Color.blue));
		
		
		display.reset();		
		display.repaint();
	}
	
	public void init(Controller c)
	{
		super.init(c);
		display = new Display2D(900, 900, this);
		displayFrame = display.createFrame();		
		displayFrame.setTitle("Zone Display");
		c.registerFrame(displayFrame); // so the frame appears in the "Display" list
		displayFrame.setVisible(true);
	
		display.setBackdrop(Color.black);
		
		display.attach(trailsPortrayal, "Trails");
		display.attach(audiencesPortrayal, "Audiences");
		display.attach(exhibitionPortrayal, "Exhibition");
		display.attach(sitesPortrayal,"Site Locations");
	}
	
	
	
	public void quit()
	{
		super.quit();
		if (displayFrame!=null) displayFrame.dispose();
		displayFrame = null;
		display = null;
	}
}
