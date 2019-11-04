package org.frc5687.deepspace.phoenixbot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.frc5687.deepspace.phoenixbot.Constants;
import org.frc5687.deepspace.phoenixbot.Robot;
import org.frc5687.deepspace.phoenixbot.RobotMap;
import org.frc5687.deepspace.phoenixbot.commands.Drive;

import static org.frc5687.deepspace.phoenixbot.Constants.DriveTrain.CREEP_FACTOR;
import static org.frc5687.deepspace.phoenixbot.utils.Helpers.applySensitivityFactor;
import static org.frc5687.deepspace.phoenixbot.utils.Helpers.limit;

public class DriveTrain extends OutliersSubsystem {

    private TalonSRX _leftTalon;
    private TalonSRX _rightTalon;

    public DriveTrain(Robot robot) {
        _leftTalon = new TalonSRX(RobotMap.CAN.LEFT_FRONT_MOTOR);
        _rightTalon = new TalonSRX(RobotMap.CAN.RIGHT_FRONT_MOTOR);
    }

    @Override
    protected void initDefaultCommand() { setDefaultCommand(new Drive());
    }

    public void cheesyDrive(double speed, double rotation, boolean creep) {
        metric("Speed", speed);
        metric("Rotation", rotation);

        speed = limit(speed, 1);
        //Shifter.Gear gear = _robot.getShifter().getGear();

        rotation = limit(rotation, 1);

        double leftMotorOutput;
        double rightMotorOutput;

        double maxInput = Math.copySign(Math.max(Math.abs(speed), Math.abs(rotation)), speed);

        if (speed < Constants.DriveTrain.DEADBAND && speed > -Constants.DriveTrain.DEADBAND) {
            metric("Rot/Raw", rotation);
            rotation = applySensitivityFactor(rotation, Constants.DriveTrain.ROTATION_SENSITIVITY);
            if (creep) {
                metric("Rot/Creep", creep);
                rotation = rotation * CREEP_FACTOR;
            }

            metric("Rot/Transformed", rotation);
            leftMotorOutput = rotation;
            rightMotorOutput = -rotation;
            metric("Rot/LeftMotor", leftMotorOutput);
            metric("Rot/RightMotor", rightMotorOutput);
        } else {
            // Square the inputs (while preserving the sign) to increase fine control
            // while permitting full power.
            metric("Str/Raw", speed);
            speed = Math.copySign(applySensitivityFactor(speed, Constants.DriveTrain.SPEED_SENSITIVITY), speed);
            metric("Str/Trans", speed);
            rotation = applySensitivityFactor(rotation, Constants.DriveTrain.TURNING_SENSITIVITY);
            double delta = rotation * Math.abs(speed);
            leftMotorOutput = speed + delta;
            rightMotorOutput = speed - delta;
            metric("Str/LeftMotor", leftMotorOutput);
            metric("Str/RightMotor", rightMotorOutput);
        }

        setPower(limit(leftMotorOutput), limit(rightMotorOutput), true);
    }

    public void setPower(double leftSpeed, double rightSpeed, boolean override) {
        _leftTalon.set(ControlMode.Current, leftSpeed);
        _rightTalon.set(ControlMode.Current, rightSpeed);
        metric("Power/Right", rightSpeed);
        metric("Power/Left", leftSpeed);
    }

    public double getLeftPower() {
        return _leftTalon.getOutputCurrent();
    }

    public double getRightPower() {
        return _rightTalon.getOutputCurrent();
    }

    @Override
    public void updateDashboard() {

    }
}
