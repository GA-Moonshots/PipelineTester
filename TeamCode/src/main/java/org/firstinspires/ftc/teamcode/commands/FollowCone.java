import com.arcrobotics.ftclib.command.CommandBase;
import org.firstinspires.ftc.teamcode.sensors.Pipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

public class FollowCone extends CommandBase {
    private OpenCvCamera webcam;
    private Pipeline pipeline;

    public FollowCone(Pipeline pipeline) {
        this.pipeline = pipeline;

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.openCameraDevice();
        webcam.setPipeline(pipeline);
        webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
    }

    @Override
    public void execute() {
        // Here you can add the logic to follow the cone based on the output of the pipeline
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
}