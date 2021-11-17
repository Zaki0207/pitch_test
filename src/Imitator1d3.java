import devstudio.generatedcode.*;
import devstudio.generatedcode.datatypes.*;
import devstudio.generatedcode.exceptions.HlaBaseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
//import java.text.DecimalFormat;

public class Imitator1d3 {
	
	//data field
	private static final boolean USE_TIME_MANAGEMENT=false;
	
	private final String ACID = "Imitator1d3";//name of this aircraft (focus)
	private final String LEADER = "TestFim";//the aircraft to follow (base)
	
	private final HlaWorld _hlaWorld;
	private final HlaPacer _hlaPacer;
	
    int waitingTime = 10;//how long to wait at the beginning
    boolean stillWaiting = true;//if still waiting (if indexW has never reached waitingTime)
    int indexW = 0;//index for memory of leader's states
	
	//Location3D Struct of leader
	float[] leaderAlt = new float[waitingTime];
	float[] leaderLat = new float[waitingTime];
	float[] leaderLong = new float[waitingTime];
    
	//GroundReferenceState Struct of leader
    float[] leaderMagneticTrack = new float[waitingTime];
    float[] leaderSpeedSI = new float[waitingTime];
    float[] leaderTrueTrack = new float[waitingTime];
    float[] leaderVerticalSpeedSI = new float[waitingTime];
    
    //Location3D Struct of me(imi)
	float myAlt;
	float myLat;
	float myLong;
    
	//GroundReferenceState Struct of me(imi)
    float myMagneticTrack;
    float mySpeedSI;
    float myTrueTrack;
    float myVerticalSpeedSI;
	
//	//AirSpeed Struct
//	float[] leaderIndicatedAirSpeed = new float[waitingTime];
//	float[] leaderMach = new float[waitingTime];
//	float[] leaderTrueAirSpeed = new float[waitingTime];
//	//Orientation Struct
//    float[] leaderHeading = new float[waitingTime];
//    boolean[] leaderIsValid = new boolean[waitingTime];
//    float[] leaderMagneticHeading = new float[waitingTime];
//    float[] leaderPitch = new float[waitingTime];
//    float[] leaderRoll = new float[waitingTime];
	
//	double WindDirection;
//	double WindSpeed;
//	double ObjAlt = -999;
//	double ObjHead = -999;
//	double ObjTAS = -999;
//	String ObjFltNo = "null";
//	double Climb_rate = 10;
//	//???
//	String Selected_AC;
//	int ATCcmd = 0;
//	double acc = 2;
//	double turing_rate = 3;
//	String ATC_cmd_str = "\n";
//	int cockpit_NO = 1;
//	
//	long master_time;
//	String Scenario = "";
//	OperationRateEnum sim_rate;
//	OperationStateEnum sim_state;
//	int ind_rate = 1;
	
	//for GUI?
//	String init_descrip = "未初始化";
//	String state_descrip = "停止";
//	String time_descrip = "00:00:00";
//	String rate_descrip = "1倍速";
	
	//listeners
	//interaction listeners
	
	//object attribute listeners
	private final HlaBaseAircraftListener _baseAircraftListener = new HlaBaseAircraftListener.Adapter() {
		@Override // listen to the leader's attributes(any)
		public void attributesUpdated(HlaBaseAircraft baseAircraft, Set<HlaBaseAircraftAttributes.Attribute> attributes, HlaTimeStamp timeStamp, HlaLogicalTime logicalTime) {
			if(baseAircraft.getHlaInstanceName().equals(LEADER)) {//if it's from the leader
				//save the leader's states
				leaderAlt[indexW] = baseAircraft.getLocation3D().getAltitude();
				leaderLat[indexW] = baseAircraft.getLocation3D().getLocation2D().getLatitude();
				leaderLong[indexW] = baseAircraft.getLocation3D().getLocation2D().getLongitude();
				
				leaderMagneticTrack[indexW] = baseAircraft.getGroundReferenceState().getMagneticTrack();
				leaderSpeedSI[indexW] = baseAircraft.getGroundReferenceState().getSpeedSI();
				leaderTrueTrack[indexW] = baseAircraft.getGroundReferenceState().getTrueTrack();
				leaderVerticalSpeedSI[indexW] = baseAircraft.getGroundReferenceState().getVerticalSpeedSI();
				
//				leaderIndicatedAirSpeed[indexW] = baseAircraft.getAirSpeed().getIndicatedAirSpeed();
//				leaderMach[indexW] = baseAircraft.getAirSpeed().getMach();
//				leaderTrueAirSpeed[indexW] = baseAircraft.getAirSpeed().getTrueAirSpeed();
//				
//				leaderHeading[indexW] = baseAircraft.getOrientation().getHeading();
//				leaderIsValid[indexW] = baseAircraft.getOrientation().isValid;
//				leaderMagneticHeading[indexW] = baseAircraft.getOrientation().getMagneticHeading();
//				leaderPitch[indexW] = baseAircraft.getOrientation().getPitch();
//				leaderRoll[indexW] = baseAircraft.getOrientation().getRoll();

				indexW++;
				
				if (indexW==waitingTime){
					stillWaiting=false;//stop waiting after indexW reached 10<waitingTime>
					indexW=0;//loop
				}
//				System.out.println("Received updated:");  // 可以添加数据项打印
			}
		}
		
	};
	
	//constructor
	public Imitator1d3() {
		//create a hlaworld
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
	             return "192.168.46.199";
	         }
	         
	         @Override
	         public String getFederateName() {
	            return "Imitator1d3";
	         }
		});
		
		//attach the listeners
		_hlaWorld.getHlaBaseAircraftManager().addHlaBaseAircraftDefaultInstanceListener(_baseAircraftListener);
		_hlaPacer = HlaPacer.create(1, TimeUnit.SECONDS); 
	}
	
	//main_
	public void simulate() throws HlaBaseException, InterruptedException {
		//boolean restart = true;
//		HlaLogicalTime currentTime = _hlaWorld.connect();
		_hlaWorld.connect();
		
		//create a new aircraft
		HlaFocusAircraft imi =_hlaWorld.getHlaFocusAircraftManager().createLocalHlaFocusAircraft(ACID, ACID);
		//create an updater for the aircraft
		while (true) {
			//one frame starts
			if (stillWaiting == false) {// if not waiting
				
				
				//Imitate the leader's state of 10<waitingTime> seconds ago
				int indexWPlusOne = (indexW+1)%waitingTime;
				
				myAlt = leaderAlt[indexWPlusOne];
				myLat = leaderLat[indexWPlusOne];
				myLong = leaderLong[indexWPlusOne];

				myMagneticTrack = leaderMagneticTrack[indexWPlusOne];
				mySpeedSI = leaderSpeedSI[indexWPlusOne];
				myTrueTrack = leaderTrueTrack[indexWPlusOne];
				myVerticalSpeedSI = leaderVerticalSpeedSI[indexWPlusOne];

				//prepare to publish
				Location2DStruct location2D;
				location2D = Location2DStruct.create(myLat,myLong);
				Location3DStruct location3D;
				location3D = Location3DStruct.create(myAlt,location2D);
				HlaFocusAircraftUpdater imiUpdater = imi.getHlaFocusAircraftUpdater();
				imiUpdater.setLocation3D(location3D);			
				GroundReferenceStateStruct groundReferencestate;
				groundReferencestate = GroundReferenceStateStruct.create(myMagneticTrack,mySpeedSI,myTrueTrack,myVerticalSpeedSI);
				imiUpdater.setGroundReferenceState(groundReferencestate);
				//publish
				imiUpdater.sendUpdate();
				System.out.println("Simualtor has updated:" + location3D);
			}
			
			_hlaWorld.advanceToNextFrame();
	        _hlaPacer.pace();	
		}
		
		
	}
	
	//main
	public static void main(String[] args) throws HlaBaseException, InterruptedException {
		// TODO Auto-generated method stub
		new Imitator1d3().simulate();
	}

}
