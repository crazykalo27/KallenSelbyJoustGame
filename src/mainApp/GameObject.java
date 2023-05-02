package mainApp;

import java.awt.Graphics2D;

public class GameObject {
	private int xCent;
	private int yCent;
	
	public GameObject() {
		
	}
	
	public GameObject(int xCord,int yCord) {
			this.xCent = xCord;
			this.yCent = yCord;
	}

	public void drawOn(Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}

	public int getXCent() {
		return xCent;
	}

	public void setXCent(int xCent) {
		this.xCent = xCent;
	}

	public int getYCent() {
		return yCent;
	}

	public void setYCent(int yCent) {
		this.yCent = yCent;
	}

}
