package com.coolguy284.cgcryos_money_mod.common;

import com.google.common.base.Strings;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayDeque;

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

    private Libs() {
        throw new UnsupportedOperationException("Class instantiation not supported");
    }
}
