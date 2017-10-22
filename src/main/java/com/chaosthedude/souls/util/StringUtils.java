package com.chaosthedude.souls.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public class StringUtils {

	public static String getTimeSince(Date dateTime) {
		StringBuilder buffer = new StringBuilder();
		Date current = Calendar.getInstance().getTime();
		long diffInSeconds = (current.getTime() - dateTime.getTime()) / 1000;

		long secs = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
		long mins = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
		long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
		long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
		long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
		long years = (diffInSeconds = (diffInSeconds / 12));

		if (years > 0) {
			if (years == 1) {
				buffer.append(getLocalizedComponent(Strings.YEARS, years));
			}
			if (months > 0) {
				buffer.append(getLocalizedSecondaryComponent(Strings.MONTHS, months));
			}
		} else if (months > 0) {
			buffer.append(getLocalizedComponent(Strings.MONTHS, months));
			if (days > 0) {
				buffer.append(getLocalizedSecondaryComponent(Strings.DAYS, days));
			}
		} else if (days > 0) {
			buffer.append(getLocalizedComponent(Strings.DAYS, days));
			if (hrs > 0) {
				buffer.append(getLocalizedSecondaryComponent(Strings.HOURS, hrs));
			}
		} else if (hrs > 0) {
			buffer.append(getLocalizedComponent(Strings.HOURS, hrs));
			if (mins > 0) {
				buffer.append(getLocalizedSecondaryComponent(Strings.MINUTES, mins));
			}
		} else if (mins > 0) {
			buffer.append(getLocalizedComponent(Strings.MINUTES, mins));
			if (secs > 0) {
				buffer.append(getLocalizedSecondaryComponent(Strings.SECONDS, secs));
			}
		} else {
			if (secs > 0) {
				buffer.append(getLocalizedComponent(Strings.SECONDS, secs));
			}
		}

		return buffer.toString();
	}

	public static String parseDate(long millis) {
		return getTimeSince(new Date(millis));
	}

	public static String localize(String unlocString, Object... args) {
		return I18n.translateToLocalFormatted(unlocString, args);
	}

	public static String getLocalizedSingularForm(String unlocString) {
		String locString = localize(unlocString);
		return locString.substring(0, locString.length() - 1);
	}

	public static String getLocalizedComponent(String unlocString, long component) {
		String s = component + " ";
		if (component == 1) {
			s = s.concat(getLocalizedSingularForm(unlocString));
		} else {
			s = s.concat(localize(unlocString));
		}

		return s;
	}

	public static String getLocalizedSecondaryComponent(String unlocString, long component) {
		return " " + localize(Strings.AND) + " " + getLocalizedComponent(unlocString, component);
	}

	public static String[] splitWords(String s) {
		return s.split(" ");
	}

	public static List<String> parseStringIntoLength(String s, int length) {
		List<String> parsedStrings = new ArrayList<String>();
		String line = "";
		boolean addedPrev = false;
		for (String word : splitWords(s)) {
			line = line.concat(" " + word).trim();

			if (line.length() > length) {
				parsedStrings.add(line);
				line = "";
				addedPrev = true;
			} else {
				addedPrev = false;
			}
		}
		if (!addedPrev) {
			parsedStrings.add(line);
		}

		return parsedStrings;
	}

	public static boolean holdShiftForInfo(List<String> info) {
		if (!GuiScreen.isShiftKeyDown()) {
			info.add(TextFormatting.DARK_GRAY.toString() + TextFormatting.ITALIC.toString() + "Hold Shift for more info");
			return false;
		}

		return true;
	}

}
