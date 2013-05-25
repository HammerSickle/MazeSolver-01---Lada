package org.wintrisstech.erik.iaroc;

import java.util.List;
import java.util.Stack;
import java.lang.Integer;

import android.os.SystemClock;
import android.util.Log;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.sensors.UltraSonicSensors;

/**
 * A Lada is an implementation of the IRobotCreateInterface, inspired by Vic's
 * awesome API. It is entirely event driven.
 * 
 * @author Erik
 */
public class Lada extends IRobotCreateAdapter {

	private static final String TAG = "Lada";
	private final Dashboard dashboard;
	public UltraSonicSensors sonar;

	/**
	 * Constructs a Lada, an amazing machine!
	 * 
	 * @param ioio
	 *            the IOIO instance that the Lada can use to communicate with
	 *            other peripherals such as sensors
	 * @param create
	 *            an implementation of an iRobot
	 * @param dashboard
	 *            the Dashboard instance that is connected to the Lada
	 * @throws ConnectionLostException
	 */
	public Lada(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard)
			throws ConnectionLostException {
		super(create);
		sonar = new UltraSonicSensors(ioio);
		this.dashboard = dashboard;
		song(0, new int[] { 58, 10 });
	}

	public void initialize() throws ConnectionLostException {
		dashboard.log("===========Start===========version 5/2/13");
		readSensors(SENSORS_GROUP_ID6);
		dashboard.log("Battery Charge = " + getBatteryCharge()
				+ ", 2,800 = Full charge" + "\n\t" + getBatteryCharge()
				/ getBatteryCapacity() + "% full");
	}

	/**
	 * This method is called repeatedly
	 * 
	 * @throws ConnectionLostException
	 */
	Boolean doneTurning = true;
	int turnD = 300;

	public void loop() throws ConnectionLostException {
		
		log("turning: " + turnD + "");
		//firstSensorTest();
		readSensors(SENSORS_GROUP_ID6);
//		log("Angle:" + getAngle() + "/n");
		try {
			sonar.readUltrasonicSensors();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driveDirect(200, 200);
//		log("\tLeftD:\t" + sonar.getLeftDistance() + "\n\tRightD:\t"
//				+ sonar.getRightDistance());
		if (sonar.getFrontDistance() <= 35 && doneTurning == true) {
			doneTurning = false;
//			log("Left distance: " + sonar.getLeftDistance()
//					+ "\nRight distance: " + sonar.getRightDistance());
		
			chooseDirectionAndTurn();
		}
		if(isBumpCenter()){
			doneTurning = false;
			driveDirect(-500, -500);
			SystemClock.sleep(500);
			chooseDirectionAndTurn();
		}
	}

	private void chooseDirectionAndTurn() throws ConnectionLostException {
		if (sonar.getLeftDistance() >= sonar.getRightDistance()) {
			turnLeft();
		} else {
			turnRight();
		}
	}

	private void firstSensorTest() throws ConnectionLostException {
		readSensors(SENSORS_GROUP_ID6);
		log("Angle:" + getAngle() + "/n");
		try {
			sonar.readUltrasonicSensors();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driveDirect(500, 500);
//		log("\tLeftD:\t" + sonar.getLeftDistance() + "\n\tRightD:\t"
//				+ sonar.getRightDistance());
		if (isBumpCenter()) {
			driveDirect(-500, -500);
			SystemClock.sleep(500);
			driveDirect(0, 0);
//			log("Left distance: " + sonar.getLeftDistance()
//					+ "\nRight distance: " + sonar.getRightDistance());
			chooseDirectionAndTurn();
		}
	}

	public boolean isBumpCenter() {
		return isBumpLeft() && isBumpRight();
	}

	public void turnRight() throws ConnectionLostException {
		dashboard.log("turningRight");
		driveDirect(0, 350);
		SystemClock.sleep(turnD);
		doneTurning = true;
		
	}

	public void turnLeft() throws ConnectionLostException {
		dashboard.log("turningLeft");
		driveDirect(350, 0);
		SystemClock.sleep(turnD);
		doneTurning = true;
		
		
	}

	public void log(String message) {
		dashboard.log(message);
	}
}