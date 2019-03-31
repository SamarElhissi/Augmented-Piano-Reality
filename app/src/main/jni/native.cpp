#include <jni.h>
#include <native.h>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <vector>
using namespace std;
using namespace cv;

extern "C" {

int keyColor=1;
int getFingerLocation( vector<vector<Point> > whiteKeys,
		vector<vector<Point> > blackKeys, Point p){
	int location=-1;
	 	for(unsigned int i=0;i<whiteKeys.size();i++){

         vector<Point> wk=whiteKeys[i];
	 		double xx=cv::pointPolygonTest(wk,p,false);


	 	if(xx>0) {
	 	location=i; keyColor=1;
	 	return location;
	 	}

	 	}
	 	for(unsigned int i=0;i<blackKeys.size();i++){
	 		vector<Point> bk=blackKeys[i];
	 		double xx=pointPolygonTest(bk,p,false);

	 			 	if(xx>0) {
	 			 	location=i; keyColor=0;
	 			 	return location;
	 			 	}

	     	}
	 	return location;
}
JNIEXPORT jobject JNICALL Java_com_example_pianoproject_PlayingClass_detectFinger(JNIEnv* env,
jobject, jlong frame, jlong mask, jobjectArray whiteKeys, jobjectArray blackKeys){

	   jclass arraylist_class = (*env).FindClass("java/util/ArrayList");
	   jclass point_class = (*env).FindClass("org/opencv/core/Point");

	   jmethodID init_arraylist = (*env).GetMethodID(arraylist_class, "<init>", "()V");
	   jmethodID init_point = (*env).GetMethodID(point_class, "<init>", "(DD)V");
       jmethodID add_arraylist = (*env).GetMethodID(arraylist_class, "add", "(Ljava/lang/Object;)Z");
       jmethodID get_arraylist = (*env).GetMethodID(arraylist_class, "get", "(I)Ljava/lang/Object;");

       jmethodID sizeArr = (*env).GetMethodID(arraylist_class, "size", "()I");
       jfieldID fidX = (*env).GetFieldID(point_class, "x", "D");
       jfieldID fidY = (*env).GetFieldID(point_class, "y", "D");


	   jobject return_obj = (*env).NewObject(arraylist_class, init_arraylist);


      vector <vector<Point> > whiteKeysVector;

	   int size1=(*env).CallIntMethod(whiteKeys,sizeArr);

		for(unsigned int i=0;i<size1;i++){
		//
			vector<Point> keysVector;
			jobjectArray wKeys_s1=(jobjectArray) (*env).CallObjectMethod(whiteKeys, get_arraylist, i);
			  int size2= (*env).GetArrayLength(wKeys_s1);
			  for(unsigned j=0;j<size2;j++){

				  jobject wKeys_s2= (*env).GetObjectArrayElement(wKeys_s1,j);

				  double x=(*env).GetDoubleField(wKeys_s2,fidX );
				  double y=(*env).GetDoubleField(wKeys_s2,fidY );
				  Point p;
				  p.x=(int)x;p.y=(int)y;
				  keysVector.push_back(p);
			  }
			  whiteKeysVector.push_back(keysVector);
			  	 	}

//////////////////////////////////////////////////////////////////////
		 vector <vector<Point> > blackKeysVector;

			   int bsize1=(*env).CallIntMethod(blackKeys,sizeArr);

				for(unsigned int i=0;i<bsize1;i++){
				//
					vector<Point> bkeysVector;
					jobjectArray bKeys_s1=(jobjectArray) (*env).CallObjectMethod(blackKeys, get_arraylist, i);
					  int bsize2= (*env).GetArrayLength(bKeys_s1);
					  for(unsigned j=0;j<bsize2;j++){
						  jobject bKeys_s2= (*env).GetObjectArrayElement(bKeys_s1,j);
						  double x=(*env).GetDoubleField(bKeys_s2,fidX );
						  double y=(*env).GetDoubleField(bKeys_s2,fidY );
						  Point bp;
						  bp.x=(int)x;bp.y=(int)y;
						  bkeysVector.push_back(bp);
					  }
					  blackKeysVector.push_back(bkeysVector);
					  	 	}


/////////////////////////////////////////////////////////

	vector<vector<Point> > fingerContours, fingersMat;

 	vector<Vec4i> hierarchy;
	Mat& fram  = *(Mat*)frame;
	Mat& mas = *(Mat*)mask;
	Mat fingureMask, RGBA, RGB ,fingerCR,fingerd;
	fram.copyTo(RGBA, mas);
	cvtColor(RGBA, RGB, COLOR_RGBA2RGB);
	cvtColor(RGB, fingureMask, COLOR_RGB2YCrCb);

	Mat m;
	Mat  fingerHeu;
//	extractChannel(fingureMask, fingerCR, 1);

	inRange(fingureMask, Scalar(0, 150, 0), Scalar(255, 255, 255),fingerHeu);

	//dilate(fingerCR, fingerd, m);

	//erode(fingerd, fingerHeu, m);

	findContours(fingerHeu, fingerContours, hierarchy, RETR_LIST , CHAIN_APPROX_SIMPLE);

	 for (unsigned int i = 0; i < fingerContours.size(); i++) {
		 vector<Point >v=fingerContours[i];
		 double con=cv::contourArea(v);


	//	 cv::contourArea(v,true);
		 if( con > 5){
			 fingersMat.push_back(v);
		    	  }
	        }
	 m.release();

	 for(unsigned int j=0;j<fingersMat.size();j++){
			 double next=0;int minLoc=0, maxLoc=0;
		     Point m=fingersMat[j][0];
		     double min=m.x, max=0;
         vector <Point> vv=fingersMat[j];
		 /* for(unsigned int i=0;i<vv.size();i++){
	    		Point pp=fingersMat[j][i];
	    		if(pp.x<min){
	    			min=pp.x;
	    			minLoc=i;
	    		}
	    	}*/
		  for(unsigned int i=0;i<vv.size();i++){
		 	    		Point pp=fingersMat[j][i];
		 	    		if(pp.y>max){
		 	    			max=pp.y;
		 	    			maxLoc=i;
		 	    		}
		 	    	}
	/*	  for(unsigned int i=0;i<vv.size();i++){
	    		Point pp=fingersMat[j][i];
	    		Point yy=fingersMat[j][minLoc];
	    		if(pp.y == yy.y){
	    			next=pp.x;
	    		//	nextLoc=i;
	    		}
	    	}*/
		  Point yyy=fingersMat[j][maxLoc];
		  Point effectiveP;
//		  effectiveP.x=(next+min)/2;
		  effectiveP.x=yyy.x;
		  effectiveP.y=yyy.y;

         int location= getFingerLocation(whiteKeysVector,blackKeysVector,effectiveP);

         if(location == -1){
         }
         else {
		  jobject loc_obj = (*env).NewObject(point_class, init_point,keyColor*1.0,location*1.0);

		  jobject effectivePoint_obj = (*env).NewObject(point_class, init_point, (effectiveP.x)*1.0, (effectiveP.y)*1.0);
		  jobject frame1Point_obj = (*env).NewObject(arraylist_class, init_arraylist);
		//  jobject refPoint_obj = (*env).NewObject(point_class, init_point, 0.0, (effectiveP.y)*1.0);
		  (*env).CallBooleanMethod(frame1Point_obj, add_arraylist, effectivePoint_obj);
		  (*env).CallBooleanMethod(frame1Point_obj, add_arraylist, loc_obj);
		  //(*env).CallBooleanMethod(frame1Point_obj, add_arraylist, refPoint_obj);

		  (*env).CallBooleanMethod(return_obj, add_arraylist, frame1Point_obj);

         }
}




return return_obj;
}
}


