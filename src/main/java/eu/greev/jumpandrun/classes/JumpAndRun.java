package eu.greev.jumpandrun.classes;

import eu.greev.jumpandrun.JumpAndRuns;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class JumpAndRun {
    public static HashMap<Player, JumpAndRun> jumpAndRuns = new HashMap<>();

    private Player player;
    private int jumpCount = 0;

    private Location startLocation;
    private Location endLocation;

    private byte colorCode;

    private BukkitTask task;

    public JumpAndRun(Player player, Location startLocation) {
        this.player = player;
        this.startLocation = startLocation;
        this.colorCode = (byte) JumpAndRuns.getInstance().getMaths().randInt(0, 15);

        placeStartBlock();
        player.teleport(this.startLocation.clone().add(0.5, 1, 0.5));
        jumpAndRuns.put(player, this);

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                Output.sendActionBar(player, getActionBarText());
            }
        }.runTaskTimer(JumpAndRuns.getInstance(), 0, 20);

        tryGenerateEndLocation();
        placeEndBlock();
    }

    public void nextJump() {
        removeStartBlock();
        setStartLocation(this.endLocation);

        tryGenerateEndLocation();

        placeStartBlock();
        placeEndBlock();
        this.jumpCount++;
        updateActionBar();
        this.player.playSound(this.player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
    }

    public void placeStartBlock() {
        Block block = this.startLocation.getBlock();
        block.setType(Material.STAINED_CLAY);
        block.setData(this.colorCode);
    }

    public void removeStartBlock() {
        Block block = this.startLocation.getBlock();
        block.setType(Material.AIR);
    }

    public void placeEndBlock() {
        Block block = this.endLocation.getBlock();
        block.setType(Material.WOOL);
        block.setData(this.colorCode);
    }

    public void removeEndBlock() {
        if (this.endLocation != null) {
            Block block = this.endLocation.getBlock();
            block.setType(Material.AIR);
        }
    }

    public void tryGenerateEndLocation() {
        int tryCount = 0;
        do {
            generateEndLocation();
            tryCount++;

            if (tryCount >= 50) {
                this.player.sendMessage(
                        JumpAndRuns.prefix
                                + "§cEs konnte leider keine freie Stelle für dich gefunden werden. Bitte versuche es noch einmal."
                );
                this.cancel();
            }
        } while (this.endLocation == null);
    }

    public void generateEndLocation() {
        int maxLength = 5;
        int yAdd;
        int offset;
        int xAdd;
        int zAdd;
        Location newEndLocation;

        yAdd = JumpAndRuns.getInstance().getMaths().randInt(0, 1);
        offset = JumpAndRuns.getInstance().getMaths().randInt(0, 3);

        if (yAdd == 1) {
            maxLength--;
        }
        if (offset/2 == 1) {
            maxLength--;
        }
        if (Math.random() < 0.5) {
            xAdd = JumpAndRuns.getInstance().getMaths().randInt(2, maxLength);
            zAdd = offset;
        } else {
            xAdd = offset;
            zAdd = JumpAndRuns.getInstance().getMaths().randInt(2, maxLength);
        }
        if (Math.random() < 0.5) {
            xAdd *= -1;
        }
        if (Math.random() < 0.5) {
            zAdd *= -1;
        }

        newEndLocation = new Location(
                this.startLocation.getWorld(),
                this.startLocation.getX() + xAdd,
                this.startLocation.getY() + yAdd,
                this.startLocation.getZ() + zAdd
        );

        if (newEndLocation.getBlock().getType() == Material.AIR) {
            this.endLocation = newEndLocation;
        } else {
            this.endLocation = null;
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Location getStartLocation() {
        return this.startLocation;
    }

    public void setStartLocation(Location location) {
        this.startLocation = location;
    }

    public Location getEndLocation() {
        return this.endLocation;
    }

    public int getJumpCount() {
        return jumpCount;
    }

    public void setJumpCount(int jumpCount) {
        this.jumpCount = jumpCount;
    }

    public void cancel() {
        removeStartBlock();
        removeEndBlock();
        this.player.playSound(this.player.getLocation(), Sound.NOTE_BASS, 1, 1);
        this.task.cancel();
        jumpAndRuns.remove(this.player);
    }

    public String getActionBarText() {
        String text = "§7Du hast bereits §e§l" + jumpCount + " §r§7";
        text += jumpCount == 1 ? "Sprung geschafft" : "Sprünge geschafft";

        return text;
    }

    public void updateActionBar() {
        Output.sendActionBar(player, getActionBarText());
    }
}
