package de.michaelbrauer.redeemify.integrations;

import de.michaelbrauer.redeemify.Redeemify;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class LuckPermsIntegration {
    
    private final Redeemify plugin;
    private LuckPerms luckPerms;
    private boolean enabled = false;

    public LuckPermsIntegration(Redeemify plugin) {
        this.plugin = plugin;
        setupLuckPerms();
    }

    private void setupLuckPerms() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            plugin.getLogger().info("LuckPerms not found - permission features disabled");
            return;
        }

        try {
            luckPerms = LuckPermsProvider.get();
            enabled = true;
            plugin.getLogger().info("LuckPerms integration enabled");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to setup LuckPerms integration: " + e.getMessage());
        }
    }

    public boolean isEnabled() {
        return enabled && luckPerms != null;
    }

    public CompletableFuture<Boolean> addPermission(Player player, String permission, Duration duration) {
        if (!isEnabled()) return CompletableFuture.completedFuture(false);

        return luckPerms.getUserManager().loadUser(player.getUniqueId())
                .thenCompose(user -> {
                    if (user == null) return CompletableFuture.completedFuture(false);

                    Node node;
                    if (duration != null) {
                        node = Node.builder(permission)
                                .expiry(duration)
                                .build();
                    } else {
                        node = Node.builder(permission).build();
                    }

                    user.data().add(node);
                    return luckPerms.getUserManager().saveUser(user)
                            .thenApply(v -> true);
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().warning("Failed to add permission " + permission + " to " + player.getName() + ": " + throwable.getMessage());
                    return false;
                });
    }

    public CompletableFuture<Boolean> addGroup(Player player, String group, Duration duration) {
        if (!isEnabled()) return CompletableFuture.completedFuture(false);

        return luckPerms.getUserManager().loadUser(player.getUniqueId())
                .thenCompose(user -> {
                    if (user == null) return CompletableFuture.completedFuture(false);

                    Node node;
                    if (duration != null) {
                        node = Node.builder("group." + group)
                                .expiry(duration)
                                .build();
                    } else {
                        node = Node.builder("group." + group).build();
                    }

                    user.data().add(node);
                    return luckPerms.getUserManager().saveUser(user)
                            .thenApply(v -> true);
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().warning("Failed to add group " + group + " to " + player.getName() + ": " + throwable.getMessage());
                    return false;
                });
    }

    public CompletableFuture<Boolean> removePermission(Player player, String permission) {
        if (!isEnabled()) return CompletableFuture.completedFuture(false);

        return luckPerms.getUserManager().loadUser(player.getUniqueId())
                .thenCompose(user -> {
                    if (user == null) return CompletableFuture.completedFuture(false);

                    user.data().remove(Node.builder(permission).build());
                    return luckPerms.getUserManager().saveUser(user)
                            .thenApply(v -> true);
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().warning("Failed to remove permission " + permission + " from " + player.getName() + ": " + throwable.getMessage());
                    return false;
                });
    }
}