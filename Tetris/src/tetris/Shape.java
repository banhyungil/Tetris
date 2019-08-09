package tetris;

import java.util.Random;

import tetEnum.Tetrominoes;

import java.lang.Math;

public class Shape {

	private Tetrominoes pieceShape;
	/**
	 * 실제 사용되는 블록의 형태의 좌표 EX){ 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 }
	 */
	private int coords[][];
	/**
	 * 전체 블록의 형태에 따른 좌표 [형태에 따른 좌표 모임 인덱스(모임이 8개)][좌표의 모임(좌표가 4개씩)][좌표 X, Y(X, Y 해서 2개)]
	 */
	private int[][][] coordsTable;

	public Shape() {

		coords = new int[4][2];
		setShape(Tetrominoes.NoShape); //초기화
	}

	/**
	 * 현제 선택되어 있는 블록의 모양에 따라 좌표를 저장한다.
	 * coordsTable을 기반으로 coords를 저장한다.
	 * @param shape
	 */
	public void setShape(Tetrominoes shape) {

		coordsTable = new int[][][] {
				{ { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } }, //NoShape
				{ { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } },//SquareShape
				{ { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } },//LShape
				{ { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } },//Line
				{ { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } },
				{ { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } },
				{ { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } },
				{ { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } } };

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; ++j) {
				coords[i][j] = coordsTable[shape.ordinal()][i][j];
			}
		}
		
		pieceShape = shape;

	}

	/**
	 * 블록을 이루는 작은 블록의 X 좌표을 저장한다. index에 따라 작은 블록을 선택이 가능하다.
	 * @param index
	 * @param x
	 */
	private void setX(int index, int x) {
		coords[index][0] = x;
	}

	/**
	 * 블록을 이루는 작은 블록의 Y 좌표을 저장한다. index에 따라 작은 블록을 선택이 가능하다.
	 * @param index
	 * @param Y
	 */
	private void setY(int index, int y) {
		coords[index][1] = y;
	}

	/**
	 * 블록을 이루는 작은 블록의 x 좌표
	 * @param index
	 * @return 블록을 이루는 index 번째 블록의 X좌표
	 */
	public int x(int index) {
		return coords[index][0];
	}

	/**
	 * 블록을 이루는 작은 블록의 Y 좌표
	 * @param index
	 * @return 블록을 이루는 index 번째 블록의 Y좌표
	 */
	public int y(int index) {
		return coords[index][1];
	}

	/**
	 * 선택되어 있는 블록의 이름을 반환
	 * @return 저장되어 있는 enum Tetrominoes의 하나로 반환한다.
	 */
	public Tetrominoes getShape() {
		return pieceShape;
	}

	/**
	 * 블록의 랜덤 설정
	 */
	public void setRandomShape() {
		Random r = new Random();
		int x = Math.abs(r.nextInt()) % 7 + 1;
		Tetrominoes[] values = Tetrominoes.values();
		setShape(values[x]);
	}

	/**
	 * 블록이 가지고 있는 제일 작은 X 좌표
	 * @return X 좌표 
	 */
	public int minX() {
		int m = coords[0][0];//첫 번째 X 좌표
		for (int i = 0; i < 4; i++) {
			m = Math.min(m, coords[i][0]); // 각각의 X 좌표와 비교한다.
		}
		return m;
	}

	/**
	 * 블록이 가지고 있는 제일 작은 Y 좌표
	 * @return Y 좌표
	 */
	public int minY() {
		int m = coords[0][1];
		for (int i = 0; i < 4; i++) {
			m = Math.min(m, coords[i][1]);
		}
		return m;
	}

	/**
	 * 블록의 왼쪽 회전. 단. 사각형은 회전하지 않는다.
	 * @return shape 객체를 반환 객체 내부에 변수를 변환하여 반환
	 */
	public Shape rotateLeft() {
		if (pieceShape == Tetrominoes.SquareShape)
			return this;

		Shape result = new Shape();
		result.pieceShape = pieceShape;

		for (int i = 0; i < 4; ++i) {
			result.setX(i, y(i));
			result.setY(i, -x(i));
		}
		return result;
	}

	/**
	 * 블록의 오른쪽 회전. 단. 사각형은 회전하지 않는다.
	 * @return shape 객체를 반환 객체 내부에 변수를 변환하여 반환
	 */
	public Shape rotateRight() {
		if (pieceShape == Tetrominoes.SquareShape)
			return this;

		Shape result = new Shape();
		result.pieceShape = pieceShape;

		for (int i = 0; i < 4; ++i) {
			result.setX(i, -y(i));
			result.setY(i, x(i));
		}
		return result;
	}
}