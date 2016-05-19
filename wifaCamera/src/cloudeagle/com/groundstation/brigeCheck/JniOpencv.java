package cloudeagle.com.groundstation.brigeCheck;;

public class JniOpencv {
	 static {   
         System.loadLibrary("qiaoliang");   
        } 
	public static native int CrackDetect(String file_path,String cascade_path);
}
