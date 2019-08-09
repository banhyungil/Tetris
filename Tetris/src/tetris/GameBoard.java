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
	 * 가상적 테트리스 필드의 가로 길이
	 */
	final int BoardWidth = 10;
	/**
	 * 가상적 테트리스 필드의 세로 길이
	 */
	final int BoardHeight = 22;
	/**
	 * timer의 delay시간을 통해 속도 상승 가능, 단위  : 초
	 */
	final int initDelayTime = 400;
	/**
	 * time의 delay시간 감소 단위, level up 시 사용, 단위 : 초
	 */
	final int speedUpTime = 40;
	/**
	 * level up 하기까지 시간 Term
	 */
	final int levelUpTimeTerm = 10000;
	
	int delayTime = initDelayTime;
	Timer timer;
	boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;
	/**
	 * 제거한 라인 카운트 - 점수 계산
	 */
	int numLinesRemoved = 0;
	/**
	 * 블록의 X 좌표
	 */
	int curX = 0;
	/**
	 * 불록의 Y 좌표
	 */
	int curY = 0;
	JLabel statusbar;
	/**
	 * 블록의 형태
	 */
	Shape curPiece;
	/**
	 * 가상의 테트리스 필드
	 */
	Tetrominoes[] board;
	/**
	 * 줄 삭제시 메인 보드와 메시지 교환을 위해 존재
	 */
	ControlGameBoards cBoard;
	/**
	 * 시간의 흐름을 측정, timer가 돌때마다 증가
	 */
	int time = 0;
	/**
	 * 20초마다 level 1증가
	 */
	int level = 0;
	
	public GameBoard() {
		
		curPiece = new Shape();
		timer = new Timer(initDelayTime, this); //일정 시간마다 actionPerformed 메소드 실행(imp ActionListener) 400 ms
		timer.start();	//타이머 시작

		// 테트리스 필드에서 점수 표기 라벨을 얻어옴
		//statusbar = parent.getStatusBar();
		
		//쌓이는 블록에 대한 정보를 저장하는 필드 생성
		board = new Tetrominoes[BoardWidth * BoardHeight];
		
		setBorder(new LineBorder(Color.DARK_GRAY));
		
		clearBoard();
	}

	/**
	 * 타이머에서 사용하는 ActionListener가 실행하는 메소드
	 */
	public void actionPerformed(ActionEvent e) {
		//떨어지는 상태 확인
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			oneLineDown();
		}
		
		//time 증가, level up 여부를 확인한다.
		time += delayTime;
		if(time >= levelUpTimeTerm)
			levelUp();
	}
	
	/**
	 * 테트리스 내려오는 속도 증가, 한 라인 증가
	 */
	public void levelUp() {
		//time 초기화
		time = 0;
		
		//속도 증가
		++this.level;
		delayTime -= speedUpTime;
		timer.setDelay(delayTime);
		
		//한 라인 증가
		createBadBlock();
	}
	
	
	/**
	 * 화면의 길이 / 게임화면의 전체 가로 길이
	 * @return 작은 블록의 가로 길이
	 */
	int squareWidth() {
		return (int) getSize().getWidth() / BoardWidth;
	}

	/**
	 * 화면의 길이 / 게임화면의 전체 세로 길이
	 * @return 작은 블록의 세로 길이
	 */
	int squareHeight() {
		return (int) getSize().getHeight() / BoardHeight;
	}

	/**
	 * 테트리스 필드에서 해당하는 좌표에 무엇이 존재하는 가 확인
	 * @param x
	 * @param y
	 * @return Tetrominoes 
	 */
	Tetrominoes shapeAt(int x, int y) {
		return board[(y * BoardWidth) + x];
	}

	/**
	 * 테트리스 게임을 시작하는 함수
	 */
	public void start() {
		if (isPaused)// 정지 상태인 경우 정지
			return;

		isStarted = true;
		isFallingFinished = false;
		numLinesRemoved = 0;
		clearBoard();

		newPiece();
		timer.start();
	}

	/**
	 * 게임 정지 혹은 다시 시작(정지 풀기)
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

		//쌓여 있는 블록을 그린다.
		for (int i = 0; i < BoardHeight; ++i) {
			for (int j = 0; j < BoardWidth; ++j) {
				Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
				if (shape != Tetrominoes.NoShape)
					drawSquare(g, 0 + j * squareWidth(), boardTop + i
							* squareHeight(), shape);
			}
		}

		// 현재 떨어지고 있는 테트리스 블록을 그린다.
		if (curPiece.getShape() != Tetrominoes.NoShape) {
			//테트리스 블록을 그린다.
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
	 * 테트리스 블록 한번에 떨어 트리기
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
	 * 블록 이동 처리
	 */
	private void oneLineDown() {
		if (!tryMove(curPiece, curX, curY - 1))
			pieceDropped();
	}

	/**
	 * 테트리스 필드 초기화
	 */
	private void clearBoard() {
		for (int i = 0; i < BoardHeight * BoardWidth; ++i)
			board[i] = Tetrominoes.NoShape;
	}

	/**
	 * 테트리스 블록 쌓기
	 */
	private void pieceDropped() {
		for (int i = 0; i < 4; ++i) {	//블록 모양에 따라 현재좌표와 계산하여 블록을 저장한다.
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
	 * 새로운 테트리스 블록을 생성한다.
	 */
	private void newPiece() {
		curPiece.setRandomShape();// 랜덤 블록 설정
		//처음 블록 생성 위치 - 상단 중앙
		curX = BoardWidth / 2 + 1;
		curY = BoardHeight - 1 + curPiece.minY();
		System.out.println("create location x : " + curX+ " y : "+ curY);
		//게임 오버 조건 확인
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
	 * 블록 이동 시도
	 * @param newPiece
	 * @param newX
	 * @param newY
	 * @return 문제가 없는 경우 true, 문제 발생시 false
	 */
	private boolean tryMove(Shape newPiece, int newX, int newY) {
		//4개의 작은 블록 확인
		for (int i = 0; i < 4; ++i) {
			//최초 좌표 0, 0 은 상단 왼쪽이다.0
			int x = newX + newPiece.x(i);//양옆이동
			int y = newY - newPiece.y(i);//위아래 이동
			//x혹은 y가 위, 아래, 양 옆으로 넘어가지 않는지 확인
			if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			//x y 좌표에 블록이 있는지 확인
			if (shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();//다시 그리기
		
		return true;
	}

	private void removeFullLines() {
		
		
		boolean isLineRemoved = false;

		for (int i = BoardHeight - 1; i >= 0; --i) {			//각각의 줄을 탐색해서 블록이 다 차있는 줄을 확인한다.
			boolean lineIsFull = true;

			for (int j = 0; j < BoardWidth; ++j) {
				if (shapeAt(j, i) == Tetrominoes.NoShape) {		//빈 블럭이 하나라도 있다면 다음 라인 탐색
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) {									//라인이 완성되있다면
				isLineRemoved = true;
				++numLinesRemoved;
				
				for (int k = i; k < BoardHeight - 1; ++k) {					//해당 라인부터 한 줄 아래로 내려보낸다
					for (int j = 0; j < BoardWidth; ++j)
						board[(k * BoardWidth) + j] = shapeAt(j, k + 1);	
				}
				
				System.out.println("RemovedLine : " + numLinesRemoved);
				//3줄 삭제시 배드블럭 등장하도록
				
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
	 * 맨 아랫줄에 한줄을 추가한다.
	 */
	public void createBadBlock() {
		System.out.println("createBadBlock()");
		Random r = new Random();
		int blankedIndex = Math.abs(r.nextInt()) % BoardWidth;
		
		int y = 0;		//맨 아랫줄
		
		upLines();		//기존블록들을 한줄위로 올려보낸다.
		
		for (int i = 0; i < BoardWidth; ++i) {	//블록 모양에 따라 현재 좌표와 계산하여 블록을 저장한다.
			if(i == blankedIndex)				//한줄에서 하나의 블록을 제외
				continue;
			
			int x = i;
			
			board[(y * BoardWidth) + x] = Tetrominoes.BadShape;
		}	
	}
	
	/**
	 * 모든 블럭들을 한줄위로 이동시킨다.
	 */
	private void upLines() {	
		for (int y = BoardHeight -2 ; y >= 0 ; --y) {					//맨 윗줄부터 탐색해간다.
			for (int x = 0; x < BoardWidth; ++x) {
				board[((y+1)*BoardWidth)+x] = board[(y*BoardWidth)+x];	//한칸 상승시킨다.
			}
		}//for end
		
		for(int x = 0; x < BoardWidth;++x) {	//맨 아랫줄은 블럭을 없앤다.
			board[x] =Tetrominoes.NoShape;
		}
	}
	/**
	 * 사각형 상자를 그린다.
	 * @param g
	 * @param x
	 * @param y
	 * @param shape
	 */
	private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
		//색 정의 모양과 같은 위치에 해당하는 것의 색이 정의되어 있다.
		Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102),
				new Color(102, 204, 102), new Color(102, 102, 204),
				new Color(204, 204, 102), new Color(204, 102, 204),
				new Color(102, 204, 204), new Color(218, 170, 0), 
				new Color(169, 169, 169)};

		//선택된 모양의 컬러 가져오기 
		Color color = colors[shape.ordinal()];

		//사각형의 내부
		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		//사각형의 외부 상단, 왼쪽
		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y); //왼쪽 선
		g.drawLine(x, y, x + squareWidth() - 1, y); // 상단 선

		//사각형의 외부 하단, 오른쪽
		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y
				+ squareHeight() - 1);//하단
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x
				+ squareWidth() - 1, y + 1);// 오른쪽

	}
	
	public void setControlBoards(ControlGameBoards cBoard) {
		this.cBoard = cBoard;
	}
	/**
	 * 키 입력 처리
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