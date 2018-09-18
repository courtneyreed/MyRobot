package crrb;

import java.awt.*;
import robocode.*;
import robocode.Robot;

/**
 * Created by eahscs on 9/12/2018.
 */
public class Test extends AdvancedRobot {

    int dir=1;
    int moveDirection=1;
    /**
     * PaintingRobot's run method - Seesaw
     */
    public void run() {

        while (true) {

            //setAdjustGunForRobotTurn(true);
            //setAdjustRadarForGunTurn(true);
            //setAdjustGunForRobotTurn(true);
            //setTurnGunLeftRadians(Math.PI);
            turnRadarRightRadians(Double.POSITIVE_INFINITY);

            execute();
        }
    }

    /**
     * Fire when we see a robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        //demonstrate feature of debugging properties on RobotDialog
        //setTurnRadarLeftRadians(getRadarTurnRemainingRadians());

        if (e.getDistance() < 50 && getEnergy() > 50) {
            fire(3);
        }
        else {
            fire(1);
        }

        scan();
        execute();



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

        turnLeft(90 - e.getBearing());
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
