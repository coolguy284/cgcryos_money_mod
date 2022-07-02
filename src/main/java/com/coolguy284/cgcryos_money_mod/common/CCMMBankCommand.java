package com.coolguy284.cgcryos_money_mod.common;

import com.coolguy284.cgcryos_money_mod.CgCryosMoneyMod;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.coolguy284.cgcryos_money_mod.common.Libs.formatAmount;

public class CCMMBankCommand {
    public static final String bankAccountLocation = CgCryosMoneyMod.MODID + ":bank_accounts";
    public static final String bankAccountSettingsLocation = CgCryosMoneyMod.MODID + ":bank_account_settings";

    public static boolean OPRequirement(CommandSource source) {
        return source.hasPermission(4);
    }
    
    public CCMMBankCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("ccmm").then(Commands.literal("bank")
                        .then(Commands.literal("list").executes(this::CCMMBankListAccounts))
                        .then(Commands.literal("get").then(Commands.argument("account", StringArgumentType.string()).executes(this::CCMMBankGetAccount)))
                        .then(Commands.literal("create").then(Commands.argument("account", StringArgumentType.string()).executes(this::CCMMBankCreateAccount)))
                        .then(Commands.literal("remove").then(Commands.argument("account", StringArgumentType.string()).executes(this::CCMMBankRemoveAccount).then(Commands.argument("transfer_account_name", StringArgumentType.string()).executes(this::CCMMBankRemoveAccount))))
                        .then(Commands.literal("transfer")
                                .then(Commands.literal("internal").then(Commands.argument("account_from", StringArgumentType.string()).then(Commands.argument("account_to", StringArgumentType.string()).then(Commands.argument("amount", LongArgumentType.longArg()).executes(this::CCMMBankTransferInternalAccount)))))
                                .then(Commands.literal("external").then(Commands.argument("account_from", StringArgumentType.string()).then(Commands.argument("player_to", GameProfileArgument.gameProfile()).then(Commands.argument("amount", LongArgumentType.longArg()).executes(this::CCMMBankTransferExternalAccount)))))
                        )
                        .then(Commands.literal("default").executes(this::CCMMBankDefaultAccount).then(Commands.argument("new_default_account", StringArgumentType.string()).executes(this::CCMMBankDefaultAccount)))
                        .then(Commands.literal("zop_list").requires(CCMMBankCommand::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes(this::CCMMBankListOtherAccounts)))
                        .then(Commands.literal("zop_get").requires(CCMMBankCommand::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes(this::CCMMBankGetOtherAccount)))
                        .then(Commands.literal("zop_create").requires(CCMMBankCommand::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account", StringArgumentType.string()).executes(this::CCMMBankCreateOtherAccount))))
                        .then(Commands.literal("zop_remove").requires(CCMMBankCommand::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account", StringArgumentType.string()).executes(this::CCMMBankRemoveOtherAccount).then(Commands.argument("transfer_account", StringArgumentType.string())))))
                        .then(Commands.literal("zop_transfer").requires(CCMMBankCommand::OPRequirement)
                                .then(Commands.literal("internal").then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account_from", StringArgumentType.string()).then(Commands.argument("account_to", StringArgumentType.string()).then(Commands.argument("amount", LongArgumentType.longArg()).executes(this::CCMMBankTransferInternalOtherAccount))))))
                                .then(Commands.literal("external").then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account_from", StringArgumentType.string()).then(Commands.argument("player_to", GameProfileArgument.gameProfile()).then(Commands.argument("amount", LongArgumentType.longArg()).executes(this::CCMMBankTransferExternalOtherAccount).then(Commands.argument("account_to", StringArgumentType.string()).executes(this::CCMMBankTransferExternalOtherAccount)))))))
                        )
                        .then(Commands.literal("zop_add").requires(CCMMBankCommand::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account", StringArgumentType.string()).then(Commands.argument("amount", LongArgumentType.longArg()).executes(this::CCMMBankAddToOtherAccount)))))
                        .then(Commands.literal("zop_set").requires(CCMMBankCommand::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account", StringArgumentType.string()).then(Commands.argument("amount", LongArgumentType.longArg()).executes(this::CCMMBankSetOtherAccount)))))
                        .then(Commands.literal("zop_default").requires(CCMMBankCommand::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes(this::CCMMBankDefaultOtherAccount).then(Commands.argument("new_default_account", StringArgumentType.string()).executes(this::CCMMBankDefaultOtherAccount))))
                )
        );
    }

    public int CCMMBankListAccounts(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = commandContext.getSource().getPlayerOrException();

        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        Set<String> keys = bankAccounts.getAllKeys();

        IFormattableTextComponent returnString = new TranslationTextComponent("commands.ccmm.list.success.top");

        if (keys.size() == 0) {
            returnString.append("\n").append(new TranslationTextComponent("commands.ccmm.list.success.none"));
        } else {
            for (String key : keys) {
                returnString.append("\n");
                returnString.append(key).append(": ").append(new TranslationTextComponent("commands.ccmm.list.success.entry", formatAmount(bankAccounts.getLong(key))));
            }
        }

        commandContext.getSource().sendSuccess(returnString, false);

        return 0;
    }

    public int CCMMBankGetAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = commandContext.getSource().getPlayerOrException();

        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        String accountName = StringArgumentType.getString(commandContext, "account");

        if (!bankAccounts.contains(accountName)) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.get.failure", accountName));
            return 1;
        }

        long amount = bankAccounts.getLong(accountName);

        commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.ccmm.get.success", accountName, formatAmount(amount)), false);

        return 0;
    }

    public int CCMMBankCreateAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = commandContext.getSource().getPlayerOrException();

        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        String accountName = StringArgumentType.getString(commandContext, "account");

        if (bankAccounts.contains(accountName)) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.create.failure", accountName));
            return 1;
        }

        bankAccounts.putLong(accountName, 0);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);

        commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.ccmm.create.success", accountName), false);

        return 0;
    }

    public int CCMMBankRemoveAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = commandContext.getSource().getPlayerOrException();

        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        String account = StringArgumentType.getString(commandContext, "account");

        if (!bankAccounts.contains(account)) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.remove.failure_no_account", account));
            return 1;
        }

        long accountValue = bankAccounts.getLong(account);

        if (accountValue != 0) {
            String transferAccount;
            try {
                transferAccount = StringArgumentType.getString(commandContext, "transfer_account_name");
            } catch (IllegalArgumentException e) {
                transferAccount = null;
            }

            if (transferAccount == null) {
                commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.remove.failure_has_money", account));
                return 1;
            }

            if (!bankAccounts.contains(transferAccount)) {
                commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.remove.failure_transfer_no_exist", account, transferAccount));
                return 1;
            }

            bankAccounts.putLong(transferAccount, bankAccounts.getLong(transferAccount) + accountValue);

            bankAccounts.remove(account);

            player.getPersistentData().put(bankAccountLocation, bankAccounts);

            commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.ccmm.remove.success_transfer", account, transferAccount, formatAmount(accountValue)), false);

            return 0;
        }

        bankAccounts.remove(account);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);

        commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.ccmm.remove.success_no_transfer", account), false);

        return 0;
    }

    public int CCMMBankTransferInternalAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = commandContext.getSource().getPlayerOrException();

        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        String accountFrom = StringArgumentType.getString(commandContext, "account_from");
        String accountTo = StringArgumentType.getString(commandContext, "account_to");
        long amount = LongArgumentType.getLong(commandContext, "amount");

        if (!bankAccounts.contains(accountFrom)) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.internal.failure_no_from", accountFrom, accountTo, formatAmount(amount)));
            return 1;
        }

        if (accountFrom.equals(accountTo)) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.internal.failure_same_account", accountFrom, formatAmount(amount)));
            return 1;
        }

        long accountFromValue = bankAccounts.getLong(accountFrom);
        long accountToValue = bankAccounts.getLong(accountTo);
        long accountFromValueAfter = accountFromValue - amount;
        long accountToValueAfter = accountToValue + amount;

        if (accountFromValueAfter < 0 && amount >= 0) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.internal.failure_negative_from_after", accountFrom, accountTo, formatAmount(amount), formatAmount(accountFromValueAfter)));
            return 1;
        }

        if (accountToValueAfter < 0 && amount <= 0) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.internal.failure_negative_to_after", accountFrom, accountTo, formatAmount(amount), formatAmount(accountToValueAfter)));
            return 1;
        }

        boolean accountToExistedBefore = bankAccounts.contains(accountTo);

        bankAccounts.putLong(accountFrom, accountFromValueAfter);
        bankAccounts.putLong(accountTo, accountToValueAfter);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);

        if (accountToExistedBefore) {
            commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.ccmm.transfer.internal.success_account_to_existed", accountFrom, accountTo, formatAmount(amount), formatAmount(accountFromValueAfter)), false);
        } else {
            commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.ccmm.transfer.internal.success_account_to_created", accountFrom, accountTo, formatAmount(amount), formatAmount(accountFromValueAfter)), false);
        }

        return 0;
    }

    public int CCMMBankTransferExternalAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = commandContext.getSource().getPlayerOrException();

        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        String accountFrom = StringArgumentType.getString(commandContext, "account_from");
        long amount = LongArgumentType.getLong(commandContext, "amount");

        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(commandContext, "player_to");

        if (!bankAccounts.contains(accountFrom)) {
            if (players.size() == 1) {
                commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_no_from.with_player", accountFrom, players.iterator().next().getName(), formatAmount(amount)));
            } else {
                commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_no_from.without_player", accountFrom, formatAmount(amount)));
            }
            return 1;
        }

        if (players.size() == 0) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_no_player_to", accountFrom, formatAmount(amount)));
            return 1;
        }
        
        if (players.size() > 1) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_many_player_to", accountFrom, formatAmount(amount)));
            return 1;
        }

        GameProfile playerToProfile = players.iterator().next();
        UUID playerToUUID = playerToProfile.getId();

        if (playerToUUID.equals(player.getGameProfile().getId())) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_same_player", accountFrom, playerToProfile.getName(), formatAmount(amount)));
            return 1;
        }

        if (amount < 0) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_negative_amount", accountFrom, playerToProfile.getName(), formatAmount(amount)));
            return 1;
        }

        ServerPlayerEntity playerTo = Objects.requireNonNull(commandContext.getSource().getServer().getPlayerList().getPlayer(playerToUUID));

        CompoundNBT playerToSettings = playerTo.getPersistentData().getCompound(bankAccountSettingsLocation);

        if (!playerToSettings.contains("default_account")) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_player_to_no_default_or_invalid", accountFrom, playerToProfile.getName(), formatAmount(amount)));
            return 1;
        }

        CompoundNBT playerToBankAccounts = playerTo.getPersistentData().getCompound(bankAccountLocation);

        String accountTo = playerToSettings.getString("default_account");

        if (!playerToBankAccounts.contains(accountTo)) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_player_to_no_default_or_invalid", accountFrom, playerToProfile.getName(), formatAmount(amount)));
            return 1;
        }

        long accountFromValue = bankAccounts.getLong(accountFrom);
        long accountToValue = playerToBankAccounts.getLong(accountTo);
        long accountFromValueAfter = accountFromValue - amount;
        long accountToValueAfter = accountToValue + amount;

        if (accountFromValueAfter < 0) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_negative_from_after", accountFrom, playerToProfile.getName(), formatAmount(amount), formatAmount(accountFromValueAfter)));
            return 1;
        }

        bankAccounts.putLong(accountFrom, accountFromValueAfter);
        playerToBankAccounts.putLong(accountTo, accountToValueAfter);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);
        playerTo.getPersistentData().put(bankAccountLocation, playerToBankAccounts);

        commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.ccmm.transfer.external.success", accountFrom, playerToProfile.getName(), formatAmount(amount), formatAmount(accountFromValueAfter)), false);

        return 0;
    }

    public int CCMMBankDefaultAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = commandContext.getSource().getPlayerOrException();

        String newDefault;
        try {
            newDefault = StringArgumentType.getString(commandContext, "new_default_account");
        } catch (IllegalArgumentException e) {
            newDefault = null;
        }

        CompoundNBT bankSettings = player.getPersistentData().getCompound(bankAccountSettingsLocation);

        if (newDefault == null) {
            if (bankSettings.contains("default_account")) {
                commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.ccmm.default.success_get.has_default", bankSettings.getString("default_account")), false);
            } else {
                commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.ccmm.default.success_get.no_default"), false);
            }

            return 0;
        }

        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        if (!bankAccounts.contains(newDefault)) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.default.failure_no_account", newDefault));

            return 1;
        }

        bankSettings.putString("default_account", newDefault);

        player.getPersistentData().put(bankAccountSettingsLocation, bankSettings);

        commandContext.getSource().sendSuccess(new TranslationTextComponent("commands.ccmm.default.success_set", newDefault), false);

        return 0;
    }

    public int CCMMBankListOtherAccounts(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(commandContext, "player");

        PlayerList playerList = commandContext.getSource().getServer().getPlayerList();

        for (GameProfile playerProfile : players) {
            ServerPlayerEntity player = Objects.requireNonNull(playerList.getPlayer(playerProfile.getId()));

            CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

            Set<String> keys = bankAccounts.getAllKeys();

            StringBuilder returnString = new StringBuilder();

            returnString.append(playerProfile.getName()).append("'s Bank Accounts:");

            if (keys.size() == 0) {
                returnString.append("\nnone");
            } else {
                for (String key : keys) {
                    returnString.append("\n");
                    returnString.append(key).append(": $").append(formatAmount(bankAccounts.getLong(key)));
                }
            }

            commandContext.getSource().sendSuccess(new StringTextComponent(returnString.toString()), false);
        }

        return 0;
    }

    public int CCMMBankGetOtherAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(commandContext, "player");

        PlayerList playerList = commandContext.getSource().getServer().getPlayerList();

        for (GameProfile playerProfile : players) {
            ServerPlayerEntity player = Objects.requireNonNull(playerList.getPlayer(playerProfile.getId()));

            CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

            String account = StringArgumentType.getString(commandContext, "account");
            long amount = bankAccounts.getLong(account);

            commandContext.getSource().sendSuccess(new StringTextComponent("Bank account \"" + account + "\" is " + amount), false);
        }

        return 0;
    }

    public int CCMMBankCreateOtherAccount(CommandContext<CommandSource> commandContext) {
        return 0;
    }

    public int CCMMBankRemoveOtherAccount(CommandContext<CommandSource> commandContext) {
        return 0;
    }

    public int CCMMBankTransferInternalOtherAccount(CommandContext<CommandSource> commandContext) {
        return 0;
    }

    public int CCMMBankTransferExternalOtherAccount(CommandContext<CommandSource> commandContext) {
        return 0;
    }

    public int CCMMBankAddToOtherAccount(CommandContext<CommandSource> commandContext) {
        return 0;
    }

    public int CCMMBankSetOtherAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgument.getGameProfiles(commandContext, "player");

        PlayerList playerList = commandContext.getSource().getServer().getPlayerList();

        for (GameProfile playerProfile : players) {
            ServerPlayerEntity player = Objects.requireNonNull(playerList.getPlayer(playerProfile.getId()));

            CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

            String account = StringArgumentType.getString(commandContext, "account");
            long amount = LongArgumentType.getLong(commandContext, "amount");

            bankAccounts.putLong(account, amount);

            player.getPersistentData().put(bankAccountLocation, bankAccounts);

            commandContext.getSource().sendSuccess(new StringTextComponent(playerProfile.getName() + "'s bank account \"" + account + "\" set to " + amount), false);
        }

        return 0;
    }

    public int CCMMBankDefaultOtherAccount(CommandContext<CommandSource> commandContext) {
        return 0;
    }
}
