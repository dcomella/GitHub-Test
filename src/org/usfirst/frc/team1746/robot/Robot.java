package org.usfirst.frc.team1746.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, 
 described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	//Auton auton;
	RobotDrive myRobot;
	Joystick xbox;
	int autoLoopCounter;
	Solenoid wedgeControl;
	Solenoid armControl;
	Solenoid ballIndicator;
	Victor intake;
	Victor tapeMeasure;
	DigitalInput beambreak;
	CameraServer server;
	Encoder leftDrive;
	Encoder rightDrive;
	AnalogInput defenseSelector;
	AnalogInput slotSelector;
	DigitalInput goalSelector;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	//auton = new Auton();
    	
    	myRobot = new RobotDrive(0,1);
    	
    	xbox = new Joystick(0);
    	
    	wedgeControl = new Solenoid(0);
    	armControl = new Solenoid(1);
    	ballIndicator = new Solenoid(2);
    	
    	tapeMeasure = new Victor(5);
    	intake = new Victor(6);
    	
    	goalSelector = new DigitalInput(7);
    	beambreak = new DigitalInput(6);
    	
    	defenseSelector = new AnalogInput(2);
    	slotSelector = new AnalogInput(3);
    	
    	leftDrive = new Encoder(0, 1, false, Encoder.EncodingType.k1X);
    	rightDrive = new Encoder(2, 3, false, Encoder.EncodingType.k1X);
    	
    	server = CameraServer.getInstance();
        server.setQuality(50);
        server.startAutomaticCapture("cam0");
        
       
    	
    }
    
    
    
    
    /**
     * This function is called once each time the robot enters tele-operated mode
     */
    public void teleopInit(){
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        myRobot.arcadeDrive(xbox);
        
    // Wedge 
        if(xbox.getRawButton(5)){
        	wedgeControl.set(true);
        	
        } else{
        	wedgeControl.set(false);
        }
    SmartDashboard.putBoolean("btn5",xbox.getRawButton(5) );
        SmartDashboard.putNumber("encoder", leftDrive.getRaw());
    //
    // Arm 
        if(xbox.getRawButton(6)){
        	armControl.set(false);
        	
        } else{
        	armControl.set(true);
        }
    //
    // Intake
        if(xbox.getRawButton(1) && !(beambreak.get())){
       	 intake.set(1);
       	  
        }else if(xbox.getRawButton(2)){
        	intake.set(-1);
        } else{
        	intake.set(0);
        }
    //    
    // Ball Indicator
       ballIndicator.set(!beambreak.get());
   
    // Tape Measure Motor
       if(xbox.getPOV(0) == 0){
    	   tapeMeasure.set(1); 
       }else if(xbox.getPOV(0) == 180){
    	   tapeMeasure.set(-1);
       }else{
    	   tapeMeasure.set(0);
       }
       //
       //AutonSelector
       
    }
    
   
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
    //////////////////////////////////////////////////////////////
    //////////////         Auton Begins           ////////////////
    //////////////////////////////////////////////////////////////
    
    Defense selectedDefense;
    Slot selectedSlot;
    Goal selectedGoal;
    
    public enum Defense {
		NONE,
		APPROACH_ONLY,
		LOWBAR,
		A_PORTCULLIS,
		A_CHEVAL_DE_FRISE,
		B_RAMPARTS,
		B_MOAT,
		C_DRAWBRIDGE,
		C_SALLY_PORT,
		D_ROCK_WALL,
		D_ROUGH_TERRAIN
	};
	public enum Slot {
		SLOT_1,
		SLOT_2,
		SLOT_3,
		SLOT_4,
		SLOT_5
	};
	public enum Goal {
		LOW_LEFT,
		LOW_RIGHT,
		HIGH_LEFT,
		HIGH_MID,
		HIGH_RIGHT
	};
	public enum AutonStates {
		INIT,
		APPROACH,
		CALIBRATE,
		APPROACH_TOWER,
		SHOOT,
		RETREAT,
		UNBREACH,
		REALIGN
	};

    public void autonomousInit() {
    	autonState = AutonStates.INIT;
    }
    
    AutonStates autonState;
    
    public void autonomousPeriodic() {
    	switch (autonState) {
    	case INIT:
    		SmartDashboard.putString("Current State: ", "INIT");
    		autonState = AutonStates.APPROACH;
    		break;
    	case APPROACH:
    		SmartDashboard.putString("Current State: ", "APPROACH");
    		SmartDashboard.putNumber("Left Drive Encoder: ", leftDrive.get());
    		if(leftDrive.get() > 100){
    			autonState = AutonStates.CALIBRATE;
    			leftDrive.reset();
    		}
        	break;
    	case CALIBRATE:
    		SmartDashboard.putString("Current State: ", "CALIBRATE");
    		autonState = AutonStates.APPROACH_TOWER;
        	break;
    	case APPROACH_TOWER:
    		SmartDashboard.putString("Current State: ", "APPROACH_TOWER");
    		SmartDashboard.putNumber("Left Drive Encoder: ", leftDrive.get());
    		if(leftDrive.get() > 200){
    			autonState = AutonStates.SHOOT;
    			leftDrive.reset();
    		}
        	break;
    	case SHOOT:
    		SmartDashboard.putString("Current State: ", "SHOOT");
    		autonState = AutonStates.RETREAT;
        	break;
    	case RETREAT:
    		SmartDashboard.putString("Current State: ", "RETREAT");
    		SmartDashboard.putNumber("Left Drive Encoder: ", leftDrive.get());
    		if(leftDrive.get() > 300){
    			autonState = AutonStates.UNBREACH;
    			leftDrive.reset();
    		}
        	break;
    	case UNBREACH:
    		SmartDashboard.putString("Current State: ", "UNBREACH");
        	break;
    	case REALIGN:
    		SmartDashboard.putString("Current State: ", "REALIGN");
        	break;
    	}
    }
	
	
	public void setDefenseSelector(){
		if(defenseSelector.getValue() >= 0 && defenseSelector.getValue() < 365){
			selectedDefense = Defense.NONE;
			SmartDashboard.putString("Selected Defense: ", "NONE");
		}
		if(defenseSelector.getValue() >= 365 && defenseSelector.getValue() < 728){
			selectedDefense = Defense.APPROACH_ONLY;
			SmartDashboard.putString("Selected Defense: ", "APPROACH_ONLY");
		}
		if(defenseSelector.getValue() >= 728 && defenseSelector.getValue() < 1091){
			selectedDefense = Defense.LOWBAR;
			SmartDashboard.putString("Selected Defense: ", "LOWBAR");
		}
		if(defenseSelector.getValue() >= 1091 && defenseSelector.getValue() < 1454){
			selectedDefense = Defense.A_PORTCULLIS;
			SmartDashboard.putString("Selected Defense: ", "A_PORTCULLIS");
		}
		if(defenseSelector.getValue() >= 1454 && defenseSelector.getValue() < 1817){
			selectedDefense = Defense.A_CHEVAL_DE_FRISE;
			SmartDashboard.putString("Selected Defense: ", "A_CHEVAL_DE_FRISE");
		}
		if(defenseSelector.getValue() >= 1817 && defenseSelector.getValue() < 2181){
			selectedDefense = Defense.B_RAMPARTS;
			SmartDashboard.putString("Selected Defense: ", "B_RAMPARTS");
		}
		if(defenseSelector.getValue() >= 2181 && defenseSelector.getValue() < 2545){
			selectedDefense = Defense.B_MOAT;
			SmartDashboard.putString("Selected Defense: ", "B_MOAT");
		}
		if(defenseSelector.getValue() >= 2545 && defenseSelector.getValue() < 2909){
			selectedDefense = Defense.C_DRAWBRIDGE;
			SmartDashboard.putString("Selected Defense: ", "C_DRAWBRIDGE");
		}
		if(defenseSelector.getValue() >= 2909 && defenseSelector.getValue() < 3273){
			selectedDefense = Defense.C_SALLY_PORT;
			SmartDashboard.putString("Selected Defense: ", "C_SALLY_PORT");
		}
		if(defenseSelector.getValue() >= 3273 && defenseSelector.getValue() < 3635){
			selectedDefense = Defense.D_ROCK_WALL;
			SmartDashboard.putString("Selected Defense: ", "D_ROCK_WALL");
		}
		if(defenseSelector.getValue() >= 3635 && defenseSelector.getValue() < 3999){
			selectedDefense = Defense.D_ROUGH_TERRAIN;
			SmartDashboard.putString("Selected Defense: ", "D_ROUGH_TERRAIN");
		}
	}
	public void setSlotSelector(){
		if(slotSelector.getValue() >= 0 && slotSelector.getValue() < 799){
			selectedSlot = Slot.SLOT_1;
			SmartDashboard.putString("Selected Slot: ", "SLOT_1");
		}
		if(slotSelector.getValue() >= 799 && slotSelector.getValue() < 1598){
			selectedSlot = Slot.SLOT_2;
			SmartDashboard.putString("Selected Slot: ", "SLOT_2");
		}
		if(slotSelector.getValue() >= 1598 && slotSelector.getValue() < 2397){
			selectedSlot = Slot.SLOT_3;
			SmartDashboard.putString("Selected Slot: ", "SLOT_3");
		}
		if(slotSelector.getValue() >= 2397 && slotSelector.getValue() < 3197){
			selectedSlot = Slot.SLOT_4;
			SmartDashboard.putString("Selected Slot: ", "SLOT_4");
		}
		if(slotSelector.getValue() >= 3197 && slotSelector.getValue() < 3999){
			selectedSlot = Slot.SLOT_5;
			SmartDashboard.putString("Selected Slot: ", "SLOT_5");
		}
	}
	public void setGoalSelector(){
		if(goalSelector.get() == true){
			selectedGoal = Goal.LOW_LEFT;
			SmartDashboard.putString("Selected Goal: ", "Left");
		} else{
			selectedGoal = Goal.LOW_RIGHT;
			SmartDashboard.putString("Selected Goal: ", "Right");
		}
	}
	public void autonSmartDashboardUpdate(){
		SmartDashboard.putNumber("Defense Selector", defenseSelector.getValue());
	    SmartDashboard.putNumber("Slot Selector", slotSelector.getValue());
	    SmartDashboard.putBoolean("Left or Right Goal", goalSelector.get());
	   
	    
	}
    
//////////////////////////////////////////////////////////////
//////////////            Disable             ////////////////
//////////////////////////////////////////////////////////////
	public void disabledPeriodic(){
		setDefenseSelector();
    	setGoalSelector();
    	setSlotSelector();
    }
}


