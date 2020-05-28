package loc4atnt.phoban.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import loc4atnt.phoban.data.match.MatchResult;
import loc4atnt.phoban.data.player.PlayerData;
import loc4atnt.phoban.data.player.PlayerDataManager;
import loc4atnt.phoban.dungeon.Dungeon;
import loc4atnt.phoban.dungeon.Round;
import loc4atnt.phoban.dungeon.reward.Reward;
import loc4atnt.phoban.event.PlayerWinPhoBanEvent;
import loc4atnt.phoban.game.board.GameBoard;
import loc4atnt.phoban.game.board.GameCommonBoard;
import loc4atnt.phoban.game.board.GameFeatherBoard;
import loc4atnt.phoban.game.board.GameInfoBoardReborn;
import loc4atnt.phoban.game.board.GameQuickBoard;
import loc4atnt.phoban.game.board.GameTitleManagerBoard;
import loc4atnt.phoban.game.menu.GameCompleteMenu;
import loc4atnt.phoban.main.PhoBan;
import loc4atnt.xlibs.external.wgrevent.events.RegionLeaveEvent;
import loc4atnt.xlibs.main.XLibs;
import loc4atnt.xlibs.mythicmobsutil.MMUtil;
import loc4atnt.xlibs.thread.ThreadUtil;

public class Game implements Listener {

	private static final List<String> ALLOW_COMMAND_LIST = Arrays.asList("roidoi", "phoban", "quai", "pb", "vukhi",
			"nhanvat");

	private PhoBan plugin;
	private GameBoard gameBoard;

	private boolean isStart;
	private Dungeon dun;
	private String dunId;
	private byte gameStatus;// 0=>waiting, 1=>playing, 2=>end game
	private int roundIndex;
	private List<Entity> spawningMob;
	private List<Player> teamList;// index 0 => leader
	private HashMap<String, Integer> quitedMap;
	private List<Location> lastPlayerLocaList;
	private double atkDmgAr[];
	private double atkDmgSum;
	private byte playerStatus[];// 0=>chet, 1=>song, 2=>out game, 3=>chet luon nhung o lai, 4=>chet luon nhung
								// out game
	private boolean isReparingToSpawnMob;

	private BukkitTask checkTimeRemainTask;
	private int timeRemain;// 600 giay = 10 phut

	private int killAr[];
	private int deadAr[];

	private boolean isBreakPrepairingToStart;
	private String roomId;

	public Game(String dunId, Dungeon dun, Player owner) {
		plugin = PhoBan.getInst();

		this.isStart = false;
		this.timeRemain = 900;// 600 giay = 10 phut
		isReparingToSpawnMob = false;
		isBreakPrepairingToStart = false;
		this.dun = dun;
		this.dunId = dunId;
		gameStatus = 0;
		roundIndex = 0;
		atkDmgSum = 0;
		spawningMob = new ArrayList<Entity>();
		teamList = new ArrayList<Player>();
		quitedMap = new HashMap<String, Integer>();
		lastPlayerLocaList = new ArrayList<Location>();
		roomId = dunId + String.valueOf(dun.getRoomId());

		PhoBan.getInst().getServer().getPluginManager().registerEvents(this, PhoBan.getInst());

		teamList.add(owner);
		lastPlayerLocaList.add(newCopyOfLocation(owner.getLocation()));
		owner.teleport(dun.getSpawnLoca());
		owner.sendMessage("§7§l§m=========================================================");
		owner.sendMessage("§a§lĐã tham gia phòng đấu!");
		owner.sendMessage("§b- /phoban để quản lý phòng đấu (Mời, kick người chơi, bắt đầu chiến đấu)");
		owner.sendMessage("§b- /roidoi để rời phòng đấu");
		owner.sendMessage(
				"§c‼ Nếu đánh chết hết quái rồi nhưng phó bản không spawn quái tiếp thì hãy nhấn /quai để triệu hồi đợt quái tiếp theo");
		owner.sendMessage("§7§l§m=========================================================");

		setupBoard();
		gameBoard.addPlayer(owner);
		gameBoard.updateBoard();
	}

	public int getRemainTime() {
		return timeRemain;
	}

	@EventHandler
	public void leaveRoom(RegionLeaveEvent e) {
		Player p = e.getPlayer();
		if (!teamList.contains(p))
			return;
		int index = teamList.indexOf(p);
		if (lastPlayerLocaList.get(index) == null)
			return;
		if (roomId.equals(e.getRegion().getId())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (gameStatus != 1)
			return;
		Player p = e.getPlayer();
		String name = p.getName();
		if (!quitedMap.containsKey(name))
			return;
		int index = quitedMap.get(name);
		playerStatus[index] = (byte) 0;
		quitedMap.remove(name);
		lastPlayerLocaList.set(index, newCopyOfLocation(p.getLocation()));
		p.teleport(dun.getSpawnLoca());
		teamList.set(index, p);
		plugin.getGameMnger().addGame(p, this);
		if (deadAr[index] < 6) {
			playerStatus[index] = (byte) 0;
			prepairForPlayerRespawn(p, index);
		} else {
			playerStatus[index] = (byte) 3;
			p.sendMessage(
					"§cBạn không còn được hồi sinh tiếp!§b Ở lại theo dõi hết trận đấu để nhận thưởng nếu đội của bạn chiến thắng.");
			p.setGameMode(GameMode.SPECTATOR);
		}
		gameBoard.addPlayer(p);
		gameBoard.updateBoard();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (!teamList.contains(p))
			return;
		int index = teamList.indexOf(p);
		plugin.getGameMnger().removeGame(p);
		gameBoard.removePlayer(p);
		if (gameStatus != ((byte) 1)) {
			isBreakPrepairingToStart = true;
			if (index == 0)
				handleWhenLeaderOut();
			else {
				Location l = lastPlayerLocaList.get(index);
				lastPlayerLocaList.remove(index);
				teamList.remove(index);
				p.teleport(l);
			}
		} else {
			Location l = lastPlayerLocaList.get(index);
			lastPlayerLocaList.set(index, null);
			p.teleport(l);
			if (playerStatus[index] == (byte) 0 || playerStatus[index] == (byte) 3)
				p.setGameMode(GameMode.SURVIVAL);
			if (playerStatus[index] != ((byte) 3)) {
				playerStatus[index] = (byte) 2;
				deadAr[index] += 1;
				quitedMap.put(p.getName(), index);
				handleAllPlayerOutGameOrDie();
			} else {
				playerStatus[index] = (byte) 4;
			}
		}
		gameBoard.updateBoard();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent e) {
		if (gameStatus != 1)
			return;
		Player p = e.getPlayer();
		if (!teamList.contains(p))
			return;
		e.setRespawnLocation(dun.getSpawnLoca());
		int index = teamList.indexOf(p);
		deadAr[index] += 1;
		if (deadAr[index] < 6)
			playerStatus[index] = (byte) 0;
		else {
			playerStatus[index] = (byte) 3;
			p.sendMessage(
					"§cBạn không còn được hồi sinh tiếp!§b Ở lại theo dõi hết trận đấu để nhận thưởng nếu đội của bạn chiến thắng.");
		}
		handleAllPlayerOutGameOrDie();
		if (gameStatus == 1) {
			Bukkit.getScheduler().runTaskLater(PhoBan.getInst(), new Runnable() {

				@Override
				public void run() {
					gameBoard.updateBoard();
				}
			}, 2);
			if (playerStatus[index] != ((byte) 3))
				prepairForPlayerRespawn(p, index);
			else {
				p.setGameMode(GameMode.SPECTATOR);
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (gameStatus != 1)
			return;
		Player p = e.getEntity();
		if (!teamList.contains(p))
			return;
		p.spigot().respawn();
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (gameStatus != 1)
			return;
		if (!spawningMob.contains(e.getEntity()))
			return;
		if (!(e.getDamager() instanceof Player))
			return;
		Player p = (Player) e.getDamager();
		if (!teamList.contains(p))
			return;
		int index = teamList.indexOf(p);
		Bukkit.getScheduler().scheduleSyncDelayedTask(PhoBan.getInst(), new Runnable() {

			@Override
			public void run() {
				atkDmgAr[index] += e.getDamage();
				atkDmgSum += e.getDamage();
				gameBoard.updateBoard();
			}
		}, 2);
	}

	@EventHandler
	public void onMobDeath(MythicMobDeathEvent e) {
		Entity entity = e.getEntity();
		if (spawningMob.contains(entity)) {
			if (e.getKiller() instanceof Player) {
				Player p = (Player) e.getKiller();
				int index = teamList.indexOf(p);
				if (index != -1) {
					killAr[index] += 1;
					gameBoard.updateBoard();
				}
			}
			spawningMob.remove(entity);
			if (spawningMob.size() == 0)
				runGame();
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		int endIndex = e.getMessage().indexOf(' ');
		if (endIndex < 2)
			endIndex = e.getMessage().length();
		String cmd = e.getMessage().substring(1, endIndex);
		if (!teamList.contains(p))
			return;
		if (p.hasPermission(PhoBan.permission))
			return;
		if (!ALLOW_COMMAND_LIST.contains(cmd)) {
			if (gameStatus == 0) {
				p.sendMessage("§b/phoban để quản lý phòng đấu (Mời, kick người chơi, bắt đầu chiến đấu)");
				p.sendMessage("§b/roidoi để rời phòng đấu");
			} else {
				int index = teamList.indexOf(p);
				if (playerStatus[index] == 2 || playerStatus[index] == 4)
					return;
				p.sendMessage("§cBạn đang chiến đấu!");
			}
			e.setCancelled(true);
		}
	}

	private void handleAllPlayerOutGameOrDie() {
		boolean isFailed = true;
		for (byte v : playerStatus) {
			if (v == ((byte) 1)) {
				isFailed = false;
				break;
			}
		}
		if (isFailed)
			finishGame(false);

	}

	private void prepairForPlayerRespawn(Player p, int index) {
		p.setGameMode(GameMode.SPECTATOR);
		int delayToSpawn = plugin.getParamMnger().getInt("he_so_tg_hoi_sinh") * (deadAr[index] - 1)
				+ plugin.getParamMnger().getInt("tg_hoi_sinh_goc");
		p.sendMessage("§eBạn sẽ được hồi sinh sau §a" + String.valueOf(delayToSpawn) + "§e giây");
		Bukkit.getScheduler().scheduleSyncDelayedTask(PhoBan.getInst(), new Runnable() {

			@Override
			public void run() {
				if (gameStatus == ((byte) 1)) {
					p.teleport(dun.getSpawnLoca());
					p.setGameMode(GameMode.SURVIVAL);
					playerStatus[index] = (byte) 1;
					gameBoard.updateBoard();
				}
			}
		}, delayToSpawn * 20);
	}

	public Player getMVP() {
		if (gameStatus != ((byte) 2))
			return null;
		int index = 0;
		for (int i = 1; i < atkDmgAr.length; i++) {
			if (atkDmgAr[i] > atkDmgAr[index])
				index = i;
		}
		return teamList.get(index);
	}

	private void setupBoard() {
		if (PhoBan.getInst().getServer().getPluginManager().isPluginEnabled("QuickBoard")) {
			gameBoard = new GameQuickBoard(this);
		} else if (PhoBan.getInst().getServer().getPluginManager().isPluginEnabled("InfoBoardReborn")) {
			gameBoard = new GameInfoBoardReborn(this);
		} else if (PhoBan.getInst().getServer().getPluginManager().isPluginEnabled("TitleManager")) {
			gameBoard = new GameTitleManagerBoard(this);
		} else if (PhoBan.getInst().getServer().getPluginManager().isPluginEnabled("FeatherBoard")) {
			gameBoard = new GameFeatherBoard(this);
		} else {
			gameBoard = new GameCommonBoard(this);
		}
	}

	private Location newCopyOfLocation(Location l) {
		return new Location(l.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
	}

	public boolean isStart() {
		return isStart;
	}

	public int[] getKillAr() {
		return killAr;
	}

	public int[] getDeadAr() {
		return deadAr;
	}

	public double[] getAtkDmgAr() {
		return atkDmgAr;
	}

	public double getAtkDmgSum() {
		return atkDmgSum;
	}

	public byte[] getPlayerStatus() {
		return playerStatus;
	}

	public byte getGameStatus() {
		return gameStatus;
	}

	public Player getLeader() {
		return teamList.get(0);
	}

	public Dungeon getDungeon() {
		return dun;
	}

	public List<Player> getTeamList() {
		return teamList;
	}

	public void kick(Player p) {
		if (gameStatus != 0)
			return;
		int index = teamList.indexOf(p);
		if (index < 0)
			return;
		teamList.remove(p);
		Location lastLoca = lastPlayerLocaList.get(index);
		p.teleport(lastLoca);
		lastPlayerLocaList.remove(index);
		gameBoard.removePlayer(p);
		gameBoard.updateBoard();
		p.sendMessage("§aBạn đã bị kick khỏi phòng!");
	}

	public void addMember(Player p) {
		teamList.add(p);
		lastPlayerLocaList.add(newCopyOfLocation(p.getLocation()));
		p.teleport(dun.getSpawnLoca());
		p.sendMessage("§aĐã tham gia phòng đấu!");
		p.sendMessage("§b/roidoi để rời phòng đấu");
		gameBoard.addPlayer(p);
		gameBoard.updateBoard();
	}

	protected void start() {
		isStart = true;
		PhoBan.getInst().getServer().getScheduler().runTaskAsynchronously(PhoBan.getInst(), new Runnable() {
			int times = 0;

			@Override
			public void run() {
				while (times < 10) {
					getLeader().sendMessage("§cGõ lệnh /quai để kéo các quái lại gần hoặc khi không thấy spawn quái!");
					teamList.forEach(p -> {
						XLibs.getInst().getNMS().sendTitle(p, "§b" + String.valueOf(10 - times), "", 0, 20, 0);
						p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1f, 1f);

					});
					if (isBreakPrepairingToStart) {
						isBreakPrepairingToStart = false;
						teamList.forEach(p -> {
							p.sendMessage("§cCó người thoát trận!");
						});
						return;
					}
					times++;
					ThreadUtil.delay(1000);
				}
				Bukkit.getScheduler().runTask(plugin, new Runnable() {

					@Override
					public void run() {
						teamList.forEach(p -> {
							p.teleport(dun.getSpawnLoca());
							p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f);
							PhoBan.getInst().getPlayTurnMnger().addPlayedTurn(p);
							XLibs.getInst().getNMS().sendTitle(p, "§aChuẩn Bị", "", 5, 30, 5);
						});
					}
				});
				ThreadUtil.delay(1000);
				gameStatus = 1;
				int teamSize = teamList.size();
				atkDmgAr = new double[teamSize];
				playerStatus = new byte[teamSize];
				killAr = new int[teamSize];
				deadAr = new int[teamSize];
				for (int i = 0; i < teamSize; i++) {
					atkDmgAr[i] = 0d;
					playerStatus[i] = (byte) 1;
					killAr[i] = 0;
					deadAr[i] = 0;
				}

				checkTimeRemainTask = Bukkit.getScheduler().runTaskTimer(PhoBan.getInst(), new Runnable() {

					@Override
					public void run() {
						timeRemain--;
						if (timeRemain == 0)
							finishGame(false);
						else
							gameBoard.updateBoard();
					}
				}, 20, 20);

				runGame();
			}
		});
	}

	private void runGame() {
		if (gameStatus == ((byte) 2))
			return;
		if (roundIndex >= dun.getCore().getRoundList().size()) {
			finishGame(true);
			return;
		}
		isReparingToSpawnMob = true;
		PhoBan.getInst().getServer().getScheduler().runTaskAsynchronously(PhoBan.getInst(), new Runnable() {
			int times = 0;

			@Override
			public void run() {
				ThreadUtil.delay(1000);
				Round r = dun.getCore().getRoundList().get(roundIndex);
				roundIndex++;
				while (times < r.getRestTime()) {
					if (gameStatus == 2)
						return;
					for (int i = 0; i < teamList.size(); i++) {
						Player p = teamList.get(i);
						if (playerStatus[i] == 1 && p != null) {
							XLibs.getInst().getNMS().sendTitle(p, "§e" + String.valueOf(r.getRestTime() - times),
									"§eĐợt quái mới sắp bắt đầu", 0, 20, 0);
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f);
						}
					}
					times++;
					ThreadUtil.delay(1000);
				}
				Bukkit.getScheduler().runTask(PhoBan.getInst(), new Runnable() {

					@Override
					public void run() {
						HashMap<String, Integer> mobMap = r.getMobMap();
						int amountLoca = dun.getLocaList().size();
						for (String mobTypeName : mobMap.keySet()) {
							int amount = mobMap.get(mobTypeName);
							int amountEachLoca = (int) amount / amountLoca;
							int surplusAmount = amount % amountLoca;
							for (int i = 0; i < amountLoca; i++) {
								Location spawnMobLocation = dun.getLocaList().get(i);
								int totalSpawnAmount = amountEachLoca;
								if (surplusAmount != 0) {
									totalSpawnAmount += 1;
									surplusAmount--;
								}
								MMUtil.getInst().spawnMythicMob(spawningMob, mobTypeName, spawnMobLocation,
										totalSpawnAmount);
							}
						}
						isReparingToSpawnMob = false;
						Bukkit.getScheduler().runTaskLater(PhoBan.getInst(), new Runnable() {

							@Override
							public void run() {
								if (spawningMob.size() == 0)
									runGame();
							}
						}, 60);
					}
				});
			}
		});
		PhoBan.getInst().getServer().getScheduler().scheduleSyncDelayedTask(PhoBan.getInst(), new Runnable() {

			@Override
			public void run() {
				gameBoard.updateBoard();
			}
		}, 20);
	}

	private void finishGame(boolean isWin) {
		gameStatus = (byte) 2;
		HandlerList.unregisterAll(Game.this);
		checkTimeRemainTask.cancel();
		spawningMob.forEach(e -> {
			((LivingEntity) e).setHealth(0d);
		});
		for (int i = 0; i < teamList.size(); i++) {
			if (playerStatus[i] != ((byte) 2) && playerStatus[i] != ((byte) 4)) {
				Player p = teamList.get(i);
				XLibs.getInst().getNMS().sendTitle(p, "§bTrận đấu kết thúc", isWin ? "§eThắng" : "§cThua", 0, 60, 20);
				Bukkit.getScheduler().runTaskLater(PhoBan.getInst(), new Runnable() {

					@Override
					public void run() {
						p.playSound(p.getLocation(), isWin ? Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST
								: Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1f, 1f);
					}
				}, 35);
			}
		}
		Bukkit.getScheduler().runTaskLater(PhoBan.getInst(), new Runnable() {

			@Override
			public void run() {
				gameBoard.destroy();
				PlayerDataManager mng = plugin.getPlayerDataMnger();
				Player mvp = getMVP();
				HashMap<Player, MatchResult> resultMap = new HashMap<Player, MatchResult>();
				HashMap<Player, List<Reward>> rewardMap = new HashMap<Player, List<Reward>>();
				HashMap<Player, MatchResult> winner = new HashMap<Player, MatchResult>();
				int teamSize = teamList.size();
				for (int i = 0; i < teamSize; i++) {
					Player p = teamList.get(i);
					if (playerStatus[i] == ((byte) 0) || playerStatus[i] == ((byte) 3)) {
						p.setGameMode(GameMode.SURVIVAL);
						p.teleport(dun.getSpawnLoca());
					} else if (playerStatus[i] == ((byte) 1)) {
						p.teleport(dun.getSpawnLoca());
					} else {
						resultMap.put(p, null);
						continue;
					}
					PlayerData pData = mng.getPlayerData(p);
					double damagedPercent = getAtkDmgAr()[i] / getAtkDmgSum() * 100;
					MatchResult result = new MatchResult(isWin, (float) damagedPercent, deadAr[i], killAr[i],
							mvp.equals(p), dunId);
					pData.addMatchResult(result);
					resultMap.put(p, result);
					// reward
					if (isWin) {
						winner.put(p, result);
						List<Reward> rewardList = dun.getCore().getRewardList();
						List<Reward> rewardForPlayerList = new ArrayList<Reward>();
						for (Reward r : rewardList) {
							if (r.canApplyReward(p, teamSize))
								rewardForPlayerList.add(r);
						}
						rewardMap.put(p, rewardForPlayerList);
					}
				}
				for (int i = 0; i < teamSize; i++) {
					if (playerStatus[i] != ((byte) 2) && playerStatus[i] != ((byte) 4)) {
						Player p = teamList.get(i);
						Location l = lastPlayerLocaList.get(i);
						p.teleport(l);
						new GameCompleteMenu(p, resultMap, rewardMap.get(p), teamSize).open();
					}
				}

				Bukkit.getScheduler().runTaskLater(PhoBan.getInst(), new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < teamSize; i++) {
							if (playerStatus[i] == ((byte) 2) || playerStatus[i] == ((byte) 4))
								continue;
							Player p = teamList.get(i);
							plugin.getGameMnger().removeGame(p);
						}
					}
				}, 60);

				if (isWin) {
					PlayerWinPhoBanEvent event = new PlayerWinPhoBanEvent(winner, dunId);
					Bukkit.getServer().getPluginManager().callEvent(event);
				}
				plugin.getDungeonMnger().resetRoom(dunId, dun);
			}
		}, 80);
	}

	public void quitGame(Player p) {
		int index = teamList.indexOf(p);
		if (gameStatus == ((byte) 1)) {
			Location l = lastPlayerLocaList.get(index);
			lastPlayerLocaList.set(index, null);
			p.teleport(l);
			if (playerStatus[index] == 3) {
				playerStatus[index] = (byte) 4;
				p.setGameMode(GameMode.SURVIVAL);
			} else {
				playerStatus[index] = (byte) 2;
				handleAllPlayerOutGameOrDie();
			}
		} else if (gameStatus == ((byte) 0)) {
			isBreakPrepairingToStart = true;
			if (index == 0) {
				handleWhenLeaderOut();
			} else {
				teamList.remove(index);
				Location l = lastPlayerLocaList.get(index);
				p.teleport(l);
				lastPlayerLocaList.remove(index);
			}
		}
		gameBoard.removePlayer(p);
		gameBoard.updateBoard();
	}

	private void handleWhenLeaderOut() {
		if (gameStatus > ((byte) 0))
			return;
		gameStatus = (byte) 3;
		for (int i = 0; i < teamList.size(); i++) {
			Player p = teamList.get(i);
			Location l = lastPlayerLocaList.get(i);
			lastPlayerLocaList.set(i, null);
			p.teleport(l);
			plugin.getGameMnger().removeGame(p);
			if (i > 0)
				p.sendMessage("§ePhòng đấu bị hủy do đội trưởng thoát trận!");
		}
		teamList.clear();
		lastPlayerLocaList.clear();
		gameBoard.destroy();
		HandlerList.unregisterAll(Game.this);
		plugin.getDungeonMnger().resetRoom(dunId, dun);
	}

	public void nextRound(Player player) {
		if (gameStatus != 1) {
			player.sendMessage("§cHãy chuẩn bị!");
			return;
		}
		if (!isReparingToSpawnMob) {
			for (int i = 0; i < spawningMob.size(); i++) {
				Entity e = spawningMob.get(i);
				if (e == null || e.isDead())
					spawningMob.remove(i);
				e.teleport(getLeader());
			}
			if (spawningMob.size() == 0) {
				if (roundIndex < (dun.getCore().getRoundList().size())) {
					teamList.forEach(p -> {
						p.sendMessage("§aChuẩn bị triệu hồi đợt quái tiếp theo!");
					});
				}
				runGame();
			}

//			if (spawningMob.size() > 4) {
//				player.sendMessage("§cCòn quá nhiều quái!");
//				return;
//			}
//			if (roundIndex < (dun.getCore().getRoundList().size() - 1)) {
//				teamList.forEach(p -> {
//					p.sendMessage("§aChuẩn bị triệu hồi đợt quái tiếp theo!");
//				});
//				runGame();
//			} else if (roundIndex <= (dun.getCore().getRoundList().size())) {
//				for (int i = 0; i < spawningMob.size(); i++) {
//					Entity e = spawningMob.get(i);
//					e.teleport(getLeader());
//				}
//				if (spawningMob.size() == 0) {
//					if (roundIndex < (dun.getCore().getRoundList().size())) {
//						teamList.forEach(p -> {
//							p.sendMessage("§aChuẩn bị triệu hồi đợt quái tiếp theo!");
//						});
//					}
//					runGame();
//				}
//			}
		} else {
			player.sendMessage("§cĐang triệu hồi quái!");
		}
	}
}
