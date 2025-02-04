package me.rawdiamondmc.tpchat;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class RawTpChat {
    public static final ModInitializer MAIN = () -> {
        CommandRegistrationCallback.EVENT.register(((dispatcher, commandRegistryAccess, registrationEnvironment) -> {
            dispatcher.register(CommandManager.literal("tpchat")
                    .executes(context -> {
                        final ServerCommandSource source = context.getSource();
                        if (source.isExecutedByPlayer()) {
                            final ServerPlayerEntity player = source.getPlayer();
                            assert player != null;
                            final String dimensionId = player.getServerWorld().getDimensionEntry().getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
                            final Vec3d location = new Vec3d(player.getX(), player.getY(), player.getZ());
                            execute(source, dimensionId, location, player.getYaw(), player.getPitch(), null);
                            return Command.SINGLE_SUCCESS;
                        }
                        throw new IllegalStateException("<dimension> and <location> is required on the console!");
                    })
                    .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                            .then(CommandManager.argument("location", Vec3ArgumentType.vec3())
                                    .executes(context -> {
                                        final ServerCommandSource source = context.getSource();
                                        final String dimensionId = DimensionArgumentType.getDimensionArgument(context, "dimension").getDimensionEntry().getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
                                        final Vec3d location = Vec3ArgumentType.getVec3(context, "location");
                                        execute(source, dimensionId, location, null);
                                        return Command.SINGLE_SUCCESS;
                                    })
                                    .then(CommandManager.argument("rotation", RotationArgumentType.rotation())
                                            .executes(context -> {
                                                final ServerCommandSource source = context.getSource();
                                                final String dimensionId = DimensionArgumentType.getDimensionArgument(context, "dimension").getDimensionEntry().getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
                                                final Vec3d location = Vec3ArgumentType.getVec3(context, "location");
                                                final Vec2f rotation = RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(source);
                                                execute(source, dimensionId, location, rotation.x, rotation.y, null);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                            .then(CommandManager.argument("text", TextArgumentType.text())
                                                    .requires(source -> source.hasPermissionLevel(2))
                                                    .executes(context -> {
                                                        final ServerCommandSource source = context.getSource();
                                                        final String dimensionId = DimensionArgumentType.getDimensionArgument(context, "dimension").getDimensionEntry().getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
                                                        final Vec3d location = Vec3ArgumentType.getVec3(context, "location");
                                                        final Text text = TextArgumentType.getTextArgument(context, "text");
                                                        final Vec2f rotation = RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(source);
                                                        execute(source, dimensionId, location, rotation.x, rotation.y, text);
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                            .then(CommandManager.literal("msg")
                                                    .then(CommandManager.argument("message", MessageArgumentType.message())
                                                            .executes(context -> {
                                                                final ServerCommandSource source = context.getSource();
                                                                final String dimensionId = DimensionArgumentType.getDimensionArgument(context, "dimension").getDimensionEntry().getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
                                                                final Vec3d location = Vec3ArgumentType.getVec3(context, "location");
                                                                final Vec2f rotation = RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(source);
                                                                final Text text = MessageArgumentType.getMessage(context, "message");
                                                                execute(source, dimensionId, location, rotation.x, rotation.y, text);
                                                                return Command.SINGLE_SUCCESS;
                                                            })
                                                    )
                                            )
                                    )
                                    .then(CommandManager.argument("text", TextArgumentType.text())
                                            .requires(source -> source.hasPermissionLevel(2))
                                            .executes(context -> {
                                                final ServerCommandSource source = context.getSource();
                                                final String dimensionId = DimensionArgumentType.getDimensionArgument(context, "dimension").getDimensionEntry().getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
                                                final Vec3d location = Vec3ArgumentType.getVec3(context, "location");
                                                final Text text = TextArgumentType.getTextArgument(context, "text");
                                                execute(source, dimensionId, location, text);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                                    .then(CommandManager.literal("msg")
                                            .then(CommandManager.argument("message", MessageArgumentType.message())
                                                    .executes(context -> {
                                                        final ServerCommandSource source = context.getSource();
                                                        final String dimensionId = DimensionArgumentType.getDimensionArgument(context, "dimension").getDimensionEntry().getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
                                                        final Vec3d location = Vec3ArgumentType.getVec3(context, "location");
                                                        final Text text = MessageArgumentType.getMessage(context, "message");
                                                        execute(source, dimensionId, location, text);
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                            )
                    )
                    .then(CommandManager.argument("text", TextArgumentType.text())
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> {
                                final ServerCommandSource source = context.getSource();
                                if (source.isExecutedByPlayer()) {
                                    final ServerPlayerEntity player = source.getPlayer();
                                    assert player != null;
                                    final String dimensionId = player.getServerWorld().getDimensionEntry().getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
                                    final Vec3d location = new Vec3d(player.getX(), player.getY(), player.getZ());
                                    final Text text = TextArgumentType.getTextArgument(context, "text");
                                    execute(source, dimensionId, location, player.getYaw(), player.getPitch(), text);
                                    return Command.SINGLE_SUCCESS;
                                }
                                throw new IllegalStateException("<dimension> and <location> is required on the console!");
                            })
                    )
                    .then(CommandManager.literal("msg")
                            .then(CommandManager.argument("message", MessageArgumentType.message())
                                    .executes(context -> {
                                        final ServerCommandSource source = context.getSource();
                                        if (source.isExecutedByPlayer()) {
                                            final ServerPlayerEntity player = source.getPlayer();
                                            assert player != null;
                                            final String dimensionId = player.getServerWorld().getDimensionEntry().getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
                                            final Vec3d location = new Vec3d(player.getX(), player.getY(), player.getZ());
                                            final Text text = MessageArgumentType.getMessage(context, "message");
                                            execute(source, dimensionId, location, text);
                                            return Command.SINGLE_SUCCESS;
                                        }
                                        throw new IllegalStateException("<dimension> and <location> is required on the console!");
                                    })
                            )
                    )
            );
        }));
    };

    private RawTpChat() {
    }

    private static void execute(ServerCommandSource source, String dimensionId, Vec3d location, @Nullable Text text) {
        MutableText mutableText = (MutableText) text;
        if (mutableText == null)
            mutableText = ((MutableText) Text.of("[")).append(Text.translatableWithFallback("dimension." + dimensionId.replace(':', '.'), dimensionId)).append(Text.of(" " + Math.round(location.x) + ", " + Math.round(location.y) + ", " + Math.round(location.y) + "]"));
        final Style newStyle = mutableText.getStyle()
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.coordinates.tooltip")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/execute in " + dimensionId + " run tp @s " + location.x + " " + location.y + " " + location.z))
                .withColor(Formatting.YELLOW);
        if (source.isExecutedByPlayer()) {
            source.getServer().getPlayerManager().broadcast(Text.translatable("raw-tpchat.share.player", source.getPlayer().getName()).append(mutableText.setStyle(newStyle)), false);
        } else {
            source.getServer().getPlayerManager().broadcast(Text.translatable("raw-tpchat.share.server").append(mutableText.setStyle(newStyle)), false);
        }
    }

    private static void execute(ServerCommandSource source, String dimensionId, Vec3d location, float yaw, float pitch, @Nullable Text text) {
        MutableText mutableText = (MutableText) text;
        if (mutableText == null)
            mutableText = ((MutableText) Text.of("[")).append(Text.translatableWithFallback("dimension." + dimensionId.replace(':', '.'), dimensionId)).append(Text.of(" " + Math.round(location.x) + ", " + Math.round(location.y) + ", " + Math.round(location.y) + "]"));
        final Style newStyle = mutableText.getStyle()
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.coordinates.tooltip")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/execute in " + dimensionId + " run tp @s " + location.x + " " + location.y + " " + location.z + " " + yaw + " " + pitch))
                .withColor(Formatting.YELLOW);
        if (source.isExecutedByPlayer()) {
            source.getServer().getPlayerManager().broadcast(Text.translatable("raw-tpchat.share.player", source.getPlayer().getName()).append(mutableText.setStyle(newStyle)), false);
        } else {
            source.getServer().getPlayerManager().broadcast(Text.translatable("raw-tpchat.share.server").append(mutableText.setStyle(newStyle)), false);
        }
    }
}