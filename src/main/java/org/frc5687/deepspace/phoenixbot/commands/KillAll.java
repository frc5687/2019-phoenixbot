package org.frc5687.deepspace.phoenixbot.commands;

import org.frc5687.deepspace.phoenixbot.Robot;

public class KillAll extends OutliersCommand {
    private boolean _finished;

    public KillAll(Robot robot){
        requires(null);
    }
    @Override
    protected void initialize() {
        _finished = true;
        error("Initializing KillAll Command.");
    }

    @Override
    protected void end() {
        error("Ending KillAll Command.");
    }

    @Override
    protected boolean isFinished() {
        return _finished;
    }
}
