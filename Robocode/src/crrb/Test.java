package crrb;

import java.awt.*;
import robocode.*;
import robocode.Robot;
import robocode.util.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eahscs on 9/12/2018.
 */
public class Test extends AdvancedRobot {

    List<WaveBullet> waves = new ArrayList<WaveBullet>();

    static int[] stats = new int[31]; // 31 is the number of unique GuessFactors we're using
    // Note: this must be odd number so we can get
    // GuessFactor 0 at middle.

    int direction = 1;



    /**
     * PaintingRobot's run method - Seesaw
     */
    public void run() {

        do {

            setAdjustGunForRobotTurn(false);
            setAdjustRadarForGunTurn(false);
            setAdjustRadarForRobotTurn(false);

            // ...
            // Turn the radar if we have no more turn, starts it if it stops and at the start of round
            if ( getRadarTurnRemaining() == 0.0 )
                setTurnRadarRightRadians( Double.POSITIVE_INFINITY );

            execute();
        } while ( true );

    }

    /**
     * Fire when we see a robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {

        // Handle Radar scans
        double angleToEnemy = getHeadingRadians() + e.getBearingRadians();

        double radarTurn = Utils.normalRelativeAngle( angleToEnemy - getRadarHeadingRadians() );

        double extraTurn = Math.min( Math.atan( 36.0 / e.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );

        if (radarTurn < 0)
            radarTurn -= extraTurn;
        else
            radarTurn += extraTurn;

        setTurnRadarRightRadians(radarTurn);


        // Next up : Targeting

        // Enemy absolute bearing, you can use your one if you already declare it.
        double absBearing = getHeadingRadians() + e.getBearingRadians();

        // find our enemy's location:
        double ex = getX() + Math.sin(absBearing) * e.getDistance();
        double ey = getY() + Math.cos(absBearing) * e.getDistance();

        // Let's process the waves now:
        for (int i=0; i < waves.size(); i++)
        {
            WaveBullet currentWave = (WaveBullet)waves.get(i);
            if (currentWave.checkHit(ex, ey, getTime()))
            {
                waves.remove(currentWave);
                i--;
            }
        }

        double power = 1.8;  //Math.min(3, Math.max(.1, /* some function */));
        // don't try to figure out the direction they're moving
        // they're not moving, just use the direction we had before
        if (e.getVelocity() != 0)
        {
            if (Math.sin(e.getHeadingRadians()-absBearing)*e.getVelocity() < 0)
                direction = -1;
            else
                direction = 1;
        }
        int[] currentStats = stats; // This seems silly, but I'm using it to
        // show something else later
        WaveBullet newWave = new WaveBullet(getX(), getY(), absBearing, power,
                direction, getTime(), currentStats);


        int bestindex = 15;	// initialize it to be in the middle, guessfactor 0.
        for (int i=0; i<31; i++)
            if (currentStats[bestindex] < currentStats[i])
                bestindex = i;

        // this should do the opposite of the math in the WaveBullet:
        double guessfactor = (double)(bestindex - (stats.length - 1) / 2) / ((stats.length - 1) / 2);
        double angleOffset = direction * guessfactor * newWave.maxEscapeAngle();
        double gunAdjust = Utils.normalRelativeAngle(
                absBearing - getGunHeadingRadians() + angleOffset);
        setTurnGunRightRadians(gunAdjust);

        if (setFireBullet(power) != null)
            waves.add(newWave);


    }

    /**
     * We were hit!  Turn perpendicular to the bullet,
     * so our seesaw might avoid a future shot.
     * In addition, draw orange circles where we were hit.
     */
    public void onHitByBullet(HitByBulletEvent e) {
        // demonstrate feature of debugging properties on RobotDialog
        setDebugProperty("lastHitBy", e.getName() + " with power of bullet " + e.getPower() + " at time " + getTime());

        // show how to remove debugging property
        setDebugProperty("lastScannedRobot", null);

        // gebugging by painting to battle view
        Graphics2D g = getGraphics();

        g.setColor(Color.orange);
        g.drawOval((int) (getX() - 55), (int) (getY() - 55), 110, 110);
        g.drawOval((int) (getX() - 56), (int) (getY() - 56), 112, 112);
        g.drawOval((int) (getX() - 59), (int) (getY() - 59), 118, 118);
        g.drawOval((int) (getX() - 60), (int) (getY() - 60), 120, 120);


    }

    /**
     * Paint a red circle around our PaintingRobot
     */
    public void onPaint(Graphics2D g) {
        g.setColor(Color.red);
        g.drawOval((int) (getX() - 50), (int) (getY() - 50), 100, 100);
        g.setColor(new Color(0, 0xFF, 0, 30));
        g.fillOval((int) (getX() - 60), (int) (getY() - 60), 120, 120);
    }
}
