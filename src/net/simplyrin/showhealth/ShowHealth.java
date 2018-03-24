package net.simplyrin.showhealth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Created by SimplyRin on 2018/03/21
 */
public class ShowHealth extends JavaPlugin implements Listener {

	private boolean pexBridge = false;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(this, this);

		this.pexBridge = this.getServer().getPluginManager().isPluginEnabled("PermissionsEx");
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Projectile)) {
			return;
		}

		if(!(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
			return;
		}

		if(!(event.getEntity() instanceof Player)) {
			return;
		}

		Player shooter = (Player) ((Projectile) event.getDamager()).getShooter();
		Player player = (Player) event.getEntity();

		String color = "";
		Double health = Double.valueOf(String.format("%.1f", player.getHealth())) - event.getFinalDamage();
		if(health >= 0) {
			color = "&c";
		}
		if(health >= 15) {
			color = "&e";
		}
		if(health >= 25) {
			color = "&a";
		}

		String message = this.getConfig().getString("Message");

		if(this.pexBridge) {
			String prefixColor = "";
			try {
				prefixColor = PermissionsEx.getUser(player).getGroups()[0].getPrefix().substring(0, 2);
			} catch (Exception e) {
				try {
					prefixColor = PermissionsEx.getUser(player).getPrefix().substring(0, 2);
				} catch (Exception e1) {
				}
			}
			message = message.replace("%player", prefixColor +  player.getName());
		} else {
			message = message.replace("%player", player.getName());
		}

		message = message.replace("%hp", color + String.format("%.1f", health));

		shooter.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

}
