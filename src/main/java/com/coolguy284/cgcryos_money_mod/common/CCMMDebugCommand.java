package com.coolguy284.cgcryos_money_mod.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;

public class CCMMDebugCommand {
    public CCMMDebugCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("ccmm").then(Commands.literal("debug")
                        .then(Commands.literal("calc_currency").then(Commands.argument("tenthCentsAmount", LongArgumentType.longArg()).executes(this::CCMMDebugCalcCurrency).then(Commands.argument("coinBillCutoff", LongArgumentType.longArg()).executes(this::CCMMDebugCalcCurrency))))
                        .then(Commands.literal("is_client_side").executes(this::CCMMDebugSide))
                )
        );
    }

    public int CCMMDebugCalcCurrency(CommandContext<CommandSource> commandContext) {
        long tenthCentsAmount = LongArgumentType.getLong(commandContext, "tenthCentsAmount");

        long coinBillCutoff;
        try {
            coinBillCutoff = LongArgumentType.getLong(commandContext, "coinBillCutoff");
        } catch (IllegalArgumentException e) {
            coinBillCutoff = 1000;
        }

        ArrayList<CCMMItem.MoneyWithCount> moneyWithCounts = CCMMItem.getItemMoneysArbitrary(tenthCentsAmount, coinBillCutoff);

        StringBuilder returnString = new StringBuilder();

        returnString.append("Money Items for ");
        returnString.append(tenthCentsAmount);
        returnString.append(" mille, ");
        returnString.append(coinBillCutoff);
        returnString.append(" cutoff:\n");

        if (moneyWithCounts.size() == 0) {
            returnString.append("none");
        } else {
            for (int index = 0; index < moneyWithCounts.size(); index++) {
                returnString.append(moneyWithCounts.get(index).count);
                returnString.append(" ");
                returnString.append(moneyWithCounts.get(index).money.REGISTRY_NAME);

                if (index != moneyWithCounts.size() - 1)
                    returnString.append(", ");
            }
        }

        commandContext.getSource().sendSuccess(new StringTextComponent(returnString.toString()), false);

        return 0;
    }

    public int CCMMDebugSide(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        boolean clientSide = commandContext.getSource().getLevel().isClientSide();

        commandContext.getSource().sendSuccess(new StringTextComponent("Is Client Side: " + clientSide), false);

        return 0;
    }
}
