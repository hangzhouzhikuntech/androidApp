
#include <opencv.hpp>
#include <iostream>
#include <stdio.h>
#include "com_example_qiaoliang_JniOpencv.h"
#include <android/log.h>
using namespace std;
using namespace cv;

#ifdef __cplusplus
extern "C" {
#endif
//String eyes_cascade_name = "/usr/share/opencv/haarcascades/haarcascade_eye_tree_eyeglasses.xml";
CascadeClassifier face_cascade;
void MyLine( Mat img, Point start, Point end, Scalar color)
{
  int thickness = 2;
  int lineType = 8;
  line( img,
        start,
        end,
        color,
        thickness,
        lineType );
}
JNIEXPORT jint JNICALL Java_cloudeagle_com_groundstation_brigeCheck_JniOpencv_CrackDetect( JNIEnv *env, jclass,jstring file_path,jstring face_cascade_path )
{
	RNG rng(12345);

	/** Canny detector */
	int edgeThresh = 1;
	int lowThreshold = 100;
	int const max_lowThreshold = 200;
	int ratio = 3;
	int kernel_size = 3;
	static bool init_flag =  false;
	Mat frame_rgb;
	Mat frame_gray;
	int name_len = 0;
	int i = 0;
	int j = 0;
	const char* file_name = env->GetStringUTFChars(file_path, false);
	__android_log_print(ANDROID_LOG_ERROR, "QIAOLIANG","start crack detect %s",file_name );
	const char* face_cascade_name = env->GetStringUTFChars(face_cascade_path, false);
	char new_file_name[256];
	name_len =  strlen(file_name);
	memset(&new_file_name[0],0,name_len+12);
	if(file_name == NULL) {
	   return -1; /* OutOfMemoryError already thrown */
	}
	frame_rgb = imread(file_name, 1);
	//-- 3. Apply the classifier to the frame
	if( !frame_rgb.empty() )
	{
		  cvtColor( frame_rgb, frame_gray, CV_BGR2GRAY );
	}
	else
	{
		return -2;
	}
	  std::vector<Rect> faces;
	  std::vector<Rect> faces2;
  /* 3 step to detect
   *   1. use large detect size to determine if have any crack, if none, just quit.
   *   2. use small detect size and canny detector to choose the crack.
   * */
  int large_detect_size = 65;
  int small_detect_size = 35;
  // detect large
  if (!init_flag)
  {
	  printf("face_cascade_name = %s\r\n",face_cascade_name);
	  if( !face_cascade.load( face_cascade_name ) ){ printf("--(!)Error loading\n"); return -1; };
	  if (face_cascade.empty())
	  {
		  printf("face_cascade load init failed");
		  return (jint)1;
	  }
	  init_flag = true;
  }

  face_cascade.detectMultiScale( frame_gray, faces2, 1.5, 1, 0|CV_HAAR_SCALE_IMAGE, Size(large_detect_size, large_detect_size) );
  printf("INFO:  large crack bbox num %d\n", faces2.size());
  if (faces2.size() <= 0) {
	  // no detection
	  //printf("OUTPUT:  未检测到目标\n");
	  //imshow( window_name, frame );
	  return (jint)2;
  }
  // detect small
//  face_cascade.detectMultiScale( frame_gray, faces2, 1.5, 1, 0|CV_HAAR_SCALE_IMAGE, Size(small_detect_size, small_detect_size) );

  // do canny
  Mat frame_gray_blur;
  blur( frame_gray, frame_gray_blur, Size(3,3) );
  Canny( frame_gray_blur, frame_gray_blur, lowThreshold, lowThreshold*ratio, kernel_size );
  Mat frame_gray_blur_canny = frame_gray_blur.clone();
  //
  int dilation_size = 6;
  int erosion_size = 4;
  //MORPH_ELLIPSE MORPH_CROSS MORPH_RECT
  Mat element = getStructuringElement( MORPH_RECT,
      Size( 2*dilation_size + 1, 2*dilation_size + 1 ),
      Point( dilation_size,  dilation_size));

  // apply the dilation operation
  cv::dilate( frame_gray_blur, frame_gray_blur, element );

  element = getStructuringElement( MORPH_RECT,
      Size( 2*erosion_size + 1 , 2*erosion_size + 1 ),
      Point( erosion_size, erosion_size ));
  // apply the erosion operation
  erode ( frame_gray_blur, frame_gray_blur, element );

//  imshow( canny_window_name, frame_gray_blur );

  // make the merge of detection and canny
  // first find contour of canny
  vector<vector<Point> > contours;
  vector<Vec4i> hierarchy;
  findContours( frame_gray_blur, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );
//  Mat drawing = Mat::zeros( frame_gray_blur.size(), CV_8UC3 );
//  for( int i = 0; i< contours.size(); i++ )
//     {
//       Scalar color = Scalar( rng.uniform(0, 255), rng.uniform(0,255), rng.uniform(0,255) );
//       drawContours( drawing, contours, i, color, 2, 8, hierarchy, 0, Point() );
//     }

  //
  vector<int> chosen_contours;
  Mat drawing = frame_rgb.clone();

  for( size_t i = 0; i < faces2.size(); i++ )
  {
	  int x = faces2[i].x;
	  int y = faces2[i].y;
	  int bx = x + faces2[i].width / 2;
	  int by = y + faces2[i].height / 2;
	  for( int j = 0; j < contours.size(); j++ ) {
		  bool flag = true;
		  for (int k = 0; k < chosen_contours.size(); k++) {
			  if (chosen_contours.at(k) == j) {
				  flag = false;
				  break;
			  }
		  }
		  if (flag)
		  for (int k = 0; k < contours[j].size(); k++) {
			  if (contours[j][k].x < bx && contours[j][k].x > x && contours[j][k].y > y && contours[j][k].y < by) {
				  chosen_contours.push_back(j);
				  Scalar color = Scalar( rng.uniform(0, 255), rng.uniform(0,255), rng.uniform(0,255) );
			      drawContours( frame_rgb, contours, j, color, 2, 8, hierarchy, 0, Point() );
			      break;
			  }
		  }
	  }
  }

  __android_log_print(ANDROID_LOG_ERROR, "QIAOLIANG","find contours finished" );
  // simple inference procedure of crack width
    // 随机选取位置计算沟壑宽度 ///////////////////////////////////////////////
    //
    /// 1. 寻找最大的contour
    int biggest_contour = -1;
    int biggest_size = -1;
    for( int i = 0; i< chosen_contours.size(); i++ ) {
  	  if (biggest_size < (int) (contours[chosen_contours[i]].size())) {
  		  biggest_size = contours[chosen_contours[i]].size();
  		  biggest_contour = chosen_contours[i];
  	  }
    }
    if (biggest_size  == -1 || biggest_contour == -1) {
  	  // something goes wrong
  	  // no hits or no contours
  	  // ignore this
    	__android_log_print(ANDROID_LOG_ERROR, "QIAOLIANG","find biggest contours failed");
    	return -1;
    }



    /// 2. 选取随机一个点，并确定contour走向
    bool found = false;
    int count = 0;
    int roi = 30;
    while (roi > 0 && !found) {
  	  count++;

  	  int cidx = rng.uniform(10, contours[biggest_contour].size() - 10);
  	__android_log_print(ANDROID_LOG_ERROR, "QIAOLIANG","cidx = %d",cidx );
  	  float dy = contours[biggest_contour][cidx - 1].y - contours[biggest_contour][cidx + 1].y;
  	  dy = dy / 2.0f;
  	  float dx = contours[biggest_contour][cidx - 1].x - contours[biggest_contour][cidx + 1].x;
  	  dx = dx / 2.0f;
  	  /// 3. 框选原图子区域、canny图子区域
  	  cv::Rect rect;
  	  rect.x = contours[biggest_contour][cidx - 1].x - roi;
  	  rect.y = contours[biggest_contour][cidx - 1].y - roi;

  	__android_log_print(ANDROID_LOG_ERROR, "QIAOLIANG","roi = %d,x = %d,y = %d",roi,rect.x,rect.y );

	  if (rect.x < 0 || rect.x >= frame_rgb.cols || rect.y < 0 || rect.y >= frame_rgb.rows) {
		  roi = roi - 10;
		  continue;
	  }

  	  rect.height = roi * 2 + 1;
  	  rect.width = roi * 2 + 1;

  	  if (rect.x+rect.width > frame_rgb.cols || rect.y+rect.height > frame_rgb.rows
  			  || rect.x+rect.width > frame_gray_blur_canny.cols || rect.y+rect.height > frame_gray_blur_canny.rows)
  	  {
  		  roi -= 10;
  		  continue;
  	  }

  	__android_log_print(ANDROID_LOG_ERROR, "QIAOLIANG","roi rect x:%d,y:%d,src rect x:%d,y:%d",rect.x,rect.y,frame_rgb.cols,frame_rgb.rows );
  	  Mat sub_frame = frame_rgb(rect);
  	//__android_log_print(ANDROID_LOG_ERROR, "QIAOLIANG","set gray blur canny");
  	  Mat sub_canny = frame_gray_blur_canny(rect);
  //	  cv::imwrite("/tmp/debug_frame_gray_blur_canny_sub.jpg", sub_canny);
  //	  cv::imwrite("/tmp/debug_frame_sub.jpg", sub_frame);
  	  /// 4. 旋转变换使得裂纹在选取点处为水平
  	  float scale = 1.0f;  // 放大2倍
  	  Point center;
  	  center.x = roi;
  	  center.y = roi;
  	  float angle = std::atan2(dy, dx) * 180.0f / 3.1415926f;
  	  cout << "angle " << angle << std::endl;
  	__android_log_print(ANDROID_LOG_ERROR, "QIAOLIANG","angle :%d",(int)angle );
  	  Mat rot_mat = cv::getRotationMatrix2D( center, angle, scale );
  	  Mat sub_canny_rotate = sub_canny.clone();
  	  Mat sub_frame_rotate = sub_frame.clone();
  	  cv::warpAffine( sub_canny, sub_canny_rotate, rot_mat, sub_canny_rotate.size() );
  	  cv::warpAffine( sub_frame, sub_frame_rotate, rot_mat, sub_frame_rotate.size() );
  //	  cv::imwrite("/tmp/debug_frame_gray_blur_canny_sub_rotate.png", sub_canny_rotate);
  	  /// 5. 作选取点及相邻点共3点，做垂线检查
  	  float dist = -1.0f;
  	  Mat vertical = sub_canny_rotate.col(roi);
  //	  cout << vertical.cols << " " << vertical.rows << " " << vertical.dims;
  	  // do a non-maximum suppression
  	  bool _in = false;
  	  std::vector<int> ipeaks;
  	  std::vector<unsigned int> vpeaks;
  	  for (int i = 0; i < sub_canny_rotate.rows; ++i) {
  		  unsigned char v = sub_canny_rotate.at<char>(i,roi);
  //		  cout << (int)v << std::endl;
  		  if (v > 30) {
  			  if (!_in) {
  				  // a new peak
  				  _in = true;
  				  ipeaks.push_back(i);
  				  vpeaks.push_back(v);
  			  } else {
  				  if (v > vpeaks[vpeaks.size() - 1]) {
  					  // a successive peak, find the largest
  					  ipeaks.pop_back();
  					  ipeaks.push_back(i);
  					  vpeaks.pop_back();
  					  vpeaks.push_back(v);
  				  }
  			  }
  		  } else
  			  _in = false;
  	  }
  	  // just use top 2

	  if (ipeaks.size() < 2) {
		  // 1 pixel
		  found = true;
		  dist = 1;
		  __android_log_print(ANDROID_LOG_ERROR, "QIAOLIANG","find ipeaks.size < 2");


	  } else {

		  found = true;
		  dist = ipeaks[ipeaks.size() - 1] - ipeaks[0];
		  __android_log_print(ANDROID_LOG_ERROR, "QIAOLIANG","find");
	  }

	  found = true;
	  dist = ipeaks[ipeaks.size() - 1] - ipeaks[0];
	  int z = ipeaks.size() - 1;

	  MyLine(sub_canny_rotate, Point(roi, 0), Point(roi, sub_canny_rotate.rows), Scalar( 255, 0, 50 ));
	  MyLine(sub_canny_rotate, Point(roi, ipeaks[0]), Point(roi, ipeaks[z]), Scalar( 50, 50, 255 ));
//		  cv::imwrite("/tmp/debug_frame_gray_blur_canny_sub_rotate.png", sub_canny_rotate);

	  // output to original image
	  // draw to sub image
	  MyLine(sub_frame_rotate, Point(roi, 0), Point(roi, sub_frame_rotate.rows), Scalar( 255, 0, 50 ));
	  MyLine(sub_frame_rotate, Point(roi, ipeaks[0]), Point(roi, ipeaks[z]), Scalar( 50, 50, 255 ));
	  // rotate back
	  rot_mat = cv::getRotationMatrix2D( center, -angle, scale );
	  cv::warpAffine( sub_frame_rotate, sub_frame, rot_mat, sub_frame.size() );
	  // embed into original image
	  sub_frame.copyTo(frame_rgb(rect));
	  // draw infomation of box and text
	  cv::rectangle(frame_rgb, rect, Scalar(100,100,100), 1, 8, 0);
	  ostringstream s1;
	  s1 << dist << " pixels";
	  cv::putText(frame_rgb, s1.str().c_str(), Point(rect.x, rect.y), CV_FONT_HERSHEY_COMPLEX, 0.7, Scalar(255,0,0));

  	  dist = dist / scale;
  //	  cout << "crack width " << ipeaks.size();
  //	  cout << "crack width " << dist;
    }//end while

  /// 在窗体中显示结果
//  namedWindow( "Contours", CV_WINDOW_AUTOSIZE );
//  imshow( "Contours", drawing );
  //imshow(window_name, frame);
        	  for (i = 0;i < name_len;i++)
        	  {
        	  	if (*(file_name+i) != '.'  )
        	  	{new_file_name[j] =   *(file_name+i);
        	  		j++;
        	  	}
        	  	else
        	  		{
        	  			break;}
        	  }
        	  if (j < name_len)
        	  	{
        	  		strncpy(&new_file_name[j],"_detect.jpg",11);
        	  		}
        	  		//printf("new file name %s\r\n",new_file_name);
        	imwrite(new_file_name, frame_rgb);

  //-- Detect faces
//  face_cascade.detectMultiScale( frame_gray, faces, 1.5, 1, 0|CV_HAAR_SCALE_IMAGE, Size(35, 35) );
//  printf("%d", faces.size());
  for( size_t i = 0; i < faces.size(); i++ )
  {
    Point center( faces[i].x + faces[i].width*0.5, faces[i].y + faces[i].height*0.5 );
    ellipse( frame_rgb, center, Size( faces[i].width*0.5, faces[i].height*0.5), 0, 0, 360, Scalar( 255, 0, 255 ), 4, 8, 0 );

    Mat faceROI = frame_gray( faces[i] );
    std::vector<Rect> eyes;

    //-- In each face, detect eyes
//    eyes_cascade.detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CV_HAAR_SCALE_IMAGE, Size(30, 30) );

    for( size_t j = 0; j < eyes.size(); j++ )
    {
      Point center( faces[i].x + eyes[j].x + eyes[j].width*0.5, faces[i].y + eyes[j].y + eyes[j].height*0.5 );
      int radius = cvRound( (eyes[j].width + eyes[j].height)*0.25 );
      circle( frame_rgb, center, radius, Scalar( 255, 0, 0 ), 4, 8, 0 );
    }
  }

  return (jint)0;
  //-- Show what you got
//  imshow( window_name, frame );
//  int c = waitKey(0);
 /* if( cvWaitKey(27) >= 0 )
	  cv::destroyAllWindows();*/
//  if( (char)c == 'c' ) { cv::destroyWindow(string(window_name));return; }
}
#ifdef __cplusplus
}
#endif
