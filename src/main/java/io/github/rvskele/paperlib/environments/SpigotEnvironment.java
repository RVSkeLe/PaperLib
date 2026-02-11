package io.github.rvskele.paperlib.environments;

public class SpigotEnvironment extends CraftBukkitEnvironment {

    public SpigotEnvironment() {
        super();
    }

    @Override
    public String getName() {
        return "Spigot";
    }

    @Override
    public boolean isSpigot() {
        return true;
    }

}
