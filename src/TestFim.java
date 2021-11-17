import java.util.concurrent.TimeUnit;

import devstudio.generatedcode.HlaBaseAircraft;
import devstudio.generatedcode.HlaBaseAircraftUpdater;
import devstudio.generatedcode.HlaFocusAircraft;
import devstudio.generatedcode.HlaFocusAircraftUpdater;
import devstudio.generatedcode.HlaPacer;
import devstudio.generatedcode.HlaSettings;
import devstudio.generatedcode.HlaWorld;
import devstudio.generatedcode.datatypes.GroundReferenceStateStruct;
import devstudio.generatedcode.datatypes.Location2DStruct;
import devstudio.generatedcode.datatypes.Location3DStruct;
import devstudio.generatedcode.exceptions.HlaBaseException;

public class TestFim {

	//data field
	private static final boolean USE_TIME_MANAGEMENT=false;

	private final String ACID = "TestFim";//name of this aircraft (base)
	
	private final HlaWorld _hlaWorld;
	private final HlaPacer _hlaPacer;

    //Location3D Struct of me
	float myAlt;
	float myLat;
	float myLong;
    
	//GroundReferenceState Struct of me
    float myMagneticTrack;
    float mySpeedSI;
    float myTrueTrack;
    float myVerticalSpeedSI;

    
	// 每秒进行一次位置运算，分别进行X和Y上的投影速度计算，进而得出单位秒的X和Y轴位移
	public static float getNextPos_X(float DIR, float SPEED, float Current_X) {
		float NextPos_X;
		SPEED = (float) SPEED/3600;
		double radians = Math.toRadians(DIR);
		NextPos_X = SPEED * (float)(Math.sin(radians)) + Current_X;
		return NextPos_X;
	}
	
	public static float getNextPos_Y(float DIR, float SPEED, float Current_Y) {
		float NextPos_Y;
		SPEED = (float) SPEED/3600;
		double radians = Math.toRadians(DIR);
		NextPos_Y = - SPEED * (float)(Math.cos(radians)) + Current_Y;	
		return NextPos_Y;
	}
    
    //constructor
    public TestFim() {
		_hlaWorld = HlaWorld.Factory.create(new HlaSettings() {
			@Override
			public boolean getTimeConstrained() {
	            return USE_TIME_MANAGEMENT;
	         }

	         @Override
	         public boolean getTimeRegulating() {
	            return USE_TIME_MANAGEMENT;
	         }
	         
	         @Override
	         public String getFederationName() {
	             return "FocusAircraftTest";
	          }
	         
	         @Override
	         public String getCrcHost() {
	             return "192.168.46.241";
	         }
	         
	         @Override
	         public String getFederateName() {
	            return "TestFim";
	         }
	         
		});
		_hlaPacer = HlaPacer.create(1, TimeUnit.SECONDS); 
    }
	
	//main_
	public void simulate() throws HlaBaseException, InterruptedException  {
		_hlaWorld.connect();
		HlaBaseAircraft testFim =_hlaWorld.getHlaBaseAircraftManager().createLocalHlaBaseAircraft(ACID, ACID);
		System.out.println("Simualtor start!");
		

		boolean restart = true;
		
		while(true) {
			if (restart) {
				
				myAlt = 555;
				myLat = 555;
				myLong = 555;

				myMagneticTrack = 0;
				mySpeedSI = 250;
				myTrueTrack = 0;
				myVerticalSpeedSI = 0;

				Location2DStruct location2D;
				location2D = Location2DStruct.create(myLat,myLong);
				Location3DStruct location3D;
				location3D = Location3DStruct.create(myAlt,location2D);
				HlaBaseAircraftUpdater testFimUpdater = testFim.getHlaBaseAircraftUpdater();
				testFimUpdater.setLocation3D(location3D);
				
				GroundReferenceStateStruct groundReferencestate;
				groundReferencestate = GroundReferenceStateStruct.create(myMagneticTrack,mySpeedSI,myTrueTrack,myVerticalSpeedSI);
				testFimUpdater.setGroundReferenceState(groundReferencestate);
				//publish
				testFimUpdater.sendUpdate();
				System.out.println("Simualtor has initiallized!");

				restart = false;
			}
				
			else {
				
				//calculate
				myLat = getNextPos_X(0, mySpeedSI, myLat);
				myLong = getNextPos_Y(0, mySpeedSI, myLong);
				
				Location2DStruct location2D;
				location2D = Location2DStruct.create(myLat,myLong);
				Location3DStruct location3D;
				location3D = Location3DStruct.create(myAlt,location2D);
				
				HlaBaseAircraftUpdater testFimUpdater = testFim.getHlaBaseAircraftUpdater();
				testFimUpdater.setLocation3D(location3D);
				testFimUpdater.sendUpdate();
				System.out.println("Simualtor has updated:" + location3D);
				
			}
			
			_hlaWorld.advanceToNextFrame();
	        _hlaPacer.pace();
		}
		
	}

	
	//main
	public static void main(String[] args) throws HlaBaseException, InterruptedException  {
		// TODO Auto-generated method stub
		new TestFim().simulate();
	}

}
