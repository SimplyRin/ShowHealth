package net.simplyrin.showhealth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.LuckPermsApi;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *  Created by SimplyRin on 2018/03/21
 *
 *  Copyright (C) 2018 SimplyRin
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class ShowHealth extends JavaPlugin implements Listener {

	private boolean pexBridge = false;
	private boolean luckBridge = false;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(this, this);

		this.pexBridge = this.getServer().getPluginManager().isPluginEnabled("PermissionsEx");
		if(this.pexBridge) {
			this.getServer().getConsoleSender().sendMessage("§b[ShowHealth] PermissionsEx bridge has been enabled!");
		}
		this.luckBridge = this.getServer().getPluginManager().isPluginEnabled("LuckPerms");
		if(this.luckBridge) {
			this.getServer().getConsoleSender().sendMessage("§b[ShowHealth] LuckPerms bridge has been enabled!");
		}
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

		String prefixColor = "";
		if(this.luckBridge && this.getConfig().getBoolean("Bridge.LuckPerms")) {
			LuckPermsApi api = LuckPerms.getApi();
			try {
				prefixColor = api.getUser(player.getUniqueId()).getCachedData().getMetaData(Contexts.allowAll()).getPrefix().substring(0, 2);
				message = message.replace("%player", prefixColor +  player.getName());
			} catch (Exception e) {
				message = message.replace("%player", player.getName());
			}
		}

		else if(this.pexBridge && this.getConfig().getBoolean("Bridge.PermissionsEx")) {
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
