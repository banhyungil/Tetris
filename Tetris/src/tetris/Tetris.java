package tetris;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;


/*
 *  코드 출처 http://zetcode.com/tutorials/javagamestutorial/tetris/
 */
public class Tetris extends JFrame {

	JLabel statusbar;
	
	final int frameWidth = 1000;
	final int frameHeight = 1000;
	
	public Tetris() {

		statusbar = new JLabel(" 0");
		add(statusbar, BorderLayout.SOUTH);
		
		setSize(frameWidth, frameHeight);
		setTitle("Tetris");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public JLabel getStatusBar() {
		return statusbar;
	}
	
	public void start() {
		ControlUI cUI = new ControlUI(frameWidth, frameHeight, this);
		cUI.start();
	}
	
	public static void main(String[] args) {
		Tetris game = new Tetris();
		game.setLocationRelativeTo(null);
		game.setVisible(true);
		game.start();
	}
}