package com.coolguy284.cgcryos_money_mod.common;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Objects;

public class Libs {
    public static IFormattableTextComponent formatAmount(long tenthCentAmount) {
        if (tenthCentAmount < 0)
            return new TranslationTextComponent("commands.ccmm.format_amount.negative_symbol")
                    .append(formatAmount(-tenthCentAmount));

        String beforeDecimal = String.valueOf(tenthCentAmount / 1000);

        ArrayDeque<String> thousandsSplit = new ArrayDeque<>();

        int i;
        for (i = beforeDecimal.length(); i > 3; i -= 3) {
            thousandsSplit.addFirst(beforeDecimal.substring(Math.max(i - 3, 0), i));
        }

        String firstSection = beforeDecimal.substring(Math.max(i - 3, 0), i);

        IFormattableTextComponent formattedResult = new StringTextComponent(firstSection);

        for (String section : thousandsSplit) {
            formattedResult
                    .append(new TranslationTextComponent("commands.ccmm.format_amount.10^3_separator"))
                    .append(section);
        }

        formattedResult
                .append(new TranslationTextComponent("commands.ccmm.format_amount.decimal_separator"))
                .append(new StringTextComponent(Strings.padStart(String.valueOf(tenthCentAmount % 1000), 3, '0')));

        return formattedResult;
    }



    public enum CCMMCommandResultStatus {
        Success,
        Failure
    }

    public static class CCMMCommandResult {

        public CCMMCommandResultStatus status;
        public IFormattableTextComponent text;

        public CCMMCommandResult(CCMMCommandResultStatus status, IFormattableTextComponent text) {
            this.status = status;
            this.text = text;
        }
    }

    public static ServerPlayerEntity CCMMGetPlayer(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        return commandContext.getSource().getPlayerOrException();
    }

    public static ServerPlayerEntity CCMMGetPlayerArg(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(commandContext, "player");

        if (players.size() == 0) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.zop.failure_no_player"));
            return null;
        }

        if (players.size() > 1) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.zop.failure_many_player"));
            return null;
        }

        GameProfile playerProfile = players.iterator().next();

        return Objects.requireNonNull(commandContext.getSource().getServer().getPlayerList().getPlayer(playerProfile.getId()));
    }

    public static int CCMMSendCommandResult(CCMMCommandResult result, CommandContext<CommandSource> commandContext) {
        switch (result.status) {
            case Success:
                commandContext.getSource().sendSuccess(result.text, false);
                return 0;

            case Failure:
                commandContext.getSource().sendFailure(result.text);
                return 1;

            default:
                return 1;
        }
    }

    public static TranslationTextComponent CCMMTranslationTextOptionalOp(String textName, boolean opMode, ServerPlayerEntity player, Object ...params) {
        if (opMode) {
            params = ArrayUtils.insert(0, params, player.getName());
            return new TranslationTextComponent("commands.ccmm.zop." + textName, params);
        } else {
            return new TranslationTextComponent("commands.ccmm." + textName, params);
        }
    }

    public static boolean OPRequirement(CommandSource source) {
        return source.hasPermission(4);
    }

    private Libs() {
        throw new UnsupportedOperationException("Class instantiation not supported");
    }
}
