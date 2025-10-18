package de.larsensmods.mctrains;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import de.larsensmods.mctrains.config.MCTConfig;
import de.larsensmods.mctrains.interfaces.IChainable;
import de.larsensmods.mctrains.networking.ClientboundSyncMinecartTrainPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MinecartTrains implements ModInitializer {

    public static final String MOD_ID = "minecart-trains";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static MCTConfig CONFIG;

    // NBT 键与持久化回退文件
    private static final String NBT_PARENT_KEY = "minecart_trains:parent_id";
    private static final File FALLBACKS_FILE = new File("config", "minecart-trains-fallbacks.json");
    private static final Gson FALLBACKS_GSON = new GsonBuilder().create();

    // 内存回退缓存（player UUID -> parent UUID）
    private static final ConcurrentMap<UUID, UUID> PLAYER_PARENT_FALLBACK = new ConcurrentHashMap<>();

    @Override
    public void onInitialize() {
        // 先加载持久化回退（如果存在）
        loadPersistentFallbacks();

        // 再加载/创建配置
        loadOrCreateConfig();

        // 保留注册 packet
        PayloadTypeRegistry.playS2C().register(ClientboundSyncMinecartTrainPacket.TYPE, ClientboundSyncMinecartTrainPacket.CODEC);

        // UseEntityCallback：潜行 + 链条行为（服务器端执行主要逻辑）
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(entity instanceof AbstractMinecartEntity cart)) return ActionResult.PASS;
            ItemStack stack = player.getStackInHand(hand);

            if (!player.isSneaking() || !stack.isOf(Items.CHAIN) || CONFIG == null || !CONFIG.enableCartChaining()) {
                return ActionResult.PASS;
            }

            if (!(world instanceof ServerWorld server)) {
                return ActionResult.SUCCESS;
            }

            UUID stored = getStackParent(stack, player);

            if (stored != null && !cart.getUuid().equals(stored)) {
                if (server.getEntity(stored) instanceof AbstractMinecartEntity parent) {
                    Set<IChainable> train = new HashSet<>();
                    train.add(parent);

                    AbstractMinecartEntity nextChainedParent;
                    while ((nextChainedParent = parent.getChainedParent()) != null && !train.contains(nextChainedParent)) {
                        train.add(nextChainedParent);
                    }

                    if (train.contains(cart) || parent.getChainedChild() != null) {
                        player.sendMessage(Text.translatable(MOD_ID + ".invalid_chaining").formatted(Formatting.RED), true);
                    } else {
                        if (cart.getChainedParent() != null) {
                            IChainable.unsetChainedParentChild(cart, cart.getChainedParent());
                        }
                        IChainable.setChainedParentChild(parent, cart);
                    }
                } else {
                    removeStackParent(stack, player);
                }

                world.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.NEUTRAL, 1f, 1.1f);

                if (!player.isCreative()) {
                    stack.decrement(1);
                }

                removeStackParent(stack, player);
            } else {
                setStackParent(stack, cart.getUuid(), player);
                world.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.NEUTRAL, 1f, 1.1f);
            }

            return ActionResult.SUCCESS;
        });
    }

    // --------------------------
    // 配置加载（保留原来的行为）
    // --------------------------
    private void loadOrCreateConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File configFile = new File("config", "minecart-trains.json");
        boolean createNewConfig = false;
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                CONFIG = gson.fromJson(reader, MCTConfig.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException | JsonIOException e) {
                LOGGER.warn("Error reading config file", e);
                if (!configFile.renameTo(new File("config", "minecart-trains.json.bak"))) {
                    throw new RuntimeException("Couldn't access broken config file, aborting...", e);
                }
                createNewConfig = true;
            }
        } else {
            createNewConfig = true;
        }
        if (createNewConfig) {
            CONFIG = new MCTConfig();
            configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(CONFIG, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // --------------------------
    // ItemStack NBT runtime adapter（运行期反射，兼容不同映射）
    // --------------------------
    private static final class ItemStackNbtRuntime {
        private static Method methodGetOrCreate = null;
        private static Method methodGet = null;
        private static Method methodHas = null;
        private static Method methodSet = null;
        private static Method methodRemove = null;
        private static boolean initialized = false;

        private static void ensureInit() {
            if (initialized) return;
            initialized = true;
            Class<?> cls = ItemStack.class;
            Method[] methods = cls.getMethods();

            for (Method m : methods) {
                try {
                    String name = m.getName().toLowerCase();
                    if (m.getParameterCount() == 0 && m.getReturnType() == NbtCompound.class) {
                        if (name.contains("getorcreate")) methodGetOrCreate = m;
                        else if (methodGet == null && (name.contains("get") && (name.contains("tag") || name.contains("nbt") || name.equals("get")))) methodGet = m;
                    }
                    if (m.getParameterCount() == 0 && (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)) {
                        if (name.contains("has") || name.contains("hastag") || name.contains("hasnbt")) methodHas = m;
                    }
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == NbtCompound.class) {
                        if (name.contains("set") || name.contains("settag") || name.contains("setnbt")) methodSet = m;
                    }
                    if (m.getParameterCount() == 0 && m.getReturnType() == void.class) {
                        if (name.contains("remove") && (name.contains("nbt") || name.contains("tag") || name.equals("remove"))) methodRemove = m;
                    }
                } catch (Throwable ignored) {}
            }

            // Declared fallback
            try {
                if (methodSet == null) {
                    Method d = ItemStack.class.getDeclaredMethod("setTag", NbtCompound.class);
                    d.setAccessible(true);
                    methodSet = d;
                }
                if (methodGetOrCreate == null) {
                    Method d = ItemStack.class.getDeclaredMethod("getOrCreateTag");
                    d.setAccessible(true);
                    methodGetOrCreate = d;
                }
                if (methodGet == null) {
                    Method d = ItemStack.class.getDeclaredMethod("getTag");
                    d.setAccessible(true);
                    methodGet = d;
                }
                if (methodHas == null) {
                    Method d = ItemStack.class.getDeclaredMethod("hasTag");
                    d.setAccessible(true);
                    methodHas = d;
                }
                if (methodRemove == null) {
                    Method d = ItemStack.class.getDeclaredMethod("removeTag");
                    d.setAccessible(true);
                    methodRemove = d;
                }
            } catch (Throwable ignored) {}
        }

        public static NbtCompound getOrCreateTag(ItemStack stack) {
            ensureInit();
            try {
                if (methodGetOrCreate != null) {
                    Object r = methodGetOrCreate.invoke(stack);
                    if (r instanceof NbtCompound) return (NbtCompound) r;
                }
                if (methodGet != null) {
                    Object r = methodGet.invoke(stack);
                    if (r instanceof NbtCompound) return (NbtCompound) r;
                }
                NbtCompound n = new NbtCompound();
                if (methodSet != null) {
                    methodSet.invoke(stack, n);
                    return n;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        public static NbtCompound getTag(ItemStack stack) {
            ensureInit();
            try {
                if (methodGet != null) {
                    Object r = methodGet.invoke(stack);
                    if (r instanceof NbtCompound) return (NbtCompound) r;
                }
                if (methodGetOrCreate != null) {
                    Object r = methodGetOrCreate.invoke(stack);
                    if (r instanceof NbtCompound) return (NbtCompound) r;
                }
            } catch (Throwable ignored) {}
            return null;
        }

        @SuppressWarnings("unused")
        public static boolean hasTag(ItemStack stack) {
            ensureInit();
            try {
                if (methodHas != null) {
                    Object r = methodHas.invoke(stack);
                    if (r instanceof Boolean) return (Boolean) r;
                }
                return getTag(stack) != null;
            } catch (Throwable ignored) {}
            return false;
        }

        public static void setTag(ItemStack stack, NbtCompound tag) {
            ensureInit();
            try {
                if (methodSet != null) {
                    methodSet.invoke(stack, tag);
                    return;
                }
                try {
                    Method fallback = ItemStack.class.getDeclaredMethod("setTag", NbtCompound.class);
                    fallback.setAccessible(true);
                    fallback.invoke(stack, tag);
                } catch (Throwable ignored) {}
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        public static void removeTag(ItemStack stack) {
            ensureInit();
            try {
                if (methodRemove != null) {
                    methodRemove.invoke(stack);
                    return;
                }
                if (methodSet != null) {
                    methodSet.invoke(stack, new Object[]{ null });
                    return;
                }
                try {
                    Method fallback = ItemStack.class.getDeclaredMethod("setTag", NbtCompound.class);
                    fallback.setAccessible(true);
                    fallback.invoke(stack, new Object[]{ null });
                } catch (Throwable ignored) {}
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    // --------------------------
    // NBT + fallback helpers（使用磁盘持久化回退）
    // --------------------------
    private static void setStackParent(ItemStack stack, UUID uuid, PlayerEntity player) {
        if (stack != null) {
            NbtCompound n = ItemStackNbtRuntime.getOrCreateTag(stack);
            if (n != null) {
                n.putUuid(NBT_PARENT_KEY, uuid);
                ItemStackNbtRuntime.setTag(stack, n);
                // 清理持久化回退与内存缓存（以 ItemStack 为准）
                clearPlayerFallback(player);
                System.out.println("mctrains: setStackParent wrote uuid=" + uuid + " nbt=" + n.toString());
                return;
            }
        }
        // fallback: 写入内存缓存并持久化到磁盘
        if (player != null) {
            savePlayerFallback(player, uuid);
        } else {
            System.out.println("mctrains: setStackParent failed: no stack nbt and no player fallback");
        }
    }

    private static UUID getStackParent(ItemStack stack, PlayerEntity player) {
        if (stack != null) {
            NbtCompound n = ItemStackNbtRuntime.getTag(stack);
            if (n != null && n.contains(NBT_PARENT_KEY)) {
                try {
                    return n.getUuid(NBT_PARENT_KEY);
                } catch (Throwable ignored) {}
            }
        }
        // 内存缓存优先
        if (player != null) {
            UUID mem = PLAYER_PARENT_FALLBACK.get(player.getUuid());
            if (mem != null) {
                System.out.println("mctrains: getStackParent returned from memory fallback " + mem + " for player " + player.getUuid());
                return mem;
            }
            // 从磁盘持久化读取（已在内存缓存中）
            UUID loaded = loadPlayerFallback(player);
            if (loaded != null) {
                System.out.println("mctrains: getStackParent returned from persistent fallback " + loaded + " for player " + player.getUuid());
                return loaded;
            }
        }
        return null;
    }

    private static void removeStackParent(ItemStack stack, PlayerEntity player) {
        boolean removed = false;
        if (stack != null) {
            NbtCompound n = ItemStackNbtRuntime.getTag(stack);
            if (n != null && n.contains(NBT_PARENT_KEY)) {
                n.remove(NBT_PARENT_KEY);
                if (n.isEmpty()) {
                    ItemStackNbtRuntime.removeTag(stack);
                } else {
                    ItemStackNbtRuntime.setTag(stack, n);
                }
                removed = true;
                System.out.println("mctrains: removeStackParent removed from item");
            }
        }
        if (player != null) {
            UUID prev = PLAYER_PARENT_FALLBACK.remove(player.getUuid());
            if (prev != null) {
                removed = true;
                clearPlayerFallback(player);
                System.out.println("mctrains: removeStackParent removed fallback for player " + player.getUuid());
            }
        }
        if (!removed) {
            System.out.println("mctrains: removeStackParent nothing removed");
        }
    }

    // --------------------------
    // 磁盘持久化回退方法（JSON）
    // --------------------------
    private static void loadPersistentFallbacks() {
        try {
            if (FALLBACKS_FILE.exists()) {
                try (FileReader r = new FileReader(FALLBACKS_FILE)) {
                    Type t = new TypeToken<Map<String, String>>(){}.getType();
                    Map<String, String> m = FALLBACKS_GSON.fromJson(r, t);
                    if (m != null) {
                        PLAYER_PARENT_FALLBACK.clear();
                        for (Map.Entry<String, String> e : m.entrySet()) {
                            try {
                                UUID playerId = UUID.fromString(e.getKey());
                                UUID parentId = UUID.fromString(e.getValue());
                                PLAYER_PARENT_FALLBACK.put(playerId, parentId);
                            } catch (Throwable ignored) {}
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            LOGGER.warn("mctrains: failed to load fallbacks file", ex);
        }
    }

    private static synchronized void persistFallbacks() {
        try {
            FALLBACKS_FILE.getParentFile().mkdirs();
            try (FileWriter w = new FileWriter(FALLBACKS_FILE)) {
                Map<String,String> out = new java.util.HashMap<>();
                for (Map.Entry<UUID,UUID> e : PLAYER_PARENT_FALLBACK.entrySet()) {
                    out.put(e.getKey().toString(), e.getValue().toString());
                }
                FALLBACKS_GSON.toJson(out, w);
            }
        } catch (Throwable ex) {
            LOGGER.warn("mctrains: failed to persist fallbacks file", ex);
        }
    }

    private static void savePlayerFallback(PlayerEntity player, UUID parent) {
        PLAYER_PARENT_FALLBACK.put(player.getUuid(), parent);
        persistFallbacks();
        LOGGER.debug("mctrains: saved fallback to memory+file for player {} -> {}", player.getUuid(), parent);
    }

    private static UUID loadPlayerFallback(PlayerEntity player) {
        return PLAYER_PARENT_FALLBACK.get(player.getUuid());
    }

    private static void clearPlayerFallback(PlayerEntity player) {
        PLAYER_PARENT_FALLBACK.remove(player.getUuid());
        persistFallbacks();
        LOGGER.debug("mctrains: cleared fallback for player {}", player.getUuid());
    }
}