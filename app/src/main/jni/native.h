
#include <jni.h>
#include <list>
#include <vector>
#include <opencv2/core/core.hpp>
using namespace std;
using namespace cv;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jobject  JNICALL Java_com_example_pianoproject_PlayingClass_detectFinger(JNIEnv* env,
jobject, jlong frame, jlong mask, jobjectArray whiteKeys,jobjectArray blackKeys);



#ifdef __cplusplus
}
#endif

