package org.firstinspires.ftc.teamcode.util;

import org.firstinspires.ftc.teamcode.Sagan;

/**
 * The primary operation file for the teleOp phase of the match
 */
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous - Primary")
public class AutoMcAutty extends CommandOpMode {

    @Override
    public void initialize() {
        // QUERY USER TO DETERMINE OUR STARTING COORDINATES
        boolean isLeft = false;
        boolean isRed = false;
        // give player time to enter selection
        while(opModeInInit()) {
            // press X for blue and B for red
            if (gamepad1.x)
                isRed = false;
            else if (gamepad1.b && !gamepad1.start)
                isRed = true;
            // press dpad LEFT for left and RIGHT for right
            if (gamepad1.dpad_left)
                isLeft = true;
            else if (gamepad1.dpad_right)
                isLeft = false;

            // DISPLAY SELECTION
            telemetry.addData("Position", "%s Team, %s Side", isRed ? "Red" : "Blue",
                    isLeft ? "Left" : "Right");
            telemetry.update();
        }
        /*
         We build our robot. From here on out, we don't need this file. When we build the robot,
         all of our buttons are bound to commands and this class's parent, CommandOpMode, will
         continuously run any scheduled commands. We now slide into the WPILib style.
         We pass in our autonomous config variables, which signals to the robot we want to be in
         autonomous mode instead of in teleop mode, which would take no params besides this.
         */
        Robot m_robot = new Sagan(this, isRed, isLeft);
    }

}
