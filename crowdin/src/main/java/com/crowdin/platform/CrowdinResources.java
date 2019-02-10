package com.crowdin.platform;

import android.content.res.Resources;
import android.icu.text.PluralRules;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;

import com.crowdin.platform.repository.StringDataManager;
import com.crowdin.platform.utils.LocaleUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is the wrapped resources which will be provided by Crowdin.
 * For getting strings and texts, it checks the strings repository first and if there's a new string
 * that will be returned, otherwise it will fallback to the original resource strings.
 */
class CrowdinResources extends Resources {

    private final StringDataManager stringDataManager;

    CrowdinResources(@NonNull final Resources res,
                     @NonNull final StringDataManager stringDataManager) {
        super(res.getAssets(), res.getDisplayMetrics(), res.getConfiguration());
        this.stringDataManager = stringDataManager;
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        String value = getStringFromRepository(id);
        return value == null ? super.getString(id) : value;
    }

    @NonNull
    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        String value = getStringFromRepository(id);
        return value == null ? super.getString(id, formatArgs) : String.format(value, formatArgs);
    }

    @NonNull
    @Override
    public String[] getStringArray(int id) throws NotFoundException {
        String[] value = getStringArrayFromRepository(id);
        return value == null ? super.getStringArray(id) : value;
    }

    @NonNull
    @Override
    public CharSequence getText(int id) throws NotFoundException {
        String value = getStringFromRepository(id);
        return value == null ? super.getText(id) : fromHtml(value);
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        String value = getStringFromRepository(id);
        return value == null ? super.getText(id, def) : fromHtml(value);
    }

    @NonNull
    @Override
    public CharSequence getQuantityText(int id, int quantity) throws NotFoundException {
        String value = getPluralFromRepository(id, quantity);
        return value == null ? super.getQuantityText(id, quantity) : value;
    }

    @Nullable
    private String getStringFromRepository(int id) {
        try {
            String stringKey = getResourceEntryName(id);
            return stringDataManager.getString(LocaleUtils.getCurrentLanguage(), stringKey);
        } catch (NotFoundException ex) {
            return null;
        }
    }

    @Nullable
    private String[] getStringArrayFromRepository(int id) {
        String arrayId = getResourceEntryName(id);
        return stringDataManager.getStringArray(LocaleUtils.getCurrentLanguage(), arrayId);
    }

    // TODO: update plurals
    @Nullable
    private String getPluralFromRepository(int id, int quantity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String stringKey = getResourceEntryName(id);
            String repositoryString = stringDataManager.getString(LocaleUtils.getCurrentLanguage(), stringKey);
            if (repositoryString == null) return null;

            HashMap<String, String> map = (HashMap<String, String>) Arrays
                    .stream(repositoryString.split("\\|"))
                    .map(new Function<String, String[]>() {
                        @Override
                        public String[] apply(String s) {
                            return s.split("\\^");
                        }
                    })
                    .collect(Collectors.toMap(
                            new Function<String[], String>() {
                                @Override
                                public String apply(String[] strings) {
                                    return strings[0].trim();
                                }
                            },
                            new Function<String[], String>() {
                                @Override
                                public String apply(String[] strings) {
                                    return strings[1];
                                }
                            }
                    ));

            PluralRules rule = PluralRules.forLocale(LocaleUtils.getCurrentLocale());
            String ruleName = rule.select(quantity);

            return map.get(ruleName);
        } else {
            return null;
        }
    }

    private CharSequence fromHtml(String source) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //noinspection deprecation
            return Html.fromHtml(source);
        } else {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT);
        }
    }
}
