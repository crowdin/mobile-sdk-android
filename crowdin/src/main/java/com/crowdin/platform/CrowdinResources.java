package com.crowdin.platform;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;

import com.crowdin.platform.repository.StringDataManager;
import com.crowdin.platform.utils.LocaleUtils;

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
        if (value != null) {
            return value;
        }
        return super.getString(id);
    }

    @NonNull
    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        String value = getStringFromRepository(id);
        if (value != null) {
            return String.format(value, formatArgs);
        }
        return super.getString(id, formatArgs);
    }

    @NonNull
    @Override
    public String[] getStringArray(int id) throws NotFoundException {
        String[] value = getStringArrayFromRepository(id);
        if (value != null) {
            return value;
        }

        return super.getStringArray(id);
    }

    @NonNull
    @Override
    public CharSequence getText(int id) throws NotFoundException {
        String value = getStringFromRepository(id);
        if (value != null) {
            return fromHtml(value);
        }
        return super.getText(id);
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        String value = getStringFromRepository(id);
        if (value != null) {
            return fromHtml(value);
        }
        return super.getText(id, def);
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
        try {
            String stringKey = getResourceEntryName(id);
            String repositoryString = stringDataManager.getString(LocaleUtils.getCurrentLanguage(), stringKey);
            return repositoryString.split("\\|");

        } catch (Exception ex) {
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
