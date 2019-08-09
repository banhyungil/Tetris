package tetris;

import javax.sound.sampled.*;
import java.io.*;

public class Sound {
    
    Clip clip;
    String fullPath;
    // 사운드 재생용 메서드
    // ** wav 파일만 받을 수 있음
    // 매개변수 loop는 true로 지정시 무한반복재생, false일 시 한번만 재생
    
    public Sound(String file) {
        fullPath = "./src/Sound/" + file + ".wav";
    }
    
    public void playSound(boolean loop) {
        
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(fullPath)));
            clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
            if(loop) clip.loop(-1);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopSound() {
        clip.stop();
    }
}
