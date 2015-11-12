package iRoomba;


import iRoomba.Robot;


public class Test {
	
	final static int DELAY = 1500;
	
	/*
	 * To-Do:
	 * 
	 * Fix turn around.
	 * Find way to keep Robot moving quickly near walls
	 * Find some way  to reconcile disconnect between light bumper and wall sensor.
	 * Optimize left/right checks
	 * Get rid of those damn delays.
	 * Have the Robot check left and/or right side at regular intervals for maze
	 * 
	 */
			
	
	
	public static boolean isLeftFree(Robot rob) throws InterruptedException {
		
		boolean leftFree = false;
		
		System.out.println("left free?");
		
		rob.left();
		rob.statusUpdate(true);
		Thread.sleep(DELAY);
		leftFree = !rob.detectsLightBump();
		rob.right();
		System.out.println(leftFree);
		
		return leftFree;
		
		
	}
	
	public static boolean isRightFree(Robot rob) throws InterruptedException {
		
		boolean rightFree = false;
		
		System.out.println("right free?");
		
		Thread.sleep(DELAY);
		rob.right();
		rob.statusUpdate(true);
		Thread.sleep(DELAY);
		rightFree = !rob.detectsLightBump();
		rob.left();
		System.out.println(rightFree);
		
		return rightFree;
		
	}

	

	public static void main(String[] args) throws InterruptedException {
		
		Robot Jim = new Robot("192.168.1.100");
/*		Jim.mode("cautious");
		Jim.vacuum("off");
		
		Jim.statusUpdate(true);
		
		int x = 45;
		int deltaX = x;
		int max = x+(10*deltaX);
		int min = x;
		boolean rememberMe = true;
		
		while(!Jim.buttonPressed("dock")) {
			/*
			Jim.drive(100, x);
			if(rememberMe) {
				x+=deltaX;
				if(x>max) {
					rememberMe = false;
				}
			} else {
				x-=deltaX;
				if(x<min) {
					rememberMe = true;
				}
			}
			
			
			int d = 1700;
			//Jim.turnAround();
			Jim.forward();
			Thread.sleep(d);
			Jim.left();
			Thread.sleep(d);
			Jim.statusUpdate(true);
		}
		
		
		
		/*
		while(true) {
			Jim.statusUpdate(true);
			while(true) {
				System.out.println("is there a wall?");
				boolean wall = Jim.detectsWall(false);
				System.out.println(wall);
				if (wall) {
					break;
				}
				Jim.forward(Speed);
				Jim.statusUpdate(true);
			}
			
			System.out.println("There is a wall, so check the sides.");
			
			if(isLeftFree(Jim)) {
				Jim.left();
				Thread.sleep(DELAY);
				Jim.forward();
			} else if (isRightFree(Jim)) {
					Jim.right();
					Thread.sleep(DELAY);
					Jim.forward();
			} else {
				System.out.println("No sides are free, so turn around.");
				lighter: while(Jim.detectsLightBump()) {
					Jim.left();
					Thread.sleep(DELAY);
					Jim.statusUpdate(true);
					if(Jim.detectsWall(false)){
						Jim.forward();
						break lighter;
					}
					Jim.statusUpdate(true);
				}
			}
			
		}
		*/
		
		// This bit of code allows the Robot to run a basic maze by only ever turning right.
		Jim.statusUpdate(true);
		//Jim.right(400);
		
		while(!Jim.buttonPressed("dock")) {
			
			if(Jim.detectsWall(false)) {
				Thread.sleep(50);
				Jim.forward();
				//!Jim.detectsLightBump()
			}else{
				Jim.forward(100);
				Thread.sleep(900);
				Jim.stop();
				Jim.right();
				Thread.sleep(1000);
				Jim.forward(200);
				Thread.sleep(1000);
				/*while(!Jim.detectsWall(false) && !Jim.buttonPressed("clean")) {
					Thread.sleep(50);
					Jim.right(50);
					Jim.statusUpdate(true);
				}*/
			}
			Jim.statusUpdate(true);
			/*
			if(Jim.detectsLightBump()) {
				int dec = 0;
				if(dec == 0) {
					Jim.right(30);
				} else {
					Jim.left(30);
				}
			} else if(Jim.bumperPressed("any") || Jim.detectsCliff("any")) {
				try {
					Thread.sleep(500);
					Jim.backward(100);
					Thread.sleep(500);
					Jim.right(100);
				} catch (InterruptedException e) {}
			} else {
				Jim.forward(100);
			}
			Jim.statusUpdate(true);
			*/
		}
		

	}


}