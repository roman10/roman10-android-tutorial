/***
this code doesn't work yet!!!
**/
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <time.h>
#include <errno.h>
#include <sys/mman.h>

#include <jni.h>
#include <android/log.h>

#define LOG_TAG "mmap test"
#define LOG_LEVEL 10
#define LOGI(level, ...) if (level <= LOG_LEVEL) {__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__);}
#define LOGE(level, ...) if (level <= LOG_LEVEL) {__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__);}

int mapSize = 8;
unsigned char *maped;
char *shareMemName="test.mmap";
int fd;

int shareMemMap() {
    //create anaymous map, not backed by any files
    if ((fd = ashmem_create_region(shareMemName, mapSize)) == -1) {        
        perror("open: ");
        LOGI(10, "error open the file: %d", errno);
        exit(1);
    }
    maped = mmap(0, mapSize, PROT_READ|PROT_WRITE, MAP_SHARED, fd, 0);
    if (maped == MAP_FAILED || maped == NULL) {
        perror("map error: ");
        return -1;
    } else {
        LOGI(10, "*****map successful");
    }
    return 0;
}

void unmapSharedMem() {
    munmap(maped, mapSize);
    close(fd);
    shm_unlink(shareMemName);
}

JNIEXPORT void JNICALL Java_roman10_tutorial_mmap_UpdateMemoryService_naUnmap(JNIEnv *pEnv, jobject pObj) {
    unmapSharedMem();
}

JNIEXPORT void JNICALL Java_roman10_tutorial_mmap_UpdateMemoryService_naMap(JNIEnv *pEnv, jobject pObj) {
    int i;
    unsigned char x;
    if (shareMemMap() == -1) {
        LOGI(10, "mmap failed\n");
        return;
    }
}


JNIEXPORT void JNICALL Java_roman10_tutorial_mmap_UpdateMemoryService_naUpdate(JNIEnv *pEnv, jobject pObj) {
    int i;
    unsigned char x;
    LOGI(10, "***********************naUpdate");
    srand(time(NULL));
    //readFile();
    for (i = 0; i < mapSize; ++i) {
        x = rand()%128;
        LOGI(10, "update: %u", x);
	    maped[i] = x;
    }
    //readFile();
}

JNIEXPORT jobject JNICALL Java_roman10_tutorial_mmap_Mmap_naMap(JNIEnv *pEnv, jobject pObj) {
   jobject byteBuf;
   unsigned char x;
   int i;
   //initFile();
   if (shareMemMap() == -1) {
       LOGI(10, "mmap failed\n");
       return NULL;
   }
   byteBuf = (*pEnv)->NewDirectByteBuffer(pEnv, maped, mapSize); 
   if (byteBuf == NULL) {
       perror("cannot create direct buffer: ");
       return NULL;
   } else {
       LOGI(10, "direct buffer created\n");
       return byteBuf;
   }
}
