package com.example.pianoproject;

import java.util.List;

import org.opencv.android.JavaCameraView;


import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;

public class CustomizeCameraView extends JavaCameraView {
	
	 public CustomizeCameraView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }
	 public void setResolution(int h,int w){
         Camera.Parameters params = mCamera.getParameters();
         params.setPreviewSize(mFrameWidth, mFrameHeight);
         params.setPictureSize(mFrameWidth, mFrameHeight);
            mCamera.setParameters(params); // mCamera is a Camera object

     }
	 public List<Size> getResolutionList() {
         //return mCamera.getParameters().getSupportedPreviewSizes();
         return mCamera.getParameters().getSupportedPictureSizes();
          // return mCamera.getParameters().getPictureSize();
        }

     public Size getResolution() {
          //  return mCamera.getParameters().getPreviewSize();
            return mCamera.getParameters().getPictureSize();
        }
}
