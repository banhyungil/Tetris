package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import tetEnum.TetKey;
import tetEnum.Tetrominoes;

public class GameBoard extends JPanel implements ActionListener {

	/**
	 * ������ ��Ʈ���� �ʵ��� ���� ����
	 */
	final int BoardWidth = 10;
	/**
	 * ������ ��Ʈ���� �ʵ��� ���� ����
	 */
	final int BoardHeight = 22;
	/**
	 * timer�� delay�ð��� ���� �ӵ� ��� ����, ����  : ��
	 */
	final int initDelayTime = 400;
	/**
	 * time�� delay�ð� ���� ����, level up �� ���, ���� : ��
	 */
	final int speedUpTime = 40;
	/**
	 * level up �ϱ���� �ð� Term
	 */
	final int levelUpTimeTerm = 10000;
	
	int delayTime = initDelayTime;
	Timer timer;
	boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;
	/**
	 * ������ ���� ī��Ʈ - ���� ���
	 */
	int numLinesRemoved = 0;
	/**
	 * ����� X ��ǥ
	 */
	int curX = 0;
	/**
	 * �ҷ��� Y ��ǥ
	 */
	int curY = 0;
	JLabel statusbar;
	/**
	 * ����� ����
	 */
	Shape curPiece;
	/**
	 * ������ ��Ʈ���� �ʵ�
	 */
	Tetrominoes[] board;
	/**
	 * �� ������ ���� ����� �޽��� ��ȯ�� ���� ����
	 */
	ControlGameBoards cBoard;
	/**
	 * �ð��� �帧�� ����, timer�� �������� ����
	 */
	int time = 0;
	/**
	 * 20�ʸ��� level 1����
	 */
	int level = 0;
	
	public GameBoard() {
		
		curPiece = new Shape();
		timer = new Timer(initDelayTime, this); //���� �ð����� actionPerformed �޼ҵ� ����(imp ActionListener) 400 ms
		timer.start();	//Ÿ�̸� ����

		// ��Ʈ���� �ʵ忡�� ���� ǥ�� ���� ����
		//statusbar = parent.getStatusBar();
		
		//���̴� ��Ͽ� ���� ������ �����ϴ� �ʵ� ����
		board = new Tetrominoes[BoardWidth * BoardHeight];
		
		setBorder(new LineBorder(Color.DARK_GRAY));
		
		clearBoard();
	}

	/**
	 * Ÿ�̸ӿ��� ����ϴ� ActionListener�� �����ϴ� �޼ҵ�
	 */
	public void actionPerformed(ActionEvent e) {
		//�������� ���� Ȯ��
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			oneLineDown();
		}
		
		//time ����, level up ���θ� Ȯ���Ѵ�.
		time += delayTime;
		if(time >= levelUpTimeTerm)
			levelUp();
	}
	
	/**
	 * ��Ʈ���� �������� �ӵ� ����, �� ���� ����
	 */
	public void levelUp() {
		//time �ʱ�ȭ
		time = 0;
		
		//�ӵ� ����
		++this.level;
		delayTime -= speedUpTime;
		timer.setDelay(delayTime);
		
		//�� ���� ����
		createBadBlock();
	}
	
	
	/**
	 * ȭ���� ���� / ����ȭ���� ��ü ���� ����
	 * @return ���� ����� ���� ����
	 */
	int squareWidth() {
		return (int) getSize().getWidth() / BoardWidth;
	}

	/**
	 * ȭ���� ���� / ����ȭ���� ��ü ���� ����
	 * @return ���� ����� ���� ����
	 */
	int squareHeight() {
		return (int) getSize().getHeight() / BoardHeight;
	}

	/**
	 * ��Ʈ���� �ʵ忡�� �ش��ϴ� ��ǥ�� ������ �����ϴ� �� Ȯ��
	 * @param x
	 * @param y
	 * @return Tetrominoes 
	 */
	Tetrominoes shapeAt(int x, int y) {
		return board[(y * BoardWidth) + x];
	}

	/**
	 * ��Ʈ���� ������ �����ϴ� �Լ�
	 */
	public void start() {
		if (isPaused)// ���� ������ ��� ����
			return;

		isStarted = true;
		isFallingFinished = false;
		numLinesRemoved = 0;
		clearBoard();

		newPiece();
		timer.start();
	}

	/**
	 * ���� ���� Ȥ�� �ٽ� ����(���� Ǯ��)
	 */
	private void pause() {
		if (!isStarted)
			return;

		isPaused = !isPaused;
		if (isPaused) {
			timer.stop();
		} else {
			timer.start();
		}
		repaint();
	}

	public void paint(Graphics g) {
		super.paint(g);

		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

		//�׿� �ִ� ����� �׸���.
		for (int i = 0; i < BoardHeight; ++i) {
			for (int j = 0; j < BoardWidth; ++j) {
				Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
				if (shape != Tetrominoes.NoShape)
					drawSquare(g, 0 + j * squareWidth(), boardTop + i
							* squareHeight(), shape);
			}
		}

		// ���� �������� �ִ� ��Ʈ���� ����� �׸���.
		if (curPiece.getShape() != Tetrominoes.NoShape) {
			//��Ʈ���� ����� �׸���.
			for (int i = 0; i < 4; ++i) {
				int x = curX + curPiece.x(i);
				int y = curY - curPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(), boardTop
						+ (BoardHeight - y - 1) * squareHeight(),
						curPiece.getShape());
			}
		}
	}

	/**
	 * ��Ʈ���� ��� �ѹ��� ���� Ʈ����
	 */
	private void dropDown() {
		int newY = curY;
		while (newY > 0) {
			if (!tryMove(curPiece, curX, newY - 1))
				break;
			--newY;
		}
		pieceDropped();
	}

	/**
	 * ��� �̵� ó��
	 */
	private void oneLineDown() {
		if (!tryMove(curPiece, curX, curY - 1))
			pieceDropped();
	}

	/**
	 * ��Ʈ���� �ʵ� �ʱ�ȭ
	 */
	private void clearBoard() {
		for (int i = 0; i < BoardHeight * BoardWidth; ++i)
			board[i] = Tetrominoes.NoShape;
	}

	/**
	 * ��Ʈ���� ��� �ױ�
	 */
	private void pieceDropped() {
		for (int i = 0; i < 4; ++i) {	//��� ��翡 ���� ������ǥ�� ����Ͽ� ����� �����Ѵ�.
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BoardWidth) + x] = curPiece.getShape();
			System.out.println("add : "+((y * BoardWidth) + x) + " shape : "+curPiece.getShape());
		}

		removeFullLines();

		if (!isFallingFinished)
			newPiece();
	}

	/**
	 * ���ο� ��Ʈ���� ����� �����Ѵ�.
	 */
	private void newPiece() {
		curPiece.setRandomShape();// ���� ��� ����
		//ó�� ��� ���� ��ġ - ��� �߾�
		curX = BoardWidth / 2 + 1;
		curY = BoardHeight - 1 + curPiece.minY();
		System.out.println("create location x : " + curX+ " y : "+ curY);
		//���� ���� ���� Ȯ��
		if (!tryMove(curPiece, curX, curY)) {
			cBoard.endGame(this);
		}
	}
	
	public void gameStop() {
		curPiece.setShape(Tetrominoes.NoShape);
		timer.stop();
		isStarted = false;
	}
	/**
	 * ��� �̵� �õ�
	 * @param newPiece
	 * @param newX
	 * @param newY
	 * @return ������ ���� ��� true, ���� �߻��� false
	 */
	private boolean tryMove(Shape newPiece, int newX, int newY) {
		//4���� ���� ��� Ȯ��
		for (int i = 0; i < 4; ++i) {
			//���� ��ǥ 0, 0 �� ��� �����̴�.0
			int x = newX + newPiece.x(i);//�翷�̵�
			int y = newY - newPiece.y(i);//���Ʒ� �̵�
			//xȤ�� y�� ��, �Ʒ�, �� ������ �Ѿ�� �ʴ��� Ȯ��
			if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			//x y ��ǥ�� ����� �ִ��� Ȯ��
			if (shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();//�ٽ� �׸���
		
		return true;
	}

	private void removeFullLines() {
		
		
		boolean isLineRemoved = false;

		for (int i = BoardHeight - 1; i >= 0; --i) {			//������ ���� Ž���ؼ� ����� �� ���ִ� ���� Ȯ���Ѵ�.
			boolean lineIsFull = true;

			for (int j = 0; j < BoardWidth; ++j) {
				if (shapeAt(j, i) == Tetrominoes.NoShape) {		//�� ���� �ϳ��� �ִٸ� ���� ���� Ž��
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) {									//������ �ϼ����ִٸ�
				isLineRemoved = true;
				++numLinesRemoved;
				
				for (int k = i; k < BoardHeight - 1; ++k) {					//�ش� ���κ��� �� �� �Ʒ��� ����������
					for (int j = 0; j < BoardWidth; ++j)
						board[(k * BoardWidth) + j] = shapeAt(j, k + 1);	
				}
				
				System.out.println("RemovedLine : " + numLinesRemoved);
				//3�� ������ ���� �����ϵ���
				
				if(numLinesRemoved % 3 == 0) {
					cBoard.attackPlayer(this);
				}
			}
		}//1 for end

		if (isLineRemoved) {
			statusbar.setText(String.valueOf(numLinesRemoved));
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NoShape);
			repaint();
		}
	}
	
	/**
	 * 
	 * �� �Ʒ��ٿ� ������ �߰��Ѵ�.
	 */
	public void createBadBlock() {
		System.out.println("createBadBlock()");
		Random r = new Random();
		int blankedIndex = Math.abs(r.nextInt()) % BoardWidth;
		
		int y = 0;		//�� �Ʒ���
		
		upLines();		//������ϵ��� �������� �÷�������.
		
		for (int i = 0; i < BoardWidth; ++i) {	//��� ��翡 ���� ���� ��ǥ�� ����Ͽ� ����� �����Ѵ�.
			if(i == blankedIndex)				//���ٿ��� �ϳ��� ����� ����
				continue;
			
			int x = i;
			
			board[(y * BoardWidth) + x] = Tetrominoes.BadShape;
		}	
	}
	
	/**
	 * ��� ������ �������� �̵���Ų��.
	 */
	private void upLines() {	
		for (int y = BoardHeight -2 ; y >= 0 ; --y) {					//�� ���ٺ��� Ž���ذ���.
			for (int x = 0; x < BoardWidth; ++x) {
				board[((y+1)*BoardWidth)+x] = board[(y*BoardWidth)+x];	//��ĭ ��½�Ų��.
			}
		}//for end
		
		for(int x = 0; x < BoardWidth;++x) {	//�� �Ʒ����� ���� ���ش�.
			board[x] =Tetrominoes.NoShape;
		}
	}
	/**
	 * �簢�� ���ڸ� �׸���.
	 * @param g
	 * @param x
	 * @param y
	 * @param shape
	 */
	private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
		//�� ���� ���� ���� ��ġ�� �ش��ϴ� ���� ���� ���ǵǾ� �ִ�.
		Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102),
				new Color(102, 204, 102), new Color(102, 102, 204),
				new Color(204, 204, 102), new Color(204, 102, 204),
				new Color(102, 204, 204), new Color(218, 170, 0), 
				new Color(169, 169, 169)};

		//���õ� ����� �÷� �������� 
		Color color = colors[shape.ordinal()];

		//�簢���� ����
		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		//�簢���� �ܺ� ���, ����
		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y); //���� ��
		g.drawLine(x, y, x + squareWidth() - 1, y); // ��� ��

		//�簢���� �ܺ� �ϴ�, ������
		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y
				+ squareHeight() - 1);//�ϴ�
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x
				+ squareWidth() - 1, y + 1);// ������

	}
	
	public void setControlBoards(ControlGameBoards cBoard) {
		this.cBoard = cBoard;
	}
	/**
	 * Ű �Է� ó��
	 */	
	public void keyProcess(TetKey tetKey) {
		if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
			return;
		}

		if (tetKey == TetKey.Pause) {
			pause();
			return;
		}

		if (isPaused)
			return;

		switch (tetKey) {
		case Left:
			tryMove(curPiece, curX - 1, curY);
			break;
		case Right:
			tryMove(curPiece, curX + 1, curY);
			break;
		case Down:
			oneLineDown();
			break;
		case RotateRight:
			tryMove(curPiece.rotateRight(), curX, curY);
			break;
		case RotateLeft:
			tryMove(curPiece.rotateLeft(), curX, curY);
			break;
		case Drop:
			dropDown();
			break;
		}
	}
}