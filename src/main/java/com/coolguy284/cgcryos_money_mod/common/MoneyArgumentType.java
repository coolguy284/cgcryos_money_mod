package com.coolguy284.cgcryos_money_mod.common;

import com.google.common.base.Strings;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// class doesn't work so this is just a creator class
public class MoneyArgumentType {
    public static StringArgumentType moneyArg() {
        return StringArgumentType.string();
    }

    public static long getMoney(final CommandContext<?> context, final String name) throws CommandSyntaxException {
        String resultString = context.getArgument(name, String.class);
        return MoneyArgumentType.parse(resultString);
    }

    public static Long parse(String resultString) throws CommandSyntaxException {
        Pattern r = Pattern.compile("^\\$?(-)?(?:(\\d+)\\.(\\d+)|(\\d+)|\\.(\\d+))$");
        Matcher m = r.matcher(resultString);

        if (!m.find())
            throw new CommandSyntaxException(new SimpleCommandExceptionType(new TranslationTextComponent("commands.ccmm.money_arg.invalid_money_type")), new TranslationTextComponent("commands.ccmm.money_arg.invalid_money"));

        boolean negative = Objects.equals(m.group(1), "-");
        int mode = m.group(2) != null ? 0 : m.group(4) != null ? 1 : 2;
        String whole = negative ? "-" : "";
        String decimal = negative ? "-" : "";
        switch (mode) {
            case 0: whole += m.group(2); decimal += m.group(3); break;
            case 1: whole += m.group(4); decimal += "0"; break;
            case 2: whole += "0"; decimal += m.group(5); break;
        }
        System.out.println(negative);
        System.out.println(whole);
        System.out.println(decimal);
        long result = Long.parseLong(whole) * 1000;

        if (decimal != null) {
            result += Long.parseLong(Strings.padEnd(decimal, negative ? 4 : 3, '0').substring(0, negative ? 4 : 3));
        }

        return result;
    }

    private MoneyArgumentType() {
        throw new UnsupportedOperationException("Class instantiation not supported");
    }
}
