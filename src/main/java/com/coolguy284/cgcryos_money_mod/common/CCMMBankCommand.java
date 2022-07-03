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
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;

import static com.coolguy284.cgcryos_money_mod.common.Libs.formatAmount;
import static com.coolguy284.cgcryos_money_mod.common.Libs.CCMMCommandResult;
import static com.coolguy284.cgcryos_money_mod.common.Libs.CCMMCommandResultStatus;
import static com.coolguy284.cgcryos_money_mod.common.Libs.CCMMTranslationTextOptionalOp;
import static com.coolguy284.cgcryos_money_mod.common.Libs.CCMMGetPlayer;
import static com.coolguy284.cgcryos_money_mod.common.Libs.CCMMGetPlayerArg;
import static com.coolguy284.cgcryos_money_mod.common.Libs.CCMMSendCommandResult;

public class CCMMBankCommand {
    public static final String bankAccountLocation = CgCryosMoneyMod.MODID + ":bank_accounts";
    public static final String bankAccountSettingsLocation = CgCryosMoneyMod.MODID + ":bank_account_settings";
    
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
                        .then(Commands.literal("zop")
                                .then(Commands.literal("list").requires(Libs::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes(this::CCMMBankListOtherAccounts)))
                                .then(Commands.literal("get").requires(Libs::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account", StringArgumentType.string()).executes(this::CCMMBankGetOtherAccount))))
                                .then(Commands.literal("create").requires(Libs::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account", StringArgumentType.string()).executes(this::CCMMBankCreateOtherAccount))))
                                .then(Commands.literal("remove").requires(Libs::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account", StringArgumentType.string()).executes(this::CCMMBankRemoveOtherAccount).then(Commands.argument("transfer_account", StringArgumentType.string()).executes(this::CCMMBankRemoveOtherAccount)))))
                                .then(Commands.literal("transfer").requires(Libs::OPRequirement)
                                        .then(Commands.literal("internal").then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account_from", StringArgumentType.string()).then(Commands.argument("account_to", StringArgumentType.string()).then(Commands.argument("amount", LongArgumentType.longArg()).executes(this::CCMMBankTransferInternalOtherAccount))))))
                                        .then(Commands.literal("external").then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account_from", StringArgumentType.string()).then(Commands.argument("player_to", GameProfileArgument.gameProfile()).then(Commands.argument("amount", LongArgumentType.longArg()).executes(this::CCMMBankTransferExternalOtherAccount).then(Commands.argument("account_to", StringArgumentType.string()).executes(this::CCMMBankTransferExternalOtherAccount)))))))
                                )
                                .then(Commands.literal("add").requires(Libs::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account", StringArgumentType.string()).then(Commands.argument("amount", LongArgumentType.longArg()).executes(this::CCMMBankAddToOtherAccount)))))
                                .then(Commands.literal("set").requires(Libs::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).then(Commands.argument("account", StringArgumentType.string()).then(Commands.argument("amount", LongArgumentType.longArg()).executes(this::CCMMBankSetOtherAccount)))))
                                .then(Commands.literal("default").requires(Libs::OPRequirement).then(Commands.argument("player", GameProfileArgument.gameProfile()).executes(this::CCMMBankDefaultOtherAccount).then(Commands.argument("new_default_account", StringArgumentType.string()).executes(this::CCMMBankDefaultOtherAccount))))
                        )
                )
        );
    }

    public CCMMCommandResult CCMMBankListAccountsInternal(ServerPlayerEntity player, boolean opMode) {
        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        Set<String> keys = bankAccounts.getAllKeys();

        IFormattableTextComponent returnString = CCMMTranslationTextOptionalOp("list.success.top", opMode, player);

        if (keys.size() == 0) {
            returnString.append("\n").append(new TranslationTextComponent("commands.ccmm.list.success.none"));
        } else {
            for (String key : keys) {
                returnString.append("\n");
                returnString.append(new TranslationTextComponent("commands.ccmm.list.success.entry", key, formatAmount(bankAccounts.getLong(key))));
            }
        }

        return new CCMMCommandResult(CCMMCommandResultStatus.Success, returnString);
    }

    public CCMMCommandResult CCMMBankGetAccountInternal(ServerPlayerEntity player, String accountName, boolean opMode) {
        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        if (!bankAccounts.contains(accountName)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, CCMMTranslationTextOptionalOp("get.failure", opMode, player, accountName));
        }

        long amount = bankAccounts.getLong(accountName);

        return new CCMMCommandResult(CCMMCommandResultStatus.Success, CCMMTranslationTextOptionalOp("get.success", opMode, player, accountName, formatAmount(amount)));
    }

    public CCMMCommandResult CCMMBankCreateAccountInternal(ServerPlayerEntity player, String accountName, boolean opMode) {
        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        if (bankAccounts.contains(accountName)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, CCMMTranslationTextOptionalOp("create.failure", opMode, player, accountName));
        }

        bankAccounts.putLong(accountName, 0);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);

        if (opMode)
            player.sendMessage(new TranslationTextComponent("commands.ccmm.create.success_admin", accountName), player.getUUID());

        return new CCMMCommandResult(CCMMCommandResultStatus.Success, CCMMTranslationTextOptionalOp("create.success", opMode, player, accountName));
    }

    public CCMMCommandResult CCMMBankRemoveAccountInternal(ServerPlayerEntity player, String accountName, String transferAccount, boolean opMode) {
        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        if (!bankAccounts.contains(accountName)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, CCMMTranslationTextOptionalOp("remove.failure_no_account", opMode, player, accountName));
        }

        long accountValue = bankAccounts.getLong(accountName);

        if (accountValue != 0) {
            boolean performTransfer = true;
            System.out.println(transferAccount);
            if (transferAccount == null) {
                if (!opMode)
                    return new CCMMCommandResult(CCMMCommandResultStatus.Failure, CCMMTranslationTextOptionalOp("remove.failure_has_money", false, player, accountName));
                else
                    performTransfer = false;
            }

            if (performTransfer) {
                if (!bankAccounts.contains(transferAccount)) {
                    return new CCMMCommandResult(CCMMCommandResultStatus.Failure, CCMMTranslationTextOptionalOp("remove.failure_transfer_no_exist", opMode, player, accountName, transferAccount));
                }

                bankAccounts.putLong(transferAccount, bankAccounts.getLong(transferAccount) + accountValue);

                bankAccounts.remove(accountName);

                player.getPersistentData().put(bankAccountLocation, bankAccounts);

                if (opMode)
                    player.sendMessage(new TranslationTextComponent("commands.ccmm.remove.success_transfer_admin", accountName, transferAccount, formatAmount(accountValue)), player.getUUID());

                return new CCMMCommandResult(CCMMCommandResultStatus.Success, CCMMTranslationTextOptionalOp("remove.success_transfer", opMode, player, accountName, transferAccount, formatAmount(accountValue)));
            }
        }

        bankAccounts.remove(accountName);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);

        if (accountValue != 0) {
            player.sendMessage(new TranslationTextComponent("commands.ccmm.remove.success_no_transfer_with_value_admin", accountName, formatAmount(accountValue)), player.getUUID());

            return new CCMMCommandResult(CCMMCommandResultStatus.Success, CCMMTranslationTextOptionalOp("remove.success_no_transfer_with_value", true, player, accountName, formatAmount(accountValue)));
        } else {
            if (opMode)
                player.sendMessage(new TranslationTextComponent("commands.ccmm.remove.success_no_transfer_admin", accountName), player.getUUID());

            return new CCMMCommandResult(CCMMCommandResultStatus.Success, CCMMTranslationTextOptionalOp("remove.success_no_transfer", opMode, player, accountName));
        }
    }

    public CCMMCommandResult CCMMBankTransferInternalAccountInternal(ServerPlayerEntity player, String accountFrom, String accountTo, long amount) {
        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        if (!bankAccounts.contains(accountFrom)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.internal.failure_no_from", accountFrom, accountTo, formatAmount(amount)));
        }

        if (accountFrom.equals(accountTo)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.internal.failure_same_account", accountFrom, formatAmount(amount)));
        }

        long accountFromValue = bankAccounts.getLong(accountFrom);
        long accountToValue = bankAccounts.getLong(accountTo);
        long accountFromValueAfter = accountFromValue - amount;
        long accountToValueAfter = accountToValue + amount;

        if (accountFromValueAfter < 0 && amount >= 0) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.internal.failure_negative_from_after", accountFrom, accountTo, formatAmount(amount), formatAmount(accountFromValueAfter)));
        }

        if (accountToValueAfter < 0 && amount <= 0) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.internal.failure_negative_to_after", accountFrom, accountTo, formatAmount(amount), formatAmount(accountToValueAfter)));
        }

        boolean accountToExistedBefore = bankAccounts.contains(accountTo);

        bankAccounts.putLong(accountFrom, accountFromValueAfter);
        bankAccounts.putLong(accountTo, accountToValueAfter);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);

        if (accountToExistedBefore)
            return new CCMMCommandResult(CCMMCommandResultStatus.Success, new TranslationTextComponent("commands.ccmm.transfer.internal.success_account_to_existed", accountFrom, accountTo, formatAmount(amount), formatAmount(accountFromValueAfter)));
        else
            return new CCMMCommandResult(CCMMCommandResultStatus.Success, new TranslationTextComponent("commands.ccmm.transfer.internal.success_account_to_created", accountFrom, accountTo, formatAmount(amount), formatAmount(accountFromValueAfter)));
    }

    public CCMMCommandResult CCMMBankTransferExternalAccountInternal(ServerPlayerEntity player, String accountFrom, long amount, ServerPlayerEntity playerTo) {
        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        if (!bankAccounts.contains(accountFrom)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.external.failure_no_from", accountFrom, playerTo.getName(), formatAmount(amount)));
        }

        if (playerTo.getUUID().equals(player.getUUID())) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.external.failure_same_player", accountFrom, playerTo.getName(), formatAmount(amount)));
        }

        if (amount < 0) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.external.failure_negative_amount", accountFrom, playerTo.getName(), formatAmount(amount)));
        }

        CompoundNBT playerToSettings = playerTo.getPersistentData().getCompound(bankAccountSettingsLocation);

        if (!playerToSettings.contains("default_account")) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.external.failure_player_to_no_default_or_invalid", accountFrom, playerTo.getName(), formatAmount(amount)));
        }

        CompoundNBT playerToBankAccounts = playerTo.getPersistentData().getCompound(bankAccountLocation);

        String accountTo = playerToSettings.getString("default_account");

        if (!playerToBankAccounts.contains(accountTo)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.external.failure_player_to_no_default_or_invalid", accountFrom, playerTo.getName(), formatAmount(amount)));
        }

        long accountFromValue = bankAccounts.getLong(accountFrom);
        long accountToValue = playerToBankAccounts.getLong(accountTo);
        long accountFromValueAfter = accountFromValue - amount;
        long accountToValueAfter = accountToValue + amount;

        if (accountFromValueAfter < 0) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.external.failure_negative_from_after", accountFrom, playerTo.getName(), formatAmount(amount), formatAmount(accountFromValueAfter)));
        }

        bankAccounts.putLong(accountFrom, accountFromValueAfter);
        playerToBankAccounts.putLong(accountTo, accountToValueAfter);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);
        playerTo.getPersistentData().put(bankAccountLocation, playerToBankAccounts);

        playerTo.sendMessage(new TranslationTextComponent("commands.ccmm.transfer.external.success_to", formatAmount(amount), player.getName(), accountTo, formatAmount(accountToValueAfter)), player.getUUID());

        return new CCMMCommandResult(CCMMCommandResultStatus.Success, new TranslationTextComponent("commands.ccmm.transfer.external.success", accountFrom, playerTo.getName(), formatAmount(amount), formatAmount(accountFromValueAfter)));
    }

    public CCMMCommandResult CCMMBankDefaultAccountInternal(ServerPlayerEntity player, String newDefault, boolean opMode) {
        CompoundNBT bankSettings = player.getPersistentData().getCompound(bankAccountSettingsLocation);

        if (newDefault == null) {
            if (bankSettings.contains("default_account")) {
                return new CCMMCommandResult(CCMMCommandResultStatus.Success, CCMMTranslationTextOptionalOp("default.success_get.has_default", opMode, player, bankSettings.getString("default_account")));
            } else {
                return new CCMMCommandResult(CCMMCommandResultStatus.Success, CCMMTranslationTextOptionalOp("default.success_get.no_default", opMode, player));
            }
        }

        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        if (!bankAccounts.contains(newDefault)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, CCMMTranslationTextOptionalOp("default.failure_no_account", opMode, player, newDefault));
        }

        bankSettings.putString("default_account", newDefault);

        player.getPersistentData().put(bankAccountSettingsLocation, bankSettings);

        if (opMode)
            player.sendMessage(new TranslationTextComponent("commands.ccmm.default.success_set_admin", newDefault), player.getUUID());

        return new CCMMCommandResult(CCMMCommandResultStatus.Success, CCMMTranslationTextOptionalOp("default.success_set", opMode, player, newDefault));
    }

    // this command is op only
    public CCMMCommandResult CCMMBankTransferInternalOtherAccountInternal(ServerPlayerEntity player, String accountFrom, String accountTo, long amount) {
        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        if (!bankAccounts.contains(accountFrom)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.internal.failure_no_from", accountFrom, accountTo, formatAmount(amount)));
        }

        if (accountFrom.equals(accountTo)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.transfer.internal.failure_same_account", accountFrom, formatAmount(amount)));
        }

        long accountFromValue = bankAccounts.getLong(accountFrom);
        long accountToValue = bankAccounts.getLong(accountTo);
        long accountFromValueAfter = accountFromValue - amount;
        long accountToValueAfter = accountToValue + amount;

        boolean accountToExistedBefore = bankAccounts.contains(accountTo);

        bankAccounts.putLong(accountFrom, accountFromValueAfter);
        bankAccounts.putLong(accountTo, accountToValueAfter);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);

        if (accountToExistedBefore) {
            player.sendMessage(new TranslationTextComponent("commands.ccmm.transfer.internal.success_account_to_existed_admin", accountFrom, accountTo, formatAmount(amount), formatAmount(accountFromValueAfter)), player.getUUID());

            return new CCMMCommandResult(CCMMCommandResultStatus.Success, new TranslationTextComponent("commands.ccmm.transfer.internal.success_account_to_existed", accountFrom, accountTo, formatAmount(amount), formatAmount(accountFromValueAfter)));
        } else {
            player.sendMessage(new TranslationTextComponent("commands.ccmm.transfer.internal.success_account_to_created_admin", accountFrom, accountTo, formatAmount(amount), formatAmount(accountFromValueAfter)), player.getUUID());

            return new CCMMCommandResult(CCMMCommandResultStatus.Success, new TranslationTextComponent("commands.ccmm.transfer.internal.success_account_to_created", accountFrom, accountTo, formatAmount(amount), formatAmount(accountFromValueAfter)));
        }
    }

    // this command is op only
    public CCMMCommandResult CCMMBankTransferExternalOtherAccountInternal(ServerPlayerEntity player, String accountFrom, long amount, ServerPlayerEntity playerTo, String accountToOverride) {
        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        if (!bankAccounts.contains(accountFrom)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.zop.transfer.external.failure_no_from", player.getName(), accountFrom, playerTo.getName(), formatAmount(amount)));
        }

        if (playerTo.getUUID().equals(player.getUUID())) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.zop.transfer.external.failure_same_player", player.getName(), accountFrom, playerTo.getName(), formatAmount(amount)));
        }

        CompoundNBT playerToSettings = playerTo.getPersistentData().getCompound(bankAccountSettingsLocation);

        if (!playerToSettings.contains("default_account")) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent("commands.ccmm.zop.transfer.external.failure_player_to_no_default", player.getName(), accountFrom, playerTo.getName(), formatAmount(amount)));
        }

        CompoundNBT playerToBankAccounts = playerTo.getPersistentData().getCompound(bankAccountLocation);

        String accountTo = accountToOverride != null ? accountToOverride : playerToSettings.getString("default_account");

        if (!playerToBankAccounts.contains(accountTo)) {
            return new CCMMCommandResult(CCMMCommandResultStatus.Failure, new TranslationTextComponent(accountToOverride != null ? "commands.ccmm.zop.transfer.external.failure_player_to_invalid_provided_account" : "commands.ccmm.zop.transfer.external.failure_player_to_invalid_default", player.getName(), accountFrom, playerTo.getName(), formatAmount(amount), accountTo));
        }

        long accountFromValue = bankAccounts.getLong(accountFrom);
        long accountToValue = playerToBankAccounts.getLong(accountTo);
        long accountFromValueAfter = accountFromValue - amount;
        long accountToValueAfter = accountToValue + amount;

        bankAccounts.putLong(accountFrom, accountFromValueAfter);
        playerToBankAccounts.putLong(accountTo, accountToValueAfter);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);
        playerTo.getPersistentData().put(bankAccountLocation, playerToBankAccounts);

        player.sendMessage(new TranslationTextComponent("commands.ccmm.transfer.external.success_admin", accountFrom, playerTo.getName(), formatAmount(amount), formatAmount(accountFromValueAfter)), player.getUUID());
        playerTo.sendMessage(new TranslationTextComponent("commands.ccmm.transfer.external.success_to", formatAmount(amount), player.getName(), accountTo, formatAmount(accountToValueAfter)), player.getUUID());

        return new CCMMCommandResult(CCMMCommandResultStatus.Success, new TranslationTextComponent("commands.ccmm.zop.transfer.external.success", player.getName(), accountFrom, formatAmount(amount), playerTo.getName(), formatAmount(accountFromValueAfter), accountTo));
    }

    // this command is op only
    public CCMMCommandResult CCMMBankAddToOtherAccountInternal(ServerPlayerEntity player, String accountName, long amount) {
        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        boolean accountExistedBefore = bankAccounts.contains(accountName);

        long newAmount = accountExistedBefore ? bankAccounts.getLong(accountName) + amount : amount;

        bankAccounts.putLong(accountName, newAmount);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);

        if (accountExistedBefore) {
            player.sendMessage(new TranslationTextComponent("commands.ccmm.zop.add.success_existed_admin", accountName, formatAmount(amount), formatAmount(newAmount)), player.getUUID());

            return new CCMMCommandResult(CCMMCommandResultStatus.Success, new TranslationTextComponent("commands.ccmm.zop.add.success_existed", player.getName(), accountName, formatAmount(amount), formatAmount(newAmount)));
        } else {
            player.sendMessage(new TranslationTextComponent("commands.ccmm.zop.add.success_created_admin", accountName, formatAmount(amount)), player.getUUID());

            return new CCMMCommandResult(CCMMCommandResultStatus.Success, new TranslationTextComponent("commands.ccmm.zop.add.success_created", player.getName(), accountName, formatAmount(amount)));
        }
    }

    // this command is op only
    public CCMMCommandResult CCMMBankSetAccountInternal(ServerPlayerEntity player, String accountName, long amount) {
        CompoundNBT bankAccounts = player.getPersistentData().getCompound(bankAccountLocation);

        boolean accountExistedBefore = bankAccounts.contains(accountName);

        bankAccounts.putLong(accountName, amount);

        player.getPersistentData().put(bankAccountLocation, bankAccounts);

        if (accountExistedBefore) {
            player.sendMessage(new TranslationTextComponent("commands.ccmm.zop.set.success_existed_admin", accountName, formatAmount(amount)), player.getUUID());

            return new CCMMCommandResult(CCMMCommandResultStatus.Success, new TranslationTextComponent("commands.ccmm.zop.set.success_existed", player.getName(), accountName, formatAmount(amount)));
        } else {
            player.sendMessage(new TranslationTextComponent("commands.ccmm.zop.set.success_created_admin", accountName, formatAmount(amount)), player.getUUID());

            return new CCMMCommandResult(CCMMCommandResultStatus.Success, new TranslationTextComponent("commands.ccmm.zop.set.success_created", player.getName(), accountName, formatAmount(amount)));
        }
    }

    public int CCMMBankListAccounts(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayer(commandContext);

        CCMMCommandResult result = CCMMBankListAccountsInternal(player, false);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankGetAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayer(commandContext);

        String accountName = StringArgumentType.getString(commandContext, "account");

        CCMMCommandResult result = CCMMBankGetAccountInternal(player, accountName, false);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankCreateAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayer(commandContext);

        String accountName = StringArgumentType.getString(commandContext, "account");

        CCMMCommandResult result = CCMMBankCreateAccountInternal(player, accountName, false);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankRemoveAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayer(commandContext);

        String accountName = StringArgumentType.getString(commandContext, "account");

        String transferAccount;
        try {
            transferAccount = StringArgumentType.getString(commandContext, "transfer_account_name");
        } catch (IllegalArgumentException e) {
            transferAccount = null;
        }

        CCMMCommandResult result = CCMMBankRemoveAccountInternal(player, accountName, transferAccount, false);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankTransferInternalAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayer(commandContext);

        String accountFrom = StringArgumentType.getString(commandContext, "account_from");
        String accountTo = StringArgumentType.getString(commandContext, "account_to");
        long amount = LongArgumentType.getLong(commandContext, "amount");

        CCMMCommandResult result = CCMMBankTransferInternalAccountInternal(player, accountFrom, accountTo, amount);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankTransferExternalAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayer(commandContext);

        String accountFrom = StringArgumentType.getString(commandContext, "account_from");
        long amount = LongArgumentType.getLong(commandContext, "amount");

        Collection<GameProfile> playerTos = GameProfileArgument.getGameProfiles(commandContext, "player_to");

        if (playerTos.size() == 0) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_no_player_to", accountFrom, formatAmount(amount)));
            return 1;
        }

        if (playerTos.size() > 1) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_many_player_to", accountFrom, formatAmount(amount)));
            return 1;
        }

        GameProfile playerToProfile = playerTos.iterator().next();

        ServerPlayerEntity playerTo = commandContext.getSource().getServer().getPlayerList().getPlayer(playerToProfile.getId());

        if (playerTo == null) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.transfer.external.failure_invalid_player_to", accountFrom, formatAmount(amount)));
            return 1;
        }

        CCMMCommandResult result = CCMMBankTransferExternalAccountInternal(player, accountFrom, amount, playerTo);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankDefaultAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayer(commandContext);

        String newDefault;
        try {
            newDefault = StringArgumentType.getString(commandContext, "new_default_account");
        } catch (IllegalArgumentException e) {
            newDefault = null;
        }

        CCMMCommandResult result = CCMMBankDefaultAccountInternal(player, newDefault, false);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankListOtherAccounts(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayerArg(commandContext);
        if (player == null) return 1;

        CCMMCommandResult result = CCMMBankListAccountsInternal(player, true);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankGetOtherAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayerArg(commandContext);
        if (player == null) return 1;

        String accountName = StringArgumentType.getString(commandContext, "account");

        CCMMCommandResult result = CCMMBankGetAccountInternal(player, accountName, true);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankCreateOtherAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayerArg(commandContext);
        if (player == null) return 1;

        String accountName = StringArgumentType.getString(commandContext, "account");

        CCMMCommandResult result = CCMMBankCreateAccountInternal(player, accountName, true);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankRemoveOtherAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayerArg(commandContext);
        if (player == null) return 1;

        String accountName = StringArgumentType.getString(commandContext, "account");

        String transferAccount;
        try {
            transferAccount = StringArgumentType.getString(commandContext, "transfer_account");
        } catch (IllegalArgumentException e) {
            transferAccount = null;
        }

        CCMMCommandResult result = CCMMBankRemoveAccountInternal(player, accountName, transferAccount, true);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankTransferInternalOtherAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayerArg(commandContext);
        if (player == null) return 1;

        String accountFrom = StringArgumentType.getString(commandContext, "account_from");
        String accountTo = StringArgumentType.getString(commandContext, "account_to");
        long amount = LongArgumentType.getLong(commandContext, "amount");

        CCMMCommandResult result = CCMMBankTransferInternalOtherAccountInternal(player, accountFrom, accountTo, amount);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankTransferExternalOtherAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayerArg(commandContext);
        if (player == null) return 1;

        String accountFrom = StringArgumentType.getString(commandContext, "account_from");
        long amount = LongArgumentType.getLong(commandContext, "amount");

        Collection<GameProfile> playerTos = GameProfileArgument.getGameProfiles(commandContext, "player_to");

        if (playerTos.size() == 0) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.zop.transfer.external.failure_no_player_to", player.getName(), accountFrom, formatAmount(amount)));
            return 1;
        }

        if (playerTos.size() > 1) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.zop.transfer.external.failure_many_player_to", player.getName(), accountFrom, formatAmount(amount)));
            return 1;
        }

        GameProfile playerToProfile = playerTos.iterator().next();

        ServerPlayerEntity playerTo = commandContext.getSource().getServer().getPlayerList().getPlayer(playerToProfile.getId());

        if (playerTo == null) {
            commandContext.getSource().sendFailure(new TranslationTextComponent("commands.ccmm.zop.transfer.external.failure_invalid_player_to", player.getName(), accountFrom, formatAmount(amount)));
            return 1;
        }

        String accountTo;
        try {
            accountTo = StringArgumentType.getString(commandContext, "account_to");
        } catch (IllegalArgumentException e) {
            accountTo = null;
        }

        CCMMCommandResult result = CCMMBankTransferExternalOtherAccountInternal(player, accountFrom, amount, playerTo, accountTo);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankAddToOtherAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayerArg(commandContext);
        if (player == null) return 1;

        String accountName = StringArgumentType.getString(commandContext, "account");
        long amount = LongArgumentType.getLong(commandContext, "amount");

        CCMMCommandResult result = CCMMBankAddToOtherAccountInternal(player, accountName, amount);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankSetOtherAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayerArg(commandContext);
        if (player == null) return 1;

        String accountName = StringArgumentType.getString(commandContext, "account");
        long amount = LongArgumentType.getLong(commandContext, "amount");

        CCMMCommandResult result = CCMMBankSetAccountInternal(player, accountName, amount);

        return CCMMSendCommandResult(result, commandContext);
    }

    public int CCMMBankDefaultOtherAccount(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity player = CCMMGetPlayerArg(commandContext);
        if (player == null) return 1;

        String newDefault;
        try {
            newDefault = StringArgumentType.getString(commandContext, "new_default_account");
        } catch (IllegalArgumentException e) {
            newDefault = null;
        }

        CCMMCommandResult result = CCMMBankDefaultAccountInternal(player, newDefault, true);

        return CCMMSendCommandResult(result, commandContext);
    }
}
