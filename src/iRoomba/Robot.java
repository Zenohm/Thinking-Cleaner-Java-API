package iRoomba;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

// Dependencies are SimpleJSON and IOUtils.
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;


/**
 * A Java API for the Thinking Cleaner device.
 */
public class Robot{
	
	String ip;
	
	/** Remembers all Robots that were initialized. */
	public static ArrayList<String> Robots = new ArrayList<String>();
	/** Preset delay that each command will take in Milliseconds */
	static int DELAY = 300;
	
	/** The Roomba will not drive in dangerous situations */
	private final String cautiousMode = "DriveNormal";
	/** The Roomba will continue to drive in dangerous situations*/
	private final String hazardousMode  = "DriveAlways";
	
	/**
	 * A array list of Strings that remembers commands given to the Robot.
	 * <br> Indev*/
	public ArrayList<String> memory = new ArrayList<String>();
	String action = null;
	String result = null;
	boolean cautious = true;
	
	/** Determines whether the most recently updated data set was the fullStatus object */
	private boolean useFullStatus   = false;
	/** Determines whether the most recently updated data set was the simpleStatus object */
	private boolean useSimpleStatus = false;
	
	private JSONObject simpleStatus = null;
	private JSONObject firmware 	= null;
	private JSONObject tc_status 	= null;
	private JSONObject power_status = null;
	private JSONObject buttons 		= null;
	private JSONObject sensors 		= null;
	private JSONObject webview 		= null;
	
	
	public Robot(String ipAddress) {
		
		ip = ipAddress;
		Robots.add(ip);
		
	}
	
	
	// Information Handling \\
	/**
	 * Updates the information gathered by the Thinking Cleaner device.
	 * <br>
	 * Allows for the retrieval of the complete status or a simpler status.
	 * 
	 * @param wantFullStatus Fetch the full status update?
	 */
	public void statusUpdate(boolean wantFullStatus) {
		
		if(wantFullStatus){
			
			String statusAddress = "http://" + ip + "/full_status.json";
			JSONObject FullJSONStatus = getJSON(statusAddress);
			
			/** What action was expected to be transmitted and executed */
			action          =	    toStr(FullJSONStatus.get("action"));
			/** Is the command itself a valid command? */
			result 		    =       toStr(FullJSONStatus.get("result"));
			
			firmware        = 		(JSONObject) FullJSONStatus.get("firmware");
			tc_status       =	 	(JSONObject) FullJSONStatus.get("tc_status");
			power_status    = 		(JSONObject) FullJSONStatus.get("power_status");
			buttons         = 		(JSONObject) FullJSONStatus.get("buttons");
			sensors         = 		(JSONObject) FullJSONStatus.get("sensors");
			webview         = 		(JSONObject) FullJSONStatus.get("webview");
						
			useFullStatus   = true;
			useSimpleStatus = false;
			
		} else {
			
			String statusAddress = "http://" + ip + "/status.json";
			JSONObject SimpleJSONStatus = getJSON(statusAddress);
			
			action = SimpleJSONStatus.get("action").toString();
			result = SimpleJSONStatus.get("result").toString();
			
			simpleStatus = (JSONObject) SimpleJSONStatus.get("status");
			
			useFullStatus   = false;
			useSimpleStatus = true;
			
		}
	}
	/**
	 * Accesses the simpleStatus JSON object so new data can accessed by 
	 * <br>developers if it cannot already be accessed via a method.
	 */
	public JSONObject  getSimpleStatus() {
				
		if(simpleStatus==null)
			statusUpdate(false);
		
		return simpleStatus;
	}
		/**
		 * Retrieves the name of the Robot as recorded by the Thinking Cleaner device.
		 */
		public String  name() {
						
			if(simpleStatus==null)
				statusUpdate(false);
			
			return toStr(simpleStatus.get("name"));
			
		}
	/**
	 * Accesses the firmware JSON object so new data can accessed by 
	 * developers if it cannot already be accessed via a method.</p>
	 * This will check to see if the required status value has been retrieved,
	 * and will fetch it if it has not.</p>
	 * @return Firmware JSONObject.
	 */
	public JSONObject  firmware() {
		
		if(firmware==null)
			statusUpdate(true);
		
		return firmware;
		
	}
		public String  version() {
			
			firmware();
			
			return toStr(firmware.get("version"));
			
		}
		public String  wifiVersion() {
			
			firmware();
			
			return toStr(firmware.get("wifi_version"));
			
		}
		public String  UUID() {
			
			firmware();
			
			return toStr(firmware.get("uuid"));
			
		}
		public String  macAddress() {
			
			firmware();
			
			return toStr(firmware.get("mac_address"));
			
		}
		public boolean usesDHCP() {
			
			firmware();
			
			return toBool(firmware.get("DHCP"));
			
		}
		public boolean hasBeenBackedUp() {
			
			firmware();
			
			return toBool(firmware.get("has_been_backed_up"));
			
		}
		public boolean hasAuthToken() {
			
			firmware();
			
			return toBool(firmware.get("has_auth_token"));
			
		}
		public String  bootStatus() {
			
			firmware();
			
			return toStr(firmware.get("boot_status"));
			
		}
		public String  bootVersion() {
			
			firmware();
			
			return toStr(firmware.get("boot_version"));
			
		}
		/**
		 * Automatic update:
		 * </b>
		 * <br>Automatic update can be switched off.
		 * <br>Beware, the module will keep it's current firmware and will not be updated if this is OFF.
		 * <br>We do not advise to switch this off.
		 */
		public boolean autoUpdate() {
			
			firmware();
			
			return toBool(firmware.get("auto_update"));
			
		}
		/**
		 * Auto Docking:
		 * </b>
		 * <p>If Roomba is in front of a homebase but not on the homebase, Thinking Cleaner
		 * will redirect Roomba to its homebase automatically.</p>
		 * <p>This is to keep Roomba charged. Because the Thinking Cleaner also takes a bit of current from the
		 * Roomba battery, it will be empty sooner then without Thinking Cleaner.</p>
		 */
		public boolean autoDock() {
			
			firmware();
			
			return toBool(firmware.get("auto_dock"));
			
		}
		/**
		 * Restart charged:
		 * <i>ON/OFF</i></b>
		 * <p>When you use the MAX Clean command you can choose for an automatic
		 * second start after the Roomba is fully charged.
		 * This way rooms up to 150m^2 can be kept clean.</p>
		 * <p>You will have to place several docking stations and you should set
		 * "Start docking at" to 25%. </p>
		 */
		public boolean restartAfterCharge() {
			
			firmware();
			
			return toBool(firmware.get("restart_AC"));
			
		}
		/**
		 * Start docking at: xx % charge
		 * <br>Roomba will start to look for its docking station below this set battery value.
		 * <br>If your Roomba often does not reach its dock then increase this value.
		 */
		public int     dockAt() {
			
			firmware();
			
			return toInt(firmware.get("dock_at"));
			
		}
		/**
		 * Stop Roomba below: xx % charge
		 * <br>To save Roombas battery it will stop cleaning below this setting.
		 */
		public int     stopAt() {
			
			firmware();
			
			return toInt(firmware.get("stop_at"));
			
		}
		/**
		 * Returns the current time in a 24 hour format as a string.
		 */
		public String  getTime() {
			
			firmware();
			
			return toStr(firmware.get("time_h_m"));
			
		}
	/**
	 * Used to access the Thinking Cleaner status JSON object so new data can
	 * accessed by developers if it cannot already be accessed via a method.
	 */
	public JSONObject  TCStatus() {
		
		if(tc_status==null)
			statusUpdate(true);
		
		return tc_status;
		
	}
		/**
		 * This is used to fetch the name of the Thinking Cleaner device.
		 */
		public String  TCName() {
			
			TCStatus();
			
			return toStr(tc_status.get("name"));
			
		}
		public String  TCModelNumber() {
			
			TCStatus();
			
			return toStr(tc_status.get("modelnr"));
			
		}
		/**
		 * Last cleaning took:
		 * </b>
		 * <p>The time Roomba has been cleaning without being picked up.</p>
		 */
		public String  lastCleaningTime() {
			
			TCStatus();
			
			return toStr(tc_status.get("cleaning_time"));
			
		}
		public String  totalCleaningTime() {
			
			TCStatus();
			
			return toStr(tc_status.get("cleaning_time_total"));
			
		}
		public String  totalCleaningDistance() {
			
			TCStatus();
			
			return toStr(tc_status.get("cleaning_distance"));
			
		}
		/**
		 * Dirt detected:
		 * </b>
		 * <p>The number of times that the Roomba has detected dirt.
		 * The Bin status message uses this to calculate the time
		 * for a bin warning.
		 */
		public int     dirtDetections() {
			
			TCStatus();
			
			return toInt(tc_status.get("dirt_detected"));
			
		}
		/**
		 * Bin status:
		 * </b>
		 * <p>If the Roomba has been cleaning more than 90 up to 120 minutes
		 * and has not been picked up, this will return true to indicate
		 * that it is time to empty the bin.</p>
		 */
		public boolean needsEmptied() {
			
			TCStatus();
			
			return toBool(tc_status.get("bin_status"));
			
		}
		public boolean getServerConnection() {
			
			TCStatus();
			
			return toBool(tc_status.get("server_connection"));
			
		}
		/**
		 * Drive vacuum:
		 * </b>
		 * <p>The vacuum motor is normally off in remote drive.
		 * The reason for this switch is that wifi reception is better with this vacuum motor off.</p>
		 */
		public boolean getVacuumDriveStatus() {
			
			TCStatus();
			
			return toBool(tc_status.get("vacuum_drive"));
			
		}
		public int     getCleanDelay() {
			
			TCStatus();
			
			return toInt(tc_status.get("clean_delay"));
			
		}
		public boolean isCleaning() {
			
			// If no information has been retrieved yet.
			if(!useFullStatus && !useSimpleStatus)
				statusUpdate(false);
			
			
			boolean cleaning = false;
			
			if(useFullStatus) {
				cleaning = toBool(power_status.get("cleaning"));
			} else {
				cleaning = toBool(simpleStatus.get("cleaning"));
			}
			
			return cleaning;
			
		}
		/**
		 * This number is incremented every time the schedule is changed.
		 */
		public int     getScheduleSerialNumber() {
			
			// If no information has been retrieved yet.
			if(!useFullStatus && !useSimpleStatus)
				statusUpdate(false);
			
			
			int schedule_serial_number = 0;
			
			if(useFullStatus) {
				schedule_serial_number = toInt(tc_status.get("schedule_serial_number"));
			} else {
				schedule_serial_number = toInt(simpleStatus.get("schedule_serial_number"));
			}
			
			return schedule_serial_number;
			
		}
	public JSONObject  powerStatus() {
		
		if(power_status==null)
			statusUpdate(true);
		
		return power_status;
		
	}
		public String  cleanerState() {
			
			// If no information has been retrieved yet.
			if(!useFullStatus && !useSimpleStatus)
				statusUpdate(false);
			
			
			String state = null;
			
			if(useFullStatus) {
				state = toStr(power_status.get("cleaner_state"));
			} else {
				state = toStr(simpleStatus.get("cleaner_state"));
			}
			
			return state;
			
		}
		/**
		 * Refers to electrical current.
		 */
		public int     current() {
			
			powerStatus();
			
			return toInt(power_status.get("current"));
			
		}
		public int     charge() {
			
			// If no information has been retrieved yet.
			if(!useFullStatus && !useSimpleStatus)
				statusUpdate(false);
			
			
			int charge = 0;
			
			if(useFullStatus) {
				charge = toInt(power_status.get("charge"));
			} else {
				charge = toInt(simpleStatus.get("charge"));
			}
			
			return charge;
			
		}	
		public int     batteryCharge() {
			
			powerStatus();
			
			return toInt(power_status.get("battery_charge"));
			
		}
		public int     capacity() {
			
			// If no information has been retrieved yet.
			if(!useFullStatus && !useSimpleStatus)
				statusUpdate(false);
			
			
			int capacity = 0;
			
			if(useFullStatus) {
				capacity = toInt(simpleStatus.get("capacity"));
			} else {
				capacity = toInt(simpleStatus.get("capacity"));
			}
			
			return capacity;
			
		}
		public int     voltage() {
			
			powerStatus();
			
			return toInt(power_status.get("voltage"));
			
		}
		public int     temperature() {
			
			powerStatus();
			
			return toInt(power_status.get("temperature"));
			
		}
		public String  batteryCondition() {
			
			powerStatus();
			
			return toStr(power_status.get("cleaner_state"));
			
		}
		public boolean isLowOnPower() {
			
			powerStatus();
			
			return toBool(power_status.get("low_power"));
			
		}
	public JSONObject  buttonData() {
		
		if(buttons==null)
			statusUpdate(true);
		
		return buttons;
		
	}
		public boolean buttonPressed(String buttonName) {
			
			boolean clean_button = toBool(buttons.get("clean_button"));
			boolean spot_button  = toBool(buttons.get("spot_button"));
			boolean dock_button  = toBool(buttons.get("dock_button"));

			
			boolean buttonPressed = false;
			
			buttonName = buttonName.toLowerCase();
			
			switch(buttonName) {
				case "a": case "any": case "all":
					buttonPressed = clean_button || spot_button || dock_button;
					break;
				
				case "c": case "cln": case "clean": case "clean button":
					buttonPressed = clean_button;
					break;
					
				case "s": case "spt": case "spot": case "spot button":
					buttonPressed = spot_button;
					break;
					
				case "d": case "dck": case "dock": case "dock button":
					buttonPressed = dock_button;
					break;
			}
			
			return buttonPressed;

			
			
		}
	public JSONObject  sensors() {
		
		return sensors;
		
	}
		public boolean bumperPressed(String bumperLocation) {
			
			sensors();
			
			boolean bumper_state 	 = toBool(sensors.get("bumper_state"));
			boolean bumper_left_state = toBool(sensors.get("bumper_left_state"));
			boolean bumper_right_state = toBool(sensors.get("bumper_right_state"));
			
			boolean bumperState = false;
			
			bumperLocation = bumperLocation.toLowerCase();
			
			switch(bumperLocation) {
				case "any": case "all": case "a":
					bumperState = bumper_state;
					break;
					
				case "left": case "l":
					bumperState = bumper_left_state;
					break;
					
				case "right": case "r":
					bumperState = bumper_right_state;
					break;
			}
			
			return bumperState;
			
		}
		public boolean wheelDropped(String wheelLocation) {
			
			sensors();
			
			boolean wheel_drop_left = toBool(sensors.get("wheel_drop_left"));
			boolean wheel_drop_right = toBool(sensors.get("wheel_drop_right"));
			
			boolean wheelState = false;
			
			wheelLocation = wheelLocation.toLowerCase();
			
			switch(wheelLocation) {
				case "any": case "all": case "a":
					wheelState = wheel_drop_left || wheel_drop_right;
					break;
					
				case "left": case "l":
					wheelState = wheel_drop_left;
					break;
					
				case "right": case "r":
					wheelState = wheel_drop_right;
					break;
			}
			
			return wheelState;
			
		}
		/**
		 * Allows the robot to detect a wall in front of it.
		 * </p>
		 * @param virtual - Check for a virtual wall?
		 * @return Whether or not a wall is detected.
		 */
		public boolean detectsWall(boolean virtual) {
			
			sensors();
			
			boolean wall = toBool(sensors.get("wall"));
			boolean virtual_wall = toBool(sensors.get("virtual_wall"));
			
			boolean wallDetected = virtual ? virtual_wall : wall;
			
			return wallDetected;
		
		}
		/**
		 * Allows the robot to check cliff is present on the
		 * <br>
		 * "left", "front left", "front right", "right", or "any" side.</p>
		 * 
		 * @param cliffLocation
		 * @return cliffState
		 */
		public boolean detectsCliff(String cliffLocation) {
			
			sensors();
			
			boolean cliffState = false;
			
			cliffLocation = cliffLocation.toLowerCase();
			
			switch(cliffLocation) {
				case "any": case "all": case "a":
					boolean cliff_left 		  = toBool(sensors.get("cliff_left"));
					boolean cliff_front_left  = toBool(sensors.get("cliff_front_left"));
					boolean cliff_right 	  = toBool(sensors.get("cliff_right"));
					boolean cliff_front_right = toBool(sensors.get("cliff_front_right"));
					cliffState = cliff_left || cliff_right || cliff_front_left || cliff_front_right;
					break;
					
				case "left": case "l":
					cliffState = toBool(sensors.get("cliff_left"));
					break;
					
				case "front left": case "fl":
					cliffState = toBool(sensors.get("cliff_front_left"));
					break;
					
				case "front right": case "fr":
					cliffState = toBool(sensors.get("cliff_front_right"));
					break;
					
				case "right": case "r":
					cliffState = toBool(sensors.get("cliff_right"));
					break;
			}
			
			return cliffState;
			
		}
		public boolean detectsDirt() {
			
			sensors();
			
			return toBool(sensors.get("dirt_detect"));
			
		}
		public boolean detectsLightBump() {
			
			sensors();
			
			return toBool(sensors.get("light_bump"));
			
		}
		public boolean hasMainbrushCurrent() {
			
			sensors();
			
			return toBool(sensors.get("mainbrush_current"));
			
		}
		public boolean hasSidebrushCurrent() {
			
			sensors();
			
			return toBool(sensors.get("sidebrush_current"));
			
		}
		public boolean detectsHomebase() {
			
			sensors();
			
			return toBool(sensors.get("homebase_detected"));
			
		}		
		public boolean isNearHomebase() {
			
			// If no information has been retrieved yet.
			if(!useSimpleStatus && !useFullStatus)
				statusUpdate(false);
			
			
			boolean near_homebase = false;
			
			if(useFullStatus) {
				near_homebase = toBool(sensors.get("near_homebase"));
			} else {
				near_homebase = toBool(simpleStatus.get("near_homebase"));
			}
			
			return near_homebase;
			
		}
		public int     getIRReadings(String IRLocation) {
			
			sensors();
			
			int IRReadings = 0;
			
			IRLocation = IRLocation.toLowerCase();
			
			switch(IRLocation) {
				case "omni": case "o":
					IRReadings = toInt(sensors.get("IR_Omni" ));
					break;
					
				case "left": case "l":
					IRReadings = toInt(sensors.get("IR_Left" ));
					break;
					
				case "right": case "r":
					IRReadings = toInt(sensors.get("IR_Right"));
					break;
			}
			
			return IRReadings;
			
		}
		public String  getWebviewAdvanced() {
			
			if(webview==null)
				statusUpdate(true);
			
			return toStr(webview.get("advanced"));
			
		}
	
	
	// Utility Methods \\
	/**
	 * Fetches a JSON response from a URL
	 * @return  Website response as a JSON object.
	 */
	private JSONObject getJSON(String url) {
		
		String rawData;
		JSONObject JSONData = null;
		
		try {
			rawData = IOUtils.toString(new URL(url));
			JSONData = (JSONObject) JSONValue.parseWithException(rawData);
		} catch (IOException | ParseException e) {}
		
	    return JSONData;
	    
	}
	
	/**
	 * Pass any single custom command along to the Robot programmatically.
	 * @param command The command that is to be passed to the Thinking Cleaner web API.
	 */
	public void send(String command)  {
		
		String url = "http://" + ip + '/' + command;
		try {
			new URL(url).openConnection().getInputStream().close();
			Thread.sleep(DELAY);
		} catch (InterruptedException | IOException e1){}
		
	}
	
	/**
	 * Pass a single custom command along to the Robot programmatically which will then be repeated for a specified length of time.
	 * @param command The command that is to be passed to the Thinking Cleaner web API.
	 * @param duration The length of time in milliseconds that the command should be repeated.
	 */
	public void send(String command, int duration)  {
		
		// Do NOT use http://irobot2.local/, this has to go through Bonjour which
		// is slower than frozen ****. Fu Steve Jobs, even if you are Magneto.
		
		long endTime = DELAY + 50; // Rough estimate for the amount of time the operation will take
		long startTime = System.currentTimeMillis();
		
		// This might overshoot any time you provide.
		while(duration + startTime > endTime) {
			
			send(command);
			
			endTime = System.currentTimeMillis(); // Roughly startTime + DELAY + 50 at last calculation
			duration -= endTime - startTime;
			System.out.println("Time remaining: " + duration/1000);
			startTime = System.currentTimeMillis();
		
		}
		
		// Signal shutdown
		try {
			String ceaseURL   = "http://" + ip + "/command.json?command=drivestop";
			new URL(ceaseURL).openConnection().getInputStream().close();
			Thread.sleep(DELAY);
		} catch (InterruptedException | IOException e) {}
		
	}
	
	/**
	 * Pass a command, specifically, along to the Robot.
	 */
	public void generalSend(String genericCommand) {
		
		String url = "http://" + ip + "/command.json?command=" + genericCommand;
		
		try {
			new URL(url).openConnection().getInputStream().close();
			Thread.sleep(DELAY);
		} catch (InterruptedException | IOException e) {}
		
	}
	
	/**
	 * Converts the "0" or "1" values returned by the JSON response objects into false and true values, respectively.
	 * @param binaryObject
	 * @return The boolean representation of the object's binary String value.
	 */
	private boolean toBool(Object binaryObject) {
		
		return toStr(binaryObject).equals("0") ? false : true;
		
	}
	
	/**
	 * Converts the String integer values returned by the JSON response objects into integers.
	 * @param integerObject
	 * @return The integer representation of the object's String integer value.
	 */
	private int toInt(Object integerObject) {
		
		return Integer.parseInt( toStr(integerObject) );
		
	}
	
	/**
	 * Converts the object returned by the JSON response objects into a string.
	 * </p>
	 * This exists mostly to make it easier to modify the API in the future and to fix possible bugs.
	 * </p>
	 * @param stringObject
	 * @return The String representation of the object.
	 */
	private String toStr(Object stringObject) {
		
		return stringObject.toString();
		
	}
	
	/**
	 * For all those Frankensteins out there. GL HF, but don't kill anybody.
	 * </p>
	 * Currently, this will make the robot drive forward and swerve left and right at variable speeds<br>
	 */
	@SuppressWarnings("unused")
	private void test(int speed) {
		
		int degree = 180;
		String command = "command.json?command=drive_only&degrees=" + degree + "&speed=" + speed;
		String ceaseURL   = "http://" + ip + "/command.json?command=drivestop";
		String commandURL = "http://" + ip + '/' + command;
		URL obj = null;
		URL stopObj = null;
		try {
			obj = new URL(commandURL);
			stopObj = new URL(ceaseURL);
		} catch (MalformedURLException e4) {}
		long endTime = DELAY + 50; // Rough estimate for the amount of time the operation will take
		long startTime = System.nanoTime();
		
		Double duration = Double.POSITIVE_INFINITY;
		
		while(duration-(endTime - startTime)/1e6 > 0) { // This will probably overshoot any time you provide
			
			degree += 10;
			degree%=360;
			System.out.println(degree);
			HttpURLConnection con;
			commandURL = "http://" + ip + "/command.json?command=drive_only&degrees=" + degree + "&speed=100";
			try {
				obj = new URL(commandURL);
				con = (HttpURLConnection) obj.openConnection();
				con.connect();
				con.getInputStream().close();
				Thread.sleep(DELAY);
			} catch (InterruptedException | IOException e1){}
			
			endTime = System.nanoTime(); // Roughly startTime + DELAY + 50
			duration -= (endTime - startTime)/1e6;
			System.out.println("Time remaining: " + duration/1000);
		
		}
			
			// Signal shutdown
			HttpURLConnection con;
			try {
				con = (HttpURLConnection) stopObj.openConnection();
				con.connect();
				con.disconnect();
				con.getInputStream().close();
			} catch (IOException e1) {}

	}
	
	/**
	 * Fetch and provides a human-readable translation for the current state of the Robot.
	 */
	public String readableCleanerState() {
				
		String state = cleanerState();
		String translation;

		switch(state) {
			case "st_base":
				translation = "I'm at the homebase, not doing anything.";
				break;
				
			case "st_base_recon":
				translation = "I'm at the homebase, doing some reconditioning charging.";
				break;
				
			case "st_base_full":
				translation = "I'm at the homebase, charging.";
				break;
				
			case "st_base_trickle":
				translation = "I'm at the homebase, doing a little charging.";
				break;
				
			case "st_base_wait":
				translation = "I'm waiting at the homebase, tell me if you want to me to do something.";
				break;
				
			case "st_plug":
				translation = "I'm plugged in, not doing much of anything.";
				break;
				
			case "st_plug_recon":
				translation = "I'm plugged in, doing a little reconditioning charging.";
				break;
				
			case "st_plug_full":
				translation = "I'm plugged in and charging.";
				break;
				
			case "st_plug_trickle":
				translation = "I'm plugged in and doing a little charging.";
				break;
				
			case "st_plug_wait":
				translation = "I'm plugged in, and ready for you to tell me what to do.";
				break;
				
			case "st_stopped":
				translation = "I've stopped.";
				break;
				
			case "st_clean":
				translation = "I'm cleaning right now.";
				break;
				
			case "st_cleanstop":
				translation = "I'm just getting done with some cleaning.";
				break;
				
			case "st_clean_spot":
				translation = "I'm cleaning this spot, it's a bit dirty.";
				break;
				
			case "st_clean_max":
				translation = "I'm cleaning as much as I can.";
				break;
				
			case "st_delayed":
				translation = "I'm about to start cleaning.";
				break;
				
			case "st_dock":
				translation = "I'm looking for the homebase, could you give me a push in the right direction?";
				break;
				
			case "st_pickup":
				translation = "Put me down! I get dizzy when you pick me up.";
				break;
				
			case "st_remote":
				translation = "I'm being moved remotely and I don't like it.";
				break;
				
			case "st_wait":
				translation = "I'm bored, don't you have anything for me to do?";
				break;
				
			case "st_off":
				translation = "Not so loud. I'm sleeping.";
				break;
				
			case "st_error":
				translation = "I think there's something wrong, can you check?";
				break;
				
			case "st_locate":
				translation = "I'm lost, please find me.";
				break;
				
			case "st_unknown":
				translation = "I don't really know what's going on.";
				break;
				
			default:
				translation = "I may or may not be doing something right now.";
				break;
		}
		
		return translation;
		
	}
	
	
	// Basic Movement Methods \\
	/**
	 * Drive with a given direction and speed.
	 * <br>Brush and vacuum motors are off
	 * @param speed Between -500 mm/s and 500 mm/s
	 * @param degrees - 
	 * <br>0 : spinright
	 * <br>1 to 179 : right turn, the lower is more turn
	 * <br>180 : forward
	 * <br>181 to 359 : left turn, the higher is more turn
	 * <br>360 : spinleft
	 */
	public void drive(int speed, int degrees) {
				
		String commandURL = "drive_only&degrees=" + degrees + "&speed=" + speed;
		generalSend(commandURL);
		
	}
	
	/**
	 * Drive forwards for one second.
	 * <br>
	 * Repeat every 0.5 seconds to drive continuously.
	 */
	public void forward() {
		
		String command = "forward";
		generalSend(command);

	}
	
	/**
	 * Drive forwards with a given speed.
	 * <br>Brush and vacuum motors are off
	 * @param speed Between -500 mm/s and 500 mm/s
	 */
	public void forward(int speed) {
				
		String command = "drive_only&degrees=180&speed=" + speed;
		
		generalSend(command);
		
	}
	
	/**
	 * Drive backwards for one second.
	 * <br>
	 * Repeat every 0.5 seconds to drive continuously.
	 */
	public void backward() {
		
		String command = "drive_only&degrees=180&speed=-250";

		generalSend(command);
		
	}
	
	/**
	 * Drive backwards with a given speed.
	 * <br>Brush and vacuum motors are off
	 * @param speed Between -500 mm/s and 500 mm/s
	 */
	public void backward(int speed) {
		
		String command = "drive_only&degrees=180&speed=-" + speed;

		generalSend(command);
		
	}
	
	/**
	 * Spin to the left by ~90 degrees.
	 */
	public void left() {
		
		String command = "spinleft";
		generalSend(command);
		
	}
	
	/**
	 * Spin to the left at ~speed mm/s.
	 */
	public void left(int speed) {
		
		String command = "drive_only&degrees=360&speed=" + speed;
		generalSend(command);
		
	}
	
	/**
	 * Spin to the right by ~90 degrees.
	 */
	public void right() {
		
		String command = "spinright";
		generalSend(command);
		
	}
	
	/**
	 * Spin to the right at ~speed mm/s.
	 */
	public void right(int speed) {
		
		String command = "drive_only&degrees=0&speed=" + speed;
		generalSend(command);
		
	}
	
	/**
	 * Rotate ~180 degrees.
	 * <p>Still in development.</p>
	 */
	public void turnAround() {
		
		left();
		
		try {
			Thread.sleep(DELAY);
		} catch (InterruptedException e) {}
		
		left();
		
	}
	
	/**
	 * Drive forward and left.
	 */
	public void driveLeft() {
		
		String command = "driveleft";
		generalSend(command);
		
	}
	
	/**
	 * Drive forward and right.
	 */
	public void driveRight() {
		
		String command = "driveright";
		generalSend(command);
		
	}
	
	/**
	 * Stop driving.
	 */
	public void stop() {
		
		String command = "drivestop";
		generalSend(command);
		
	}
	
	
	// Advanced Functions \\
	/**
	 * Go back to the docking station.
	 * </p>
	 * When Roomba is sleeping this command will wake Roomba and execute the Dock command;
	 * when the Roomba is cleaning this command will stop the cleaning cycle and execute the dock command.</p>
	 */
	public void dock() {
		
		String command = "dock";
		generalSend(command);
		
	}
	
	/**
	 * Drive backwards then turn around.
	 * <br>
	 * It is meant to be used for leaving the dock.
	 */
	public void leaveHome() {
		
		String command = "leavehomebase";
		generalSend(command);
		
	}
	
	/**
	 * Play a short tune.
	 */
	public void beep() {
		
		String command = "find_me";
		generalSend(command);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {}
		
	}
	
	/**
	 * Turn off.
	 */
	public void shutdown() {
		
		String command = "poweroff";
		generalSend(command);
		
	}
	
	
	// Mode Control \\
	/**
	 * Controls the vacuum and brushes.
	 * <br>
	 * Default mode will drive your Roomba without the vacuum and brushes.
	 * @param newMode Can be "on", "off", or "toggle".
	 */
	public void vacuum(String newMode) {
		
		newMode = newMode.toLowerCase();
		
		String command = null;
		
		switch(newMode) {
			case "on":
				command = "VacuumDriveON";
				break;
				
			case "off":
				command = "VacuumDriveOFF";
				break;
				
			case "toggle":
				if(getVacuumDriveStatus()) {
					command = "VacuumDriveON";
				} else {
					command = "VacuumDriveOFF";
				}
				break;
		}
		
		generalSend(command);
		
	}
	
	/**
	 * Control the conditions in which the Roomba will drive.
	 * <br>
	 * In cautious mode Roomba will stop driving as soon as it detects a situation where continuing to drive would be dangerous.
	 * <p>For example, this will stop the Roomba from driving down stairs or when it's picked up.</p>
	 * 
	 * In hazardous mode, the Roomba will drive even when doing so could be dangerous.
	 * Be careful, you can drive it down the stairs if you use this!</p>
	 * This setting is reset automatically when Roomba is docked or charging.
	 * 
	 * @param newMode Can be "cautious", "hazardous", or "toggle".
	 */
	public void mode(String newMode) {
		
		newMode = newMode.toLowerCase();
		
		String command = null;
		
		switch(newMode) {
			case "cautious":
				command = cautiousMode;
				break;
				
			case "hazardous":
				command = hazardousMode;
				break;
				
			case "toggle":
				if(cautious) {
					command = hazardousMode;
				} else {
					command = cautiousMode;
				}
				cautious = !cautious;
				break;
		}
		
		generalSend(command);
		
	}
	
	
	// Cleaning Methods \\
	/**
	 * Starts or stops a cleaning cycle.
	 * </p>
	 * If the Roomba is sleeping this command will wake Roomba first and execute the Dock command.<br>
	 */
	public void clean() {
		
		String command = "clean";
		generalSend(command);
		
	}
		
	/**
	 * Performs a clean of the local area.
	 */
	public void spotClean() {
		
		String command = "spot";
		generalSend(command);
		
	}
	
	/**
	 * Starts a max cleaning cycle on the Roomba.
	 * </p>
	 * <p>In large rooms Roomba will often stop cleaning in the middle of the room.
	 * <br>Every 8 meters Roomba will turn and after three times with no virtual wall or a real life obstacle,
	 * <br>Roomba will go into an error state and stop cleaning.</p>
	 * 
	 * <p>In the Thinking Cleaner max mode this error is overruled and Roomba will start again automatically.
	 * <br>If you let your Roomba go outside, it will not stop!
	 * <br>Please only use this max mode when Roomba cannot escape from the building it is in. </p>
	 */
	public void maxClean() {
				
		String command = "max";
		generalSend(command);
		
	}
	
	/**
	 * Starts a cleaning cycle after a preset number of minutes.
	 */
	public void delayedClean() {
		
		String command = "delayedclean";
		generalSend(command);
		
	}
	
	/**
	 * Starts a cleaning cycle after a given number of minutes.
	 * @param minutes Must be an integer between 30 or 240.
	 */
	public void delayedClean(int minutes) {
				
		if(minutes<30 || minutes>240)
			return;
		
		String command = "DelayedClean&minutes=" + minutes;
		generalSend(command);
		
	}
	
	
	// Schedule Methods \\
	
	// NOT IMPLEMENTED.
	
	/**
	 * This will be executed each time a program starts up the Robot class.
	 * <br>
	 * Ensures that the Robot is ready and waiting to receive its first command.
	 * <p>Still in development.</p>
	 */
	public static void startUp(String ip) {		
		
		String commandURL = "http://" + ip + "/status.json";
		
		try {
			URL obj = new URL(commandURL);
			obj.openConnection().getInputStream().close();
			Thread.sleep(DELAY);
		} catch (InterruptedException | IOException e) {}
        
    }

}
