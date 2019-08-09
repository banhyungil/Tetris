package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import tetEnum.Player;

public class ControlUI {
	
	JPanel jp;
	int frameWidth;
	int frameHeight;
	/**
	 * 테트리스 게임이 끝날때 값이 결정됨.
	 */
	Player winnerPlayer;
	String player1ID;
	String player2ID;
	
	Timer timer;
	int time = 0;
	Tetris tetris;
	/**
	 * 랭킹을 위해 이떄까지 게임을 한 player를 저장.
	 */
	Map<String,Integer> userMap;
	
	public ControlUI(int frameWidth, int frameHeight, Tetris tetris) {
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.tetris = tetris;
		
		userMap = new HashMap<String,Integer>();
	}
	
	/**
	 * 승자와 패자를 랭킹에 등록
	 * @param winner
	 * @param loser
	 */
	public void saveRanking() {
		
		String winnerID="";
		String loserID="";
		
		switch(winnerPlayer) {
		case Player1:
			winnerID = player1ID;
			loserID = player2ID;
			break;
		case Player2:
			winnerID = player2ID;
			loserID = player1ID;
			break;
		}
		
		//승자
		if(userMap.containsKey(winnerID)) {
			int value = userMap.get(winnerID);
			++value;
			userMap.replace(winnerID, value);
		} else {
			userMap.put(winnerID, 1);
		}
		
		//패자
		if(!userMap.containsKey(loserID))
			userMap.put(loserID, 0);
	}
	
	public void start() {
		jp = getStartPanel();
		changePanel(jp);
	}
	public void changePanel(JPanel jp) {
		tetris.getContentPane().removeAll();
		tetris.getContentPane().add(jp);
		tetris.revalidate();
		tetris.repaint();
		System.out.println("change Panel");
		jp.requestFocus();
	}
	
	public JPanel getStartPanel() {
		jp = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				// TODO Auto-generated method stub
				drawImage(g, ImageURL.START);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		JButton jbStart = new JButton("시작");
		JButton jbRank = new JButton("랭킹");
		JButton jbGuide = new JButton("설명");
		StartPanelAL spAL = new StartPanelAL();
		//ActionListener 연결
		jbStart.addActionListener(spAL);
		jbRank.addActionListener(spAL);
		jbGuide.addActionListener(spAL);
		
		GridBagLayout layout = new GridBagLayout();
		jp.setLayout(layout);
		
		GridBagConstraints gbc1 = getGBC(0, 0, 1, 1, 2, 50);
		GridBagConstraints gbc2 = getGBC(0, 1, 1, 1, 2, 50);
		GridBagConstraints gbc3 = getGBC(0, 2, 1, 1, 2, 50);
		jp.add(jbStart, gbc1);
		jp.add(jbRank,gbc2);
		jp.add(jbGuide,gbc3);
		
		jbRank.setPreferredSize(new Dimension(100, 50));
		jbStart.setPreferredSize(new Dimension(100, 50));
		jbGuide.setPreferredSize(new Dimension(100, 50));
			
		return jp;
	}
	
	private GridBagConstraints getGBC( int x, int y, int w, int h, int wMargin, int hMargin){
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;

        gbc.insets = new Insets(hMargin, wMargin, hMargin, wMargin);
        
        return gbc;
    }//gbAd
	
	/**
	 * @return
	 */
	public JPanel getGamePanel() {
		GameBoard player1 = new GameBoard();
		GameBoard player2 = new GameBoard();
		ControlGameBoards cBoard = new ControlGameBoards(player1, player2, this);
		cBoard.setLayout(new GridLayout(1,2,10,10));
		
		cBoard.add(player1);
		cBoard.add(player2);
		
		cBoard.setBoards();
		cBoard.start();
		
		return cBoard;
	}
	
	public void setWinnerPlayer(Player winnerPlayer) {
		this.winnerPlayer = winnerPlayer;
	}

	public JPanel getRankingPanel() {
		jp = new JPanel();
		
		GridBagLayout layout = new GridBagLayout();
		jp.setLayout(layout);
		
		//승수에 따른 내림차순 정렬
		ArrayList<String> rankingList = sortByValue(userMap);
		
		GridBagConstraints gbc1;
		GridBagConstraints gbc2;	
		GridBagConstraints gbc3;
		JLabel jlUserID;
		JLabel jlRanking;
		JLabel jlWinCount;
		//레이아웃 헤드 설정
		int y=0;	//레이아웃 배치를 위한 인덱스 y좌표
		gbc1 = getGBC(0, y, 1, 1, 10, 30);
		gbc2 = getGBC(1, y, 1, 1, 10, 30);
		gbc3 = getGBC(2, y, 1, 1, 10, 30);
		jlRanking = new JLabel("순위");
		jlUserID = new JLabel("유저");
		jlWinCount = new JLabel("승리횟수");
		jp.add(jlRanking, gbc1);
		jp.add(jlUserID, gbc2);
		jp.add(jlWinCount, gbc3);
		++y;
		
		for(String key : rankingList) {		
			jlRanking = new JLabel(Integer.toString(y));
			jlUserID = new JLabel(key);
			jlWinCount = new JLabel(Integer.toString(userMap.get(key)));
			gbc1 = getGBC(0, y, 1, 1, 10, 30);
			gbc2 = getGBC(1, y, 1, 1, 10, 30);
			gbc3 = getGBC(2, y, 1, 1, 10, 30);
			jp.add(jlRanking, gbc1);
			jp.add(jlUserID, gbc2);
			jp.add(jlWinCount, gbc3);
			++y;
		}
		
		GridBagConstraints gbc = getGBC(0, y, 3, 1, 10, 30);
		JButton jb = new JButton("홈");
		//홈으로 가는 버튼
		jb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				jp = getStartPanel();
				changePanel(jp);
			}
		});
		
		jp.add(jb,gbc);
		return jp;
	}
	
	/**
	 * hash map을 value에 따라 정렬, 승수에 따라 정렬이된다.
	 * @param map
	 * @return
	 */
	public ArrayList<String> sortByValue(Map map) {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(map.keySet());
		
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				Object v1 = map.get(o1);
				Object v2 = map.get(o2);	
				
				return ((Comparable)v2).compareTo(v1);
			}
		});
		
		//Collections.reverse(list);		//내림차순 정렬
		return list;
	}
	
	public JPanel getInputUserIDPanel() {
		jp = jp = new JPanel();
		
		GridBagLayout layout = new GridBagLayout();
		jp.setLayout(layout);
		
		JLabel jl1 = new JLabel("Player1");
		JLabel jl2 = new JLabel("Player2");
		JTextField jtfPlayer1 = new JTextField(10);
		JTextField jtfPlayer2 = new JTextField(10);
		
		GridBagConstraints gbc1 = getGBC(0, 0, 1, 1, 10, 30);
		GridBagConstraints gbc2 = getGBC(1, 0, 1, 1, 10, 30);
		jp.add(jl1, gbc1);
		jp.add(jtfPlayer1, gbc2);
		gbc1 = getGBC(0, 1, 1, 1, 10, 30);
		gbc2 = getGBC(1, 1, 1, 1, 10, 30);
		jp.add(jl2, gbc1);
		jp.add(jtfPlayer2, gbc2);
		
		JButton jbInput = new JButton("입력");
		jbInput.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				player1ID = jtfPlayer1.getText();
				player2ID = jtfPlayer2.getText();
				
				//validate ID구현해야함.
				saveRanking();
				changePanel(getStartPanel());
			}
		});
		
		gbc1 = getGBC(0, 2, 2, 1, 10, 30);
		jp.add(jbInput, gbc1);
		//아이디 입력 받아서 저장 받아야함, 점수를 위해
		return jp;
	}
	
	public JPanel getEndPanel() {
		jp = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				// TODO Auto-generated method stub
				drawImage(g, ImageURL.GAMEOVER);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		timer = new Timer(2000,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				++time;			
				if(time > 1) {
					timer.stop();
					changePanel(getInputUserIDPanel());
				}
					
			}
		});
		
		timer.start();
		return jp;
	}
	
	public JPanel getGuidePanel() {
		jp = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		jp.setLayout(layout);
		
		GridBagConstraints[] gbc = new GridBagConstraints[8];
		gbc[0] = getGBC(0, 0, 2, 1, 0, 0);
		gbc[1] = getGBC(0, 1, 1, 1, 0, 0);
		gbc[2] = getGBC(1, 1, 1, 1, 0, 0);
		gbc[3] = getGBC(0, 2, 1, 1, 0, 0);
		gbc[4] = getGBC(1, 2, 1, 1, 0, 0);
		gbc[5] = getGBC(0, 3, 2, 1, 0, 10);
		gbc[6] = getGBC(0, 4, 2, 1, 10, 10);
		gbc[7] = getGBC(0, 5, 2, 1, 0, 10);
		
		JLabel[] jl = new JLabel[7];
		jl[0] = new JLabel("키 설명");
		jl[1] = new JLabel("1P");
		jl[2] = new JLabel("2P");
		String p1 = "<html>위 : R <br> 아래 : F <br> 왼 : D <br> 오 : G <br> 'a' : 한번에 내리기  <br> 'e' : 빠르게 내리기</html>";
		String p2 = "<html>방향키 <br> 한번에 내리기 : ',' <br> 빠르게 내리기 : '.'</html>";
		jl[3] = new JLabel(p1);
		jl[4] = new JLabel(p2);
		jl[5] = new JLabel("기능");
		String p3 = "<html>1. 3줄 삭제 시 상대 편 배드블럭 등장 <br> -상대방 한 라인 증가 <br> 2. 레벨업 <br> -20초 경과시 레벨업 " +
					"<br>3.랭킹 <br>-게임 끝난 후 유저를 등록하면 랭킹 화면에 표시됨</html>";
		jl[6] = new JLabel(p3);
		
		for(int i=0; i < jl.length ; ++i) {
			gbc[i].fill = GridBagConstraints.BOTH;
			jl[i].setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.darkGray), new EmptyBorder(5, 10, 5, 10)));
			jl[i].setFont(jl[i].getFont().deriveFont(15.0f));
			//jl[i].setPreferredSize(new Dimension(100, 100));
			jl[i].setHorizontalAlignment(SwingConstants.CENTER);
			jp.add(jl[i],gbc[i]);
		}
		gbc[7].fill = GridBagConstraints.BOTH;	
		JButton jb = new JButton("홈");
		jb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				changePanel(getStartPanel());
			}
		});
		jp.add(jb, gbc[7]);
		return jp;
	}
	
	public void drawImage(Graphics g, String url) {
		Image img = new ImageIcon(url).getImage();
		g.drawImage(img, 0, 0, frameWidth, frameHeight-50, null);
	}
	
	public class StartPanelAL implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Object obj = e.getSource();
			if(obj instanceof JButton) {
				JButton jb = (JButton) obj;
				String text = jb.getText();
				switch(text) {
				case "시작":
					jp = getGamePanel();
					changePanel(jp);
					break;
				case "랭킹":
					changePanel(getRankingPanel());
					break;
				
				case "설명":
					changePanel(getGuidePanel());
					break;
				}
			}
		}	
	}
}
