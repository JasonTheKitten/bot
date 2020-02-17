package everyos.discord.exobot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class StaticFunctions {
    private static String dir;
    private static String filename = "save.json";
    public static String keysFile = "keys.config";
    static String seper = "/";    
    static {
        String mdir;
        String OS = (System.getProperty("os.name")).toUpperCase();
        if (OS.contains("WIN")) {
            mdir =  new File(System.getenv("APPDATA")).getAbsolutePath();
            seper = "\\";
        } else {
            mdir = new File(System.getProperty("user.home")).getAbsolutePath();
        }
        dir = mdir + seper + "everyos" + seper +"bot";
    }

	public static String getAppData(String path){
            return dir+seper+path;
	}
	public static boolean save(){
		try {
			String path = getAppData(filename);
			
			File fi = new File(path);
			if (!(fi.exists())) {
				fi.getParentFile().mkdirs();
				fi.createNewFile();
			}
			
			BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(path));
			file.write(Statics.serializeSave().getBytes());
			file.close();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean load(){
		try {
			String path = getAppData(filename);
			
			if (!(new File(path).exists())) return true;
			
			File fi = new File(path);
			if (!(fi.exists())) {
				fi.getParentFile().mkdirs();
				fi.createNewFile();
			}
			
			BufferedInputStream file = new BufferedInputStream(new FileInputStream(path));
			StringBuilder contents = new StringBuilder();
			
			int ch;
			while ((ch=file.read())!=-1) {
				contents.append((char) ch);
			}
			
			file.close();
			
			Statics.loadSave(contents.toString());
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
