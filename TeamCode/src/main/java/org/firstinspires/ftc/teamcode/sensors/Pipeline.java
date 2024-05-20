package org.firstinspires.ftc.teamcode.sensors;

import org.firstinspires.ftc.teamcode.Sagan;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import org.opencv.core.Scalar;
import org.tensorflow.lite.Interpreter;
import org.openftc.easyopencv.OpenCvPipeline;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.firstinspires.ftc.teamcode.util.Constants;
import org.tensorflow.lite.Tensor;

// Define a class for the ObjectDetectionPipeline
public class Pipeline extends OpenCvPipeline {
    // Instance variables
    private int imageHeight;
    private int imageWidth;
    // Declare variables for model and labels
    private Interpreter tfliteInterpreter;
    private String[] labels;
    private Sagan robot;

    // Constructor to load the TensorFlow Lite model and labels
    public Pipeline(Sagan robot) throws IOException {
        this.robot = robot;
        // Load TensorFlow Lite model from file
        try {
            tfliteInterpreter = new Interpreter(loadModelFile(Constants.TFLITE));
        } catch (IOException e) {
            // do something
        }
    }

    private MappedByteBuffer loadModelFile(String modelName) throws IOException {
        FileDescriptor fileDescriptor = robot.opMode.hardwareMap.appContext.getAssets().openFd(modelName).getFileDescriptor();
        InputStream inputStream = new FileInputStream(fileDescriptor);
        FileChannel fileChannel = ((FileInputStream) inputStream).getChannel();
        long startOffet = robot.opMode.hardwareMap.appContext.getAssets().openFd(modelName).getStartOffset();
        long declaredLength = robot.opMode.hardwareMap.appContext.getAssets().openFd(modelName).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffet, declaredLength);
    }

    @Override
    public Mat processFrame(Mat input) {

        // Pre-process the image for the TensorFlow Lite model (e.g., resize, normalize)
        Mat processedImage = preProcessImage(input);

        // Run inference on the pre-processed image
        runInference(processedImage);

        // Get detection results (bounding boxes and class IDs)
        List<Rect> boundingBoxes = getBoundingBoxes();
        List<Integer> classIds = getClassIds();

        // Draw bounding boxes and labels on the original image
        for (int i = 0; i < boundingBoxes.size(); i++) {
            Rect box = boundingBoxes.get(i);
            int classId = classIds.get(i);
            String label = labels[classId];

            // Draw bounding box and label on the image using OpenCV functions
            Imgproc.rectangle(input, box.tl(), box.br(), new Scalar(255, 0, 0), 2);
            Imgproc.putText(input, label, box.tl(), Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(255, 0, 0), 2);
        }
        return input;
    }

    // Helper methods to pre-process the image, run inference, and get results
    private Mat preProcessImage(Mat image) {
        // Increase the brightness of the image
        Mat brightenedImage = new Mat();
        image.convertTo(brightenedImage, -1, 1.2, 0);
        
        // Set instance variables with the size of the image
        imageHeight = brightenedImage.rows();
        imageWidth = brightenedImage.cols();
        
        // Implement your specific pre-processing steps here (e.g., resize)
        // ...
        
        return brightenedImage;
    }

    private void runInference(Mat image) {
        // Allocate tensors based on the model's input and output requirements
       // tfliteInterpreter.run(new Object[]{image.toArray()}, null);
    }

    /**
     * This method is responsible for extracting the bounding box coordinates from the model's output tensor.
     * The implementation details will depend on the specific format of the output tensor produced by your
     * TensorFlow Lite model.
     *
     * Below is an example implementation that assumes the output tensor is a 4D float tensor with the following shape:
     * [batch_size, num_detections, 4 (coordinates), 2 (class_id and confidence)]
     *
     * Each detection is represented as a vector of length 6, where the first 4 values represent the normalized
     * bounding box coordinates [y_min, x_min, y_max, x_max], and the last two values represent the class ID and
     * confidence score, respectively.
     *
     * Note: This is just an example implementation, and you will need to modify it according to your model's
     * output format.
     */
    private List<Rect> getBoundingBoxes() {
        List<Rect> boundingBoxes = new ArrayList<>();

        // Get the output tensor from the interpreter
        Tensor outputTensor = tfliteInterpreter.getOutputTensor(0);

        // Iterate over the detections
        for (int i = 0; i < outputTensor[0].length; i++) {
            // Extract the bounding box coordinates and confidence score
            float[] detection = outputTensor[0][i][0];
            float yMin = detection[0];
            float xMin = detection[1];
            float yMax = detection[2];
            float xMax = detection[3];
            float confidence = detection[5];

            // Apply a confidence threshold (e.g., 0.5)
            if (confidence > 0.5) {
                int left = (int) (xMin * imageWidth);
                int top = (int) (yMin * imageHeight);
                int right = (int) (xMax * imageWidth);
                int bottom = (int) (yMax * imageHeight);

                // Create a Rect object and add it to the list
                Rect rect = new Rect(left, top, right - left, bottom - top);
                boundingBoxes.add(rect);
            }
        }

        return boundingBoxes;
    }

    private List<Integer> getClassIds() {
        // Extract class ID data from the model's output tensor
        // ... (implementation details depend on the model's output format)
        return new ArrayList<>();
    }

    public Point getObjectLocation(){
        return null;
    }
}