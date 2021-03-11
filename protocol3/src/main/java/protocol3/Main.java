package protocol3;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.FileManager;
import protocol3.backend.LagProcessor;
import protocol3.backend.Notifications;
import protocol3.backend.PlayerMeta;
import protocol3.commands.About;
import protocol3.commands.Admin;
import protocol3.commands.Discord;
import protocol3.commands.DupeHand;
import protocol3.commands.Help;
import protocol3.commands.Kill;
import protocol3.commands.Kit;
import protocol3.commands.Lagfag;
import protocol3.commands.Message;
import protocol3.commands.Mute;
import protocol3.commands.Redeem;
import protocol3.commands.Reply;
import protocol3.commands.Restart;
import protocol3.commands.Say;
import protocol3.commands.Server;
import protocol3.commands.SetDonator;
import protocol3.commands.Sign;
import protocol3.commands.Stats;
import protocol3.commands.ToggleJoinMessages;
import protocol3.commands.Tps;
import protocol3.commands.Vote;
import protocol3.commands.VoteMute;
import protocol3.events.Chat;
import protocol3.events.Connection;
import protocol3.events.ItemCheckTriggers;
import protocol3.events.LagPrevention;
import protocol3.events.Move;
import protocol3.tasks.AutoAnnouncer;
import protocol3.tasks.OnTick;
import protocol3.tasks.ProcessPlaytime;

public class Main extends JavaPlugin implements Listener
{
	public static Plugin instance;
	public static OfflinePlayer Top = null;
	public static boolean alreadyRestarting = false;

	@Override
	public void onEnable()
	{

		instance = this;

		// Required files load
		System.out.println("[protocol3] Creating required files if they do not exist...");
		try
		{
			FileManager.setup();
		} catch (IOException e)
		{
			System.out.println("[protocol3] An error occured creating the necessary files.");
		}

		// Load required files
		System.out.println("[protocol3] Loading files..");
		try
		{
			PlayerMeta.loadDonators();
			PlayerMeta.loadMuted();
			PlayerMeta.loadLagfags();
		} catch (IOException e)
		{
			System.out.println("[protocol3] An error occured loading files.");
		}

		// Load timers
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagProcessor(), 1L, 1L);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new AutoAnnouncer(), 15000L, 15000L);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new ProcessPlaytime(), 20L, 20L);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new OnTick(), 1L, 1L);

		// Load listeners
		getServer().getPluginManager().registerEvents(new Chat(), this);
		getServer().getPluginManager().registerEvents(new Connection(), this);
		getServer().getPluginManager().registerEvents(new Move(), this);
		getServer().getPluginManager().registerEvents(new ItemCheckTriggers(), this);
		getServer().getPluginManager().registerEvents(new LagPrevention(), this);

		// Load ProtcolLib Listeners

		// no more wither boom
		ProtocolLibrary.getProtocolManager()
				.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_EVENT)
				{
					@Override
					public void onPacketSending(PacketEvent event)
					{
						PacketContainer packetContainer = event.getPacket();
						if (packetContainer.getIntegers().read(0) == 1023)
						{
							packetContainer.getBooleans().write(0, false);
						}
					}
				});

		// --- TASKS ---

		// Load commands
		this.getCommand("kit").setExecutor(new Kit());
		this.getCommand("mute").setExecutor(new Mute());
		this.getCommand("dupehand").setExecutor(new DupeHand());
		this.getCommand("vm").setExecutor(new VoteMute());
		this.getCommand("msg").setExecutor(new Message());
		this.getCommand("r").setExecutor(new Reply());
		this.getCommand("say").setExecutor(new Say());
		this.getCommand("discord").setExecutor(new Discord());
		this.getCommand("tps").setExecutor(new Tps());
		this.getCommand("kill").setExecutor(new Kill());
		this.getCommand("setdonator").setExecutor(new SetDonator());
		this.getCommand("about").setExecutor(new About());
		this.getCommand("vote").setExecutor(new Vote());
		this.getCommand("restart").setExecutor(new Restart());
		this.getCommand("sign").setExecutor(new Sign());
		this.getCommand("admin").setExecutor(new Admin());
		this.getCommand("stats").setExecutor(new Stats());
		this.getCommand("redeem").setExecutor(new Redeem());
		this.getCommand("lagfag").setExecutor(new Lagfag());
		this.getCommand("tjm").setExecutor(new ToggleJoinMessages());
		this.getCommand("server").setExecutor(new Server());
		this.getCommand("help").setExecutor(new Help());

		new Notifications();
	}

	@Override
	public void onDisable()
	{
		System.out.println("[protocol3] Saving files...");
		try
		{
			PlayerMeta.saveDonators();
			PlayerMeta.saveMuted();
			PlayerMeta.saveLagfags();
			PlayerMeta.writePlaytime();
		} catch (IOException ex)
		{
			System.out.println("[protocol3] Failed to save one or more files.");
		}
	}

	public static void cleanRestart()
	{
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l5 §r§6minutes."));
				try
				{
					TimeUnit.MINUTES.sleep(4);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l1 §r§6minute."));
				try
				{
					TimeUnit.SECONDS.sleep(30);
				} catch (Exception e)
				{
				}
				try
				{
					TimeUnit.SECONDS.sleep(15);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l15 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(5);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l10 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(5);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l5 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l4 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l3 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l2 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l1 §r§6second."));
				try
				{
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server is restarting."));
				Bukkit.shutdown();
			}
		};
		new Thread(r).start();
	}

	public static void quickRestart()
	{
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l1 §r§6minute."));
				try
				{
					TimeUnit.SECONDS.sleep(30);
				} catch (Exception e)
				{
				}
				try
				{
					TimeUnit.SECONDS.sleep(15);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l15 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(5);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l10 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(5);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l5 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l4 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l3 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l2 §r§6seconds."));
				try
				{
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server restarting in §6§l1 §r§6second."));
				try
				{
					TimeUnit.SECONDS.sleep(1);
				} catch (Exception e)
				{
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server is restarting."));
				Bukkit.shutdown();
			}
		};
		new Thread(r).start();
	}
}
