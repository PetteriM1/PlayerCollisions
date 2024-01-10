package petterim1.playercollisions;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.*;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;

public class Main extends PluginBase implements Listener {

    private double strengthReducer;

    private static final Vector3 vector = new Vector3(0, 0, 0);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        strengthReducer = getConfig().getDouble("strengthReducer", 1.5);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PlayerMoveEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        for (Player viewer : p.getViewers().values()) {
            if (!(p.getX() == viewer.getX() && p.getZ() == viewer.getZ()) && (Math.abs(viewer.getX() - p.getX()) + Math.abs(viewer.getZ() - p.getZ())) < 0.8) {
                if (from.distanceSquared(viewer) > to.distanceSquared(viewer)) {
                    double kb = from.distanceSquared(to) / strengthReducer;
                    knockBack(viewer, viewer.getX() - p.getX(), viewer.getZ() - p.getZ(), kb);
                    knockBack(p, p.getX() - viewer.getX(), p.getZ() - viewer.getZ(), kb);
                }
            }
        }
    }

    private static void knockBack(Entity entity, double x, double z, double base) {
        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }
        f = 1.0 / f;
        Vector3 motion = vector.setComponents(entity.motionX, entity.motionY, entity.motionZ);
        motion.x /= 2.0;
        motion.y /= 2.0;
        motion.z /= 2.0;
        motion.x += x * f * base;
        motion.y += base;
        motion.z += z * f * base;
        if (motion.y > 0.1) {
            motion.y = 0.1;
        }
        entity.setMotion(motion);
    }
}
