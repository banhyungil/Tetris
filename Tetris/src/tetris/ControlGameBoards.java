package tetris;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;

import javax.swing.JPanel;

import tetEnum.Player;
import tetEnum.TetKey;


/**
 * Player1, 2를 컨트롤하기위한 클래스, 키 입력을 양쪽  player에 전달하는 역할
 * @author ban
 *
 */
public class ControlGameBoards extends JPanel{
	GameBoard player1;
	GameBoard player2;
	ControlUI cUI;
	
	public ControlGameBoards(GameBoard player1, GameBoard player2, ControlUI cUI) {
		this.player1 = player1;
		this.player2 = player2;
		this.cUI = cUI;
		addKeyListener(new TetKeyAdapter());
		System.out.println("controlGameBoard");
	}
	
	public void start() {
		player1.start();
		player2.start();
	}
	
	/**
	 * 메시지 교환을 위해 자신의 객체를 Board에 전달한다. 
	 */
	public void setBoards() {
		player1.setControlBoards(this);
		player2.setControlBoards(this);
	}
	
	public void attackPlayer(GameBoard player) {
		if(player.equals(player1))
			player2.createBadBlock();
		else
			player1.createBadBlock();
	}
	
	/**
	 * 승리한 Player 설정 후 화면전환
	 * @param endPlayer
	 */
	public void endGame(GameBoard endPlayer) {		
		player1.gameStop();
		player2.gameStop();
		
		if(endPlayer.equals(player1))
			cUI.setWinnerPlayer(Player.Player2);
		else
			cUI.setWinnerPlayer(Player.Player1);
		
//		Timer timer = new Timer() {
//			
//		}
		//유저아이디 입력화면으로
		JPanel jp = cUI.getEndPanel();
		cUI.changePanel(jp);
	}
	
	class TetKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			super.keyPressed(e);
			
			int keyCord = e.getKeyCode();
			
			System.out.println("key");
			switch(keyCord){
			case KeyEvent.VK_D: 
				player1.keyProcess(TetKey.Left);
				break;
			case KeyEvent.VK_G:
				player1.keyProcess(TetKey.Right);
				break;
			case KeyEvent.VK_Q:
				player1.keyProcess(TetKey.Down);
				break;
			case KeyEvent.VK_R:
				player1.keyProcess(TetKey.RotateLeft);
				break;
			case KeyEvent.VK_F:
				player1.keyProcess(TetKey.RotateRight);
				break;
			case KeyEvent.VK_A:
				player1.keyProcess(TetKey.Drop);
				break;
			case KeyEvent.VK_LEFT:
				player2.keyProcess(TetKey.Left);
				break;
			case KeyEvent.VK_RIGHT:
				player2.keyProcess(TetKey.Right);
				break;
			case KeyEvent.VK_COMMA:					//쉼표
				player2.keyProcess(TetKey.Down);
				break;
			case KeyEvent.VK_UP:
				player2.keyProcess(TetKey.RotateLeft);
				break;
			case KeyEvent.VK_DOWN:
				player2.keyProcess(TetKey.RotateRight);
				break;
			case KeyEvent.VK_PERIOD:				//마침표
				player2.keyProcess(TetKey.Drop);
				break;
			case KeyEvent.VK_P:
				player1.keyProcess(TetKey.Pause);
				player2.keyProcess(TetKey.Pause);
				break;
			case KeyEvent.VK_1:
				attackPlayer(player2);
				break;
			case KeyEvent.VK_2:
				attackPlayer(player1);
				break;
			}
		}
	}
}
