package org.usfirst.frc.team1746.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	//test test
	// test 2
	RobotDrive myRobot;
	Joystick xbox;
	int autoLoopCounter;
	Solenoid wedgeUp;
	Solenoid wedgeDown;
	Solenoid armUp;
	Solenoid armDown;
	Victor intake;
	Victor tapeMeasure;
	DigitalInput beambreak;
	DigitalOutput ballIndicator;
	
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	myRobot = new RobotDrive(0,1);
    	
    	xbox = new Joystick(0);
    	
    	wedgeUp = new Solenoid(0);
    	wedgeDown = new Solenoid(1);
    	armUp = new Solenoid(2);
    	armDown = new Solenoid(3);
    	
    	tapeMeasure = new Victor(5);
    	intake = new Victor(6);
    	
    	beambreak = new DigitalInput(6);
    	ballIndicator = new DigitalOutput(9);
    }
    
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
    	autoLoopCounter = 0;
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	if(autoLoopCounter < 100) //Check if we've completed 100 loops (approximately 2 seconds)
		{
			myRobot.drive(-0.5, 0.0); 	// drive forwards half speed
			autoLoopCounter++;
			} else {
			myRobot.drive(0.0, 0.0); 	// stop robot
		}
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
        	wedgeUp.set(false);
        	wedgeDown.set(true);
        	
        } else{
        	wedgeDown.set(false);
        	wedgeUp.set(true);
        }
    //
    // Arm 
        if(xbox.getRawButton(6)){
        	armUp.set(false);
        	armDown.set(true);
        	
        } else{
        	armDown.set(false);
        	armUp.set(true);
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
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
    
}