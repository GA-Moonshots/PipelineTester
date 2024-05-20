package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.CommandBase;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Sagan;
import org.firstinspires.ftc.teamcode.sensors.Pipeline;
import org.firstinspires.ftc.teamcode.util.HardwareNames;
import org.opencv.core.Mat;
import org.opencv.core.Point;
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

        int cameraMonitorViewId = robot.opMode.hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", robot.opMode.hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance()
                .createWebcam(robot.opMode.hardwareMap.get(WebcamName.class, HardwareNames.WEBCAM_NAME), cameraMonitorViewId);
        webcam.setPipeline(pipeline);
        webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
    }

    @Override
    public void execute() {
        Point objLoc = pipeline.getObjectLocation();

        // Calculate the speed and direction for the robot to drive
      /*  double speed = calculateSpeed(distance);
          double driveDirection = calculateDriveDirection(direction);*/

        // Drive the robot
        //robot.drive.drive(0, 0, 0 ,0);
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