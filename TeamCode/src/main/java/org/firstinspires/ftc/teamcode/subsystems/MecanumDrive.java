package org.firstinspires.ftc.teamcode.subsystems;


import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.geometry.Vector2d;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.Sagan;
import org.firstinspires.ftc.teamcode.util.HardwareNames;

/**
 * We extend RoadRunner's mecanum drive. That file needs our motor instantiations
 */
public class MecanumDrive extends SubsystemBase {

    // INSTANCE VARIABLES
    private boolean isTargetSet = false;
    private double fieldCentricTarget = 0.0d;
    private boolean isFieldCentric = true;
    private boolean isGyroLocked = false;
    private double gyroTarget;

    //motors
    private DcMotorEx leftFront;
    private DcMotorEx leftBack;
    private DcMotorEx rightBack;
    private DcMotorEx rightFront;


    // USEFUL REFERENCES
    private final Sagan robot;
    public Telemetry telemetry;

    public MecanumDrive(Sagan robot) {
        // convenience references
        this.robot = robot;
        this.telemetry = robot.opMode.telemetry;
        leftFront = robot.opMode.hardwareMap.get(DcMotorEx.class, HardwareNames.LEFT_FRONT_NAME);
        leftBack = robot.opMode.hardwareMap.get(DcMotorEx.class, HardwareNames.LEFT_BACK_NAME);
        rightBack = robot.opMode.hardwareMap.get(DcMotorEx.class, HardwareNames.RIGHT_BACK_NAME);
        rightFront = robot.opMode.hardwareMap.get(DcMotorEx.class, HardwareNames.RIGHT_FRONT_NAME);


        this.resetFieldCentricTarget();
    }

    // --- FIELD CENTRIC HELPERS ---
    public void toggleFieldCentric() {
        isFieldCentric = !isFieldCentric;
    }
    public void makeRobotCentric() {
        isFieldCentric = false;
    }
    public void makeFieldCentric() {
        isFieldCentric = true;
    }
    public void resetFieldCentricTarget() {
        fieldCentricTarget = getZAngle();
    }

    /**
     * Translates desired motion into mecanum commands
     * @param forward negative is forward
     * @param strafe lateral movement
     * @param turn positive is clockwise
     */
    public void drive(double forward, double strafe, double turn) {
        // Field Centric adjustment
        if (isFieldCentric) {
            // Learn more:
            // https://www.geogebra.org/m/fmegkksm
            double diff = fieldCentricTarget - getZAngle();
            double temp = forward;
            forward = forward * Math.cos(Math.toRadians(diff)) - strafe * Math.sin(Math.toRadians(diff));
            strafe = temp * Math.sin(Math.toRadians(diff)) + strafe * Math.cos(Math.toRadians(diff));
            if(telemetry != null)
                telemetry.addData("Mode", "Field Centric");
        } else if(telemetry != null)
            telemetry.addData("Mode", "Robot Centric");

        isGyroLocked = turn <= HardwareNames.INPUT_THRESHOLD;
        if(isGyroLocked && !isTargetSet) {
            gyroTarget = getYAngle();
            isTargetSet = true;
        } else if(!isGyroLocked) {
            isTargetSet = false;
        }

        // I'm tired of figuring out the input problems so the inputs are still in flight stick mode
        // Meaning forward is reversed
        // The boost values should match the turn
        // Since the drive is a diamond wheel pattern instead of an X, it reverses the strafe.
        double leftFrontPower = -forward +strafe + turn;
        double rightFrontPower = forward + strafe + turn;
        double leftBackPower = -forward - strafe + turn;
        double rightBackPower = forward - strafe + turn;

        double powerScale = HardwareNames.MOTOR_MAX_SPEED * Math.max(1,
                Math.max(
                        Math.max(
                                Math.abs(leftFrontPower),
                                Math.abs(leftBackPower)
                        ),
                        Math.max(
                                Math.abs(rightFrontPower),
                                Math.abs(rightBackPower)
                        )
                )
        );

        leftFrontPower /= powerScale;
        leftBackPower /= powerScale;
        rightBackPower /= powerScale;
        rightFrontPower /= powerScale;


        if(telemetry != null)
            telemetry.addData("Motors", "(%.2f, %.2f, %.2f, %.2f)",
                    leftFrontPower, leftBackPower, rightBackPower, rightFrontPower);

        drive(
                leftFrontPower,
                rightFrontPower,
                leftBackPower,
                rightBackPower
        );
    }

    /**
     * Clips and executes given motor speeds
     */
    protected void drive(double m1, double m2, double m3, double m4) {
        leftFront.setPower(Range.clip(m1, -HardwareNames.MOTOR_MAX_SPEED, HardwareNames.MOTOR_MAX_SPEED));
        rightFront.setPower(Range.clip(m2, -HardwareNames.MOTOR_MAX_SPEED, HardwareNames.MOTOR_MAX_SPEED));
        leftBack.setPower(Range.clip(m3, -HardwareNames.MOTOR_MAX_SPEED, HardwareNames.MOTOR_MAX_SPEED));
        rightBack.setPower(Range.clip(m4, -HardwareNames.MOTOR_MAX_SPEED, HardwareNames.MOTOR_MAX_SPEED));
    }

    public void stop() {
        this.drive(0,0,0,0);
    }

    /**
     * Rotates robot to its imu's 0 degree heading adjusted by fieldCentricTarget. This is not
     * intended to be used with RoadRunner as it doesn't explicitly communicate its intended pose
     */
    public void goToZeroAngle() {
        while (Math.abs(robot.drive.getZAngle() - fieldCentricTarget) >= 2) {
            drive(0.0, 0.0, 0.7 * Math.toRadians(robot.drive.getZAngle() - fieldCentricTarget));
        }
        stop();
    }

    /**
     * @return the difference between the robot's current angle and the Zero Angle
     */
    public double getAngleDifferenceFromZero() {
        double currentAngle = robot.drive.getZAngle(); // Get current angle
        double angleDifference = currentAngle - fieldCentricTarget; // Calculate difference

        // if the difference is greater than or equal to 360, subtract 360 to keep it within -180 to 180
        if (angleDifference >= 360) {
            angleDifference -= 360;
        }

        // if the difference is less than -360, add 360 to keep it within -180 to 180
        if (angleDifference < -360) {
            angleDifference += 360;
        }

        return angleDifference; // Return the normalized angle difference
    }

    /**
     *
     * @return the X angle of the internal IMU in the control panel.
     */
    public double getXAngle() {
        return robot.imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).firstAngle;
    }

    /**
     *
     * @return the Y angle of the internal IMU in the control panel.
     */
    public double getYAngle() {
        return robot.imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).secondAngle;
    }

    /**
     *
     * @return the Z angle of the internal IMU in the control panel.
     */
    public double getZAngle() {
        return robot.imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;
    }

}


