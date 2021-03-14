package protocol3.commands;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import protocol3.backend.Config;
import protocol3.backend.PlayerMeta;

// INTERNAL USE ONLY

public class DupeHand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!PlayerMeta.isOp(sender)) {
			Player player = (Player) sender;
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 255));
			player.kickPlayer("§6get fucked newfag [pog]");
			return true;
		} else {
			int rewardMultiplier = Integer.parseInt(Config.getValue("vote.multiplier"));
			Player player = Bukkit.getPlayer(args[0]);
			ItemStack itemInHand = player.getInventory().getItemInMainHand();
			if (Config.getValue("vote.heal").equals("true")) {
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			}
			IntStream.range(0, rewardMultiplier).mapToObj(x -> itemInHand).forEach(modItemInHand -> {
				if (modItemInHand.getItemMeta() != null) {
					if (modItemInHand.getItemMeta().hasLore()) {
						ItemMeta im = modItemInHand.getItemMeta();
						im.setLore(null);
						modItemInHand.setItemMeta(im);
					}
				}

				HashMap<Integer, ItemStack> didntFit = player.getInventory().addItem(modItemInHand);
				if (!didntFit.values().isEmpty()) {
					didntFit.forEach((key, value) -> {
						for (int y = 0; y > key; y++) {
							player.getWorld().dropItem(player.getLocation(), value);
						}
					});
				}
			});
			return true;
		}
	}

}
