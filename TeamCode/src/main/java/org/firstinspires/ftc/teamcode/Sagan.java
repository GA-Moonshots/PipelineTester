package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.button.Button;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.commands.DriveCommand;
import org.firstinspires.ftc.teamcode.commands.FollowCone;
import org.firstinspires.ftc.teamcode.sensors.Pipeline;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDrive;
import org.firstinspires.ftc.teamcode.util.HardwareNames;
import org.firstinspires.ftc.teamcode.util.Robot;

import java.io.IOException;

public class Sagan extends Robot {
    // INSTANCE VARIABLES
    public LinearOpMode opMode;
    public GamepadEx player1;
    public GamepadEx player2;

    // SUBSYSTEMS
    public MecanumDrive drive;

    // ASSETS
    public Pipeline pipeline;
    public IMU imu;


    public Sagan(LinearOpMode opMode) {
        this.opMode = opMode;
        player1 = new GamepadEx(opMode.gamepad1);
        player2 = new GamepadEx(opMode.gamepad2);
        imu = opMode.hardwareMap.get(IMU.class, HardwareNames.IMU_NAME);
        initTele();
    }

    public Sagan(LinearOpMode opMode, boolean isRed, boolean isLeft) {
        this.opMode = opMode;
        imu = opMode.hardwareMap.get(IMU.class, HardwareNames.IMU_NAME);
        initAuto(isRed, isLeft);
    }

    /**
     * Set teleOp's default commands and player control bindings
     */
    public void initTele() {
        // throw-away pose because we're not localizing anymore
        drive = new MecanumDrive(this);
        register(drive);
        drive.setDefaultCommand(new DriveCommand(this));

        /*
                .__                                      ____
        ______  |  |  _____   ___.__.  ____ _______     /_   |
        \____ \ |  |  \__  \ <   |  |_/ __ \\_  __ \     |   |
        |  |_> >|  |__ / __ \_\___  |\  ___/ |  | \/     |   |
        |   __/ |____/(____  // ____| \___  >|__|        |___|
        |__|               \/ \/          \/
        */
        Button aButtonP1 = new GamepadButton(player1, GamepadKeys.Button.A);
        aButtonP1.whenPressed(new InstantCommand(() -> {
            drive.toggleFieldCentric();
        }));

        Button bButtonP1 = new GamepadButton(player1, GamepadKeys.Button.B);
        bButtonP1.whenPressed(new InstantCommand(() -> {
            drive.resetFieldCentricTarget();
        }));

        Button xButtonP1 = new GamepadButton(player1, GamepadKeys.Button.X);
        Button yButtonP1 = new GamepadButton(player1, GamepadKeys.Button.Y);
        Button dPadUpP1 = new GamepadButton(player1, GamepadKeys.Button.DPAD_UP);
        Button dPadDownP1 = new GamepadButton(player1, GamepadKeys.Button.DPAD_DOWN);
        Button dPadLeftP1 = new GamepadButton(player1, GamepadKeys.Button.DPAD_LEFT);
        Button dPadRightP1 = new GamepadButton(player1, GamepadKeys.Button.DPAD_RIGHT);

        /*
                _                                    __
               (_ )                                /'__`\
         _ _    | |    _ _  _   _    __   _ __    (_)  ) )
        ( '_`\  | |  /'_` )( ) ( ) /'__`\( '__)      /' /
        | (_) ) | | ( (_| || (_) |(  ___/| |       /' /( )
        | ,__/'(___)`\__,_)`\__, |`\____)(_)      (_____/'
        | |                ( )_| |
        (_)                `\___/'
         */
        Button aButtonP2 = new GamepadButton(player2, GamepadKeys.Button.A);

        Button bButtonP2 = new GamepadButton(player2, GamepadKeys.Button.B);
        bButtonP2.whenPressed(new InstantCommand(() -> {
            //arm.toggleOpen();
        }));

        Button dPadUpP2 = new GamepadButton(player2, GamepadKeys.Button.DPAD_UP);
        dPadUpP2.whileHeld(new InstantCommand(() -> {
            //arm.wristUp();
        }));

    }

    /**
     * Query user for starting position and call the corresponding commands
     */
    public void initAuto(boolean isRed, boolean isLeft) {

        drive = new MecanumDrive(this);
        register(drive);

        // call follow cone
        try{
          pipeline = new Pipeline(this);
        } catch (IOException e) {
            // TODO: report error on telemetry
        }
        new FollowCone(this).schedule();

    }

}
