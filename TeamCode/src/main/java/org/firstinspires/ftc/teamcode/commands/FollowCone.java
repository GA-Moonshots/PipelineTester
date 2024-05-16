import com.arcrobotics.ftclib.command.CommandBase;
import org.firstinspires.ftc.teamcode.sensors.Pipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

public class FollowCone extends CommandBase {
    private OpenCvCamera webcam;
    private Sagan robot;
    private Pipeline pipeline;

    public FollowCone(Sagan robot) {
        this.robot = robot;
        this.pipeline = robot.pipeline;

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.openCameraDevice();
        webcam.setPipeline(pipeline);
        webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
    }

    @Override
    public void execute() {
        // Process the image and get the Mat
        Mat processedImage = pipeline.processImage();

        // Calculate the distance and direction to the cone
        double distance = calculateDistanceToCone(processedImage);
        double direction = calculateDirectionToCone(processedImage);

        // Calculate the speed and direction for the robot to drive
        double speed = calculateSpeed(distance);
        double driveDirection = calculateDriveDirection(direction);

        // Drive the robot
        robot.drive.drive(0, 0, 0 ,0);
    }

    @Override
    public void end(boolean interrupted) {
        webcam.stopStreaming();
    }

    @Override
    public boolean isFinished() {
        // Here you can add the condition that determines when the command should end
        return false;
    }

    private double calculateDistanceToCone(Mat processedImage) {
        // Here you would add the logic to calculate the distance to the cone based on the processed image
        return 0;
    }
    
    private double calculateDirectionToCone(Mat processedImage) {
        // Here you would add the logic to calculate the direction to the cone based on the processed image
        return 0;
    }
    
    private double calculateSpeed(double distance) {
        // Here you would add the logic to calculate the speed based on the distance
        return 0;
    }
    
    private double calculateDriveDirection(double direction) {
        // Here you would add the logic to calculate the drive direction based on the direction
        return 0;
    }
}