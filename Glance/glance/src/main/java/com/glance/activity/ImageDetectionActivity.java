package com.glance.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.glance.R;
import com.glance.bean.model.CameraPreview;
import com.glance.utils.Constants;
import com.google.android.glass.content.Intents;
import com.google.android.glass.widget.CardBuilder;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageDetectionActivity extends Activity {

    private final static int TAKE_IMAGE_FOR_DETECTION_ANALYSIS = 1;
    private final static String TAG = "ImageDetectionActivity";
    private static final String CLOUD_VISION_API_KEY = "AIzaSyBSzUdVO_ehZH33j6-IGPW7MEeUlo6Cyhw";
    private Camera mCamera = null;
    private CameraPreview mPreview = null;

    private View view = null;
    private File globalPictureFile = null;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            // user tapped take a picture
            mCamera.takePicture(null,null,mPicture);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // to keep screen on
//        setContentView(R.layout.activity_image_detection);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

//        view = new CardBuilder(this, CardBuilder.Layout.TEXT)
//                .setText(R.string.ImageDetectionWelcomeMessage)
//                .getView();
//        setContentView(view);

        setContentView(R.layout.camerapreview);
        if(mCamera == null)
            mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);



    }

    /** A safe way to get an instance of the Camera object. Adapted from Android Developer Guide*/
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            // http://stackoverflow.com/questions/19235477/google-glass-preview-image-scrambled-with-new-xe10-release
            Camera.Parameters params = c.getParameters();

            //seems the bug the link was accomodating was fixed in Glass 2
            //However the camera preview is rotated so must rotate to correct orientation
            Log.d("glance.tom", "Glass Model: " + Build.MODEL.toString());
            if(Build.MODEL.toString().contains("Glass 1"))
                params.setPreviewFpsRange(30000, 30000);
            else if(Build.MODEL.toString().contains("Glass 2"))
                c.setDisplayOrientation(90);
            else
                Log.d(TAG, "Could not recognise Glass Model in CameraManager.java line 107");


            c.setParameters(params);
            System.out.println("Camera is Open");
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            System.out.println("Error Message: " + e.getMessage());
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    /*
    Suppy image data after image is captured. Adapted from Android Developer Guide
    */
    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                uploadImage(Uri.fromFile(pictureFile));
                fos.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    };

    /*
   Creates a media file. Adapted from Android Developer Guide
    */
    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    public void takePictureForAnalysis()
    {
        //start camera to take a picture to be analysed
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, TAKE_IMAGE_FOR_DETECTION_ANALYSIS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //if camera returned from taking an image for detection analysis successfully
        if(requestCode == TAKE_IMAGE_FOR_DETECTION_ANALYSIS && resultCode == RESULT_OK)
        {
            //String thumbnailPath = data.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);

            processPictureWhenReady(picturePath);
        }
        else
        {
            Log.d(TAG, "did not return with TAKE_IMAGE_FOR_DETECTION and/or RESULT_OK" + " requestCode: "
                    + requestCode + ", resultCode: " + resultCode);
        }
    }

    private void processPictureWhenReady(final String picturePath) {
        final File pictureFile = new File(picturePath);
        globalPictureFile = pictureFile;

        if(pictureFile.exists())
        {
            //send to google for processing
            Log.d(TAG, "Sending picture to google");
            uploadImage(Uri.fromFile(pictureFile));
        }
        else //not finished saving the picture yet
        {
            Log.d(TAG, "Observing file...");
            //observe file and wait until it is done loading
            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                // Protect against additional pending events after CLOSE_WRITE
                // or MOVED_TO is handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(picturePath);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to 800px to save on bandwidth
                Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 1200);

                callCloudVision(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "Something wrong with the picture", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, "Something wrong with the picture", Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        view = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText(R.string.wait_message)
                .getView();
        setContentView(view);
        // Do the real work in an async task, because we need to use the network
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        Log.d(TAG, "Label detection feature");
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(1);
                            add(labelDetection);
                        }});
                        // Add the list of one thing to the reques
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                System.out.println("RESULT: " + result);
                buildImageDetailsView(result);
                setContentView(view);
            }
        }.execute();
    }

    //used to update UI after image analysis returns
    private void buildImageDetailsView(String result) {
        view = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText(result)
                .getView();
        setContentView(view);

        //delete picture from glass to not over populate image storage
        //deleteRecentPicture();
    }

    //delete picture from glass to not over populate image storage
    private void deleteRecentPicture() {
        if(globalPictureFile != null || globalPictureFile.exists())
        {
            Log.d(TAG, "Deleting Photo");
            try {
                globalPictureFile.getCanonicalFile().delete();
            } catch (IOException e) {
                Log.d(TAG, "error deleting recent photo");
                e.printStackTrace();
            }
            globalPictureFile.delete();
            if(globalPictureFile.exists()){
                Log.d(TAG, "File Still exists");
                getApplicationContext().deleteFile(globalPictureFile.getName());
            }
        }
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";
        message = "Found this Object:\n\n";
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                //message += String.format("%.3f: %s", label.getScore(), label.getDescription());
                message += label.getDescription();
//                        + ". Score: " + label.getScore();
//                if(label.getDescription().equals(labels.get(label.size() - 1).getDescription()))
//                    message += ".";
//                else
//                    message += ", ";
            }
        } else {
            message += "nothing";
        }
        return message;

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.camerapreview);
        if(mCamera == null)
            mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mCamera != null){
            mCamera.release();
        }
    }

}
